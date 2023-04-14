package lithium.service.migration.service.cashier;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.cashier.client.objects.UserPaymentOptionsMigrationRequest;
import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.cashier.UserPaymentOptionsMigrationOutputQueue;
import lithium.service.migration.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableBinding({
    UserPaymentOptionsMigrationOutputQueue.class
})
public class CashierUPOIngestionService implements MigrationIngestion {

  private final UserPaymentOptionsMigrationOutputQueue queue;
  private final MigrationCredentialService migrationCredentialService;
  private final ProgressRepo progressRepo;
  private final ServiceVbMigrationConfigProperties properties;

  @Override
  public MigrationType getType() {
    return MigrationType.CASHIER_UPO_INGESTION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress currentProgress, TableResult results, FieldList fields)
      throws Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());

      do {
        ++pageCount;
        SW.start("CashierUPOIngestion_pagerequest_" + pageCount);
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
        SW.start("CashierUPOIngestion_pagerequest_" + pageCount);
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

      return "Cashier UPO Ingestion has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String customerId = Util.getStringFieldValue(fieldValue, "CustomerID", fields);

    MigrationCredential migrationCredential = null;
    try {
      migrationCredential = migrationCredentialService
          .findPlayerCredentialByCustomerId(customerId);
    } catch (Exception e) {
      migrationCredentialService.saveMigrationException(MigrationExceptionRecord.builder()
          .customerId(customerId)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .requestJson(customerId)
          .build());
      log.error("MigrationCredential not found for customer {}", customerId);
    }

    if (migrationCredential != null) {
      queue.outputQueue()
          .send(MessageBuilder
              .withPayload(
                  UserPaymentOptionsMigrationRequest.builder()
                      .domainName(properties.getHistoricIngestion().getDomain())
                      .userGuid(migrationCredential.getPlayerGuid())
                      .methodCode("nuvei-cc")
                      .processorCode("nuvei-cc")
                      .userTokenId(customerId)
                      .build()
              )
              .build());
    }

    currentProgress.setCustomerId(customerId);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }
}
