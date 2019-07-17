package me.grudzien.patryk.integration.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;

import java.util.Locale;
import java.util.stream.Stream;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.utils.app.ApplicationZone;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenExpiredException;
import me.grudzien.patryk.registration.exception.EmailVerificationTokenNotFoundException;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static me.grudzien.patryk.TestsUtils.prepareEmailVerificationToken;

@SpringBootTest
@DirtiesContext
class UserRegistrationServiceImplIT {

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
        BDDMockito.given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(emailVerificationToken);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25));

        // then
        Assertions.assertAll(
                () -> assertFalse(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isNullOrEmpty(),
                () -> assertThat(registrationResponse.getRegisteredUser()).isNull(),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo("http://localhost:8080/ui/registration-confirmed?info=userAlreadyEnabled")
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
    }

    private static Stream<Arguments> verificationTokenExpiredTestData() {
        return Stream.of(
                Arguments.arguments("pl", "Token weryfikacyjny stracił ważność! Wygenerować nowy?"),
                Arguments.arguments("en", "Verification token expired! Would you like to resend it again?")
        );
    }

    @DisplayName("Confirm registration - failed! Email verification token expired!")
    @MethodSource("verificationTokenExpiredTestData")
    @ParameterizedTest(name = "Locale: ({0})")
    void confirmRegistration_verificationTokenExpired(final String language, final String exceptionMessage) {
        // given
        localeMessagesHelper.setLocale(new Locale(language));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().minusMinutes(10L));
        BDDMockito.given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(emailVerificationToken);

        // when
        final EmailVerificationTokenExpiredException emailVerificationTokenExpiredException = assertThrows(
                EmailVerificationTokenExpiredException.class, () -> userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25)));

        // then
        Assertions.assertAll(
                () -> assertThat(emailVerificationTokenExpiredException.getMessage()).isNotNull(),
                () -> assertThat(emailVerificationTokenExpiredException.getMessage()).isEqualTo(exceptionMessage)
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
    }

    private static Stream<Arguments> confirmRegistrationUserSuccessfullyEnabledTestData() {
        return Stream.of(
                Arguments.arguments("pl", "Twoje konto zostało potwierdzone oraz aktywowane!"),
                Arguments.arguments("en", "Your account has been confirmed and activated!")
        );
    }

    @DisplayName("Confirm registration - success. User successfully enabled.")
    @MethodSource("confirmRegistrationUserSuccessfullyEnabledTestData")
    @ParameterizedTest(name = "Locale: ({0})")
    void confirmRegistration_userSuccessfullyEnabled(final String language, final String registrationResponseMessage) {
        // given
        localeMessagesHelper.setLocale(new Locale(language));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().plusHours(24L));
        BDDMockito.given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(emailVerificationToken);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25));

        // then
        Assertions.assertAll(
                () -> assertTrue(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isEqualTo(registrationResponseMessage),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo("http://localhost:8080/ui/registration-confirmed")
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
        verify(cacheManagerHelper, times(1)).clearAllCache(anyString());
        verify(customUserRepository, times(1)).save(any());
        verify(emailVerificationTokenRepository, times(1)).delete(any());
    }

    private static Stream<Arguments> confirmRegistrationEnabledUserCannotBeSavedTestData() {
        return Stream.of(
                Arguments.arguments("pl", "System napotkał problem podczas potwierdzania konta i ciągle jest nieaktywne!"),
                Arguments.arguments("en", "System encountered an error on account confirmation and it is still disabled!")
        );
    }

    @DisplayName("Confirm registration - failed! User was enabled but not updated in the database!")
    @MethodSource("confirmRegistrationEnabledUserCannotBeSavedTestData")
    @ParameterizedTest(name = "Locale: ({0})")
    void confirmRegistration_enabledUserCannotBeSaved(final String language, final String registrationResponseMessage) {
        // given
        localeMessagesHelper.setLocale(new Locale(language));
        final boolean isUserEnabled = false;
        final EmailVerificationToken emailVerificationToken = prepareEmailVerificationToken(isUserEnabled, ApplicationZone.POLAND.now().plusHours(24L));
        BDDMockito.given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(emailVerificationToken);
        BDDMockito.given(customUserRepository.save(any()))
                  .willThrow(RuntimeException.class);

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25));

        // then
        Assertions.assertAll(
                () -> assertFalse(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isEqualTo(registrationResponseMessage),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo(
                        "http://localhost:8080/ui/registration-confirmed?error=systemEncounteredAnErrorOnEnablingAccount")
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
        verify(cacheManagerHelper, times(1)).clearAllCache(anyString());
        verify(customUserRepository, times(1)).save(any());
        verify(emailVerificationTokenRepository, times(0)).delete(any());
    }

    private static Stream<Arguments> confirmRegistrationTokenNotFoundTestData() {
        return Stream.of(
                Arguments.arguments("pl", "Token weryfikacyjny dla Twojego konta nie został znaleziony w bazie danych! Mógł wystąpić błąd podczas procesu rejestracji! Proszę powtórz go."),
                Arguments.arguments("en", "Verification token for your account not found in the database! Some error occurred during registration process! Please register again.")
        );
    }

    @DisplayName("Confirm registration - failed! Token not found in request param!")
    @MethodSource("confirmRegistrationTokenNotFoundTestData")
    @ParameterizedTest(name = "Locale: ({0})")
    void confirmRegistration_tokenNotFoundInRequestParam(final String language, final String exceptionMessage) {
        // given
        localeMessagesHelper.setLocale(new Locale(language));
        BDDMockito.given(emailVerificationTokenRepository.findByToken(anyString()))
                  .willReturn(null);

        // when
        final EmailVerificationTokenNotFoundException emailVerificationTokenNotFoundException = assertThrows(
                EmailVerificationTokenNotFoundException.class, () -> userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25)));

        // then
        Assertions.assertAll(
                () -> assertThat(emailVerificationTokenNotFoundException.getMessage()).isNotNull(),
                () -> assertThat(emailVerificationTokenNotFoundException.getMessage()).isEqualTo(exceptionMessage)
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
    }
}