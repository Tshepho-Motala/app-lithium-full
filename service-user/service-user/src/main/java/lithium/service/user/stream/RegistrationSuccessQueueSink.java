package lithium.service.user.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RegistrationSuccessQueueSink {

	String INPUT = "registration-success-input";
	
	@Input(INPUT)
	SubscribableChannel registrationSuccessChannel();
	
}
