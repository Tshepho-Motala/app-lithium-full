package lithium.service.user.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RegisterRolesQueueSink {

	String INPUT = "rolesregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
