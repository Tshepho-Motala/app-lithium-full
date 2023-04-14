package lithium.service.accounting.domain.summary.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AsyncLabelValueOutputQueue {
	@Output("asynclabelvalueoutput")
	public MessageChannel outputQueue();
}