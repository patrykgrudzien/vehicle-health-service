package me.grudzien.patryk.oauth2;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.ObjectUtils;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.Base64;
import java.util.stream.Stream;

import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.oauth2.utils.HttpCookieUtils;

/**
 * For storing the {@link org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest},
 * Spring provides a "session-based" implementation:
 * {@link org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository}
 * of:
 * {@link org.springframework.security.oauth2.client.web.AuthorizationRequestRepository}.
 *
 * That will not work if my backend is stateless, because there'll be no session.
 * Implementation below is a "cookie-based".
 */
@Log4j2
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	private static final String AUTHORIZATION_REQUEST_COOKIE_NAME = HttpCookieOAuth2AuthorizationRequestRepository.class.getSimpleName() + ".AUTHORIZATION_REQUEST";
	public static final String CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME = "CUSTOM_REDIRECT_URI";

	private final int cookieExpirySecs;

	/**
	 * @param propertiesKeeper created in:
	 * {@link me.grudzien.patryk.config.security.SecurityConfig}
	 */
	public HttpCookieOAuth2AuthorizationRequestRepository(final PropertiesKeeper propertiesKeeper) {
		log.info(OAUTH2_MARKER, "Custom implementation of -> {}", this.getClass().getSimpleName());
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		cookieExpirySecs = propertiesKeeper.oAuth2().SHORT_LIVED_MILLIS / 1000;
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, "loadAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		return HttpCookieUtils.fetchCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME)
		                      .map(Utils::deserialize)
		                      .orElse(null);
	}

	/**
	 * Save authorization request in cookie.
	 */
	@Override
	public void saveAuthorizationRequest(final OAuth2AuthorizationRequest authorizationRequest, final HttpServletRequest request,
	                                     final HttpServletResponse response) {
		log.info(OAUTH2_MARKER, "saveAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		Preconditions.checkNotNull(request, "response cannot be null!");

		if (authorizationRequest == null) {
			deleteCookies(request, response);
			return;
		}

		Cookie cookie = new Cookie(AUTHORIZATION_REQUEST_COOKIE_NAME, Marshaller.serialize(authorizationRequest));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(cookieExpirySecs);
		response.addCookie(cookie);

		final String customRedirectUri = request.getParameter(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME);
		if (StringUtils.isNotBlank(customRedirectUri)) {
			cookie = new Cookie(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME, customRedirectUri);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(cookieExpirySecs);
			response.addCookie(cookie);
		}
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, "removeAuthorizationRequest()");
		return loadAuthorizationRequest(request);
	}

	/**
	 * Delete related cookies.
	 */
	public static void deleteCookies(final HttpServletRequest request, final HttpServletResponse response) {
		log.info(OAUTH2_MARKER, "deleteCookies()");
		final Cookie[] cookies = request.getCookies();
		if (!ObjectUtils.isEmpty(cookies)) {
			Stream.of(cookies)
			      .filter(cookie -> cookie.getName().equals(AUTHORIZATION_REQUEST_COOKIE_NAME) || cookie.getName().equals(CUSTOM_REDIRECT_URI_COOKIE_PARAM_NAME))
			      .findFirst()
			      .ifPresent(cookie -> {
			      	cookie.setValue("");
			      	cookie.setPath("/");
			      	cookie.setMaxAge(0);
			      	response.addCookie(cookie);
			      });
		}
	}

	private static class Utils {

		static OAuth2AuthorizationRequest deserialize(final Cookie cookie) {
			return Marshaller.deserialize(cookie.getValue());
		}
	}

	private static class Marshaller {

		static String serialize(final Serializable serializableObject) {
			return Base64.getUrlEncoder()
			             .encodeToString(SerializationUtils.serialize(serializableObject));
		}

		static <T> T deserialize(final String serializedObject) {
			return SerializationUtils.deserialize(Base64.getUrlDecoder().decode(serializedObject));
		}
	}
}

