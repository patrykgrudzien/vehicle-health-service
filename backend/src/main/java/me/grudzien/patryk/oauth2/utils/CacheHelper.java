package me.grudzien.patryk.oauth2.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.function.Supplier;

import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

@Log4j2
@Component
public class CacheHelper {

	private final CacheManager cacheManager;

	@Autowired
	public CacheHelper(final CacheManager cacheManager) {
		Preconditions.checkNotNull(cacheManager, "cacheManager cannot be null!");
		this.cacheManager = cacheManager;
	}

	public void saveCache(final String cacheName, final String cacheKey, @Nullable final Object value) {
		Optional.ofNullable(cacheManager.getCache(cacheName))
		        .ifPresent(cache -> {
		        	cache.put(cacheKey, value);
		        	log.info(OAUTH2_MARKER, "{} saved inside ({}) cache.", String.valueOf(value), cacheKey);
		        });
	}

	@SuppressWarnings("unchecked")
	public <T> T loadCache(final String cacheName, final String cacheKey, final Supplier<T> orElseGet) {
		return (T) Optional.ofNullable(cacheManager.getCache(cacheName))
		                   .map(cache -> Optional.ofNullable(cache.get(cacheKey))
		                                     .map(Cache.ValueWrapper::get)
		                                     .orElseGet(orElseGet))
		                   .orElseGet(orElseGet);
	}

	public void evictCacheByNameAndKey(final String cacheName, final String cacheKey) {
		Optional.ofNullable(cacheManager.getCache(cacheName))
		        .ifPresent(cache -> {
			        cache.evict(cacheKey);
			        log.info(OAUTH2_MARKER, "Cache ({}) evicted.", cacheKey);
		        });
	}
}
