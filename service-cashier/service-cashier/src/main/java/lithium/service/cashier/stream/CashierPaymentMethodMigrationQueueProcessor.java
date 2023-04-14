package lithium.service.cashier.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.services.HistoricTransactionsOperatorMigrationService;
import lithium.service.libraryvbmigration.data.dto.LegacyCashierPaymentMethod;
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
@EnableBinding(CashierPaymentMethodMigrationQueueSink.class)
@RequiredArgsConstructor
public class CashierPaymentMethodMigrationQueueProcessor implements IDeadLetterQueueHandler {

  private final HistoricTransactionsOperatorMigrationService service;

  private final RabbitTemplate rabbitTemplate;
  @Value("${lithium.services.cashier.dlqRetries:3}")
  private int maxDlqRetries;
  private final String parkingLotQueueName = CashierPaymentMethodMigrationQueueSink.PARKING_LOT;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Override
  @Bean
  @Qualifier("cashierPaymentMethodMigrationQueueParkingLot")
  public Queue parkingLotQueue() {
    return new Queue(CashierPaymentMethodMigrationQueueSink.PARKING_LOT);
  }

  @Override
  @RabbitListener(queues = CashierPaymentMethodMigrationQueueSink.DLQ)
  public void dlqHandle(Message failedMessage) {
    IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
  }

  @StreamListener(CashierPaymentMethodMigrationQueueSink.INPUT)
  void handle(LegacyCashierPaymentMethod paymentMethod,
      @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
      throws Status500InternalServerErrorException {
    try {
      service.createLegacyDomainMethodProcessor(paymentMethod);
    } catch (Exception e) {
      if (xRetries == getMaxDlqRetries()) {
        log.warn("Cashier Payment Methods Migration exception: {}", e.getMessage(), e);
        migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                .send(MessageBuilder
                        .withPayload(MigrationExceptionRecord.builder()
                                .customerId(paymentMethod.toString())
                                .migrationType(MigrationType.CASHIER_PAYMENT_METHODS_MIGRATION.type())
                                .exceptionMessage(e.getMessage())
                                .requestJson(paymentMethod.toString())
                                .build())
                        .build());
      }
      throw new Status500InternalServerErrorException(e.getMessage(), e.getStackTrace());
    }
  }
}
