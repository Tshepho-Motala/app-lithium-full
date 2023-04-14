package lithium.service.affiliate.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TransactionQueueSink {

	String INPUT = "affiliatetransactioninput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
