package lithium.service.accounting.domain.summary.stream;

import lithium.service.accounting.objects.CompleteTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdjustmentStream {
	@Autowired
	private AdjustmentOutputQueue adjustmentOutputQueue;

	public void register(List<CompleteTransaction> transactions) {
		adjustmentOutputQueue.outputQueue().send(MessageBuilder.<List<CompleteTransaction>>withPayload(transactions).build());
	}
}