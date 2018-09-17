package me.grudzien.patryk.oauth2.handlers;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.utils.jwt.JwtTokenUtil;

@Log4j2
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

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

		// TODO

		// 1. Get principal user
		final DefaultOidcUser customOidcPrincipalUser = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// 2. Create short lived auth token
		final String shortLivedAuthToken = JwtTokenUtil.Creator.OAuth2.generateAccessToken(customOidcPrincipalUser);

		// 3. Prepare target url
		final String targetUrl = "/social-login-success?token=" + shortLivedAuthToken;

		// 4. Delete cache
		cacheBasedOAuth2AuthorizationRequestRepository.evictOAuth2AuthorizationRequestCache();

		// 5. return target url + short lived token
		return targetUrl;
	}
}
