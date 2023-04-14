package lithium.service.raf.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface RAFConversionStreamOutputQueue {
	@Output("rafconversionoutput")
	public MessageChannel channel();
}