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
import com.google.common.collect.Sets;

import javax.servlet.http.HttpServletResponse;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.engine.Engine;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;
import me.grudzien.patryk.domain.entities.registration.Privilege;
import me.grudzien.patryk.domain.entities.registration.Role;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.registration.PrivilegeName;
import me.grudzien.patryk.domain.enums.registration.RoleName;
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
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

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
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public UserRegistrationServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
	                                   final ApplicationEventPublisher eventPublisher,
	                                   final EmailVerificationTokenRepository emailVerificationTokenRepository,
	                                   final HttpResponseHandler httpResponseHandler, final EmailService emailService,
	                                   final LocaleMessagesCreator localeMessagesCreator, final EngineRepository engineRepository,
	                                   final VehicleRepository vehicleRepository, final RequestsDecoder requestsDecoder) {

		Preconditions.checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		Preconditions.checkNotNull(eventPublisher, "eventPublisher cannot be null!");
		Preconditions.checkNotNull(emailVerificationTokenRepository, "emailVerificationTokenRepository cannot be null!");
		Preconditions.checkNotNull(httpResponseHandler, "httpResponseHandler cannot be null!");
		Preconditions.checkNotNull(emailService, "emailService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(engineRepository, "engineRepository cannot be null!");
		Preconditions.checkNotNull(vehicleRepository, "carRepository cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
		this.httpResponseHandler = httpResponseHandler;
		this.emailService = emailService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.engineRepository = engineRepository;
		this.vehicleRepository = vehicleRepository;
		this.requestsDecoder = requestsDecoder;
	}

	@Override
	public void registerNewCustomUserAccount(final UserRegistrationDto userRegistrationDto, final BindingResult bindingResult,
	                                         final WebRequest webRequest) {

		// (firstName) & (lastName) are not encoded because (firstName) goes to confirmation email as variable in template
		final String firstName = userRegistrationDto.getFirstName();
		final String lastName = userRegistrationDto.getLastName();
		final String decodedPassword = requestsDecoder.decodeStringParam(userRegistrationDto.getPassword());
		// (email) & (confirmedEmail) fields are not encoded on UI side because they must be validated by @ValidEmail annotation
		final String email = userRegistrationDto.getEmail();

		if (doesEmailExist(email)) {
			log.error(EXCEPTION_MARKER, "User with specified email {} already exists.", email);
			throw new UserAlreadyExistsException(localeMessagesCreator.buildLocaleMessageWithParam("user-already-exists", email));
		}
		if (!bindingResult.hasErrors()) {
			log.info("No validation errors during user registration.");
			final CustomUser customUser = CustomUser.Builder()
			                                        .firstName(firstName)
			                                        .lastName(lastName)
			                                        .email(email)
			                                        .password(passwordEncoder.encode(decodedPassword))
			                                        .roles(Collections.singleton(Role.Builder()
			                                                                         .roleName(RoleName.ROLE_ADMIN)
			                                                                         .privileges(Sets.newHashSet(Privilege.Builder()
			                                                                                                              .privilegeName(PrivilegeName.CAN_DO_EVERYTHING)
			                                                                                                              .build()))
			                                                                         .build()))
			                                        .createdDate(new Date())
			                                        .build();

			// TODO: temporary (test purposes)
			final Engine engine = Engine.Builder().engineType(EngineType.DIESEL).build();
			final Vehicle vehicle = Vehicle.Builder().vehicleType(VehicleType.CAR).engine(engine).build();
			vehicle.setMileage(0L);
			vehicle.setCustomUser(customUser);
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
		if (token != null) {
			final CustomUser customUser = token.getCustomUser();
			if (customUser.isEnabled()) {
				log.info(FLOW_MARKER, "User account with e-mail address ({}) has been already enabled.", customUser.getEmail());
				httpResponseHandler.redirectUserTo(ACCOUNT_ALREADY_ENABLED, response);
			} else {
				final Calendar calendar = Calendar.getInstance();
				if (token.getExpiryDate().compareTo(calendar.getTime()) < 0) {
					log.error("Verification token has expired.");
					httpResponseHandler.redirectUserTo(VERIFICATION_TOKEN_EXPIRED, response);
					throw new TokenExpiredException(localeMessagesCreator.buildLocaleMessage("verification-token-expired"));
				}
				customUser.setEnabled(Boolean.TRUE);
				saveRegisteredCustomUser(customUser);
				log.info("User account has been activated.");

				emailVerificationTokenRepository.delete(token);
				log.info("Token confirmed and deleted from database.");

				httpResponseHandler.redirectUserTo(CONFIRM_REGISTRATION, response);
			}
		} else {
			log.error("No verification token found in the database. Some error occurred during registration process.");
			httpResponseHandler.redirectUserTo(VERIFICATION_TOKEN_NOT_FOUND, response);
			throw new TokenNotFoundException(localeMessagesCreator.buildLocaleMessage("verification-token-not-found"));
		}
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
