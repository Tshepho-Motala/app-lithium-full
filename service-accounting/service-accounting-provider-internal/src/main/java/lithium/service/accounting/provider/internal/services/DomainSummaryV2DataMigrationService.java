package lithium.service.accounting.provider.internal.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.domain.summary.v2.stream.AdjustmentStreamV2;
import lithium.service.accounting.domain.summary.v2.stream.AsyncLabelValueStreamV2;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.provider.internal.data.entities.DomainSummaryV2MigrationProgress;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.projection.entities.TransactionLabelValueProjection;
import lithium.service.accounting.provider.internal.data.repositories.DomainSummaryV2MigrationProgressRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DomainSummaryV2DataMigrationService {

    @Autowired
    private DomainSummaryV2MigrationProgressRepository domainSummaryV2MigrationProgressRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionEntryRepository transactionEntryRepository;

    @Value("${lithium.services.accounting.domain.summary.v2.data-migration.inclusive-upper-bound-tran-id}")
    private Long inclusiveUpperBoundTranId;

    @Value("${lithium.services.accounting.domain.summary.v2.data-migration.data-enqueue-size}")
    private Long dataEnqueueSize;

    @Autowired
    TransactionService transactionService;

    @Autowired
    RabbitEventService rabbitEventService;

    @Autowired
    AdjustmentStreamV2 adjustmentStreamV2;

    @Autowired
    AsyncLabelValueStreamV2 asyncLabelValueStreamV2;

    @Autowired
    ModelMapper mapper;


    @TimeThisMethod
    public void process() {
        DomainSummaryV2MigrationProgress domainSummaryV2MigrationProgress = domainSummaryV2MigrationProgressRepository.findOne(1L);
        if (domainSummaryV2MigrationProgress == null) {
            domainSummaryV2MigrationProgress = domainSummaryV2MigrationProgressRepository.save(
                    DomainSummaryV2MigrationProgress.builder()
                            .id(1L)
                            .version(0)
                            .lastTranIdProcessed(0L)
                            .build()
            );
        }

        if (dataEnqueueSize == null) {
            log.warn("lithium.services.accounting.domain.summary.v2.data-migration.data-enqueue-size not set.");
            return;
        }

        if (domainSummaryV2MigrationProgress.getLastTranIdProcessed().longValue() >= inclusiveUpperBoundTranId.longValue()) {
            log.warn("Last transaction id has reached the inclusive upper bound limit.");
            return;
        }
        //On every invocation, look up transactions in the accounting database table
        Long lowerBoundInclusive = domainSummaryV2MigrationProgress.getLastTranIdProcessed() + 1;
        Long upperBoundInclusive = lowerBoundInclusive + dataEnqueueSize;

        if (upperBoundInclusive.longValue() > inclusiveUpperBoundTranId.longValue()) {
            // Then we need to set upperBoundInclusive to inclusiveUpperBoundTranId, else we'll have a situation
            // where we duplicate a couple of transactions
            upperBoundInclusive = inclusiveUpperBoundTranId;
        }

        List<Transaction> transactionList = transactionRepository.findByIdBetweenOrderByIdAsc(lowerBoundInclusive, upperBoundInclusive);

        if (transactionList.size() <= 0) {
            //update transaction id and return
            log.trace("No Trans For Range | {} and {}", lowerBoundInclusive, upperBoundInclusive);
            domainSummaryV2MigrationProgress.setLastTranIdProcessed(upperBoundInclusive);
            domainSummaryV2MigrationProgressRepository.save(domainSummaryV2MigrationProgress);
            return;
        } else {
            List<CompleteTransactionV2> completeTransactions = new ArrayList<>(transactionList.size());
            for (Transaction transaction : transactionList) {

                // Build the CompleteTransactionV2 object for each transaction and enqueue to adjustmentqueuev2.adjustmentgroupv2 only, then increment the lastTranIdProcessed value and save
                ArrayList<TransactionEntry> entryList = new ArrayList<>();
                SW.start("transactionEntryRepository.findByTransactionId");
                List<lithium.service.accounting.provider.internal.data.entities.TransactionEntry> byTransactionId = transactionEntryRepository.findByTransactionId(transaction.getId());
                byTransactionId.forEach(transactionEntry -> {
                    entryList.add(mapper.map(transactionEntry, TransactionEntry.class));
                });
                SW.stop();

                List<TransactionLabelBasic> transactionLabelBasicList = new ArrayList<>();
                SW.start("transactionService.getLabelsForTranProjection for " + transaction.getId());
                log.trace("Projection Query for Id | {}", transaction.getId());
                List<TransactionLabelValueProjection> labelsForTransactionToDto = transactionService.getLabelsForTransactionToDto(transaction.getId());
                log.trace("Labels for projection ||| {}", labelsForTransactionToDto);

                SW.stop();

                labelsForTransactionToDto.stream().filter(tlvl -> {
                    return !TransactionService.PERIOD_DOMAIN_SUMMARY_EXCLUDED_LABELS.contains(
                      tlvl.getLabelName()
                    );
                }).forEach(trnLblVal -> {
                    transactionLabelBasicList.add(
                            TransactionLabelBasic.builder()
                            .labelName(trnLblVal.getLabelName())
                            .labelValue(trnLblVal.getLabelValue())
                            .summarize(true)
                            .build());
                });

                log.trace("Final Tran Basic List | {}", transactionLabelBasicList);

                CompleteTransactionV2 completeTransactionV2 = CompleteTransactionV2
                        .builder()
                        .transactionId(transaction.getId())
                        .transactionType(transaction.getTransactionType().getCode())
                        .createdOn(transaction.getCreatedOn().toString())
                        .transactionEntryList(entryList)
                        .transactionLabelList(transactionLabelBasicList)
                        .testUser(byTransactionId.get(0).getAccount().getOwner().isTestAccount())
                        .build();
                completeTransactions.add(completeTransactionV2);
                domainSummaryV2MigrationProgress.setLastTranIdProcessed(transaction.getId());
                log.trace("Complete V2 Trans size | {}, last transaction id | {}", completeTransactions.size(), domainSummaryV2MigrationProgress);

         }

            SW.start("Saving to Domain Progress");
            domainSummaryV2MigrationProgressRepository.save(domainSummaryV2MigrationProgress);
            SW.stop();
            log.trace("Stop watch summary | {}", SW.getFromThreadLocal().prettyPrint());
            adjustmentStreamV2.register(completeTransactions);
        }


    }

}
