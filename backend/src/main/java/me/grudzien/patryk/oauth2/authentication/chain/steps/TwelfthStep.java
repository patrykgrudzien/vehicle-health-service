package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;

public final class TwelfthStep extends AbstractAuthenticationStepBuilder<CustomAuthenticationToken> {

    private final AdditionalChecks<JwtUser> additionalChecks;

    // disabling an option to override it
    @SuppressWarnings("unused")
    private TwelfthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        this(null, nextAuthenticationStepTemplate);
    }

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public TwelfthStep(final AdditionalChecks<JwtUser> additionalChecks,
                       final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
        this.additionalChecks = additionalChecks;
    }

    @Override
    public Try<CustomAuthenticationToken> performSingleAuthOperation(final Authentication authentication) {
        // ADDITIONAL-authentication checks
        final JwtUser jwtUser = getAuthenticationItems().getJwtUser();
        return Try.of(() -> {
            final CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(jwtUser, getAuthenticationItems().getToken(),
                                                                                                      jwtUser.getAuthorities());
            additionalChecks.additionalAuthenticationChecks(jwtUser, customAuthenticationToken, getAuthenticationItems().getSubjectIdentifier());
            return customAuthenticationToken;
        });
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<CustomAuthenticationToken> tryResult) {
        getAuthenticationItems().setCustomAuthenticationToken(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<CustomAuthenticationToken> tryResult) {
        return createGenericFailedResult(tryResult);
    }
}
