package lithium.service.accounting.domain.summary.stream;

import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AdjustmentQueueSink {
	String ORIGINAL_QUEUE = "adjustmentqueue.adjustmentgroup";

	String INPUT = "adjustmentinput";
	String DLQ = ORIGINAL_QUEUE + ".dlq";
	String PARKING_LOT = ORIGINAL_QUEUE + ".parkingLot";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
