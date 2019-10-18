package me.grudzien.patryk.registration.integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.utils.appplication.ApplicationZone;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static me.grudzien.patryk.TestsUtils.prepareEmailVerificationToken;

@SpringBootTest
@DirtiesContext
class UserRegistrationServiceImplIT extends AbstractUserRegistrationServiceHelper {

    @MockBean
    private CacheManagerHelper cacheManagerHelper;
    @MockBean
    private CustomUserRepository customUserRepository;
    @MockBean
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private LocaleMessagesHelper localeMessagesHelper;
    @Autowired
    private UserRegistrationService userRegistrationService;

    @DisplayName("Confirm registration - failed! User is already enabled!")
    @Test
    void confirmRegistration_userIsAlreadyEnabled() {
        // given
        final boolean isUserEnabled = true;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().plusHours(24L));
        given(emailVerificationTokenRepository.findByToken(anyString())).willReturn(emailVerificationToken);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(randomAlphanumeric(25));

        // then
        assertAll(
                () -> assertFalse(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isNullOrEmpty(),
                () -> assertThat(registrationResponse.getRegisteredUser()).isNull(),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo("http://localhost:8080/ui/registration-confirmed?info=userAlreadyEnabled")
        );
        verify(emailVerificationTokenRepository).findByToken(anyString());
    }

    private static Stream<Arguments> verificationTokenExpiredTestData() {
        return Stream.of(
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("pl")
                                .exceptionMessage("Token weryfikacyjny stracił ważność! Wygenerować nowy?")
                                .build()
                ),
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("en")
                                .exceptionMessage("Verification token expired! Would you like to resend it again?")
                                .build()
                )
        );
    }

    @DisplayName("Confirm registration - failed! Email verification token expired!")
    @MethodSource("verificationTokenExpiredTestData")
    @ParameterizedTest
    void confirmRegistration_verificationTokenExpired(final ConfirmRegistrationTestDataBuilder builder) {
        // given
        localeMessagesHelper.setLocale(new Locale(builder.getLanguage()));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().minusMinutes(10L));
        given(emailVerificationTokenRepository.findByToken(anyString())).willReturn(emailVerificationToken);

        // when
        final EmailVerificationTokenExpiredException emailVerificationTokenExpiredException = assertThrows(
                EmailVerificationTokenExpiredException.class, () -> userRegistrationService.confirmRegistration(randomAlphanumeric(25)));

        // then
        assertAll(
                () -> assertThat(emailVerificationTokenExpiredException.getMessage()).isNotNull(),
                () -> assertThat(emailVerificationTokenExpiredException.getMessage()).isEqualTo(builder.getExceptionMessage())
        );
        verify(emailVerificationTokenRepository).findByToken(anyString());
    }

    private static Stream<Arguments> confirmRegistrationUserSuccessfullyEnabledTestData() {
        return Stream.of(
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("pl")
                                .registrationResponseMessage("Twoje konto zostało potwierdzone oraz aktywowane!")
                                .build()
                ),
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("en")
                                .registrationResponseMessage("Your account has been confirmed and activated!")
                                .build()
                )
        );
    }

    @DisplayName("Confirm registration - success. User successfully enabled.")
    @MethodSource("confirmRegistrationUserSuccessfullyEnabledTestData")
    @ParameterizedTest
    void confirmRegistration_userSuccessfullyEnabled(final ConfirmRegistrationTestDataBuilder builder) {
        // given
        localeMessagesHelper.setLocale(new Locale(builder.getLanguage()));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().plusHours(24L));
        given(emailVerificationTokenRepository.findByToken(anyString())).willReturn(emailVerificationToken);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(randomAlphanumeric(25));

        // then
        assertAll(
                () -> assertTrue(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isEqualTo(builder.getRegistrationResponseMessage()),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo("http://localhost:8080/ui/registration-confirmed")
        );
        verify(emailVerificationTokenRepository).findByToken(anyString());
        verify(cacheManagerHelper).clearAllCache(anyString());
        verify(customUserRepository).save(any());
        verify(emailVerificationTokenRepository).delete(any());
    }

    private static Stream<Arguments> confirmRegistrationEnabledUserCannotBeSavedTestData() {
        return Stream.of(
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("pl")
                                .registrationResponseMessage("System napotkał problem podczas potwierdzania konta i ciągle jest nieaktywne!")
                                .build()
                ),
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("en")
                                .registrationResponseMessage("System encountered an error on account confirmation and it is still disabled!")
                                .build()
                )
        );
    }

    @DisplayName("Confirm registration - failed! User was enabled but not updated in the database!")
    @MethodSource("confirmRegistrationEnabledUserCannotBeSavedTestData")
    @ParameterizedTest
    void confirmRegistration_enabledUserCannotBeSaved(final ConfirmRegistrationTestDataBuilder builder) {
        // given
        localeMessagesHelper.setLocale(new Locale(builder.getLanguage()));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().plusHours(24L));
        given(emailVerificationTokenRepository.findByToken(anyString())).willReturn(emailVerificationToken);
        given(customUserRepository.save(any())).willThrow(RuntimeException.class);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(randomAlphanumeric(25));

        // then
        assertAll(
                () -> assertFalse(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isEqualTo(builder.getRegistrationResponseMessage()),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo(
                        "http://localhost:8080/ui/registration-confirmed?error=systemEncounteredAnErrorOnEnablingAccount")
        );
        verify(emailVerificationTokenRepository).findByToken(anyString());
        verify(cacheManagerHelper).clearAllCache(anyString());
        verify(customUserRepository).save(any());
        verify(emailVerificationTokenRepository, times(0)).delete(any());
    }

    private static Stream<Arguments> confirmRegistrationTokenNotFoundTestData() {
        return Stream.of(
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("pl")
                                .exceptionMessage("Token weryfikacyjny dla Twojego konta nie został znaleziony w bazie danych! Mógł wystąpić błąd podczas procesu rejestracji! Proszę powtórz go.")
                                .build()
                ),
                arguments(
                        ConfirmRegistrationTestDataBuilder.builder()
                                .language("en")
                                .exceptionMessage("Verification token for your account not found in the database! Some error occurred during registration process! Please register again.")
                                .build()
                )
        );
    }

    @DisplayName("Confirm registration - failed! Token not found in request param!")
    @MethodSource("confirmRegistrationTokenNotFoundTestData")
    @ParameterizedTest
    void confirmRegistration_tokenNotFoundInRequestParam(final ConfirmRegistrationTestDataBuilder builder) {
        // given
        localeMessagesHelper.setLocale(new Locale(builder.getLanguage()));
        given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(null);

        // when
        final EmailVerificationTokenNotFoundException emailVerificationTokenNotFoundException = assertThrows(
                EmailVerificationTokenNotFoundException.class, () -> userRegistrationService.confirmRegistration(randomAlphanumeric(25)));

        // then
        assertAll(
                () -> assertThat(emailVerificationTokenNotFoundException.getMessage()).isNotNull(),
                () -> assertThat(emailVerificationTokenNotFoundException.getMessage()).isEqualTo(builder.getExceptionMessage())
        );
        verify(emailVerificationTokenRepository).findByToken(anyString());
    }
}