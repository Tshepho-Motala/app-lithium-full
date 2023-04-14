package lithium.service.sms.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProviderRegisterQueueSink {
	String INPUT = "smsprovidersregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}