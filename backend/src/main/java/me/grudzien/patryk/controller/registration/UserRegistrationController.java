package me.grudzien.patryk.controller.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import java.net.URI;

import me.grudzien.patryk.domain.dto.registration.RegistrationResponse;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.service.registration.UserRegistrationService;
import me.grudzien.patryk.service.registration.event.RegistrationEventPublisher;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.api-context-path}" + "${custom.properties.endpoints.registration.root}")
public class UserRegistrationController {

	private final UserRegistrationService userRegistrationService;
	private final RegistrationEventPublisher registrationEventPublisher;

	@Autowired
	public UserRegistrationController(final UserRegistrationService userRegistrationService,
                                      final RegistrationEventPublisher registrationEventPublisher) {
        Preconditions.checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");
        Preconditions.checkNotNull(registrationEventPublisher, "registrationEventPublisher cannot be null!");

        this.userRegistrationService = userRegistrationService;
        this.registrationEventPublisher = registrationEventPublisher;
    }

	@PostMapping("${custom.properties.endpoints.registration.register-user-account}")
	public ResponseEntity<CustomResponse> registerUserAccount(@RequestBody final UserRegistrationDto userRegistrationDto, final WebRequest webRequest) {
        final RegistrationResponse registrationResponse = userRegistrationService.registerNewCustomUserAccount(userRegistrationDto);
        if (registrationResponse.isSuccessful()) {
            final RegistrationResponse emailSent = registrationEventPublisher.publishRegistrationEven(registrationResponse.getRegisteredUser(), webRequest);
            return emailSent.isSuccessful() ?
                    ResponseEntity.ok().body(new SuccessResponse(emailSent.getMessage())) :
                    ResponseEntity.badRequest().build();
        }
        return ResponseEntity.badRequest().body(new ExceptionResponse(registrationResponse.getMessage()));
	}

	@GetMapping("${custom.properties.endpoints.registration.confirm-registration}")
	public ResponseEntity<CustomResponse> confirmRegistration(@RequestParam("token") final String token,
                                                              @SuppressWarnings("unused") final WebRequest webRequest) {
        final RegistrationResponse registrationResponse = userRegistrationService.confirmRegistration(token);
		final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(registrationResponse.getRedirectionUrl()));
        final String responseMessage = registrationResponse.getMessage();
        return registrationResponse.isSuccessful() ?
                ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).body(new SuccessResponse(responseMessage)) :
                ResponseEntity.badRequest().body(new ExceptionResponse(responseMessage));
	}

	@GetMapping("${custom.properties.endpoints.registration.resend-email-verification-token}")
	public ResponseEntity<Void> resendEmailVerificationToken(@RequestParam("token") final String token,
	                                                         @SuppressWarnings("unused") final WebRequest webRequest) {
//		userRegistrationService.resendEmailVerificationToken(token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}