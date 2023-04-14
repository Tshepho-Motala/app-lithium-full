package lithium.cache;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CacheClearMessageSenderChannel {

	@Output("clearcacheoutput")
	public MessageChannel channel();
	
}
