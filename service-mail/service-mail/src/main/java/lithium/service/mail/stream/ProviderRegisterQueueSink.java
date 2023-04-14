package lithium.service.mail.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProviderRegisterQueueSink {
	String INPUT = "mailprovidersregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}