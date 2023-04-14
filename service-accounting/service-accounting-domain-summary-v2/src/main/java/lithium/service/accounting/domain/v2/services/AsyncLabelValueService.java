package lithium.service.accounting.domain.v2.services;

import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.shards.objects.Shard;
import lithium.shards.ShardsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsyncLabelValueService {
    @Autowired private DomainService domainService;
    @Autowired private ShardsRegistry shardsRegistry;
    @Autowired private SummaryDomainLabelValueService summaryDomainLabelValueService;
    @Autowired private LabelValueService labelValueService;

    private static final String SHARD_POOL = "AsyncLabelValueQueueProcessor";

    public void process(String threadName, CompleteTransactionV2 transaction) {
        Shard shard = shardsRegistry.get(SHARD_POOL, threadName);
        Domain domain = domainService.findOrCreate(transaction.getTransactionEntryList()
                .get(0)
                .getAccount()
                .getDomain()
                .getName());
        // Find or create label values before transactional
        for (TransactionLabelBasic tranLabelBasic: transaction.getTransactionLabelList()) {
            if (tranLabelBasic.isSummarize()) {
                labelValueService.findOrCreate(tranLabelBasic.getLabelName(), tranLabelBasic.getLabelValue());
            }
        }
        List<CompleteTransactionV2> transactions = new ArrayList<>();
        transactions.add(transaction);
        // Start of transaction
        summaryDomainLabelValueService.process(shard.getUuid(), domain, transactions);
    }
}
