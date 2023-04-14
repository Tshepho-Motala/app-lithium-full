package lithium.service.accounting.domain.summary.v2.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AsyncLabelValueOutputQueueV2 {
	@Output("asynclabelvalueoutputv2")
	public MessageChannel outputQueue();
}