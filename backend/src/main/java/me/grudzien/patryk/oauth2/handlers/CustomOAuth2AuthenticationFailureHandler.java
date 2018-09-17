package me.grudzien.patryk.oauth2.handlers;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;

@Log4j2
@Component
public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository;

	@Autowired
	public CustomOAuth2AuthenticationFailureHandler(final CacheBasedOAuth2AuthorizationRequestRepository cacheBasedOAuth2AuthorizationRequestRepository) {
		Preconditions.checkNotNull(cacheBasedOAuth2AuthorizationRequestRepository, "cacheBasedOAuth2AuthorizationRequestRepository cannot be null!");
		this.cacheBasedOAuth2AuthorizationRequestRepository = cacheBasedOAuth2AuthorizationRequestRepository;
	}

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception)
			throws IOException, ServletException {
		cacheBasedOAuth2AuthorizationRequestRepository.evictOAuth2AuthorizationRequestCache();
		super.onAuthenticationFailure(request, response, exception);
	}
}
