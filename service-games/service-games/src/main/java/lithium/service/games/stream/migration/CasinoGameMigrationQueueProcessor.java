package lithium.service.games.stream.migration;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.games.services.MigrationGamesService;
import lithium.service.libraryvbmigration.data.dto.GameMigrationDetails;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
@EnableBinding({CasinoGameMigrationQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class CasinoGameMigrationQueueProcessor implements IDeadLetterQueueHandler {

  private final RabbitTemplate rabbitTemplate;
  private final MigrationGamesService service;
  @Value("${lithium.services.games.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = CasinoGameMigrationQueueSink.PARKING_LOT;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;


  @Override
  @Bean
  @Qualifier("casinoGameMigrationQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(CasinoGameMigrationQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = CasinoGameMigrationQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(CasinoGameMigrationQueueSink.INPUT)
  void handle(GameMigrationDetails details,
      @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
      throws Status500InternalServerErrorException {
    try {
      service.initiateProviderService(details);
    } catch (Exception e) {
      log.warn("{} exception: {}", MigrationType.CASINO_GAMES_MIGRATION, e.getMessage(), e);
      if (xRetries == getMaxDlqRetries()) {
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(details.getCustomerId())
                                .migrationType(MigrationType.CASINO_GAMES_MIGRATION.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(details.toString())
                                .build())
                        .build());
      }
      throw new Status500InternalServerErrorException(e.getMessage(), e.getStackTrace());
    }
  }
}