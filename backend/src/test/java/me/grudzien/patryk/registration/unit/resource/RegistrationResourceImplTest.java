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
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = RegistrationResourceImpl.class,
    secure = false
)
class RegistrationResourceImplTest extends BaseRegistrationResource {

    @MockBean
    private UserRegistrationService userRegistrationService;

    @Test
    @DisplayName("Registration successful. Response status: 200 OK (using MockMvc).")
	void shouldReturn200onRegisterUserAccountSuccessfullyMockMvc() throws Exception {
		// given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        final RegistrationResponse expectedResponse = createSuccessfulRegistrationResponse("Registration success!");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(expectedResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(expectedResponse);

        // when - then
        mockMvc.perform(createUserAccountRequestBuilder(tryConvertObjectToJson(decodedRegistrationDto)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message", is("Registration success!")));
        verifyRegistrationMapper();
        verifyRegistrationPublisher();
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
	}

    @Test
    @DisplayName("Registration successful. Response status: 200 OK (using RestAssuredMockMvc).")
    void shouldReturn200onRegisterUserAccountSuccessfullyRestAssured() {
        // given
        final UserRegistrationDto decodedRegistrationDto = createUserRegistrationDto().doEncoding(false);
        final RegistrationResponse expectedResponse = createSuccessfulRegistrationResponse("Registration success!");

        setupRegistrationMapperToReturn(decodedRegistrationDto);
        setupRegistrationPublisherToReturn(expectedResponse);
        given(userRegistrationService.createUserAccount(any(UserRegistrationDto.class))).willReturn(expectedResponse);

        // when - then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().all()
                          .body(tryConvertObjectToJson(decodedRegistrationDto))
                          .contentType(JSON)
                          .accept(JSON)
                          .when()
                          .post(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                          .then()
                          .log().all()
                          .statusCode(OK.value())
                          .body("message", is("Registration success!"));
        verifyRegistrationMapper();
        verifyRegistrationPublisher();
        verify(userRegistrationService).createUserAccount(any(UserRegistrationDto.class));
    }
}
