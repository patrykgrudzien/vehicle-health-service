package me.grudzien.patryk.oauth2.controller;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.responses.ExceptionResponse;
import me.grudzien.patryk.domain.dto.responses.SuccessResponse;
import me.grudzien.patryk.handler.web.HttpResponseHandler;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
import me.grudzien.patryk.util.jwt.JwtTokenUtil;

import static me.grudzien.patryk.domain.enums.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;

@Log4j2
@RestController
public class GoogleOAuth2Controller {

	private final HttpResponseHandler httpResponseHandler;

	@Autowired
	public GoogleOAuth2Controller(final HttpResponseHandler httpResponseHandler) {
		Preconditions.checkNotNull(httpResponseHandler, "httpResponseHandler cannot be null!");
		this.httpResponseHandler = httpResponseHandler;
	}

	@GetMapping("${custom.properties.endpoints.oauth2.user-not-found}")
	public ResponseEntity<ExceptionResponse> userNotFound() {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

    @GetMapping("${custom.properties.endpoints.oauth2.user-account-is-locked}")
    public ResponseEntity<ExceptionResponse> userAccountIsLocked() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_ACCOUNT_IS_LOCKED), HttpStatus.FORBIDDEN);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.user-is-disabled}")
    public ResponseEntity<ExceptionResponse> userIsDisabled() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_IS_DISABLED), HttpStatus.FORBIDDEN);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.user-account-is-expired}")
    public ResponseEntity<ExceptionResponse> userAccountIsExpired() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.USER_ACCOUNT_IS_EXPIRED), HttpStatus.FORBIDDEN);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.user-account-already-exists}")
    public ResponseEntity<ExceptionResponse> userAccountAlreadyExists() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.ALREADY_EXISTS), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.credentials-have-expired}")
    public ResponseEntity<ExceptionResponse> credentialsHaveExpired() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.CREDENTIALS_HAVE_EXPIRED), HttpStatus.FORBIDDEN);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.jwt-token-not-found}")
    public ResponseEntity<ExceptionResponse> jwtTokenNotFound() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.JWT_TOKEN_NOT_FOUND), HttpStatus.FORBIDDEN);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.registration-provider-mismatch}")
    public ResponseEntity<ExceptionResponse> registrationProviderMismatch() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.REGISTRATION_PROVIDER_MISMATCH), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("${custom.properties.endpoints.oauth2.bad-credentials}")
    public ResponseEntity<ExceptionResponse> badCredentials() {
        return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
    }

	@GetMapping("${custom.properties.endpoints.oauth2.user-logged-in-using-google}")
	public ResponseEntity<SuccessResponse> userLoggedInUsingGoogle(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) final String token,
	                                                               final HttpServletResponse httpServletResponse) {
	    final Tuple2<String, String> additionalParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, token);
		httpResponseHandler.redirectUserTo(USER_LOGGED_IN_USING_GOOGLE, httpServletResponse, additionalParameters);
		return new ResponseEntity<>(SuccessResponse.buildBodyMessage(AccountStatus.LOGGED) , HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.exchange-short-lived-token}")
	public ResponseEntity<JwtAuthenticationResponse> exchangeShortLivedTokenForLonger(final Device device) {
		final JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(JwtAuthenticationResponse.Builder()
		                                                  .accessToken(JwtTokenUtil.Creator.generateAccessToken(jwtUser, device))
		                                                  .refreshToken(JwtTokenUtil.Creator.generateRefreshToken(jwtUser))
		                                                  .isSuccessful(Boolean.TRUE)
		                                                  .build());
	}

	@GetMapping("${custom.properties.endpoints.oauth2.user-registered-using-google}")
	public ResponseEntity<SuccessResponse> userRegisteredUsingGoogle() {
		return new ResponseEntity<>(SuccessResponse.buildBodyMessage(AccountStatus.REGISTERED), HttpStatus.OK);
	}

	@GetMapping("${custom.properties.endpoints.oauth2.failure-target-url}")
	public ResponseEntity<ExceptionResponse> failureTargetUrl() {
		return new ResponseEntity<>(ExceptionResponse.buildBodyMessage(AccountStatus.OAUTH2_FLOW_ERROR),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
