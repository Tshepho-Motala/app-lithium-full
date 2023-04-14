package lithium.service.affiliate.provider.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.stream.objects.PapTransactionStreamData;

@Service
public class ExportStream {
	
	@Autowired
	private ExportOutputQueue channel;

	public void register(PapTransactionStreamData entry) {
		channel.outputQueue().send(MessageBuilder.<Object>withPayload(entry).build());
	}
}