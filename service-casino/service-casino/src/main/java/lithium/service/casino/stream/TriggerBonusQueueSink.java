package lithium.service.casino.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TriggerBonusQueueSink {
	String INPUT = "triggerbonusinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}