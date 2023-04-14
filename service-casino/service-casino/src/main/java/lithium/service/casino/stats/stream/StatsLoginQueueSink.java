package lithium.service.casino.stats.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface StatsLoginQueueSink {
	String INPUT = "statslogininput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}