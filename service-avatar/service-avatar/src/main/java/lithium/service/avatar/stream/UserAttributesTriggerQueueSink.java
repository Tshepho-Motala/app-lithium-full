package lithium.service.avatar.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserAttributesTriggerQueueSink {
	String INPUT = "userattributestriggerinput";

	@Input(INPUT)
	SubscribableChannel inputChannel();
}
