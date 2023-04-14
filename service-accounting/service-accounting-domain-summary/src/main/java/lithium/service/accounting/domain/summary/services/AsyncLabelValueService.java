package lithium.service.accounting.domain.summary.services;

import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.shards.objects.Shard;
import lithium.shards.ShardsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsyncLabelValueService {
    @Autowired private DomainService domainService;
    @Autowired private SharedDataService sharedDataService;
    @Autowired private ShardsRegistry shardsRegistry;
    @Autowired private SummaryDomainLabelValueService summaryDomainLabelValueService;

    private static final String SHARD_POOL = "AsyncLabelValueQueueProcessor";

    public void process(String threadName, CompleteTransaction transaction) {
        List<CompleteTransaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Domain domain = domainService.findOrCreate(transactions.get(0)
                .getTransactionEntryList().get(0)
                .getAccount()
                .getDomain()
                .getName());
        sharedDataService.findOrCreates(domain, transactions);
        Shard shard = shardsRegistry.get(SHARD_POOL, threadName);
        // Start of transaction
        summaryDomainLabelValueService.process(shard.getUuid(), domain, transactions);
    }
}
