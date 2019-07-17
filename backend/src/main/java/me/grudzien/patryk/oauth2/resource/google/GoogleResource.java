package me.grudzien.patryk.oauth2.resource.google;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.utils.web.model.SuccessResponse;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;

import static me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationSuccessHandler.SHORT_LIVED_AUTH_TOKEN_NAME;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.BAD_CREDENTIALS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.CREDENTIALS_HAVE_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.EXCHANGE_SHORT_LIVED_TOKEN;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.FAILURE_TARGET_URL;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.GOOGLE_OAUTH2_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.JWT_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.REGISTRATION_PROVIDER_MISMATCH;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_ALREADY_EXISTS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_LOCKED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_IS_DISABLED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_REGISTERED_USING_GOOGLE;

/**
 *
 */
@RequestMapping(GOOGLE_OAUTH2_RESOURCE_ROOT)
public interface GoogleResource {

    /**
     *
     * @return
     */
    @GetMapping(USER_NOT_FOUND)
    ResponseEntity<ExceptionResponse> userNotFound();

    /**
     *
     * @return
     */
    @GetMapping(USER_ACCOUNT_IS_LOCKED)
    ResponseEntity<ExceptionResponse> userAccountIsLocked();

    /**
     *
     * @return
     */
    @GetMapping(USER_IS_DISABLED)
    ResponseEntity<ExceptionResponse> userIsDisabled();

    /**
     *
     * @return
     */
    @GetMapping(USER_ACCOUNT_IS_EXPIRED)
    ResponseEntity<ExceptionResponse> userAccountIsExpired();

    /**
     *
     * @return
     */
    @GetMapping(USER_ACCOUNT_ALREADY_EXISTS)
    ResponseEntity<ExceptionResponse> userAccountAlreadyExists();

    /**
     *
     * @return
     */
    @GetMapping(CREDENTIALS_HAVE_EXPIRED)
    ResponseEntity<ExceptionResponse> credentialsHaveExpired();

    /**
     *
     * @return
     */
    @GetMapping(JWT_TOKEN_NOT_FOUND)
    ResponseEntity<ExceptionResponse> jwtTokenNotFound();

    /**
     *
     * @return
     */
    @GetMapping(REGISTRATION_PROVIDER_MISMATCH)
    ResponseEntity<ExceptionResponse> registrationProviderMismatch();

    /**
     *
     * @return
     */
    @GetMapping(BAD_CREDENTIALS)
    ResponseEntity<ExceptionResponse> badCredentials();

    /**
     *
     * @param token
     * @return
     */
    @GetMapping(USER_LOGGED_IN_USING_GOOGLE)
    ResponseEntity<SuccessResponse> userLoggedInUsingGoogle(@RequestParam(SHORT_LIVED_AUTH_TOKEN_NAME) String token);

    /**
     *
     * @param device
     * @return
     */
    @GetMapping(EXCHANGE_SHORT_LIVED_TOKEN)
    ResponseEntity<JwtAuthenticationResponse> exchangeShortLivedTokenForLonger(Device device);

    /**
     *
     * @return
     */
    @GetMapping(USER_REGISTERED_USING_GOOGLE)
    ResponseEntity<SuccessResponse> userRegisteredUsingGoogle();

    /**
     *
     * @return
     */
    @GetMapping(FAILURE_TARGET_URL)
    ResponseEntity<ExceptionResponse> failureTargetUrl();
}
