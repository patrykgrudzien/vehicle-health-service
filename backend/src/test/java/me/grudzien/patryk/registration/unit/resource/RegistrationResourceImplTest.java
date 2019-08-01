package me.grudzien.patryk.registration.unit.resource;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.resource.impl.RegistrationResourceImpl;
import me.grudzien.patryk.registration.service.UserRegistrationService;

import static io.restassured.http.ContentType.JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static me.grudzien.patryk.registration.model.enums.RegistrationResponseType.FAILED;
import static me.grudzien.patryk.registration.model.enums.RegistrationResponseType.SUCCESSFUL;

@WebMvcTest(
    controllers = RegistrationResourceImpl.class,
    secure = false
)
class RegistrationResourceImplTest extends BaseRegistrationResource {

    @MockBean
    private UserRegistrationService userRegistrationService;

    // -- createUserAccount()

    @Test
    @DisplayName("Registration successful. Response status: 200 OK (using MockMvc).")
	void shouldReturn200onRegisterUserAccountUsingMockMvc() throws Exception {
		// given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        final RegistrationResponse successResponse = createRegistrationResponse(SUCCESSFUL, "Registration success.");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(successResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(successResponse);

        // when - then
        mockMvc.perform(createUserAccountPostRequestBuilder(tryConvertObjectToJson(decodedRegistrationDto)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasKey("message")))
               .andExpect(jsonPath("$.message", is("Registration success.")))
               .andExpect(jsonPath("$", not(hasKey("securityStatus"))))
               .andExpect(jsonPath("$", not(hasKey("accountStatus"))))
               .andExpect(jsonPath("$", not(hasKey("lastRequestedPath"))))
               .andExpect(jsonPath("$", not(hasKey("lastRequestMethod"))));
        verifyRegistrationMapper();
        verifyRegistrationPublisher(never());
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
	}

    @Test
    @DisplayName("Registration successful. Response status: 200 OK (using RestAssuredMockMvc).")
    void shouldReturn200onRegisterUserAccountUsingRestAssured() {
        // given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        final RegistrationResponse successResponse = createRegistrationResponse(SUCCESSFUL, "Registration success.");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(successResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(successResponse);

        // when - then
        restAssuredPostInvoker()
                .jsonBody(tryConvertObjectToJson(decodedRegistrationDto))
                .endpoint(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                .expectedStatusCode(OK)
                .body(notNullValue())
                .body("$", hasKey("message"))
                .body("message", is("Registration success."))
                .body("$", not(hasKey("securityStatus")))
                .body("$", not(hasKey("accountStatus")))
                .body("$", not(hasKey("lastRequestedPath")))
                .body("$", not(hasKey("lastRequestMethod")));
        verifyRegistrationMapper();
        verifyRegistrationPublisher(never());
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Registration successful. Response status: 200 OK - Email successfully sent to the real address.")
    void shouldReturn200onRegisterUserAccountWithEmailSentToRealAddress() {
        // given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        decodedRegistrationDto.setHasFakeEmail(false);
        final RegistrationResponse successResponse = createRegistrationResponse(SUCCESSFUL, "Registration success.");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(successResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(successResponse);

        // when - then
        restAssuredPostInvoker()
                .jsonBody(tryConvertObjectToJson(decodedRegistrationDto))
                .endpoint(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                .expectedStatusCode(OK)
                .body(notNullValue())
                .body("$", hasKey("message"))
                .body("message", is("Registration success."))
                .body("$", not(hasKey("securityStatus")))
                .body("$", not(hasKey("accountStatus")))
                .body("$", not(hasKey("lastRequestedPath")))
                .body("$", not(hasKey("lastRequestMethod")));
        verifyRegistrationMapper();
        verifyRegistrationPublisher(times(1));
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Registration failed. Response status: 400 Bad Request - Email not sent to the real address!")
    void shouldReturn400onRegisterUserAccountWhenEmailNotSentToRealAddress() {
        // given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        decodedRegistrationDto.setHasFakeEmail(false);
        final RegistrationResponse successResponse = createRegistrationResponse(SUCCESSFUL, "Registration success.");
        final RegistrationResponse failedResponse = createRegistrationResponse(FAILED, "Registration failed!");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(failedResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(successResponse);

        // when - then
        restAssuredPostInvoker()
                .jsonBody(tryConvertObjectToJson(decodedRegistrationDto))
                .endpoint(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                .expectedStatusCode(BAD_REQUEST)
                .body(isEmptyString());
        verifyRegistrationMapper();
        verifyRegistrationPublisher(times(1));
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Registration failed. Response status: 400 Bad Request - User email already exists!")
    void shouldReturn400onRegisterUserAccountWhenEmailAlreadyExists() {
        // given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto(EMAIL_ALREADY_EXISTS).doEncoding(false);
        final RegistrationResponse failedResponse = createRegistrationResponse(FAILED, "Registration failed!");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(failedResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(failedResponse);

        // when - then
        restAssuredPostInvoker()
                .jsonBody(tryConvertObjectToJson(decodedRegistrationDto))
                .endpoint(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                .expectedStatusCode(BAD_REQUEST)
                .body(notNullValue())
                .body("$", hasKey("message"))
                .body("message", is("Registration failed!"))
                .body("$", not(hasKey("securityStatus")))
                .body("$", not(hasKey("accountStatus")))
                .body("$", not(hasKey("lastRequestedPath")))
                .body("$", not(hasKey("lastRequestMethod")));
        verifyRegistrationMapper();
        verifyRegistrationPublisher(never());
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
    }

    // -- confirmRegistration()

    @Test
    @DisplayName("Confirm registration failed. Response status: 400 Bad Request - Email Verification Token is empty!")
    void shouldReturn400onConfirmRegistrationWhenTokenIsEmpty() {
        // when - then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().body(true)
                          .accept(JSON)
                          .when()
                          .get(REGISTRATION_CONFIRM_REGISTRATION_URI.apply(EMPTY))
                          .then()
                          .log().body(true)
                          .statusCode(BAD_REQUEST.value())
                          .body(notNullValue());
    }

    @Test
    @DisplayName("Confirm registration failed. Response status: 400 Bad Request - Email Verification Token is null!")
    void shouldReturn400onConfirmRegistrationWhenTokenIsNull() {
        // when - then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().body(true)
                          .accept(JSON)
                          .when()
                          .get(REGISTRATION_CONFIRM_REGISTRATION_URI.apply(null))
                          .then()
                          .log().body(true)
                          .statusCode(BAD_REQUEST.value())
                          .body(notNullValue());
    }
}
