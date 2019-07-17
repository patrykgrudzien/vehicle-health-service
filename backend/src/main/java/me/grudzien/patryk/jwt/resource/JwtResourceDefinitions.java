package me.grudzien.patryk.jwt.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
final class JwtResourceDefinitions {

    private static final String JWT_RESOURCE = "/jwt";

    static final String JWT_RESOURCE_ROOT = API_VERSION + JWT_RESOURCE;
    static final String GENERATE_ACCESS_TOKEN = "/generate-access-token";
    static final String GENERATE_REFRESH_TOKEN = "/generate-refresh-token";
    static final String REFRESH_ACCESS_TOKEN = "/refresh-access-token";
}
