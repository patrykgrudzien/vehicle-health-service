package me.grudzien.patryk.oauth2.handlers;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;
import me.grudzien.patryk.utils.web.CustomURLBuilder;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

@Log4j2
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public static final String SHORT_LIVED_AUTH_TOKEN_NAME = "shortLivedAuthToken";

	private final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public CustomOAuth2AuthenticationSuccessHandler(final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository,
	                                                final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(cacheBasedOAuth2AuthorizationRequestRepository, "cacheBasedOAuth2AuthorizationRequestRepository cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.cacheBasedOAuth2AuthorizationRequestRepository = cacheBasedOAuth2AuthorizationRequestRepository;
		this.propertiesKeeper = propertiesKeeper;
	}

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< determineTargetUrl()");
		final CustomOAuth2OidcPrincipalUser customOidcPrincipalUser = (CustomOAuth2OidcPrincipalUser) SecurityContextHolder.getContext().getAuthentication()
                                                                                                                           .getPrincipal();
		cacheBasedOAuth2AuthorizationRequestRepository.evictOAuth2AuthorizationRequestCache();
		return defineSuccessTargetURL(customOidcPrincipalUser);
	}

	private String defineSuccessTargetURL(final CustomOAuth2OidcPrincipalUser customOAuth2OidcPrincipalUser) {
		final AccountStatus userAccountStatus = customOAuth2OidcPrincipalUser.getAccountStatus();
		return Match(userAccountStatus).of(
				Case($(is(AccountStatus.REGISTERED)), () -> propertiesKeeper.oAuth2().USER_REGISTERED_USING_GOOGLE),
				Case($(is(AccountStatus.LOGGED)), () -> {
					final String shortLivedAuthToken = JwtTokenUtil.Creator.generateShortLivedToken(customOAuth2OidcPrincipalUser);
					final Tuple2<String, String> additionalUrlParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, shortLivedAuthToken);
					return CustomURLBuilder.buildURL(propertiesKeeper.oAuth2().USER_LOGGED_IN_USING_GOOGLE,
                                                     CustomURLBuilder.AdditionalParamsDelimiterType.REQUEST_PARAM, additionalUrlParameters);
				}),
				Case($(), StringUtils.EMPTY)
		);
	}
}
