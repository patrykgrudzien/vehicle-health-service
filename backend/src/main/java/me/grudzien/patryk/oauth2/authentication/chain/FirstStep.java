package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

public class FirstStep extends AbstractAuthenticationStepBuilder {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public FirstStep(final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<?> updateAuthenticationStateContainer(final Authentication authentication) {
        return Try.run(() -> getAuthenticationStateContainer().setToken((String) authentication.getCredentials()));
    }

    @Override
    public Optional<StepStatus> performAuthenticationStep(final Authentication authentication) {
        return nextStepExists() ? callNextStep(authentication) : Optional.of(StepStatus.OK);
    }
}
