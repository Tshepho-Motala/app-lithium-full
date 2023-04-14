package lithium.service.cashier.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProcessorsStreamOutputQueue {
	@Output("processorsregisteroutput")
	public MessageChannel channel();
}