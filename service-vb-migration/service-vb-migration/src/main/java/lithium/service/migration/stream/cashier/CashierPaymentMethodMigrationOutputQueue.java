package lithium.service.migration.stream.cashier;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CashierPaymentMethodMigrationOutputQueue {
  @Output("cashier-payment-method-migration-output")
  MessageChannel CashierPaymentMethodMigrationOutputStream();
}
