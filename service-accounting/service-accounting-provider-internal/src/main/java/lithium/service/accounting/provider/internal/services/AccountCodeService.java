package lithium.service.accounting.provider.internal.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.repositories.AccountCodeRepository;

@Service
public class AccountCodeService {
	
	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	ConcurrentHashMap<String, AccountCode> cache = new ConcurrentHashMap<>(1000);
	
    @Autowired
    AccountCodeRepository accountCodeRepository;
    @Autowired
    private ModelMapper mapper;

    @Retryable
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

    public List<lithium.service.accounting.objects.AccountCode> findAllCodes() {
        Iterable<AccountCode> codes = accountCodeRepository.findAll();
        List<lithium.service.accounting.objects.AccountCode> list = new ArrayList<>();
        codes.forEach((code) -> list.add(mapper.map(code, lithium.service.accounting.objects.AccountCode.class)));

        return list;
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
