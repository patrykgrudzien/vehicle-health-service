package me.grudzien.patryk.registration.service.impl;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.utils.app.ApplicationZone;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.exception.CustomUserValidationException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.registration.exception.UserAlreadyExistsException;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.registration.model.entity.Privilege;
import me.grudzien.patryk.registration.model.entity.Role;
import me.grudzien.patryk.registration.model.enums.PrivilegeName;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;
import me.grudzien.patryk.registration.model.enums.RoleName;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.utils.mapping.ObjectDecoder;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.validation.CustomValidator;
import me.grudzien.patryk.utils.web.HttpLocationHeaderCreator;
import me.grudzien.patryk.vehicle.model.entity.Engine;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;
import me.grudzien.patryk.vehicle.model.enums.EngineType;
import me.grudzien.patryk.vehicle.model.enums.VehicleType;

import static com.google.common.base.Preconditions.checkNotNull;

import static me.grudzien.patryk.utils.app.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.utils.app.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.utils.app.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.utils.app.AppFLow.VERIFICATION_TOKEN_EXPIRED;
import static me.grudzien.patryk.utils.app.AppFLow.VERIFICATION_TOKEN_NOT_FOUND;

@Log4j2
@Service
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	private final HttpLocationHeaderCreator httpLocationHeaderCreator;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final CacheManagerHelper cacheManagerHelper;
	private final UserRegistrationDtoMapper userRegistrationDtoMapper;

	@Autowired
	public UserRegistrationServiceImpl(final CustomUserRepository customUserRepository, final BCryptPasswordEncoder passwordEncoder,
                                       final EmailVerificationTokenRepository emailVerificationTokenRepository,
                                       final HttpLocationHeaderCreator httpLocationHeaderCreator, final LocaleMessagesCreator localeMessagesCreator,
                                       final CacheManagerHelper cacheManagerHelper, final UserRegistrationDtoMapper userRegistrationDtoMapper) {

        checkNotNull(customUserRepository, "customUserRepository cannot be null!");
        checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
        checkNotNull(emailVerificationTokenRepository, "emailVerificationTokenRepository cannot be null!");
        checkNotNull(httpLocationHeaderCreator, "httpLocationHeaderCreator cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");
        checkNotNull(userRegistrationDtoMapper, "userRegistrationDtoMapper cannot be null!");

        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.httpLocationHeaderCreator = httpLocationHeaderCreator;
        this.localeMessagesCreator = localeMessagesCreator;
        this.cacheManagerHelper = cacheManagerHelper;
        this.userRegistrationDtoMapper = userRegistrationDtoMapper;
    }

	@SuppressWarnings("Duplicates")
	@Override
	public RegistrationResponse registerNewCustomUserAccount(final UserRegistrationDto userRegistrationDto) {
		final UserRegistrationDto decodedUserRegistrationDto = ObjectDecoder.decodeUserRegistrationDto().apply(userRegistrationDto, userRegistrationDtoMapper);
		final String email = decodedUserRegistrationDto.getEmail();
		final RegistrationProvider registrationProvider = userRegistrationDto.getRegistrationProvider();
		final RegistrationResponse registrationResponse = RegistrationResponse.Builder(false).build();
		if (doesEmailExist(email)) {
			log.error("User with specified email {} already exists.", email);
            final String userAlreadyExistsMessage = localeMessagesCreator.buildLocaleMessageWithParam("user-already-exists", email);
            registrationResponse.setMessage(userAlreadyExistsMessage);
            throw new UserAlreadyExistsException(userAlreadyExistsMessage);
		}
        final List<String> translatedValidationResult = CustomValidator.getTranslatedValidationResult(decodedUserRegistrationDto, localeMessagesCreator);
		if (translatedValidationResult.isEmpty()) {
			log.info("No validation errors during user registration.");
            final CustomUser customUser = CustomUser.Builder()
                                                    .firstName(decodedUserRegistrationDto.getFirstName())
                                                    .lastName(decodedUserRegistrationDto.getLastName())
                                                    .email(email)
                                                    .hasFakeEmail(userRegistrationDto.isHasFakeEmail())
                                                    .password(passwordEncoder.encode(decodedUserRegistrationDto.getPassword()))
                                                    .profilePictureUrl(userRegistrationDto.getProfilePictureUrl())
                                                    .registrationProvider(registrationProvider == null ? RegistrationProvider.CUSTOM : registrationProvider)
                                                    .roles(Collections.singleton(Role.Builder()
			                                                                         .roleName(RoleName.ROLE_ADMIN)
			                                                                         .privileges(Sets.newHashSet(Privilege.Builder()
			                                                                                                              .privilegeName(PrivilegeName.CAN_DO_EVERYTHING)
			                                                                                                              .build()))
			                                                                         .build()))
                                                    .createdDate(ApplicationZone.POLAND.now())
                                                    .build();

            final Engine engine = Engine.Builder().engineType(EngineType.DIESEL).build();
			final Vehicle vehicle = Vehicle.Builder().vehicleType(VehicleType.CAR).engine(engine).build();
			vehicle.setMileage(0L);
			vehicle.setCustomUser(customUser);
			customUser.setVehicles(Lists.newArrayList(vehicle));

			customUserRepository.save(customUser);
			registrationResponse.setSuccessful(true);
			registrationResponse.setRegisteredUser(customUser);
			registrationResponse.setMessage(localeMessagesCreator.buildLocaleMessageWithParam("register-user-account-success", email));
		} else {
			log.error("Validation errors present during user registration.");
            final String validationErrorsMessage = localeMessagesCreator.buildLocaleMessage("registration-form-validation-errors");
            registrationResponse.setMessage(validationErrorsMessage);
            throw new CustomUserValidationException(validationErrorsMessage, translatedValidationResult);
		}
		return registrationResponse;
	}

	@Override
	public RegistrationResponse confirmRegistration(final String tokenRequestParam) {
		final EmailVerificationToken token = getEmailVerificationToken(tokenRequestParam);
		RegistrationResponse registrationResponse = RegistrationResponse.Builder(false).build();
		if (token != null) {
		    log.info("Email verification token successfully found.");
			final CustomUser customUser = token.getCustomUser();
			if (customUser.isEnabled()) {
				log.info("User account with e-mail address ({}) has been already enabled.", customUser.getEmail());
				registrationResponse.setRedirectionUrl(httpLocationHeaderCreator.createRedirectionUrl(ACCOUNT_ALREADY_ENABLED));
			} else {
			    if (ApplicationZone.POLAND.now().isAfter(token.getExpiryDate())) {
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
	public Boolean doesEmailExist(final String email) {
		return customUserRepository.findByEmail(email) != null;
	}

	@Override
	public RegistrationResponse enableRegisteredCustomUser(final CustomUser customUser) {
		final RegistrationResponse registrationResponse = RegistrationResponse.Builder(false).build();

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
    public void createEmailVerificationTokenForUser(final CustomUser customUser, final String uuidToken) {
        Try.run(() -> {
            final EmailVerificationToken emailVerificationToken = new EmailVerificationToken(uuidToken, customUser);
            log.info("Trying to persist email verification token...");
            emailVerificationTokenRepository.save(emailVerificationToken);
        })
           .onSuccess(successVoid -> log.info("Email verification token successfully saved into database."))
           .onFailure(throwable -> log.error("Email verification token has NOT been persisted!"));
    }

    @Override
    public EmailVerificationToken getEmailVerificationToken(final String tokenRequestParam) {
        return emailVerificationTokenRepository.findByToken(tokenRequestParam);
    }

    @Override
    public CustomUser getCustomUserFromEmailVerificationToken(final String tokenRequestParam) {
        final EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenRequestParam);
        return token != null ? token.getCustomUser() : null;
    }

    @Override
    public EmailVerificationToken generateNewEmailVerificationToken(final String existingTokenRequestParam) {
        final EmailVerificationToken existingToken = emailVerificationTokenRepository.findByToken(existingTokenRequestParam);
        log.info("Found expired token for user: {}", existingToken.getCustomUser().getEmail());
        existingToken.updateToken(UUID.randomUUID().toString());
        log.info("New token: {} generated successfully.", existingToken.getToken());
        return emailVerificationTokenRepository.save(existingToken);
    }
}
