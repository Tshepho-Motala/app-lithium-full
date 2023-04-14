package lithium.service.casino.stream.migration;

import lithium.service.casino.service.CasinoHistoricBetIngestionService;
import lithium.service.casino.stream.MigrationExceptionOutputQueue;
import lithium.service.libraryvbmigration.data.dto.BetsMigrationDetails;
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
@EnableBinding({CasinoBetsMigrationQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class CasinoBetsMigrationQueueProcessor implements IDeadLetterQueueHandler {

  private final RabbitTemplate rabbitTemplate;
  private final CasinoHistoricBetIngestionService service;
  @Value("${lithium.services.casino.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = CasinoBetsMigrationQueueSink.PARKING_LOT;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Override
  @Bean
  @Qualifier("casinoBetsMigrationQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(CasinoBetsMigrationQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = CasinoBetsMigrationQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(CasinoBetsMigrationQueueSink.INPUT)
  void handle(BetsMigrationDetails details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries) {
    log.debug("Received transaction via queue: {}", details);

    try {
      service.startBetIngestion(details);
    } catch (Exception e) {
      if (xRetries == getMaxDlqRetries()) {
        log.error("{} error occurred: {}", MigrationType.CASINO_BETS_MIGRATION.name(), e.getMessage(),
            e);
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(details.getCustomerId())
                                .migrationType(MigrationType.CASINO_BETS_MIGRATION.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(details.toString())
                                .build())
                        .build());
      }
    }
  }

}
