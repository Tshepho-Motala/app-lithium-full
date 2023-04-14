package lithium.service.accounting.domain.summary.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AdjustmentOutputQueue {
	@Output("adjustmentoutput")
	public MessageChannel outputQueue();
}