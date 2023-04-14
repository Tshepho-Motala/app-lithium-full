package lithium.service.accounting.domain.summary.v2.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AdjustmentOutputQueueV2 {
	@Output("adjustmentoutputv2")
	public MessageChannel outputQueue();
}