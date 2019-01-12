package me.grudzien.patryk.unit.controller.registration;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import me.grudzien.patryk.controller.registration.UserRegistrationController;
import me.grudzien.patryk.domain.dto.registration.RegistrationResponse;
import me.grudzien.patryk.service.registration.UserRegistrationService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;
import static me.grudzien.patryk.TestsUtils.prepareRegistrationJSONBody;

@WebMvcTest(controllers = UserRegistrationController.class, secure = false)
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@SuppressWarnings("unused")
    @MockBean
    private LocaleMessagesCreator localeMessagesCreator;

    @MockBean
	private UserRegistrationService userRegistrationService;

    private String registrationJSONBody;

    private static final String REGISTRATION_ENDPOINT = "/api/registration/register-user-account";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        registrationJSONBody = prepareRegistrationJSONBody("John", "Snow", TEST_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);
    }

    @AfterEach
    void tearDown() {
        registrationJSONBody = null;
    }

    @Test
    @DisplayName("Register user account - successful using MockMvc.")
	void testRegisterUserAccountSuccessfully() throws Exception {
		// given
        final RegistrationResponse expectedResponse = RegistrationResponse.Builder()
                                                                          .message("Registration success!")
                                                                          .isSuccessful(true)
                                                                          .build();
        // when
        when(userRegistrationService.registerNewCustomUserAccount(any(), any())).thenReturn(expectedResponse);

        // then
        mockMvc.perform(
                post(REGISTRATION_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJSONBody))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Registration success!")));
	}

    @Test
    @DisplayName("Register user account - successful using RestAssuredMockMvc.")
    void testRegisterUserAccountSuccessfully_usingRestAssured() {
        // given
        final RegistrationResponse expectedResponse = RegistrationResponse.Builder()
                                                                          .message("Registration success!")
                                                                          .isSuccessful(true)
                                                                          .build();
        // when
        when(userRegistrationService.registerNewCustomUserAccount(any(), any())).thenReturn(expectedResponse);

        // then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().all()
                          .body(registrationJSONBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post(REGISTRATION_ENDPOINT)
                          .then()
                          .log().all()
                          .statusCode(HttpStatus.OK.value())
                          .body("message", Matchers.is("Registration success!"));
    }
}
