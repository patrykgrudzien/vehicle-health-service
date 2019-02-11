package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.util.Optional;

import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public class ThirdStep extends AbstractAuthenticationStepBuilder<RsaVerifier> {

    private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;

    // disabling an option to override it
	@SuppressWarnings("unused")
    private ThirdStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
		this(nextAuthenticationStepTemplate, null);
	}

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    ThirdStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate,
                     final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
        super(nextAuthenticationStepTemplate);
        this.googlePrincipalServiceProxy = googlePrincipalServiceProxy;
    }

    @Override
    public Try<RsaVerifier> updateAuthenticationStateContainer(final Authentication authentication) {
        return Try.of(() -> googlePrincipalServiceProxy.rsaVerifier(getAuthenticationItems().getKeyIdentifier()));
    }

    @Override
    public void handleSuccessTry(final Try<RsaVerifier> tryResult) {
        getAuthenticationItems().setRsaVerifier(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureTry(final Try<RsaVerifier> tryResult) {
        final Throwable cause = tryResult.getCause();
        return Optional.of(getAuthenticationResult().FAILED(cause, cause.getMessage()));
    }
}
