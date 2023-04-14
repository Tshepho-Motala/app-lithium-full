package lithium.service.accounting.client.transactiontyperegister;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransactionTypeRegisterOutputQueue {

	@Output("transactiontyperegisteroutput")
	public MessageChannel outputQueue();
	
}
