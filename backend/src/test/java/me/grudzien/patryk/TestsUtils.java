package me.grudzien.patryk;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entity.registration.Role;
import me.grudzien.patryk.domain.enums.registration.RoleName;
import me.grudzien.patryk.util.jwt.JwtUserFactory;

public final class TestsUtils {

    private static final Encoder encoder = Base64.getEncoder();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String TEST_EMAIL = "admin.root@gmail.com";
    public static final String TEST_PASSWORD = "admin";

    private TestsUtils() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public static String prepareAuthJSONBody(final String email, final String password, final boolean doEncoding) throws JsonProcessingException {
        return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(doEncoding ? encodeNotNullValue(email) : email)
                                                                       .password(doEncoding ? encodeNotNullValue(password) : password)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }

    public static JwtAuthenticationRequest prepareAccessTokenRequest(final String email, final boolean doEncoding) {
        return JwtAuthenticationRequest.Builder()
                                       .email(doEncoding ? encodeNotNullValue(email) : email)
                                       .build();
    }

    public static String prepareJwtTokenControllerJSONBody(final String email, final String password, final boolean doEncoding) throws JsonProcessingException {
        return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(doEncoding ? encodeNotNullValue(email) : email)
                                                                       .password(doEncoding ? encodeNotNullValue(password) : password)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }

    public static String prepareVehicleJSONBody(final String mileageToEncode) throws JsonProcessingException {
        return objectMapper.writeValueAsString(VehicleDto.Builder()
                                                         .encodedMileage(encodeNotNullValue(mileageToEncode))
                                                         .build());
    }

    public static String prepareRegistrationJSONBody(final String firstName, final String lastName, final String email, final String password,
                                                     final boolean doEncoding) throws JsonProcessingException {
        return objectMapper.writeValueAsString(UserRegistrationDto.Builder()
                                                                  .firstName(doEncoding ? encodeNotNullValue(firstName) : firstName)
                                                                  .lastName(doEncoding ? encodeNotNullValue(lastName) : lastName)
                                                                  .email(doEncoding ? encodeNotNullValue(email) : email)
                                                                  .confirmedEmail(doEncoding ? encodeNotNullValue(email) : email)
                                                                  .hasFakeEmail(true)
                                                                  .password(doEncoding ? encodeNotNullValue(password) : password)
                                                                  .confirmedPassword(doEncoding ? encodeNotNullValue(password) : password)
                                                                  .build());
    }

    public static JwtUser prepareTestJwtUser() {
        return JwtUser.Builder()
                      .id(1L)
                      .firstname("John")
                      .lastname("Snow")
                      .password("password")
                      .email("test@email.com")
                      .enabled(true)
                      .roles(JwtUserFactory.mapRolesToAuthorities(Sets.newHashSet(new Role(RoleName.ROLE_USER))))
                      .build();
    }

    public static Device testDevice() {
        return new Device() {
            @Override
            public boolean isNormal() {
                return true;
            }

            @Override
            public boolean isMobile() {
                return false;
            }

            @Override
            public boolean isTablet() {
                return false;
            }

            @Override
            public DevicePlatform getDevicePlatform() {
                return DevicePlatform.UNKNOWN;
            }
        };
    }

    private static String encodeNotNullValue(final String value) {
        return Optional.ofNullable(value).map(notEmptyValue -> encoder.encodeToString(notEmptyValue.getBytes())).orElse(null);
    }
}
