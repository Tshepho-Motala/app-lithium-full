package lithium.service.event.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface EventQueueSink {

	String INPUT = "eventinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
