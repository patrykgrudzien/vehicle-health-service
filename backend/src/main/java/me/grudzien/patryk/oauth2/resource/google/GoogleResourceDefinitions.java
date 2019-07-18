package me.grudzien.patryk.oauth2.resource.google;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.utils.appplication.ApiVersion.API_VERSION;

@NoArgsConstructor(access = NONE)
public final class GoogleResourceDefinitions {

    private static final String GOOGLE_OAUTH2_RESOURCE = "/google-oauth2";

    public static final String GOOGLE_OAUTH2_RESOURCE_ROOT = API_VERSION + GOOGLE_OAUTH2_RESOURCE;
    public static final String USER_NOT_FOUND = "/user-not-found";
    public static final String USER_ACCOUNT_IS_LOCKED = "/user-account-is-locked";
    public static final String USER_IS_DISABLED = "/user-is-disabled";
    public static final String USER_ACCOUNT_IS_EXPIRED = "/user-account-is-expired";
    public static final String USER_ACCOUNT_ALREADY_EXISTS = "/user-account-already-exists";
    public static final String CREDENTIALS_HAVE_EXPIRED = "/credentials-have-expired";
    public static final String JWT_TOKEN_NOT_FOUND = "/jwt-token-not-found";
    public static final String REGISTRATION_PROVIDER_MISMATCH = "/registration-provider-mismatch";
    public static final String BAD_CREDENTIALS = "/bad-credentials";
    public static final String USER_LOGGED_IN_USING_GOOGLE = "/user-logged-in-using-google";
    public static final String EXCHANGE_SHORT_LIVED_TOKEN = "/exchange-short-lived-token";
    public static final String USER_REGISTERED_USING_GOOGLE = "/user-registered-using-google";
    public static final String FAILURE_TARGET_URL = "/failure-target-url";
}
