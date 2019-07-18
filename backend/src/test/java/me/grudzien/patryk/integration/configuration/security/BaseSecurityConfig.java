package me.grudzien.patryk.integration.configuration.security;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions;
import me.grudzien.patryk.jwt.resource.JwtResourceDefinitions;
import me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions;
import me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions;

import static lombok.AccessLevel.NONE;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.PRINCIPAL_USER;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.GENERATE_ACCESS_TOKEN;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.JWT_RESOURCE_ROOT;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.REFRESH_ACCESS_TOKEN;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.CURRENT_MILEAGE;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.OWNER_EMAIL_ADDRESS;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.UPDATE_CURRENT_MILEAGE;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.VEHICLE_RESOURCE_ROOT;

@NoArgsConstructor(access = NONE)
abstract class BaseSecurityConfig {

    private static final String TEST_EMAIL = "test@email.com";

    /**
     * URI(s) for {@link AuthenticationResourceDefinitions}
     */
    static final String AUTHENTICATION_LOGIN_URI =
            fromPath(AUTHENTICATION_RESOURCE_ROOT).path(LOGIN).toUriString();

    static final String AUTHENTICATION_PRINCIPAL_USER_URI =
            fromPath(AUTHENTICATION_RESOURCE_ROOT).path(PRINCIPAL_USER).toUriString();

    /**
     * URI(s) for {@link VehicleResourceDefinitions}
     */
    static final String VEHICLE_GET_BY_OWNER_EMAIL_ADDRESS_URI =
            fromPath(VEHICLE_RESOURCE_ROOT).path(OWNER_EMAIL_ADDRESS).buildAndExpand(TEST_EMAIL).toUriString();

    static final String VEHICLE_GET_CURRENT_MILEAGE_URI =
            fromPath(VEHICLE_RESOURCE_ROOT).path(CURRENT_MILEAGE).buildAndExpand(TEST_EMAIL).toUriString();

    static final String VEHICLE_UPDATE_CURRENT_MILEAGE_URI =
            fromPath(VEHICLE_RESOURCE_ROOT).path(UPDATE_CURRENT_MILEAGE).buildAndExpand(TEST_EMAIL).toUriString();

    /**
     * URI(s) for {@link RegistrationResourceDefinitions}
     */
    static final String REGISTRATION_CREATE_USER_ACCOUNT_URI =
            fromPath(REGISTRATION_RESOURCE_ROOT).path(CREATE_USER_ACCOUNT).toUriString();

    /**
     * URI(s) for {@link JwtResourceDefinitions}
     */
    static final String JWT_GENERATE_ACCESS_TOKEN_URI =
            fromPath(JWT_RESOURCE_ROOT).path(GENERATE_ACCESS_TOKEN).toUriString();

    static final String JWT_GENERATE_REFRESH_TOKEN_URI =
            fromPath(JWT_RESOURCE_ROOT).path(GENERATE_ACCESS_TOKEN).toUriString();

    static final String JWT_REFRESH_ACCESS_TOKEN_URI =
            fromPath(JWT_RESOURCE_ROOT).path(REFRESH_ACCESS_TOKEN).toUriString();
}
