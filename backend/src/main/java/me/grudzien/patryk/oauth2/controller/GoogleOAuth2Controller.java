package me.grudzien.patryk.oauth2.controller;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

import static me.grudzien.patryk.domain.enums.AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL;
import static me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;

@Log4j2
@RestController
public class GoogleOAuth2Controller {

	private final HttpResponseHandler httpResponseHandler;

	@Autowired
	public GoogleOAuth2Controller(final HttpResponseHandler httpResponseHandler) {
		Preconditions.checkNotNull(httpResponseHandler, "httpResponseHandler cannot be null!");
		this.httpResponseHandler = httpResponseHandler;
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-success-target-url}")
	public ResponseEntity<Void> googleRedirectionSuccess(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) final String token,
	                                                     final HttpServletResponse httpServletResponse) {
		final Tuple2<String, String> additionalParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, token);
		httpResponseHandler.redirectUserTo(GOOGLE_REDIRECTION_SUCCESSFUL, httpServletResponse, additionalParameters);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.exchange-short-lived-token}")
	public ResponseEntity<JwtAuthenticationResponse> exchangeShortLivedTokenForLonger(final Device device) {
		final JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(JwtAuthenticationResponse.Builder()
		                                                  .accessToken(JwtTokenUtil.Creator.generateAccessToken(jwtUser, device))
		                                                  .refreshToken(JwtTokenUtil.Creator.generateRefreshToken(jwtUser))
		                                                  .isSuccessful(Boolean.TRUE)
		                                                  .build());
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-failure-target-url}")
	public ResponseEntity<Void> googleRedirectionFailure() {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
