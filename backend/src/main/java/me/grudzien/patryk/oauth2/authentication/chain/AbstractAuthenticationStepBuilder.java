package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.API.Match.Pattern0;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

@Log4j2
public abstract class AbstractAuthenticationStepBuilder<T> extends AbstractAuthenticationStepTemplate<T> {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public AbstractAuthenticationStepBuilder(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    // template method
    @Override
    public final Optional<AuthenticationResult> performAuthenticationSteps(final Authentication authentication) {
        final Try<T> tryResult = performSingleAuthOperation(authentication);
        return Match(tryResult).of(
                Case($Success($()), () -> {
                    log.info("Authentication State Container has been successfully updated. Going to the next operation.");
                    updateAuthenticationItemsOnSuccessOperation(tryResult);
                    return invokeNextAuthenticationStep(authentication);
                }),
                Case($Failure(Pattern0.of(Throwable.class)), () -> {
                    log.error("Authentication State Container couldn't be updated! Next operation won't be executed!");
                    return handleFailureDuringAuthOperation(tryResult);
                })
        );
    }

    @Override
    public final Optional<AuthenticationResult> invokeNextAuthenticationStep(final Authentication authentication) {
        return nextStepExists() ?
                callNextStep(authentication) : finishAuthentication();
    }

	/**
	 * Generic method that handles failed update of {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationItems}.
	 * @param tryResult result of {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)}
	 * @return {@link AuthenticationResult} object with failed state.
	 */
	protected final Optional<AuthenticationResult> createGenericFailedResult(final Try<T> tryResult) {
        getAuthenticationItems().clearState();
		final Throwable cause = tryResult.getCause();
		return Optional.of(getAuthenticationResult().failed(cause, cause.getMessage()));
	}

	/**
	 * Method that finishes authentication steps flow with:
	 * <ol>
	 *     <li>Cleaning {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationItems} state</li>
	 *     <li>Preparing <b>OK</b> {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult}</li>
	 * </ol>
	 * @return {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult} object.
	 */
	private Optional<AuthenticationResult> finishAuthentication() {
        getAuthenticationItems().clearState();
        return Optional.of(getAuthenticationResult().ok(getAuthenticationItems().getCustomAuthenticationToken()));
    }

	/**
	 * Helper method which checks if there is a next authentication step in a chain.
	 * @return {@code true} if a next authentication step in the chain is present, false otherwise.
	 */
	private boolean nextStepExists() {
		return Objects.nonNull(getNextAuthenticationStepTemplate());
	}

	/**
	 * Helper method that takes the next authentication step in a chain and calls template method.
	 * @param authentication {@link Authentication} object.
	 * @return {@link AuthenticationResult} of the next authentication step in a chain.
	 */
	private Optional<AuthenticationResult> callNextStep(final Authentication authentication) {
		return getNextAuthenticationStepTemplate().performAuthenticationSteps(authentication);
	}
}
