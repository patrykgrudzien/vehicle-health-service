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
		this(null, nextAuthenticationStepTemplate);
	}

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public ThirdStep(final GooglePrincipalServiceProxy googlePrincipalServiceProxy,
                     final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
        this.googlePrincipalServiceProxy = googlePrincipalServiceProxy;
    }

    @Override
    public Try<RsaVerifier> performSingleAuthOperation(final Authentication authentication) {
        return Try.of(() -> googlePrincipalServiceProxy.rsaVerifier(getAuthenticationItems().getKeyIdentifier()));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<RsaVerifier> tryResult) {
        getAuthenticationItems().setRsaVerifier(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<RsaVerifier> tryResult) {
	    return createGenericFailedResult(tryResult);
    }
}