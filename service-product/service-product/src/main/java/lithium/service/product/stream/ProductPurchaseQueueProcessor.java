package lithium.service.product.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.product.client.objects.ProductPurchase;
import lithium.service.product.data.entities.Transaction;
import lithium.service.product.services.TransactionService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(ProductPurchaseQueueSink.class)
@Slf4j
public class ProductPurchaseQueueProcessor {
	@Autowired TransactionService transactionService;
	
	@StreamListener(ProductPurchaseQueueSink.INPUT) 
	public void handle(ProductPurchase productPurchase) throws Exception {
		log.info("Received ProductPurchase " + productPurchase);
		Transaction transaction = transactionService.registerProductPurchase(productPurchase);
		boolean processedAccountingWithdrawal = transactionService.processAccountingWithdrawal(transaction, productPurchase);
		if (processedAccountingWithdrawal) {
			transactionService.payoutProduct(transaction);
		}
	}
}