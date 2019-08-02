package me.grudzien.patryk.registration.unit.resource;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import org.mockito.verification.VerificationMode;

import me.grudzien.patryk.DefaultResourceConfiguration;
import me.grudzien.patryk.registration.AbstractRegistrationResourceHelper;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.service.event.RegistrationEventPublisher;

import static io.restassured.http.ContentType.JSON;
import static lombok.AccessLevel.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Import(DefaultResourceConfiguration.class)
@NoArgsConstructor(access = NONE)
abstract class BaseRegistrationResource extends AbstractRegistrationResourceHelper {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    private RegistrationEventPublisher registrationEventPublisher;
    @MockBean
    private UserRegistrationDtoMapper registrationDtoMapper;

    void setupRegistrationMapperToReturn(final UserRegistrationDto decodedRegistrationDto) {
        given(registrationDtoMapper.toDecodedUserRegistrationDto(any(UserRegistrationDto.class))).willReturn(decodedRegistrationDto);
    }

    void verifyRegistrationMapper() {
        verify(registrationDtoMapper).toDecodedUserRegistrationDto(any(UserRegistrationDto.class));
    }

    void setupRegistrationPublisherToReturn(final RegistrationResponse expectedResponse) {
        given(registrationEventPublisher.publishRegistrationEven(any(CustomUser.class), any(WebRequest.class))).willReturn(expectedResponse);
    }

    void verifyRegistrationPublisher(final VerificationMode verificationMode) {
        verify(registrationEventPublisher, verificationMode).publishRegistrationEven(any(CustomUser.class), any(WebRequest.class));
    }

    /**
     * RestAssured helpers
     */
    JsonBody restAssuredPostInvoker() {
        return jsonBody -> uriPath -> expectedStatusCode -> createRestAssuredPostClient(jsonBody, uriPath, expectedStatusCode);
    }

    Request restAssuredGetInvoker() {
        return uriPath -> expectedStatusCode -> createRestAssuredGetClient(uriPath, expectedStatusCode);
    }

    @FunctionalInterface
    interface JsonBody {
        Request jsonBody(String jsonBody);
    }

    @FunctionalInterface
    interface Request {
        ExpectedStatusCode endpoint(String uriPath);
    }

    @FunctionalInterface
    interface ExpectedStatusCode {
        ValidatableMockMvcResponse expectedStatusCode(HttpStatus expectedStatusCode);
    }

    void assertResponseHasSize(final ValidatableMockMvcResponse response, final int expectedSize) {
        assertThat(response.extract().jsonPath().getMap("$", String.class, String.class)).hasSize(expectedSize);
    }

    private ValidatableMockMvcResponse createRestAssuredPostClient(final String jsonBody, final String uriPath,
                                                                   final HttpStatus expectedStatusCode) {
        return RestAssuredMockMvc.given()
                                 .webAppContextSetup(webApplicationContext)
                                 .log().body(true)
                                 .body(jsonBody)
                                 .contentType(JSON).accept(JSON)
                                 .when().post(uriPath)
                                 .then().log().body(true)
                                 .assertThat()
                                 .statusCode(expectedStatusCode.value());
    }

    private ValidatableMockMvcResponse createRestAssuredGetClient(final String uriPath,
                                                                  final HttpStatus expectedStatusCode) {
        return RestAssuredMockMvc.given()
                                 .webAppContextSetup(webApplicationContext)
                                 .log().body(true)
                                 .accept(JSON)
                                 .when().get(uriPath)
                                 .then().log().body(true)
                                 .assertThat()
                                 .statusCode(expectedStatusCode.value());
    }
}
