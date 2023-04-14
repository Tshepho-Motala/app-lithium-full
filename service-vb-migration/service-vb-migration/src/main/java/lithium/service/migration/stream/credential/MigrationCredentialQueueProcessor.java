package lithium.service.migration.stream.credential;

import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
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
@EnableBinding(MigrationCredentialQueueSink.class)
@RequiredArgsConstructor
public class MigrationCredentialQueueProcessor implements IDeadLetterQueueHandler {

  @Value("${lithium.service.vb.migration.dlqRetries:3}")
  private int maxDlqRetries;
  private final RabbitTemplate rabbitTemplate;
  private final MigrationCredentialService service;
  private final String parkingLotQueueName = MigrationCredentialQueueSink.PARKING_LOT;

  @Override
  @Bean
  @Qualifier("migrationCredentialQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(MigrationCredentialQueueSink.PARKING_LOT);}

  @Override
  @RabbitListener(queues = MigrationCredentialQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(MigrationCredentialQueueSink.INPUT)
  void handle(MigrationCredential credential){
    service.saveMigrationCredential(credential);
  }

}
