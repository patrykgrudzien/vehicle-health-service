package me.grudzien.patryk.oauth2.resource.google;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = NONE)
final class GoogleResourceDefinitions {

    private static final String GOOGLE_OAUTH2_RESOURCE = "/google-oauth2";

    static final String GOOGLE_OAUTH2_RESOURCE_ROOT = API_VERSION + GOOGLE_OAUTH2_RESOURCE;
    static final String USER_NOT_FOUND = "/user-not-found";
    static final String USER_ACCOUNT_IS_LOCKED = "/user-account-is-locked";
    static final String USER_IS_DISABLED = "/user-is-disabled";
    static final String USER_ACCOUNT_IS_EXPIRED = "/user-account-is-expired";
    static final String USER_ACCOUNT_ALREADY_EXISTS = "/user-account-already-exists";
    static final String CREDENTIALS_HAVE_EXPIRED = "/credentials-have-expired";
    static final String JWT_TOKEN_NOT_FOUND = "/jwt-token-not-found";
    static final String REGISTRATION_PROVIDER_MISMATCH = "/registration-provider-mismatch";
    static final String BAD_CREDENTIALS = "/bad-credentials";
    static final String USER_LOGGED_IN_USING_GOOGLE = "/user-logged-in-using-google";
    static final String EXCHANGE_SHORT_LIVED_TOKEN = "/exchange-short-lived-token";
    static final String USER_REGISTERED_USING_GOOGLE = "/user-registered-using-google";
    static final String FAILURE_TARGET_URL = "/failure-target-url";
}
