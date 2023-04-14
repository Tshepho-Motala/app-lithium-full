package lithium.service.mail.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserFullNameSynchronizeQueueSink {
	String INPUT = "user-fullname-synchronize-input";

	@Input(INPUT)
	SubscribableChannel inputChannel();
}
