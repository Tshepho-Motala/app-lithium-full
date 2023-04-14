package lithium.service.accounting.domain.summary.stream;

import lithium.service.accounting.objects.CompleteTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AsyncLabelValueStream {
	@Autowired private AsyncLabelValueOutputQueue asyncLabelValueOutputQueue;

	public void register(CompleteTransaction transaction) {
		asyncLabelValueOutputQueue.outputQueue().send(MessageBuilder.<CompleteTransaction>withPayload(transaction).build());
	}
}