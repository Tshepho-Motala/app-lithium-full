package lithium.service.migration.stream.exception;

import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.migration.service.user.MigrationCredentialService;
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

@Slf4j
@Data
@EnableBinding(MigrationExceptionQueueSink.class)
@RequiredArgsConstructor
public class MigrationExceptionQueueProcessor implements IDeadLetterQueueHandler {

  @Value("${lithium.service.vb.migration.dlqRetries:3}")
  private int maxDlqRetries;
  private final RabbitTemplate rabbitTemplate;
  private final MigrationCredentialService service;
  private final String parkingLotQueueName = MigrationExceptionQueueSink.PARKING_LOT;

  @Override
  @Bean
  @Qualifier("migrationExceptionQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(MigrationExceptionQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = MigrationExceptionQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(MigrationExceptionQueueSink.INPUT)
  void handle(MigrationExceptionRecord migrationExceptionRecord){
    service.saveMigrationException(migrationExceptionRecord);
  }

}
