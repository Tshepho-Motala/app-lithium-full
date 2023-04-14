package lithium.service.accounting.provider.internal.stream;

import lithium.service.accounting.objects.AccountMigrationHistoricDetails;
import lithium.service.accounting.provider.internal.conditional.NotReadOnlyConditional;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.services.HistoricTransactionIngestionService;
import lithium.service.accounting.provider.internal.services.QueueRateLimiter;
import lithium.service.accounting.provider.internal.services.TransactionTypeService;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import org.springframework.context.annotation.Conditional;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
@Conditional(NotReadOnlyConditional.class)
@EnableBinding({HistoricTransactionQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class HistoricTransactionQueueProcessor implements IDeadLetterQueueHandler {

  private final HistoricTransactionIngestionService historicTransactionIngestionService;

  private final TransactionTypeService transactionTypeService;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Autowired
  RabbitTemplate rabbitTemplate;
  @Autowired
  QueueRateLimiter queueRateLimiter;
  private final ModelMapper modelMapper;
  private final AccountRepository accountRepository;

  @Value("${lithium.service.accounting.provider.internal.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = HistoricTransactionQueueSink.PARKING_LOT;

  @Override
  @Bean
  @Qualifier("transactionLabelQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(HistoricTransactionQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = HistoricTransactionQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(HistoricTransactionQueueSink.INPUT)
  void handle(AccountMigrationHistoricDetails accountMigrationHistoricDetails, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries) throws Exception {
    log.debug("Received transaction via queue: {}", accountMigrationHistoricDetails);
    try {
      historicTransactionIngestionService.initiateIngestion(accountMigrationHistoricDetails);
    } catch (Exception e) {
      if (xRetries == getMaxDlqRetries()) {
        log.error("Historic Accounting transaction Migration Que Argument exception", e);
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(accountMigrationHistoricDetails.getCustomerId())
                                .migrationType(MigrationType.TRANSACTION_MIGRATION.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(accountMigrationHistoricDetails.toString())
                                .build())
                        .build());

      }
      throw new Exception(e.getMessage(), e);
    }
  }
}
