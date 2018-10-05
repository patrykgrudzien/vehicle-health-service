package me.grudzien.patryk.oauth2.repository;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.oauth2.utils.CacheHelper;
import me.grudzien.patryk.oauth2.utils.URLParser;

import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

/**
 * For storing the {@link org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest},
 * Spring provides a "session-based" implementation:
 * {@link org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository}
 * of:
 * {@link org.springframework.security.oauth2.client.web.AuthorizationRequestRepository}.
 *
 * That will not work if my backend is stateless, because there'll be no session.
 * Implementation below is a "cache-based".
 */
@Log4j2
@Repository
public class CacheBasedOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	public static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME = "oauth2-authorization-request-cache-name";
	private static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY = "oauth2_authorization_request_cache_key";
	public static final String SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY = "sso_button_click_event_endpoint_url_cache_key";

	private final CacheHelper cacheHelper;

	@Autowired
	public CacheBasedOAuth2AuthorizationRequestRepository(final CacheHelper cacheHelper) {
		Preconditions.checkNotNull(cacheHelper, "cacheHelper cannot be null!");
		this.cacheHelper = cacheHelper;
	}

	@Override
	public void saveAuthorizationRequest(final OAuth2AuthorizationRequest authorizationRequest, final HttpServletRequest request,
	                                     final HttpServletResponse response) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< saveAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		Preconditions.checkNotNull(request, "response cannot be null!");

		if (authorizationRequest == null) {
			this.evictOAuth2AuthorizationRequestCache();
			return;
		}
		/**
		 * Saving additional cache which will be required in:
		 * {@link me.grudzien.patryk.oauth2.service.CustomOAuth2UserService#determineFlowAndPreparePrincipal(
		 * org.springframework.security.oauth2.core.user.OAuth2User, org.springframework.security.oauth2.client.registration.ClientRegistration)}
		 * to decide if user should be (log in) or (register).
		 */
		URLParser.retrieveEndpointFromURL(request.getHeader(HttpHeaders.REFERER))
		         .ifPresent(endpoint -> cacheHelper.saveCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY, endpoint));
		cacheHelper.saveCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY, authorizationRequest);
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< loadAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		return cacheHelper.loadCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY, () -> null);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< removeAuthorizationRequest()");
		return this.loadAuthorizationRequest(request);
	}

	public void evictOAuth2AuthorizationRequestCache() {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< evictOAuth2AuthorizationRequestCache()");
		cacheHelper.evictCacheByNameAndKey(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY);
	}
}

