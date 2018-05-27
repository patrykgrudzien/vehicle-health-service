package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static me.grudzien.patryk.utils.log.LogMarkers.CONTROLLER_MARKER;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.server.root}")
public class SecuredResourceController {

	private final CustomApplicationProperties customApplicationProperties;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public SecuredResourceController(final CustomApplicationProperties customApplicationProperties,
	                                 final LocaleMessagesCreator localeMessagesCreator) {
		this.customApplicationProperties = customApplicationProperties;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@GetMapping("${custom.properties.endpoints.server.health-check}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomResponse> healthCheck(final WebRequest webRequest) {
		log.info(CONTROLLER_MARKER, "Inside: {}", customApplicationProperties.getEndpoints().getServer().getRootHealthCheck());
		final String message = localeMessagesCreator.buildLocaleMessage("health-check-secured-resource", webRequest);
		return new ResponseEntity<>(new SuccessResponse(message), HttpStatus.OK);
	}
}