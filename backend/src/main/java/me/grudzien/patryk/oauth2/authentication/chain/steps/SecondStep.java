package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.SECOND;

public final class SecondStep extends AbstractAuthenticationStepBuilder<String> {

    private static final String KEY_ID_ATTRIBUTE = "kid";

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public SecondStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
    }

    @Override
    public Try<String> performSingleAuthOperation(final Authentication authentication) {
        return Try.of(() -> JwtHelper.headers(getAuthenticationItems().getToken())
                                     .get(KEY_ID_ATTRIBUTE));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<String> tryResult) {
        getAuthenticationItems().setKeyIdentifier(tryResult.get());
    }

    @Override
    protected AuthenticationStepOrder specifyStepOrder() {
        return SECOND;
    }
}
