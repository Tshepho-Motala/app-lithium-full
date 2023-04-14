package lithium.service.cashier.client.service;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cashier.client.CashierInternalAccessClient;
import lithium.service.cashier.client.CashierInternalClient;
import lithium.service.cashier.client.CashierProcessorAccountInternalClient;
import lithium.service.cashier.client.CashierProcessorAccountVerifyInternalClient;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.objects.ProccesorAccountTransaction;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class CashierInternalClientService {

	@Autowired @Setter
	LithiumServiceClientFactory services;

	public Long saveProcessingAttempt(
			Long transactionId, 
			boolean success, 
			String messages,
			String rawResponse,
			String reference) throws Exception {
		CashierInternalClient client = services.target(CashierInternalClient.class);
		return client.saveProcessingAttempt(transactionId, success, messages, rawResponse, reference);
	}
	
	public HashMap<String, String> domainMethodProcessorProperties(Long domainMethodProcessorId) throws Exception {		
		HashMap<String, String> map = new HashMap<>();
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<List<ProcessedProcessorProperty>> properties = client.processorProperties(domainMethodProcessorId);
		for (ProcessedProcessorProperty prop: properties.getData()) {
			map.put(prop.getName(), prop.getValue());
		}
		return map;
	}

	public DomainMethodProcessor processorByMethodCodeAndProcessorDescription(
			String domainName, boolean deposit, String methodCode, String processorDescription) throws Exception {
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<DomainMethodProcessor> response =
				client.processorByMethodCodeAndProcessorDescription(domainName, deposit, methodCode, processorDescription);
		if (!response.isSuccessful()) {
			throw new Exception("No processor configuration found. Is this processor configured? (" +
				"domainName " + domainName + " method " + (deposit? "deposit": "withdrawal") +
				" methodCode " + methodCode + " processorDescription " + processorDescription);
		}
		return response.getData();
	}

	public Map<String, String> propertiesOfFirstEnabledProcessorByMethodCode(String domainName, boolean deposit, String methodCode) throws Exception{
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<Map<String, String>> response =
				client.propertiesOfFirstEnabledProcessorByMethodCode(domainName, deposit, methodCode);
		if (!response.isSuccessful()) {
			throw new Exception("No processor configuration found. Is this processor configured? (" +
					"domainName " + domainName + " method " + (deposit? "deposit": "withdrawal") +
					" methodCode " + methodCode + " enabled = true ");
		}
		return response.getData();
	}

	public DomainMethodProcessorProperty propertyOfFirstEnabledProcessor(String propertyName,
			String methodCode, boolean deposit, String userGuid, String domainName,
			String ipAddress, String userAgent) throws Exception {

		List<DomainMethodProcessorProperty> properties =
				propertiesOfFirstEnabledProcessor(methodCode, deposit, userGuid, domainName, ipAddress, userAgent);

		return properties.stream()
				.filter(p -> p.getProcessorProperty().getName().equalsIgnoreCase(propertyName))
				.findAny()
				.orElseThrow(() -> new Exception(propertyName + " property is not configured"));
	}

	public Map<String, String> propertiesMapOfFirstEnabledProcessor(String methodCode, boolean deposit, String userGuid,
			String domainName, String ipAddress, String userAgent) throws Exception {
		return propertiesOfFirstEnabledProcessor(methodCode, deposit, userGuid, domainName, ipAddress, userAgent)
				.stream()
				.collect(Collectors.toMap(p -> p.getProcessorProperty().getName(), p -> p.getValue()));
	}

	public List<DomainMethodProcessorProperty> propertiesOfFirstEnabledProcessor(
			String methodCode, boolean deposit, String userGuid,
			String domainName, String ipAddress, String userAgent) throws Exception {
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<List<DomainMethodProcessorProperty>> response =
				client.propertiesOfFirstEnabledProcessor(methodCode, deposit, userGuid, domainName, ipAddress, userAgent);
		if (!response.isSuccessful()) {
			if (response.getStatus().equals(Response.Status.BAD_REQUEST)) {
				throw new Status400BadRequestException("Method with code " + methodCode + " is not configured/disabled for domain: " + domainName);
			} else {
				throw new Exception("No processor properties found.");
			}
		}
		return response.getData();
	}

	public long registerDeposit(
		Long domainMethodProcessorId,
		String userGuid,
		String currencyCode,
		Long amountInCents,
		String reference,
		String additionalReference,
		Long sessionId,
		boolean success,
        String paymentType
	) throws Exception {
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<Long> response =
				client.registerDeposit(domainMethodProcessorId, userGuid, currencyCode, amountInCents, reference, additionalReference, sessionId, success, paymentType==null ? "" : paymentType);
		if (!response.isSuccessful()) {
			throw new Exception("Deposit could not be registered " + response.getStatus().id() + " " + response.getMessage());
		}
		return response.getData();
	}

	public DepositStatus getDepositStatus(String processorReference,
										  String processorCode) throws Exception {
		CashierInternalClient client = services.target(CashierInternalClient.class);
		Response<DepositStatus> response =
				client.depositStatus(processorReference,processorCode);
		if (!response.isSuccessful()) {
			throw new Exception("Can't fetch deposit status " + response.getStatus().id() + " " + response.getMessage());
		}
		return response.getData();
	}

	public Long saveUserCard(Long transactionId, UserCard userCard) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);
		Response<Long> response = client.saveUserCard(transactionId, userCard);
		if (!response.isSuccessful()) {
			throw new Exception("Failed to save user card. TransactionId: " + transactionId + " Response: "+ response.getStatus().id() + " " + response.getMessage());
		}
		return response.getData();
	}

	public void addCardRemark(Long transactionId, String cardReference, UserCard userCard, TransactionRemarkType remarkType) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);
		client.addCardRemark(transactionId, cardReference, remarkType, userCard);
	}

	public void saveUserCard(String userGuid, Long  domainMethodProcessorId, UserCard userCard) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);
		Response<String> response = client.saveUserCardByDomainProcessorId(userGuid, domainMethodProcessorId, userCard);
		if (!response.isSuccessful()) {
			throw new Exception("Failed to save user card. UserGuid: " + userGuid + " domainMethodProcessorId: " + domainMethodProcessorId + " Response: " + response.getStatus().id() + " " + response.getMessage());
		}
	}

	public List<UserCard> getUserCards(
			String methodCode, boolean deposit, String userName, String domainName, String userGuid,
			String ipAddress, String userAgent) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<List<UserCard>> response = client.getUserCards(methodCode, deposit, userName, domainName, userGuid, ipAddress, userAgent);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get user cards. (" +
					"domainName: " + domainName + "; method: " + (deposit? "deposit": "withdrawal") +
					"; methodCode: " + methodCode + "; userName: " + userName + "; userGuid: " + userGuid + "; ipAddress: " + ipAddress + "; userAgent:" + userAgent);
		}
		return response.getData();
	}

	public UserCard getUserCard(String cardReference, String userGuid) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<UserCard> response = client.getUserCard(cardReference, userGuid);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get user card for card reference: " + cardReference);
		}
		return response.getData();
	}

	public Boolean checkCardOwner(String userGuid, String fingerprint, boolean isDeposit) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<Boolean> response = client.checkCardOwner(userGuid, fingerprint, isDeposit);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to check card owner for user: " + userGuid + ". Fingerprint: " + fingerprint);
		}
		return response.getData();
	}

	public void setDefaultCard(String userGuid, String reference) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<String> response = client.setDefaultUserCard(userGuid, reference);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to set user " + userGuid + " card with reference " + reference + " as default. Responce: "+ response.getStatus().id() + " " + response.getMessage());
		}
	}

	public String getCurrency(String domainName) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<String> response = client.getCurrency(domainName);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get user currency domainName: " + domainName);
		}
		return response.getData();
	}

	public AccountProcessorRequest getAccountProcessorRequest(Long transactionId) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<AccountProcessorRequest> response = client.getAccountProcessorRequest(transactionId);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get data for processor account transaction id: " + transactionId);
		}
		return response.getData();
	}

	public ProccesorAccountTransaction getAccountProcessorTransaction(@RequestParam("patx_id") Long transactionId) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<ProccesorAccountTransaction> response = client.getAccountProcessorTransaction(transactionId);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get  processor account transaction by id: " + transactionId);
		}
		return response.getData();
	}


	public Long saveProcessorAccount(ProcessorAccountResponse accountProcessorResponse) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<Long> response = client.saveProcessorAccount(accountProcessorResponse);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to save processor account. Request:" + accountProcessorResponse);
		}
		return response.getData();
	}

    public boolean isAccessAllowed(
            String domainName,
            String methodCode,
            String ipAddress,
            String userAgent,
            String userGuid,
            Boolean deposit) throws Exception {
        CashierInternalAccessClient client = services.target(CashierInternalAccessClient.class);
        Response<Boolean> response = client.isAuthorised(domainName, methodCode, ipAddress, userAgent, userGuid, deposit);
        return response.getData().booleanValue();
    }

	public List<ProcessorAccount> getProcessorAccountsPerUser(String domainName, String type, String userGuid) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<List<ProcessorAccount>> response = client.getProcessorAccountsPerUser(domainName, userGuid, type);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get processor accounts (" + userGuid + "): " + response.getMessage());
		}
		return response.getData();
	}

	public List<ProcessorAccount> getProcessorAccounts(String domainName, String reference, String type) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<List<ProcessorAccount>> response = client.getProcessorAccounts(domainName, reference, type);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get processor account (" + reference + "): " + response.getMessage());
		}
		return response.getData();
	}

	public void updateProcessorAccount(ProcessorAccount processorAccount) throws Exception {
		if (isNull(processorAccount) || isNull(processorAccount.getId())) {
			throw new Exception("Processor account transaction should be exists: " + processorAccount);
		}

		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response response = client.updateProcessorAccount(processorAccount);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to update processor account. (" + processorAccount.getId() + ") Request:" + response.getMessage());
		}
	}

	public VerifyProcessorAccountResponse verifyAccount(VerifyProcessorAccountRequest request) throws Exception {
		CashierProcessorAccountVerifyInternalClient client = services.target(CashierProcessorAccountVerifyInternalClient.class, "service-cashier", true);

		Response<VerifyProcessorAccountResponse> response = client.verifyAccount(request);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to verify processor account. Verification request: " + request + ", Response:" + response.getMessage());
		}
		return response.getData();
	}

	public ProcessorAccount getUserContraAccount(String userGuid) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<ProcessorAccount> response = client.getContraAccount(userGuid);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get user" + userGuid + " contra account. Response:" + response.getMessage());
		}
		return response.getData();
	}

	public void updateExpiredUserCard(ProcessorAccount processorAccount) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response response = client.updateExpiredUserCard(processorAccount);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to update expired processor account (" + processorAccount.getId() + "). Response:" + response.getMessage());
		}
	}

	public List<ProcessorAccount> getProcessorAccountsByReference(String reference) throws Exception
	{
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);

		Response<List<ProcessorAccount>> response = client.getProcessorAccountsByReference(reference);

		if (!response.isSuccessful()) {
			throw new Exception("Failed to get processor account (" + reference + "): " + response.getMessage());
		}
		return response.getData();
	}
}
