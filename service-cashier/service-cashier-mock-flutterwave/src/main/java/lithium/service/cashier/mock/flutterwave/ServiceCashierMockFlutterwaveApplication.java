package lithium.service.cashier.mock.flutterwave;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.leader.EnableLeaderCandidate;
import lithium.leader.LeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.cashier.mock.flutterwave.services.WebhookSchedulingService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;


@Slf4j
@EnableAsync
@LithiumService
@EnableLeaderCandidate
@EnableScheduling
@EnableRestTemplate
public class ServiceCashierMockFlutterwaveApplication extends LithiumServiceApplication {

    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private WebhookSchedulingService webhookSchedulingService;

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceCashierMockFlutterwaveApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
        return builder.build();
    }

    @Scheduled(fixedDelayString = "${lithium.service.cashier.mock.flutterwave.webhook-scheduling-in-milliseconds}")
    public void processPendingTransactions() {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        log.debug("Simulate flutterwave webhooks running : " + DateTime.now());
        webhookSchedulingService.processPendingTransactions();
    }

    @Scheduled(fixedDelayString = "${lithium.service.cashier.mock.flutterwave.update-status-in-milliseconds}")
    public void updateTransactionsStatus() {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        log.debug("Simulate flutterwave change status running : " + DateTime.now());
        webhookSchedulingService.processUpdateTransactions();
    }
}
