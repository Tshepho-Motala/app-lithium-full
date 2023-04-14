package lithium.service.mail.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SystemMailQueueSink {
	String INPUT = "systemmailinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}