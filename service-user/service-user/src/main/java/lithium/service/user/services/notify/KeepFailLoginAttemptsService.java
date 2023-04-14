package lithium.service.user.services.notify;

import lithium.leader.LeaderCandidate;
import lithium.service.access.client.AccessService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.DomainRevisionLabelValue;
import lithium.service.user.data.entities.FailLoginAttempt;
import lithium.service.user.data.repositories.FailLoginAttemptRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class KeepFailLoginAttemptsService {
    private static final String LOGIN_FAILURES_AMOUNT = "LOGIN_FAILURES_AMOUNT";
    private static final String CLEAN_LOGIN_FAILURES_EXPIRATION_TIME = "CLEAN_LOGIN_FAILURES_EXPIRATION_TIME";


    @Autowired
    private FailLoginAttemptRepository failLoginAttemptRepository;
    @Autowired
    private LithiumServiceClientFactory serviceFactory;
    @Autowired
    private AccessService accessService;
    @Autowired
    private LeaderCandidate leaderCandidate;


    public void update(String domainName, String ipAddress, boolean isLoginSuccess) throws LithiumServiceClientFactoryException {
        FailLoginAttempt previousIpEntry = failLoginAttemptRepository.findOne(ipAddress);
        if (isLoginSuccess) {
            if (nonNull(previousIpEntry)) {
                failLoginAttemptRepository.deleteById(ipAddress);
                log.info("Removed failed login Ip " + ipAddress + " from DB due to a success login");
            }
        } else {
            Integer failures = 1;
            if (nonNull(previousIpEntry)) {
                failures = previousIpEntry.getFailureAmount() + 1;
            }
            failLoginAttemptRepository.save(FailLoginAttempt.builder()
                    .ip(ipAddress)
                    .domainName(domainName)
                    .failureAmount(failures)
                    .version(nonNull(previousIpEntry) ? previousIpEntry.getVersion() : 0)
                    .build());

            String failedLoginIpListName = getFailedLoginIpList(domainName);
            Integer maxFailures = getMaxFailures(domainName);
            if (!failedLoginIpListName.isEmpty() && maxFailures != null && failures >= maxFailures) {
                log.info("Adding : '" + ipAddress + "' to : '" + failedLoginIpListName + "' for domain : " + domainName);
                accessService.addValueToList(domainName, failedLoginIpListName, ipAddress);
            }

        }
    }

    @Scheduled(fixedRateString = "${lithium.service.user.check-expired-login-attempts-interval-in-milliseconds:60000}",
            initialDelayString = "${lithium.service.user.check-expired-login-attempts-delay-in-milliseconds:60000}")
    public void cleanExpiredFailLoginAttempts() throws LithiumServiceClientFactoryException {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }

        for (Map.Entry<String, Integer> entry : getFailuresExpirationTime().entrySet()) {
            List<FailLoginAttempt> expiredLoginAttempts = failLoginAttemptRepository.deleteAllByDomainNameAndDateAddedIsBefore(entry.getKey(), DateTime.now().minusSeconds(entry.getValue()));
            if (!expiredLoginAttempts.isEmpty()) {
              log.info("Removed failed login attempts (" + entry.getKey() + "):  " + expiredLoginAttempts + " from DB due to reaching max expiration time (" + entry.getValue() + ")");
              String failedLoginIpListName = getFailedLoginIpList(entry.getKey());
              expiredLoginAttempts.forEach(failLoginAttempt -> accessService.removeValueFromList(failLoginAttempt.getDomainName(), failedLoginIpListName, failLoginAttempt.getIp()));
            }
        }
    }

    private Integer getMaxFailures(String domainName) throws LithiumServiceClientFactoryException {
        DomainClient client = serviceFactory.target(DomainClient.class, true);
        DomainRevisionLabelValue labelValue = client.findCurrentSetting(domainName, LOGIN_FAILURES_AMOUNT).getData();
        if (nonNull(labelValue) && !labelValue.getLabelValue().getValue().isEmpty()) {
            return Integer.valueOf(labelValue.getLabelValue().getValue());
        }
        return null;
    }

    private Map<String, Integer> getFailuresExpirationTime() throws LithiumServiceClientFactoryException {
        Map<String, Integer> expirationTimePerDomain = new HashMap<>();
        DomainClient client = serviceFactory.target(DomainClient.class, true);
        for (Domain domain : client.findAllDomains().getData()) {
            Optional<String> expirationTime = domain.findDomainSettingByName(CLEAN_LOGIN_FAILURES_EXPIRATION_TIME);
            if (expirationTime.isPresent() && !expirationTime.get().isEmpty()) {
                expirationTimePerDomain.put(domain.getName(), Integer.valueOf(expirationTime.get()));
            }
        }
        return expirationTimePerDomain;
    }

    public String getFailedLoginIpList(String domainName) throws LithiumServiceClientFactoryException {
        DomainClient client = serviceFactory.target(DomainClient.class, true);
        Domain domain = client.findByName(domainName).getData();
        if (nonNull(domain) && nonNull(domain.getFailedLoginIpList())) {
            return domain.getFailedLoginIpList();
        }
        return "";
    }

}
