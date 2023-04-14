package lithium.service.leaderboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.CompleteTransaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionCompletedEventService implements ICompletedTransactionProcessor {
	@Autowired LeaderboardService leaderboardService;
	
	// Copied from svc-missions - Riaan
	@Override
	public void processCompletedTransaction(CompleteTransaction request) throws Exception {
		try {
			log.info("CompleteTransaction : "+request);
			String userGuid = request.getTransactionEntryList().get(0).getAccount().getOwner().getGuid();
			String domainName = request.getTransactionEntryList().get(0).getAccount().getDomain().getName();
			String transactionType = request.getTransactionType();
			Long amountCents = Math.abs(request.getTransactionEntryList().get(0).getAmountCents());
			
			leaderboardService.registerTran(domainName, userGuid, transactionType, amountCents);
		} catch (Exception e) {
			log.error("Problem recording leaderboard trans (" +request+ ")" + e.getMessage(), e);
		}
	}
}
