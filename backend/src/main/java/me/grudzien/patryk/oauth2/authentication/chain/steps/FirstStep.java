package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;

public final class FirstStep extends AbstractAuthenticationStepBuilder<String> {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public FirstStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> performSingleAuthOperation(final Authentication authentication) {
        return Try.of(() -> (String) authentication.getCredentials());
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<String> tryResult) {
        getAuthenticationItems().setToken(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<String> tryResult) {
    	return createGenericFailedResult(tryResult);
    }
}
