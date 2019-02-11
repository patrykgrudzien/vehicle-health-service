package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractAuthenticationStepTemplate {

    @Getter
    private final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate;

	public abstract Optional<AuthenticationResult> performAllAuthenticationSteps(final Authentication authentication);

	public abstract Optional<AuthenticationResult> invokeNextAuthenticationStep(final Authentication authentication);

    @Getter
    private final AuthenticationStateContainer authenticationStateContainer = AuthenticationStateContainer.getInstance();

    public abstract Try<?> updateAuthenticationStateContainer(final Authentication authentication);

    @Getter
	private final AuthenticationResult authenticationResult = AuthenticationResult.getInstance();

    // TODO: maybe two auth statuses inside that could be called in case of $Success / $Failure
    public abstract void setAuthenticationResult();

    // helpers
    final boolean nextStepExists() {
    	return Objects.nonNull(getNextAuthenticationStepTemplate());
    }

    final Optional<AuthenticationResult> callNextStep(final Authentication authentication) {
        return getNextAuthenticationStepTemplate().performAllAuthenticationSteps(authentication);
    }
}
