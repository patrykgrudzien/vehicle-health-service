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

import static me.grudzien.patryk.domain.enums.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
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

	@GetMapping("/user-not-found")
	public ResponseEntity<Void> userNotFound() {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.user-logged-in-using-google}")
	public ResponseEntity<Void> userLoggedInUsingGoogle(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) final String token,
	                                                    final HttpServletResponse httpServletResponse) {
		final Tuple2<String, String> additionalParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, token);
		httpResponseHandler.redirectUserTo(USER_LOGGED_IN_USING_GOOGLE, httpServletResponse, additionalParameters);
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

	@GetMapping("/user-registered-using-google")
	public ResponseEntity<Void> userRegisteredUsingGoogle() {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/user-account-already-exists")
	public ResponseEntity<Void> userAccountAlreadyExists() {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.failure-target-url}")
	public ResponseEntity<Void> failureTargetUrl() {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
