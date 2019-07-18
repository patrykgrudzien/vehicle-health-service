package me.grudzien.patryk.jwt.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
public final class JwtResourceDefinitions {

    private static final String JWT_RESOURCE = "/jwt";

    public static final String JWT_RESOURCE_ROOT = API_VERSION + JWT_RESOURCE;
    public static final String GENERATE_ACCESS_TOKEN = "/generate-access-token";
    public static final String GENERATE_REFRESH_TOKEN = "/generate-refresh-token";
    public static final String REFRESH_ACCESS_TOKEN = "/refresh-access-token";
}
