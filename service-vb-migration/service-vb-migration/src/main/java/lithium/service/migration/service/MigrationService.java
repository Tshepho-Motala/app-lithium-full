package lithium.service.migration.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;

public interface MigrationService {
  String initializeMigration(String domainName, String migrationType, int pageSize)
      throws InterruptedException, LithiumServiceClientFactoryException, Status500InternalServerErrorException;
}
