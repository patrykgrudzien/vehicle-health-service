package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.service.CustomUserDetailsService;

@Log4j
@RestController
@RequestMapping("/registration")
public class UserRegistrationController extends CorsController {

	private final CustomUserDetailsService customUserDetailsService;

	@Autowired
	public UserRegistrationController(final CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@PostMapping("/register-user-account")
	public @ResponseBody String checkRegisterEndpoint(@RequestBody @Valid final UserRegistrationDto userRegistrationDto,
	                                                  final BindingResult bindingResult) {

		final CustomUser existingUser = customUserDetailsService.findByEmail(userRegistrationDto.getEmail());
		if (existingUser != null) {
			bindingResult.rejectValue("email", "There is already an account registered with that email.");
		}
		if (bindingResult.hasErrors()) {
			return bindingResult.getFieldError().getDefaultMessage();
		}
		customUserDetailsService.save(userRegistrationDto);
		return "User has been created and saved to DB.";
	}
}
