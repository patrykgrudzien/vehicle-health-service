package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.service.login.UserAuthenticationService;

import static me.grudzien.patryk.domain.enums.jwt.TokenTypes.ACCESS_TOKEN;
import static me.grudzien.patryk.domain.enums.jwt.TokenTypes.REFRESH_TOKEN;

@Log4j2
@RestController
public class UserAuthenticationController {

	private final UserAuthenticationService userAuthenticationService;

	@Autowired
	public UserAuthenticationController(final UserAuthenticationService userAuthenticationService) {
		Preconditions.checkNotNull(userAuthenticationService, "userAuthenticationService cannot be null!");
		this.userAuthenticationService = userAuthenticationService;
	}

	@PostMapping("${custom.properties.endpoints.authentication.root}")
	public ResponseEntity<?> authenticateUserAndGenerateTokens(@RequestBody final JwtAuthenticationRequest authenticationRequest, final Device device,
	                                                           @SuppressWarnings("unused") final WebRequest webRequest) {
		final String accessToken = userAuthenticationService.authenticateAndGenerateToken(ACCESS_TOKEN, authenticationRequest, device);
		final String refreshToken = userAuthenticationService.authenticateAndGenerateToken(REFRESH_TOKEN, authenticationRequest, device);
		return ResponseEntity.ok(JwtAuthenticationResponse.Builder()
		                                                  .accessToken(accessToken)
		                                                  .refreshToken(refreshToken)
		                                                  .build());
	}

	@PostMapping("${custom.properties.endpoints.authentication.refresh-token}")
	public ResponseEntity<?> refreshAuthAccessToken(@RequestBody final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final String newAccessToken = userAuthenticationService.createRefreshedAuthAccessToken(authenticationRequest, device);
		return ResponseEntity.ok(JwtAuthenticationResponse.Builder()
		                                                  .accessToken(newAccessToken)
		                                                  .build());
	}

	@GetMapping("${custom.properties.endpoints.authentication.principal-user}")
	@PreAuthorize("isAuthenticated()")
	public JwtUser getPrincipalUser(@SuppressWarnings("unused") final WebRequest webRequest) {
		return (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
