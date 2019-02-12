package me.grudzien.patryk.oauth2.authentication.chain;

import me.grudzien.patryk.oauth2.authentication.chain.steps.FirstStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.SecondStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.ThirdStep;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public final class AuthenticationStepsFacade {

    private AuthenticationStepsFacade() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public static AbstractAuthenticationStepBuilder<?> buildAuthenticationFlow(final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
    	return
		getJWTokenFromAuthentication(
			getKeyIdAttributeFromJWToken(
    	        verifySignatureUsingRSA(null, googlePrincipalServiceProxy)
			)
		);
    }

	private static AbstractAuthenticationStepBuilder<?> getJWTokenFromAuthentication(final AbstractAuthenticationStepTemplate<?> nextStep) {
    	return new FirstStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> getKeyIdAttributeFromJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new SecondStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> verifySignatureUsingRSA(final AbstractAuthenticationStepTemplate<?> nextStep,
	                                                                            final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
		return new ThirdStep(nextStep, googlePrincipalServiceProxy);
	}
}
