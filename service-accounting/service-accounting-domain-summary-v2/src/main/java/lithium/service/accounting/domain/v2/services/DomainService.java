package lithium.service.accounting.domain.v2.services;

import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.repositories.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DomainService {
	// Maintain a near-cache of domains in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	private Map<String, Domain> cache = new ConcurrentHashMap<>(1000);

	@Autowired
	private DomainRepository domainRepository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
	public Domain findOrCreate(String name) {
		Domain domain = cacheGet(name);
		if (domain != null) return domain;
		domain = domainRepository.findByName(name);
		if (domain != null) return cachePut(domain);
		domain = Domain.builder().name(name).build();
		domainRepository.save(domain);
		return cachePut(domain);
	}
	
	private Domain cachePut(Domain object) {
		String cacheKey = object.getName(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private Domain cacheGet(String code) {
		String cacheKey = code;
		return cache.get(cacheKey);
	}
}