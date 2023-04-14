package lithium.service.sms.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SMSStreamOutputQueue {
	@Output("smsoutput")
	public MessageChannel channel();
}