package lithium.service.migration.service.changelog;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import java.text.ParseException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.AccountingNotes;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.changelog.AccountNotesMigrationOutputQueue;
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
    AccountNotesMigrationOutputQueue.class
})
public class AccountNotesMigrationService implements MigrationIngestion {

  private final ServiceVbMigrationConfigProperties properties;
  private final ProgressRepo progressRepo;
  private final AccountNotesMigrationOutputQueue queue;

  private final MigrationCredentialService migrationCredentialService;

  @Override
  public MigrationType getType() {
    return MigrationType.ACCOUNT_NOTES_MIGRATION;
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
        SW.start("AccountNotesMigration_pagerequest_" + pageCount);
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
        SW.start("AccountNotesMigration_pagerequest_" + pageCount);
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

      log.debug("Account Notes Migration performed successfully. Pages {} of page size {} with {} rows", pageCount,
          currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "Account Notes Migration has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) throws ParseException {
    String customerId = Util.getStringFieldValue(fieldValue, AccountingNotesColumn.CUSTOMER_ID.columnName, fields);
    String subCategory = Util.getStringFieldValue(fieldValue, AccountingNotesColumn.SUB_CATEGORY_NAME.columnName, fields);
    String category = Util.getStringFieldValue(fieldValue, AccountingNotesColumn.CATEGORY_NAME.columnName, fields);
    boolean isDeleted = Util.getBooleanFieldValue(fieldValue, AccountingNotesColumn.IS_DELETE.columnName, fields);

    if(!ObjectUtils.isEmpty(category) && "Accounts".equals(category)){
      category = "Account";
    }

    if(ObjectUtils.isEmpty(subCategory) || "General".equals(subCategory)){
      subCategory = category;
    }

    try {
      queue.accountNotesOutputQueue()
          .send(MessageBuilder.withPayload(AccountingNotes.builder()
              .customerId(customerId)
              .domainName(properties.getHistoricIngestion().getDomain())
              .playerguid(migrationCredentialService.getPlayerGuidByCustomerId(customerId))
              .entityId(Util.getLongFieldValue(fieldValue, AccountingNotesColumn.ID.columnName, fields))
              .category(category)
              .subCategory(subCategory)
              .comments(Util.getStringFieldValue(fieldValue, AccountingNotesColumn.COMMENT.columnName, fields))
              .creationDate(Util.getDateFieldValue(fieldValue, AccountingNotesColumn.CREATED_DATE.columnName, fields))
              .deletionDate(isDeleted ? Util.getUpdatedDateFieldValue(fieldValue, AccountingNotesColumn.DELETE_DATE.columnName, fields) : null)
              .deleted(isDeleted)
              .build()).build());
    }catch (Exception e){
      migrationCredentialService.saveMigrationException(MigrationExceptionRecord.builder()
          .customerId(customerId)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .build());
    }
    currentProgress.setCustomerId(customerId);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }

}
