package lithium.service.accounting.provider.internal.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TransactionTypeQueueSink {
	String INPUT = "transactiontyperegisterinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}
