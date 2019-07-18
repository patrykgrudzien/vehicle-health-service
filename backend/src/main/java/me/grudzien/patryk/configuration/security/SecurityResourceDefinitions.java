package me.grudzien.patryk.configuration.security;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
final class SecurityResourceDefinitions {

    /**
     * Allows calls for request methods of "OPTIONS" type -> (CORS purpose) without checking JWT token.
     * This helps to avoid duplicate calls before the specific ones.
     */
    static final String APP_ROOT_CONTEXT = "/**";

    static final String[] STATIC_RESOURCES = {
            "/assets/**",
            "/index.html",
            "/favicon.ico",
            "/static/**",
            "/static/css/**",
            "/static/img/**",
            "/static/js/**",
            "/public**"
    };
}
