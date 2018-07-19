package me.grudzien.patryk.service.login;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import java.util.Objects;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.jwt.TokenTypes;
import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;
import me.grudzien.patryk.utils.log.LogMarkers;
import me.grudzien.patryk.utils.web.RequestsDecoder;

@Log4j2
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private final UserDetailsService userDetailsService;
	private final AuthenticationManager authenticationManager;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public UserAuthenticationServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                     final AuthenticationManager authenticationManager,
	                                     final LocaleMessagesCreator localeMessagesCreator,
	                                     final RequestsDecoder requestsDecoder) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(authenticationManager, "authenticationManager cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
		this.localeMessagesCreator = localeMessagesCreator;
		this.requestsDecoder = requestsDecoder;
	}

	/**
	 * Authenticates the user. If something is wrong, an
	 * {@link me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException} or
	 * {@link me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException} will be thrown
	 */
	@Override
	public String authenticateAndGenerateToken(final TokenTypes tokenType, final JwtAuthenticationRequest authenticationRequest, final Device device) {

		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final String password = requestsDecoder.decodeStringParam(authenticationRequest.getPassword());

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
			final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(email);

			switch (tokenType) {
				case ACCESS_TOKEN:
					return JwtTokenUtil.Creator.generateAccessToken(jwtUser, device);
				case REFRESH_TOKEN:
					return JwtTokenUtil.Creator.generateRefreshToken(jwtUser);
				default:
					return null;
			}

		} catch (final DisabledException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "User with {} is disabled! Error message -> {}", email, exception.getMessage());
			// it is checked in (AccountStatusUserDetailsChecker implements UserDetailsChecker)
			throw new UserDisabledAuthenticationException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
		} catch (final BadCredentialsException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "E-mail address or password is not correct! Error message -> {}", exception.getMessage());
			// it is checked in (AbstractUserDetailsAuthenticationProvider)
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}
	}

	@Override
	public String createRefreshedAuthAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final String refreshToken = authenticationRequest.getRefreshToken();
		final String email = JwtTokenUtil.Retriever.getUserEmailFromToken(refreshToken);
		final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(email);
		return JwtTokenUtil.Creator.generateAccessToken(jwtUser, device);
	}
}
