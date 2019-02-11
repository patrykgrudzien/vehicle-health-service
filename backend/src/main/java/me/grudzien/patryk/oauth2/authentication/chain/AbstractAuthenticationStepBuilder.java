package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.API.Match.Pattern0;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

@Getter
@Log4j2
public abstract class AbstractAuthenticationStepBuilder extends AbstractAuthenticationStepTemplate {

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    AbstractAuthenticationStepBuilder(final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    // template method
    @Override
    public final Optional<StepStatus> performAllAuthenticationSteps(final Authentication authentication) {
        return Match(updateAuthenticationStateContainer(authentication)).of(
                Case($Success($()), () -> {
                    log.info("Authentication State Container has been successfully updated. Going to the next operation.");
                    return invokeNextAuthenticationStep(authentication);
                }),
                Case($Failure(Pattern0.of(Throwable.class)), () -> {
                    log.error("Authentication State Container couldn't be updated! Next operation won't be executed!");
                    return Optional.of(StepStatus.ERROR);
                })
        );
    }

    @Override
    public final Optional<StepStatus> invokeNextAuthenticationStep(final Authentication authentication) {
        return nextStepExists() ? callNextStep(authentication) : Optional.of(StepStatus.OK);
    }
}
