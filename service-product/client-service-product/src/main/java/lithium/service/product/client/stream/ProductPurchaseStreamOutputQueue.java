package lithium.service.product.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProductPurchaseStreamOutputQueue {
	@Output("productpurchaseoutput")
	public MessageChannel channel();
}