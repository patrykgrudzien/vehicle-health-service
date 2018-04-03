package me.grudzien.patryk.service.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.log.LogMarkers;
import me.grudzien.patryk.utils.security.JwtTokenUtil;

@Log4j2
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private final UserDetailsService userDetailsService;
	private final AuthenticationManager authenticationManager;

	public UserAuthenticationServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                     final AuthenticationManager authenticationManager) {
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Authenticates the user. If something is wrong, an
	 * {@link me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException} or
	 * {@link me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException} will be thrown
	 */
	@Override
	public String authenticateAndGenerateToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {

		final String email = authenticationRequest.getEmail();
		final String password = authenticationRequest.getPassword();

		Objects.requireNonNull(email);
		Objects.requireNonNull(password);

		try {
			/*
			 * (AuthenticationManager) in authenticate() method will use (DaoAuthenticationProvider).
			 * (DaoAuthenticationProvider) is an (AuthenticationProvider interface) implementation that receives user details
			 * from a (MyUserDetailsService).
			 *
			 * authenticationManager.authenticate() returns fully authenticated (Authentication) object (it includes credentials and
			 * granted authorities if successful).
			 * It attempts to authenticate the passed (Authentication) object, returning a fully populated "Authentication" object.
			 */
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			// Reload password post-security so we can generate the token
			final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
			return JwtTokenUtil.Creator.generateToken(jwtUser, device);

		} catch (final DisabledException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "User with {} is disabled! Error message -> {}", email, exception.getMessage());
			// it is checked in (AccountStatusUserDetailsChecker implements UserDetailsChecker)
			throw new UserDisabledAuthenticationException("User is disabled!");
		} catch (final BadCredentialsException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "Bad credentials! Error message -> {}", exception.getMessage());
			// it is checked in (AbstractUserDetailsAuthenticationProvider)
			throw new BadCredentialsAuthenticationException("Bad credentials!");
		}
	}
}
