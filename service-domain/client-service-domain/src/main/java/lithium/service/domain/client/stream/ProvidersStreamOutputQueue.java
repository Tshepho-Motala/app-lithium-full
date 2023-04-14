package lithium.service.domain.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProvidersStreamOutputQueue {
	@Output("providersregisteroutput")
	public MessageChannel channel();
}