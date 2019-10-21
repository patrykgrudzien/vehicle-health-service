package me.grudzien.patryk.registration.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.grudzien.patryk.utils.appplication.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.utils.appplication.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.utils.appplication.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.utils.appplication.ApplicationZone.POLAND;
import static me.grudzien.patryk.vehicle.model.entity.Engine.diesel;
import static me.grudzien.patryk.vehicle.model.enums.VehicleType.CAR;

import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.registration.exception.UserAlreadyExistsException;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.registration.model.factory.CustomUserFactory;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.HttpLocationHeaderCreator;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;

@Slf4j
@Service
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	private final HttpLocationHeaderCreator httpLocationHeaderCreator;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final CacheManagerHelper cacheManagerHelper;
	
	@Autowired
	public UserRegistrationServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
                                       final EmailVerificationTokenRepository emailVerificationTokenRepository,
                                       final HttpLocationHeaderCreator httpLocationHeaderCreator, final LocaleMessagesCreator localeMessagesCreator,
                                       final CacheManagerHelper cacheManagerHelper) {

        checkNotNull(customUserRepository, "customUserRepository cannot be null!");
        checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
        checkNotNull(emailVerificationTokenRepository, "emailVerificationTokenRepository cannot be null!");
        checkNotNull(httpLocationHeaderCreator, "httpLocationHeaderCreator cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");

        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.httpLocationHeaderCreator = httpLocationHeaderCreator;
        this.localeMessagesCreator = localeMessagesCreator;
        this.cacheManagerHelper = cacheManagerHelper;
    }

	@Override
	public RegistrationResponse createUserAccount(final UserRegistrationDto registrationDto) {
        log.info("No validation errors during user registration.");
        final String email = registrationDto.getEmail();
		if (customUserRepository.existsByEmail(email)) {
			log.error("User with specified email {} already exists.", email);
            throw new UserAlreadyExistsException(localeMessagesCreator.buildLocaleMessageWithParam("user-already-exists", email));
		}
        final CustomUser customUser = CustomUserFactory.createFrom(registrationDto, passwordEncoder);
        final Vehicle vehicle = Vehicle.Builder()
                                       .vehicleType(CAR)
                                       .engine(diesel())
                                       .mileage(0L)
                                       .customUser(customUser)
                                       .build();
        customUser.setVehicles(singletonList(vehicle));
        customUserRepository.save(customUser);
        return RegistrationResponse.Builder()
                                   .isSuccessful(true)
                                   .registeredUser(customUser)
                                   .message(localeMessagesCreator.buildLocaleMessageWithParam("register-user-account-success", email))
                                   .build();
	}

	@Override
	public RegistrationResponse confirmRegistration(final String tokenRequestParam) {
		final EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenRequestParam);
		RegistrationResponse registrationResponse = new RegistrationResponse();
		if (token != null) {
		    log.info("Email verification token successfully found.");
			final CustomUser customUser = token.getCustomUser();
			if (customUser.isEnabled()) {
				log.info("User account with e-mail address ({}) has been already enabled.", customUser.getEmail());
				registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(ACCOUNT_ALREADY_ENABLED));
			} else {
			    if (POLAND.now().isAfter(token.getExpiryDate())) {
					log.error("Verification token has expired!");
					registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(VERIFICATION_TOKEN_EXPIRED));
					throw new EmailVerificationTokenExpiredException(localeMessagesCreator.buildLocaleMessage("verification-token-expired"));
				}
				log.info("Email verification token is still valid.");
                registrationResponse = enableRegisteredCustomUser(customUser);
                if (registrationResponse.isSuccessful()) {
                    emailVerificationTokenRepository.delete(token);
                    log.info("Token confirmed and deleted from database.");
                    return registrationResponse;
                } else {
                    registrationResponse.setMessage(localeMessagesCreator.buildLocaleMessage("system-encountered-error-on-account-confirmation"));
                    registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT));
                }
			}
		} else {
			log.error("No verification token found in the database. Some error occurred during registration process!");
			registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(VERIFICATION_TOKEN_NOT_FOUND));
			throw new EmailVerificationTokenNotFoundException(localeMessagesCreator.buildLocaleMessage("verification-token-not-found"));
		}
		return registrationResponse;
	}

	@Override
	public RegistrationResponse enableRegisteredCustomUser(final CustomUser customUser) {
		final RegistrationResponse registrationResponse = new RegistrationResponse();

		Try.run(() -> {
		    log.info("Trying to enable newly created user...");
			// cleaning user from cache because it's been saved (with "enabled" status = FALSE) before email confirmation
			cacheManagerHelper.clearAllCache(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME);
			customUser.setEnabled(Boolean.TRUE);
			customUserRepository.save(customUser);
		}).onSuccess(successVoid -> {
			log.info("User account has been activated.");
			registrationResponse.setSuccessful(true);
			registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(CONFIRM_REGISTRATION));
			registrationResponse.setMessage(localeMessagesCreator.buildLocaleMessage("confirm-registration"));
		}).onFailure(throwable -> log.error("User account has NOT been activated!"));

        return registrationResponse;
	}

    @Override
    public void createEmailVerificationTokenForUser(final Long customUserId, final String uuidToken) {
        Try.run(() -> customUserRepository.findById(customUserId)
                                          .map(customUser -> new EmailVerificationToken(uuidToken, customUser))
                                          .ifPresent(emailVerificationToken -> {
                                          	log.info("Trying to persist email verification token...");
                                          	emailVerificationTokenRepository.save(emailVerificationToken);
                                          }))
           .onSuccess(successVoid -> log.info("Email verification token successfully saved into database."))
           .onFailure(throwable -> log.error("Email verification token has NOT been persisted!"));
    }
}
