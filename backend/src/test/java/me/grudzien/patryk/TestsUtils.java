package me.grudzien.patryk;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entity.registration.Role;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.domain.enums.registration.RoleName;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.domain.factory.JwtUserFactory;

public final class TestsUtils {

    private static final Encoder encoder = Base64.getEncoder();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String TEST_EMAIL = "admin.root@gmail.com";
    public static final String NO_EXISTING_EMAIL = "bad-email@gmail.com";
    public static final String NO_EXISTING_EMAIL_1 = "bad-email-1@gmail.com";
    public static final String NO_EXISTING_EMAIL_2 = "bad-email-2@gmail.com";
    public static final String TEST_PASSWORD = "admin";

    public static final boolean ENABLE_ENCODING = true;
    public static final boolean DISABLE_ENCODING = false;

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

    public static JwtAuthenticationRequest prepareLoginRequest(final String email, final String password, final boolean doEncoding) {
        return JwtAuthenticationRequest.Builder()
                                       .email(doEncoding ? encodeNotNullValue(email) : email)
                                       .password(doEncoding ? encodeNotNullValue(password) : password)
                                       .build();
    }

	public static JwtAuthenticationRequest prepareAccessTokenRequest(final String email, final String refreshToken, final boolean doEncoding) {
		return JwtAuthenticationRequest.Builder()
		                               .email(doEncoding ? encodeNotNullValue(email) : email)
		                               .refreshToken(refreshToken)
		                               .build();
	}

    public static String prepareTestAccessToken(final JwtTokenService jwtTokenService) {
        return jwtTokenService.generateAccessToken(JwtAuthenticationRequest.Builder()
                                                                           .email(TEST_EMAIL)
                                                                           .password(TEST_PASSWORD)
                                                                           .build(), testDevice());
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

    public static String prepareRegistrationJSONBody(final String firstName, final String lastName, final String email, final String confirmedEmail,
                                                     final String password, final String confirmedPassword, final boolean doEncoding) throws JsonProcessingException {
        return objectMapper.writeValueAsString(UserRegistrationDto.Builder()
                                                                  .firstName(doEncoding ? encodeNotNullValue(firstName) : firstName)
                                                                  .lastName(doEncoding ? encodeNotNullValue(lastName) : lastName)
                                                                  .email(doEncoding ? encodeNotNullValue(email) : email)
                                                                  .confirmedEmail(doEncoding ? encodeNotNullValue(confirmedEmail) : confirmedEmail)
                                                                  .hasFakeEmail(true)
                                                                  .password(doEncoding ? encodeNotNullValue(password) : password)
                                                                  .confirmedPassword(doEncoding ? encodeNotNullValue(confirmedPassword) : confirmedPassword)
                                                                  .build());
    }

	public static UserRegistrationDto prepareUserRegistrationDto(final String firstName, final String lastName, final String email, final String password,
	                                                             final boolean doEncoding) {
    	return UserRegistrationDto.Builder()
	                              .firstName(doEncoding ? encodeNotNullValue(firstName) : firstName)
	                              .lastName(doEncoding ? encodeNotNullValue(lastName) : lastName)
	                              .email(doEncoding ? encodeNotNullValue(email) : email)
	                              .confirmedEmail(doEncoding ? encodeNotNullValue(email) : email)
	                              .hasFakeEmail(true)
	                              .password(doEncoding ? encodeNotNullValue(password) : password)
	                              .confirmedPassword(doEncoding ? encodeNotNullValue(password) : password)
	                              .build();
	}

    public static JwtUser prepareTestJwtUser() {
        return JwtUser.Builder()
                      .id(1L)
                      .firstname("John")
                      .lastname("Snow")
                      .email(TEST_EMAIL)
                      .password(TEST_PASSWORD)
                      .enabled(true)
                      .roles(JwtUserFactory.mapRolesToAuthorities(Sets.newHashSet(new Role(RoleName.ROLE_USER))))
                      .lastPasswordResetDate(ApplicationZone.POLAND.now().minusDays(7L))
                      .build();
    }

    public static JwtUser prepareTestJwtUser(final ZonedDateTime lastPasswordResetDate) {
        return JwtUser.Builder()
                      .id(1L)
                      .firstname("John")
                      .lastname("Snow")
                      .email(TEST_EMAIL)
                      .password(TEST_PASSWORD)
                      .enabled(true)
                      .roles(JwtUserFactory.mapRolesToAuthorities(Sets.newHashSet(new Role(RoleName.ROLE_USER))))
                      .lastPasswordResetDate(lastPasswordResetDate)
                      .build();
    }

    public static CustomOAuth2OidcPrincipalUser prepareCustomOAuth2OidcPrincipalUser() {
	    final Map<String, Object> attributes = Maps.newHashMap();
	    attributes.put(StandardClaimNames.EMAIL, TEST_EMAIL);
	    attributes.put(StandardClaimNames.PICTURE, "www.my-profile-photo.fakeUrl.com");

	    return CustomOAuth2OidcPrincipalUser.Builder(prepareTestJwtUser())
	                                        .attributes(attributes)
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
        return Optional.ofNullable(value)
                       .map(notEmptyValue -> encoder.encodeToString(notEmptyValue.getBytes()))
                       .orElse(null);
    }
}
