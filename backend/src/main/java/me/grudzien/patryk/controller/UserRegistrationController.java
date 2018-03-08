package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
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

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.service.CustomUserService;

@Log4j
@RestController
@RequestMapping("/registration")
public class UserRegistrationController {

	private final CustomUserService customUserService;

	@Autowired
	public UserRegistrationController(final CustomUserService customUserService) {
		this.customUserService = customUserService;
	}

	@PostMapping("/register-user-account")
	public @ResponseBody String registerUserAccount(@RequestBody @Valid final UserRegistrationDto userRegistrationDto,
	                                                final BindingResult bindingResult, final WebRequest webRequest) {

		return customUserService.registerNewCustomUserAccount(userRegistrationDto, bindingResult, webRequest)
		                        // I want to show on UI email's newly created user if validation passes
		                        .getEmail();
	}

	@GetMapping("/registration-confirm")
	public @ResponseBody String confirmRegistration(@RequestParam("token") final String token) {

		return ">>>>>>>>>>>>>>>>>>>>>>>>>> CONFIRMING REGISTRATION TO BE DONE...";
	}
}
