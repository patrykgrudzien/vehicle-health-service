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

import javax.servlet.http.HttpServletResponse;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;
import me.grudzien.patryk.domain.entities.registration.Role;
import me.grudzien.patryk.domain.enums.RoleName;
import me.grudzien.patryk.events.registration.OnRegistrationCompleteEvent;
import me.grudzien.patryk.exceptions.registration.CustomUserValidationException;
import me.grudzien.patryk.exceptions.registration.TokenExpiredException;
import me.grudzien.patryk.exceptions.registration.TokenNotFoundException;
import me.grudzien.patryk.exceptions.registration.UserAlreadyExistsException;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.repository.registration.EmailVerificationTokenRepository;

@Log4j2
@Service
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	private final HttpResponseHandler httpResponseHandler;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public UserRegistrationServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
	                                   final ApplicationEventPublisher eventPublisher,
	                                   final EmailVerificationTokenRepository emailVerificationTokenRepository,
	                                   final HttpResponseHandler httpResponseHandler,
	                                   final CustomApplicationProperties customApplicationProperties) {

		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
		this.httpResponseHandler = httpResponseHandler;
		this.customApplicationProperties = customApplicationProperties;
	}

	@Override
	public void registerNewCustomUserAccount(final UserRegistrationDto userRegistrationDto, final BindingResult bindingResult,
	                                         final WebRequest webRequest) {

		if (doesEmailExist(userRegistrationDto.getEmail())) {
			log.error("User with specified email " + userRegistrationDto.getEmail() + " already exists.");
			throw new UserAlreadyExistsException("User with specified email " + userRegistrationDto.getEmail() + " already exists.");
		}
		if (!bindingResult.hasErrors()) {
			log.info("No validation errors during user registration.");
			final CustomUser customUser = CustomUser.Builder()
			                                        .username(userRegistrationDto.getFirstName() + userRegistrationDto.getLastName())
			                                        .firstName(userRegistrationDto.getFirstName())
			                                        .lastName(userRegistrationDto.getLastName())
			                                        .email(userRegistrationDto.getEmail())
			                                        .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
			                                        .roles(Collections.singleton(new Role(RoleName.ROLE_ADMIN)))
			                                        .createdDate(new Date())
			                                        .build();
			customUserRepository.save(customUser);

			// we use Spring Event to create the token and send verification email (it should not be performed by controller directly)
			log.info("Publisher published event for verification token generation.");
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(customUser, webRequest.getContextPath()));
		} else {
			log.error("Validation errors present during user registration.");
			throw new CustomUserValidationException("Cannot save user. Validation errors.",
			                                        bindingResult.getAllErrors()
			                                                     .stream()
			                                                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
			                                                     // I'm checking two fields for email and two for password but there is
			                                                     // no need to duplicate the same message
			                                                     .distinct()
			                                                     .collect(Collectors.toList()));
		}
	}

	@Override
	public void confirmRegistration(final String emailVerificationToken, final HttpServletResponse response) {
		final EmailVerificationToken token = getEmailVerificationToken(emailVerificationToken);
		if (token == null) {
			// TODO: check additional case if user is already enabled
			log.error("No verification token found.");
			httpResponseHandler.redirectUserToEmailTokenNotFoundUrl(response);
			throw new TokenNotFoundException("No verification token found.");
		}
		final Calendar calendar = Calendar.getInstance();
		if (token.getExpiryDate().compareTo(calendar.getTime()) < 0) {
			log.error("Verification token has expired.");
			throw new TokenExpiredException("Verification token has expired.");
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
	public CustomUser getCustomUser(final String emailVerificationToken) {
		final EmailVerificationToken token = emailVerificationTokenRepository.findByToken(emailVerificationToken);
		return token != null ? token.getCustomUser() : null;
	}

	@Override
	public void saveRegisteredCustomUser(final CustomUser customUser) {
		customUserRepository.save(customUser);
	}

	@Override
	public void createEmailVerificationToken(final CustomUser customUser, final String token) {
		emailVerificationTokenRepository.save(new EmailVerificationToken(token, customUser));
	}

	@Override
	public EmailVerificationToken generateNewEmailVerificationToken(final String existingEmailVerificationToken) {
		final EmailVerificationToken existingToken = emailVerificationTokenRepository.findByToken(existingEmailVerificationToken);
		log.info("Found expired token for user: " + existingToken.getCustomUser().getEmail());
		existingToken.updateToken(UUID.randomUUID().toString());
		log.info("New token: " + existingToken.getToken() + " generated successfully.");
		return emailVerificationTokenRepository.save(existingToken);
	}

	@Override
	public void resendEmailVerificationToken(final String existingEmailVerificationToken) {
		// TODO: finish implementation
		final EmailVerificationToken newToken = generateNewEmailVerificationToken(existingEmailVerificationToken);
		final CustomUser existingUser = getCustomUser(newToken.getToken());
	}

	@Override
	public EmailVerificationToken getEmailVerificationToken(final String emailVerificationToken) {
		return emailVerificationTokenRepository.findByToken(emailVerificationToken);
	}
}
