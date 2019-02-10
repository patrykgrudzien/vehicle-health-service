package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class AbstractAuthenticationStepTemplate {

    enum StepStatus {
        OK, ERROR
    }

    @Getter
    private final AbstractAuthenticationStepTemplate nextAuthenticationStepTemplate;

    @Getter
    private final AuthenticationStateContainer authenticationStateContainer = AuthenticationStateContainer.getInstance();

    public abstract Try<?> updateAuthenticationStateContainer(final Authentication authentication);

    public abstract Optional<StepStatus> performAuthenticationStep(final Authentication authentication);

    public abstract Optional<StepStatus> performAllAuthenticationSteps(final Authentication authentication);

    // helpers
    boolean nextStepExists() {
        final Predicate<Object> nextStepExists = Objects::nonNull;
        return nextStepExists.test(getNextAuthenticationStepTemplate());
    }

    Optional<StepStatus> callNextStep(final Authentication authentication) {
        return getNextAuthenticationStepTemplate().performAllAuthenticationSteps(authentication);
    }
}
