package me.grudzien.patryk.oauth2.repository;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

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
@CacheConfig(cacheNames = CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME)
public class CacheBasedOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	public static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME = "oauth2-authorization-request";
	private static final String OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY = "oauth2_authorization_request_key";
	private static final String REFERENCE_URL_HEADER_NAME = "referer";

	private final CacheManager cacheManager;

	@Autowired
	public CacheBasedOAuth2AuthorizationRequestRepository(final CacheManager cacheManager) {
		Preconditions.checkNotNull(cacheManager, "cacheManager cannot be null!");
		this.cacheManager = cacheManager;
	}

	@Override
	public void saveAuthorizationRequest(final OAuth2AuthorizationRequest authorizationRequest, final HttpServletRequest request,
	                                     final HttpServletResponse response) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< saveAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		Preconditions.checkNotNull(request, "response cannot be null!");

		// TODO: FIX ME (in purpose to determine where request comes from -> "/login" or "/register") for the future implementation
		log.info(OAUTH2_MARKER, "Request comes from ({}).", request.getHeader(REFERENCE_URL_HEADER_NAME));

		if (authorizationRequest == null) {
			this.evictOAuth2AuthorizationRequestCache();
			return;
		}
		Optional.ofNullable(cacheManager.getCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME))
		        .ifPresent(cache -> {
		        	cache.put(OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY, authorizationRequest);
		        	log.info(OAUTH2_MARKER, "OAuth2AuthorizationRequest saved inside ({}) cache.", OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY);
		        });
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< loadAuthorizationRequest()");
		Preconditions.checkNotNull(request, "request cannot be null!");
		return (OAuth2AuthorizationRequest) Optional.ofNullable(cacheManager.getCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME))
		                                            .map(cache -> Optional.ofNullable(cache.get(OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY))
		                                                                  .map(Cache.ValueWrapper::get)
		                                                                  .orElse(null))
		                                            .orElse(null);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(final HttpServletRequest request) {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< removeAuthorizationRequest()");
		return this.loadAuthorizationRequest(request);
	}

	public void evictOAuth2AuthorizationRequestCache() {
		log.info(OAUTH2_MARKER, ">>>> OAUTH2 <<<< evictOAuth2AuthorizationRequestCache()");
		Optional.ofNullable(cacheManager.getCache(OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME))
		        .ifPresent(cache -> {
		        	cache.evict(OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY);
		        	log.info(OAUTH2_MARKER, "Cache ({}) evicted.", OAUTH2_AUTHORIZATION_REQUEST_CACHE_KEY);
		        });
	}
}

