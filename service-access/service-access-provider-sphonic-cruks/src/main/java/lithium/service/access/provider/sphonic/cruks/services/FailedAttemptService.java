package lithium.service.access.provider.sphonic.cruks.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.provider.sphonic.cruks.config.Configuration;
import lithium.service.access.provider.sphonic.cruks.config.Properties;
import lithium.service.access.provider.sphonic.cruks.jobs.FailedAttemptRetryJob;
import lithium.service.access.provider.sphonic.cruks.storage.entities.FailedAttempt;
import lithium.service.access.provider.sphonic.cruks.storage.entities.User;
import lithium.service.access.provider.sphonic.cruks.storage.repositories.AuthenticationRepository;
import lithium.service.access.provider.sphonic.cruks.storage.repositories.FailedAttemptRepository;
import lithium.service.access.provider.sphonic.cruks.storage.repositories.UserRepository;
import lithium.service.access.provider.sphonic.data.entities.Domain;
import lithium.service.access.provider.sphonic.data.repositories.DomainRepository;
import lithium.service.access.provider.sphonic.schema.cruks.login.CRUKSLoginResponse;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lithium.service.access.provider.sphonic.util.SphonicUserRevisionLabelValueUtil;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FailedAttemptService {
    @Autowired private AuthenticationRepository authenticationRepository;
    @Autowired private SphonicAuthenticationService sphonicAuthenticationService;
    @Autowired private CRUKSService cruksService;
    @Autowired private CRUKSResultService cruksResultService;
    @Autowired private ConfigurationService configurationService;
    @Autowired private DomainRepository domainRepository;
    @Autowired private FailedAttemptRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserApiInternalClientService userApiInternalClientService;
    @Autowired private Properties properties;

    public FailedAttempt createOrUpdate(String domainName, String userGuid, String failureMessage) {
        Domain domain = domainRepository.findOrCreateByName(domainName, Domain::new);
        User user = userRepository.findOrCreateByGuid(userGuid, () -> User.builder().domain(domain).build());
        FailedAttempt failedAttempt = repository.findByUserOrderByLastAttemptedAtAsc(user);
        if (failedAttempt != null) {
            failedAttempt.setTotalAttempts(failedAttempt.getTotalAttempts() + 1);
        } else {
            failedAttempt = FailedAttempt.builder()
                    .firstFailedAttempt(new Date())
                    .firstFailedMessage(failureMessage)
                    .user(user)
                    .totalAttempts(1L)
                    .build();
        }
        failedAttempt.setLastFailureMessage(failureMessage);
        failedAttempt.setLastAttemptedAt(new Date());
        failedAttempt = repository.save(failedAttempt);
        return failedAttempt;
    }

    /**
     * This process is triggered every min.
     *
     * @see FailedAttemptRetryJob#process
     */
    public void retry() {
        Pageable pageable = PageRequest.of(0, properties.getFailedAttemptJob().getBatchAttempts(),
                Sort.Direction.DESC, "lastAttemptedAt");
        Page<FailedAttempt> entries = repository.findAllByTotalAttemptsLessThanEqual(Long.valueOf(properties.getFailedAttemptJob().getBackoffThreshold()), pageable);
        int userRetrievalFailures = 0;
        if (!ObjectUtils.isEmpty(entries.getContent())) {
            for (FailedAttempt attempt : entries.getContent()) {

                String logDetail = "[attempt.id=" + attempt.getId() + ", user.guid=" + attempt.getUser().getGuid()
                        + ", lastAttemptedAt=" + attempt.getLastAttemptedAt()
                        + ", totalAttempts=" + attempt.getTotalAttempts() + "]";

                boolean success = false;
                boolean abandonProcess = false;

                try {
                    lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(attempt.getUser().getGuid());
                    log.trace("User " + user);
                    String domainName = user.getDomain().getName();

                    Configuration configuration = configurationService.getDomainConfiguration(domainName);
                    String accessToken = sphonicAuthenticationService.getAccessToken(authenticationRepository, domainName,
                            configuration.getAuthenticationUrl(), configuration.getUsername(), configuration.getPassword(),
                            configuration.getConnectTimeout(), configuration.getConnectionRequestTimeout(), configuration.getSocketTimeout());

                    String cruksId = null;
                    String uniqueReference = UUID.randomUUID().toString().replace("-", "");

                    boolean failFast = false;

                    if (user.getCurrent() == null ||
                            user.getCurrent().getLabelValueList() == null ||
                            user.getCurrent().getLabelValueList().isEmpty()) {
                        failFast = true;
                    } else {
                        cruksId = SphonicUserRevisionLabelValueUtil.getValueFromUserRevisionLabelValues(
                                user.getCurrent().getLabelValueList(), Label.CRUKS_ID);
                        if (cruksId == null) {
                            failFast = true;
                        }
                    }

                    if (failFast) {
                        throw new Status500InternalServerErrorException("CRUKS id could not be retrieved from user"
                                + " service.");
                    }

                    CRUKSLoginResponse response = cruksService.cruksLogin(configuration,
                            accessToken, cruksId, uniqueReference, null);

                    String result = response.getSphonicResponse().getData().getResult();

                    // TODO: Logout user if result indicates exclusion on cruks?
                    //       Do we have a way to do this from the backend?
                    cruksResultService.handle(user, cruksId, result, true);

                    switch (result.replaceAll("\\s", "").toUpperCase()) {
                        case CRUKSResultService.CRUKS_RESULT_ERROR:
                            attempt.setLastFailureMessage(CRUKSResultService.CRUKS_RESULT_ERROR_MESSAGE);
                            if (attempt.getTotalAttempts().intValue() >= properties.getFailedAttemptJob().getErrorLoggingThreshold()) {
                                log.error(CRUKSResultService.CRUKS_RESULT_ERROR_MESSAGE + logDetail);
                            } else {
                                log.warn(CRUKSResultService.CRUKS_RESULT_ERROR_MESSAGE + logDetail);
                            }
                            break;
                        case CRUKSResultService.CRUKS_RESULT_NONE:
                            attempt.setLastFailureMessage(CRUKSResultService.CRUKS_RESULT_NONE_MESSAGE);
                            if (attempt.getTotalAttempts().intValue() >= properties.getFailedAttemptJob().getErrorLoggingThreshold()) {
                                log.error(CRUKSResultService.CRUKS_RESULT_NONE_MESSAGE + logDetail);
                            } else {
                                log.warn(CRUKSResultService.CRUKS_RESULT_NONE_MESSAGE + logDetail);
                            }
                            break;
                        default:
                            success = true;
                    }
                } catch (UserClientServiceFactoryException | UserNotFoundException e) {
                    attempt.setLastFailureMessage(e.getMessage());
                    userRetrievalFailures++;
                    if (userRetrievalFailures > 1) {
                        log.error("Something went wrong while trying to retrieve user from SVC-user for the second time." +
                                " Something is probably broken in SVC-user " + logDetail);
                        abandonProcess = true;
                    } else {
                        log.warn("Something went wrong while trying to retrieve user from user service. "
                                + logDetail + " Going to try the next user anyway.");
                    }
                } catch (Exception e) {
                    attempt.setLastFailureMessage(e.getMessage());
                    abandonProcess = true;
                    log.error("FailedAttemptService.retry failed " + e.getMessage(), e);
                } finally {
                    if (success) {
                        log.trace("Failed attempt retried, got a successful response, deleting entry " + logDetail);
                        repository.delete(attempt);
                    } else {
                        //update the attempts after the attempt has been made
                        attempt.setLastAttemptedAt(new Date());
                        attempt.setTotalAttempts(attempt.getTotalAttempts() + 1);
                        repository.save(attempt);
                        log.trace("Failed attempt retried but still got a bad response. Updating entry " + logDetail);
                        if (abandonProcess) return;
                    }
                }
            }
        }
    }

    public void removeUserFromAttempts(String guid) {
        Optional<FailedAttempt> attempt = repository.findByUserGuid(guid);
        if (attempt.isPresent()) {
            repository.delete(attempt.get());
            String logDetail = "[attempt.id=" + attempt.get().getId() + ", user.guid=" + attempt.get().getUser().getGuid()
                    + ", lastAttemptedAt=" + attempt.get().getLastAttemptedAt()
                    + ", totalAttempts=" + attempt.get().getTotalAttempts() + "]";

            log.debug("Failed attempt retried, got a successful response, deleting entry " + logDetail);
        }
    }
}
