package lithium.service.product.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.product.client.objects.ProductPurchase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductPurchaseStream {
	@Autowired ProductPurchaseStreamOutputQueue channel;
	
	public void process(ProductPurchase productPurchase) {
		try {
			channel.channel().send(MessageBuilder.<ProductPurchase>withPayload(productPurchase).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}