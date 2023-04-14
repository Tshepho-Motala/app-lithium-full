package lithium.service.user.stream;

import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.dto.MigrationUserDetails;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.user.client.objects.User;
import lithium.service.user.services.HistoricRegistrationIngestionService;
import lithium.service.user.stream.credential.MigrationCredentialOutputQueue;
import lithium.service.user.stream.exception.MigrationExceptionOutputQueue;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableBinding({MigrationCredentialOutputQueue.class, MigrationExceptionOutputQueue.class, VbMigrationQueueSink.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VbMigrationQueueProcessor implements IDeadLetterQueueHandler {

  @Getter
  private final RabbitTemplate rabbitTemplate;
  @Value("${lithium.services.accounting.domain.summary.dlq-retries:3}")
  @Getter
  private int maxDlqRetries;
  @Getter
  private final String parkingLotQueueName = VbMigrationQueueSink.PARKING_LOT;
  private final HistoricRegistrationIngestionService service;

  private final MigrationCredentialOutputQueue migrationCredentialOutputQueue;

  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Override
  @Bean
  @Qualifier("vbMigrationQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(VbMigrationQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = VbMigrationQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(VbMigrationQueueSink.INPUT)
  public void queueListener(MigrationUserDetails migrationUserDetails,
      @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
      throws Status500InternalServerErrorException {
    try {
      User player = service.createBasicUser(migrationUserDetails.getPlayerBasic());
      migrationUserDetails.getMigrationCredential().setPlayerGuid(player.guid());
      migrationCredentialOutputQueue.migrationCredentialOutputQueue()
          .send(MessageBuilder
              .withPayload(migrationUserDetails.getMigrationCredential())
              .build());
    } catch (ConstraintViolationException e) {
      List<String> errorMessages = e.getConstraintViolations()
          .stream()
          .map(ConstraintViolation::getMessage).toList();
      migrationExceptionOutputQueue.migrationExceptionOutputQueue()
          .send(MessageBuilder
              .withPayload(MigrationExceptionRecord.builder()
                  .customerId(migrationUserDetails.getMigrationCredential().getCustomerId())
                  .migrationType(MigrationType.USER_MIGRATION.type())
                  .exceptionMessage(errorMessages.toString())
                  .requestJson(migrationUserDetails.toString())
                  .build())
              .build());
    } catch (Exception e) {
      if (xRetries == getMaxDlqRetries()) {
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
            .send(MessageBuilder
                .withPayload(MigrationExceptionRecord.builder()
                    .customerId(migrationUserDetails.getMigrationCredential().getCustomerId())
                    .migrationType(MigrationType.USER_MIGRATION.type())
                    .exceptionMessage(e.getMessage())
                    .requestJson(migrationUserDetails.getMigrationCredential().toString())
                    .build())
                .build());
      }
      throw new Status500InternalServerErrorException(e.getMessage(), e);
    }

  }

}
