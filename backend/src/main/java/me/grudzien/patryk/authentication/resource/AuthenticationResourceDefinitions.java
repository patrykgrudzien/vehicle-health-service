package me.grudzien.patryk.authentication.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
final class AuthenticationResourceDefinitions {

    private static final String AUTHENTICATION_RESOURCE = "/authentication";

    static final String AUTHENTICATION_RESOURCE_ROOT = API_VERSION + AUTHENTICATION_RESOURCE;
    static final String LOGIN = "/login";
    static final String PRINCIPAL_USER = "/principal-user";
}
