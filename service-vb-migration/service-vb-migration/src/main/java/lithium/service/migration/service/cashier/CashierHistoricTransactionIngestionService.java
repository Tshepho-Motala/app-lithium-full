package lithium.service.migration.service.cashier;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.HistoricCashierTransaction;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.cashier.CashierHistoricTransactionMigrationOutputQueue;
import lithium.service.migration.util.columns.CashierColumn;
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
    CashierHistoricTransactionMigrationOutputQueue.class
})
public class CashierHistoricTransactionIngestionService implements MigrationIngestion {

  private final CashierHistoricTransactionMigrationOutputQueue queue;
  private final ProgressRepo progressRepo;
  private final MigrationCredentialService migrationCredentialService;

  @Override
  public MigrationType getType() {
    return MigrationType.CASHIER_TRANSACTIONS_MIGRATION;
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
        SW.start("CashierTransactionsIngestion_pagerequest_" + pageCount);
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
        SW.start("CashierTransactionsIngestion_pagerequest_" + pageCount);
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
      return "Cashier Transactions Migration has started successfully";

    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {
   String customerId = Util.getStringFieldValue(fieldValue, CashierColumn.CUSTOMER_ID.columnName, fields);
    try {
     HistoricCashierTransaction historicCashierTransaction = buildHistoricCashierTransaction(fieldValue, fields,
         migrationCredentialService.getPlayerGuidByCustomerId(customerId));

     queue.CashierHistoricTransactionMigrationOutputStream()
         .send(MessageBuilder
             .withPayload(historicCashierTransaction)
             .build());
   }catch (Exception e){
     migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
         .customerId(customerId)
         .migrationType(getType().type())
         .exceptionMessage(e.getMessage())
         .build());
   }
    currentProgress.setCustomerId(customerId);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }

  private HistoricCashierTransaction buildHistoricCashierTransaction(FieldValueList fieldValueList, FieldList fields, String playerGuid) {
    return HistoricCashierTransaction.builder()
        .customerId(Util.getLongFieldValue(fieldValueList, CashierColumn.CUSTOMER_ID.columnName, fields))
        .transactionId(Util.getLongFieldValue(fieldValueList, CashierColumn.TRANSACTION_ID.columnName, fields))
        .type(Util.getStringFieldValue(fieldValueList, CashierColumn.TYPE.columnName, fields))
        .lithiumUserGuid(playerGuid)
        .status(Util.getStringFieldValue(fieldValueList, CashierColumn.STATUS.columnName, fields))
        .createdDate(Util.getDateFieldValue(fieldValueList, CashierColumn.CREATED_DATE.columnName, fields))
        .updatedDate(Util.getDateFieldValue(fieldValueList, CashierColumn.UPDATED_DATE.columnName, fields))
        .currencyCode(Util.getStringFieldValue(fieldValueList, CashierColumn.CURRENCY_CODE.columnName, fields))
        .amount(Util.getDoubleFieldValue(fieldValueList, CashierColumn.AMOUNT.columnName, fields))
        .operationTypeDescription(Util.getStringFieldValue(fieldValueList, CashierColumn.OPERATION_TYPE_DESCRIPTION.columnName, fields))
        .paymentMethodType(Util.getStringFieldValue(fieldValueList, CashierColumn.PAYMENT_METHOD_TYPE.columnName, fields))
        .paymentMethod(Util.getStringFieldValue(fieldValueList, CashierColumn.PAYMENT_METHOD.columnName, fields))
        .paymentProvider(Util.getStringFieldValue(fieldValueList, CashierColumn.PAYMENT_PROVIDER.columnName, fields))
        .operationGroupDescription(Util.getStringFieldValue(fieldValueList, CashierColumn.OPERATION_GROUP_DESCRIPTION.columnName, fields))
        .operationDescription(Util.getStringFieldValue(fieldValueList, CashierColumn.OPERATION_DESCRIPTION.columnName, fields))
        .operationCategory(Util.getStringFieldValue(fieldValueList, CashierColumn.OPERATION_CATEGORY.columnName, fields))
        .build();
  }

}
