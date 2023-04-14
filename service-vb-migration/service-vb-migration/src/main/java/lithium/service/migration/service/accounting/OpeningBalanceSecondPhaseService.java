package lithium.service.migration.service.accounting;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.accounting.objects.BalanceMigrationHistoricDetails;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.accounting.UpdatingBalanceOutputQueue;
import lithium.service.migration.util.columns.TransactionsColumn;
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
    UpdatingBalanceOutputQueue.class
})
public class OpeningBalanceSecondPhaseService implements MigrationIngestion {

  private final ServiceVbMigrationConfigProperties properties;
  private final MigrationCredentialService migrationCredentialService;
  private final UpdatingBalanceOutputQueue queue;
  private final ProgressRepo progressRepo;

  @Override
  public MigrationType getType() {
    return MigrationType.OPENING_BALANCE_PHASE2_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress currentProgress, TableResult results, FieldList fields) throws Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());

      do {
        ++pageCount;
        SW.start("OpeningBalanceSecondPhase_pagerequest_" + pageCount);
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
        SW.start("OpeningBalanceSecondPhase_pagerequest_" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(currentProgress, rowCount, fieldValue, fields);
        }
        SW.stop();
      }

      if (rowCount >= currentProgress.getTotalNumberOfRows()){
        currentProgress.setRunning(false);
        progressRepo.save(currentProgress);
      }

      log.debug("Opening Balance Second Phase Migration performed successfully. Pages {} of page size {} with {} rows", pageCount, currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "Opening Balance Second Phase Migration has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int currentRow, FieldValueList fieldValue, FieldList fields) {
    String customerId = Util.getStringFieldValue(fieldValue, TransactionsColumn.CUSTOMER_ID.TransactionColumnName, fields);
    try {
      BalanceMigrationHistoricDetails details = BalanceMigrationHistoricDetails.builder()
          .customerId(Util.getStringFieldValue(fieldValue, TransactionsColumn.CUSTOMER_ID.TransactionColumnName, fields))
          .domainName(properties.getHistoricIngestion().getDomain())
          .userGuid(migrationCredentialService.getPlayerGuidByCustomerId(customerId))
          .openingBalancePhase1(Util.getLongFieldValue(fieldValue, TransactionsColumn.OLD_BALANCE.TransactionColumnName, fields))
          .openingBalancePhase2(Util.getLongFieldValue(fieldValue, TransactionsColumn.NEW_BALANCE.TransactionColumnName, fields))
          .build();

      queue.UpdatingBalanceOutputStream()
          .send(MessageBuilder.withPayload(details)
              .build());
    } catch (Exception e){
      migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
          .customerId(customerId)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .build());
    }

    currentProgress.setCustomerId(customerId);
    currentProgress.setLastRowProcessed(currentRow);
    progressRepo.save(currentProgress);
  }

}
