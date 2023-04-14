package lithium.service.pushmsg.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PushMsgQueueSink {
	String INPUT = "pushmsginput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}