package lithium.service.migration.service.cashier;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.LegacyCashierPaymentMethod;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.stream.cashier.CashierPaymentMethodMigrationOutputQueue;
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
    CashierPaymentMethodMigrationOutputQueue.class
})
public class CashierPaymentMethodIngestionService implements MigrationIngestion {

  private final CashierPaymentMethodMigrationOutputQueue queue;
  private final ProgressRepo progressRepo;
  private final ServiceVbMigrationConfigProperties properties;
  private final LithiumServiceClientFactory lithiumServiceClientFactory;

  @Override
  public MigrationType getType() {
    return MigrationType.CASHIER_PAYMENT_METHODS_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress currentProgress, TableResult results, FieldList fields)
      throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());
      setDomain(properties.getHistoricIngestion().getDomain());

      do {
        ++pageCount;
        SW.start("CashierPaymentMethodIngestion_pagerequest_" + pageCount);
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
        SW.start("CashierPaymentMethodIngestion_pagerequest_" + pageCount);
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

      return "Cashier Payment Method Migration has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String paymentMethodName = Util.getStringFieldValue(fieldValue, CashierColumn.PAYMENT_METHOD_NAME.columnName, fields);
    queue.CashierPaymentMethodMigrationOutputStream()
        .send(MessageBuilder
            .withPayload(LegacyCashierPaymentMethod.builder()
                .domainName(properties.getHistoricIngestion().getDomain())
                .paymentMethodName(paymentMethodName)
                .paymentProviderName(Util.getStringFieldValue(fieldValue, CashierColumn.PAYMENT_PROVIDER_NAME.columnName, fields))
                .build())
            .build());

    currentProgress.setCustomerId(paymentMethodName);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }

  private void setDomain(String domainName) throws LithiumServiceClientFactoryException {

    TransactionClient client = lithiumServiceClientFactory.target(TransactionClient.class, "service-cashier", true);

    client.findFirstDomain(domainName);
  }
}
