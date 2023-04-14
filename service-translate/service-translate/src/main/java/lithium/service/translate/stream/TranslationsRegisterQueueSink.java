package lithium.service.translate.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TranslationsRegisterQueueSink {

	String INPUT = "translationsregisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
	
}
