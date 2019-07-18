package me.grudzien.patryk.integration.registration.resource;

import lombok.NoArgsConstructor;

import java.util.function.Function;

import me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions;
import me.grudzien.patryk.utils.web.MvcPattern;

import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;

@NoArgsConstructor(access = NONE)
abstract class BaseRegistrationResource {

    /**
     * URI(s) for {@link RegistrationResourceDefinitions}
     */
    static final String REGISTRATION_CREATE_USER_ACCOUNT_URI = MvcPattern.of(REGISTRATION_RESOURCE_ROOT, CREATE_USER_ACCOUNT);

    static final Function<String, String> REGISTRATION_CONFIRM_REGISTRATION_URI =
            token -> REGISTRATION_RESOURCE_ROOT + CONFIRM_REGISTRATION + "?token=" + token;
}
