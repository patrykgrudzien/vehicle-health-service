package me.grudzien.patryk.controller.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.utils.security.JwtTokenUtil;

@Log4j2
@RestController
public class UserLoginController {

	private final CustomApplicationProperties customApplicationProperties;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;

	@Autowired
	public UserLoginController(final CustomApplicationProperties customApplicationProperties,
	                           final AuthenticationManager authenticationManager,
	                           final UserDetailsService userDetailsService) {

		this.customApplicationProperties = customApplicationProperties;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}

	@RequestMapping("${custom.properties.endpoints.login.root}")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody final JwtAuthenticationRequest authenticationRequest,
	                                                   final Device device) {

		log.info("Inside: " + customApplicationProperties.getEndpoints().getLogin().getRoot());

		// Perform the security
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
				                                        authenticationRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Reload password post-security so we can generate token
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = JwtTokenUtil.Creator.generateToken(userDetails, device);

		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
}
