package me.grudzien.patryk.registration.resource.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;
import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.resource.RegistrationResource;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.registration.service.event.RegistrationEventPublisher;
import me.grudzien.patryk.utils.validation.ValidationService;
import me.grudzien.patryk.utils.web.model.CustomResponse;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.utils.web.model.SuccessResponse;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import static me.grudzien.patryk.utils.common.Predicates.isNullOrEmpty;
import static me.grudzien.patryk.utils.web.ObjectDecoder.userRegistrationDtoDecoder;

@Slf4j
@RestController
public class RegistrationResourceImpl implements RegistrationResource {

    private final CustomUIMessageCodesProperties uiMessageCodesProperties;
    private final RegistrationEventPublisher registrationEventPublisher;
    private final UserRegistrationDtoMapper registrationDtoMapper;
    private final UserRegistrationService userRegistrationService;
    private final ValidationService validationService;

    public RegistrationResourceImpl(final CustomUIMessageCodesProperties uiMessageCodesProperties,
                                    final RegistrationEventPublisher registrationEventPublisher,
                                    final UserRegistrationDtoMapper registrationDtoMapper,
                                    final UserRegistrationService userRegistrationService,
                                    final ValidationService validationService) {
        checkNotNull(uiMessageCodesProperties, "uiMessageCodesProperties cannot be null!");
        checkNotNull(registrationEventPublisher, "registrationEventPublisher cannot be null!");
        checkNotNull(registrationDtoMapper, "registrationDtoMapper cannot be null!");
        checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");
        checkNotNull(validationService, "validationService cannot be null!");

        this.uiMessageCodesProperties = uiMessageCodesProperties;
        this.registrationEventPublisher = registrationEventPublisher;
        this.registrationDtoMapper = registrationDtoMapper;
        this.userRegistrationService = userRegistrationService;
        this.validationService = validationService;
    }

    @Override
    public ResponseEntity<CustomResponse> createUserAccount(final UserRegistrationDto registrationDto, final WebRequest webRequest) {
        final UserRegistrationDto decodedRegistrationDto = userRegistrationDtoDecoder().apply(registrationDto, registrationDtoMapper);
        validationService.validateWithResult(decodedRegistrationDto)
                         .onErrorsSetExceptionMessageCode(uiMessageCodesProperties.registrationFormErrors());
        final RegistrationResponse response = userRegistrationService.createUserAccount(decodedRegistrationDto);
        if (response.isSuccessful()) {
        	if (decodedRegistrationDto.isHasFakeEmail()) {
        		return ok(new SuccessResponse(response.getMessage()));
	        } else {
		        final RegistrationResponse emailSent = registrationEventPublisher.publishRegistrationEven(response.getRegisteredUser(), webRequest);
		        return emailSent.isSuccessful() ? ok(new SuccessResponse(emailSent.getMessage())) : badRequest().build();
	        }
        }
        return badRequest().body(new ExceptionResponse(response.getMessage()));
    }

    @Override
    public ResponseEntity<CustomResponse> confirmRegistration(final String token, final WebRequest webRequest) {
        if (isNullOrEmpty(token)) {
            return badRequest().body(new ExceptionResponse(uiMessageCodesProperties.emailVerificationTokenEmpty()));
        }
        final RegistrationResponse response = userRegistrationService.confirmRegistration(token);
        final String responseMessage = response.getMessage();
        if (response.isSuccessful()) {
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create(response.getRedirectionUrl()));
            return status(MOVED_PERMANENTLY).headers(httpHeaders).body(new SuccessResponse(responseMessage));
        }
        return badRequest().body(new ExceptionResponse(responseMessage));
    }

    @Override
    public ResponseEntity<Void> resendEmailVerificationToken(final String token, final WebRequest webRequest) {
        // userRegistrationService.resendEmailVerificationToken(token);
		return new ResponseEntity<>(HttpStatus.OK);
    }
}
