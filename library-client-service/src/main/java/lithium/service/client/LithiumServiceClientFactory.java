package lithium.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.LithiumFeignClientsRegistrar;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LithiumServiceClientFactory {
	
	@Autowired LithiumFeignClientsRegistrar registrar;
	
	@Retryable(backoff=@Backoff(multiplier=2, delay=50),maxAttempts=10)
	public <T> T target(Class<T> apiType) throws Exception {
		return target(apiType, null, true);
	}

	@Retryable(backoff=@Backoff(multiplier=2, delay=50),maxAttempts=10)
	public <T> T target(Class<T> apiType, boolean useSystemAuth) throws LithiumServiceClientFactoryException {
		return target(apiType, null, useSystemAuth);
	}
	
	@Retryable(backoff=@Backoff(multiplier=2, delay=50),maxAttempts=10)
	public <T> T target(Class<T> apiType, String url, boolean useSystemAuth) throws LithiumServiceClientFactoryException {
		return target(apiType, url, useSystemAuth, null);
	}
	
	@Retryable(backoff=@Backoff(multiplier=2, delay=50),maxAttempts=10)
	public <T> T target(Class<T> apiType, String url, boolean useSystemAuth, T fallback) throws LithiumServiceClientFactoryException {
				
		FeignClient feignClient = apiType.getAnnotation(FeignClient.class);
		if (feignClient != null) {
			if (url == null) url = feignClient.name();
		}
		
		try {

			return registrar.target(apiType, url, useSystemAuth, fallback);
			
		} catch (Exception e) {
			log.warn("Feign target invocation exception: " + e.getMessage());
			throw new LithiumServiceClientFactoryException(e.getMessage(), e, false, true);
		}
	}

}
