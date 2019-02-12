package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class AbstractAuthenticationStepTemplate<T> {

    private final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate;

    // containers
    private final AuthenticationItems authenticationItems = AuthenticationItems.getInstance();
    private final AuthenticationResult authenticationResult = AuthenticationResult.getInstance();

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
     * Step's specific method in each authentication step which is responsible for performing specific business logic.
     * @param authentication {@link Authentication} object.
     * @return {@link Try} object that holds result of performed function.
     */
    public abstract Try<T> updateAuthenticationItemsContainer(final Authentication authentication);

    /**
     * Method that must inherit all authentication steps instances to perform specific logic when
     * update made against {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationItems} was successful.
     * @param tryResult result of {@link AbstractAuthenticationStepTemplate#updateAuthenticationItemsContainer(Authentication)}
     */
    public abstract void handleSuccessAuthenticationItemsUpdate(final Try<T> tryResult);

    /**
     * Method that must inherit all authentication steps instances to perform specific logic when
     * update made against {@link me.grudzien.patryk.oauth2.authentication.chain.AuthenticationItems} was failed.
     * @param tryResult result of {@link AbstractAuthenticationStepTemplate#updateAuthenticationItemsContainer(Authentication)}
     */
    public abstract Optional<AuthenticationResult> handleFailedAuthenticationItemsUpdate(final Try<T> tryResult);
}
