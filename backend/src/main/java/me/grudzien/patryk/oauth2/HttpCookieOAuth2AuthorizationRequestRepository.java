package me.grudzien.patryk.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.oauth2.utils.HttpCookieUtils;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	private static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "custom_oauth2_authorization_request";
	public static final String CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME = "custom_redirect_uri";

	private final int cookieExpirySecs;

	public HttpCookieOAuth2AuthorizationRequestRepository(final CustomApplicationProperties customApplicationProperties) {
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		cookieExpirySecs = customApplicationProperties.getJwt().getShortLivedMillis() / 1000;
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
		Preconditions.checkNotNull(request, "request cannot be null!");
		return HttpCookieUtils.fetchCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME).map(this::deserialize).orElse(null);
	}

	/**
	 * Save authorization request in cookie
	 */
	@Override
	public void saveAuthorizationRequest(final OAuth2AuthorizationRequest authorizationRequest, final HttpServletRequest request,
	                                     final HttpServletResponse response) {
		Preconditions.checkNotNull(request, "request cannot be null!");
		Preconditions.checkNotNull(request, "response cannot be null!");

		if (authorizationRequest == null) {
			deleteCookies(request, response);
			return;
		}

		Cookie cookie = new Cookie(AUTHORIZATION_REQUEST_COOKIE_NAME, LecUtils.serialize(authorizationRequest));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(cookieExpirySecs);
		response.addCookie(cookie);

		final String lemonRedirectUri = request.getParameter(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME);
		if (StringUtils.isNotBlank(lemonRedirectUri)) {
			cookie = new Cookie(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME, lemonRedirectUri);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(cookieExpirySecs);
			response.addCookie(cookie);
		}
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request) {
		return loadAuthorizationRequest(request);
	}

	/**
	 * Utility for deleting related cookies
	 */
	public static void deleteCookies(final HttpServletRequest request, final HttpServletResponse response) {

		final Cookie[] cookies = request.getCookies();

		if (cookies != null && cookies.length > 0) {
			for (final Cookie cooky : cookies) {
				if (cooky.getName().equals(AUTHORIZATION_REQUEST_COOKIE_NAME) || cooky.getName().equals(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME)) {
					cooky.setValue("");
					cooky.setPath("/");
					cooky.setMaxAge(0);
					response.addCookie(cooky);
				}
			}
		}
	}

	private OAuth2AuthorizationRequest deserialize(final Cookie cookie) {
		return LecUtils.deserialize(cookie.getValue());
	}
}

