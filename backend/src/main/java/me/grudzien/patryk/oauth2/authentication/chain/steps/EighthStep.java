package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.chain.AuthenticationResult;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.authentication.service.MyUserDetailsService;

public final class EighthStep extends AbstractAuthenticationStepBuilder<Void> {

    private final CacheManagerHelper cacheManagerHelper;

    // disabling an option to override it
    @SuppressWarnings("unused")
    private EighthStep(final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        this(null, nextAuthenticationStepTemplate);
    }

    /**
     * @param nextAuthenticationStepTemplate Next authentication step in a chain of responsibility.
     */
    public EighthStep(final CacheManagerHelper cacheManagerHelper,
                      final AbstractAuthenticationStepTemplate<?> nextAuthenticationStepTemplate) {
        super(nextAuthenticationStepTemplate);
        this.cacheManagerHelper = cacheManagerHelper;
    }

    @Override
    public Try<Void> performSingleAuthOperation(final Authentication authentication) {
        // cleaning user from cache because it's been saved (with "enabled" status = FALSE) before email confirmation
        return Try.run(() -> cacheManagerHelper.clearAllCache(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME));
    }

    @Override
    public void updateAuthenticationItemsOnSuccessOperation(final Try<Void> tryResult) {
        // no need to do something as cacheManagerHelper already performs log.info()
    }

    @Override
    public Optional<AuthenticationResult> handleFailureDuringAuthOperation(final Try<Void> tryResult) {
        return createGenericFailedResult(tryResult);
    }
}
