package lithium.service.pushmsg.provider.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProvidersStreamOutputQueue {
	@Output("pushmsgprovidersregisteroutput")
	public MessageChannel channel();
}