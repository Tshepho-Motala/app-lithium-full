package lithium.service.accounting.client.transactiontyperegister;

import lithium.service.accounting.objects.TransactionTypeRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.AssertTrue;

@Service
public class TransactionTypeRegisterStream {
	
	@Autowired
	private TransactionTypeRegisterOutputQueue channel;

	public void register(TransactionTypeRegistration entry) {
		Assert.isTrue(channel.outputQueue().send(MessageBuilder.<Object>withPayload(entry).build()));
	}
}