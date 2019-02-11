package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public class Third extends AbstractAuthenticationStepBuilder {

    private final GooglePrincipalServiceProxy googlePrincipalServiceProxy;

    // disabling an option to override it
	private Third(final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate) {
		this(nextAuthenticationStepTemplate, null);
	}

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
        final AuthenticationStateContainer authenticationStateContainer = getAuthenticationStateContainer();
        return Try.of(() -> googlePrincipalServiceProxy.rsaVerifier(authenticationStateContainer.getKeyIdentifier()))
                  .onSuccess(authenticationStateContainer::setRsaVerifier)
                  .orElse(() -> {
                      final RuntimeException exception = new RuntimeException("Could NOT obtain RSA!");
                      authenticationStateContainer.setThrowable(exception);
                      return Try.failure(exception);
                  });
    }

	@Override
	public void setAuthenticationResult() {

	}
}
