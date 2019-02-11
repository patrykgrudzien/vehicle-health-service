package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

public class FirstStep extends AbstractAuthenticationStepBuilder<String> {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    FirstStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> updateAuthenticationStateContainer(final Authentication authentication) {
        return Try.of(() -> (String) authentication.getCredentials());
    }

    @Override
    public void handleSuccessTry(final Try<String> tryResult) {
        getAuthenticationItems().setToken(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureTry(final Try<String> tryResult) {
        final Throwable cause = tryResult.getCause();
        return Optional.of(getAuthenticationResult().FAILED(cause, cause.getMessage()));
    }
}
