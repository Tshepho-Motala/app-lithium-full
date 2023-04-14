package lithium.service.mail.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MailStreamOutputQueue {
	@Output("mailoutput")
	public MessageChannel channel();
}