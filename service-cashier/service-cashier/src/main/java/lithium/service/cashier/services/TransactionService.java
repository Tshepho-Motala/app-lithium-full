package lithium.service.cashier.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.cashier.CashierTransactionLabels;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingTransactionLabelClient;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.LabelValue;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.AccountType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionAmountsData;
import lithium.service.cashier.data.entities.TransactionComment;
import lithium.service.cashier.data.entities.TransactionData;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionRemark;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionTag;
import lithium.service.cashier.data.entities.TransactionTagTypeInfo;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.entities.backoffice.ManualCashierAdjustmentAccountCode;
import lithium.service.cashier.data.entities.backoffice.ShortenCashierTransactionBO;
import lithium.service.cashier.data.entities.frontend.TransactionFE;
import lithium.service.cashier.data.objects.DepositResponse;
import lithium.service.cashier.data.objects.LastXTransactionResponseBO;
import lithium.service.cashier.data.objects.ProcessorAccountDetails;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.cashier.data.repositories.DomainMethodRepository;
import lithium.service.cashier.data.repositories.TagTypeRepository;
import lithium.service.cashier.data.repositories.TransactionCommentRepository;
import lithium.service.cashier.data.repositories.TransactionDataRepository;
import lithium.service.cashier.data.repositories.TransactionPaymentTypeRepository;
import lithium.service.cashier.data.repositories.TransactionRemarkRepository;
import lithium.service.cashier.data.repositories.TransactionRemarkTypeRepository;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.data.repositories.TransactionStatusRepository;
import lithium.service.cashier.data.repositories.TransactionTagRepository;
import lithium.service.cashier.data.repositories.TransactionWorkflowHistoryRepository;
import lithium.service.cashier.data.specifications.TransactionSpecification;
import lithium.service.cashier.exceptions.Status407TransactionInFinalStateException;
import lithium.service.cashier.exceptions.TransactionUniqueProcessorReferenceException;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.UserEvent;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private TransactionCommentRepository commentRepo;
	@Autowired
	private TransactionStatusRepository statusRepo;
	@Autowired
	private TransactionWorkflowHistoryRepository workflowRepo;
	@Autowired
	private TransactionDataRepository dataRepo;
	@Autowired
	private TransactionRemarkRepository transactionRemarkRepository;
	@Autowired
	private TransactionRemarkTypeRepository rmTypeRepository;
	@Autowired
	private TransactionTagRepository transactionTagRepository;
	@Autowired
	private TransactionPaymentTypeRepository transactionPaymentTypeRepository;
	@Autowired
	private ProcessorAccountService processorAccountService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionStatusService statuses;
	@Autowired
	private DomainMethodRepository domainMethodRepository;
    @Autowired
    private DomainMethodProcessorRepository domainMethodProcessorRepository;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private DomainService domainService;
	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private TagTypeRepository tagTypeRepository;
	@Autowired
	AccountingClientService accountingClientService;
	@Autowired
	private UserApiInternalClientService userApiInternalClientService;
	@Autowired
	private ChangeLogService changeLogService;

	private static final int PAGE_SIZE = 100;
	private static final String NOT_PREFIX = "NOT_";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private AccountingTransactionLabelClient getAccountingTransactionLabelClient() throws Exception {
		return services.target(AccountingTransactionLabelClient.class, "service-accounting-provider-internal", true);
	}
	
	private UserEventClient getUserEventService() throws Exception {
		return services.target(UserEventClient.class, "service-user", true);
	}

	@Transactional
	public Transaction startTransaction(Long sessionId, TransactionType type, DomainMethod domainMethod, Long amountCents,
										String currencyCode, String userGuid, String source, DomainMethodProcessor processor,
										Boolean directWithdrawal, User initiationAuthor, Long ttl, Transaction linkedTransaction) {
		Transaction t = Transaction.builder()
				.sessionId(sessionId)
				.amountCents(amountCents)
				.transactionType(type)
				.createdOn(new Date())
				.currencyCode(currencyCode)
				.domainMethod(domainMethod)
				.user(userService.findOrCreate(userGuid))
				.directWithdrawal(type == TransactionType.WITHDRAWAL ? false : null)
				.ttl(ttl)
				.directWithdrawal(directWithdrawal)
				.initiationAuthor(initiationAuthor)
				.linkedTransaction(linkedTransaction)
				.build();
		t = transactionRepository.save(t);

		linkedTransaction = t.getLinkedTransaction();
		if (linkedTransaction != null) {
			linkedTransaction.setLinkedTransaction(t);
			t.setLinkedTransaction(transactionRepository.save(linkedTransaction));
		}

		TransactionWorkflowHistory h = TransactionWorkflowHistory.builder()
				.transaction(t)
				.timestamp(new Date())
				.stage(1)
				.status(statuses.findOrCreate("START", true))
				.source(source)
				.processor(processor)
				.build();

		h = workflowRepo.save(h);
		t.setCurrent(h);
		t.setStatus(h.getStatus());
		t = transactionRepository.save(t);

		return t;
	}

	@Transactional
	public Transaction startTransactionWithUniqueProcessorReference(Long sessionId, TransactionType type, DomainMethod domainMethod, Long amountCents, String currencyCode, String userGuid, String source, String processorReference, Long ttl) throws TransactionUniqueProcessorReferenceException {
		Transaction t = transactionRepository.findByProcessorReferenceAndDomainMethod(processorReference, domainMethod);
		if (nonNull(t)) {
			log.error("Transaction with defined processor reference already exists: " + t);
			throw new TransactionUniqueProcessorReferenceException(t.getId().toString());
		}
		t = Transaction.builder()
				.processorReference(processorReference)
				.sessionId(sessionId)
				.amountCents(amountCents)
				.transactionType(type)
				.createdOn(new Date())
				.currencyCode(currencyCode)
				.domainMethod(domainMethod)
				.user(userService.findOrCreate(userGuid))
				.ttl(ttl)
				.build();
		t = transactionRepository.save(t);

		TransactionWorkflowHistory h = TransactionWorkflowHistory.builder()
				.transaction(t)
				.timestamp(new Date())
				.stage(1)
				.status(statuses.findOrCreate("START", true))
				.source(source)
				.build();

		h = workflowRepo.save(h);
		t.setCurrent(h);
		t.setStatus(h.getStatus());
		t = transactionRepository.save(t);

		return t;
	}

	/**
	 *
	 * Please use proxy method in lithium.service.cashier.machine.DoMachine#addWorkflowEntry(lithium.service.cashier.data.entities.Transaction, lithium.service.cashier.data.entities.User, lithium.service.cashier.data.entities.DomainMethodProcessor, java.lang.String, boolean, int, java.lang.Long, java.lang.String, java.lang.String)
	 *
	 */
	@Transactional(rollbackOn=Exception.class)
	public Transaction saveWorkflowEntry(TransactionWorkflowHistory entry) {
			Transaction transaction = transactionRepository.findOne(entry.getTransaction().getId());
			entry = workflowRepo.save(entry);
			transaction.setCurrent(entry);
			transaction.setStatus(entry.getStatus());
			if (!StringUtil.isEmpty(entry.getTransaction().getDeclineReason())) {
				transaction.setDeclineReason(entry.getTransaction().getDeclineReason());
			}
			transactionRepository.save(transaction);
			return transaction;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10), exclude = NotRetryableErrorCodeException.class, include = DoErrorException.class)
	public Transaction addWorkflowEntrySafe(Transaction transaction, User author,
											DomainMethodProcessor processor, String status, boolean active,
											int stage, Long accountingReference, String source,
											String billingDescriptor, Long processDelay)
			throws Status407TransactionInFinalStateException, DoErrorException {

		Date now = new Date();
		Date processTime = processDelay != null ? new Date(now.getTime() + processDelay) : null;
		try {
			TransactionWorkflowHistory entry = TransactionWorkflowHistory.builder()
					.transaction(transaction)
					.timestamp(now)
					.processTime(active ? processTime : null)
					.author(author)
					.processor(processor)
					.status(statuses.findOrCreate(status, active))
					.stage(stage)
					.source(source)
					.accountingReference(accountingReference)
					.billingDescriptor(billingDescriptor)
					.build();
			return saveWorkflowEntry(entry);
		} catch (OptimisticLockingFailureException | StaleObjectStateException e) {
			log.info("e " + e.getMessage());
			transaction = transactionRepository.findOne(transaction.getId());
			String currentTranStatus = transaction.getCurrent().getStatus().getCode();
			if (finalStateCodes().contains(currentTranStatus)) {
				throw new Status407TransactionInFinalStateException();
			} else {
				throw new DoErrorException(e.getClass().getName() + " occurred but the transaction is not in a final state." +
						" Transaction workflow update will be retried a couple of times.");
			}
		} catch (Exception e) {
			throw new DoErrorException(e.getMessage());
		}
	}

	private List<String> finalStateCodes() {
		List<String> finalStateCodes = Arrays.asList(DoMachineState.values())
				.stream()
				.filter(state -> !state.isActive())
				.map(state -> state.name())
				.collect(Collectors.toList());
		return finalStateCodes;
	}

	public void update(Transaction transaction) {
		transactionRepository.save(transaction);
	}

	public void sendUserEventDeposit(Transaction transaction, String status, String processorMessage, Long depositCount) {
		sendUserEvent(transaction, status, "DEPOSIT", processorMessage, depositCount);
	}
	public void sendUserEventWithdraw(Transaction transaction, String status, String processorMessage) {
		sendUserEvent(transaction, status, "PAYOUT", processorMessage, null);
	}
	
	private void sendUserEvent(Transaction transaction, String status, String type, String processorMessage, Long depositCount) {
		DepositResponse response = new DepositResponse();
		response.setDepositCount(depositCount);
		response.setTransactionId(transaction.getId());
		response.setAccountingReference(transaction.getCurrent().getAccountingReference());
		response.setSuccess(((status == "SUCCESS") || (status == "APPROVED")));
		response.setState(status);
//		response.setMessage("Transaction successfully completed with reference : "+transaction.getId()+"/"+transaction.getCurrent().getAccountingReference());
		response.setMessage(processorMessage);
		response.setData(transaction.getAmountCents()+"");
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			getUserEventService().registerEvent(
				transaction.getUser().domainName(),
				transaction.getUser().username(),
				UserEvent.builder()
				.type("CASHIER_"+type+"_RESULT")
				.data(mapper.writeValueAsString(response))
				.message(type+" RESULT")
				.build()
			);
		} catch (Exception e) {
			log.error("Unable to register user event for deposit status: " + e + " " + transaction.toString(), e);
		}
	}
	
	public TransactionComment addComment(Transaction transaction, User author, String comment) {
		return commentRepo.save(TransactionComment.builder()
				.author(author)
				.transaction(transaction)
				.workflow(transaction.getCurrent())
				.timestamp(new Date())
				.comment(comment)
				.build());
	}


    public Transaction markForcedSuccess(Transaction transaction) {
        transaction.setForcedSuccess(true);
        return transactionRepository.save(transaction);
    }

    public Transaction updatePaymentTypeAndAdditionalReference(Transaction transaction, String paymentType, String additionalReference) {

	    boolean changesDetected = false;

        if (paymentType != null && !paymentType.isEmpty()) {
            TransactionPaymentType transactionPaymentType = getOrCreateTransactionPaymentType(paymentType);
            if (transactionPaymentType != null) {
                transaction.setTransactionPaymentType(transactionPaymentType);
                changesDetected = true;
            }
        }

        if (additionalReference != null && !additionalReference.isEmpty()) {
            transaction.setAdditionalReference(additionalReference);
            changesDetected = true;
        }

        if (changesDetected) {
            return transactionRepository.save(transaction);
        } else {
            return transaction;
        }
    }

	private TransactionPaymentType getOrCreateTransactionPaymentType(String paymentType) {
		TransactionPaymentType transactionPaymentType = transactionPaymentTypeRepository.findByPaymentType(paymentType);
		if (transactionPaymentType == null) {
			transactionPaymentType = new TransactionPaymentType();
			transactionPaymentType.setPaymentType(paymentType);
			transactionPaymentTypeRepository.save(transactionPaymentType);
		}
		return transactionPaymentType;
	}

	@Transactional
	@Retryable(interceptor = "cashier.retryLoggingInterceptor", maxAttempts = 5, backoff = @Backoff(delay = 10), include = { ObjectOptimisticLockingFailureException.class, GenericJDBCException.class })
	public Transaction updateTransactionFromProcessorResponse(Long transactionId, DoProcessorResponse response, String accountInfo) {
		Transaction transaction = transactionRepository.findOne(transactionId);
		boolean save = false;
		if (accountInfo != null && (transaction.getAccountInfo() == null || !accountInfo.equalsIgnoreCase(transaction.getAccountInfo()))) {
			save = true;
			transaction.setAccountInfo(accountInfo);
		}
		if (response.getProcessorReference() != null && (transaction.getProcessorReference() == null || !response.getProcessorReference().equalsIgnoreCase(transaction.getProcessorReference()))) {
			save = true;
			transaction.setProcessorReference(response.getProcessorReference());
		}
		if (response.getAdditionalReference() != null && (transaction.getAdditionalReference() == null || !response.getAdditionalReference().equalsIgnoreCase(transaction.getAdditionalReference()))) {
			save = true;
			transaction.setAdditionalReference(response.getAdditionalReference());
		}
		if (response.getPaymentMethodId() != null && (transaction.getPaymentMethod() == null || transaction.getPaymentMethod().getId() != response.getPaymentMethodId())) {
			save = true;
			transaction.setPaymentMethod(processorAccountService.getProcessorUserCardById(response.getPaymentMethodId()));
		}
		if (response.getPaymentType() != null) {
			TransactionPaymentType transactionPaymentType = getOrCreateTransactionPaymentType(response.getPaymentType());
			if (transaction.getTransactionPaymentType() == null || transactionPaymentType.getId() != transaction.getTransactionPaymentType().getId()) {
				save = true;
				transaction.setTransactionPaymentType(transactionPaymentType);
			}
		}
		if (response.getErrorCode() != null && (transaction.getErrorCode() == null || transaction.getErrorCode() != response.getErrorCode())) {
			save = true;
			transaction.setErrorCode(response.getErrorCode());
		}

		return save ? transactionRepository.save(transaction) : transaction;
	}

	@Transactional
	@Retryable(maxAttempts=5, backoff=@Backoff(delay=10), include=DoErrorException.class)
	public Transaction saveWithdrawalReservedFundsAccRefs(Transaction transaction, Long accRefToWithdrawalPending,
	                                                      Long accRefFromWithdrawalPending) throws DoErrorException {
		log.debug("TransactionService.saveWithdrawalReservedFundsAccRefs [transaction="+transaction
			+ ", accRefToWithdrawalPending="+accRefToWithdrawalPending+", accRefFromWithdrawalPending="
			+ accRefFromWithdrawalPending+"]");
		try {
			transaction = findById(transaction.getId());
			if (accRefToWithdrawalPending != null) transaction.setAccRefToWithdrawalPending(accRefToWithdrawalPending);
			if (accRefFromWithdrawalPending != null) transaction.setAccRefFromWithdrawalPending(accRefFromWithdrawalPending);
			return transactionRepository.save(transaction);
		} catch (Exception e) {
			throw new DoErrorException(e.getMessage());
		}
	}

	/**
	 * Activates or deactivates the retry via scheduled job execution on a specific transaction
	 * @param transactionId
	 * @param doRetry
	 * @return Transaction
	 */
	@Transactional
	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class })
	public Transaction saveDoRetryTransaction(Long transactionId, boolean doRetry) {
		Transaction transaction = transactionRepository.findOne(transactionId);
		transaction.setRetryProcessing(doRetry);
		return transactionRepository.save(transaction);
	}

	public void updateTtl(Long transactionId, Long ttl) {
		if (ttl == null) return;
		Transaction transaction = transactionRepository.findOne(transactionId);
		transaction.setTtl(ttl);
		transactionRepository.save(transaction);
	}
	public Long getTtl(Long transactionId) {
		Long ttl = -1L;
		Transaction transaction = transactionRepository.findOne(transactionId);
		if (transaction.getTtl()!=null) {
			ttl = transaction.getTtl();
		}
		return ttl;
	}


	public Transaction findLastPendingTransaction(DomainMethod domainMethod, User user) {
		return transactionRepository.findByDomainMethodAndUserAndStatusActive(domainMethod, user, true)
				.stream()
				.max(Comparator.comparing(Transaction::getId)).orElse(null);
	}
	
	public Map<Integer, Map<String, String>> getData(Transaction transaction, boolean output) {
		 Map<Integer, Map<String, String>> response = new HashMap<>();
		 for (TransactionData data: dataRepo.findByTransactionAndOutput(transaction, output)) {
			 if (response.get(data.getStage()) == null) {
				 response.put(data.getStage(), new HashMap<>());
			 }
			 response.get(data.getStage()).put(data.getField(), data.getValue());
		 }
		 return response;
	}
	
	public String getData(Transaction transaction, String field, int stage, boolean output) {
		if (transaction == null) return null;
		TransactionData data = dataRepo.findByTransactionAndFieldAndStageAndOutput(transaction, field, stage, output);
		if (data == null) return null;
		return data.getValue();
	}

	public String getDataByTransactionId(Long transactionId, String field, int stage, boolean output) {
		return ofNullable(transactionId)
				.map(id -> dataRepo.findByTransactionIdAndFieldAndStageAndOutput(id, field, stage, output))
				.map(data -> ofNullable(data).map(TransactionData::getValue).orElse(null))
				.orElse(null);
	}

	@Transactional
	@Retryable(interceptor = "cashier.retryLoggingInterceptor", maxAttempts = 5, backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class,GenericJDBCException.class })
	public void setData(Transaction transaction, String field, String value, Integer stage, boolean output) {
		TransactionData data = dataRepo.findByTransactionAndFieldAndStageAndOutput(transaction, field, stage, output);
		if (data == null) {
			data = TransactionData.builder()
					.transaction(transaction)
					.field(field)
					.stage(stage)
					.output(output)
					.build();
		} else if (data.getValue() != null && data.getValue().equalsIgnoreCase(value)) {
			return;
		}
		data.setValue(value);
		dataRepo.save(data);
	}

	public List<Transaction> findWithTTL() {
		List<String> statusIncludeList = Arrays.asList("WAITFORPROCESSOR", "PENDING_CANCEL");
		return transactionRepository.findByTtlNotAndStatusCodeIn(-1L, statusIncludeList);
	}

	public List<Transaction> findByTransactionTypeAndStatusCode(TransactionType transactionType, String statusCode) {
		return transactionRepository.findByTransactionTypeAndStatusCode(transactionType, statusCode);
	}
	
	public List<Transaction> findTransWithEmptyFields() {
		return transactionRepository.findByBonusCodeIsNullAndAccountInfoIsNull();
	}
	
	public Transaction findByProcessorReference(String processorReference) {
		return transactionRepository.findByProcessorReference(processorReference);
	}

	public Transaction findByAdditionalReference(String additionalReference) {
		return transactionRepository.findByAdditionalReference(additionalReference);
	}

	public Transaction findById(Long transactionId) {
		return transactionRepository.findOne(transactionId);
	}
	
	private Page<Transaction> findAll(DomainMethodProcessor domainMethodProcessor, DomainMethod domainMethod, String guid, String domainName,
									  String transactionType, List<String> statuses, String search, DateTime createdStartDate, DateTime createdEndDate,
									  DateTime updatedStartDate, DateTime updatedEndDate, DateTime registrationStartDate, DateTime registrationEndDate,
                                      String processorReference, String additionalReference,
									  String paymentType, String declineReason, String lastFourDigits, String transactionId,
									  @NotNull List<String> includedTransactionTagsNames, @NotNull List<String> excludedTransactionTagsNames,
                                      Boolean isTestAccount, String transactionRuntimeQuery, String depositCountQuery, String daysSinceFirstDepositQuery,
                                      String transactionAmount, String activePaymentMethodCount, List<Long> userStatusIds, List<Long> userTagIds,
                                      PageRequest pageRequest) {
		User user = null;
		if ((guid != null) && (!guid.isEmpty())) {
			user = userService.findOrCreate(guid);
		}

		List<TransactionStatus> transactionStatuses = statusRepo.findAllByCodeIn(statuses);
		Domain domain = domainService.findOrCreateDomain(domainName);
		TransactionPaymentType transactionPaymentType = transactionPaymentTypeRepository.findByPaymentType(paymentType);

		// Note: the change made here for LSPLAT-9827 means that transactions tied to disabled domain method processors
		// will now be visible in LBO and XLS.
		List<DomainMethod> domainMethods = Optional.ofNullable(domainMethodProcessor)
			.map(dm -> Arrays.asList(dm.getDomainMethod()))
			.orElseGet(() -> Optional.ofNullable(domainMethod)
				.map(dm -> Arrays.asList(dm))
				.orElseGet(() -> Optional.ofNullable(transactionType)
						.map(dm -> domainMethodRepository.findByDomainAndDepositOrderByPriority(domain, TransactionType.DEPOSIT.description().equalsIgnoreCase(transactionType)))
						.orElseGet(() -> domainMethodRepository.findByDomain(domain))));

		Set<TransactionTagType> includedTagTypes = getTransactionTagTypes(includedTransactionTagsNames);
		Set<TransactionTagType> excludedTagTypes = getTransactionTagTypes(excludedTransactionTagsNames);

		Page<Transaction> transactionPage = domainMethods.isEmpty() ? Page.empty(pageRequest) : transactionRepository
				.findAll(
						TransactionSpecification.table(
								domainMethods,
								user,
								transactionStatuses,
								search,
								createdStartDate,
								createdEndDate,
								updatedStartDate,
								updatedEndDate,
								registrationStartDate,
								registrationEndDate,
								processorReference,
								additionalReference,
								transactionPaymentType,
								declineReason,
								lastFourDigits,
								transactionId,
								includedTagTypes,
								excludedTagTypes,
								isTestAccount,
								transactionRuntimeQuery,
                                depositCountQuery,
                                daysSinceFirstDepositQuery,
                                transactionAmount,
                                activePaymentMethodCount,
                                userStatusIds,
                                userTagIds
                                ),
						pageRequest);

		// Get the amount field from the input values if one is not present and use it
		transactionPage.forEach(transaction -> {
			transactionAmountEnrichment(transaction);
		});
		return transactionPage;
	}

	private static Set<TransactionTagType> getTransactionTagTypes(List<String> transactionTagsNames) {
		return transactionTagsNames.stream()
				.map(TransactionTagType::fromName)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	public Page<Transaction> findByFilter(DataTableRequest request, TransactionFilterRequest filter) {
		var domainMethodProcessor = getDomainMethodProcessor(filter.getDmp());
		var domainMethod = getDomainMethod(filter.getDm());

		return findAll(domainMethodProcessor, domainMethod, filter.getGuid(),
				filter.getDomain(), filter.getTransactionType(), filter.getAllStatuses(),
				request.getSearchValue(), filter.getCreatedStart(), filter.getCreatedEnd(),
				filter.getUpdatedStart(), filter.getUpdatedEnd(),
				filter.getRegistrationStart(), filter.getRegistrationEnd(),
				filter.getProcessorReference(),
				filter.getAdditionalReference(), filter.getPaymentType(),
				filter.getDeclineReason(), filter.getLastFourDigits(), filter.getId(),
				filter.getIncludedTransactionTagsNames(), filter.getExcludedTransactionTagsNames(),
				filter.getTestAccount(), filter.getTransactionRuntimeQuery(),
                filter.getDepositCount(), filter.getDaysSinceFirstDeposit(),
                filter.getTransactionAmount(), filter.getActivePaymentMethodCount(),
                filter.getUserStatusIds(), filter.getUserTagIds(),
                request.getPageRequest()
		);
	}

	public Page<TransactionFE> findByUser(DataTableRequest request, String username, String domainName, Locale locale) {
		Order order = null;
		try {
			order = request.getPageRequest().getSort().iterator().next();
		} catch (Exception e) {
			order = new Order(Direction.DESC, "id");
		}
		String properties = order.getProperty();
		switch (properties) {
			case "date":
				properties = "createdOn";
				break;
			case "activity":
				properties = "transactionType";
				break;
			case "method":
				properties = "domainMethod.name";
				break;
			case "amountCents":
				properties = "amountCents";
				break;
			case "feeCents":
				properties = "feeCents";
				break;
			case "status":
				properties = "current.status.code";
				break;
			case "paymentType":
				properties = "paymentType";
				break;
			default:
				properties = "id";
				break;
		}
		PageRequest pageRequest = PageRequest.of(request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(), order.getDirection(), properties);
		request.setPageRequest(pageRequest);
		Page<Transaction> transactions = transactionRepository.findAll(
			TransactionSpecification.userFrontendTable(userService.findOrCreate(domainName+"/"+username), domainService.findOrCreateDomain(domainName)),
			request.getPageRequest()
		);
		return transactions.map(t -> toTransactionFE(t, locale));
	}

	public TransactionFE toTransactionFE(Transaction transaction, Locale locale) {
		Map<String, String> statusDisplayTranslations = new HashMap<>();
		String status = transaction.getCurrent().getStatus().getCode();
		String statusDisplay = getStatusDisplay(status, statusDisplayTranslations, locale);
		transactionAmountEnrichment(transaction);
		return TransactionFE.builder()
				.id(transaction.getId())
				.date(new DateTime(transaction.getCreatedOn()))
				.activity(transaction.getTransactionType().name())
				.method(transaction.getDomainMethod().getName())
				.amountCents(transaction.getAmountCents())
				.feeCents(transaction.getFeeCents())
				.paymentType(transaction.getTransactionPaymentType() != null ? transaction.getTransactionPaymentType().getPaymentType() : null)
				.status(status)
				.statusDisplay(statusDisplay)
				.errorMessage(transaction.getErrorCode() != null ? GeneralError.fromErrorCode(transaction.getErrorCode()).getResponseMessageLocal(messageSource, transaction.getDomainMethod().getDomain().getName(), locale.getLanguage()) : null)
				.build();
	}
	
	public Page<TransactionWorkflowHistory> workflow(Transaction transaction, Integer page, Integer pageSize, boolean truncate) {
		PageRequest pageRequest =  PageRequest.of(page, pageSize);
		Page<TransactionWorkflowHistory> workflow = workflowRepo.findByTransactionOrderByTimestampDesc(transaction, pageRequest);
		List<TransactionWorkflowHistory> content = workflow.getContent();
		int actualCount = content.size();
		if (truncate && actualCount == pageSize) {
			content = content.subList(0, pageSize / 2);
			pageRequest =  PageRequest.of(page, actualCount - pageSize / 2);
			List<TransactionWorkflowHistory> result = workflowRepo.findByTransactionOrderByTimestampAsc(transaction, pageRequest);
			result.addAll(content);
			Collections.sort(result, Comparator.comparing(TransactionWorkflowHistory::getTimestamp));
			return new PageImpl<>(result);
		}
		return workflow;
	}

	public boolean hasAccountingRefInWorkflow(Transaction transaction) {
		return workflowRepo.countByTransactionAndAccountingReferenceNotNull(transaction) > 0;
	}
	
	public List<LabelValue> accountingLabels(Transaction transaction) {
		List<TransactionData> data = data(transaction);
		List<LabelValue> labelsForTransaction = new ArrayList<>();
		data.stream().filter(td -> td.getField().equalsIgnoreCase("accountingFeeTranId"))
		.findFirst().ifPresent(td -> {
			try {
				labelsForTransaction.addAll(getAccountingTransactionLabelClient().findLabelsForTransaction(Long.parseLong(td.getValue())));
			} catch (Exception e) {
				log.error("Could not retrieve accounting labels", e);
			}
		});
		return labelsForTransaction;
	}
	
	public List<TransactionData> data(Transaction transaction) {
		return dataRepo.findByTransactionOrderByStage(transaction);
	}
	public List<TransactionData> dataPerStage(Transaction transaction, Integer stage) {
		return dataRepo.findByTransactionAndStageOrderByStage(transaction, stage);
	}
	
	public List<TransactionStatus> statuses() {
		List<TransactionStatus> transactionStatus = new ArrayList<>();
		statusRepo.findAll().forEach(transactionStatus::add);
		return transactionStatus;
	}

	public List<TransactionPaymentType> paymentTypes() {
		List<TransactionPaymentType> paymentTypes = new ArrayList<>();
		transactionPaymentTypeRepository.findAll().forEach(paymentTypes::add);
		log.info("paymentTypes=" + paymentTypes);
		return paymentTypes;
	}
	
	/**
	 * Gets the billing descriptor from the latest TransactionWorkflowHistory
	 * 
	 * @param transaction
	 * @return
	 */
	public Optional<String> getBillingDescriptor(Transaction transaction) {
		return workflowRepo.findFirstByTransactionAndBillingDescriptorNotLikeOrderByTimestampDesc(transaction, "")
				.map(TransactionWorkflowHistory::getBillingDescriptor);
	}

	public Response<Boolean> cancelPayoutByPlayer(LithiumTokenUtil user, Long transactionId, String comment) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		final Response<Boolean> response = machine.playerCancelPayout(user.getJwtUser().getDomainName(), transactionId, comment, user);
		return response;
	}

	public DataTableResponse<TransactionFE> findByUserAndType(String guid, DateTime dateStart, DateTime dateEnd, String type, String status, int pageSize, int page, Locale locale) {
		if (pageSize > 100) pageSize = PAGE_SIZE;
		DataTableRequest request = new DataTableRequest();
		request.setPageRequest(PageRequest.of(page, pageSize, Direction.DESC, new String[] {"id"}));

		String logMsg = ("retrieving player cashier transactions for request : g: "+guid+", ds: "+dateStart+", de: "+dateEnd+", t: "+type+", p: "+page+", ps: "+pageSize+", locale: "+locale);
		log.debug(logMsg);

		TransactionType tt = null;
		switch (type.toLowerCase()) {
			case "d":
				tt = TransactionType.DEPOSIT;
				break;
			case "w":
				tt = TransactionType.WITHDRAWAL;
				break;
		}
		
		Page<Transaction> transactions = transactionRepository.findAll(
			TransactionSpecification.userFrontendTableByType(
				userService.findOrCreate(guid),
				domainService.findOrCreateDomain(guid.split("/")[0]),
				tt, 
				!StringUtil.isEmpty(status) ? statusRepo.findByCode(status) : null,
				dateStart,
				dateEnd
			),
			request.getPageRequest()
		);

		Map<String, String> statusDisplayTranslations = new HashMap<>();
		Page<TransactionFE> transactionsFE = transactions.map(t -> {
			String statusFE = t.getCurrent().getStatus().getCode();
			String statusDisplay = getStatusDisplay(statusFE, statusDisplayTranslations, locale);
			transactionAmountEnrichment(t);
			String activity = messageSource.getMessage("SERVICE-CASHIER.TRAN." + t.getTransactionType().name() + ".LABEL", null, locale);
			return TransactionFE.builder()
				.id(t.getId())
				.date(new DateTime(t.getCreatedOn()))
				.activity(activity)
				.method(t.getDomainMethod().getName())
				.methodCode(t.getDomainMethod().getMethod().getCode())
				.amountCents(t.getAmountCents())
				.amount(CurrencyAmount.fromCentsAllowNull(t.getAmountCents()).toAmount())
				.processorReference(t.getProcessorReference())
				.processorDescription((t.getCurrent().getProcessor()!=null)?t.getCurrent().getProcessor().getDescription():"")
				.paymentType((t.getTransactionPaymentType()!=null)?t.getCurrent().getProcessor().getDescription():"")
				.feeCents(t.getFeeCents())
				.fee(CurrencyAmount.fromCentsAllowNull(t.getFeeCents()).toAmount())
				.status(statusFE)
				.statusDisplay(statusDisplay).errorMessage(t.getErrorCode() != null ? GeneralError.fromErrorCode(t.getErrorCode()).getResponseMessageLocal(messageSource, t.getDomainMethod().getDomain().getName(), locale.getLanguage()) : null)
				.build();
		});
		return new DataTableResponse<>(request, transactionsFE);
	}

	private void transactionAmountEnrichment(Transaction transaction) {
		if (transaction.getAmountCents() == null || transaction.getAmountCents() == 0L) {
			TransactionData amountDataField = dataRepo.findByTransactionAndFieldAndStageAndOutput(transaction, "amount", 1, false);
			if (amountDataField == null) {
				amountDataField = dataRepo.findByTransactionAndFieldAndStageAndOutput(transaction, "amount", 1, true);
			}

			if (amountDataField != null) {
				try {
					transaction.setAmountCents(new BigDecimal(amountDataField.getValue()).movePointRight(2).longValue());
				} catch (Exception ex) {
					log.warn("Problem parsing an input value that should be longable for cashier tran list: " + amountDataField + " on " + transaction, ex);
				}
			}
		}
	}

	private String getStatusDisplay(String status, Map<String, String> statusDisplayTranslations, Locale locale) {
		String translationKey = "SERVICE-CASHIER.TRAN." + status + ".LABEL";
		String statusDisplay = null;
		if (statusDisplayTranslations.get(translationKey) != null) {
			statusDisplay = statusDisplayTranslations.get(translationKey);
		}
		if (statusDisplay != null && !translationKey.contentEquals(statusDisplay)) {
			return statusDisplay;
		}
		statusDisplay = messageSource.getMessage(translationKey, null, locale);
		statusDisplayTranslations.put(translationKey, statusDisplay);
		if (translationKey.contentEquals(statusDisplay)) {
			// No translation available, returning status as is
			statusDisplay = status;
		}
		return statusDisplay;
	}

	public Transaction findLastTransaction(String userGuid, TransactionType type, String currentStatusCode) {
		return transactionRepository.findTop1ByUserGuidAndTransactionTypeAndStatusCodeOrderByCurrentIdDesc(
			userGuid, type, currentStatusCode);
	}

    public Transaction findFirstTransaction(String userGuid, TransactionType type, String currentStatusCode) {
        return transactionRepository.findFirstByUserGuidAndTransactionTypeAndStatusCode(userGuid, type, currentStatusCode);
    }

	public TransactionRemark addTransactionRemark(Transaction transaction, String authorGuid, String message, TransactionRemarkType remarkType) {
		lithium.service.cashier.data.entities.TransactionRemarkType remarkTypeEntity = rmTypeRepository.findByName(remarkType.getName());
		if (remarkType.equals(TransactionRemarkType.ACCOUNT_DATA)) {
			TransactionRemark transactionRemarks = transactionRemarkRepository.findTop1ByTransactionAndType(transaction, remarkTypeEntity);
			if (transactionRemarks != null) {
				return transactionRemarks;
			}
		}
		return transactionRemarkRepository.save(
			TransactionRemark.builder()
			.transaction(transaction)
			.author(userService.findOrCreate(authorGuid))
			.message(message)
			.type(remarkTypeEntity)
			.build()
		);
	}

	public TransactionRemark getTop1TransactionRemark(Transaction transaction) {
		return transactionRemarkRepository.findTop1ByTransaction(transaction);
	}

	/**
	 * We do not expect to have 100s of transaction remarks, so I am just returning a list w/o pagination.
	 * If that changes, we will need to make time to make this pageable and modify the backoffice to cater for it.
	 *
	 * @param transaction
	 * @return
	 */
	public List<TransactionRemark> getTransactionRemarks(Transaction transaction) {
		return transactionRemarkRepository.findByTransactionOrderByIdDesc(transaction);
	}

	public TransactionAmountsData calculateAmounts (String amountString, Fees fees) {
		Long amountCents = CurrencyAmount.fromAmountString(amountString).toCents();
		BigDecimal flatFee = (fees!=null&&fees.getFlatDec()!=null)?fees.getFlatDec():BigDecimal.ZERO;
		BigDecimal percentage = (fees!=null&&fees.getPercentage()!=null)?fees.getPercentage():BigDecimal.ZERO;
		BigDecimal minimumFee = (fees!=null&&fees.getMinimumDec()!=null)?fees.getMinimumDec():BigDecimal.ZERO;
		BigDecimal depositAmount = toBasicCurrencyUnit(amountCents);

		BigDecimal feeAmount = new BigDecimal(0);
		BigDecimal percentageFee = new BigDecimal(0);

		if ((percentage!=null)&&(percentage.compareTo(BigDecimal.ZERO)>0)) percentageFee = (depositAmount.multiply(percentage.movePointLeft(2))).setScale(2, BigDecimal.ROUND_HALF_UP);;
		if ((percentageFee!=null)&&(percentageFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(percentageFee);
		if ((flatFee!=null)&&(flatFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(flatFee);
		if ((minimumFee!=null)&&(minimumFee.compareTo(BigDecimal.ZERO)>0)&&(minimumFee.compareTo(feeAmount)>0)) feeAmount = minimumFee;
		return new TransactionAmountsData(amountCents, feeAmount.movePointRight(2).longValue());
	}

	public Long getSummaryPendingAmountForUser(String userGuid){
		List<Transaction> pendingTransaction = transactionRepository.findByUserGuidAndTransactionTypeAndStatusCode(userGuid, TransactionType.DEPOSIT, DoMachineState.WAITFORPROCESSOR.name());
		long summaryAmount = 0L;
		for (Transaction t: pendingTransaction){
			if (t.getAmountCents() != null) {
				summaryAmount = Long.sum(summaryAmount, t.getAmountCents());
			}
		}
		return summaryAmount;
	}

	@Transactional
	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class })
	public void saveErrorCode(Long transactionId, Integer errorCode) {
		if (nonNull(errorCode)) {
			Transaction transaction = findById(transactionId);
			transaction.setErrorCode(errorCode);
			transactionRepository.save(transaction);
		}
	}

	public Long countAllSuccessWithdrawalTransactionsByUserGuidAndDomainMethod(String userGuid, DomainMethod domainMethod) {
		return transactionRepository.countByUserGuidAndDomainMethodAndTransactionTypeAndStatusCode(userGuid, domainMethod, TransactionType.WITHDRAWAL, DoMachineState.SUCCESS.name());
	}

	public void addTransactionRemark(Long transactionId, String remark, TransactionRemarkType remarkType) {
		Transaction t = transactionRepository.findOne(transactionId);
		addTransactionRemark(t, t.getUser().getGuid(), remark, remarkType);

	}

	public long hoursSinceTransaction(String userGuid, TransactionType transactionType) {
		Transaction transaction = findFirstTransaction(userGuid, transactionType, DoMachineState.SUCCESS.name());
		long hours = 0;
		if (transaction != null) {
			hours = Hours.hoursBetween(new DateTime(transaction.getCreatedOn()), DateTime.now()).getHours();
		}
		return hours;
	}

	public LastXTransactionResponseBO findLastXByUser(Long transactionId, int count) throws Exception {
		LastXTransactionResponseBO result = new LastXTransactionResponseBO();

		Transaction currentTransaction = transactionRepository.findOne(transactionId);
		User user = userService.find(currentTransaction.getUser().getGuid());

		PageRequest pageRequest = PageRequest.of(0, count);

		Page<Transaction> lastXTransactions = transactionRepository.findByUserGuidOrderByIdDesc(currentTransaction.getUser().getGuid(), pageRequest);
		List<ShortenCashierTransactionBO> shortenTransactions = lastXTransactions.getContent().stream()
				.map(this::buildShortenTransactionBO)
				.collect(Collectors.toList());

		result.setUserId(userService.retrieveUserFromUserService(user).getId());
		result.setDomainName(currentTransaction.getUser().domainName());
		result.setLastXTransactions(shortenTransactions);

		return result;
	}

	public List<Transaction> findAllTransactions(String userGuid, TransactionType transactionType, List<String> states) {
		return transactionRepository.findByUserGuidAndTransactionTypeAndStatusCodeIn(userGuid, transactionType, states);
	}

    public List<Transaction> findAllTransactionsByType(String userGuid, TransactionType transactionType) {
        return transactionRepository.findByUserGuidAndTransactionType(userGuid, transactionType);
    }

	private ShortenCashierTransactionBO buildShortenTransactionBO(Transaction transaction) {

		String description = Optional.ofNullable(transaction.getPaymentMethod())
				.map(ProcessorUserCard::getLastFourDigits)
				.filter(descriptor -> !descriptor.isEmpty())
				.orElse("N/A");

		return ShortenCashierTransactionBO.builder()
				.createdOn(transaction.getCreatedOn())
				.amount(transaction.getAmountCents() != null ? BigDecimal.valueOf(transaction.getAmountCents()).movePointLeft(2) : null)
				.transactionType(transaction.getTransactionType().name())
				.processor(transaction.getDomainMethod().getMethod().getName())
				.descriptor(description)
				.status(transaction.getCurrent().getStatus().getCode())
				.currencyCode(transaction.getCurrencyCode())
				.build();
	}

	public void setupFromEnum() {
		Arrays.stream(TransactionRemarkType.values()).forEach(remarkType ->
				rmTypeRepository.findOrCreateByName(remarkType.name(),
						() -> lithium.service.cashier.data.entities.TransactionRemarkType.builder().build()));
		Arrays.stream(TransactionTagType.values()).forEach(tagType ->
				tagTypeRepository.findById(tagType.getId())
                        .orElseGet(() -> tagTypeRepository.save(new TransactionTagTypeInfo(tagType.getId(), tagType.getName()))));
	}

	public boolean hasEnoughBalance(String domainName, String guid, String currencyCode, Long transactionId, boolean isWithdrawalFundsReserved) {
		String amountString = getDataByTransactionId(transactionId, "amount", 1, false);
		Long amountCents = CurrencyAmount.fromAmountString(amountString).toCents();
		Long customerBalance = cashierService.getCustomerBalance(currencyCode,	domainName, guid);

		if (customerBalance < 0) {
			return false;
		}

		if (isWithdrawalFundsReserved) {
			customerBalance = cashierService.getCustomerBalance(currencyCode, domainName, guid, "PLAYER_BALANCE_PENDING_WITHDRAWAL", "PLAYER_BALANCE");
		}

		if (customerBalance < amountCents) {
			return false;
		}
		return true;
	}

	public List<Transaction> getUsersTransactionsByCodes(String guid, List<String> pengingStatusCodes) {
		User user = userService.findOrCreate(guid);
		return transactionRepository.findByUserAndStatusCodeIn(user, pengingStatusCodes);
	}

    public long getSummarySuccessDepositAmountForUserByPaymentMethod(String guid, ProcessorUserCard paymentMethod) {
        if (paymentMethod == null) { return 0l; }
        List<Transaction> depositTransactions =  transactionRepository.findByUserGuidAndTransactionTypeAndStatusCodeAndPaymentMethodId(guid, TransactionType.DEPOSIT, DoMachineState.SUCCESS.name(), paymentMethod.getId());
        return depositTransactions.stream()
                .map(Transaction::getAmountCents)
                .reduce(0l, Long :: sum);
    }

	public DomainMethod getDomainMethod(String dmId) {
		if (StringUtil.isEmpty(dmId)) return null;
		return getDomainMethod(Long.parseLong(dmId));
	}

	private DomainMethod getDomainMethod(long domainMethodId) {
		return domainMethodRepository.findOne(domainMethodId);
	}

	public DomainMethodProcessor getDomainMethodProcessor(String dmpId) {
		if (StringUtil.isEmpty(dmpId) || Long.parseLong(dmpId) < 0) return null;
		return getDomainMethodProcessor(Long.parseLong(dmpId));
	}

    private DomainMethodProcessor getDomainMethodProcessor(long domainMethodProcessorId) {
        return domainMethodProcessorRepository.findOne(domainMethodProcessorId);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findOne(id);
    }

    public void addTransactionTagByName(Long transactionId, String tagName) {
        TransactionTagType tagType = TransactionTagType.fromNameThrowable(tagName);
        Transaction transaction = getTransactionById(transactionId);
        addTagForTransaction(transaction, tagType);
    }

	public void addTagForTransaction(Transaction transaction, TransactionTagType tagType) {
		TransactionTag transactionTag = transactionTagRepository.findByTransactionAndType(transaction, tagType)
				.orElseGet(() -> transactionTagRepository.save(TransactionTag.builder()
						.transaction(transaction)
						.type(tagType)
						.build()));
		transaction.getTags().add(transactionTag);
	}

    public void removeTransactionTagByName(Long transactionId, String tagName) {
        TransactionTagType tagType = TransactionTagType.fromNameThrowable(tagName);
        Transaction transaction = getTransactionById(transactionId);
        transactionTagRepository.findByTransactionAndType(transaction, tagType)
                .ifPresent(transactionTag -> {
                    transaction.getTags().remove(transactionTag);
                    transactionRepository.save(transaction);
                    transactionTagRepository.delete(transactionTag);
                });
    }

    public boolean isFirstSuccessTransaction(Long transactionId, String userGuid, TransactionType transactionType) {
		Transaction userFirstSuccessTransaction = findFirstTransaction(userGuid, transactionType, DoMachineState.SUCCESS.name());
		return userFirstSuccessTransaction != null && userFirstSuccessTransaction.getId().equals(transactionId);
	}

    public long getCountByUserAndTransactionTypeAndStatusCode(String userGuid, TransactionType transactionType, Optional<String> statusCode) {
        return statusCode.map(DoMachineState::valueOf)
                .map(DoMachineState::name)
                .map(st -> transactionRepository.countByUserGuidAndTransactionTypeAndStatusCode(userGuid, transactionType, st))
                .orElse(transactionRepository.countByUserGuidAndTransactionType(userGuid, transactionType));
    }

    public List<ProcessorAccountDetails> updatePaymentMethodsCreationDate(String userGuid, List<ProcessorAccountDetails> userPaymentMethods) {
        userPaymentMethods.forEach(populateCreatedOn(userGuid));
        return userPaymentMethods;
    }

    private Consumer<ProcessorAccountDetails> populateCreatedOn(String userGuid) {
        return processorAccount ->transactionRepository.findFirstByUserGuidAndPaymentMethodIdOrderByCreatedOn(userGuid, processorAccount.getId())
                .map(Transaction::getCreatedOn)
                .map(DATE_FORMAT::format)
                .ifPresent(processorAccount::setCreatedOn);
    }


	public Response<AdjustmentTransaction> reverse(long transactionId, String adjustmentAccountCode, String comment, LithiumTokenUtil token)
			throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException {
		Transaction transaction = findById(transactionId);
		ManualCashierAdjustmentAccountCode accountCode = ManualCashierAdjustmentAccountCode.fromCode(adjustmentAccountCode);
		validateAdjustBalance(transaction, accountCode);
		String ownerGuid = transaction.getUser().guid();
		String authorGuid = token.guid();
		String domainName = transaction.getDomainMethod().getDomain().getName();
		Long amountCents = accountCode.isDebit() ? (transaction.getAmountCents() * -1) : transaction.getAmountCents();

		Response<AdjustmentTransaction> adjustmentTransaction = accountingClientService.adjustMultiV2(AdjustMultiRequest.builder()
				.amountCents(amountCents)
				.date(new DateTime().toDateTimeISO())
				.accountCode(AccountType.PLAYER_BALANCE.getCode())
				.accountTypeCode(AccountType.PLAYER_BALANCE.getCode())
				.contraAccountCode(accountCode.getCode())
				.contraAccountTypeCode(AccountType.MANUAL_BALANCE_ADJUST.getCode())
				.currencyCode(transaction.getCurrencyCode())
				.labels(new String[]{CashierTransactionLabels.COMMENT_LABEL + "=" + comment, CashierTransactionLabels.TRAN_ID_LABEL + "=" + transaction.getId()})
				.domainName(domainName)
				.ownerGuid(ownerGuid)
				.authorGuid(authorGuid)
				.transactionTypeCode(CashierTranType.MANUAL_CASHIER_ADJUST.value())
				.allowNegativeAdjust(false)
				.build());

		if (adjustmentTransaction.isSuccessful()) {
			transaction.setManualCashierAdjustmentId(adjustmentTransaction.getData().getTransactionId());
			transactionRepository.save(transaction);
			logReverseChanges(comment, token, transaction, accountCode, ownerGuid, authorGuid, domainName, amountCents);
		}
		return adjustmentTransaction;
	}

	private void logReverseChanges(String comment, LithiumTokenUtil token, Transaction transaction, ManualCashierAdjustmentAccountCode accountCode, String ownerGuid, String authorGuid, String domainName, Long amountCents) {
		try {
			String noteComment = messageSource.getMessage("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.COMMENT", new Object[]{String.valueOf(transaction.getId()), transaction.getCurrencyCode(), toBasicCurrencyUnit(amountCents), accountCode.getCode(), comment}, Locale.US);
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(ownerGuid);

			changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), authorGuid, token, noteComment, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domainName);
		} catch (UserClientServiceFactoryException e) {
			log.error("Can't store changelog for cashier balance adjust for user (" + ownerGuid + ") status due to internal server error: " + e.getMessage(), e);
		} catch (Exception e) {
			log.error("ChangeLogService could not add notes  ", e);
		}
	}

	private void validateAdjustBalance(Transaction transaction, ManualCashierAdjustmentAccountCode adjustmentAccountCode) {
		if (transaction == null) {
			log.error("Transaction not found.");
			throw new IllegalArgumentException("Transaction not found.");
		}
		if (transaction.getStatus().getActive()) {
			log.error("Transaction " + transaction.getId() + " is in an active status. Adjustment can't be performed");
			throw new IllegalArgumentException("Transaction is in an active status. Adjustment can't be performed");
		}
		if (transaction.getManualCashierAdjustmentId() != null) {
			log.error("Transaction " + transaction.getId() + " was already adjusted with manual cashier adjustment id: " + transaction.getManualCashierAdjustmentId());
			throw new IllegalArgumentException("Transaction was already adjusted.");
		}
		if (!ManualCashierAdjustmentAccountCode.isValidForTransaction(adjustmentAccountCode, transaction)) {
			log.error("Account code " + adjustmentAccountCode + " is not valid for this transaction.");
			throw new IllegalArgumentException("Account code is not valid for this transaction.");
		}
	}

	private static BigDecimal toBasicCurrencyUnit(Long amountCents) {
		return new BigDecimal(amountCents).movePointLeft(2);
	}

    public Page<Transaction> findByUserGuidAndTransactionTypeAndStatusCodeInOrderByIdDesc(String userGuid, TransactionType transactionType, List<String> statusCodes, PageRequest pageRequest) {
        return transactionRepository.findByUserGuidAndTransactionTypeAndStatusCodeInOrderByIdDesc(userGuid, transactionType, statusCodes, pageRequest);
    }
}
