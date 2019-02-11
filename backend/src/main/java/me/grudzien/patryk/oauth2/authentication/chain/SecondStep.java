package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;

public class SecondStep extends AbstractAuthenticationStepBuilder {

    private static final String KEY_ID_ATTRIBUTE = "kid";

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public SecondStep(final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<?> updateAuthenticationStateContainer(final Authentication authentication) {
        return Try.run(() -> {
            final AuthenticationStateContainer stateContainer = getAuthenticationStateContainer();
            stateContainer.setKeyIdentifier(JwtHelper.headers(stateContainer.getToken()).get(KEY_ID_ATTRIBUTE));
        });
    }
}
