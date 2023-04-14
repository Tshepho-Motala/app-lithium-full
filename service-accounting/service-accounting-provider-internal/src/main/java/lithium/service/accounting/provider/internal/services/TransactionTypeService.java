package lithium.service.accounting.provider.internal.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeAccount;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeLabel;
import lithium.service.accounting.provider.internal.data.repositories.AccountTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.LabelRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeAccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeLabelRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeRepository;

@Service
public class TransactionTypeService {
	// Maintain a near-cache of transaction types in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	ConcurrentHashMap<String, TransactionType> cache = new ConcurrentHashMap<>(1000);

    @Autowired
    LabelRepository labelRepository;
    @Autowired
    AccountTypeRepository accountTypeRepository;
    @Autowired
    TransactionTypeRepository transactionTypeRepository;
    @Autowired
    TransactionTypeLabelRepository transactionTypeLabelRepository;
    @Autowired
    TransactionTypeAccountRepository transactionTypeAccountRepository;

    @Autowired
    private ModelMapper mapper;

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

    public TransactionType findByCode(String code) {
    	TransactionType t = cacheGet(code);
    	if (t != null) return t;
        return cachePut(transactionTypeRepository.findByCode(code));
    }

    public TransactionType findOne(Long id) {
        return(transactionTypeRepository.findOne(id));
    }

    public TransactionType save(TransactionType tt) {
        return cachePut(transactionTypeRepository.save(tt));
    }

    public TransactionTypeLabel findTransactionTypeLabel(TransactionType transactionType, String label) {
        return transactionTypeLabelRepository.findByTransactionTypeAndLabel(transactionType, label);
    }

    public void addLabel(TransactionType tt, String label, boolean summarize, Boolean summarizeTotal,
            Boolean synchronous) {
        //handle modification case
        TransactionTypeLabel ttl = transactionTypeLabelRepository.findByTransactionTypeAndLabel(tt, label);
        if (ttl != null) {
            ttl.setSummarize(summarize);
            ttl.setSummarizeTotal(summarizeTotal);
            ttl.setSynchronous(synchronous);
            ttl.setAccountTypeCode(null);
            ttl.setOptional(false);
            transactionTypeLabelRepository.save(ttl);
        } else {
            transactionTypeLabelRepository.save(TransactionTypeLabel.builder()
                    .transactionType(tt)
                    .label(label)
                    .summarize(summarize)
                    .summarizeTotal(summarizeTotal)
                    .synchronous(synchronous)
                    .optional(false)
                    .build());
        }
    }

    public void addOptionalLabel(TransactionType tt, String label, boolean summarize, Boolean summarizeTotal,
            Boolean synchronous) {
        //handle modification case
        TransactionTypeLabel ttl = transactionTypeLabelRepository.findByTransactionTypeAndLabel(tt, label);
        if (ttl != null) {
            ttl.setSummarize(summarize);
            ttl.setSummarizeTotal(summarizeTotal);
            ttl.setSynchronous(synchronous);
            ttl.setAccountTypeCode(null);
            ttl.setOptional(true);
            transactionTypeLabelRepository.save(ttl);
        } else {
            transactionTypeLabelRepository.save(TransactionTypeLabel.builder()
                    .transactionType(tt)
                    .label(label)
                    .summarize(summarize)
                    .summarizeTotal(summarizeTotal)
                    .synchronous(synchronous)
                    .optional(true)
                    .build());
        }
    }

    public void addUniqueLabel(TransactionType tt, String label, boolean summarize, Boolean summarizeTotal,
            Boolean synchronous, String accountTypeCode) {
        //handle modification case
        TransactionTypeLabel ttl = transactionTypeLabelRepository.findByTransactionTypeAndLabel(tt, label);
        if (ttl != null) {
            ttl.setSummarize(summarize);
            ttl.setSummarizeTotal(summarizeTotal);
            ttl.setSynchronous(synchronous);
            ttl.setAccountTypeCode(accountTypeCode);
            ttl.setOptional(false);
            transactionTypeLabelRepository.save(ttl);
        } else {
            transactionTypeLabelRepository.save(TransactionTypeLabel.builder()
                    .transactionType(tt)
                    .label(label)
                    .summarize(summarize)
                    .summarizeTotal(summarizeTotal)
                    .synchronous(synchronous)
                    .optional(false)
                    .accountTypeCode(accountTypeCode)
                    .build());
        }
    }

    public void addAccount(TransactionType tt, String accountTypeCode, Boolean debit, Boolean credit) {
        addAccount(tt, accountTypeCode, debit, credit, 1);
    }


    public void addAccount(TransactionType tt, String accountTypeCode, Boolean debit, Boolean credit, Integer dividerToCents) {
        if (transactionTypeAccountRepository.findByTransactionTypeAndAccountTypeCode(tt, accountTypeCode) != null) return;
        transactionTypeAccountRepository.save(TransactionTypeAccount.builder()
                .transactionType(tt)
                .accountTypeCode(accountTypeCode)
                .debit(debit)
                .credit(credit)
                .dividerToCents(dividerToCents)
                .build());
    }

    public List<lithium.service.accounting.objects.TransactionType> findAll() {
        Iterable<TransactionType> types = transactionTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
        List<lithium.service.accounting.objects.TransactionType> list = new ArrayList<>();
        types.forEach((type)-> list.add(mapper.map(type,lithium.service.accounting.objects.TransactionType.class)));
        return list;
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
