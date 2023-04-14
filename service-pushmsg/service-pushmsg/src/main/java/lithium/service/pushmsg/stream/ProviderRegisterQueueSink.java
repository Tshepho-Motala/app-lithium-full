package lithium.service.pushmsg.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProviderRegisterQueueSink {
	String INPUT = "pushmsgprovidersregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}