package me.grudzien.patryk.oauth2.handler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.utils.web.MvcPattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;
import static java.util.function.Predicate.isEqual;

import static me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.BAD_CREDENTIALS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.CREDENTIALS_HAVE_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.FAILURE_TARGET_URL;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.GOOGLE_OAUTH2_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.JWT_TOKEN_NOT_FOUND;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.REGISTRATION_PROVIDER_MISMATCH;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_ALREADY_EXISTS;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_EXPIRED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_ACCOUNT_IS_LOCKED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_IS_DISABLED;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_NOT_FOUND;
import static me.grudzien.patryk.oauth2.service.CustomOAuth2UserService.UNKNOWN_OAUTH2_USER_ERROR_CODE;
import static me.grudzien.patryk.oauth2.service.CustomOidcUserService.UNKNOWN_OIDC_USER_ERROR_CODE;

@Slf4j
@Component
public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CacheBasedOAuth2AuthorizationRequestRepository requestRepository;

	@Autowired
	public CustomOAuth2AuthenticationFailureHandler(final CacheBasedOAuth2AuthorizationRequestRepository requestRepository) {
		checkNotNull(requestRepository, "requestRepository cannot be null!");
		this.requestRepository = requestRepository;
	}

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception)
            throws IOException, ServletException {
		log.info(">>>> OAUTH2 <<<< onAuthenticationFailure()");
		requestRepository.evictOAuth2AuthorizationRequestCache();
		final OAuth2Error oAuth2Error = ((OAuth2AuthenticationException) exception).getError();
		super.setDefaultFailureUrl(defineFailureTargetURL(oAuth2Error));
		super.onAuthenticationFailure(request, response, exception);
	}

	private String defineFailureTargetURL(final OAuth2Error oAuth2Error) {
        return Match(oAuth2Error.getErrorCode()).of(
                Case($(isEqual(AccountStatus.NOT_FOUND.name())), createUriTo(USER_NOT_FOUND)),
                Case($(isEqual(AccountStatus.USER_ACCOUNT_IS_LOCKED.name())), createUriTo(USER_ACCOUNT_IS_LOCKED)),
                Case($(isEqual(AccountStatus.USER_IS_DISABLED.name())), createUriTo(USER_IS_DISABLED)),
                Case($(isEqual(AccountStatus.USER_ACCOUNT_IS_EXPIRED.name())), createUriTo(USER_ACCOUNT_IS_EXPIRED)),
                Case($(isEqual(AccountStatus.ALREADY_EXISTS.name())), createUriTo(USER_ACCOUNT_ALREADY_EXISTS)),
                Case($(isEqual(AccountStatus.CREDENTIALS_HAVE_EXPIRED.name())), createUriTo(CREDENTIALS_HAVE_EXPIRED)),
                Case($(isEqual(AccountStatus.JWT_TOKEN_NOT_FOUND.name())), createUriTo(JWT_TOKEN_NOT_FOUND)),
                Case($(isEqual(AccountStatus.REGISTRATION_PROVIDER_MISMATCH.name())), createUriTo(REGISTRATION_PROVIDER_MISMATCH)),
                Case($(isEqual(AccountStatus.BAD_CREDENTIALS.name())), createUriTo(BAD_CREDENTIALS)),
				Case($(isIn(UNKNOWN_OAUTH2_USER_ERROR_CODE, UNKNOWN_OIDC_USER_ERROR_CODE)), createUriTo(FAILURE_TARGET_URL))
		);
	}

	// TODO: extract somewhere?
	private String createUriTo(final String resource) {
	    return MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, resource);
    }
}
