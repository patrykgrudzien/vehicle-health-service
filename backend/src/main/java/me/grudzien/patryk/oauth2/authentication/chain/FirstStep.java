package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

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
	public void setAuthenticationResult() {

	}
}
