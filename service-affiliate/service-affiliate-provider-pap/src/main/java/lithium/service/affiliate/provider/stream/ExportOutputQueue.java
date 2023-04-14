package lithium.service.affiliate.provider.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ExportOutputQueue {

	@Output("papexportoutput")
	public MessageChannel outputQueue();
	
}
