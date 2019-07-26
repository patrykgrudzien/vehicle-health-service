package me.grudzien.patryk.authentication.service.impl;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.utils.factory.FactoryProvider;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.validation.ValidationService;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.Match;

import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseType.FAILED_RESPONSE;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseType.SUCCESS_RESPONSE;
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
import static me.grudzien.patryk.utils.factory.FactoryType.JWT_AUTH_RESPONSE;

@Log4j2
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final RequestsDecoder requestsDecoder;
	private final JwtTokenService jwtTokenService;
	private final ValidationService validationService;

	@Autowired
	public UserAuthenticationServiceImpl(final AuthenticationManager authenticationManager, final LocaleMessagesCreator localeMessagesCreator,
                                         final RequestsDecoder requestsDecoder, final JwtTokenService jwtTokenService,
                                         final ValidationService validationService) {
        checkNotNull(authenticationManager, "authenticationManager cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");
        checkNotNull(validationService, "validationService cannot be null!");

        this.authenticationManager = authenticationManager;
        this.localeMessagesCreator = localeMessagesCreator;
        this.requestsDecoder = requestsDecoder;
        this.jwtTokenService = jwtTokenService;
        this.validationService = validationService;
    }

	@Override
	public JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        log.info("Login request is correct. Starting authenticating the user with ({}) email.", authenticationRequest.getEmail());
        return authenticateUser(authenticationRequest)
                .map(authentication -> createSuccessResponse(authenticationRequest, device))
                .orElse((JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE).create(FAILED_RESPONSE));
	}

	@Override
	public Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest) {
		final String email = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
		final String password = requestsDecoder.decodeStringParam(authenticationRequest.getPassword());

		Objects.requireNonNull(email);
		Objects.requireNonNull(password);

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
        return (JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE).create(
                SUCCESS_RESPONSE,
                jwtTokenService.generateAccessToken(decodedAuthRequest, device),
                jwtTokenService.generateRefreshToken(decodedAuthRequest, device)
        );
    }
}
