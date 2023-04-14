package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AuxLabelQueueSink {
	String ORIGINAL_QUEUE = "aux-label-accounting-queue.aux-label-accounting-group";

	String INPUT = "aux-label-accounting-input";
	String DLQ = ORIGINAL_QUEUE+".dlq";
	String PARKING_LOT = ORIGINAL_QUEUE+".parking-lot";

	@Input(INPUT)
	SubscribableChannel inputChannel();
}
