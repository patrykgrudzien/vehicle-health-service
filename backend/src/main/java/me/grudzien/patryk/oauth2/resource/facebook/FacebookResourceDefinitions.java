package me.grudzien.patryk.oauth2.resource.facebook;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = NONE)
final class FacebookResourceDefinitions {

    private static final String FACEBOOK_OAUTH2_RESOURCE = "/facebook-oauth2";

    static final String FACEBOOK_OAUTH2_RESOURCE_ROOT = API_VERSION + FACEBOOK_OAUTH2_RESOURCE;
}
