package me.grudzien.patryk.oauth2.resource.google.impl;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.utils.web.model.SuccessResponse;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import me.grudzien.patryk.oauth2.resource.google.GoogleResource;
import me.grudzien.patryk.utils.web.HttpLocationHeaderCreator;

import static com.google.common.base.Preconditions.checkNotNull;

import static me.grudzien.patryk.utils.app.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;

@Log4j2
@RestController
public class GoogleResourceImpl implements GoogleResource {

    private final HttpLocationHeaderCreator httpLocationHeaderCreator;
    private final JwtTokenService jwtTokenService;

    public GoogleResourceImpl(final HttpLocationHeaderCreator httpLocationHeaderCreator, final JwtTokenService jwtTokenService) {
        checkNotNull(httpLocationHeaderCreator, "httpLocationHeaderCreator cannot be null!");
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");

        this.httpLocationHeaderCreator = httpLocationHeaderCreator;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public ResponseEntity<ExceptionResponse> userNotFound() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<ExceptionResponse> userAccountIsLocked() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_ACCOUNT_IS_LOCKED), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ExceptionResponse> userIsDisabled() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_IS_DISABLED), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ExceptionResponse> userAccountIsExpired() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_ACCOUNT_IS_EXPIRED), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ExceptionResponse> userAccountAlreadyExists() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.ALREADY_EXISTS), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ExceptionResponse> credentialsHaveExpired() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.CREDENTIALS_HAVE_EXPIRED), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ExceptionResponse> jwtTokenNotFound() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.JWT_TOKEN_NOT_FOUND), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ExceptionResponse> registrationProviderMismatch() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.REGISTRATION_PROVIDER_MISMATCH), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ExceptionResponse> badCredentials() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<SuccessResponse> userLoggedInUsingGoogle(final String token) {
        final Tuple2<String, String> additionalParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, token);
	    // TODO
	    final String redirectionUrl = httpLocationHeaderCreator.createRedirectionUrl(USER_LOGGED_IN_USING_GOOGLE, additionalParameters);
		return new ResponseEntity<>(SuccessResponse.buildBodyMessage(AccountStatus.LOGGED) , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> exchangeShortLivedTokenForLonger(final Device device) {
        final JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final JwtAuthenticationRequest jwtAuthenticationRequest = JwtAuthenticationRequest.Builder()
                                                                                          .email(jwtUser.getEmail())
                                                                                          .password(jwtUser.getPassword())
                                                                                          .build();
        return ResponseEntity.ok(JwtAuthenticationResponse.Builder()
                                                          .accessToken(jwtTokenService.generateAccessToken(jwtAuthenticationRequest, device))
                                                          .refreshToken(jwtTokenService.generateRefreshToken(jwtAuthenticationRequest, device))
                                                          .isSuccessful(Boolean.TRUE)
                                                          .build());
    }

    @Override
    public ResponseEntity<SuccessResponse> userRegisteredUsingGoogle() {
        return new ResponseEntity<>(SuccessResponse.buildBodyMessage(AccountStatus.REGISTERED), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ExceptionResponse> failureTargetUrl() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.OAUTH2_FLOW_ERROR),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }
}