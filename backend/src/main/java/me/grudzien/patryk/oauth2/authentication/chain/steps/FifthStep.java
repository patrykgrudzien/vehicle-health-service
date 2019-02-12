package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;

public final class FifthStep extends AbstractAuthenticationStepBuilder<Map<String, String>> {

	/**
	 * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
	 */
	public FifthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
		super(nextAuthenticationStepTemplate);
	}

	@Override
	public Try<Map<String, String>> performSingleAuthOperation(final Authentication authentication) {
		// reading map of attributes from JWT token
		final Function1<String, Try<Map<String, String>>> liftTry = CheckedFunction1.liftTry(
				input -> new ObjectMapper().readValue(input, new TypeReference<Map<String, String>>() {})
		);
		return liftTry.apply(getAuthenticationItems().getDecodedJwt().getClaims());
	}

	@Override
	public void updateAuthenticationItemsOnSuccessOperation(final Try<Map<String, String>> tryResult) {
		getAuthenticationItems().setJwtTokenAttributes(tryResult.get());
	}

	@Override
	public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<Map<String, String>> tryResult) {
		return createGenericFailedResult(tryResult);
	}
}
