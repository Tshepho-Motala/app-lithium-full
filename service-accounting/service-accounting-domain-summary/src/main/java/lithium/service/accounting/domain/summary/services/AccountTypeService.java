package lithium.service.accounting.domain.summary.services;

import lithium.service.accounting.domain.summary.storage.entities.AccountType;
import lithium.service.accounting.domain.summary.storage.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountTypeService {
	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	private Map<String, AccountType> cache = new ConcurrentHashMap<>(1000);
	
    @Autowired private AccountTypeRepository accountTypeRepository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public AccountType findOrCreate(String code) {
        AccountType a = cacheGet(code);
        if (a != null) return a;
        a = accountTypeRepository.findByCode(code);
        if (a != null) return cachePut(a);
        a = accountTypeRepository.save(
                AccountType.builder()
                        .code(code)
                        .build()
        );
        return cachePut(a);
    }
    
	private AccountType cachePut(AccountType object) {
		String cacheKey = object.getCode(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private AccountType cacheGet(String accountTypeCode) {
		String cacheKey = accountTypeCode;
		return cache.get(cacheKey);
	}
}
