package lithium.service.sms.provider.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProvidersStreamOutputQueue {
	@Output("smsprovidersregisteroutput")
	public MessageChannel channel();
}