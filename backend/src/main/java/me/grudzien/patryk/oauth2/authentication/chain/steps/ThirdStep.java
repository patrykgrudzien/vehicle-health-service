package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public final class ThirdStep extends AbstractAuthenticationStepBuilder<RsaVerifier> {

    private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;

    // disabling an option to override it
	@SuppressWarnings("unused")
    private ThirdStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
		this(nextAuthenticationStepTemplate, null);
	}

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public ThirdStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate,
                     final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
        super(nextAuthenticationStepTemplate);
        this.googlePrincipalServiceProxy = googlePrincipalServiceProxy;
    }

    @Override
    public Try<RsaVerifier> updateAuthenticationItemsContainer(final Authentication authentication) {
        return Try.of(() -> googlePrincipalServiceProxy.rsaVerifier(getAuthenticationItems().getKeyIdentifier()));
    }

    @Override
    public void handleSuccessAuthenticationItemsUpdate(final Try<RsaVerifier> tryResult) {
        getAuthenticationItems().setRsaVerifier(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailedAuthenticationItemsUpdate(final Try<RsaVerifier> tryResult) {
	    return createGenericFailedResult(tryResult);
    }
}
