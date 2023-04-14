package lithium.service.cashier.services;

import lithium.service.Response;
import lithium.service.cashier.client.internal.BankAccountLookupClient;
import lithium.service.cashier.client.internal.BanksLookupClient;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.objects.Bank;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class BankAccountLookupService {

	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private CashierFrontendService cashierFrontendService;

	public Response<BankAccountLookupResponse> bankAccountLookup(String processorUrl, long transactionId, String domainName,
			String processorCode, String userGuid, String ipAddr, String userAgent) throws Exception {

		try {
			lithium.service.cashier.data.entities.DomainMethodProcessor domainMethodProcessor = cashierFrontendService.firstEnabledProcessor(domainName, processorCode, false, userGuid, ipAddr, userAgent);
			BankAccountLookupRequest bankAccountLookupRequest = buildBankAccountLookupRequest(transactionId, processorCode, domainMethodProcessor);
			return bankAccountLookup(processorUrl, bankAccountLookupRequest);
		} catch (NoMethodWithCodeException | MoreThanOneMethodWithCodeException e) {
			BankAccountLookupResponse bankAccountLookupResponse = new BankAccountLookupResponse();
			bankAccountLookupResponse.setStatus("Failed");
			bankAccountLookupResponse.setFailedStatusReasonMessage("Payment method or processor disabled");
			return Response.<BankAccountLookupResponse>builder().data(bankAccountLookupResponse).build();
		}
	}

	public Response<BankAccountLookupResponse> bankAccountLookup(String processorUrl, BankAccountLookupRequest bankAccountLookupRequest) {
		BankAccountLookupClient bankAccountLookupClient = getBankAccountLookupClient(processorUrl);
		try {
			return bankAccountLookupClient.bankAccountLookup(bankAccountLookupRequest);
		} catch (Exception ex) {
			log.error("Can't get bank account details: ", ex);
			return Response.<BankAccountLookupResponse>builder().data(buildFailedBankAccountLookupResponse()).build();
		}
	}

	public Response<List<DomainMethodProcessor>> getProcessorsByDomainNameAndDeposit(String domainName, boolean deposit) {
		return Response.<List<DomainMethodProcessor>>builder()
				.data(domainMethodProcessorService.getProcessorsByDomainNameAndDeposit(domainName, deposit))
				.status(Response.Status.OK)
				.build();
	}

	private BankAccountLookupResponse buildFailedBankAccountLookupResponse() {
		return BankAccountLookupResponse.builder()
				.status("Failed")
				.failedStatusReasonMessage("Can't get bank account details. Try later, please...").build();
	}

	public Response<List<Bank>> banksLookup(String processorUrl, Map<String, String> processorProperties) throws Exception {
		BanksLookupClient banksLookupClient = getBanksLookupClient(processorUrl);
		return banksLookupClient.banks(processorProperties);
	}

	private BankAccountLookupRequest buildBankAccountLookupRequest(long transactionId, String processorCode, lithium.service.cashier.data.entities.DomainMethodProcessor dmp) throws Exception {

		Map<String, String> inputData = getIncomingBankDetails(transactionId, processorCode);

		Map<String, String> dmpProps = domainMethodProcessorService.propertiesWithDefaults(dmp.getId())
				.stream()
				.filter(p -> nonNull(p.getValue()))
				.collect(Collectors.toMap(p -> p.getProcessorProperty().getName(), DomainMethodProcessorProperty::getValue));

		BankAccountLookupRequest bankAccountLookupRequest = new BankAccountLookupRequest();
		bankAccountLookupRequest.setAccountNumber(inputData.get("account_number"));
		bankAccountLookupRequest.setBankName(inputData.get("bank_name"));
		bankAccountLookupRequest.setBankCode(inputData.get("bank_code"));
		bankAccountLookupRequest.setDomainMethodProcessorProperties(dmpProps);
		return bankAccountLookupRequest;
	}

	private Map<String, String> getIncomingBankDetails(long transactionId, String processorCode) throws Exception {
		try {
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoProcessorRequest request = machine.processorCallbackGetTransaction(transactionId, processorCode);
			ProcessorAccount processorAccount = request.getProcessorAccount();
			if (processorAccount != null && processorAccount.getData() != null) {
				return processorAccount.getData();
			} else {
				return request.stageInputData(1);
			}
		} catch (Exception e) {
			log.error("/do-callback-get-transaction-input-data :: " + e.getMessage(), e);
			throw new Exception();
		}
	}

	private BanksLookupClient getBanksLookupClient(String processorUrl) {
		BanksLookupClient cl = null;
		try {
			cl = services.target(BanksLookupClient.class, processorUrl, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting processor's banks lookup service", e);
		}
		return cl;
	}

	private BankAccountLookupClient getBankAccountLookupClient(String processorUrl) {
		BankAccountLookupClient cl = null;
		try {
			cl = services.target(BankAccountLookupClient.class, processorUrl, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting processor's bank account lookup service", e);
		}
		return cl;
	}
}
