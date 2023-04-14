package lithium.service.pushmsg.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PushMsgStreamOutputQueue {
	@Output("pushmsgoutput")
	public MessageChannel channel();
}