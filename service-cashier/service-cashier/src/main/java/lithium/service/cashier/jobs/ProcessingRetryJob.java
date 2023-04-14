package lithium.service.cashier.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.machine.DoMachine;
import lithium.systemauth.SystemAuthService;
import lithium.tokens.JWTUser;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Component
@Slf4j
public class ProcessingRetryJob {

	@Autowired WebApplicationContext beanContext;
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired ModelMapper mapper;
	@Autowired TransactionRepository transactionRepository;
	@Autowired SystemAuthService systemAuthService;
	@Autowired TokenStore tokenStore;

	public ProcessingRetryJob() {
	}

	@Value("${lithium.service.cashier.jobs.system-domain-name:system}")
	String systemUserDomainName;
	@Value("${lithium.service.cashier.jobs.system-username:system}")
	String systemUserUsername;
	@Scheduled(cron="${lithium.service.cashier.jobs.processing.retry.cron: 0 */10 * * * * }")
	public void retryProcessing() {
		processingRetryRun();
	}

	private void processingRetryRun() {
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
		List<Transaction> retryableTransactionList =  transactionRepository.findByRetryProcessingIsTrue();

		for (Transaction t : retryableTransactionList) {
			//This can be offloaded to runners if we require more performant retrying
			if (t.getCurrent().getStatus().getActive()) {
				log.debug("Transaction retry flag is true and the transaction is not in a final state. " + t);
				DoMachine machine = beanContext.getBean(DoMachine.class);
				try {
					DoResponse response = machine.retry(
							t.getDomainMethod().getDomain().getName(),
							t.getId(),
							systemToken,
							"Transaction retry processing job");
				} catch (Exception e) {
					log.error("Problem in processing system auto retry transaction attempt" + t, e);
				}
			} else {
				log.debug("Transaction retry cancelled due to transaction being in a final state. " + t);
				t.setRetryProcessing(false);
				transactionRepository.save(t);
			}
		}

		//Option for sequential execution or to run parallel depending on requirements
	}
}
