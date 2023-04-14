package lithium.service.migration.service.user;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.limit.client.objects.SelfExclusionCoolOffPreferenceRequest;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.stream.SelfExclusionCoolOffPreferenceOutputQueue;
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
    SelfExclusionCoolOffPreferenceOutputQueue.class
})
public class SelfExclusionCoolOffPreferenceIngestionService implements MigrationIngestion {
  private final SelfExclusionCoolOffPreferenceOutputQueue queue;
  private final MigrationCredentialService migrationCredentialService;
  private final ProgressRepo progressRepo;

  @Override
  public MigrationType getType() {
    return MigrationType.SELF_EXCLUSION_COOL_OFF_PREFERENCE;
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
        SW.start("SelfExclusionCoolOffPreferenceIngestion_pagerequest_" + pageCount);
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
        SW.start("SelfExclusionCoolOffPreferenceIngestion_pagerequest_" + pageCount);
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
      return "Self Exclusion Cool Off Preference Migration has started successfully";

    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValueList, FieldList fields) {
    SelfExclusionCoolOffPreferenceRequest request = buildSelfExclusionCoolOffPreferenceRequest(fieldValueList, fields);

    String playerGuid = null;
    try {
      playerGuid = migrationCredentialService
          .getPlayerGuidByCustomerId(String.valueOf(request.getCustomerId()));
    } catch (Exception e) {
      // Given the requirement to make sure all users are created, this should not be a concern
      migrationCredentialService.saveMigrationException(MigrationExceptionRecord.builder()
          .customerId(String.valueOf(request.getCustomerId()))
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .requestJson(request.toString())
          .build());
      log.error("MigrationCredential not found for customer {}", request.getCustomerId());
    }

    // I'm doing this here, because I only want to queue valid messages to service-limit.
    // Given that we have not fixed all possible errors for user ingestion, local still has
    // a fair amount of non existant users. This will eventually be fixed.
    if (!ObjectUtils.isEmpty(playerGuid)) {
      request.setLithiumUserGuid(playerGuid);

      if (request.isCurrentlyActive() &&
          (request.isCoolOffRequest() || request.isSelfExclusionRequest())) {
        // No history. SE/cool off records are hard deleted in lithium.
        // There is a separate history storage.
        // Question for PO on whether it is necessary.
        // We only care about SE/cool off. Any other restriction types, if such is possible, is ignored.
        queue.selfExclusionCoolOffPreferenceOutputQueue().send(
            MessageBuilder
                .withPayload(request)
                .build()
        );
      }
    }

    currentProgress.setCustomerId(String.valueOf(request.getCustomerId()));
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }

  private SelfExclusionCoolOffPreferenceRequest buildSelfExclusionCoolOffPreferenceRequest(FieldValueList fieldValueList, FieldList fields) {
    String restrictionTypeId = Util.getStringFieldValue(fieldValueList, "Restriction_type_id", fields);

    return SelfExclusionCoolOffPreferenceRequest.builder()
        .customerId(Util.getLongFieldValue(fieldValueList, "Player_ID", fields))
        .requestedDate(Util.getDateFieldValue(fieldValueList, "Transaction_date", fields))
        .currentlyActive(Util.getLongFieldValue(fieldValueList, "Is_deletion", fields) == 1L)
        .startDate(Util.getDateFieldValue(fieldValueList, "Start_date", fields))
        .periodInDays(Util.getLongFieldValue(fieldValueList, "Period_Length", fields))
        .coolOffRequest(restrictionTypeId.contentEquals("9"))
        .selfExclusionRequest(restrictionTypeId.contentEquals("1"))
        .build();
  }
}
