package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Log4j2
@RestController
public class UserLoginController {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public UserLoginController(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@RequestMapping("${custom.properties.endpoints.login.root}")
	public ResponseEntity<Void> login() {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getLogin().getRoot());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
