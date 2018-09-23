package me.grudzien.patryk.config.caching;

import net.sf.ehcache.config.CacheConfiguration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.service.vehicle.impl.VehicleServiceImpl;

@Configuration
@EnableCaching
public class CachingConfig {

	/**
	 * {@link org.springframework.cache.CacheManager}
	 */
	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheManager());
	}

	@Bean(destroyMethod = "shutdown")
	public net.sf.ehcache.CacheManager ehCacheManager() {
		final CacheConfiguration vehicleMileageCacheConfiguration = namedCacheConfiguration(VehicleServiceImpl.VEHICLE_MILEAGE_CACHE_NAME);
		final CacheConfiguration principalUserCacheConfiguration = namedCacheConfiguration(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME);
		final CacheConfiguration oauth2AuthorizationRequestCacheConfiguration = namedCacheConfiguration(
				CacheBasedOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_CACHE_NAME);

		final net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
		configuration.addCache(vehicleMileageCacheConfiguration);
		configuration.addCache(principalUserCacheConfiguration);
		configuration.addCache(oauth2AuthorizationRequestCacheConfiguration);

		return new net.sf.ehcache.CacheManager(configuration);
	}

	private CacheConfiguration namedCacheConfiguration(final String cacheName) {
		final CacheConfiguration cacheConfiguration = new CacheConfiguration();
		// Sets the name of the cache
		cacheConfiguration.setName(cacheName);
		// Maximum number of entries that may be stored in local heap memory store
		cacheConfiguration.setMaxEntriesLocalHeap(5);
		// Sets whether elements are eternal. If eternal (wieczny), timeouts are ignored and the element is never expired. False by default.
		cacheConfiguration.setEternal(false); // just for visibility that there is such property.
		// Sets the time to idle for an element before it expires. Is only used if the element is not eternal.
		// Parameter: (timeToLiveSeconds) -> the default amount of time to live for an element from its creation date.
		cacheConfiguration.setTimeToLiveSeconds(300);
		// Sets the time to idle for an element before it expires. Is only used if the element is not eternal.
		// Parameter: (timeToIdleSeconds) -> the default amount of time to live for an element from its last accessed or modified date.
		cacheConfiguration.setTimeToIdleSeconds(0);
		// Sets the eviction policy. An invalid argument will set it to LRU.
		// LRU - Least Recently Used
		// LFU - Least Frequently Used
		// FIFO - First In First Out
		cacheConfiguration.setMemoryStoreEvictionPolicy("LFU");
		// Sets the transactionalMode
		cacheConfiguration.setTransactionalMode("off");

		return cacheConfiguration;
	}
}
