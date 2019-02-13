package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;

public final class SixthStep extends AbstractAuthenticationStepBuilder<String> {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public SixthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> performSingleAuthOperation(final Authentication authentication) {
        // loading email attribute
        return Try.of(() -> (String) getAuthenticationItems().getJwtTokenAttributes()
                                                             .get(StandardClaimNames.EMAIL));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<String> tryResult) {
        getAuthenticationItems().setEmail(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<String> tryResult) {
        return createGenericFailedResult(tryResult);
    }
}
