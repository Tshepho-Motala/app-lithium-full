package lithium.service.translate.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TranslationsStreamOutputQueue {

	@Output("translationsregisteroutput")
	public MessageChannel channel();
	
}
