package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.TENTH;

@Slf4j
public final class TenthStep extends AbstractAuthenticationStepBuilder<Void> {

    private final UserDetailsChecker customPreAuthenticationChecks;

    // disabling an option to override it
    @SuppressWarnings("unused")
    private TenthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        this(null, nextAuthenticationStepTemplate);
    }

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public TenthStep(final UserDetailsChecker customPreAuthenticationChecks,
                     final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
        this.customPreAuthenticationChecks = customPreAuthenticationChecks;
    }

    @Override
    public Try<Void> performSingleAuthOperation(final Authentication authentication) {
        log.debug("Performing authentication step number={} - {}", stepOrder().getId(), stepOrder().getDescription());
        return Try.run(() -> customPreAuthenticationChecks.check(getAuthenticationItems().getJwtUser()));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<Void> tryResult) {
        // no need to do something as logging is already done
    }

    @Override
    protected AuthenticationStepOrder stepOrder() {
        return TENTH;
    }
}
