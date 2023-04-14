package lithium.service.migration.service;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQuery.TableDataListOption;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import java.util.Optional;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status412DomainNotFoundException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestionFactory;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {

  private final ProgressRepo progressRepo;
  private final BigQuery bigquery;
  private final CachingDomainClientService cachingDomainClientService;

  private final ServiceVbMigrationConfigProperties properties;

  private final MigrationIngestionFactory migrationIngestionFactory;

  @Override
  public String initializeMigration(String domainName, String migrationType, int pageSize)
      throws InterruptedException, LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    try {
      if (!properties.getHistoricIngestion().getDomain().equals(domainName)) {
        throw new Status412DomainNotFoundException("Domain: " + domainName + " does not align with the domain setup in the configurations");
      }

      if (!cachingDomainClientService.isDomainName(domainName)) {
        throw new Status412DomainNotFoundException("Domain not found : " + domainName);
      }

      MigrationType type = MigrationType.fromType(migrationType);

      if (ObjectUtils.isEmpty(type)) {
        throw new Status400BadRequestException(
            "Migration Type: " + migrationType + " is not configured to run, please enquire with Admin or check potential typos");
      }

      String table = type.table();

      String dataset = properties.getQuery().getDataSet();
      TableId tableId = TableId.of(dataset, table);
      Table bigqueryTable = bigquery.getTable(tableId);

      if (ObjectUtils.isEmpty(bigqueryTable.getDefinition().getSchema())) {
        log.warn("Table: {} does not have columns assigned to it", table);
        throw new Status400BadRequestException("Table: " + table + " does not have columns assigned to it");
      }

      long totalNumberOfRows = bigqueryTable.getNumRows().longValue();

      Optional<Progress> initial = progressRepo.findFirstByIdGreaterThanAndMigrationType(0L, type);

      Progress currentProgress = initial.orElseGet(
          () -> progressRepo.save(Progress.builder()
              .lastRowProcessed(0)
              .migrationType(type)
              .totalNumberOfRows(totalNumberOfRows)
              .build()));

      if (currentProgress.isRunning()) {
        log.warn(type.type() + " currently running");
        throw new Status400BadRequestException(type.type() + " currently running");
      }
      if (currentProgress.getLastRowProcessed() == totalNumberOfRows) {
        log.warn(type.type() + " was already completed");
        throw new Status400BadRequestException(type.type() + " was already completed");
      }
      currentProgress.setRunning(true);
      FieldList fields = bigqueryTable.getDefinition().getSchema().getFields();
      TableResult results;
      if (currentProgress.getLastRowProcessed() != 0L && currentProgress.getLastRowProcessed() < totalNumberOfRows) {
        log.warn(type.type() + " is now continuing");

        TableDataListOption[] options = new TableDataListOption[2];
        options[0] = TableDataListOption.pageSize(pageSize);
        options[1] = TableDataListOption.startIndex(currentProgress.getLastRowProcessed());

        results = bigquery.listTableData(tableId, options);
        return migrationIngestionFactory.getMigration(type)
            .initiateMigration(bigqueryTable.toBuilder().build(), tableId, currentProgress, results, fields);
      }

      results = bigquery.listTableData(tableId, TableDataListOption.pageSize(pageSize));
      return migrationIngestionFactory.getMigration(type)
          .initiateMigration(bigqueryTable.toBuilder().build(), tableId, currentProgress, results, fields);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }
}
