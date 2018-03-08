package me.grudzien.patryk.service;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.EmailVerificationToken;
import me.grudzien.patryk.domain.entities.Role;
import me.grudzien.patryk.events.OnRegistrationCompleteEvent;
import me.grudzien.patryk.exceptions.exception.CustomUserValidationException;
import me.grudzien.patryk.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.repository.CustomUserRepository;

@Log4j
@Service
@Transactional
public class CustomUserServiceImpl implements CustomUserService {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;

	@Autowired
	public CustomUserServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
	                             final ApplicationEventPublisher eventPublisher,
	                             final EmailVerificationTokenRepository emailVerificationTokenRepository) {
		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
	}

	@Override
	public Boolean doesEmailExist(final String email) {
		return customUserRepository.findByEmail(email) != null;
	}

	@Override
	public CustomUser registerNewCustomUserAccount(final UserRegistrationDto userRegistrationDto, final BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			log.info("No validation errors during user registration.");
			final CustomUser customUser = CustomUser.Builder()
			                                        .firstName(userRegistrationDto.getFirstName())
			                                        .lastName(userRegistrationDto.getLastName())
			                                        .email(userRegistrationDto.getEmail())
			                                        .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
			                                        .roles(Collections.singleton(new Role("ROLE_USER")))
			                                        .build();
			final CustomUser newlyRegisterCustomUser = customUserRepository.save(customUser);

			// we use Spring Event to create the token and send verification email (it should not be performed by controller directly)
			log.info("Publisher published event for verification token generation.");
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newlyRegisterCustomUser));

			return newlyRegisterCustomUser;
		} else {
			log.error("Validation errors present during user registration.");
			throw new CustomUserValidationException("Cannot save user. Validation errors.",
			                                        bindingResult.getAllErrors()
			                                                     .stream()
			                                                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
			                                                     // I'm checking two fields for email and two for password but there in no need to duplicate the same message
			                                                     .distinct()
			                                                     .collect(Collectors.toList()));
		}
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
	public EmailVerificationToken getEmailVerificationToken(final String emailVerificationToken) {
		return emailVerificationTokenRepository.findByToken(emailVerificationToken);
	}
}
