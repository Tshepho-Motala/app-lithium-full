package lithium.service.event.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventOutputQueue {

	@Output("eventoutput")
	public MessageChannel outputQueue();
	
}
