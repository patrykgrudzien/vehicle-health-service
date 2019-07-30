package me.grudzien.patryk.registration.unit.resource;

import static lombok.AccessLevel.NONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import org.mockito.verification.VerificationMode;

import me.grudzien.patryk.DefaultResourceConfiguration;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;
import me.grudzien.patryk.registration.AbstractRegistrationResourceHelper;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.service.event.RegistrationEventPublisher;

@Import(DefaultResourceConfiguration.class)
@MockBean(CustomUIMessageCodesProperties.class)
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
}
