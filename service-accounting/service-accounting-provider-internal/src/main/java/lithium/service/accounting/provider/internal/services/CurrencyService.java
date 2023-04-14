package lithium.service.accounting.provider.internal.services;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;

@Service
public class CurrencyService {

	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	ConcurrentHashMap<String, Currency> cache = new ConcurrentHashMap<>(1000);

	@Autowired
	private CurrencyRepository repository;
	
	public Currency findOrCreate(String code) {
		Currency object = cacheGet(code);
		if (object != null) return object;
		object = repository.findByCode(code);
		if (object != null) return cachePut(object);
		object = Currency.builder().code(code).build();
		repository.save(object);
		return cachePut(object);
	}

	public Currency findByCode(String code) {
		Currency object = cacheGet(code);
		if (object != null) return object;
		object = repository.findByCode(code);
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