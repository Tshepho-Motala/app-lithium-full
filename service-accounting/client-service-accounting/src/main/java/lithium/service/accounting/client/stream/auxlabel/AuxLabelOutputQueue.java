package lithium.service.accounting.client.stream.auxlabel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AuxLabelOutputQueue {

	@Output("aux-label-accounting-output")
	public MessageChannel auxLabelOutputQueue();

}
