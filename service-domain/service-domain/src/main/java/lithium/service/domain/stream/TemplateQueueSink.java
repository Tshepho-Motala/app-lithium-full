package lithium.service.domain.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TemplateQueueSink {
	String INPUT = "templateinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
