package lithium.service.stats.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface StatsLoginOutputQueue {
	@Output("statsloginoutput")
	public MessageChannel outputQueue();
}