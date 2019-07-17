package me.grudzien.patryk.oauth2.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import java.util.Optional;

@Log4j2
@Component
public class CacheManagerHelper {

	private final CacheManager cacheManager;

	@Autowired
	public CacheManagerHelper(final CacheManager cacheManager) {
		Preconditions.checkNotNull(cacheManager, "cacheManager cannot be null!");
		this.cacheManager = cacheManager;
	}

    private Optional<Cache> getCache(@NonNull final String cacheName) {
	    log.info("Trying to get cache with ({}) name.", cacheName);
	    return Optional.ofNullable(cacheManager.getCache(cacheName));
    }

	public <K, V> void putIntoCache(@NonNull final String cacheName, @NonNull final K cacheKey, @Nullable final V cacheValue) {
	    getCache(cacheName).ifPresent(cache -> {
            cache.put(cacheKey, cacheValue);
            log.info("New value has been put to ({}) cache.", cacheName);
        });
	}

    public <K, T> Optional<T> getCacheValue(@NonNull final String cacheName, @NonNull final K cacheKey, @NonNull final Class<T> returnType) {
	    return getCache(cacheName).map(cache -> {
	        log.info("Returning value from ({}) cache.", cacheName);
	        return cache.get(cacheKey, returnType);
        });
    }

    public void clearAllCache(@NonNull final String cacheName) {
	    getCache(cacheName).ifPresent(cache -> {
            cache.clear();
            log.info("All mappings from the cache ({}) have been removed!", cacheName);
        });
    }

	public <K> void evictCache(@NonNull final String cacheName, @NonNull final K cacheKey) {
	    getCache(cacheName).ifPresent(cache -> {
            cache.evict(cacheKey);
            log.info("Cache ({}) has been evicted!", cacheName);
        });
	}
}
