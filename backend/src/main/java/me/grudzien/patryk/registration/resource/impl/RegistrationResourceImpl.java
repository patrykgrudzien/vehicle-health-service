package me.grudzien.patryk.registration.resource.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.event.RegistrationEventPublisher;
import me.grudzien.patryk.registration.resource.RegistrationResource;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.utils.web.model.CustomResponse;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.utils.web.model.SuccessResponse;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
public class RegistrationResourceImpl implements RegistrationResource {

    private final UserRegistrationService userRegistrationService;
    private final RegistrationEventPublisher registrationEventPublisher;

    public RegistrationResourceImpl(final UserRegistrationService userRegistrationService,
                                    final RegistrationEventPublisher registrationEventPublisher) {
        checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");
        checkNotNull(registrationEventPublisher, "registrationEventPublisher cannot be null!");

        this.userRegistrationService = userRegistrationService;
        this.registrationEventPublisher = registrationEventPublisher;
    }

    @Override
    public ResponseEntity<CustomResponse> createUserAccount(final UserRegistrationDto userRegistrationDto, final WebRequest webRequest) {
        final RegistrationResponse registrationResponse = userRegistrationService.registerNewCustomUserAccount(userRegistrationDto);
        if (registrationResponse.isSuccessful()) {
            final RegistrationResponse emailSent = registrationEventPublisher.publishRegistrationEven(registrationResponse.getRegisteredUser(), webRequest);
            return emailSent.isSuccessful() ?
                    ok().body(new SuccessResponse(emailSent.getMessage())) : badRequest().build();
        }
        return badRequest().body(new ExceptionResponse(registrationResponse.getMessage()));
    }

    @Override
    public ResponseEntity<CustomResponse> confirmRegistration(final String token, final WebRequest webRequest) {
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(token);
		final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(registrationResponse.getRedirectionUrl()));
        final String responseMessage = registrationResponse.getMessage();
        return registrationResponse.isSuccessful() ?
                status(MOVED_PERMANENTLY).headers(httpHeaders).body(new SuccessResponse(responseMessage)) :
                badRequest().body(new ExceptionResponse(responseMessage));
    }

    @Override
    public ResponseEntity<Void> resendEmailVerificationToken(final String token, final WebRequest webRequest) {
        // userRegistrationService.resendEmailVerificationToken(token);
		return new ResponseEntity<>(HttpStatus.OK);
    }
}
