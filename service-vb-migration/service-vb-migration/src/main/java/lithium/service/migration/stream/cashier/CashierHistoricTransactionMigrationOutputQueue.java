package lithium.service.migration.stream.cashier;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CashierHistoricTransactionMigrationOutputQueue {
  @Output("cashier-historic-transaction-migration-output")
  MessageChannel CashierHistoricTransactionMigrationOutputStream();
}
