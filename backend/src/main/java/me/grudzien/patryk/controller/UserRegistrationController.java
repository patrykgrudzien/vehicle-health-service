package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.service.CustomUserService;

@Log4j
@RestController
@RequestMapping("${custom.properties.endpoints.registration.home}")
public class UserRegistrationController {

	private final CustomUserService customUserService;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public UserRegistrationController(final CustomUserService customUserService, final CustomApplicationProperties endpointsProperties) {
		this.customUserService = customUserService;
		this.customApplicationProperties = endpointsProperties;
	}

	@PostMapping("${custom.properties.endpoints.registration.register-user-account}")
	public @ResponseBody ResponseEntity<Void> registerUserAccount(@RequestBody @Valid final UserRegistrationDto userRegistrationDto,
	                                                              final BindingResult bindingResult, final WebRequest webRequest) {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getRegistration().getHomeRegisterUserAccount());
		customUserService.registerNewCustomUserAccount(userRegistrationDto, bindingResult, webRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.registration.confirm-registration}")
	public @ResponseBody ResponseEntity<Void> confirmRegistration(@RequestParam("token") final String token) {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getRegistration().getHomeConfirmRegistration());
		customUserService.confirmRegistration(token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}