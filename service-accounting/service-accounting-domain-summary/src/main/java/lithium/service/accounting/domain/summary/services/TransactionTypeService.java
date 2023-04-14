package lithium.service.accounting.domain.summary.services;

import lithium.service.accounting.domain.summary.storage.entities.TransactionType;
import lithium.service.accounting.domain.summary.storage.repositories.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionTypeService {
	// Maintain a near-cache of transaction types in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	private Map<String, TransactionType> cache = new ConcurrentHashMap<>(1000);

    @Autowired private TransactionTypeRepository transactionTypeRepository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public TransactionType findOrCreate(String code) {
    	TransactionType t = cacheGet(code);
    	if (t != null) return t;
        t = transactionTypeRepository.findByCode(code);
        if (t != null) return cachePut(t);

        t = TransactionType.builder()
                .code(code)
                .build();
        t = transactionTypeRepository.save(t);
        return cachePut(t);
    }

	private TransactionType cachePut(TransactionType object) {
		String cacheKey = object.getCode(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private TransactionType cacheGet(String code) {
		String cacheKey = code;
		return cache.get(cacheKey);
	}
}
