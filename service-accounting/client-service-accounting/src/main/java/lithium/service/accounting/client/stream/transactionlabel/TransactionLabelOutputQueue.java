package lithium.service.accounting.client.stream.transactionlabel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransactionLabelOutputQueue {

	@Output("transaction-label-accounting-output")
	public MessageChannel transactionLabelOutputQueue();

}
