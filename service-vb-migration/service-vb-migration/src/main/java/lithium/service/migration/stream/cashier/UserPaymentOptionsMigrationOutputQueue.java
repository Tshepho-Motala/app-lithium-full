package lithium.service.migration.stream.cashier;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserPaymentOptionsMigrationOutputQueue {
	@Output("user-payment-options-migration-output")
	public MessageChannel outputQueue();
}
