package lithium.service.mail.provider.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProvidersStreamOutputQueue {
	@Output("mailprovidersregisteroutput")
	public MessageChannel channel();
}