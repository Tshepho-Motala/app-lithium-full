package lithium.service.casino.controllers;

import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.service.CasinoTransactionDetailService;
import lithium.service.client.LithiumServiceClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
public class CasinoTransactionDetailController implements CasinoTransactionDetailClient {
	@Autowired private CasinoTransactionDetailService casinoTransactionDetailService;
	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrls")
	@ResponseBody
	public List<TransactionDetailPayload> findTransactionDetailUrls(@RequestBody List<TransactionDetailPayload> transactionDetailRequestList
	) throws
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException {
		return casinoTransactionDetailService
				.handleTransactionDetailLookup(transactionDetailRequestList);
	}

	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrl")
	@ResponseBody
	public TransactionDetailPayload findTransactionDetailUrl(@RequestBody TransactionDetailPayload transactionDetailRequest
	) throws
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException {
		return casinoTransactionDetailService
				.handleTransactionDetailLookup(transactionDetailRequest);
	}
}
