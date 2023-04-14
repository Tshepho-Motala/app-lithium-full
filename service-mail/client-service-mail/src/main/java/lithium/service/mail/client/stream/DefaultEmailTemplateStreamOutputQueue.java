package lithium.service.mail.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DefaultEmailTemplateStreamOutputQueue {
	@Output("defaultemailtemplateoutput")
	public MessageChannel channel();
}