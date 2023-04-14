package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TransactionLabelQueueSink {
	String ORIGINAL_QUEUE = "transaction-label-accounting-queue.transaction-label-accounting-group";

	String INPUT = "transaction-label-accounting-input";
	String DLQ = ORIGINAL_QUEUE+".dlq";
	String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

	@Input(INPUT)
	SubscribableChannel inputChannel();
}
