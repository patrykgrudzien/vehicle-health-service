package me.grudzien.patryk.registration;

import static lombok.AccessLevel.NONE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import lombok.NoArgsConstructor;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.Function;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;

import me.grudzien.patryk.ObjectMapperEncoder;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions;

@NoArgsConstructor(access = NONE)
public abstract class AbstractRegistrationResourceHelper extends ObjectMapperEncoder {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Snow";

    protected static final String EMPTY_FIRST_NAME = EMPTY;
    protected static final String EMPTY_LAST_NAME = EMPTY;

    protected static final String EMPTY_EMAIL = EMPTY;
    protected static final String TEST_EMAIL = "admin.root@gmail.com";
	protected static final String NO_EXISTING_EMAIL = "bad-email@gmail.com";
	protected static final String NO_EXISTING_EMAIL_1 = "bad-email-1@gmail.com";
	protected static final String NO_EXISTING_EMAIL_2 = "bad-email-2@gmail.com";

	protected static final String TEST_PASSWORD = "admin";
	protected static final String EMPTY_PASSWORD = EMPTY;

	protected static final String HTTP_LOCALHOST = "http://localhost";

    /**
     * URI(s) for {@link RegistrationResourceDefinitions}
     */
    protected static final String REGISTRATION_CREATE_USER_ACCOUNT_URI =
            fromPath(REGISTRATION_RESOURCE_ROOT).path(CREATE_USER_ACCOUNT).toUriString();

    protected static final Function<String, String> REGISTRATION_CONFIRM_REGISTRATION_URI =
            token -> REGISTRATION_RESOURCE_ROOT + CONFIRM_REGISTRATION + "?token=" + token;

    protected static RequestBuilder createUserAccountPostRequestBuilder(final String jsonContent) {
        return MockMvcRequestBuilders.post(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                                     .accept(APPLICATION_JSON)
                                     .content(jsonContent)
                                     .contentType(APPLICATION_JSON);
    }

	protected static UserRegistrationDtoWithEncoding createUserRegistrationDto() {
		return doEncoding -> buildRegistrationDto(FIRST_NAME, LAST_NAME, TEST_EMAIL, TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, doEncoding);
	}

    protected static UserRegistrationDtoJsonWithEncoding createUserRegistrationDtoJson(final String email,
                                                                                       final String password) {
    	return doEncoding -> createUserRegistrationDtoJson(email, email, password).doEncoding(doEncoding);
    }

	protected static UserRegistrationDtoJsonWithEncoding createUserRegistrationDtoJson(final String email, final String confirmedEmail,
	                                                                                   final String password) {
    	return doEncoding -> createUserRegistrationDtoJson(email, confirmedEmail, password, password).doEncoding(doEncoding);
	}

	protected static UserRegistrationDtoJsonWithEncoding createUserRegistrationDtoJson(final String email, final String confirmedEmail,
	                                                                                   final String password, final String confirmedPassword) {
		return doEncoding -> createUserRegistrationDtoJson(FIRST_NAME, LAST_NAME, email, confirmedEmail, password, confirmedPassword).doEncoding(doEncoding);
	}

	protected static UserRegistrationDtoJsonWithEncoding createUserRegistrationDtoJson(final String firstName, final String lastName,
	                                                                                   final String email, final String confirmedEmail,
	                                                                                   final String password, final String confirmedPassword) {
		return doEncoding -> tryConvertObjectToJson(
				buildRegistrationDto(firstName, lastName, email, confirmedEmail, password, confirmedPassword, doEncoding));
	}

    @FunctionalInterface
    protected interface UserRegistrationDtoJsonWithEncoding {
    	String doEncoding(boolean doEncoding);
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

    private static UserRegistrationDto buildRegistrationDto(final String firstName, final String lastName,
                                                            final String email, final String confirmedEmail,
                                                            final String password, final String confirmedPassword,
                                                            final boolean doEncoding) {
        return UserRegistrationDto.Builder()
                                  .firstName(doEncoding ? encodeNotNullValue(firstName) : firstName)
                                  .lastName(doEncoding ? encodeNotNullValue(lastName) : lastName)
                                  .email(doEncoding ? encodeNotNullValue(email) : email)
                                  .confirmedEmail(doEncoding ? encodeNotNullValue(confirmedEmail) : confirmedEmail)
                                  .hasFakeEmail(true)
                                  .password(doEncoding ? encodeNotNullValue(password) : password)
                                  .confirmedPassword(doEncoding ? encodeNotNullValue(confirmedPassword) : confirmedPassword)
                                  .build();
    }
}
