package lithium.service.migration.service.user;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.libraryvbmigration.data.dto.MigrationUserDetails;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.data.DataRequestService;
import lithium.service.migration.stream.VbMigrationOutputQueue;
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
    VbMigrationOutputQueue.class
})
public class UserIngestionService implements MigrationIngestion {

  private final VbMigrationOutputQueue vbMigrationOutputQueue;
  private final DataRequestService dataRequestService;
  private final ProgressRepo progressRepo;

  @Override
  public MigrationType getType() {
    return MigrationType.USER_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId, Progress currentProgress, TableResult results, FieldList fields)
      throws Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(currentProgress.getLastRowProcessed());

      do {
        ++pageCount;
        SW.start("HistoricUserIngestion_pagerequest_" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(currentProgress, rowCount, fieldValue, fields);
        }
        SW.stop();
        results = results.getNextPage();
      } while (!ObjectUtils.isEmpty(results) && results.hasNextPage());

      if(!ObjectUtils.isEmpty(results)) {
        //for the last page
        ++pageCount;
        SW.start("HistoricUserIngestion_pagerequest_" + pageCount);
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
      log.debug("User Migration performed successfully. Pages {} of page size {} with {}", pageCount, currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "User Migration has started successfully";
    } catch (Exception e){
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int currentRow, FieldValueList fieldValue, FieldList fields) {
    MigrationUserDetails migrationUserDetails = MigrationUserDetails.builder()
        .playerBasic(dataRequestService.getPlayerBasic(fieldValue, fields))
        .migrationCredential(dataRequestService.generateCredentials(fieldValue, fields))
        .build();

    vbMigrationOutputQueue.vbMigrationOutputStream()
        .send(MessageBuilder.withPayload(migrationUserDetails)
            .build());

    currentProgress.setCustomerId(migrationUserDetails.getMigrationCredential().getCustomerId());
    currentProgress.setLastRowProcessed(currentRow);
    progressRepo.save(currentProgress);
  }

}
