package me.grudzien.patryk.unit.oauth2.authentication.chain;

import org.springframework.security.core.Authentication;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.FirstStep;
import me.grudzien.patryk.oauth2.authentication.chain.SecondStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AbstractAuthenticationStepTest {

    @Mock
    private Authentication authentication;

    private AbstractAuthenticationStepTemplate authenticationStep;

    @AfterEach
    void tearDown() {
        authenticationStep = null;
    }

    @Test
    void test() {
        // given
        authenticationStep = new FirstStep(new SecondStep(null));
        given(authentication.getCredentials()).willReturn(RandomStringUtils.randomAlphanumeric(25));

        // when
        final Object authenticationResult = authenticationStep.invokeNextAuthenticationStep(authentication);

        // then
        Assertions.assertAll(
                () -> assertThat(authenticationStep.getNextAuthenticationStepTemplate()).isNull()
        );
    }
}
