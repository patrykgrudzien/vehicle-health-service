package me.grudzien.patryk.oauth2.handlers;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;

@Log4j2
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository;

	@Autowired
	public CustomOAuth2AuthenticationSuccessHandler(final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository) {
		Preconditions.checkNotNull(cacheBasedOAuth2AuthorizationRequestRepository, "cacheBasedOAuth2AuthorizationRequestRepository cannot be null!");
		this.cacheBasedOAuth2AuthorizationRequestRepository = cacheBasedOAuth2AuthorizationRequestRepository;
	}

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {

		// 1. Get principal user

		// 2. Create short lived auth token

		// 3. Prepare target url

		// 4. Delete cache

		// 5. return target url + short lived token

		return super.determineTargetUrl(request, response);
	}
}
