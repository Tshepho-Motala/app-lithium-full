package lithium.service.accounting.controllers;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClientWithExceptions;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.service.AccountingService;
import lithium.service.domain.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class AccountingControllerWithExceptions implements AccountingClientWithExceptions {
	@Autowired
	AccountingService accountingService;

	@RequestMapping("/balance/v2/getBalanceMapByAccountType")
	@TimeThisMethod
	@Override
	public Response<Map<String, Long>> getBalanceMapByAccountType(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid
	) throws
			Status510AccountingProviderUnavailableException,
			Status410AccountingAccountTypeNotFoundException,
			Status411AccountingUserNotFoundException,
			Status412AccountingDomainNotFoundException,
			Status413AccountingCurrencyNotFoundException {

		log.debug("getByAccountType(" + domainName + ", " + accountType + ", " + currencyCode + ", " + ownerGuid + ")");
		String providerUrl = providerUrl(domainName);
		SW.start("AccountingService - " + providerUrl);
		Response<Map<String, Long>> balanceResponse =
				accountingService.accountingClientWithExceptions(providerUrl)
				.getBalanceMapByAccountType(domainName, accountType, currencyCode, ownerGuid);
		log.debug("Return :" + balanceResponse + " for (" + domainName + ", " + accountType + ", " + currencyCode + ", " + ownerGuid + ")");
		SW.stop();
		return balanceResponse;
	}

	/**
	 * Perform multiple adjustment transactions in a single atomic transaction.
	 * They will either all succeed or if one fails, they all will be rolled back.
	 *
	 * @param request
	 * @return List of adjustment transaction responses
	 */
	@Override
	@RequestMapping("/system/adjust/v1")
	@TimeThisMethod
	public AdjustmentResponse adjust(@RequestBody AdjustmentRequest request)
			throws Status510AccountingProviderUnavailableException, Status500InternalServerErrorException,
			Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException {
		return accountingService.accountingClientWithExceptions(providerUrl(request.getDomainName()))
			.adjust(request);
	}

	private String providerUrl(String domainName) throws Status510AccountingProviderUnavailableException {
		try {
				SW.start("Provider - "+domainName);
				Provider provider = accountingService.provider(domainName);
				log.debug("Provider :"+provider);
				SW.stop();
				return provider.getUrl();
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}
}
