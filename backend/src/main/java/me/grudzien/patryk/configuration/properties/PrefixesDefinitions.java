package me.grudzien.patryk.configuration.properties;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class PrefixesDefinitions {

    private static final String CUSTOM_PROPERTIES_BASE_PREFIX = "custom.properties.";

    public static final String CUSTOM_JWT_PROPERTIES_PREFIX = CUSTOM_PROPERTIES_BASE_PREFIX + "jwt";
    public static final String CUSTOM_OAUTH2_PROPERTIES_PREFIX = CUSTOM_PROPERTIES_BASE_PREFIX + "oauth2";
    public static final String CUSTOM_CORS_PROPERTIES_PREFIX = CUSTOM_PROPERTIES_BASE_PREFIX + "cors";
    public static final String CUSTOM_HEROKU_PROPERTIES_PREFIX = CUSTOM_PROPERTIES_BASE_PREFIX + "heroku";
}
