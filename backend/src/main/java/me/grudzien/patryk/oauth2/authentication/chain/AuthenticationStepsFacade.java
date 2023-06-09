package me.grudzien.patryk.oauth2.authentication.chain;

import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.oauth2.authentication.chain.steps.EighthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.EleventhStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.FifthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.FirstStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.FourthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.NinthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.SecondStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.SeventhStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.SixthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.TenthStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.ThirdStep;
import me.grudzien.patryk.oauth2.authentication.chain.steps.TwelfthStep;
import me.grudzien.patryk.oauth2.authentication.checkers.AdditionalChecks;
import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

public final class AuthenticationStepsFacade {

    private AuthenticationStepsFacade() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    @FunctionalInterface
    public interface WithGooglePrincipalServiceProxy {
        AuthenticationStepsFacade.WithCacheManagerHelper withGooglePrincipalServiceProxy(GooglePrincipalServiceProxy googlePrincipalServiceProxy);
    }

    @FunctionalInterface
    public interface WithCacheManagerHelper {
        AuthenticationStepsFacade.WithUserDetailsService withCacheManagerHelper(CacheManagerHelper cacheManagerHelper);
    }

    @FunctionalInterface
    public interface WithUserDetailsService {
        AuthenticationStepsFacade.WithLocaleMessageCreator withUserDetailsService(UserDetailsService userDetailsService);
    }

    @FunctionalInterface
    public interface WithLocaleMessageCreator {
        AuthenticationStepsFacade.WithPreUserDetailsChecker withLocaleMessageCreator(LocaleMessagesCreator localeMessagesCreator);
    }

    @FunctionalInterface
    public interface WithPreUserDetailsChecker {
        AuthenticationStepsFacade.WithPostUserDetailsChecker withPreUserDetailsChecker(UserDetailsChecker customPreAuthenticationChecks);
    }

    @FunctionalInterface
    public interface WithPostUserDetailsChecker {
        AuthenticationStepsFacade.WithAdditionalChecks withPostUserDetailsChecker(UserDetailsChecker customPostAuthenticationChecks);
    }

    @FunctionalInterface
    public interface WithAdditionalChecks {
        AuthenticationStepsFacade.Builder withAdditionalChecks(AdditionalChecks<JwtUser> additionalChecks);
    }

    @FunctionalInterface
    public interface Builder {
        AbstractAuthenticationStepBuilder<?> build();
    }

    public static WithGooglePrincipalServiceProxy buildAuthenticationFlow() {
        return googlePrincipalServiceProxy -> cacheManagerHelper -> userDetailsService -> localeMessagesCreator ->
               customPreAuthenticationChecks -> customPostAuthenticationChecks -> additionalChecks ->
                       (Builder) createAuthenticationSteps(googlePrincipalServiceProxy, cacheManagerHelper, userDetailsService,
                                                           localeMessagesCreator, customPreAuthenticationChecks, customPostAuthenticationChecks,
                                                           additionalChecks);
    }

    private static AbstractAuthenticationStepBuilder<?> createAuthenticationSteps(
            final GooglePrincipalServiceProxy googlePrincipalServiceProxy, final CacheManagerHelper cacheManagerHelper,
            final UserDetailsService userDetailsService, final LocaleMessagesCreator localeMessagesCreator,
            final UserDetailsChecker customPreAuthenticationChecks, final UserDetailsChecker customPostAuthenticationChecks,
            final AdditionalChecks<JwtUser> additionalChecks) {
    	return getJWTokenFromAuthentication(
    	        getKeyIdAttributeFromJWToken(
    	          verifySignatureUsingRSA(googlePrincipalServiceProxy,
                    decodeJWToken(
	                  readMapOfAttributesFromJWToken(
	                    loadEmailAttribute(
	                      loadSubjectIdentifier(
	                        clearPrincipalUserCache(cacheManagerHelper,
                              loadUserFromDB(userDetailsService, localeMessagesCreator,
                                preAuthenticationChecks(customPreAuthenticationChecks,
                                  postAuthenticationChecks(customPostAuthenticationChecks,
                                    additionalAuthenticationChecks(additionalChecks))))))))))));
    }

	private static AbstractAuthenticationStepBuilder<?> getJWTokenFromAuthentication(final AbstractAuthenticationStepTemplate<?> nextStep) {
    	return new FirstStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> getKeyIdAttributeFromJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new SecondStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> verifySignatureUsingRSA(final GooglePrincipalServiceProxy googlePrincipalServiceProxy,
	                                                                            final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new ThirdStep(googlePrincipalServiceProxy, nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> decodeJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
    	return new FourthStep(nextStep);
	}

	private static AbstractAuthenticationStepBuilder<?> readMapOfAttributesFromJWToken(final AbstractAuthenticationStepTemplate<?> nextStep) {
		return new FifthStep(nextStep);
	}

    private static AbstractAuthenticationStepBuilder<?> loadEmailAttribute(final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new SixthStep(nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> loadSubjectIdentifier(final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new SeventhStep(nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> clearPrincipalUserCache(final CacheManagerHelper cacheManagerHelper,
                                                                                final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new EighthStep(cacheManagerHelper, nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> loadUserFromDB(final UserDetailsService userDetailsService,
                                                                       final LocaleMessagesCreator localeMessagesCreator,
                                                                       final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new NinthStep(userDetailsService, localeMessagesCreator, nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> preAuthenticationChecks(final UserDetailsChecker customPreAuthenticationChecks,
                                                                                final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new TenthStep(customPreAuthenticationChecks, nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> postAuthenticationChecks(final UserDetailsChecker customPostAuthenticationChecks,
                                                                                 final AbstractAuthenticationStepTemplate<?> nextStep) {
        return new EleventhStep(customPostAuthenticationChecks, nextStep);
    }

    private static AbstractAuthenticationStepBuilder<?> additionalAuthenticationChecks(final AdditionalChecks<JwtUser> additionalChecks) {
        return new TwelfthStep(additionalChecks, null); // no next step as it's the final
    }
}
