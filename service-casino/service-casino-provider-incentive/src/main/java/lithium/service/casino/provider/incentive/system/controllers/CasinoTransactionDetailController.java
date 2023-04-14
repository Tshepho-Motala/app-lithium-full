package lithium.service.casino.provider.incentive.system.controllers;

import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class CasinoTransactionDetailController implements CasinoTransactionDetailClient {

	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrls")
	@ResponseBody
	public List<TransactionDetailPayload> findTransactionDetailUrls(@RequestBody List<TransactionDetailPayload> transactionDetailRequestList
	) throws
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException {
		//FIXME: Lookup the tran url from the remote provider based on the info provided
		transactionDetailRequestList.forEach(payLoad -> {
			payLoad.setTransactionDetailUrl("http://localhost:9000/info?"+payLoad.getProviderTransactionGuid());
		});
		//return transactionDetailRequestList;
		return null;
	}

	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrl")
	@ResponseBody
	public TransactionDetailPayload findTransactionDetailUrl(@RequestBody TransactionDetailPayload transactionDetailRequest
	) throws
			Status500UnhandledCasinoClientException,
			Status511UpstreamServiceUnavailableException {
		transactionDetailRequest.setTransactionDetailUrl("http://localhost:9000/info?"+transactionDetailRequest.getProviderTransactionGuid());
		//return transactionDetailRequest;
		return null;
	}
}
