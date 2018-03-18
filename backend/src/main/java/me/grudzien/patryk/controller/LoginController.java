package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.handlers.web.ResponseHandler;

@Log4j2
@RestController
public class LoginController {

	private final ResponseHandler responseHandler;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public LoginController(final ResponseHandler responseHandler, final CustomApplicationProperties customApplicationProperties) {
		this.responseHandler = responseHandler;
		this.customApplicationProperties = customApplicationProperties;
	}

	@RequestMapping("${custom.properties.endpoints.login.root}")
	public ResponseEntity<Void> redirectUserToLoginPage(final HttpServletResponse response) {
		log.info("Inside /login in LoginController");
		// TODO
		responseHandler.redirectUserToLoginPage(response, customApplicationProperties.getEndpoints().getLogin().getRoot());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
