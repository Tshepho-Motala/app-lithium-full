package lithium.service.sms.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SMSQueueSink {
	String INPUT = "smsinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}