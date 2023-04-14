package lithium.service.mail.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MailQueueSink {
	String INPUT = "mailinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}