package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.service.login.UserAuthenticationService;

@Log4j2
@RestController
public class UserAuthenticationController {

	private final UserAuthenticationService userAuthenticationService;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public UserAuthenticationController(final UserAuthenticationService userAuthenticationService,
	                                    final CustomApplicationProperties customApplicationProperties) {
		this.userAuthenticationService = userAuthenticationService;
		this.customApplicationProperties = customApplicationProperties;
	}

	@PostMapping("${custom.properties.endpoints.authentication.root}")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody final JwtAuthenticationRequest authenticationRequest, final Device device) {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getAuthentication().getRoot());
		final String token = userAuthenticationService.authenticateAndGenerateToken(authenticationRequest, device);
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
}
