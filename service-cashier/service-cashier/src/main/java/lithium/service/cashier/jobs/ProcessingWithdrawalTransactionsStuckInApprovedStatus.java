package lithium.service.cashier.jobs;


import lithium.leader.LeaderCandidate;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.machine.DoMachine;
import lithium.systemauth.SystemAuthService;
import lithium.tokens.JWTUser;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.List;

@Component
@Slf4j
public class ProcessingWithdrawalTransactionsStuckInApprovedStatus {

    @Autowired
    WebApplicationContext beanContext;
    @Autowired
    LeaderCandidate leaderCandidate;
    @Autowired
    ModelMapper mapper;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    SystemAuthService systemAuthService;
    @Autowired
    TokenStore tokenStore;

    public ProcessingWithdrawalTransactionsStuckInApprovedStatus() {
    }

    @Value("${lithium.service.cashier.jobs.system-domain-name:system}")
    String systemUserDomainName;
    @Value("${lithium.service.cashier.jobs.system-username:system}")
    String systemUserUsername;

    @Scheduled(cron = "${lithium.service.cashier.jobs.processing.approved.state.stuck.retry.cron: 0 0 */1 * * * }")
    public void retryProcessing() {
        processingRetryRun();
    }

    private void processingRetryRun() {
        //Leadership
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        LithiumTokenUtil systemToken = null;
        try {
            systemToken = systemAuthService.getSystemLithiumTokenUtil(tokenStore, systemUserDomainName, systemUserUsername);
        } catch (Exception ex) {
            log.error("Unable to get system token: " + ex.getMessage(), ex);
            return;
        }

        //Transaction retrieval

        DateTime now = new DateTime();
        DateTime checkTime = now.minusMinutes(10); // The status "APPROVED" under no cases can be maintained for more than a few minutes. Under any normal cases, it should end/change with something else. So putting this out in an external setting doesn't make sense.
        Timestamp timestamp = new Timestamp(checkTime.toDate().getTime());
        List<Transaction> retryableTransactionList = transactionRepository.findByTransactionTypeAndStatusCodeAndCurrentTimestampBefore(TransactionType.WITHDRAWAL, DoMachineState.APPROVED.name(), timestamp);

        for (Transaction t : retryableTransactionList) {
            log.warn("Transaction in Unexpected APPROVED DoMachineState found, try to retry . " + t);
            DoMachine machine = beanContext.getBean(DoMachine.class);
            try {
                DoResponse response = machine.retry(
                        t.getDomainMethod().getDomain().getName(),
                        t.getId(),
                        systemToken,
                        "Transaction Unexpected APPROVED DoMachineState retry processing job");
            } catch (Exception e) {
                log.error("Problem in processing system Unexpected APPROVED DoMachineState auto retry transaction attempt" + t, e);
            }
        }
    }
}


