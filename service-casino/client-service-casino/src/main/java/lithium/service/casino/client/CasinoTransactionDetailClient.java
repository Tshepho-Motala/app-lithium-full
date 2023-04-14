package lithium.service.casino.client;

import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@FeignClient(name="service-casino")
public interface CasinoTransactionDetailClient {
	
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrls")
	@ResponseBody
	public List<TransactionDetailPayload> findTransactionDetailUrls(@RequestBody List<TransactionDetailPayload> transactionDetailRequestList
	) throws
			Status422InvalidParameterProvidedException,
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException,
			Status512ProviderNotConfiguredException;

	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrl")
	@ResponseBody
	public TransactionDetailPayload findTransactionDetailUrl(@RequestBody TransactionDetailPayload transactionDetailRequest
	) throws
			Status422InvalidParameterProvidedException,
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException,
			Status512ProviderNotConfiguredException;

	static <E> E lookupService(Class<E> theClass, String url, final LithiumServiceClientFactory serviceFactory
	) throws Status511UpstreamServiceUnavailableException {
		E clientInstance = null;

		try {
			clientInstance = serviceFactory.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status511UpstreamServiceUnavailableException(e.getMessage());
		}
		return clientInstance;
	}
}
