package lithium.service.accounting.domain.summary.v2.stream;

import lithium.service.accounting.objects.CompleteTransactionV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdjustmentStreamV2 {
	@Autowired
	private AdjustmentOutputQueueV2 adjustmentOutputQueueV2;

	public void register(List<CompleteTransactionV2> transactions) {
		adjustmentOutputQueueV2.outputQueue().send(MessageBuilder.<List<CompleteTransactionV2>>withPayload(transactions).build());
	}
}