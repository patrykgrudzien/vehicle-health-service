package me.grudzien.patryk.oauth2.handlers;

import io.vavr.Tuple2;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.AdditionalParamsDelimiterType.REQUEST_PARAM;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.buildURL;

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

		final CustomOAuth2OidcPrincipalUser customOidcPrincipalUser = (CustomOAuth2OidcPrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String shortLivedAuthToken = JwtTokenUtil.Creator.generateShortLivedToken(customOidcPrincipalUser);

		final Tuple2<String, String> additionalUrlParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, shortLivedAuthToken);
		final String targetUrl = buildURL(propertiesKeeper.oAuth2().SUCCESS_TARGET_URL, REQUEST_PARAM, additionalUrlParameters);

		cacheBasedOAuth2AuthorizationRequestRepository.evictOAuth2AuthorizationRequestCache();
		return targetUrl;
	}
}
