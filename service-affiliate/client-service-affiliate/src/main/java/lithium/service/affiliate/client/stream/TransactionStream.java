package lithium.service.affiliate.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.accounting.objects.TransactionStreamData;


@Service
public class TransactionStream {
	
	@Autowired
	private TransactionOutputQueue channel;

	public void register(TransactionStreamData entry) {
		channel.outputQueue().send(MessageBuilder.<Object>withPayload(entry).build());
	}
}