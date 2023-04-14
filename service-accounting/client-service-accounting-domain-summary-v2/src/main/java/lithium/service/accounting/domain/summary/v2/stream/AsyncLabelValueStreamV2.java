package lithium.service.accounting.domain.summary.v2.stream;

import lithium.service.accounting.objects.CompleteTransactionV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AsyncLabelValueStreamV2 {
	@Autowired private AsyncLabelValueOutputQueueV2 asyncLabelValueOutputQueueV2;

	public void register(CompleteTransactionV2 transaction) {
		asyncLabelValueOutputQueueV2.outputQueue().send(MessageBuilder.<CompleteTransactionV2>withPayload(transaction).build());
	}
}