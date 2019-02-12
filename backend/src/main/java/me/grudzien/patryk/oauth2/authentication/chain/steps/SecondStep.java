package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;

public final class SecondStep extends AbstractAuthenticationStepBuilder<String> {

    private static final String KEY_ID_ATTRIBUTE = "kid";

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public SecondStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> updateAuthenticationItemsContainer(final Authentication authentication) {
        return Try.of(() -> JwtHelper.headers(getAuthenticationItems().getToken())
                                     .get(KEY_ID_ATTRIBUTE));
    }

    @Override
    public void handleSuccessAuthenticationItemsUpdate(final Try<String> tryResult) {
        getAuthenticationItems().setKeyIdentifier(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailedAuthenticationItemsUpdate(final Try<String> tryResult) {
	    return createGenericFailedResult(tryResult);
    }
}
