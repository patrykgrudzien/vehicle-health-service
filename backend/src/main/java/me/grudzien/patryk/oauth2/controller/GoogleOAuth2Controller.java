package me.grudzien.patryk.oauth2.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;

import static me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;

@Log4j2
@RestController
public class GoogleOAuth2Controller {

	private final HttpResponseHandler httpResponseHandler;

	@Autowired
	public GoogleOAuth2Controller(final HttpResponseHandler httpResponseHandler) {
		this.httpResponseHandler = httpResponseHandler;
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-success-target-url}")
	public ResponseEntity<CustomResponse> googleRedirectionSuccess(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) final String token,
	                                                         final HttpServletResponse httpServletResponse) {
		httpResponseHandler.redirectUserTo(AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL, httpServletResponse, token);
		return new ResponseEntity<>(new SuccessResponse(token), HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-failure-target-url}")
	public ResponseEntity<Void> googleRedirectionFailure(final HttpServletResponse httpServletResponse) {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
