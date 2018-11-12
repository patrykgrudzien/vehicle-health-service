package me.grudzien.patryk.config.security;

import me.grudzien.patryk.PropertiesKeeper;

final class MvcPatterns {

    private MvcPatterns() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    /**
     * Allow calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token.
     * This helps to avoid duplicate calls before the specific ones.
     */
    static String homeWildcard() {
        return "/**";
    }

    static String auth(final PropertiesKeeper propertiesKeeper) {
        // /auth
        return propertiesKeeper.endpoints().AUTH;
    }

    static String authWildcard(final PropertiesKeeper propertiesKeeper) {
        // /auth/**
        return propertiesKeeper.endpoints().AUTH + "/**";
    }

    static String registrationWildcard(final PropertiesKeeper propertiesKeeper) {
        // /registration/**
        return propertiesKeeper.endpoints().REGISTRATION + "/**";
    }

    static String refreshToken(final PropertiesKeeper propertiesKeeper) {
        // /refresh-token
        return propertiesKeeper.endpoints().REFRESH_TOKEN;
    }

    static String userLoggedInUsingGoogleWildcard(final PropertiesKeeper propertiesKeeper) {
        // //user-logged-in-using-google**
        return propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE + "**";
    }

    static String userNotFoundWildcard(final PropertiesKeeper propertiesKeeper) {
        // /user-not-found**
        return propertiesKeeper.oAuth2().USER_NOT_FOUND + "**";
    }

    static String userIsDisabledWildcard(final PropertiesKeeper propertiesKeeper) {
        // /user-is-disabled**
        return propertiesKeeper.oAuth2().USER_IS_DISABLED + "**";
    }

    static String userRegisteredUsingGoogleWildcard(final PropertiesKeeper propertiesKeeper) {
        // /user-registered-using-google**
        return propertiesKeeper.oAuth2().USER_REGISTERED_USING_GOOGLE + "**";
    }

    static String userAccountAlreadyExistsWildcard(final PropertiesKeeper propertiesKeeper) {
        // /user-account-already-exists**
        return propertiesKeeper.oAuth2().USER_ACCOUNT_ALREADY_EXISTS + "**";
    }

    static String failureTargetUrlWildcard(final PropertiesKeeper propertiesKeeper) {
        // /failure-target-url**
        return propertiesKeeper.oAuth2().FAILURE_TARGET_URL + "**";
    }

    static String h2InMemoryWebConsole() {
        return "/h2-console/**";
    }
}
