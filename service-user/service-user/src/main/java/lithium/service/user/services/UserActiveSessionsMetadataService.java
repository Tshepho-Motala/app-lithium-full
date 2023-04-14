package lithium.service.user.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserActiveSessionsMetadata;
import lithium.service.user.data.repositories.UserActiveSessionsMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserActiveSessionsMetadataService {
  @Autowired private UserActiveSessionsMetadataService self;
  @Autowired private CachingDomainClientService cachingDomainClientService;
  @Autowired private LoginEventService loginEventService;
  @Autowired private UserActiveSessionsMetadataRepository repository;
  @Autowired private UserService userService;
  @Autowired private PlaytimeLimitsV2Service playtimeLimitsV2Service;

  @Transactional
  public UserActiveSessionsMetadata updateMetadata(Long userId, boolean invokedFromLogin)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException, Status426InvalidParameterProvidedException,
  Status550ServiceDomainClientException{
    log.trace("updateMetadata | userId: {}, invokedFromLogin: {}", userId, invokedFromLogin);

    UserActiveSessionsMetadata metadata = self.findOrCreate(userId);
    log.trace("updateMetadata | metadata (before): {}", metadata);

    if (invokedFromLogin && !metadata.isCreatedNow()) {
      // Called on login, need to increment active sessions.
      metadata.setActiveSessionCount(metadata.getActiveSessionCount() + 1);
      if (metadata.getActiveSessionCount() == 1) {
        // If there is only one active session, then we initialise this value with now to calculate time used based on most
        // recent login.
        metadata.setPlaytimeLimitLastUpdated(LocalDateTime.now(ZoneOffset.UTC));
      }
    } else if (!invokedFromLogin) {
      // Called on logout or session inactivity timeout, need to update play time limit used and decrement active session count.
      if (metadata.getActiveSessionCount() > 0) {
        Domain domain = null;
        try {
          domain = cachingDomainClientService.retrieveDomainFromDomainService(metadata.getUser().domainName());
        } catch (Status550ServiceDomainClientException e) {
          log.error("Could not retrieve domain from domain service | {}", e.getMessage(), e);
        }
        if ((domain == null) || (domain.isPlaytimeLimit())) {
          // In the unlikely event that we are unable to retrieve the domain, call the method anyway, it will
          // print an error that playtime limit not found.
          try {
            playtimeLimitsV2Service.updateAndGetPlayerEntry(metadata.getUser().guid());
          } catch (Exception e) {
            log.error("Unable to updateAndGetPlayerEntry for userGuid: " + metadata.getUser().guid() + " | Continuing gracefully not to affect users active session metadata count", e);
          }
        }

        metadata.setActiveSessionCount(metadata.getActiveSessionCount() - 1);
        if (metadata.getActiveSessionCount() == 0) {
          metadata.setPlaytimeLimitLastUpdated(null);
        }
      } else {
        log.debug("Received a metadata update request to decrement active session count, but there are no active sessions recorded. metadata: {}",
            metadata);
      }
    }

    metadata = repository.save(metadata);

    log.trace("updateMetadata | metadata (after): {}", metadata);

    return metadata;
  }

  @Transactional
  public UserActiveSessionsMetadata findOrCreate(Long userId) {
    // Get an exclusive lock on user.
    // Concurrent invocations of this method will queue up here, waiting for the lock on user to be released.
    User user = userService.findForUpdate(userId);

    UserActiveSessionsMetadata metadata = repository.findByUser(user);

    if (metadata == null) {

      // Get the active session count to initialise the metadata.
      // From here on out, it will be updated on login, logout, and session inactivity timeout.
      int activeSessionCount = loginEventService.getActiveSessionCountForUser(user);

      metadata = repository.save(
          UserActiveSessionsMetadata.builder()
              .user(user)
              .activeSessionCount(activeSessionCount)
              .playtimeLimitLastUpdated((activeSessionCount == 1) ? LocalDateTime.now(ZoneOffset.UTC) : null)
              .build());

      metadata.setCreatedNow(true);
    }

    return metadata;
  }

  public UserActiveSessionsMetadata findByUserId(Long userId) {
    return repository.findByUserId(userId);
  }
  public UserActiveSessionsMetadata findByUserGuid(String userGuid) {
    return repository.findByUserGuid(userGuid);
  }
  public UserActiveSessionsMetadata save(UserActiveSessionsMetadata metadata) {
    return repository.save(metadata);
  }


  public Page<UserActiveSessionsMetadata> findAll(PageRequest pageRequest) {
    return repository.findAll(pageRequest);
  }
}
