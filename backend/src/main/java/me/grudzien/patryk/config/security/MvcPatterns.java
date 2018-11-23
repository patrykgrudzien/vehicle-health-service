package me.grudzien.patryk.config.security;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.PropertiesKeeper.StaticResources;

final class MvcPatterns {

    private MvcPatterns() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    static String APIContextPathWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + "/**";
    }

    static String authWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/auth**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.endpoints().AUTH + "**";
    }

    static String registrationWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/registration**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.endpoints().REGISTRATION + "/**";
    }

    static String refreshToken(final PropertiesKeeper propertiesKeeper) {
        // /api/refresh-token
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.endpoints().REFRESH_TOKEN;
    }

    static String userLoggedInUsingGoogleWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-logged-in-using-google**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE + "**";
    }

    static String userNotFoundWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-not-found**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_NOT_FOUND + "**";
    }

    static String userAccountIsLockedWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-account-is-locked**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_ACCOUNT_IS_LOCKED + "**";
    }

    static String userIsDisabledWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-is-disabled**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_IS_DISABLED + "**";
    }

    static String userAccountIsExpiredWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-account-is-expired**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_ACCOUNT_IS_EXPIRED + "**";
    }

    static String userRegisteredUsingGoogleWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-registered-using-google**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_REGISTERED_USING_GOOGLE + "**";
    }

    static String userAccountAlreadyExistsWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/user-account-already-exists**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().USER_ACCOUNT_ALREADY_EXISTS + "**";
    }

    static String credentialsHaveExpiredWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/credentials-have-expired**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().CREDENTIALS_HAVE_EXPIRED + "**";
    }

    static String jwtTokenNotFoundWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/jwt-token-not-found**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().JWT_TOKEN_NOT_FOUND + "**";
    }

    static String registrationProviderMismatchWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/registration-provider-mismatch**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().REGISTRATION_PROVIDER_MISMATCH + "**";
    }

    static String badCredentialsWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/bad-credentials**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().BAD_CREDENTIALS + "**";
    }

    static String exchangeShortLivedTokenWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/exchange-short-lived-token**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().EXCHANGE_SHORT_LIVED_TOKEN + "**";
    }

    static String failureTargetUrlWildcard(final PropertiesKeeper propertiesKeeper) {
        // /api/failure-target-url**
        return propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.oAuth2().FAILURE_TARGET_URL + "**";
    }

    // IGNORED URL's BELOW
    static String h2InMemoryWebConsole() {
        return "/h2-console/**";
    }

    /**
     * Allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token.
     * This helps to avoid duplicate calls before the specific ones.
     */
    static String rootWildcard() {
        return "/**";
    }

    static String[] staticResources() {
        return StaticResources.ALL;
    }

    static String UIContextPathWildcard() {
        // /ui**
        return PropertiesKeeper.FrontendRoutes.UI_CONTEXT_PATH + "**";
    }
}
