package lithium.service.migration.service.casino;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import java.util.Objects;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.BetsMigrationDetails;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.casino.CasinoBetsMigrationOutputQueue;
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
    CasinoBetsMigrationOutputQueue.class
})
public class CasinoBetsIngestionService implements MigrationIngestion {

  private final ServiceVbMigrationConfigProperties properties;
  private final ProgressRepo progressRepo;
  private final CasinoBetsMigrationOutputQueue queue;

  private final MigrationCredentialService migrationCredentialService;

  @Override
  public MigrationType getType() {
    return MigrationType.CASINO_BETS_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress currentProgress, TableResult results, FieldList fields)
      throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());

      do {
        ++pageCount;
        SW.start("CasinoBetsMigration_pagerequest_" + pageCount);
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
        SW.start("CasinoBetsMigration_pagerequest_" + pageCount);
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

      log.debug("Casino Bets Migration performed successfully. Pages {} of page size {} with {} rows", pageCount, currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "Casino Bets Migration has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress progress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String customerId = Util.getStringFieldValue(fieldValue, TransactionsColumn.CUSTOMER_ID.TransactionColumnName, fields);
    try {
      queue.CasinoBetsMigrationOutputStream()
          .send(MessageBuilder.withPayload(
              BetsMigrationDetails.builder()
                  .domainName(properties.getHistoricIngestion().getDomain())
                  .customerId(customerId)
                  .currencyCode(properties.getHistoricIngestion().getDomainCurrencyCode())
                  .playerGuid(migrationCredentialService.getPlayerGuidByCustomerId(customerId))
                  .betId(Util.getStringFieldValue(fieldValue, TransactionsColumn.BET_ID.TransactionColumnName, fields))
                  .providerGameId(Util.getStringFieldValue(fieldValue, TransactionsColumn.GAME_ID.TransactionColumnName, fields))
                  .providerGuid(properties.getHistoricIngestion().getLegacyGuid() + Objects.requireNonNull(
                      Util.getStringFieldValue(fieldValue, TransactionsColumn.SUB_PROVIDER_NAME.TransactionColumnName, fields)).toLowerCase())
                  .placementDateTime(Util.getTimestampFieldValue(fieldValue, TransactionsColumn.PLACEMENT_DATE_TIME.TransactionColumnName, fields))
                  .settlementDateTime(Util.getTimestampFieldValue(fieldValue, TransactionsColumn.SETTLEMENT_DATE_TIME.TransactionColumnName, fields))
                  .amount(Util.getDoubleFieldValue(fieldValue, TransactionsColumn.TURNOVER.TransactionColumnName, fields))
                  .returns(Util.getDoubleFieldValue(fieldValue, TransactionsColumn.RETURN.TransactionColumnName, fields))
                  .build()
          ).build());
    }catch(Exception e){
      migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
          .customerId(customerId)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .build());
    }
    progress.setCustomerId(customerId);
    progress.setLastRowProcessed(rowCount);
    progressRepo.save(progress);

  }

}
