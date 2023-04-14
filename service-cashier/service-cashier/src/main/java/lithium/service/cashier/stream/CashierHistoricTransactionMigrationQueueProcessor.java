package lithium.service.cashier.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.HistoricTransactionsOperatorMigrationService;
import lithium.service.cashier.services.TransactionStatusService;
import lithium.service.cashier.services.UserService;
import lithium.service.libraryvbmigration.data.dto.HistoricCashierTransaction;
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
@RequiredArgsConstructor
@EnableBinding({CashierHistoricTransactionMigrationQueueSink.class, MigrationExceptionOutputQueue.class})
public class CashierHistoricTransactionMigrationQueueProcessor implements IDeadLetterQueueHandler {

  private final HistoricTransactionsOperatorMigrationService service;
  private final TransactionStatusService transactionStatusService;
  private final UserService userService;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;
  private final RabbitTemplate rabbitTemplate;
  @Value("${lithium.services.cashier.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = CashierHistoricTransactionMigrationQueueSink.PARKING_LOT;

  @Override
  @Bean
  @Qualifier("cashierHistoricTransactionMigrationQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(CashierHistoricTransactionMigrationQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = CashierHistoricTransactionMigrationQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(CashierHistoricTransactionMigrationQueueSink.INPUT)
  void handle(HistoricCashierTransaction historicCashierTransaction,
      @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
      throws Status500InternalServerErrorException {
    try {
      User user = userService.findOrCreateRetryable(historicCashierTransaction.getLithiumUserGuid());
      TransactionStatus transactionStatus = null;
      if (historicCashierTransaction.getStatus().contentEquals("Approved")) {
        transactionStatus = transactionStatusService.findOrCreateRetryable("APPROVED", true);
      } else if (historicCashierTransaction.getStatus().contentEquals("Rejected")) {
        transactionStatus = transactionStatusService.findOrCreateRetryable("DECLINED", false);
      }

      service.createHistoricCashierTransaction(historicCashierTransaction, user, transactionStatus);
    } catch (Exception e) {
      if (xRetries == getMaxDlqRetries()) {
        log.error("Cashier Historic Transaction Migration exception: {}", e.getMessage(), e);
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(String.valueOf(historicCashierTransaction.getCustomerId()))
                                .migrationType(MigrationType.CASHIER_TRANSACTIONS_MIGRATION.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(historicCashierTransaction.toString())
                                .build())
                        .build());
      }
      throw new Status500InternalServerErrorException(e.getMessage(), e.getStackTrace());
    }
  }
}
