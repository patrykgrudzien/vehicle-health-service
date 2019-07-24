package me.grudzien.patryk.authentication.service.impl;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import me.grudzien.patryk.authentication.exception.BadCredentialsAuthenticationException;
import me.grudzien.patryk.authentication.exception.UserDisabledAuthenticationException;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;
import me.grudzien.patryk.registration.exception.CustomUserValidationException;
import me.grudzien.patryk.utils.factory.FactoryProvider;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
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
import static me.grudzien.patryk.utils.common.Predicates.isListEmpty;
import static me.grudzien.patryk.utils.common.Predicates.isListNotEmpty;
import static me.grudzien.patryk.utils.factory.FactoryType.JWT_AUTH_RESPONSE;
import static me.grudzien.patryk.utils.mapping.ObjectDecoder.authRequestDecoder;
import static me.grudzien.patryk.utils.validation.CustomValidator.getTranslatedValidationResult;

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
        checkNotNull(authenticationManager, "authenticationManager cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");
        checkNotNull(jwtAuthenticationRequestMapper, "jwtAuthenticationRequestMapper cannot be null!");

        this.authenticationManager = authenticationManager;
        this.localeMessagesCreator = localeMessagesCreator;
        this.requestsDecoder = requestsDecoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtAuthenticationRequestMapper = jwtAuthenticationRequestMapper;
    }

	@Override
	public JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		final JwtAuthenticationRequest decodedAuthRequest = authRequestDecoder().apply(authenticationRequest, jwtAuthenticationRequestMapper);
		final List<String> translatedValidationResult = getTranslatedValidationResult(decodedAuthRequest, localeMessagesCreator);
		return Match(translatedValidationResult).of(
		        Case($(isListNotEmpty()), () -> {
                    log.error("Validation errors present during login.");
                    // TODO:
                    throw new CustomUserValidationException(localeMessagesCreator.buildLocaleMessage("login-form-validation-errors"),
                                                            translatedValidationResult);
                }),
                Case($(isListEmpty()), () -> {
                    final String email = decodedAuthRequest.getEmail();
                    log.info("Login request is correct. Starting authenticating the user with ({}) email.", email);
                    return authenticateUser(authenticationRequest)
                            .map(authentication -> createSuccessResponse(decodedAuthRequest, device))
                            .orElse((JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE).create(FAILED_RESPONSE));
                })
        );
	}

    private JwtAuthenticationResponse createSuccessResponse(final JwtAuthenticationRequest decodedAuthRequest, final Device device) {
        return (JwtAuthenticationResponse) FactoryProvider.getFactory(JWT_AUTH_RESPONSE).create(
                SUCCESS_RESPONSE,
                jwtTokenService.generateAccessToken(decodedAuthRequest, device),
                jwtTokenService.generateRefreshToken(decodedAuthRequest, device)
        );
    }

    /**
	 * Authenticates the user. If something is wrong, an
	 * {@link BadCredentialsAuthenticationException} or
	 * {@link UserDisabledAuthenticationException} will be thrown
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
		 * authenticationManager.authenticate() returns fully authenticated (Authentication) object
		 * (it includes credentials and granted authorities if successful).
		 * It attempts to authenticate the passed (Authentication) object, returning a fully populated "Authentication" object.
		 */
		return Try.of(() -> Optional.ofNullable(authenticationManager.authenticate(StringUtils.isEmpty(idToken) ?
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
}
