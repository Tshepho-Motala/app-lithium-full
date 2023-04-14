package lithium.service.cashier.services;

import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountTransactionState;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorAccountData;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorAccountDataRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountStatusRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountTransactionStateRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountTypeRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountVerificationTypeRepository;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lithium.service.cashier.exceptions.AccountTransactionLableAlredyExistsException;
import lithium.service.cashier.exceptions.NoAccountTransactionException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.UserApiInternalClient;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.PaymentMethodStatusType.isActiveAccountStatus;

@Slf4j
@Service
public class ProcessorAccountService {
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private CashierFrontendService cashierFrontendService;
	@Autowired
	private DomainMethodService dmService;
	@Autowired
	private ProcessorUserCardRepository processorUserCardRepository;
	@Autowired
	private ProcessorAccountStatusRepository paStatusRepository;
	@Autowired
	private ProcessorAccountTypeRepository paTypeRepository;
	@Autowired
	private ProcessorAccountTransactionStateRepository paTransactioStateRepository;
	@Autowired
	private ProcessorAccountDataRepository paDataRepository;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private ProcessorAccountChangeLogService changeLogService;
	@Autowired
	ProcessorAccountVerificationService verificationService;
	@Autowired
	DomainMethodProcessorService dmpService;
	@Autowired
	private DomainMethodUserService dmUserService;
	@Autowired
	ProcessorAccountTransactionService paTransactionService;
	@Autowired
	ProcessorAccountVerificationTypeRepository verificationTypeRepository;
	@Autowired
	CashierService cashierService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	Environment environment;
	@Autowired
	private LimitInternalSystemService limitInternalSystemService;

	public void setupFromEnum() {
		Arrays.stream(PaymentMethodStatusType.values()).forEach(status ->
			paStatusRepository.findOrCreateByName(status.name(),
					() -> ProcessorAccountStatus.builder().description(status.description()).build()));

		Arrays.stream(ProcessorAccountType.values()).forEach(accountType ->
				paTypeRepository.findOrCreateByName(accountType.name(),
						() -> lithium.service.cashier.data.entities.ProcessorAccountType.builder().build()));

		Arrays.stream(ProcessorAccountTransactionState.values()).forEach(accountType ->
				paTransactioStateRepository.findOrCreateByName(accountType.name(),
						() -> lithium.service.cashier.data.entities.ProcessorAccountTransactionState.builder().build()));

		Arrays.stream(ProcessorAccountVerificationType.values()).forEach(accountType ->
 			verificationTypeRepository.findOrCreateByName(accountType.name(),
				() -> lithium.service.cashier.data.entities.ProcessorAccountVerificationType.builder().build()));
	}

    public long getActiveProcessorAccountsCount(String userGuid) {
        User user = userService.find(userGuid);
		ProcessorAccountStatus status = paStatusRepository.findByName(PaymentMethodStatusType.ACTIVE.getName());
        return processorUserCardRepository.countByUserIdAndStatusId(user.getId(), status.getId());
    }

	public Iterable<ProcessorAccountStatus> getProcessorAccountStatusAll() {
		return paStatusRepository.findAll();
	}

