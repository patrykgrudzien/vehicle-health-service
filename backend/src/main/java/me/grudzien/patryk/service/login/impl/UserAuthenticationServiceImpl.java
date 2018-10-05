package me.grudzien.patryk.service.login.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import javax.validation.ConstraintViolation;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import me.grudzien.patryk.config.security.SecurityConfig;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException;
import me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException;
import me.grudzien.patryk.exceptions.registration.CustomUserValidationException;
import me.grudzien.patryk.oauth2.authentication.JwtAuthenticationToken;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;
import me.grudzien.patryk.utils.log.LogMarkers;
import me.grudzien.patryk.utils.validators.ValidatorCreator;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

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

	@Override
	public JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final JwtAuthenticationResponse jwtAuthenticationResponseFailure = JwtAuthenticationResponse.Builder().isSuccessful(Boolean.FALSE).build();
		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final Set<ConstraintViolation<String>> validation = ValidatorCreator.getDefaultValidator().validate(email);
		if (!validation.isEmpty()) {
			log.error("Validation errors present during login.");
			throw new CustomUserValidationException(localeMessagesCreator.buildLocaleMessage("login-form-validation-errors"),
			                                        validation.stream()
			                                                  .map(ConstraintViolation::getMessage)
			                                                  .map(localeMessagesCreator::buildLocaleMessage)
			                                                  .collect(Collectors.toList()));
		} else {
			log.info(FLOW_MARKER, "Login request is correct. Starting authenticating the user with ({}) email.", email);
			final Optional<Authentication> authentication = authenticateUser(authenticationRequest);
			if (authentication.isPresent()) {
				// Reload password post-security so we can generate the token
				final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email));

				return jwtUser.map(user -> {
									log.info("Started generating tokens...");
									return JwtAuthenticationResponse.Builder()
					                                .accessToken(JwtTokenUtil.Creator.generateAccessToken(user, device))
					                                .refreshToken(JwtTokenUtil.Creator.generateRefreshToken(user))
					                                .isSuccessful(Boolean.TRUE)
					                                .build();
							   })
				              .orElseThrow(() -> new UsernameNotFoundException(
				              		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));
			}
		}
		return jwtAuthenticationResponseFailure;
	}

	/**
	 * Authenticates the user. If something is wrong, an
	 * {@link me.grudzien.patryk.exceptions.login.BadCredentialsAuthenticationException} or
	 * {@link me.grudzien.patryk.exceptions.login.UserDisabledAuthenticationException} will be thrown
	 */
	@Override
	public Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest) {
		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final String password = requestsDecoder.decodeStringParam(authenticationRequest.getPassword());
		Authentication authentication = null;

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

			if (SecurityConfig.AuthenticationApproach.BUILT_IN.isActive()) {
				authentication = new UsernamePasswordAuthenticationToken(email, password);
			} else if (SecurityConfig.AuthenticationApproach.CUSTOM.isActive()) {
				authentication = new JwtAuthenticationToken()
			}
			return Optional.ofNullable(authenticationManager.authenticate(authentication));

		} catch (final DisabledException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "User with {} is disabled! Error message -> {}", email, exception.getMessage());
			/**
			 * Exception thrown below is determined in:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#preAuthenticationChecks}
			 * which points to:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks#check(
			 * org.springframework.security.core.userdetails.UserDetails)}
			 */
			throw new UserDisabledAuthenticationException(localeMessagesCreator.buildLocaleMessage("user-disabled-exception"));
		} catch (final BadCredentialsException exception) {
			log.error(LogMarkers.EXCEPTION_MARKER, "E-mail address or password is not correct! Error message -> {}", exception.getMessage());
			/**
			 * Exception thrown below is determined in:
			 * {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)}
			 * which tries to retrieve user using:
			 * {@link me.grudzien.patryk.service.security.MyUserDetailsService}
			 */
			throw new BadCredentialsAuthenticationException(localeMessagesCreator.buildLocaleMessage("bad-credentials-exception"));
		}
	}

	@Override
	public String createRefreshedAuthAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final String refreshToken = authenticationRequest.getRefreshToken();
		final String email = JwtTokenUtil.Retriever.getUserEmailFromToken(refreshToken);

		final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email));
		return jwtUser.map(user -> JwtTokenUtil.Creator.generateAccessToken(user, device))
		              .orElseThrow(() -> new UsernameNotFoundException(
		              		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));
	}
}
