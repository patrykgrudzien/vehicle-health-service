package me.grudzien.patryk.oauth2.authentication.chain.steps;

import io.vavr.control.Try;

import org.springframework.security.core.Authentication;

import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepBuilder;
import me.grudzien.patryk.oauth2.authentication.chain.AbstractAuthenticationStepTemplate;
import me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;

import static me.grudzien.patryk.oauth2.authentication.model.enums.AuthenticationStepOrder.EIGHT;

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
    protected AuthenticationStepOrder specifyStepOrder() {
        return EIGHT;
    }
}
