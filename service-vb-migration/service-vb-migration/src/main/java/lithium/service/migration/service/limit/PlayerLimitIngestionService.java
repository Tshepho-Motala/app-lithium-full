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
import lithium.service.limit.client.objects.PlayerLimitPreferenceMigrationDetails;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.migration.stream.limit.PlayerLimitPreferencesMigrationOutputQueue;
import lithium.service.migration.util.Util;
import lithium.service.migration.util.columns.PlayerLimitColumn;
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
    PlayerLimitPreferencesMigrationOutputQueue.class
})
public class PlayerLimitIngestionService implements MigrationIngestion {

  private final PlayerLimitPreferencesMigrationOutputQueue playerLimitPreferencesMigrationOutputQueue;

  private final ProgressRepo progressRepo;

  private final MigrationCredentialService migrationCredentialService;

  private final ServiceVbMigrationConfigProperties serviceVbMigrationConfigProperties;


  @Override
  public MigrationType getType() {
    return MigrationType.PLAYER_LIMIT_PREFERENCES_MIGRATION;
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
        SW.start("PlayerLimitMigration_PageRequest" + pageCount);
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
        SW.start("PlayerLimitMigration_PageRequest" + pageCount);
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

      log.debug("Player limit preferences migration performed successfully. Pages {} of page size {} with {} rows", pageCount, currentProgress.getTotalNumberOfRows(),
          rowCount);
      return "Player limit preferences has started successfully";
    } catch (Exception e) {
      currentProgress.setRunning(false);
      progressRepo.save(currentProgress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());

    }
  }

  private void getCurrentRow(Progress currentProgress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String limitTypeFromBigQuery = Util.getStringFieldValue(fieldValue, PlayerLimitColumn.LIMIT_TYPE.PlayerLimitColumnName, fields);
    String customerID = Util.getStringFieldValue(fieldValue, PlayerLimitColumn.CUSTOMER_ID.PlayerLimitColumnName, fields);
    String granularityFromBigQuery = Util.getStringFieldValue(fieldValue, PlayerLimitColumn.GRANULARITY.PlayerLimitColumnName, fields);
    try {
      PlayerLimitPreferenceMigrationDetails details = PlayerLimitPreferenceMigrationDetails.builder()
          .customerID(customerID)
          .domainName(serviceVbMigrationConfigProperties.getHistoricIngestion().getDomain())
          .playerGuid(migrationCredentialService.getPlayerGuidByCustomerId(customerID))
          .limitType(Util.getLimitType(limitTypeFromBigQuery))
          .granularity(Util.getGranularity(granularityFromBigQuery))
          .amountCents(Util.getLongFieldValue(fieldValue, PlayerLimitColumn.AMOUNT_CENTS.PlayerLimitColumnName, fields))
          .build();

      playerLimitPreferencesMigrationOutputQueue.PlayerLimitPreferencesMigrationOutputStream()
          .send(MessageBuilder.withPayload(details)
              .build());
    }catch(Exception e){
      migrationCredentialService.saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord.builder()
          .customerId(customerID)
          .migrationType(getType().type())
          .exceptionMessage(e.getMessage())
          .build());
    }
    currentProgress.setCustomerId(customerID);
    currentProgress.setLastRowProcessed(rowCount);
    progressRepo.save(currentProgress);
  }
}
