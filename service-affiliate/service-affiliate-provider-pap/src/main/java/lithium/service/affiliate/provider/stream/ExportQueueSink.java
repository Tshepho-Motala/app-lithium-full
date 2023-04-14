package lithium.service.affiliate.provider.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ExportQueueSink {

	String INPUT = "papexportinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
