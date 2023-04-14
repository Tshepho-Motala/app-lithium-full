package lithium.service.vb.migration.service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.vb.migration.SystemHistoricRegistrationIngestionClient;
import lithium.service.vb.migration.exceptions.Status500CredentialsInternalSystemClientException;
import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CachingMigrationClientService {

  private final LithiumServiceClientFactory services;
  private final MessageSource messageSource;

  public SystemHistoricRegistrationIngestionClient getIngestionClient()
      throws Status500CredentialsInternalSystemClientException {
    try {
      return services.target(SystemHistoricRegistrationIngestionClient.class, "service-user", true);
    } catch (LithiumServiceClientFactoryException fe) {
      throw new Status500CredentialsInternalSystemClientException(fe);
    }
  }

  public MigrationCredential retrieveMigrationCredentialByGuid(String playerGuid)
      throws Status500CredentialsInternalSystemClientException {
    log.debug("Retrieving migrated user " + playerGuid);
    Response<MigrationCredential> migrationCredential = getIngestionClient().findPlayerByPlayerGuid(
        playerGuid);
    if (migrationCredential.isSuccessful() && migrationCredential.getData() != null) {
      log.info("Retrieved migrated user " + migrationCredential);
      return migrationCredential.getData();
    }
    throw new Status500CredentialsInternalSystemClientException("Unable to retrieve player credentials from historic ingestion service: " + playerGuid);
  }

  public MigrationCredential retrieveMigrationCredentialByCustomerId(String customerId)
      throws Status500CredentialsInternalSystemClientException {
    log.debug("Retrieving migrated user " + customerId);
    Response<MigrationCredential> migrationCredential = getIngestionClient().findPlayerByCustomerId(
        customerId);
    if (migrationCredential.isSuccessful() && migrationCredential.getData() != null) {
      log.info("Retrieved migrated user " + migrationCredential);
      return migrationCredential.getData();
    }
    throw new Status500CredentialsInternalSystemClientException("Unable to retrieve player credentials from historic ingestion service: " + customerId);
  }

  public MigrationCredential retrieveMigrationCredentialByUsername(String username)
      throws Status500CredentialsInternalSystemClientException {
    log.debug("Retrieving migrated user " + username);
    Response<MigrationCredential> migrationCredential = getIngestionClient().findPlayerByUsername(
        username);
    if (migrationCredential.isSuccessful() && migrationCredential.getData() != null) {
      log.info("Retrieved migrated user " + migrationCredential);
      return migrationCredential.getData();
    }
    throw new Status500CredentialsInternalSystemClientException("Unable to retrieve player credentials from historic ingestion service: " + username);
  }
}

