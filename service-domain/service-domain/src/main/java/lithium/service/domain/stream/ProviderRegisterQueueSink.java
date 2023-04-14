package lithium.service.domain.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProviderRegisterQueueSink {
	String INPUT = "providersregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
