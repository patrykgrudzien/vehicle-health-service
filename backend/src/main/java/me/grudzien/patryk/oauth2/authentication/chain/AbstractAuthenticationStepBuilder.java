package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.API.Match.Pattern0;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;

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
    AbstractAuthenticationStepBuilder(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    // template method
    @Override
    public final Optional<AuthenticationResult> performAuthenticationSteps(final Authentication authentication) {
        final Try<T> tryResult = updateAuthenticationStateContainer(authentication);
        return Match(tryResult).of(
                Case($Success($()), () -> {
                    log.info("Authentication State Container has been successfully updated. Going to the next operation.");
                    handleSuccessTry(tryResult);
                    return invokeNextAuthenticationStep(authentication);
                }),
                Case($Failure(Pattern0.of(Throwable.class)), () -> {
                    log.error("Authentication State Container couldn't be updated! Next operation won't be executed!");
                    return handleFailureTry(tryResult);
                })
        );
    }

    @Override
    public final Optional<AuthenticationResult> invokeNextAuthenticationStep(final Authentication authentication) {
        return nextStepExists() ?
                callNextStep(authentication) : finishAuthentication();
    }

    private Optional<AuthenticationResult> finishAuthentication() {
        getAuthenticationItems().clearState();
        return Optional.of(getAuthenticationResult().OK());
    }
}
