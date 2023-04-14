package lithium.service.cashier.scheduled;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Slf4j
@Service
public class CleanupScheduling {
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	private LeaderCandidate leaderCandidate;
	
	public void doTransactionCleanup() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		log.debug("Doing Transaction Cleanup...");
		DoMachine machine = beanContext.getBean(DoMachine.class);
		List<Transaction> transactions = transactionService.findWithTTL();
		if ((transactions == null) || (transactions.size() == 0)) {
			log.debug("No Transactions found to check.");
		} else {
			for (Transaction t:transactions) {
				log.debug("Found : "+t);
				log.debug("getTtl + getCreatedOn : "+(t.getTtl().longValue()+t.getCreatedOn().getTime()));
				log.debug("getTtl                : "+t.getTtl());
				log.debug("getCreatedOn          : "+t.getCreatedOn().getTime());
				log.debug("now                   : "+DateTime.now().getMillis());
				if ((t.getTtl().longValue()+t.getCreatedOn().getTime()) < DateTime.now().getMillis()) {
					log.warn(">>>>>>>>>>>   expiring : "+t);
					try {
						if (t.getCurrent().getStatus().getCode().equals(DoMachineState.PENDING_CANCEL.name())) {
							machine.cancel(t.getId(), "System Scheduled Cancel");
						} else {
							machine.expire(t.getId(), "System Scheduled Expiry");
						}
					} catch (Exception e) {
						log.error("", e);
					}
				}
			}
		}
	}
}