package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.services.AgeLimitService;
import lithium.service.limit.services.PlayerLimitService;
import lithium.service.limit.services.SystemPlayerLimitService;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutoAgeLimitUpdaterJob {

    private final LithiumServiceClientFactory services;
    private final LeaderCandidate leaderCandidate;
    private final AgeLimitService ageLimitService;
    private final SystemPlayerLimitService systemPlayerLimitService;
    private final PlayerLimitService playerLimitService;

    @Autowired
    public AutoAgeLimitUpdaterJob(
            LithiumServiceClientFactory services,
            LeaderCandidate leaderCandidate,
            AgeLimitService ageLimitService,
            SystemPlayerLimitService systemPlayerLimitService,
            PlayerLimitService playerLimitService) {
        this.services = services;
        this.leaderCandidate = leaderCandidate;
        this.ageLimitService = ageLimitService;
        this.systemPlayerLimitService = systemPlayerLimitService;
        this.playerLimitService = playerLimitService;
    }

    @Scheduled(cron="${lithium.service.limit.jobs.auto-age-limit-update.cron:0 0/1 * * * *}")
    public void reader() throws InterruptedException {
        log.debug("AutoAgeLimitUpdaterJob running");
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }

        Iterable<String> iterable = ageLimitService.findAllDomainsWithAgeLimits();

        for (String domain : iterable) {
            process(domain);
        }
    }

    private void process(String domain) throws InterruptedException {

        UserClient userClient = getUserClient();

        if (userClient == null) {
            log.error("AutoAgeLimitUpdaterJob failed. Could not retrieve users.");
            return;
        }

        long count = 0;
        long position = 0;
        boolean process = true;

        while (process) {
            DataTableResponse<User> response = userClient.table(domain, "1", position, 1L);
            count += response.getData().size();
            position += response.getData().size();

            if (count >= response.getRecordsTotal()) {
                process = false;
            }

            for (User user : response.getData()) {
                updateUserLimit(user);
                Thread.sleep(100L);
            }
        }
    }

    private void updateUserLimit(User user) {
        if (playerLimitService.fetchPlayerLossLimits(user.guid()).isEmpty()) {
            systemPlayerLimitService.setUserLimit(user);
        }
    }

    private UserClient getUserClient() {
        UserClient client;
        try {
            client = services.target(UserClient.class, "service-user", true);
            return client;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting user client | " + e.getMessage(), e);
            return null;
        }
    }
}
