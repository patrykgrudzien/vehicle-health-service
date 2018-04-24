package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.server.root}")
public class ServerHealthCheckController {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public ServerHealthCheckController(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@GetMapping("${custom.properties.endpoints.server.health-check}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomResponse> healthCheck() {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getServer().getRootHealthCheck());
		return new ResponseEntity<>(new SuccessResponse("Server works! Hello there!"), HttpStatus.OK);
	}
}