package lithium.service.user.services;

import com.hazelcast.core.HazelcastInstance;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.repositories.LoginEventRepository;
import lithium.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SessionInactivityService {
  @Autowired private CachingDomainClientService cachingDomainClientService;
  @Autowired private LoginEventRepository repository;
  @Autowired private ServiceUserConfigurationProperties properties;
  @Autowired private HazelcastInstance hazelcast;
  @Autowired private UserActiveSessionsMetadataService userActiveSessionsMetadataService;

  public int getSessionInactivityTimeoutSetting(String domainName) {
    try {
      Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Optional<String> value = domain.findDomainSettingByName(DomainSettings.SESSION_INACTIVITY_TIMEOUT.key());
      return parseSetting(domainName, value, DomainSettings.SESSION_INACTIVITY_TIMEOUT);
    } catch (Status550ServiceDomainClientException e) {
      log.error("Failed to read session inactivity timeout domain setting, defaulting to " + DomainSettings.SESSION_INACTIVITY_TIMEOUT.defaultValue()
          + " [domainName="+domainName+"] " + e.getMessage(), e);
      return Integer.parseInt(DomainSettings.SESSION_INACTIVITY_TIMEOUT.defaultValue());
    }
  }

  public int getSessionTimeoutSetting(String domainName) {
    try {
      Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Optional<String> value = domain.findDomainSettingByName(DomainSettings.SESSION_TIMEOUT.key());
      return parseSetting(domainName, value, DomainSettings.SESSION_TIMEOUT);
    } catch (Status550ServiceDomainClientException e) {
      log.error("Failed to read session timeout domain setting, defaulting to " + DomainSettings.SESSION_TIMEOUT.defaultValue()
          + " [domainName="+domainName+"] " + e.getMessage(), e);
      return Integer.parseInt(DomainSettings.SESSION_TIMEOUT.defaultValue());
    }
  }

  private int parseSetting(String domainName, Optional<String> value, DomainSettings setting) {
    if (value.isPresent()) {
      try {
        return Integer.parseInt(value.get());
      } catch (NumberFormatException e) {
        log.error("Improper value for " + setting.key().toLowerCase() + ", should be a number representing seconds. Defaulting to "
            + setting.defaultValue() + " seconds. [domainName="+domainName+", value="+value+"]");
      }
    }
    return Integer.parseInt(setting.defaultValue());
  }

  // Run through older login events.
  // We can switch off this job when we reach a point where there are no more login events with no logout and last activity timestamp.
  @TimeThisMethod(infoThresholdMillis = 3500, warningThresholdMillis = 4000, errorThresholdMillis = 4500)
  public void processInactiveSessionTimeoutNoLastActivity() {
    PageRequest pageRequest = PageRequest.of(0, properties.getSessionInactivityTimeoutJob().getFetchSize());
    SW.start("retrieveSessions");
    Page<LoginEvent> sessions = repository.findBySuccessfulTrueAndLogoutIsNullAndLastActivityIsNull(pageRequest);
    log.trace("Found " + sessions.getContent().size() + " sessions to process");
    if (sessions.getContent().size() == 0) {
      log.warn("There are no more sessions with no logout and last activity timestamp, please change"
          + " lithium.services.user.session-inactivity-timeout-job.no-last-activity-enabled to false.");
    }
    SW.stop();

    Date now = new Date();

    SW.start("processSessions");
    List<LoginEvent> bulkUpdate = new ArrayList<>();
    for (LoginEvent session: sessions.getContent()) {
      int sessionDurationSeconds = DateUtil.secondsBetween(session.getDate(), now);
      int sessionTimeoutSeconds = getSessionTimeoutSetting(session.getDomain().getName());
      if (sessionDurationSeconds >= sessionTimeoutSeconds) {
        log.trace("Updating session | user: {}, sessionDurationSeconds: {}, sessionTimeoutSetting: {}", session.getUser().getGuid(),
            sessionDurationSeconds, sessionTimeoutSeconds);
        session.setLogout(new Timestamp(now.getTime()));
        session.setDuration(now.getTime() - session.getDate().getTime());
        bulkUpdate.add(session);
      }
    }
    SW.stop();

    if (!bulkUpdate.isEmpty()) {
      SW.start("update");
      repository.saveAll(bulkUpdate);
      SW.stop();
    }
  }

  @TimeThisMethod(infoThresholdMillis = 3500, warningThresholdMillis = 4000, errorThresholdMillis = 4500)
  public void processInactiveSessionTimeout() throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException {
    List<Domain> domains = null;
    try {
      SW.start("findAllPlayerDomains");
      domains = cachingDomainClientService.getDomainClient().findAllPlayerDomains().getData();
    } catch (Status550ServiceDomainClientException e) {
      log.error("Failed to retrieve list of player domains, cannot process inactive session timeouts | " + e.getMessage(), e);
    } finally {
      SW.stop();
      if (domains == null) return;
    }

    // Subset per domain.
    int fetchSize = Math.round(properties.getSessionInactivityTimeoutJob().getFetchSize() / domains.size());

    List<LoginEvent> bulkUpdate = new ArrayList<>();

    // Because individual domains may have their own session inactivity timeout setting.
    for (Domain domain: domains) {
      int inactivityTimeoutSeconds = getSessionInactivityTimeoutSetting(domain.getName());
      int sessionTimeoutSeconds = getSessionTimeoutSetting(domain.getName());
      DateTime lastActivityBefore = DateTime.now().minusSeconds(inactivityTimeoutSeconds);

      PageRequest pageRequest = PageRequest.of(0, fetchSize);
      SW.start("retrieveSessions_" + domain.getName());
      // Sessions that should be logged out due to inactivity.
      Page<LoginEvent> sessions = repository.findByDomainNameAndSuccessfulTrueAndLogoutIsNullAndLastActivityNotNullAndLastActivityBefore(
          domain.getName(), lastActivityBefore.toDate(), pageRequest);
      log.trace("Found " + sessions.getContent().size() + " inactive sessions to process");
      SW.stop();

      SW.start("processSessions_" + domain.getName());
      for (LoginEvent session: sessions.getContent()) {
        Date now = new Date();
        int inactiveForSeconds = DateUtil.secondsBetween(session.getLastActivity(), now);
        log.trace("Updating session | user: {}, inactiveForSeconds: {}, inactivityTimeoutSeconds: {}", session.getUser().getGuid(),
            inactiveForSeconds, inactivityTimeoutSeconds);
        long sessionDuration = now.getTime() - session.getDate().getTime();
        session.setLogout(new Timestamp(now.getTime()));
        session.setDuration(sessionDuration);
        bulkUpdate.add(session);
        int sessionTimeUsedSeconds = DateUtil.secondsBetween(session.getDate(), now);
        int ttlSeconds = sessionTimeoutSeconds - sessionTimeUsedSeconds;
        log.trace("sessionTimeoutSeconds: {}, sessionTimeUsedSeconds: {}, ttlSeconds: {}", sessionTimeoutSeconds, sessionTimeUsedSeconds, ttlSeconds);
        cachePut(session.getId(), ttlSeconds);

        // TODO: Transactional
        try {
          userActiveSessionsMetadataService.updateMetadata(session.getUser().getId(), false);
        } catch (Exception e) {
          log.error("Failed to update user session activity metadata for userGuid=" + session.getUser().guid() + " sessionId=" + session.getId() + " errorMsg= " + e.getMessage(), e);
        }
      }
      SW.stop();
    }

    if (!bulkUpdate.isEmpty()) {
      SW.start("update");
      repository.saveAll(bulkUpdate);
      SW.stop();
    }
  }

  public Long cachePut(Long loginEventId, int ttlSeconds) {
    hazelcast.getMap("lithium.service.user.inactive-session").put(loginEventId, loginEventId, ttlSeconds, TimeUnit.SECONDS);
    log.trace("Inactive session put in cache: {}", loginEventId);
    return loginEventId;
  }

  public Long cacheGet(Long loginEventId) {
    Long inactiveSession = (Long) hazelcast.getMap("lithium.service.user.inactive-session").get(loginEventId);
    if (inactiveSession != null) {
      log.trace("Got inactive session from cache: {}", inactiveSession);
    }
    return inactiveSession;
  }
}
