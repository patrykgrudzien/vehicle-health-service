package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.TWELFTH;

@Slf4j
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
        log.debug("Performing authentication step number={} - {}", stepOrder().getId(), stepOrder().getDescription());
        // ADDITIONAL-authentication checks
        final JwtUser jwtUser = getAuthenticationItems().getJwtUser();
        return Try.of(() -> {
            final CustomAuthenticationToken customAuthenticationToken = CustomAuthenticationToken.Builder()
                                                                                                 .principal(jwtUser)
                                                                                                 .jwtToken(getAuthenticationItems().getToken())
                                                                                                 .authorities(jwtUser.getAuthorities())
                                                                                                 .build();
            additionalChecks.additionalAuthenticationChecks(jwtUser, customAuthenticationToken, getAuthenticationItems().getSubjectIdentifier());
            return customAuthenticationToken;
        });
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<CustomAuthenticationToken> tryResult) {
        getAuthenticationItems().setCustomAuthenticationToken(tryResult.get());
    }

    @Override
    protected AuthenticationStepOrder stepOrder() {
        return TWELFTH;
    }
}
