package lithium.service.gateway.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GatewayExchangeStreamOutputQueue {
	@Output("gatewayexchange")
	public MessageChannel channel();
}