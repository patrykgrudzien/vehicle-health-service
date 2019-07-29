package me.grudzien.patryk.oauth2.repository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.oauth2.utils.URLParser;

import static com.google.common.base.Preconditions.checkNotNull;

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
@Slf4j
@Repository
public class CacheBasedOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	public static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME = "oauth2-authorization-request-cache-name";
	private static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY = "oauth2_authorization_request_cache_key";
	public static final String SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY = "sso_button_click_event_endpoint_url_cache_key";

	private final CacheManagerHelper cacheManagerHelper;

	@Autowired
	public CacheBasedOAuth2AuthorizationRequestRepository(final CacheManagerHelper cacheManagerHelper) {
		checkNotNull(cacheManagerHelper, "cacheManagerHelper cannot be null!");
		this.cacheManagerHelper = cacheManagerHelper;
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
		log.info(">>>> OAUTH2 <<<< loadAuthorizationRequest()");
		checkNotNull(request, "request cannot be null!");

		final String stateParameter = this.getStateParameter(request);
		log.info(">>>> OAUTH2 <<<< stateParameter == null ? -> ({})", stateParameter == null);
		if (stateParameter == null) {
			return null;
		}
		return cacheManagerHelper.getCacheValue(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY, OAuth2AuthorizationRequest.class)
                                 .orElse(null);
	}

	@Override
	public void saveAuthorizationRequest(final OAuth2AuthorizationRequest authorizationRequest, final HttpServletRequest request,
	                                     final HttpServletResponse response) {
		log.info(">>>> OAUTH2 <<<< saveAuthorizationRequest()");
		checkNotNull(request, "request cannot be null!");
		checkNotNull(request, "response cannot be null!");

		if (authorizationRequest == null) {
			this.evictOAuth2AuthorizationRequestCache();
			return;
		}
		final String state = authorizationRequest.getState();
		Assert.hasText(state, "authorizationRequest.state cannot be empty");
		/**
		 * Saving additional cache which will be required in:
		 * {@link me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator#determineFlowBasedOnUrl(String)}
		 * to decide if user should be (log in) or (register).
		 */
		URLParser.retrieveEndpointFromURL(request.getHeader(HttpHeaders.REFERER))
		         .ifPresent(endpoint -> cacheManagerHelper.putIntoCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, SSO_BUTTON_CLICK_EVENT_ENDPOINT_URL_CACHE_KEY, endpoint));
		cacheManagerHelper.putIntoCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY, authorizationRequest);
	}

	/**
	 * @since 5.1 - it's been deprecated !!!
	 */
	@Deprecated
	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request) {
		log.info(">>>> OAUTH2 <<<< removeAuthorizationRequest() !DEPRECATED!");
		checkNotNull(request, "request cannot be null!");

		final String stateParameter = this.getStateParameter(request);
		log.info(">>>> OAUTH2 <<<< stateParameter == null ? -> ({})", stateParameter == null);
		if (stateParameter == null) {
			return null;
		}
		return this.loadAuthorizationRequest(request);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request, final HttpServletResponse response) {
		log.info(">>>> OAUTH2 <<<< removeAuthorizationRequest() !CORRECT!");
		checkNotNull(response, "response cannot be null!");

		return this.removeAuthorizationRequest(request);
	}

	public void evictOAuth2AuthorizationRequestCache() {
		log.info(">>>> OAUTH2 <<<< evictOAuth2AuthorizationRequestCache()");
		cacheManagerHelper.evictCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME, OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY);
	}

	/**
	 * Gets the state parameter from the {@link HttpServletRequest}
	 * @param request the request to use
	 * @return the state parameter or null if not found
	 */
	private String getStateParameter(final HttpServletRequest request) {
		return request.getParameter(OAuth2ParameterNames.STATE);
	}
}

