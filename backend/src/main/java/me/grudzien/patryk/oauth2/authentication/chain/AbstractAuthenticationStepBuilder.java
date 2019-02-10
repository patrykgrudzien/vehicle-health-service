package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

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
        return Match(updateAuthenticationStateContainer(authentication).isSuccess()).of(
                Case($(true), () -> {
                    log.info("Authentication State Container has been successfully updated. Going to the next operation.");
                    return performAuthenticationStep(authentication);
                }),
                Case($(false), () -> {
                    log.error("Authentication State Container couldn't be updated! Next operation won't be executed!");
                    return Optional.of(StepStatus.ERROR);
                })
        );
    }
}
