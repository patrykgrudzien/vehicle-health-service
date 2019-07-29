package me.grudzien.patryk.authentication.service.impl;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.utils.factory.FactoryProvider;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.Match;
import static java.util.Objects.requireNonNull;

import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory.JwtAuthResponseType.FAILED_RESPONSE;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory.JwtAuthResponseType.SUCCESS_ACCESS_REFRESH_TOKEN_RESPONSE;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.BadCredentialsExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.CredentialsHaveExpiredExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.CustomAuthenticationUnknownExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.JwtTokenNotFoundExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.MissingAuthenticationResultExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.RegistrationProviderMismatchExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.UserAccountIsExpiredExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.UserAccountIsLockedExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.UserIsDisabledExceptionCase;
import static me.grudzien.patryk.oauth2.authentication.FailedAuthenticationCases.UsernameNotFoundExceptionCase;
import static me.grudzien.patryk.utils.common.Predicates.isEmpty;
import static me.grudzien.patryk.utils.factory.FactoryProvider.FactoryType.JWT_AUTH_RESPONSE_FACTORY;

@Slf4j
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final RequestsDecoder requestsDecoder;
	private final JwtTokenService jwtTokenService;

	@Autowired
	public UserAuthenticationServiceImpl(final AuthenticationManager authenticationManager,
	                                     final LocaleMessagesCreator localeMessagesCreator,
	                                     final RequestsDecoder requestsDecoder,
	                                     final JwtTokenService jwtTokenService) {
        checkNotNull(authenticationManager, "authenticationManager cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");

        this.authenticationManager = authenticationManager;
        this.localeMessagesCreator = localeMessagesCreator;
        this.requestsDecoder = requestsDecoder;
        this.jwtTokenService = jwtTokenService;
    }

	@Override
	public JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        log.info("Login request is correct. Starting authenticating the user with ({}) email.", authenticationRequest.getEmail());
        return authenticateUser(authenticationRequest)
                .map(authentication -> createSuccessResponse(authenticationRequest, device))
                .orElse((JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE_FACTORY).create(FAILED_RESPONSE));
	}

	@Override
	public Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest) {
		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final String password = requestsDecoder.decodeStringParam(authenticationRequest.getPassword());
		requireNonNull(email);
		requireNonNull(password);

		final String idToken = authenticationRequest.getIdToken();
		return Try.of(() -> Optional.ofNullable(authenticationManager.authenticate(isEmpty(idToken) ?
                                                                                           new UsernamePasswordAuthenticationToken(email, password) :
                                                                                           new CustomAuthenticationToken(idToken))))
		          .onSuccess(Optional::get)
		          .onFailure(throwable -> Match(throwable).of(
                          UsernameNotFoundExceptionCase(email, localeMessagesCreator),
                          UserAccountIsLockedExceptionCase(localeMessagesCreator),
                          UserIsDisabledExceptionCase(email, localeMessagesCreator),
                          UserAccountIsExpiredExceptionCase(localeMessagesCreator),
                          CredentialsHaveExpiredExceptionCase(localeMessagesCreator),
                          JwtTokenNotFoundExceptionCase(localeMessagesCreator),
                          RegistrationProviderMismatchExceptionCase(localeMessagesCreator),
                          BadCredentialsExceptionCase(localeMessagesCreator),
                          MissingAuthenticationResultExceptionCase(localeMessagesCreator),
                          CustomAuthenticationUnknownExceptionCase(localeMessagesCreator)
		          ))
		          .getOrElse(Optional.empty());
	}

    private JwtAuthenticationResponse createSuccessResponse(final JwtAuthenticationRequest decodedAuthRequest, final Device device) {
        return (JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE_FACTORY).create(
                SUCCESS_ACCESS_REFRESH_TOKEN_RESPONSE,
                jwtTokenService.generateAccessToken(decodedAuthRequest, device),
                jwtTokenService.generateRefreshToken(decodedAuthRequest, device)
        );
    }
}
