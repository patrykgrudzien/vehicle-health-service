package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;

import java.util.Optional;

public class SecondStep extends AbstractAuthenticationStepBuilder<String> {

    private static final String KEY_ID_ATTRIBUTE = "kid";

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    SecondStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> updateAuthenticationStateContainer(final Authentication authentication) {
        return Try.of(() -> JwtHelper.headers(getAuthenticationItems().getToken())
                                     .get(KEY_ID_ATTRIBUTE));
    }

    @Override
    public void handleSuccessTry(final Try<String> tryResult) {
        getAuthenticationItems().setKeyIdentifier(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureTry(final Try<String> tryResult) {
        final Throwable cause = tryResult.getCause();
        return Optional.of(getAuthenticationResult().FAILED(cause, cause.getMessage()));
    }
}
