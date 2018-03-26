package me.grudzien.patryk.controller.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.service.registration.CustomUserService;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.registration.root}")
public class UserRegistrationController {

	private final CustomUserService customUserService;
	private final CustomApplicationProperties customApplicationProperties;
	private final HttpResponseHandler httpResponseHandler;

	@Autowired
	public UserRegistrationController(final CustomUserService customUserService, final CustomApplicationProperties endpointsProperties,
	                                  final HttpResponseHandler httpResponseHandler) {
		this.customUserService = customUserService;
		this.customApplicationProperties = endpointsProperties;
		this.httpResponseHandler = httpResponseHandler;
	}

	@PostMapping("${custom.properties.endpoints.registration.register-user-account}")
	public ResponseEntity<Void> registerUserAccount(@RequestBody @Valid final UserRegistrationDto userRegistrationDto,
	                                                final BindingResult bindingResult, final WebRequest webRequest) {
		log.debug("Inside: " + customApplicationProperties.getEndpoints().getRegistration().getRootRegisterUserAccount());
		customUserService.registerNewCustomUserAccount(userRegistrationDto, bindingResult, webRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping("${custom.properties.endpoints.registration.confirm-registration}")
	public ResponseEntity<Void> confirmRegistration(@RequestParam("token") final String token, final HttpServletResponse response) {
		log.debug("Inside: " + customApplicationProperties.getEndpoints().getRegistration().getRootConfirmRegistration());
		customUserService.confirmRegistration(token, response);
		httpResponseHandler.redirectUserToConfirmedPage(response, customApplicationProperties.getEndpoints().getRegistration().getConfirmed());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.registration.resend-email-verification-token}")
	public ResponseEntity<Void> resendEmailVerificationToken(@RequestParam("token") final String token) {
		log.debug("Inside: " + customApplicationProperties.getEndpoints().getRegistration().getRootResendEmailVerificationToken());
		customUserService.resendEmailVerificationToken(token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}