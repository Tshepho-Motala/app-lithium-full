package lithium.service.user.search.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserRestrictionTriggerQueueSink {
	String INPUT = "userrestrictiontriggerinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
