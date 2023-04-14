package lithium.service.migration.service.casino;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import java.util.Date;
import java.util.Objects;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.libraryvbmigration.data.dto.GameMigrationDetails;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.factory.MigrationIngestion;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.stream.casino.CasinoGameMigrationOutputQueue;
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
    CasinoGameMigrationOutputQueue.class
})
public class GamesCasinoIngestionService implements MigrationIngestion {

  private final ServiceVbMigrationConfigProperties properties;
  private final ProgressRepo progressRepo;
  private final CasinoGameMigrationOutputQueue queue;

  @Override
  public MigrationType getType() {
    return MigrationType.CASINO_GAMES_MIGRATION;
  }

  @Override
  public String initiateMigration(TableInfo bigqueryTable, TableId tableId,
      Progress progress, TableResult results, FieldList fields)
      throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
    try {
      int pageCount = 0;
      int rowCount = Math.toIntExact(progress.getLastRowProcessed());

      do {
        ++pageCount;
        SW.start("CasinoGamesMigration_pagerequest_" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(progress, rowCount, fieldValue, fields);
        }
        SW.stop();
        results = results.getNextPage();
      } while (!ObjectUtils.isEmpty(results) && results.hasNextPage());

      if (!ObjectUtils.isEmpty(results)) {
        //for the last page
        ++pageCount;
        SW.start("CasinoGamesMigration_pagerequest_" + pageCount);
        for (FieldValueList fieldValue : results.getValues()) {
          rowCount++;
          getCurrentRow(progress, rowCount, fieldValue, fields);
        }
        SW.stop();
      }

      if (rowCount >= progress.getTotalNumberOfRows()){
        progress.setRunning(false);
        progressRepo.save(progress);
      }

      log.debug("Casino Games Migration performed successfully. Pages {} of page size {} with {} rows", pageCount, progress.getTotalNumberOfRows(),
          rowCount);
      return "Casino Games Migration has started successfully";
    } catch (Exception e) {
      progress.setRunning(false);
      progressRepo.save(progress);
      log.debug("Error: {}", e.getMessage(), e);
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  private void getCurrentRow(Progress progress, int rowCount, FieldValueList fieldValue, FieldList fields) {
    String gameId = Util.getStringFieldValue(fieldValue, TransactionsColumn.GAME_ID.TransactionColumnName, fields);
    queue.CasinoGameMigrationOutputStream()
        .send(MessageBuilder.withPayload(
            GameMigrationDetails.builder()
                .commercialName(Util.getStringFieldValue(fieldValue, TransactionsColumn.GAME_NAME.TransactionColumnName, fields))
                .currencyCode(properties.getHistoricIngestion().getDomainCurrencyCode())
                .dateNow(new Date())
                .description("MIGRATED LEGACY GAME")
                .domainName(properties.getHistoricIngestion().getDomain())
                .gameName(Util.getStringFieldValue(fieldValue, TransactionsColumn.GAME_NAME.TransactionColumnName, fields))
                .providerGameId(gameId)
                .providerGuid(properties.getHistoricIngestion().getLegacyGuid() + Objects.requireNonNull(
                    Util.getStringFieldValue(fieldValue, TransactionsColumn.SUB_PROVIDER_NAME.TransactionColumnName, fields)).toLowerCase())
                .rtp(Util.getBigDecimalFieldValue(fieldValue, TransactionsColumn.RTP.TransactionColumnName, fields))
                .build()
        ).build());

    progress.setCustomerId("GameId: " + gameId);
    progress.setLastRowProcessed(rowCount);
    progressRepo.save(progress);

  }

}
