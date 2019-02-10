package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public class Third extends AbstractAuthenticationStepBuilder {

    private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public Third(final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate,
                 final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
        super(nextAuthenticationStepTemplate);
        this.googlePrincipalServiceProxy = googlePrincipalServiceProxy;
    }

    @Override
    public Try<?> updateAuthenticationStateContainer(final Authentication authentication) {
        // TODO:
        return Try.of(() -> googlePrincipalServiceProxy.rsaVerifier(getAuthenticationStateContainer().getKeyIdentifier()));
    }

    @Override
    public Optional<StepStatus> performAuthenticationStep(final Authentication authentication) {
        return Optional.empty();
    }
}
