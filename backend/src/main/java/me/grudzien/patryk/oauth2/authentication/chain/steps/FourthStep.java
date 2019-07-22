package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.FOURTH;

public final class FourthStep extends AbstractAuthenticationStepBuilder<Jwt> {

	/**
	 * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
	 */
	public FourthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
		super(nextAuthenticationStepTemplate);
	}

	@Override
	public Try<Jwt> performSingleAuthOperation(final Authentication authentication) {
		return Try.of(() -> JwtHelper.decodeAndVerify(getAuthenticationItems().getToken(),
		                                              getAuthenticationItems().getRsaVerifier()));
	}

	@Override
	public void updateAuthenticationItemsOnSuccessOperation(final Try<Jwt> tryResult) {
		getAuthenticationItems().setDecodedJwt(tryResult.get());
	}

    @Override
    protected AuthenticationStepOrder specifyStepOrder() {
        return FOURTH;
    }
}
