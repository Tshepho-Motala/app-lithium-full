package lithium.service.cashier.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProcessorRegisterQueueSink {
	String INPUT = "processorsregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
