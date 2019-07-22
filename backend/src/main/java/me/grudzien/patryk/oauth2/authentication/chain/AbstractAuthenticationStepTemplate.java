package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

@Getter
public abstract class AbstractAuthenticationStepTemplate<OperationResultType> {

    private final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate;
    private final AuthenticationStepOrder authenticationStepOrder;

    // containers
    @Setter
    private AuthenticationItems authenticationItems = AuthenticationItems.getInstance();
    private final AuthenticationResult authenticationResult = AuthenticationResult.getInstance();

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    protected AbstractAuthenticationStepTemplate(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        this.nextAuthenticationStepTemplate = nextAuthenticationStepTemplate;
        this.authenticationStepOrder = stepOrder();
    }

    /**
     * Template method that specifies required flow execution for each {@link AbstractAuthenticationStepTemplate}.
     *
     * @param authentication {@link Authentication} object.
     * @return {@link AuthenticationResult} which will be used to determine final result of
     * {@link me.grudzien.patryk.oauth2.authentication.CustomAuthenticationProvider#authenticate(Authentication)}
     */
	public abstract Optional<AuthenticationResult> performAuthenticationSteps(final Authentication authentication);

    /**
     * Method that checks if the next authentication step exists and invokes it when there was no exception thrown before.
     * @param authentication {@link Authentication} object.
     * @return {@link AuthenticationResult} object.
     */
	public abstract Optional<AuthenticationResult> invokeNextAuthenticationStep(final Authentication authentication);

    /**
     * Step's specific method in each authentication step which is responsible for performing appropriate authentication logic.
     * @param authentication {@link Authentication} object.
     * @return {@link Try} object that holds result of performed function.
     */
    public abstract Try<OperationResultType> performSingleAuthOperation(final Authentication authentication);

    /**
     * Method that must inherit all authentication steps instances which purpose is to update
     * {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationItems} with specific action's result once
     * {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)} was successful.
     * @param tryResult result of {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)}
     */
    public abstract void updateAuthenticationItemsOnSuccessOperation(final Try<OperationResultType> tryResult);

	/**
	 * Method that must inherit all authentication steps instances which purpose is to execute specific logic that handles failure of:
	 * {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)} and returns {@link AuthenticationResult}.
	 * @param tryResult result of {@link AbstractAuthenticationStepTemplate#performSingleAuthOperation(Authentication)}
	 */
	// TODO: update comment here
    public abstract Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<OperationResultType> tryResult);

    /**
     * Specifies the {@link me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder}
     * for each particular {@link me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder}
     * implementation in which the authentication flow is going to be performed.
     */
    protected abstract AuthenticationStepOrder stepOrder();
}
