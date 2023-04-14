package lithium.service.limit.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AutoRestrictionTriggerOutputQueue {
	@Output("autorestrictiontriggeroutput")
	public MessageChannel outputQueue();
}