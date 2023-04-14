package lithium.service.migration.service.changelog;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.client.changelog.ChangeLogClient;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.AccountingNotes;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.changelog.AccountNotesMigrationPrepOutputQueue;
import lithium.service.migration.util.Util;
import lithium.service.migration.util.columns.AccountingNotesColumn;
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
    AccountNotesMigrationPrepOutputQueue.class
})
public class AccountNotesMigrationPrepService implements MigrationIngestion {

  private final ProgressRepo progressRepo;
  private final AccountNotesMigrationPrepOutputQueue queue;
  private final LithiumServiceClientFactory clientFactory;
  private final ServiceVbMigrationConfigProperties properties;
  private final MigrationCredentialService migrationCredentialService;

  @Override
  public MigrationType getType() {
    return MigrationType.ACCOUNT_NOTES_MIGRATION_PREP;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress currentProgress, TableResult results, FieldList fields)
      throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    ChangeLogClient client = clientFactory.target(ChangeLogClient.class, "service-changelog", true);

    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());
      client.addDomain(properties.getHistoricIngestion().getDomain());
      do {
        ++pageCount;
        SW.start("AccountNotesMigrationPreparation_pagerequest_" + pageCount);
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
        SW.start("AccountNotesMigrationPreparation_pagerequest_" + pageCount);
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

      log.debug("Account Notes Migration performed successfully Preparation. Pages {} of page size {} with {} rows", pageCount,
          currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "Account Notes Migration Preparation has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String subCategory = Util.getStringFieldValue(fieldValue, AccountingNotesColumn.SUB_CATEGORY_NAME.columnName, fields);
    String category = Util.getStringFieldValue(fieldValue, AccountingNotesColumn.CATEGORY_NAME.columnName, fields);

    if(!ObjectUtils.isEmpty(category) && "Accounts".equals(category)){
      category = "Account";
    }

    if(ObjectUtils.isEmpty(subCategory) || "General".equals(subCategory)){
      subCategory = category;
    }
    try {
      queue.accountNotesPrepOutputQueue()
          .send(MessageBuilder.withPayload(AccountingNotes.builder()
              .category(category).subCategory(subCategory).build()).build());
    }catch (Exception e){
      migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
          .customerId(subCategory)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .requestJson(category + ":" + subCategory).build());
    }
    currentProgress.setCustomerId(subCategory);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }
}
