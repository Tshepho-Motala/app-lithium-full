package lithium.service.accounting.domain.summary.services;

import lithium.service.accounting.domain.summary.storage.entities.AccountCode;
import lithium.service.accounting.domain.summary.storage.repositories.AccountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountCodeService {
	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	private Map<String, AccountCode> cache = new ConcurrentHashMap<>(1000);
	
    @Autowired private AccountCodeRepository accountCodeRepository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public AccountCode findOrCreate(String code) {
        AccountCode a = cacheGet(code);
        if (a != null) return a;
        a = accountCodeRepository.findByCode(code);
        if (a != null) return cachePut(a);
        a = accountCodeRepository.save(
                AccountCode.builder()
                        .code(code)
                        .build()
        );
        return cachePut(a);
    }
    
	private AccountCode cachePut(AccountCode object) {
		String cacheKey = object.getCode(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private AccountCode cacheGet(String accountCode) {
		String cacheKey = accountCode;
		return cache.get(cacheKey);
	}
}
