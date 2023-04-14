package lithium.hazelcast;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@Slf4j
public class HazelcastCacheConfiguration extends CachingConfigurerSupport implements NamedInstance {
	
	HazelcastClientProperties cacheProperties;

	Environment environment;

	HazelcastInstance hazelcastInstance;

	@Override
	public Class<?> getClazz() {
		return this.getClass();
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}

	@Bean
	public CacheManager hazelcastCacheManager() throws IOException, IllegalAccessException {
		HazelcastCacheManager manager = new HazelcastCacheManager(hazelcastInstance);
		manager.setDefaultReadTimeout(cacheProperties.getCacheOperationTimeoutMs());
		
		manager.getHazelcastInstance().getMap("lithium.hazelcast")
			.set("last-service-startup", getName());
		
		manager.getHazelcastInstance().getMap("lithium.hazelcast")
			.addEntryListener(new EntryListener(), "last-service-startup", true);
		return manager;
	}
	
	@Override
	public CacheManager cacheManager() {
		try {
			return hazelcastCacheManager();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return cacheErrorHandler();
	}
	
	@Bean
	public CacheErrorHandler cacheErrorHandler() {
		return new CacheErrorHandler() {
			@Override
			public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
				log.error("handleCachePutError {} {} {}", exception.toString(), cache.getName(), key.toString());
			}
			
			@Override
			public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
				log.error("handleCacheGetError {} {} {}", exception.toString(), cache.getName(), key.toString());				
			}
			
			@Override
			public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
				log.error("handleCacheEvictError {} {} {}", exception.toString(), cache.getName(), key.toString());
			}
			
			@Override
			public void handleCacheClearError(RuntimeException exception, Cache cache) {
				log.error("handleCacheClearError {} {}", exception.toString(), cache.getName());
			}
		};
	}
	
	public class EntryListener implements EntryUpdatedListener<String, String> {

		@Override
		public void entryUpdated(EntryEvent<String, String> event) {
			log.warn("Service startup detected via shared hazelcast map lithium.hazelcast:last-service-startup: " + event.getValue());
		}
	}
}
