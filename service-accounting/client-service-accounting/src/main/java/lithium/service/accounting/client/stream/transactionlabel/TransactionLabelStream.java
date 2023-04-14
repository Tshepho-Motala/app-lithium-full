package lithium.service.accounting.client.stream.transactionlabel;

import lithium.service.accounting.objects.TransactionLabelContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TransactionLabelStream {
	
	@Autowired
	private TransactionLabelOutputQueue channel;
	
	public void register(TransactionLabelContainer entry) {
		channel.transactionLabelOutputQueue()
				.send(MessageBuilder.<TransactionLabelContainer>withPayload(entry)
						.build());
	}
}