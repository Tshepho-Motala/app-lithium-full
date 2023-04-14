package lithium.service.user.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserEventsStreamOutputQueue {

	@Output("usereventsoutput")
	public MessageChannel userEventsChannel();

	@Output("registration-success-output")
	public MessageChannel userRegistrationSuccessChannel();
}