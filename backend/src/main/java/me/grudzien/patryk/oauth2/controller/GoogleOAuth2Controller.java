package me.grudzien.patryk.oauth2.controller;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static me.grudzien.patryk.domain.enums.AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL;
import static me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.responses.CustomResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.handlers.web.HttpResponseHandler;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

@Log4j2
@RestController
public class GoogleOAuth2Controller {

	private final HttpResponseHandler httpResponseHandler;
	private final UserAuthenticationService userAuthenticationService;
	private final UserDetailsService userDetailsService;

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public GoogleOAuth2Controller(final HttpResponseHandler httpResponseHandler, final UserAuthenticationService userAuthenticationService,
	                              @Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                              final AuthenticationManager authenticationManager, final PasswordEncoder passwordEncoder) {
		this.httpResponseHandler = httpResponseHandler;
		this.userAuthenticationService = userAuthenticationService;
		this.userDetailsService = userDetailsService;

		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-success-target-url}")
	public ResponseEntity<CustomResponse> googleRedirectionSuccess(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) final String token,
	                                                               final HttpServletResponse httpServletResponse) {
		httpResponseHandler.redirectUserTo(GOOGLE_REDIRECTION_SUCCESSFUL, httpServletResponse, new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, token));
		return new ResponseEntity<>(new SuccessResponse(token), HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.redirection-failure-target-url}")
	public ResponseEntity<Void> googleRedirectionFailure(final HttpServletResponse httpServletResponse) {
		// TODO: FIX ME
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/exchange-short-lived-token")
	public ResponseEntity<?> exchangeShortLivedTokenForLonger(@RequestBody final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final String email = JwtTokenUtil.Retriever.getUserEmailFromToken(authenticationRequest.getShortLivedAuthToken());

		Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email));

		final JwtAuthenticationRequest tempAuthRequest = new JwtAuthenticationRequest();
		tempAuthRequest.setEmail(email);
		tempAuthRequest.setPassword(jwtUser.get().getPassword());

		final JwtAuthenticationResponse response = userAuthenticationService.login(tempAuthRequest, device);
		return ResponseEntity.ok(response);
	}
}
