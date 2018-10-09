package me.grudzien.patryk.oauth2.handlers;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;
import static java.util.function.Predicate.isEqual;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.service.CustomOAuth2UserService.UNKNOWN_OAUTH2_USER_ERROR_CODE;
import static me.grudzien.patryk.oauth2.service.CustomOidcUserService.UNKNOWN_OIDC_USER_ERROR_CODE;
import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;

@Log4j2
@Component
public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public CustomOAuth2AuthenticationFailureHandler(final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository,
	                                                final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(cacheBasedOAuth2AuthorizationRequestRepository, "cacheBasedOAuth2AuthorizationRequestRepository cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.cacheBasedOAuth2AuthorizationRequestRepository = cacheBasedOAuth2AuthorizationRequestRepository;
		this.propertiesKeeper = propertiesKeeper;
	}

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception)
			throws IOException, ServletException {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< onAuthenticationFailure()");
		cacheBasedOAuth2AuthorizationRequestRepository.evictOAuth2AuthorizationRequestCache();
		final OAuth2Error oAuth2Error = ((OAuth2AuthenticationException) exception).getError();
		super.setDefaultFailureUrl(defineFailureTargetURL(oAuth2Error));
		super.onAuthenticationFailure(request, response, exception);
	}

	private String defineFailureTargetURL(final OAuth2Error oAuth2Error) {
		return Match(oAuth2Error.getErrorCode()).of(
				Case($(isEqual(AccountStatus.ALREADY_EXISTS.name())), propertiesKeeper.oAuth2().USER_ACCOUNT_ALREADY_EXISTS),
				Case($(isEqual(AccountStatus.NOT_FOUND.name())), propertiesKeeper.oAuth2().USER_NOT_FOUND),
				Case($(isIn(UNKNOWN_OAUTH2_USER_ERROR_CODE, UNKNOWN_OIDC_USER_ERROR_CODE)), propertiesKeeper.oAuth2().FAILURE_TARGET_URL)
		);
	}
}
