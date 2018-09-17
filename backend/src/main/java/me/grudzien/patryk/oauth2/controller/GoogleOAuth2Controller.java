package me.grudzien.patryk.oauth2.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;

@Log4j2
@RestController
public class GoogleOAuth2Controller {

	// TODO

	@GetMapping("/social-login-success")
	public ResponseEntity<CustomResponse> socialLoginSuccess(@RequestParam("token") final String token) {
		return new ResponseEntity<>(new SuccessResponse(token), HttpStatus.OK);
	}
}
