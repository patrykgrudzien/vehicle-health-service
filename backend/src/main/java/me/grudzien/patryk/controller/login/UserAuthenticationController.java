package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.exceptions.login.AuthenticationException;
import me.grudzien.patryk.utils.security.JwtTokenUtil;

@Log4j2
@RestController
public class UserAuthenticationController {

	private final CustomApplicationProperties customApplicationProperties;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;

	@Autowired
	public UserAuthenticationController(final CustomApplicationProperties customApplicationProperties,
	                                    final AuthenticationManager authenticationManager,
	                                    final UserDetailsService userDetailsService) {

		this.customApplicationProperties = customApplicationProperties;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("${custom.properties.endpoints.authentication.root}")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody final JwtAuthenticationRequest authenticationRequest,
	                                                   final Device device) {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getAuthentication().getRoot());

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		// Reload password post-security so we can generate the token
		final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
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
