package lithium.service.cashier.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.machine.DoMachine;
import lithium.systemauth.SystemAuthService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TransactionsProcessingJob {

	@Autowired WebApplicationContext beanContext;
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired ModelMapper mapper;
	@Autowired TransactionRepository transactionRepository;
	@Autowired SystemAuthService systemAuthService;
	@Autowired TokenStore tokenStore;

	public TransactionsProcessingJob() {
	}

	@Value("${lithium.service.cashier.jobs.system-domain-name:system}")
	String systemUserDomainName;
	@Value("${lithium.service.cashier.jobs.system-username:system}")
	String systemUserUsername;
	@Scheduled(fixedDelayString = "${lithium.service.cashier.jobs.delay.processing.fixedDelay:1000}")
	public void delayProcessing() {
		processDelayedTransactions();
	}

	private void processDelayedTransactions() {
		//Leadership
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		//System token retrieval
		LithiumTokenUtil systemToken = null;
		try {
			systemToken = systemAuthService.getSystemLithiumTokenUtil(tokenStore, systemUserDomainName, systemUserUsername);
		} catch (Exception ex) {
			log.error("Unable to get system token: " + ex.getMessage(), ex);
			return;
		}

		//Transaction retrieval
		List<Transaction> delayedTransactionList =  transactionRepository.findByStatusActiveAndCurrentProcessTimeNotNullAndCurrentProcessTimeBefore(true, new Date());

		for (Transaction t : delayedTransactionList) {
			//This should be done asynchronously
			log.debug("Processing delayed transaction. " + t);
			DoMachine machine = beanContext.getBean(DoMachine.class);
			try {
				DoResponse response = machine.processTransaction(
						t.getDomainMethod().getDomain().getName(),
						t.getId(),
						systemToken,
						"Delayed transactions processing job");
			} catch (Exception e) {
				log.error("Error during processing delayed transaction" + t, e);
			}
		}
	}
}
