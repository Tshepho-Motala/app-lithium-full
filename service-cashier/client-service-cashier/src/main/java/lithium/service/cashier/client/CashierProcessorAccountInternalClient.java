package lithium.service.cashier.client;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.UserCard;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-cashier")
public interface CashierProcessorAccountInternalClient {

	@RequestMapping(path="/internal/saveUserCardByDomainProcessorId", method=RequestMethod.POST)
	public Response<String> saveUserCardByDomainProcessorId(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
			@RequestBody UserCard userCard);

	@RequestMapping(path="/internal/addcardremark", method=RequestMethod.POST)
	public Response<String> addCardRemark(
			@RequestParam("transactionId")  Long transactionId,
			@RequestParam(value = "cardReference",required = false)  String cardReference,
			@RequestParam("remarkType") TransactionRemarkType remarkType,
			@RequestBody UserCard userCard);

	@RequestMapping(path="/internal/saveusercard", method=RequestMethod.POST)
	public Response<Long> saveUserCard(
			@RequestParam("transactionId")  Long transactionId,
			@RequestBody UserCard userCard);

	@RequestMapping(path="/internal/processor-account-request", method = RequestMethod.GET)
	public Response<AccountProcessorRequest> getAccountProcessorRequest(
			@RequestParam("patx_id") Long transactionId);

	@RequestMapping(path="/internal/processor-account/transaction", method = RequestMethod.GET)
	public Response<lithium.service.cashier.client.objects.ProccesorAccountTransaction> getAccountProcessorTransaction(
			@RequestParam("patx_id") Long transactionId);

	@RequestMapping(path="/internal/processor-account/save", method = RequestMethod.POST)
	public Response<Long> saveProcessorAccount(@RequestBody ProcessorAccountResponse processorAccountResponse);

	@RequestMapping(path="/internal/usercard/default", method=RequestMethod.POST)
	public Response<String> setDefaultUserCard(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("reference") String reference);

	@RequestMapping(path="/internal/usercards", method=RequestMethod.GET)
	public Response<List<UserCard>> getUserCards(
			@RequestParam("methodCode")  String methodCode, @RequestParam("deposit")  boolean deposit,
			@RequestParam("userName")  String userName, @RequestParam("domainName")  String domainName,
			@RequestParam("userGuid") String userGuid, @RequestParam("ipAddress")  String ipAddress,
			@RequestParam("userAgent")  String userAgent);

	@RequestMapping(path="/internal/usercard", method=RequestMethod.GET)
	public Response<UserCard> getUserCard(@RequestParam("cardReference")  String cardReference,
										  @RequestParam("userGuid") String userGuid);

	@RequestMapping(path="/internal/check-card-owner", method=RequestMethod.GET)
	public Response<Boolean> checkCardOwner(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("fingerprint")  String fingerprint,
			@RequestParam("isDeposit")  boolean isDeposit);

	@RequestMapping(path="/internal/usercurrency", method=RequestMethod.GET)
	public Response<String> getCurrency(
			@RequestParam("domainName") String domainName
	);

	@RequestMapping(path="internal/get-processor-accounts-per-user", method=RequestMethod.GET)
	public Response<List<ProcessorAccount>> getProcessorAccountsPerUser(@RequestParam("domainName") String domainName,
																		@RequestParam("userGuid") String userGuid,	@RequestParam("type") String type);

	@RequestMapping(path="internal/get-processor-accounts", method=RequestMethod.GET)
	public Response<List<ProcessorAccount>> getProcessorAccounts(@RequestParam("domainName") String domainName, @RequestParam("reference") String reference,
															   @RequestParam("type") String type);

	@RequestMapping(path="internal/get-processor-account-by-id", method=RequestMethod.GET)
	public Response<ProcessorAccount> getProcessorAccountById(@RequestParam("id") long processorAccountId);

	@RequestMapping(path="internal/update-processor-account", method=RequestMethod.POST)
	public Response updateProcessorAccount(@RequestBody ProcessorAccount processorAccount);

	@RequestMapping(path="internal/get-contra-account", method=RequestMethod.GET)
	public Response<ProcessorAccount> getContraAccount(@RequestParam("userGuid") String userGuid);

	@RequestMapping(path="/internal/get-processor-accounts-by-reference", method=RequestMethod.GET)
	public Response<List<ProcessorAccount>> getProcessorAccountsByReference(@RequestParam("reference") String reference);

	@RequestMapping(path="/internal/update-expired-user-card", method=RequestMethod.POST)
	public Response updateExpiredUserCard(@RequestBody ProcessorAccount processorAccount);
}
