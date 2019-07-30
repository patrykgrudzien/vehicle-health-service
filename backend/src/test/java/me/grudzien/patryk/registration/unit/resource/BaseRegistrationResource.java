package me.grudzien.patryk.registration.unit.resource;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.ObjectMapperEncoder;
import me.grudzien.patryk.configuration.i18n.LocaleMessagesConfig;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions;
import me.grudzien.patryk.registration.service.event.RegistrationEventPublisher;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;
import me.grudzien.patryk.utils.validation.ValidationService;

import static lombok.AccessLevel.NONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;

@Import({
    ValidationService.class,
    LocaleMessagesHelper.class,
    LocaleMessagesCreator.class,
    LocaleMessagesConfig.class
})
@MockBeans({
	@MockBean(CustomUIMessageCodesProperties.class)
})
@NoArgsConstructor(access = NONE)
abstract class BaseRegistrationResource extends ObjectMapperEncoder {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    private RegistrationEventPublisher registrationEventPublisher;
    @MockBean
    private UserRegistrationDtoMapper registrationDtoMapper;

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Snow";
    private static final String TEST_EMAIL = "admin.root@gmail.com";
    private static final String TEST_PASSWORD = "admin";

    /**
     * URI(s) for {@link RegistrationResourceDefinitions}
     */
    static final String REGISTRATION_CREATE_USER_ACCOUNT_URI =
            fromPath(REGISTRATION_RESOURCE_ROOT).path(CREATE_USER_ACCOUNT).toUriString();

    static RequestBuilder createUserAccountRequestBuilder(final String jsonContent) {
        return MockMvcRequestBuilders.post(REGISTRATION_CREATE_USER_ACCOUNT_URI)
                .accept(APPLICATION_JSON)
                .content(jsonContent)
                .contentType(APPLICATION_JSON);
    }

    static UserRegistrationDtoWithEncoding createUserRegistrationDto() {
        return BaseRegistrationResource::buildRegistrationDto;
    }

    @FunctionalInterface
    interface UserRegistrationDtoWithEncoding {
        UserRegistrationDto doEncoding(boolean doEncoding);
    }

    static RegistrationResponse createSuccessfulRegistrationResponse(final String message) {
        return RegistrationResponse.Builder()
                                   .isSuccessful(true)
                                   .registeredUser(new CustomUser())
                                   .message(message)
                                   .build();
    }

    void setupRegistrationMapperToReturn(final UserRegistrationDto decodedRegistrationDto) {
        given(registrationDtoMapper.toDecodedUserRegistrationDto(any(UserRegistrationDto.class))).willReturn(decodedRegistrationDto);
    }

    void verifyRegistrationMapper() {
        verify(registrationDtoMapper).toDecodedUserRegistrationDto(any(UserRegistrationDto.class));
    }

    void setupRegistrationPublisherToReturn(final RegistrationResponse expectedResponse) {
        given(registrationEventPublisher.publishRegistrationEven(any(CustomUser.class), any(WebRequest.class))).willReturn(expectedResponse);
    }

    void verifyRegistrationPublisher() {
        verify(registrationEventPublisher).publishRegistrationEven(any(CustomUser.class), any(WebRequest.class));
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
