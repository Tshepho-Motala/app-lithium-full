package lithium.service.affiliate.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransactionOutputQueue {

	@Output("affiliatetransactionoutput")
	public MessageChannel outputQueue();
	
}
