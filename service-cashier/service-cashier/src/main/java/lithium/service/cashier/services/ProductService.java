package lithium.service.cashier.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.product.client.ProductClient;
import lithium.service.product.client.objects.ProductPurchase;
import lithium.service.product.client.objects.CashierTransaction;
import lithium.service.product.client.stream.ProductPurchaseStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {
	@Autowired ProductPurchaseStream productPurchaseStream;
	@Autowired LithiumServiceClientFactory clientFactory;
	
	@Async
	public void triggerProductPurchase(
		DoMachineContext context
	) {
		DoProcessorResponse pr = context.getProcessorResponse();
		lithium.service.cashier.data.entities.Transaction t = context.getTransaction();
		ProductPurchase productPurchase = ProductPurchase.builder()
		.orderId(pr.stageOutputData(1, "orderId"))
		//.productId(service.getData(context.getTransaction(), "productId", 1, false))
		.productId(context.getRequest().productGuid())
		.purchaseState(pr.stageOutputData(1, "purchaseState"))
		.purchaseTime(pr.stageOutputData(1, "purchaseTime"))
		.purchaseToken(pr.stageOutputData(1, "purchaseToken"))
		.consumptionState(pr.stageOutputData(1, "consumptionState"))
		.developerPayload(pr.stageOutputData(1, "developerPayload"))
		.playerGuid(context.getUser().guid())
		.cashierTransaction(
			CashierTransaction.builder()
			.transactionId(t.getId())
			.createdOn(t.getCreatedOn())
			.amountCents(t.getAmountCents())
			.currencyCode(t.getCurrencyCode())
			.processorCode(context.getProcessor().getProcessor().getCode())
			.methodCode(context.getProcessor().getDomainMethod().getMethod().getCode())
			.domainMethodProcessorId(context.getProcessor().getId())
			.domainMethodName(t.getDomainMethod().getName())
			.domainMethodId(t.getDomainMethod().getId())
			.build()
		)
		.build();
		triggerProductPurchase(productPurchase);
	}
	
	@Async
	public void triggerProductPurchase(ProductPurchase productPurchase) {
		productPurchaseStream.process(productPurchase);
	}
	
	//TODO : user ip to determine country to get local currency.
	public BigDecimal getPrice(String guid, String domainName, String ipAddr) {
		try {
			return getProduct(guid, domainName, ipAddr).getCurrencyAmount();
		} catch (Exception e) {
			log.error("", e);
			return BigDecimal.ZERO;
		}
	}
	
	public lithium.service.product.client.objects.Product getProduct(String guid, String domainName, String ipAddr) {
		Response<lithium.service.product.client.objects.Product> productResponse = null;
		try {
			productResponse = getProductClient().get().findByDomainAndGuid(domainName, guid, ipAddr);
			if (productResponse.isSuccessful() && productResponse.getData() != null) {
				log.info("productResponse : "+productResponse);
				return productResponse.getData();
			}
		} catch (Exception e) {
			log.error("Could not retrieve product details :: getProduct("+guid+", "+domainName+", "+ipAddr+") : "+productResponse, e);
		}
		return null;
	}
	
	private Optional<ProductClient> getProductClient() {
		return getClient(ProductClient.class, "service-product");
	}
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		try {
			clientInstance = clientFactory.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
	}
}
