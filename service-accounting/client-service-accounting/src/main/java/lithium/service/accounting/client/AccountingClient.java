package lithium.service.accounting.client;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.accounting.objects.TransactionEntry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FeignClient("service-accounting")
public interface AccountingClient {
	
	@RequestMapping("/transaction/{transactionId}")
	ResponseEntity<List<TransactionEntry>> transactions(
		@RequestParam("transactionId") Long transactionId
	);
	
	@RequestMapping("/balance/get")
	public Response<Long> get(
			@RequestParam("currencyCode") String currencyCode, 
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid) throws Exception;

	@RequestMapping("/balance/get/{domainName}/{accountCode}/{accountType}/{currencyCode}/{ownerDomain}/{owner}")
	public Response<Long> getPath(
			@PathVariable("domainName") String domainName,
			@PathVariable("accountCode") String accountCode,
			@PathVariable("accountType") String accountType,
			@PathVariable("currencyCode") String currencyCode,
			@PathVariable("ownerDomain") String ownerGuid,
			@PathVariable("owner") String owner) throws Exception;

	@RequestMapping("/balance/getAllByOwnerGuid")
	public Response<List<PlayerBalanceResponse>> getAllByOwnerGuid(
			@RequestParam("domainName") String domainName,
			@RequestParam("ownerGuid") String ownerGuid) throws Exception;


	@RequestMapping("/balance/getByOwnerGuid")
	public Response<Long> getByOwnerGuid(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid) throws Exception;
	
	@RequestMapping("/balance/getByAccountType")
	public Response<Map<String, Long>> getByAccountType(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid) throws Exception;

	@RequestMapping("balance/getByAccountTypeWithExceptions")
	public Response<Map<String, Long>> getByAccountTypeWithExceptions(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid
	) throws Status510AccountingProviderUnavailableException,
			Status412AccountingDomainNotFoundException,
			Status410AccountingAccountTypeNotFoundException,
			Status411AccountingUserNotFoundException,
			Status413AccountingCurrencyNotFoundException;

	@RequestMapping("/balance/adjust")
	public Response<AdjustmentTransaction> adjust(
			@RequestParam("amountCents") Long amountCents,
			@RequestParam("date") String date,
			@RequestParam("transactionTypeCode") String transactionTypeCode,
			@RequestParam("contraAccountCode") String contraAccountCode,
			@RequestParam("contraAccountTypeCode") String contraAccountTypeCode,
			@RequestParam(name="labels", required=false) String[] labels,
			@RequestParam("currencyCode") String currencyCode, 
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("authorGuid") String authorGuid,
			@RequestParam(name="allowNegativeAdjust", required=false, defaultValue="true") Boolean allowNegativeAdjust
	) throws Exception;
	
	@RequestMapping("/balance/adjustMulti")
	public Response<AdjustmentTransaction> adjustMulti(
			@RequestParam("amountCents") Long amountCents,
			@RequestParam("date") String date,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("accountTypeCode") String accountTypeCode,
			@RequestParam("transactionTypeCode") String transactionTypeCode,
			@RequestParam("contraAccountCode") String contraAccountCode,
			@RequestParam("contraAccountTypeCode") String contraAccountTypeCode,
			@RequestParam(name="labels", required=false) String[] labels,
			@RequestParam("currencyCode") String currencyCode, 
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("authorGuid") String authorGuid,
			@RequestParam(name="allowNegativeAdjust", required=false, defaultValue="true") Boolean allowNegativeAdjust,
			@RequestParam(name="negAdjProbeAccCodes", required=false) String[] negAdjProbeAccCodes) throws Status415NegativeBalanceException, Exception;

    @RequestMapping(path= "/balance/v2/adjustMulti",  method= RequestMethod.POST)
    public Response<AdjustmentTransaction> adjustMultiV2(
            @RequestBody AdjustMultiRequest request) throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException;
	/**
	 * Perform multiple adjustment transactions in a single transaction statement. 
	 * They will either all succeed or if one fails, they all will be rolled back.
	 * @param adjustmentRequestList
	 * @return List of adjustment transaction responses
	 * @throws Status414AccountingTransactionDataValidationException
	 * @throws Status415NegativeBalanceException
	 * @throws Status500InternalServerErrorException
	 */
	@RequestMapping("/balance/adjustMultiBatch")
	public Response<ArrayList<AdjustmentTransaction>> adjustMultiBatch(@RequestBody ArrayList<AdjustmentRequestComponent> adjustmentRequestList) throws Exception;
	
	@RequestMapping("/balance/rollback")
	public Response<AdjustmentTransaction> rollback(
			@RequestParam("date") String date,
			@RequestParam("reversalTransactionTypeCode") String reversalTransactionTypeCode,
			@RequestParam("reversalLabelName") String reversalLabelName,
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("authorGuid") String authorGuid,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("originalAccountCode") String originalAccountCode,
			@RequestParam("originalAccountTypeCode") String originalAccountTypeCode) throws Exception;
	
	@RequestMapping("/transaction/findByLabelConstraint")
	public Response<CompleteTransaction> findByLabelConstraint(@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("originalAccountCode") String originalAccountCode,
			@RequestParam("originalAccountTypeCode") String originalAccountTypeCode) throws Exception;
	
	@RequestMapping("/transaction/findByLabelConstraintList")
	public Response<List<CompleteTransaction>> findByLabelConstraintList(
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("originalAccountCodeList") List<String> originalAccountCodeList,
			@RequestParam("originalAccountTypeCodeList") List<String> originalAccountTypeCodeList
			) throws Exception;
}
