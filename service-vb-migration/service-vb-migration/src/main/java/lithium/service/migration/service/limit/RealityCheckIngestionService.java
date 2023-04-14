package lithium.service.migration.service.limit;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.limit.client.objects.RealityCheckMigrationDetails;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.limit.RealityCheckMigrationOutputQueue;
import lithium.service.migration.util.RealityCheckColumn;
import lithium.service.migration.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableBinding(RealityCheckMigrationOutputQueue.class)
public class RealityCheckIngestionService implements MigrationIngestion {

  private final ProgressRepo progressRepo;

  private final ServiceVbMigrationConfigProperties serviceVbMigrationConfigProperties;

  private final RealityCheckMigrationOutputQueue realityCheckMigrationOutputQueue;

  private final MigrationCredentialService migrationCredentialService;
  @Override
  public MigrationType getType() {
    return MigrationType.REALITY_CHECK_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
                                  Progress currentProgress, TableResult results, FieldList fields)
          throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = 0;

      do {
        ++pageCount;
        SW.start("RealityCheckMigration_PageRequest" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(currentProgress, rowCount, fieldValue, fields);
        }
        SW.stop();
        results = results.getNextPage();
      } while (!ObjectUtils.isEmpty(results) && results.hasNextPage());

      if (!ObjectUtils.isEmpty(results)) {
        //for the last page
        ++pageCount;
        SW.start("RealityCheckMigration_PageRequest" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(currentProgress, rowCount, fieldValue, fields);
        }
        SW.stop();
      }

      if (rowCount >= currentProgress.getTotalNumberOfRows()) {
        currentProgress.setRunning(false);
        progressRepo.save(currentProgress);
      }

      log.debug("Query pagination performed successfully. Pages {} of page size {} with {} rows", pageCount, currentProgress.getTotalNumberOfRows(),
              rowCount);
      return "Reality Check has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());

    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {

    String customerIdFromBigQuery = Util.getStringFieldValue(fieldValue, RealityCheckColumn.CUSTOMER_ID.RealityCheckColumnName, fields);
    Long interval = Util.getLongFieldValue(fieldValue, RealityCheckColumn.CUSTOMER_ID.RealityCheckColumnName, fields);
      try {
        if (interval == null) {
          interval = 0L;
        }
        if (interval != 0L && interval >= 60) {
          interval = 3600000L;
        }
        if (interval != 0L && interval < 60) {
          interval = 1800000L;
        }

        if (interval != 0L) {

          realityCheckMigrationOutputQueue.RealityCheckMigrationOutputStream()
              .send(MessageBuilder
                  .withPayload(RealityCheckMigrationDetails.builder()
                      .playerGuid(migrationCredentialService.getPlayerGuidByCustomerId(customerIdFromBigQuery))
                      .realityCheckInterval(interval)
                      .customerID(customerIdFromBigQuery)
                      .build())
                  .build());
        }

      }catch (Exception e){
        migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
            .customerId(customerIdFromBigQuery)
            .migrationType(getType().type())
            .exceptionMessage(e.getMessage())
            .build());
      }
      currentProgress.setCustomerId(customerIdFromBigQuery);
      currentProgress.setLastRowProcessed(rowCount);
      progressRepo.save(currentProgress);
  }


  }
