package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.FIRST;

@Log4j2
public final class FirstStep extends AbstractAuthenticationStepBuilder<String> {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public FirstStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> performSingleAuthOperation(final Authentication authentication) {
        log.debug("Performing authentication step number={} - {}", stepOrder().getId(), stepOrder().getDescription());
        return Try.of(() -> (String) authentication.getCredentials());
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<String> tryResult) {
        getAuthenticationItems().setToken(tryResult.get());
    }

    @Override
    protected AuthenticationStepOrder stepOrder() {
        return FIRST;
    }
}
