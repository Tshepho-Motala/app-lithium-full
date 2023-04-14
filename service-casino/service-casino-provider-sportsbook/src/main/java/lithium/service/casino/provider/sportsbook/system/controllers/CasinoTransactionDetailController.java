package lithium.service.casino.provider.sportsbook.system.controllers;

import lithium.metrics.SW;
import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.data.Bet;
import lithium.service.casino.provider.sportsbook.request.BetInfoRequest;
import lithium.service.casino.provider.sportsbook.response.BetInfoResponse;
import lithium.service.casino.provider.sportsbook.services.CasinoTransactionDetailService;
import lithium.util.HmacSha256HashCalculator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CasinoTransactionDetailController implements CasinoTransactionDetailClient {
	@Autowired private CasinoTransactionDetailService casinoTransactionDetailService;
	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrls")
	@ResponseBody
	public List<TransactionDetailPayload> findTransactionDetailUrls(@RequestBody List<TransactionDetailPayload> transactionDetailRequestList
	) throws
			Status422InvalidParameterProvidedException,
			Status512ProviderNotConfiguredException {
		return casinoTransactionDetailService.handleTransactionDetailRequest(transactionDetailRequestList);
	}

	@Override
	@RequestMapping("/system/casino/transaction-detail-url/findTransactionDetailUrl")
	@ResponseBody
	public TransactionDetailPayload findTransactionDetailUrl(@RequestBody TransactionDetailPayload transactionDetailRequest
	) throws
			Status422InvalidParameterProvidedException,
			Status512ProviderNotConfiguredException {
		return casinoTransactionDetailService.handleTransactionDetailRequest(transactionDetailRequest);
	}
}
