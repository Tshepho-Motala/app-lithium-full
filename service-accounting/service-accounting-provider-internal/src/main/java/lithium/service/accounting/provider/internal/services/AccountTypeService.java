package lithium.service.accounting.provider.internal.services;

import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeAccount;
import lithium.service.accounting.provider.internal.data.repositories.AccountTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired private TransactionTypeAccountRepository transactionTypeAccountRepository;

	@Retryable
    public AccountType findOrCreate(String code, TransactionType transactionType) {
        AccountType a = findByCode(code);
		if (a != null) return a;
		int dividerToCents = 1;
		if (transactionType != null) {
			TransactionTypeAccount transactionTypeAccount = transactionTypeAccountRepository.
					findByTransactionTypeAndAccountTypeCode(transactionType, code);
			dividerToCents = (transactionTypeAccount != null) ? transactionTypeAccount.getDividerToCents() : 1;
		}
        a = accountTypeRepository.save(
                AccountType.builder()
                        .code(code)
						.dividerToCents(dividerToCents)
                        .build()
        );
        return cachePut(a);
    }

	public AccountType findByCode(String code) {
		AccountType a = cacheGet(code);
		if (a != null) return a;
		a = accountTypeRepository.findByCode(code);
		if (a != null) return cachePut(a);
		return null;
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
