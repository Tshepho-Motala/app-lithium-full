package lithium.service.cashier.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.cashier.client.objects.transaction.dto.Processor;

@Service
public class ProcessorsStream {
	@Autowired
	private ProcessorsStreamOutputQueue channel;
	
	public void registerProcessor(Processor processor) {
		channel.channel().send(MessageBuilder.<Processor>withPayload(processor).build());
	}
}
