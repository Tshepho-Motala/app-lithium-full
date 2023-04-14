package lithium.service.product.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ProductPurchaseQueueSink {
	String INPUT = "productpurchaseinput";
	
	@Input(INPUT)
	SubscribableChannel inputChannel();
}