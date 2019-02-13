package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

public final class NinthStep extends AbstractAuthenticationStepBuilder<JwtUser> {

    private final UserDetailsService userDetailsService;
    private final LocaleMessagesCreator localeMessagesCreator;

    // disabling an option to override it
    @SuppressWarnings("unused")
    private NinthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        this(null, null, nextAuthenticationStepTemplate);
    }

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public NinthStep(final UserDetailsService userDetailsService, final LocaleMessagesCreator localeMessagesCreator,
                     final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    @Override
    public Try<JwtUser> performSingleAuthOperation(final Authentication authentication) {
        final String email = getAuthenticationItems().getEmail();
        // loading user from DB
        return Try.of(() -> Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email))
                                    .orElseThrow(() -> new UsernameNotFoundException(
                                            localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email))));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<JwtUser> tryResult) {
        getAuthenticationItems().setJwtUser(tryResult.get());
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<JwtUser> tryResult) {
        return createGenericFailedResult(tryResult);
    }
}
