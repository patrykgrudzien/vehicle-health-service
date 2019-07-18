package me.grudzien.patryk.unit.registration.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;
import me.grudzien.patryk.registration.service.impl.UserRegistrationServiceImpl;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.HttpLocationHeaderCreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static me.grudzien.patryk.TestsUtils.prepareEmailVerificationToken;
import static me.grudzien.patryk.utils.app.AppFLow.ACCOUNT_ALREADY_ENABLED;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private HttpLocationHeaderCreator httpLocationHeaderCreator;

    @Mock
    private LocaleMessagesCreator localeMessagesCreator;

    @Mock
    private CacheManagerHelper cacheManagerHelper;

    @Mock
    private UserRegistrationDtoMapper userRegistrationDtoMapper;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    void confirmRegistration() {
        // given
        final boolean isUserEnabled = true;
        given(emailVerificationTokenRepository.findByToken(anyString()))
                .willReturn(prepareEmailVerificationToken(isUserEnabled));
        given(httpLocationHeaderCreator.createRedirectionUrl(ACCOUNT_ALREADY_ENABLED))
                .willReturn("http://localhost:8080/ui/registration-confirmed?info=userAlreadyEnabled");

        // when
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(RandomStringUtils.randomAlphanumeric(25));

        // then
        Assertions.assertAll(
                () -> assertFalse(registrationResponse.isSuccessful()),
                () -> assertThat(registrationResponse.getMessage()).isNullOrEmpty(),
                () -> assertThat(registrationResponse.getRegisteredUser()).isNull(),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isNotBlank(),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isNotEmpty(),
                () -> assertThat(registrationResponse.getRedirectionUrl()).isEqualTo("http://localhost:8080/ui/registration-confirmed?info=userAlreadyEnabled")
        );
        verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
        verify(httpLocationHeaderCreator, times(1)).createRedirectionUrl(any());
    }
}