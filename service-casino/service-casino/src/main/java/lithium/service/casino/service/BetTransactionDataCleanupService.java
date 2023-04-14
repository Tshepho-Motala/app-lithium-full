package lithium.service.casino.service;

import lithium.service.accounting.client.AccountingBatchTransactionsClient;
import lithium.service.casino.data.entities.Bet;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.accounting.objects.AccountingBatchDeleteRequest;
import lithium.service.casino.data.repositories.BetRepository;
import lithium.service.casino.data.repositories.BetResultRepository;
import lithium.service.casino.data.repositories.BetRoundRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BetTransactionDataCleanupService {

    @Autowired private BetRepository betRepository;

    @Autowired private BetRoundRepository betRoundRepository;

    @Autowired private BetResultRepository betResultRepository;

    @Autowired private LithiumServiceClientFactory clientFactory;

    @Value("${lithium.services.casino.bet-transactions-cleanup-job.data-fetch-size}")
    private Integer batchSize;

    @Value("${lithium.services.casino.bet-transactions-cleanup-job.data-retention-period}")
    private Integer dataRetentionPeriod;

    @Transactional(rollbackOn = Exception.class)
    public void deleteOldData() throws LithiumServiceClientFactoryException {
        Pageable pagedResultSet = PageRequest.of(0, batchSize);
        AccountingBatchDeleteRequest transactionData = new AccountingBatchDeleteRequest();
        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.add(Calendar.DATE, dataRetentionPeriod);
        //get transaction data and populate list
        List<BetRound> betRoundResultSet = betRoundRepository.findByCreatedDateLessThan(calendarInstance.getTimeInMillis(), pagedResultSet);
        List<Bet> bets = betRepository.findByBetRoundIn(betRoundResultSet);
        List<Long> transactionsList = bets.stream().map(Bet::getLithiumAccountingId).collect(Collectors.toList());
        transactionData.setTransactionIds(transactionsList);

        if (!betRoundResultSet.isEmpty()) {
            try {
                log.debug("Beginning transaction data cleanup, sending {} records to service accounting provider internal.", transactionsList.size());
                AccountingBatchTransactionsClient accountingClient = clientFactory.target(AccountingBatchTransactionsClient.class,
                        "service-accounting-provider-internal", true);
                accountingClient.findAndDeleteTransactionsBatch(transactionData);
            } catch (LithiumServiceClientFactoryException ex) {
                log.error("Data cleanup error, unable to reach client, failed to perform cleanup operation", ex);
                throw ex;
            }
            //we need to set the bet result object to null to remove constraint violations; last_bet_result_id references bet result
            betRoundResultSet.forEach(betRoundRecord -> {
                if(!Objects.isNull(betRoundRecord.getLastBetResult()))
                    betRoundRecord.setLastBetResult(null);
            });
            try {
                Long deletedBetResultEntries = betResultRepository.deleteByBetRoundIn(betRoundResultSet);
                log.debug("Marking {} entries for deletion from Bet Result table", deletedBetResultEntries);
                Long deletedBetEntries = betRepository.deleteByBetRoundIn(betRoundResultSet);
                log.debug("Marking {} entries for deletion from Bet table", deletedBetEntries);
                betRoundRepository.deleteAll(betRoundResultSet);
                log.debug("Marking {} entries for deletion from Bet Round table", betRoundResultSet.size());
            }catch(Exception e){
                log.error("Failed to complete data cleanup with exception: ", e);
            }
            log.debug("Batch job completed successfully");
        } else {
            log.warn("There are no transactions to cleanup matching criteria older than {} days. Adjust criteria if delete is required", dataRetentionPeriod);
        }
    }
}
