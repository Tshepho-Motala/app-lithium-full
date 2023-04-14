package lithium.service.accounting.client;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.AdjustmentTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@FeignClient("service-accounting")
public interface AccountingClientWithExceptions {

	/**
	 * Provides a map containing the various balances with the account codes as keys to the balance map.
	 * <br/>The most used account type is "PLAYER_BALANCE"
	 * and the currency to use would generally be the default domain currency.
	 *
	 * @param domainName Domain name
	 * @param accountType Account type
	 * @param currencyCode Currency code
	 * @param ownerGuid Player globally unique identifier
	 * @return {@link Map}(AccountCode, Balance in cents)
	 * @throws Status510AccountingProviderUnavailableException
	 * @throws Status412AccountingDomainNotFoundException
	 * @throws Status410AccountingAccountTypeNotFoundException
	 * @throws Status411AccountingUserNotFoundException
	 * @throws Status413AccountingCurrencyNotFoundException
	 */
	@RequestMapping("/balance/v2/getBalanceMapByAccountType")
	public Response<Map<String, Long>> getBalanceMapByAccountType(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid
	) throws Status510AccountingProviderUnavailableException,
			Status410AccountingAccountTypeNotFoundException,
			Status411AccountingUserNotFoundException,
			Status412AccountingDomainNotFoundException,
			Status413AccountingCurrencyNotFoundException;

	/**
	 * Perform multiple adjustment transactions in a single atomic transaction.
	 * They will either all succeed or if one fails, they all will be rolled back.
	 * @param request
	 * @return A list of responses wrapped in an AdjustmentResponse
	 */
	@RequestMapping("/system/adjust/v1")
	public AdjustmentResponse adjust(@RequestBody AdjustmentRequest request
	) throws
			Status414AccountingTransactionDataValidationException,
			Status415NegativeBalanceException,
			Status500InternalServerErrorException,
			Status510AccountingProviderUnavailableException;
}
