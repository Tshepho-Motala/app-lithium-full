package lithium.service.cashier.services;

import lithium.service.cashier.client.frontend.AddProcessorAccountRequest;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.frontend.ProcessorAccountResponseStatus;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.ProcessorAccountClient;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.ProcessorAccountTransaction;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class ProcessorAccountAddService {
	@Autowired
	private UserService userService;
	@Autowired
	private CashierFrontendService cashierFrontendService;
	@Autowired
	private DomainMethodService dmService;
	@Autowired
	DomainMethodProcessorService dmpService;
	@Autowired
	LithiumServiceClientFactory serviceFactory;
	@Autowired
	ProcessorAccountTransactionService paTransactionService;
	@Autowired
	CashierService cashierService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	Environment environment;
	@Autowired
	ProcessorAccountService processorAccountService;
	@Autowired
	ProcessorAccountVerificationService verificationService;


	public ProcessorAccountResponse addProcessorAccount(AddProcessorAccountRequest request,
														String domainName, String userGuid, String userAgent, String ipAddress) throws Exception {

		User user = userService.findOrCreate(userGuid);

		log.debug("Add processor account is requested for:  methodCode: " + request.getMethodCode() +
			"; userName: " + user.username() + "; userGuid: " + user.getGuid() + " ipAddress: " + ipAddress + "; userAgent:" + userAgent);

		DomainMethodProcessor processor = cashierFrontendService.firstEnabledProcessor(domainName, request.getMethodCode(), true,
						user, ipAddress, userAgent)
				.orElseThrow(() -> new Exception("No processors for this method"));

		ProcessorAccountTransaction transaction = paTransactionService.createTransaction(user, processor, request.getRedirectUrl());

		AccountProcessorRequest processorRequest = createAccountProcessorRequest(user, processor);
		processorRequest.setRedirectUrl(request.getRedirectUrl());
		processorRequest.setMethodCode(request.getMethodCode());
		processorRequest.setAccountTransactionId(transaction.getId());
		processorRequest.setProcessorId(processor.getId());
		processorRequest.setMetadata(request.getMetadata());

		ProcessorAccountClient client = serviceFactory.target(ProcessorAccountClient.class, processor.getProcessor().getUrl(), true);

		ProcessorAccountResponse processorResponse;
		try {
			processorResponse = client.addProcessorAccount(processorRequest);
		} catch (Exception e) {
			log.error("Failed add processor account. Exception " + e.getMessage(), e);
			processorResponse = ProcessorAccountResponse.builder()
				.status(ProcessorAccountResponseStatus.FAILED)
				.generalError(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, user.domainName(), processorRequest.getUser().getLanguage()))
				.errorCode("500")
				.errorMessage(e.getMessage())
				.build();
		}
		processorResponse.setTransactionId(transaction.getId());
		saveProcessorAccount(processorResponse, transaction);

		return processorResponse;
	}

	public lithium.service.cashier.client.objects.ProccesorAccountTransaction getProcessorAccountTransaction(Long transactionId) throws Exception {
		ProcessorAccountTransaction transaction = paTransactionService.getTransactionById(transactionId);
		if (transaction == null) {
			throw new Exception("No processor account transaction with id: " + transactionId);
		}
		return getProcessorAccountTransaction(transaction);
	}

	public lithium.service.cashier.client.objects.ProccesorAccountTransaction getProcessorAccountTransaction(ProcessorAccountTransaction transaction) {
		return lithium.service.cashier.client.objects.ProccesorAccountTransaction.builder()
			.id(transaction.getId())
			.date(new DateTime(transaction.getCreatedOn()))
			.method(transaction.getDomainMethodProcessor().getDomainMethod().getName())
			.state(transaction.getState().getName())
			.processorAccount(processorAccountService.processorAccountFromEntity(transaction.getProcessorAccount(), true))
			.user(transaction.getUser().getGuid())
			.redirectUrl(transaction.getRedirectUrl())
			.generalError(transaction.getGeneralError())
			.build();
	}

	public ProcessorUserCard saveProcessorAccount(ProcessorAccountResponse processorAccountResponse, ProcessorAccountTransaction transaction) throws Exception {
		if (transaction == null) {
			transaction = paTransactionService.getTransactionById(processorAccountResponse.getTransactionId());
			if (transaction == null) {
				throw new Exception("No processor account transaction with id: " + processorAccountResponse.getTransactionId());
			}
		}
		if (processorAccountResponse.getProcessorAccount() != null) {
			verifyProcessorAccount(processorAccountResponse, transaction);
			if (processorAccountResponse.getProcessorAccount().shouldSave()) {
				 processorAccountService.saveProcessorAccount(transaction.getUser(), transaction.getDomainMethodProcessor(), processorAccountResponse.getProcessorAccount(), true);
			}
		}
		transaction = paTransactionService.updateTransaction(transaction.getId(), processorAccountResponse, Optional.ofNullable(processorAccountResponse.getProcessorAccount()).map(ProcessorAccount::getId).orElse(null));

		return transaction.getProcessorAccount();
	}

	public AccountProcessorRequest createAccountProcessorRequest(Long transactionId) throws Exception {
		ProcessorAccountTransaction transaction = paTransactionService.getTransactionById(transactionId);

		if (transaction == null) {
			throw new Exception("No processor transaction with id: " + transactionId);
		}
		AccountProcessorRequest accountProcessorRequest = createAccountProcessorRequest(transaction.getUser(), transaction.getDomainMethodProcessor());
		accountProcessorRequest.setRedirectUrl(transaction.getRedirectUrl());
		return accountProcessorRequest;
	}

	public AccountProcessorRequest createAccountProcessorRequest(User user, DomainMethodProcessor processor) throws Exception {
		lithium.service.user.client.objects.User u = userService.retrieveUserFromUserService(user);
		lithium.service.domain.client.objects.Domain d = userService.retrieveDomainFromDomainService(user.domainName());
		DoProcessorRequestUser processorRequestUser = DoProcessorRequestUser.builder()
			.cellphoneNumber(u.getCellphoneNumber())
			.dobDay(u.getDobDay())
			.dobMonth(u.getDobMonth())
			.dobYear(u.getDobYear())
			.domain(d.getName())
			.email(u.getEmail())
			.firstName(u.getFirstName())
			.lastName(u.getLastName())
			.postalAddress(u.getPostalAddress())
			.residentialAddress(u.getResidentialAddress())
			.socialSecurityNumber(u.getSocialSecurityNumber())
			.telephoneNumber(u.getCellphoneNumber())
			.username(u.getUsername())
			.currency(d.getCurrency())
			.locale(d.getDefaultLocale())
			.language(d.getDefaultLocale().split("-")[0])
			.createdDate(u.getCreatedDate())
			.shortGuid(u.getShortGuid())
			.realGuid(user.guid())
			.countryCode(u.getCountryCode())
			.build();

		HashMap<String, String> properties = new HashMap<>();
		for (DomainMethodProcessorProperty prop : dmpService.propertiesWithDefaults(processor.getId())) {
			properties.put(prop.getProcessorProperty().getName(), prop.getValue());
		}

		return AccountProcessorRequest.builder()
			.properties(properties)
			.user(processorRequestUser)
			.build();
	}

	private void verifyProcessorAccount(ProcessorAccountResponse response, ProcessorAccountTransaction transaction) throws Exception {
		ProcessorAccount processorAccount = verificationService.verifyProcessorAccount(transaction.getUser(), response.getProcessorAccount(), transaction.getDomainMethodProcessor(), true);
		response.setProcessorAccount(processorAccount);

		if (BooleanUtils.isFalse(processorAccount.getVerified())) {
			ProcessorAccountVerificationType failedVerification = processorAccount.getFailedVerification();
			response.setErrorCode(failedVerification.getGeneralError().getCode().toString());
			response.setGeneralError(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, transaction.getDomainMethodProcessor().getDomainMethod().getDomain().getName()));
			response.setErrorMessage("Account verification is failed: " + failedVerification.getDescription());
			log.error("Account is invalid. Verification: " + processorAccount.getFailedVerification() + " Processor account: " + processorAccount);
			response.setStatus(ProcessorAccountResponseStatus.FAILED);
		}
	}
}
