package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.RegisterUser;

@RestController
@RequestMapping("/register")
@Log4j
public class RegisterController {

	@PostMapping("/check")
	public @ResponseBody ResponseEntity<String> checkRegisterEndpoint(@RequestBody @Valid final RegisterUser registerUser,
	                                                                  final BindingResult bindingResult) {
		log.info(">> name:" + registerUser.getName());
		log.info(">> lastName:" + registerUser.getLastName());
		log.info(">> email:" + registerUser.getEmail());
		log.info(">> password:" + registerUser.getPassword());
		if (bindingResult.hasErrors()) {
			registerUser.setValidationErrors(bindingResult.getFieldErrors().stream()
			                                              .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
			                                              .collect(Collectors.toList()));
			return ResponseEntity.badRequest().body("ERROR DURING VALIDATION!!!");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Created: " + registerUser.getName() + " " + registerUser.getLastName() + " user.");
	}
}