	private void setDomainMathodEnabled(ProcessorUserCard processorAccount) {
		String disableProcessorValue = dmpService.getPropertyValue(processorAccount.getDomainMethodProcessor(), "disable_on_block_account");

		if (disableProcessorValue != null) {
			boolean disableProcessor = Boolean.parseBoolean(disableProcessorValue);
			List processorAccounts =  processorUserCardRepository.findByUserAndDomainMethodProcessor(processorAccount.getUser(), processorAccount.getDomainMethodProcessor());
			if (disableProcessor
					&& processorAccounts.stream().anyMatch(a -> PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()) == PaymentMethodStatusType.BLOCKED)
					&& processorAccounts.stream().allMatch(a -> PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()) == PaymentMethodStatusType.BLOCKED
															|| PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()) == PaymentMethodStatusType.DISABLED
															|| PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()) == PaymentMethodStatusType.EXPIRED)
			) {
				dmUserService.setEnabled(processorAccount.getUser(), processorAccount.getDomainMethodProcessor().getDomainMethod(), false);
			} else {
				dmUserService.setEnabled(processorAccount.getUser(), processorAccount.getDomainMethodProcessor().getDomainMethod(), true);
			}
		}
	}

	public ProcessorUserCard getProcessorUserCardById(Long processorAccountId) {
		return processorUserCardRepository.findOne(processorAccountId);
	}

	public ProcessorAccount getProcessorAccountById(Long processorAccountId) {
		ProcessorUserCard processorUserCard = processorUserCardRepository.findOne(processorAccountId);
		return processorUserCard != null ? processorAccountFromEntity(processorUserCard, true) : null;
	}

	public List<ProcessorAccount> getProcessorAccountsByReference(String reference) {
		return processorUserCardRepository.findByReference(reference).stream().map(pa -> processorAccountFromEntity(pa, true)).collect(Collectors.toList());
	}

	public void updateExpiredUserCard(ProcessorAccount processorAccount) throws Exception {
		if (processorAccount.getType() != ProcessorAccountType.CARD) {
			throw new Exception("Failed to update expired processor account id:" + processorAccount.getId() + ". Unexpected processor account type: " + processorAccount.getType());
		}

		if (isNull(processorAccount.getData())) {
			throw new Exception("Failed to update expired processor account id:" + processorAccount.getId() + ". No data to update.");
		}

		ProcessorUserCard processorAccountEntry = processorUserCardRepository.findOne(processorAccount.getId());

		if (isNull(processorAccountEntry)) {
			throw new Exception("Processor account not found (" + processorAccount.getId() + ")");
		}

		List<ChangeLogFieldChange> clfc = new ArrayList<>();

		//mark as EXPIRED in case  new card was already added before card source updated
		ProcessorUserCard newCardEntry = processorUserCardRepository.findByUserAndFingerprint(processorAccountEntry.getUser(), processorAccount.getData().get("fingerprint"));
		if(nonNull(newCardEntry) && !newCardEntry.getReference().equals(processorAccount.getReference()))
		{
			if (!PaymentMethodStatusType.EXPIRED.getName().equals(newCardEntry.getType().getName())) {
				ProcessorAccountStatus status = paStatusRepository.findByName(PaymentMethodStatusType.EXPIRED.getName());
				changeLogService.addChangeLogField(status, processorAccountEntry.getStatus(), processorAccountEntry.getChangeLogFieldName("status"), clfc);
				processorAccountEntry.setStatus(status);
				processorUserCardRepository.save(processorAccountEntry);
			}
		} else {
			changeLogService.addChangeLogField(processorAccount.getData().get("bin"), processorAccountEntry.getBin(), processorAccountEntry.getChangeLogFieldName("bin"), clfc);
			processorAccountEntry.setBin(processorAccount.getData().get("bin"));

			changeLogService.addChangeLogField(processorAccount.getData().get("last4Digits"), processorAccountEntry.getLastFourDigits(), processorAccountEntry.getChangeLogFieldName("last4Digits"), clfc);
			processorAccountEntry.setLastFourDigits(processorAccount.getData().get("last4Digits"));

			processorAccountEntry.setFingerprint(processorAccount.getData().get("fingerprint"));

			changeLogService.addChangeLogField(processorAccount.getData().get("expiryDate"), processorAccountEntry.getExpiryDate(), processorAccountEntry.getChangeLogFieldName("expiryDate"), clfc);
			processorAccountEntry.setExpiryDate(processorAccount.getData().get("expiryDate"));

			saveProcessorAccountData(processorAccount.getData(), processorAccountEntry);
			processorUserCardRepository.save(processorAccountEntry);
		}

		if (!clfc.isEmpty()) {
			changeLogService.logProcessorAccount(clfc, processorAccountEntry.getUser().getGuid(), processorAccountEntry.getUser().domainName(), null, "Expired account was auto updated.");
		}
		log.info("Expired processor account with id:" + processorAccount.getId() + " was successfully updated.");
	}

	//this do force update !!!
	public ProcessorUserCard updateProcessorAccount(Long processorAccountId, ProcessorAccountStatus status,  String name,  String providerData, Boolean hideInDeposit, Boolean verified, ProcessorAccountVerificationType failedVerification, Boolean contraAccount, Map<String,String> data, String comment, LithiumTokenUtil tokenUtil) throws Exception {
        ProcessorUserCard processorAccountEntry = processorUserCardRepository.findOne(processorAccountId);
        if (isNull(processorAccountEntry)) {
            throw new Exception("Processor account not found (" + processorAccountId + ")");
        }
		return updateProcessorAccount(processorAccountEntry, status, name, providerData, hideInDeposit, verified, failedVerification, contraAccount,  data, comment, tokenUtil);
    }

	//this do force update !!!
    private ProcessorUserCard updateProcessorAccount(ProcessorUserCard processorAccountEntry, ProcessorAccountStatus status,  String name,  String providerData, Boolean hideInDeposit, Boolean verified, ProcessorAccountVerificationType failedVerification, Boolean contraAccount, Map<String,String> data, String comment, LithiumTokenUtil tokenUtil) throws Exception{
		boolean updateProcessorAccount = false;
		List<ChangeLogFieldChange> clfc = new ArrayList<>();

		if (status != null && !status.equals(processorAccountEntry.getStatus())) {
			changeLogService.addChangeLogField(status, processorAccountEntry.getStatus(), processorAccountEntry.getChangeLogFieldName("status"), clfc);
			processorAccountEntry.setStatus(status);
			updateProcessorAccount = true;
		}

		if (providerData != null && !providerData.equals(processorAccountEntry.getProviderData())) {
			processorAccountEntry.setProviderData(providerData);
			updateProcessorAccount = true;
		}

		if (hideInDeposit != null && !hideInDeposit.equals(processorAccountEntry.getHideInDeposit())) {
			processorAccountEntry.setHideInDeposit(hideInDeposit);
			updateProcessorAccount = true;
		}

		if (verified != null && !verified.equals(processorAccountEntry.getVerified())) {
			setVerified(processorAccountEntry, verified, failedVerification, clfc);
			updateProcessorAccount = true;
		}

		if (name != null && name.equals(processorAccountEntry.getName())) {
			processorAccountEntry.setName(name);
			updateProcessorAccount = true;
		}

		if (updateProcessorAccount) {
			processorAccountEntry = processorUserCardRepository.save(processorAccountEntry);
		}

		//account data is expected to be static and should not be updated every time, new account should be created instead
		if (nonNull(data)) {
			ProcessorAccountData existProcessorAccount = paDataRepository.findByProcessorAccount(processorAccountEntry);
			if (isNull(existProcessorAccount) || isNull(existProcessorAccount.getData()) || existProcessorAccount.getData().isEmpty()) {
				saveProcessorAccountData(data, processorAccountEntry);
			}
		}

		saveUserContraAccount(processorAccountEntry, contraAccount, clfc);

		setDomainMathodEnabled(processorAccountEntry);
		if (!clfc.isEmpty()) {
			changeLogService.logProcessorAccount(clfc, processorAccountEntry.getUser().getGuid(), processorAccountEntry.getUser().domainName(), tokenUtil, comment);
		}

		log.debug("Processor account was successfully updated. ProcessorAccount: " + processorAccountEntry);
		return processorAccountEntry;
	}

	private ProcessorUserCard createProcessorAccount(User user, DomainMethodProcessor domainMethodProcessor, ProcessorAccount processorAccount) {
		List<ChangeLogFieldChange> clfc = new ArrayList<>();

		ProcessorUserCard processorAccountEntry = ProcessorUserCard.builder()
			.user(user)
			.domainMethodProcessor(domainMethodProcessor)
			.reference(processorAccount.getReference())
			.providerData(processorAccount.getProviderData())
			.status(paStatusRepository.findByName(processorAccount.getStatus().getName()))
			//last4Digits should be renamed to descriptor in the DB
			.lastFourDigits(processorAccount.getDescriptor())
			.type(paTypeRepository.findByName(processorAccount.getType().getName()))
			.isDefault(true)
			.name(processorAccount.getName())
			.hideInDeposit(processorAccount.isHideInDeposit())
			.isActive(true)
			.build();

		if (processorAccount.getType() == ProcessorAccountType.CARD) {
			processorAccountEntry.setBin(processorAccount.getData().get("bin"));
			processorAccountEntry.setCardType(processorAccount.getData().get("cardType"));
			processorAccountEntry.setExpiryDate(processorAccount.getData().get("expiryDate"));
			processorAccountEntry.setScheme(processorAccount.getData().get("scheme"));
			processorAccountEntry.setFingerprint(processorAccount.getData().get("fingerprint"));
			processorAccountEntry.setName(processorAccount.getData().get("name"));
		}
		setVerified(processorAccountEntry, processorAccount.getVerified(), processorAccount.getFailedVerification(), clfc);

		processorAccountEntry = processorUserCardRepository.save(processorAccountEntry);
		saveProcessorAccountData(processorAccount.getData(), processorAccountEntry);

		changeLogService.logProcessorAccount(clfc, user.getGuid(), user.domainName(), null, "");
		log.debug("Processor account with reference " + processorAccount.getReference() + "was successfully saved . UserCard: " + processorAccount);
		return processorAccountEntry;
	}

	public ProcessorUserCard saveProcessorAccount(User user, DomainMethodProcessor domainMethodProcessor, ProcessorAccount processorAccount, Boolean forceUpdate) throws Exception {
		if (user == null || domainMethodProcessor == null || processorAccount == null) {
			log.error("Failed to save processor account: " + processorAccount + " Incorrect input data. User: " + user + " DomainMethodProcessor: " + domainMethodProcessor);
			throw new Exception("Failed to save user card: " + processorAccount + " Incorrect input data. User: " + user + " DomainMethodProcessor: " + domainMethodProcessor);
		}
		//this is actually processorAccount
		String fingerprint = processorAccount.getData() != null ? processorAccount.getData().get("fingerprint") : null;
		ProcessorUserCard savedProcessorAccount = (fingerprint != null && !fingerprint.isEmpty())
				? processorUserCardRepository.findByUserAndFingerprint(user, fingerprint)
				: processorUserCardRepository.findByUserAndReference(user, processorAccount.getReference());

		if (savedProcessorAccount == null) {
			savedProcessorAccount = createProcessorAccount(user, domainMethodProcessor, processorAccount);
			setProcessorAccountAsContra(savedProcessorAccount);
		} else if (BooleanUtils.isTrue(forceUpdate)) {
			log.info("Account update was requested by " + domainMethodProcessor.getDomainMethod().getName() + " processor for processorAccountId: " + savedProcessorAccount.getId() + ". Values to update:  name=" + processorAccount.getName() + ", hideOndeposit=" + processorAccount.isHideInDeposit() + "accountData: " + processorAccount.getData() );
			savedProcessorAccount = updateProcessorAccount(savedProcessorAccount, null, processorAccount.getName(), processorAccount.getProviderData(), processorAccount.isHideInDeposit(), processorAccount.getVerified(), processorAccount.getFailedVerification(), null, processorAccount.getData(), "Force update processor account", null);
			setProcessorAccountAsContra(savedProcessorAccount);
		}
		processorAccount.setContraAccount(savedProcessorAccount.getContraAccount());
		processorAccount.setId(savedProcessorAccount.getId());
		processorAccount.setVerified(savedProcessorAccount.getVerified());
		processorAccount.setFailedVerification(Optional.ofNullable(savedProcessorAccount.getFailedVerification()).map(t -> ProcessorAccountVerificationType.fromName(t.getName())).orElse(null));
		return savedProcessorAccount;
	}

	private void saveProcessorAccountData(Map<String, String> data, ProcessorUserCard entity) {
		ProcessorAccountData processorAccountData = ofNullable(paDataRepository.findByProcessorAccount(entity))
				.orElse(lithium.service.cashier.data.entities.ProcessorAccountData.builder()
						.processorAccount(entity)
						.build());
		processorAccountData.setData(data);
		paDataRepository.save(processorAccountData);
	}

	public List<ProcessorAccount> getProcessorAccounts(String userGuid, boolean isDeposit) throws Exception {
		User user = userService.findOrCreate(userGuid);
		return getProcessorAccounts(user).stream().map(pa -> getProcessorAccountAndUpdateWithWithdrawMethodCode(pa, isDeposit)).collect(Collectors.toList());
	}

	private List<ProcessorUserCard> getProcessorAccounts(User user) throws Exception {
		if (user == null) {
			log.error("Failed to get user processor account. Incorrect input data. User: " + user);
			throw new Exception("Failed to get processor account. Incorrect input data.");
		}

		log.debug("Processor accounts are requested for user: " + user);
		return processorUserCardRepository.findByUser(user).stream().collect(Collectors.toList());
	}

	public List<ProcessorUserCard> getActiveProcessorAccounts(String userGuid, Boolean isDeposit) throws Exception {
		User user = userService.findOrCreate(userGuid);

		List<ProcessorUserCard> processorAccount = getProcessorAccounts(user).stream().filter(pa -> !BooleanUtils.isFalse(pa.getVerified())).collect(Collectors.toList());

		if (processorAccount != null && !processorAccount.isEmpty()) {
			processorAccount = processorAccount.stream().filter(uc -> uc != null && isActiveAccountStatus(PaymentMethodStatusType.fromName(uc.getStatus().getName()), isDeposit)).collect(Collectors.toList());
		}

		if (isDeposit != null && isDeposit) {
			processorAccount = processorAccount.stream().filter(pa -> !BooleanUtils.isTrue(pa.getHideInDeposit())).collect(Collectors.toList());
		}

		return processorAccount;
	}

	private boolean checkContraAccountRestriction(ProcessorAccount processorAccount, boolean noContraAccountRestrictionInDomain, boolean isDeposit) {
		if (isDeposit || noContraAccountRestrictionInDomain) {
			return true;
		}
		return BooleanUtils.isTrue(processorAccount.getContraAccount());
	}

	private ProcessorAccount getProcessorAccountAndUpdateWithWithdrawMethodCode(ProcessorUserCard processorAccountEntity, boolean isDeposit) {
		ProcessorAccount processorAccount = processorAccountFromEntity(processorAccountEntity, true);
		if (!isDeposit) {
			String withdrawMethodCode = dmpService.getPropertyValue(processorAccountEntity.getDomainMethodProcessor(), "withdraw_method_code");

			if (!StringUtil.isEmpty(withdrawMethodCode)) {
				processorAccount.setMethodCode(withdrawMethodCode);
			}
		}
		return processorAccount;
	}

	public List<ProcessorAccount> getActiveProcessorAccountsMethodsEnabled(String userGuid, String userIp, String userAgent, boolean isDeposit) throws Exception {
		List<ProcessorUserCard> processorAccounts = getActiveProcessorAccounts(userGuid, isDeposit);
		User user = userService.findOrCreate(userGuid);
		boolean noContraAccountRestrictionInDomain = !limitInternalSystemService.contraAccountRestrictionInDomain(user.domainName());
		return processorAccounts.stream()
				.map(pa -> getProcessorAccountAndUpdateWithWithdrawMethodCode(pa, isDeposit))
				.filter(pa -> checkDomainMethodEnabled(pa, userIp, userAgent, isDeposit, user))
				.filter(pa -> checkContraAccountRestriction(pa, noContraAccountRestrictionInDomain, isDeposit))
				.collect(Collectors.toList());
	}

	private boolean checkDomainMethodEnabled(ProcessorAccount pa, String userIp, String userAgent, boolean isDeposit, User user) {
		try {
			List<DomainMethod> domainMethods = dmService.findAllEnabledByCode(user.domainName(), pa.getMethodCode(), isDeposit);
			return domainMethods.stream().anyMatch(dm -> cashierFrontendService.checkDomainMethodUserAndProfile(dm, user, userIp, userAgent,false));
		} catch (Exception ex) {
			log.info("Failed to get domain method for processorAccount: " + pa + " userIp: " + userIp + " userAgent: " + userAgent + " isDeposit: " + isDeposit);
		}
		return false;
	}

	public ProcessorAccount getProcessorAccount(User user, String reference, String fingerprint) throws Exception {
		if (fingerprint == null && reference == null || user == null) {
			log.error("Failed to get processor account. Incorrect input data. User: " + user + " Processor account reference: " + reference + " Fingerprint: " + fingerprint);
			throw new Exception("Failed to get processor account. Incorrect input data.");
		}

		ProcessorUserCard savedProcessorAccount = (fingerprint != null && !fingerprint.isEmpty())
			? processorUserCardRepository.findByUserAndFingerprint(user, fingerprint)
			: processorUserCardRepository.findByUserAndReference(user, reference);

		return processorAccountFromEntity(savedProcessorAccount, true);
	}

    public List<ProcessorAccount> getProcessorAccountsPerUserAndType(String guid, String type) throws Exception {
        if (type == null || guid == null) {
            log.warn("Can't validate processor account owner. Missing arguments: " + "type: " + type + ", userGuid: " + guid);
            throw new Exception("Missing arguments");
        }

        List<ProcessorUserCard> processorUserCards = processorUserCardRepository.findByUserGuidAndTypeName(guid, type);

        return processorUserCards.stream()
                .map(account -> processorAccountFromEntity(account, true))
                .collect(Collectors.toList());
    }

    public List<ProcessorAccount> getProcessorAccounts(String domainName, String reference, String type) throws Exception {
        if (reference == null || type == null || domainName == null) {
            log.warn("Can't get processor account. Missing arguments: " + reference + " , " + type + ", " + domainName);
            throw new Exception("Missing arguments");
        }

        List<ProcessorUserCard> processorUserCards = processorUserCardRepository.findByReferenceAndTypeNameAndDomainMethodProcessorDomainMethodDomainName(reference, type, domainName);

        return processorUserCards.stream()
				.map(processorUserCard -> processorAccountFromEntity(processorUserCard, true))
				.collect(Collectors.toList());

    }

	public ProcessorAccount processorAccountFromEntity(ProcessorUserCard paEntity, boolean withData) {
		if (paEntity == null) {
			return null;
		}
		Map<String, String> data = null;
		if (withData) {
			lithium.service.cashier.data.entities.ProcessorAccountData pad = paDataRepository.findByProcessorAccount(paEntity);
			//for backward capability
			if (pad == null || pad.getData() == null)
			{
				if(ProcessorAccountType.fromName(paEntity.getType().getName()) == ProcessorAccountType.CARD) {
					data = new HashMap<>();
					data.put("scheme", paEntity.getScheme());
					data.put("cardType", paEntity.getCardType());
					data.put("bin", paEntity.getBin());
					data.put("last4Digits", paEntity.getLastFourDigits());
					data.put("name", paEntity.getName());
					data.put("expiryDate", paEntity.getExpiryDate());
				}
			} else {
				data = pad.getData();
			}
		}
		return ProcessorAccount.builder()
				.id(paEntity.getId())
				.userGuid(paEntity.getUser().getGuid())
				.reference(paEntity.getReference())
				.providerData(paEntity.getProviderData())
				.descriptor(paEntity.getLastFourDigits())
				.status(PaymentMethodStatusType.fromName(paEntity.getStatus().getName()))
				.type(ProcessorAccountType.fromName(paEntity.getType().getName()))
				.name(paEntity.getName())
				.data(data)
				.methodCode(paEntity.getDomainMethodProcessor().getDomainMethod().getMethod().getCode())
                .hideInDeposit(paEntity.getHideInDeposit() != null ? paEntity.getHideInDeposit() : false)
				.verified(paEntity.getVerified())
				.failedVerification(Optional.ofNullable(paEntity.getFailedVerification()).map(t -> ProcessorAccountVerificationType.fromName(t.getName())).orElse(null))
				.contraAccount(paEntity.getContraAccount())
				.build();
	}

	public boolean addTransactionRemark(Long transactionId, String reference) {
		try {
			Transaction transaction = transactionService.findById(transactionId);
			if (transaction == null) throw new Exception("Invalid transaction ID " + transactionId);

			ProcessorAccount processorAccount = getProcessorAccount(transaction.getUser(), reference, null);
			if (processorAccount == null) {
				throw new Exception("No processor account with reference: " + reference + ". TransactionId: " + transactionId);
			}
			return addTransactionRemark(transaction, processorAccount);
		} catch (Exception ex) {
			log.error("Failed to add processor account remark. TransactionId: " + transactionId + " Processor account reference:" + reference + " Exception " + ex.getMessage(), ex);
			return false;
		}
	}

	public boolean addTransactionRemark(Long transactionId, Long processorAccountId) {
		try {
			Transaction transaction = transactionService.findById(transactionId);
			if (transaction == null) throw new Exception("Invalid transaction ID " + transactionId);

			ProcessorAccount processorAccount = getProcessorAccountById(processorAccountId);
			if (processorAccount == null) {
				throw new Exception("No processor account with id: " + processorAccountId + ". TransactionId: " + transactionId);
			}
			return addTransactionRemark(transaction, processorAccount);
		} catch (Exception ex) {
			log.error("Failed to add processor account remark. TransactionId: " + transactionId + " Processor account id:" + processorAccountId + " Exception " + ex.getMessage(), ex);
			return false;
		}
	}

	public boolean addTransactionRemark(Transaction transaction, ProcessorAccount processorAccount) {
		try {
			if (processorAccount.getData() != null) {
				String remark = "Additional Transaction Information: " + processorAccount.getData().entrySet().stream()
						.filter(entrySet -> !entrySet.getKey().equals("fingerprint"))
						.map(entrySet -> entrySet.getKey() + ": " + entrySet.getValue())
						.collect(Collectors.joining(", ", "", "."));
				transactionService.addTransactionRemark(transaction, transaction.getUser().getGuid(), remark, TransactionRemarkType.ACCOUNT_DATA);
			} else {
				throw new Exception("Processor account data is empty.");
			}
		} catch (Exception ex) {
			log.error("Failed to add processor account remark. TransactionId: " + transaction.getId() + " Processor account:" + processorAccount + " Exception " + ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public ProcessorAccountStatus findOrCreateHistoricProcessorAccountStatus() {
		ProcessorAccountStatus processorAccountStatus = paStatusRepository.findByName(PaymentMethodStatusType.HISTORIC.getName());
		if (processorAccountStatus != null) return processorAccountStatus;
		return ProcessorAccountStatus.builder()
				.name(PaymentMethodStatusType.HISTORIC.name())
				.description(PaymentMethodStatusType.HISTORIC.description())
				.version(0)
				.build();
	}

	public lithium.service.cashier.data.entities.ProcessorAccountType findOrCreateHistoricProcessorAccountType() {
		lithium.service.cashier.data.entities.ProcessorAccountType processorAccountType = paTypeRepository.findByName(ProcessorAccountType.HISTORIC.getName());
		if (processorAccountType != null) return processorAccountType;
		return lithium.service.cashier.data.entities.ProcessorAccountType.builder()
				.name(ProcessorAccountType.HISTORIC.name())
				.version(0)
				.build();
	}

	@Retryable(interceptor = "cashier.retryLoggingInterceptor", maxAttempts = 2, backoff = @Backoff(delay = 10, multiplier = 5), include = { NoAccountTransactionException.class })
	public void addProcessorAccountLabel(Transaction transaction, ProcessorAccount processorAccount) throws Exception {
		if (processorAccount.getId() == null) {
			log.info("PLAYER_PAYMENT_METHOD_REFERENCE label will not be added for transaction " + transaction.getId() + ". Processor account: " + processorAccount);
			return;
		}
		try {
			TransactionLabelBasic tlb = TransactionLabelBasic.builder()
					.labelName(lithium.cashier.CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE)
					.labelValue(String.valueOf(processorAccount.getId()))
					.summarize(true)
					.build();

			cashierService.addAccountingTransactionLabel(transaction, Arrays.asList(tlb));
		} catch (Exception ex) {
			if (ex instanceof NoAccountTransactionException) {
				throw ex;
			} else if (ex instanceof AccountTransactionLableAlredyExistsException) {
				return;
			}
			log.error("Failed to add processor account id label to account transaction. TransactionId: " + transaction.getId() + " Processor account:" + processorAccount + " Exception " + ex.getMessage(), ex);
		}
	}

	private boolean shouldSetContraAccount(ProcessorUserCard processorAccount) {
		return Optional.ofNullable(dmpService.getPropertyValue(processorAccount.getDomainMethodProcessor(), "set_contra_account")).map(Boolean::parseBoolean).orElse(false);
	}

	public boolean isContraAccount(ProcessorUserCard processorAccount) {
		return processorUserCardRepository.findByUserGuidAndContraAccountTrueAndVerifiedTrue(processorAccount.getUser().guid()).isEmpty()
			&& BooleanUtils.isTrue(processorAccount.getVerified());
	}

	private ProcessorUserCard setProcessorAccountAsContra(ProcessorUserCard processorAccount) throws Exception {
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		if (shouldSetContraAccount(processorAccount)) {
			saveUserContraAccount(processorAccount, isContraAccount(processorAccount), clfc);
			changeLogService.logProcessorAccount(clfc, processorAccount.getUser().getGuid(), processorAccount.getUser().domainName(), null, "");
		}
		return processorAccount;
	}

	private void saveUserContraAccount(ProcessorUserCard processorAccountEntry, Boolean contraAccount, List<ChangeLogFieldChange> clfc) {
		if (contraAccount != null && !contraAccount.equals(processorAccountEntry.getContraAccount())) {
			List<ProcessorUserCard> accountsToUpdate = Stream.of(processorAccountEntry).collect(Collectors.toList());
			if (BooleanUtils.isTrue(contraAccount)) {
				accountsToUpdate.addAll(processorUserCardRepository.findByUserAndContraAccountTrueAndVerifiedTrue(processorAccountEntry.getUser()));
				accountsToUpdate.forEach(pa -> setContraAccount(pa, pa.getId() == processorAccountEntry.getId(), clfc));
			} else {
				setContraAccount(processorAccountEntry,false, clfc);
			}

			//update user registration data with contra account details
			updateUserAdditionalData(processorAccountEntry.getUser(), processorAccountFromEntity(processorAccountEntry, true), !BooleanUtils.isTrue(processorAccountEntry.getContraAccount()));
			processorUserCardRepository.saveAll(accountsToUpdate);

		}
	}

	private void setContraAccount(ProcessorUserCard processorAccount, boolean isContraAccount, List<ChangeLogFieldChange> clfc) {
		changeLogService.addChangeLogField(isContraAccount, processorAccount.getContraAccount(), processorAccount.getChangeLogFieldName("contraAccount"), clfc);
		processorAccount.setContraAccount(isContraAccount);
	}

	public ProcessorAccount getVerifiedContraProcessorAccount(String guid) throws Exception {
		User user = userService.findOrCreate(guid);
		List<ProcessorUserCard> contraAccount = processorUserCardRepository.findByUserAndContraAccountTrueAndVerifiedTrue(user);
		if (contraAccount.isEmpty()) {
			return null;
		} else if (contraAccount.size() > 1) {
			log.error("Found more that one verified contra processor accounts (" + guid + "): " + contraAccount);
			throw new Exception("Found more that one verified contra processor accounts");
		}
		return getProcessorAccountAndUpdateWithWithdrawMethodCode(contraAccount.get(0), false);
	}

	private void setVerified(ProcessorUserCard processorAccountEntry, Boolean verified, ProcessorAccountVerificationType failedVerification, List<ChangeLogFieldChange> clfc) {
		changeLogService.addChangeLogField(verified, processorAccountEntry.getVerified(), processorAccountEntry.getChangeLogFieldName("verified"), clfc);
		changeLogService.addChangeLogField(failedVerification, processorAccountEntry.getFailedVerification(), processorAccountEntry.getChangeLogFieldName("failed_verification"), clfc);

		processorAccountEntry.setVerified(verified);
		processorAccountEntry.setFailedVerification(Optional.ofNullable(failedVerification).map(fv -> verificationTypeRepository.findByName(fv.getName())).orElse(null));
	}

	private void updateUserAdditionalData(User user, ProcessorAccount processorAccount, boolean remove) {
		try {
			UserApiInternalClient client = services.target(UserApiInternalClient.class, "service-user", true);
			switch (processorAccount.getType()) {
				case BANK:
					String iban;
					if (!remove) {
						iban = processorAccount.getData().get("iban");
						if (StringUtil.isEmpty(iban)) {
							throw new Exception("IBAN is not defined for processor account.");
						}
					} else {
						iban = processorAccount.getData().get("iban");
					}

					client.updateOrAddUserLabelValues(user.guid(), Collections.singletonMap("IBAN", iban));
					break;
				default:
					throw new Exception("Contra account can be BANK/IBAN only, for now");
			}

		} catch(Exception e) {
			log.error("Failed to update user "+ user.getGuid() + " registration data with processor account (id=" +processorAccount.getId() + ") detailes. Exception:" + e.getMessage(), e);
		}
	}

	public Optional<String> getAccountBankCodeValue(ProcessorUserCard paymentMethod) {
		return ofNullable(paDataRepository.findByProcessorAccount(paymentMethod))
				.map(ProcessorAccountData::getData)
				.map(map -> map.get("bank_code"));
	}

	public Long getActiveProcessorAccountsPerProcessor(User user, DomainMethodProcessor domainMethodProcessor) {
		return processorUserCardRepository.countByUserIdAndDomainMethodProcessorIdAndStatusName(user.getId(), domainMethodProcessor.getId(), PaymentMethodStatusType.ACTIVE.getName());
	}
}
