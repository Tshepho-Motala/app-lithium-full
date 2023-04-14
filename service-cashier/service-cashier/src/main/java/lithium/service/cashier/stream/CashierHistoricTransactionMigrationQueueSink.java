package lithium.service.cashier.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CashierHistoricTransactionMigrationQueueSink {
  String ORIGINAL_QUEUE = "cashier-historic-transaction-migration-queue.cashier-historic-transaction-migration-group";

  String INPUT = "cashier-historic-transaction-migration-input";

  String DLQ = ORIGINAL_QUEUE+".dlq";
  String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

  @Input(INPUT)
  SubscribableChannel inputChannel();
}
