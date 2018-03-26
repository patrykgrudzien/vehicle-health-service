package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.exceptions.login.AuthenticationException;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.security.JwtTokenUtil;
import me.grudzien.patryk.utils.security.JwtUser;

@Log4j2
@RestController
public class UserLoginController {

	private final CustomApplicationProperties customApplicationProperties;
	private final AuthenticationManager authenticationManager;
	private final MyUserDetailsService myUserDetailsService;

	@Autowired
	public UserLoginController(final CustomApplicationProperties customApplicationProperties,
	                           final AuthenticationManager authenticationManager,
	                           final MyUserDetailsService myUserDetailsService) {

		this.customApplicationProperties = customApplicationProperties;
		this.authenticationManager = authenticationManager;
		this.myUserDetailsService = myUserDetailsService;
	}

	@PostMapping("${custom.properties.endpoints.login.root}")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody final JwtAuthenticationRequest authenticationRequest,
	                                                   final Device device) {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getLogin().getRoot());

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		// Reload password post-security so we can generate the token
		final JwtUser jwtUser = (JwtUser) myUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = JwtTokenUtil.Creator.generateToken(jwtUser, device);

		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}

	/**
	 * Authenticates the user. If something is wrong, an {@link me.grudzien.patryk.exceptions.login.AuthenticationException} will be thrown
	 */
	private void authenticate(final String email, final String password) {
		Objects.requireNonNull(email);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (final DisabledException exception) {
			// TODO: add exceptionHandler for this exception
			throw new AuthenticationException("User is disabled!");
		} catch (final BadCredentialsException exception) {
			// TODO: add exceptionHandler for this exception
			throw new AuthenticationException("Bad credentials!");
		}
	}
}
