package me.grudzien.patryk.registration;

import lombok.NoArgsConstructor;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.Function;

import me.grudzien.patryk.ObjectMapperEncoder;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions;

import static lombok.AccessLevel.NONE;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;

@NoArgsConstructor(access = NONE)
public abstract class AbstractRegistrationResourceHelper extends ObjectMapperEncoder {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Snow";
    private static final String TEST_EMAIL = "admin.root@gmail.com";
    private static final String TEST_PASSWORD = "admin";

    protected static final String HTTP_LOCALHOST = "http://localhost";

    /**
     * URI(s) for {@link RegistrationResourceDefinitions}
     */
    protected static final String REGISTRATION_CREATE_USER_ACCOUNT_URI =
            fromPath(REGISTRATION_RESOURCE_ROOT).path(CREATE_USER_ACCOUNT).toUriString();

    protected static final Function<String, String> REGISTRATION_CONFIRM_REGISTRATION_URI =
            token -> REGISTRATION_RESOURCE_ROOT + CONFIRM_REGISTRATION + "?token=" + token;

    protected static RequestBuilder createUserAccountRequestBuilder(final String jsonContent) {
        return MockMvcRequestBuilders.post(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                                     .accept(APPLICATION_JSON)
                                     .content(jsonContent)
                                     .contentType(APPLICATION_JSON);
    }

    protected static UserRegistrationDtoWithEncoding createUserRegistrationDto() {
        return AbstractRegistrationResourceHelper::buildRegistrationDto;
    }

    @FunctionalInterface
    protected interface UserRegistrationDtoWithEncoding {
        UserRegistrationDto doEncoding(boolean doEncoding);
    }

    protected static RegistrationResponse createSuccessfulRegistrationResponse(final String message) {
        return RegistrationResponse.Builder()
                                   .isSuccessful(true)
                                   .registeredUser(new CustomUser())
                                   .message(message)
                                   .build();
    }

    private static UserRegistrationDto buildRegistrationDto(final boolean doEncoding) {
        return UserRegistrationDto.Builder()
                                  .firstName(doEncoding ? encodeNotNullValue(FIRST_NAME) : FIRST_NAME)
                                  .lastName(doEncoding ? encodeNotNullValue(LAST_NAME) : LAST_NAME)
                                  .email(doEncoding ? encodeNotNullValue(TEST_EMAIL) : TEST_EMAIL)
                                  .confirmedEmail(doEncoding ? encodeNotNullValue(TEST_EMAIL) : TEST_EMAIL)
                                  .hasFakeEmail(true)
                                  .password(doEncoding ? encodeNotNullValue(TEST_PASSWORD) : TEST_PASSWORD)
                                  .confirmedPassword(doEncoding ? encodeNotNullValue(TEST_PASSWORD) : TEST_PASSWORD)
                                  .build();
    }
}
