package lithium.service.accounting.domain.summary.services;

import lithium.service.accounting.domain.summary.storage.entities.Currency;
import lithium.service.accounting.domain.summary.storage.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyService {
	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	private Map<String, Currency> cache = new ConcurrentHashMap<>(1000);

	@Autowired private CurrencyRepository repository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
	public Currency findOrCreate(String code, String name) {
		Currency object = cacheGet(code);
		if (object != null) return object;
		object = repository.findByCode(code);
		if (object != null) return cachePut(object);
		object = Currency.builder().code(code).name(name).build();
		repository.save(object);
		return cachePut(object);
	}
	
	private Currency cachePut(Currency object) {
		String cacheKey = object.getName(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private Currency cacheGet(String code) {
		String cacheKey = code;
		return cache.get(cacheKey);
	}
}