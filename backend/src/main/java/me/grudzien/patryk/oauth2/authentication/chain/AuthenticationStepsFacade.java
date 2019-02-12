package me.grudzien.patryk.oauth2.authentication.chain;

import me.grudzien.patryk.oauth2.authentication.chain.steps.FifthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.FirstStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.FourthStep;
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
	                verifySignatureUsingRSA(googlePrincipalServiceProxy,
	                    decodeJWToken(
	                        readMapOfAttributesFromJWToken()
	                    )
	                )
				)
			);
    }

	private static AbstractAuthenticationStepBuilder<?> getJWTokenFromAuthentication(final AbstractAuthenticationStepTemplate<?> nextStep) {
    	return new FirstStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> getKeyIdAttributeFromJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new SecondStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> verifySignatureUsingRSA(final GooglePrincipalServiceProxy googlePrincipalServiceProxy,
	                                                                            final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new ThirdStep(googlePrincipalServiceProxy, nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> decodeJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
    	return new FourthStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> readMapOfAttributesFromJWToken() {
		return new FifthStep(null);
	}
}
