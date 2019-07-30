package me.grudzien.patryk.oauth2.integration.resource;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions;
import me.grudzien.patryk.utils.web.MvcPattern;

import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.BAD_CREDENTIALS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.CREDENTIALS_HAVE_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.EXCHANGE_SHORT_LIVED_TOKEN;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.FAILURE_TARGET_URL;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.GOOGLE_OAUTH2_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.JWT_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.REGISTRATION_PROVIDER_MISMATCH;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_ALREADY_EXISTS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_LOCKED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_IS_DISABLED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_REGISTERED_USING_GOOGLE;

@NoArgsConstructor(access = NONE)
abstract class BaseGoogleResource {

    /**
     * URI(s) for {@link GoogleResourceDefinitions}
     */
    static final String GOOGLE_OAUTH2_USER_NOT_FOUND_URI = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_NOT_FOUND);

    static final String GOOGLE_OAUTH2_USER_ACCOUNT_IS_LOCKED = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_IS_LOCKED);

    static final String GOOGLE_OAUTH2_USER_IS_DISABLED = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_IS_DISABLED);

    static final String GOOGLE_OAUTH2_USER_ACCOUNT_IS_EXPIRED = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_IS_EXPIRED);

    static final String GOOGLE_OAUTH2_USER_ACCOUNT_ALREADY_EXISTS = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_ACCOUNT_ALREADY_EXISTS);

    static final String GOOGLE_OAUTH2_CREDENTIALS_HAVE_EXPIRED = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, CREDENTIALS_HAVE_EXPIRED);

    static final String GOOGLE_OAUTH2_JWT_TOKEN_NOT_FOUND = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, JWT_TOKEN_NOT_FOUND);

    static final String GOOGLE_OAUTH2_REGISTRATION_PROVIDER_MISMATCH = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, REGISTRATION_PROVIDER_MISMATCH);

    static final String GOOGLE_OAUTH2_BAD_CREDENTIALS = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, BAD_CREDENTIALS);

    static final String GOOGLE_OAUTH2_EXCHANGE_SHORT_LIVED_TOKEN = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, EXCHANGE_SHORT_LIVED_TOKEN);

    static final String GOOGLE_OAUTH2_USER_REGISTERED_USING_GOOGLE = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, USER_REGISTERED_USING_GOOGLE);

    static final String GOOGLE_OAUTH2_FAILURE_TARGET_URL = MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, FAILURE_TARGET_URL);
}
