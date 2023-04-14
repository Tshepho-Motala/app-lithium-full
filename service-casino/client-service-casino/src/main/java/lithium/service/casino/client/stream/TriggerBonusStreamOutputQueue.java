package lithium.service.casino.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TriggerBonusStreamOutputQueue {
	@Output("triggerbonusoutput")
	public MessageChannel channel();
}