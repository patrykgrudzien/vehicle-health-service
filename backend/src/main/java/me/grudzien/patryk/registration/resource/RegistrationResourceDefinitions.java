package me.grudzien.patryk.registration.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
final class RegistrationResourceDefinitions {

    private static final String REGISTRATION_RESOURCE = "/registration";

    static final String REGISTRATION_RESOURCE_ROOT = API_VERSION + REGISTRATION_RESOURCE;
    static final String CREATE_USER_ACCOUNT = "/create-user-account";
    static final String CONFIRM_REGISTRATION = "/confirm";
    static final String RESEND_EMAIL_VERIFICATION_TOKEN = "/resend-verification-token?token=";
}
