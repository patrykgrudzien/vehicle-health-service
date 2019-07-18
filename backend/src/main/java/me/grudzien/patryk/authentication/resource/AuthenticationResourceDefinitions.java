package me.grudzien.patryk.authentication.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.appplication.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
public final class AuthenticationResourceDefinitions {

    private static final String AUTHENTICATION_RESOURCE = "/authentication";

    public static final String AUTHENTICATION_RESOURCE_ROOT = API_VERSION + AUTHENTICATION_RESOURCE;
    public static final String LOGIN = "/login";
    public static final String PRINCIPAL_USER = "/principal-user";
}
