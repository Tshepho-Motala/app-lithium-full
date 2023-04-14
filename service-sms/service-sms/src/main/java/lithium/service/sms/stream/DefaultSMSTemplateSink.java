package lithium.service.sms.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DefaultSMSTemplateSink {
	String INPUT = "defaultsmstemplateinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}