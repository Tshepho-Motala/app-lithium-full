package lithium.service.cashier.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CashierPaymentMethodMigrationQueueSink {
  String ORIGINAL_QUEUE = "cashier-payment-method-migration-queue.cashier-payment-method-migration-group";

  String INPUT = "cashier-payment-method-migration-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
