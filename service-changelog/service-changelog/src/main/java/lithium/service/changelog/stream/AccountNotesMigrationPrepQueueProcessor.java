package lithium.service.changelog.stream;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.changelog.services.HistoricNotesMigrationService;
import lithium.service.libraryvbmigration.data.dto.AccountingNotes;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
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
@EnableBinding({AccountNotesMigrationPrepQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class AccountNotesMigrationPrepQueueProcessor implements IDeadLetterQueueHandler {
  private final HistoricNotesMigrationService service;


  private final RabbitTemplate rabbitTemplate;
  @Value("${lithium.service.changelog.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = AccountNotesMigrationPrepQueueSink.PARKING_LOT;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Override
  @Bean
  @Qualifier("accountNotesMigrationPrepQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(AccountNotesMigrationPrepQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = AccountNotesMigrationPrepQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(AccountNotesMigrationPrepQueueSink.INPUT)
  void handle(AccountingNotes details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries){
    try {
     service.prepAccountNotesData(details);
    }catch (Status500UserInternalSystemClientException e) {
      log.debug("Account Notes Migration Preparation exception: {}", e.getMessage(), e);
      migrationExceptionOutputQueue.migrationExceptionOutputQueue()
              .send(MessageBuilder
                      .withPayload(MigrationExceptionRecord.builder()
                              .customerId(details.getCustomerId())
                              .migrationType(MigrationType.ACCOUNT_NOTES_MIGRATION_PREP.type())
                              .exceptionMessage(e.getMessage())
                              .requestJson(details.toString())
                              .build())
                      .build());
      throw new Status400BadRequestException(e.getMessage(), e.getStackTrace());
    }catch (Exception e){
      if (xRetries == getMaxDlqRetries()){
        log.error("Player preference exception: {}", e.getMessage(), e);
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(details.getCustomerId())
                                .migrationType(MigrationType.ACCOUNT_NOTES_MIGRATION_PREP.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(details.toString())
                                .build())
                        .build());
      }
      throw new Status400BadRequestException(e.getMessage(), e.getStackTrace());
    }
  }
}
