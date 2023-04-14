package lithium.service.limit.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AutoRestrictionTriggerQueueSink {
	String INPUT = "autorestrictiontriggerinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
