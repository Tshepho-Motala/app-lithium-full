package lithium.service.stats.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface StatsOutputQueue {

	@Output("statsoutputv2")
	public MessageChannel outputQueue();
	
}
