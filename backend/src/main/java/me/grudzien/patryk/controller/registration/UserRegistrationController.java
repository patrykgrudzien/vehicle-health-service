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

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.service.registration.UserRegistrationService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.api-context-path}" + "${custom.properties.endpoints.registration.root}")
public class UserRegistrationController {

	private final UserRegistrationService userRegistrationService;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public UserRegistrationController(final UserRegistrationService userRegistrationService,
	                                  final LocaleMessagesCreator localeMessagesCreator) {

		Preconditions.checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.userRegistrationService = userRegistrationService;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@PostMapping("${custom.properties.endpoints.registration.register-user-account}")
	public ResponseEntity<CustomResponse> registerUserAccount(@RequestBody @Valid final UserRegistrationDto userRegistrationDto,
	                                                          final BindingResult bindingResult, final WebRequest webRequest) {
		final String message = localeMessagesCreator.buildLocaleMessageWithParam("register-user-account-success", userRegistrationDto.getEmail());
		userRegistrationService.registerNewCustomUserAccount(userRegistrationDto, bindingResult, webRequest);
		return new ResponseEntity<>(new SuccessResponse(message), HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.registration.confirm-registration}")
	public ResponseEntity<CustomResponse> confirmRegistration(@RequestParam("token") final String token, final HttpServletResponse response,
	                                                          @SuppressWarnings("unused") final WebRequest webRequest) {
		userRegistrationService.confirmRegistration(token, response);
		final String message = localeMessagesCreator.buildLocaleMessage("confirm-registration");
		return new ResponseEntity<>(new SuccessResponse(message), HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.registration.resend-email-verification-token}")
	public ResponseEntity<Void> resendEmailVerificationToken(@RequestParam("token") final String token,
	                                                         @SuppressWarnings("unused") final WebRequest webRequest) {
		userRegistrationService.resendEmailVerificationToken(token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}