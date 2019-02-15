package me.grudzien.patryk.service.login.impl;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.exception.registration.CustomUserValidationException;
import me.grudzien.patryk.mapper.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.oauth2.authentication.CustomAuthenticationToken;
import me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.util.ObjectDecoder;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.validator.CustomValidator;
import me.grudzien.patryk.util.web.RequestsDecoder;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

@Log4j2
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final RequestsDecoder requestsDecoder;
	private final JwtTokenService jwtTokenService;
	private final JwtAuthenticationRequestMapper jwtAuthenticationRequestMapper;

	@Autowired
	public UserAuthenticationServiceImpl(final AuthenticationManager authenticationManager, final LocaleMessagesCreator localeMessagesCreator,
                                         final RequestsDecoder requestsDecoder, final JwtTokenService jwtTokenService,
                                         final JwtAuthenticationRequestMapper jwtAuthenticationRequestMapper) {

        Preconditions.checkNotNull(authenticationManager, "authenticationManager cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
        Preconditions.checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");
        Preconditions.checkNotNull(jwtAuthenticationRequestMapper, "jwtAuthenticationRequestMapper cannot be null!");

        this.authenticationManager = authenticationManager;
        this.localeMessagesCreator = localeMessagesCreator;
        this.requestsDecoder = requestsDecoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtAuthenticationRequestMapper = jwtAuthenticationRequestMapper;
    }

	@Override
	public JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final JwtAuthenticationResponse failedAuthResponse = JwtAuthenticationResponse.Builder().isSuccessful(Boolean.FALSE).build();
		final JwtAuthenticationRequest decodedAuthRequest = ObjectDecoder.decodeAuthRequest().apply(authenticationRequest, jwtAuthenticationRequestMapper);
		final List<String> translatedValidationResult = CustomValidator.getTranslatedValidationResult(decodedAuthRequest, localeMessagesCreator);
		final Predicate<List<String>> isNotEmpty = list -> !ObjectUtils.isEmpty(list);
		final Predicate<List<String>> isEmpty = ObjectUtils::isEmpty;
		return Match(translatedValidationResult).of(
		        Case($(isNotEmpty), () -> {
                    log.error("Validation errors present during login.");
                    throw new CustomUserValidationException(localeMessagesCreator.buildLocaleMessage("login-form-validation-errors"),
                                                            translatedValidationResult);
                }),
                Case($(isEmpty), () -> {
                    final String email = decodedAuthRequest.getEmail();
                    log.info(FLOW_MARKER, "Login request is correct. Starting authenticating the user with ({}) email.", email);
                    return authenticateUser(authenticationRequest)
                            .map(authentication -> JwtAuthenticationResponse.Builder()
                                                                            .accessToken(jwtTokenService.generateAccessToken(decodedAuthRequest, device))
                                                                            .refreshToken(jwtTokenService.generateRefreshToken(decodedAuthRequest, device))
                                                                            .isSuccessful(Boolean.TRUE)
                                                                            .build())
                            .orElse(failedAuthResponse);
                })
        );
	}

	/**
	 * Authenticates the user. If something is wrong, an
	 * {@link me.grudzien.patryk.exception.login.BadCredentialsAuthenticationException} or
	 * {@link me.grudzien.patryk.exception.login.UserDisabledAuthenticationException} will be thrown
	 */
	@Override
	public Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest) {
		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final String password = requestsDecoder.decodeStringParam(authenticationRequest.getPassword());

		Objects.requireNonNull(email);
		Objects.requireNonNull(password);

		final String idToken = authenticationRequest.getIdToken();
		/*
		 * (AuthenticationManager) in authenticate() method will use (DaoAuthenticationProvider).
		 * (DaoAuthenticationProvider) is an (AuthenticationProvider interface) implementation that receives user details
		 * from a (MyUserDetailsService).
		 *
		 * authenticationManager.authenticate() returns fully authenticated (Authentication) object (it includes credentials and
		 * granted authorities if successful).
		 * It attempts to authenticate the passed (Authentication) object, returning a fully populated "Authentication" object.
		 */
		return Try.of(() -> Optional.ofNullable(authenticationManager.authenticate(StringUtils.isEmpty(idToken) ?
				                                                                           new UsernamePasswordAuthenticationToken(email, password) :
				                                                                           new CustomAuthenticationToken(idToken))))
		          .onSuccess(Optional::get)
		          .onFailure(throwable -> Match(throwable).of(
                          FailedAuthenticationCases.UsernameNotFoundExceptionCase(email, localeMessagesCreator),
                          FailedAuthenticationCases.UserAccountIsLockedExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.UserIsDisabledExceptionCase(email, localeMessagesCreator),
                          FailedAuthenticationCases.UserAccountIsExpiredExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.CredentialsHaveExpiredExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.JwtTokenNotFoundExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.RegistrationProviderMismatchExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.BadCredentialsExceptionCase(localeMessagesCreator),
                          FailedAuthenticationCases.MissingAuthenticationResultException(localeMessagesCreator),
                          FailedAuthenticationCases.CustomAuthenticationUnknownException(localeMessagesCreator)
		          ))
		          .getOrElse(Optional.empty());
	}
}
