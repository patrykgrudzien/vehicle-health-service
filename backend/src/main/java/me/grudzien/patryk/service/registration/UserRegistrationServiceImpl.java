package me.grudzien.patryk.service.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletResponse;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.engine.Engine;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;
import me.grudzien.patryk.domain.entities.registration.Role;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.domain.enums.RoleName;
import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;
import me.grudzien.patryk.events.registration.OnRegistrationCompleteEvent;
import me.grudzien.patryk.exceptions.registration.CustomUserValidationException;
import me.grudzien.patryk.exceptions.registration.TokenExpiredException;
import me.grudzien.patryk.exceptions.registration.TokenNotFoundException;
import me.grudzien.patryk.exceptions.registration.UserAlreadyExistsException;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.repository.engine.EngineRepository;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.repository.registration.EmailVerificationTokenRepository;
import me.grudzien.patryk.repository.vehicle.VehicleRepository;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Log4j2
@Service
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	private final HttpResponseHandler httpResponseHandler;
	private final EmailService emailService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final EngineRepository engineRepository;
	private final VehicleRepository vehicleRepository;

	@Autowired
	public UserRegistrationServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
	                                   final ApplicationEventPublisher eventPublisher,
	                                   final EmailVerificationTokenRepository emailVerificationTokenRepository,
	                                   final HttpResponseHandler httpResponseHandler, final EmailService emailService,
	                                   final LocaleMessagesCreator localeMessagesCreator, final EngineRepository engineRepository,
	                                   final VehicleRepository vehicleRepository) {

		Preconditions.checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		Preconditions.checkNotNull(eventPublisher, "eventPublisher cannot be null!");
		Preconditions.checkNotNull(emailVerificationTokenRepository, "emailVerificationTokenRepository cannot be null!");
		Preconditions.checkNotNull(httpResponseHandler, "httpResponseHandler cannot be null!");
		Preconditions.checkNotNull(emailService, "emailService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(engineRepository, "engineRepository cannot be null!");
		Preconditions.checkNotNull(vehicleRepository, "carRepository cannot be null!");

		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
		this.httpResponseHandler = httpResponseHandler;
		this.emailService = emailService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.engineRepository = engineRepository;
		this.vehicleRepository = vehicleRepository;
	}

	@Override
	public void registerNewCustomUserAccount(final UserRegistrationDto userRegistrationDto, final BindingResult bindingResult,
	                                         final WebRequest webRequest) {

		if (doesEmailExist(userRegistrationDto.getEmail())) {
			log.error(EXCEPTION_MARKER, "User with specified email {} already exists.", userRegistrationDto.getEmail());
			throw new UserAlreadyExistsException(localeMessagesCreator.buildLocaleMessageWithParam("user-already-exists",
			                                                                                       userRegistrationDto.getEmail()));
		}
		if (!bindingResult.hasErrors()) {
			log.info("No validation errors during user registration.");
			final CustomUser customUser = CustomUser.Builder()
			                                        .firstName(userRegistrationDto.getFirstName())
			                                        .lastName(userRegistrationDto.getLastName())
			                                        .email(userRegistrationDto.getEmail())
			                                        .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
			                                        .roles(Collections.singleton(new Role(RoleName.ROLE_ADMIN)))
			                                        .createdDate(new Date())
			                                        .build();

			// TODO: temporary (test purposes)
			final Engine engine = Engine.Builder().engineType(EngineType.DIESEL).build();
			final Vehicle vehicle = Vehicle.Builder().vehicleType(VehicleType.CAR).engine(engine).build();
			vehicle.setCustomUser(customUser);
			engineRepository.save(engine);
			vehicleRepository.save(vehicle);
			customUser.setVehicles(Lists.newArrayList(vehicle));
			// TODO: temporary (test purposes)

			customUserRepository.save(customUser);

			// we use Spring Event to create the token and send verification email (it should not be performed by controller directly)
			log.info("Publisher published event for verification token generation.");
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(customUser, webRequest.getContextPath()));
		} else {
			log.error("Validation errors present during user registration.");
			throw new CustomUserValidationException(localeMessagesCreator.buildLocaleMessage("form-validation-errors"),
			                                        bindingResult.getAllErrors()
			                                                     .stream()
			                                                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
			                                                     // I'm checking two fields for email and two for password but there is
			                                                     // no need to duplicate the same message
			                                                     .distinct()
			                                                     // translate "messageCode" to i18n message
			                                                     .map(localeMessagesCreator::buildLocaleMessage)
			                                                     .collect(Collectors.toList()));
		}
	}

	@Override
	public void confirmRegistration(final String emailVerificationToken, final HttpServletResponse response) {
		final EmailVerificationToken token = emailService.getEmailVerificationToken(emailVerificationToken);
		if (token == null) {
			// TODO: check additional case if user is already enabled when token is still VALID
			log.error("No verification token found.");
			httpResponseHandler.redirectUserToEmailTokenNotFoundUrl(response);
			throw new TokenNotFoundException(localeMessagesCreator.buildLocaleMessage("verification-token-not-found"));
		}
		final Calendar calendar = Calendar.getInstance();
		if (token.getExpiryDate().compareTo(calendar.getTime()) < 0) {
			log.error("Verification token has expired.");
			httpResponseHandler.redirectUserToEmailTokenExpiredUrl(response);
			throw new TokenExpiredException(localeMessagesCreator.buildLocaleMessage("verification-token-expired"));
		}
		final CustomUser user = token.getCustomUser();
		user.setEnabled(Boolean.TRUE);
		saveRegisteredCustomUser(user);
		log.info("User account has been activated.");

		emailVerificationTokenRepository.delete(token);
		log.info("Token confirmed and deleted from database.");
	}

	@Override
	public Boolean doesEmailExist(final String email) {
		return customUserRepository.findByEmail(email) != null;
	}

	@Override
	public CustomUser getCustomUserFromEmailVerificationToken(final String emailVerificationToken) {
		final EmailVerificationToken token = emailVerificationTokenRepository.findByToken(emailVerificationToken);
		return token != null ? token.getCustomUser() : null;
	}

	@Override
	public void saveRegisteredCustomUser(final CustomUser customUser) {
		customUserRepository.save(customUser);
	}

	@Override
	public void resendEmailVerificationToken(final String existingEmailVerificationToken) {
		// TODO: finish implementation
		final EmailVerificationToken newToken = emailService.generateNewEmailVerificationToken(existingEmailVerificationToken);
		final CustomUser existingUser = getCustomUserFromEmailVerificationToken(newToken.getToken());
	}
}
