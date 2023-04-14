package lithium.service.cashier.machine;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status461DepositRestrictedException;
import lithium.exceptions.Status462WithdrawRestrictedException;
import lithium.math.CurrencyAmount;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.Tag;
import lithium.service.Response;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.frontend.UserRequest;
import lithium.service.cashier.client.internal.DoProcessorClient;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.internal.InitialValidateClient;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.ProcessorCommunicationType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.config.ServiceCashierConfigurationProperties;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.MethodStageField;
import lithium.service.cashier.data.entities.ProcessorProperty;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionAmountsData;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionProcessingAttempt;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.objects.AutoApproveResult;
import lithium.service.cashier.data.objects.ManualTransactionFieldValue;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.exceptions.Status407TransactionInFinalStateException;
import lithium.service.cashier.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.cashier.exceptions.TransactionUniqueProcessorReferenceException;
import lithium.service.cashier.services.AccessRuleService;
import lithium.service.cashier.services.CashierFrontendService;
import lithium.service.cashier.services.CashierMailService;
import lithium.service.cashier.services.CashierNotificationService;
import lithium.service.cashier.services.CashierService;
import lithium.service.cashier.services.CashierSmsService;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.MethodStageFieldService;
import lithium.service.cashier.services.MethodStageService;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.ProcessorAccountServiceOld;
import lithium.service.cashier.services.ProcessorAccountVerificationService;
import lithium.service.cashier.services.ProductService;
import lithium.service.cashier.services.PubSubWalletTransactionService;
import lithium.service.cashier.services.ReferralService;
import lithium.service.cashier.services.TransactionProcessingAttemptService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.TranslationService;
import lithium.service.cashier.services.UserService;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalService;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.BonusRevision;
import lithium.service.casino.client.data.CasinoBonus;
import lithium.service.casino.client.data.CasinoBonusCheck;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.DepositLimitClient;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status477BalanceLimitReachedException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status486DailyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status487WeeklyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status488MonthlyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.enums.Type;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static lithium.service.cashier.client.frontend.DoMachineState.DECLINED;
import static lithium.service.cashier.client.frontend.DoMachineState.PENDING_CANCEL;
import static lithium.service.cashier.client.frontend.DoMachineState.SUCCESS;
import static lithium.service.cashier.client.objects.PaymentMethodStatusType.isActiveAccountStatus;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_ACCESS_RESTRICTED;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_ACCESS_RULE_CHECK_FAILED;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_API_TRANSACTION_DECLINED;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_APPROVE_ERROR;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_INSUFFICIENT_BALANCE;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_INVALID_ACCOUNT;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_MAX_ACCOUNT_COUNT;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_NOT_ACCEPTABLE_AMOUNT_DIFFERENCE;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_PAYMENT_METHOD_UNAVAILABLE;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_PROCESSOR_CALLBACK_DECLINED;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getDepositCheckLimitMessage;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getError;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getInvalidTransactionAmountMessage;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getPlayTimeCheckLimitMessage;
import static lithium.service.cashier.client.objects.enums.TransactionTagType.FIRST_DEPOSIT;
import static lithium.service.cashier.client.objects.enums.TransactionTagType.FIRST_WITHDRAWAL;
import static lithium.service.cashier.client.objects.enums.TransactionTagType.WD_ON_BALANCE_LIMIT_RICHED;
import static lithium.service.user.client.objects.User.SYSTEM_GUID;

/**
 * The Deposit / Withdrawal Machine
 * @author johantheitguy
 *
 * The DoMachine is a state class. It is not a singleton. It gets created on every request for every user and destroyed after every response.
 * It is used so that the very many functions that relate to the request can easily get access to the context of the request, without relying on every
 * step to pass every variable related to the context between stateless methods. For this reason, you cannot autowire a DoMachine where you need it.
 * The @Scope("prototype") annotation means you will have to request an instance explicitely from the Spring context so that Spring will create you
 * a new one every time, and then you have to pass it to whomever needs it.
 */

@Slf4j
@Component
@Scope("prototype")
public class DoMachine {
	private final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance";
	@Autowired AutoRestrictionTriggerStream autoRestrictionTriggerStream;
	@Autowired AutoWithdrawalService autoWithdrawalService;
	@Autowired TransactionService service;
	@Autowired UserService userService;
	@Autowired CashierFrontendService frontendService;
	@Autowired MethodStageService stageService;
	@Autowired MethodStageFieldService stageFieldService;
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired DomainMethodService dmService;
	@Autowired DomainMethodProcessorService dmpService;
	@Autowired CashierService cashierService;
	@Autowired CashierMailService cashierMailService;
	@Autowired CashierSmsService cashierSmsService;
	@Autowired ServiceCashierConfigurationProperties properties;
	@Autowired MessageSource messageSource;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired ReferralService referralService;
	@Autowired ProductService productService;
	@Autowired ModelMapper mapper;
	@Autowired private AccessRuleService accessRuleService;
	@Autowired CashierNotificationService cashierNotificationService;
	@Autowired StatsStream stats;
	@Autowired LimitInternalSystemService limitInternalSystemService;
	@Autowired PubSubWalletTransactionService pubSubWalletTransactionService;
	@Autowired UserApiInternalClientService userApiInternalClientService;
	@Autowired ProcessorAccountService processorAccountService;
	@Autowired ProcessorAccountVerificationService processorAccountVerifyService;
	@Autowired ProcessorAccountServiceOld processorAccountServiceOld;
	@Autowired LithiumMetricsService lithiumMetricsService;
	@Autowired CashierFrontendService cashierFrontendService;
	@Autowired TransactionService transactionService;
	@Autowired
	private TranslationService translationService;
	@Autowired
	private TransactionProcessingAttemptService transactionProcessingAttemptService;
	@Autowired ChangeLogService changeLogService;

	private DoMachineContext context;
	private DoMachineContext reversalContext;

	@Value("${lithium.service.cashier.machine-apply-access-rule-block:false}")
	private boolean applyAccessRuleBlock;

	@Value("${lithium.service.cashier.allow-multiple-withdrawals:false}")
	private boolean allowMultipleWithdrawals;

	@Autowired
	private PostProcessorService postProcessorService;

	static private int DECLINE_REASON_METRIC_SIZE = 20;

	public DoResponse run(String methodCode, LithiumTokenUtil token, DoRequest request, String type, HttpServletRequest httpServletRequest)
			throws NoMethodWithCodeException, MoreThanOneMethodWithCodeException {
		TransactionType tt = TransactionType.fromDescription(type);
		DomainMethod dm = dmService.findOneEnabledByCode(token.domainName(), methodCode, tt.equals(TransactionType.DEPOSIT));

		User user = userService.findOrCreate(token.guid());
		String ipAddr = (httpServletRequest.getHeader("X-Forwarded-For") != null) ? httpServletRequest.getHeader("X-Forwarded-For") : httpServletRequest.getRemoteAddr();
		boolean isFirstDeposit = tt.equals(TransactionType.DEPOSIT) && transactionService.findFirstTransaction(user.getGuid(), TransactionType.DEPOSIT, DoMachineState.SUCCESS.name()) == null;

		if (!cashierFrontendService.checkDomainMethodUserAndProfile(dm, user, ipAddr, httpServletRequest.getHeader("user-agent"), isFirstDeposit)) {
			log.error("Domain method: " + methodCode + " is not allowed for user: " + user.getGuid());
			throw new NoMethodWithCodeException();
		}
		return run(dm, token, request, type, httpServletRequest, isFirstDeposit);
	}

	public DoResponse run(DomainMethod domainMethod, LithiumTokenUtil token, DoRequest request, String type, HttpServletRequest httpServletRequest, boolean isFirstDeposit) {
		if (token == null) {
			DoResponse response = new DoResponse();
			response.setError(true);
			response.setErrorMessage("Invalid token");
			response.setDeclineReason("Invalid token");

			log.error("Can't run transaction due token is null request = " + request);
			return response;
		}
		return run(domainMethod, request, type, token.sessionId(), token.guid(), null, false,
				httpServletRequest.getRemoteAddr(),
				Collections.list(httpServletRequest.getHeaderNames())
						.stream()
						.collect(Collectors.toMap(header -> header, httpServletRequest::getHeader)), isFirstDeposit, null);
	}

	public DoResponse runDirectWithdraw(DomainMethod domainMethod, DoRequest request, Long sessionId, String userGuid, String initiationAuthorGuid, boolean balanceLimitEscrow, String ip, Map<String, String> headers, Long linkedTransactionId) {
		return run(domainMethod, request, "direct_withdraw", sessionId , userGuid, initiationAuthorGuid, balanceLimitEscrow, ip, headers, false, linkedTransactionId);
	}

	private DoResponse run(DomainMethod domainMethod, DoRequest request, String type, Long sessionId, String userGuid, String initiationAuthorGuid, boolean balanceLimitEscrow, String ip, Map<String, String> headers, boolean isFirstDeposit, Long linkedTransactionId) {
		try {
			createContext(domainMethod, sessionId, userGuid, initiationAuthorGuid, request, type, balanceLimitEscrow, ip, headers, isFirstDeposit, linkedTransactionId);
			if (context.isDirectWithdraw()) {
				context.setSource("Backoffice");
			} else {
				context.setSource("Customer");
			}
			return processState();
		} catch (Throwable t) {
			return handleProcessTransactionException(t);
		}
	}

	public DoResponse doManualTransaction(
			lithium.service.user.client.objects.User user,
			Domain domain,
			TransactionType tranType,
			CurrencyAmount amount,
			lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor dmp,
			List<ManualTransactionFieldValue> fields,
			String processorReference,
			Long bonusId,
			LithiumTokenUtil token
	) throws Exception {
		Transaction transaction = null;
		try {
			transaction = startTransaction(
					token.sessionId(), // Admin session
					tranType,
					dmService.find(dmp.getDomainMethod().getId()),
					null,
					domain.getCurrency(),
					user.guid(),
					"Admin/Backoffice Initiated.",
					dmpService.find(dmp.getId()),
					null,
					null,
					null
			);

			service.setData(transaction, "amount", amount.toAmount().toPlainString(), 1, false);
			if (tranType.equals(TransactionType.DEPOSIT)) {
				service.setData(transaction, "bonusId", (bonusId != null) ? bonusId.toString() : "", 1, false);
			}
			service.setData(transaction, "processorReference", processorReference, 2, true);


			// Fields things...
			for (ManualTransactionFieldValue mtfv : fields) {
				if (mtfv.getValue() != null && !mtfv.getValue().isEmpty()) {
					service.setData(transaction, mtfv.getKey(), mtfv.getValue(), mtfv.getStage(), !mtfv.isInput());

					if (tranType.equals(TransactionType.DEPOSIT)) {
						switch (mtfv.getKey()) {
							case "ccnumber":
								service.setData(transaction, "account_info", mtfv.getValue(), 1, true);
								break;
							case "control_number":
								service.setData(transaction, "account_info", mtfv.getValue(), 2, true);
								break;
							case "address":
								service.setData(transaction, "account_info", mtfv.getValue(), 1, true);
								break;
							case "sender":
								service.setData(transaction, "account_info", mtfv.getValue(), 1, true);
								break;
							case "receiver_account":
								service.setData(transaction, "account_info", mtfv.getValue(), 1, true);
								break;
							default:
								;
						}
					}
				}
			}

			String processorResponse = (tranType.equals(TransactionType.DEPOSIT))
					? "Manual Deposit Transaction"
					: "Manual Withdrawal Transaction";

			createAdminContextWithSource(transaction.getId(), token, domain.getName(), "Admin/Backoffice Initiated.");
			context.setSessionId(token.sessionId());
			context.getTransaction().setProcessorReference(processorReference);
			context.getTransaction().setManual(true);
			context.getTransaction().setAccountInfo(getAccountInfo());
			service.update(context.getTransaction());
			context.setProcessor(dmpService.find(dmp.getId()));
			context.setUser(transaction.getUser());
			context.setProcessorUser(userService.findProcessorUser(context.getUser(), context.getProcessor()));
			context.setProcessorResponse(DoProcessorResponse.builder().message(processorResponse).build());
			DoResponse response = finish(DoMachineState.SUCCESS);
			return response;
		}  catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse doAPITransaction(
			long domainMethodProcessorId,
			long amountCents,
			String guid,
			String processorReference,
			String additionalReference,
			String currencyCode,
			Long sessionId,
			boolean success,
			String paymentType
	) throws Exception {
		String transaction = "doAPITransaction context [domainMethodProcessorId=" + domainMethodProcessorId + ", guid=" + guid +
				", currencyCode=" + currencyCode + ", amountCents=" + amountCents +
				", processorReference=" + processorReference + ", additionalReference=" + additionalReference +
				", sessionId=" + sessionId + ", success=" + success + ", playerPaymentType=" + paymentType + "]";

		log.info("Starting " + transaction);

		DomainMethodProcessor dmp = dmpService.find(domainMethodProcessorId);

		if (processorReference == null) {
			return DoResponse.builder()
					.error(true)
					.errorMessage("Processor reference should be defined.")
					.build();
		}

		try {
			Transaction t = service.startTransactionWithUniqueProcessorReference(sessionId, TransactionType.DEPOSIT,
					dmp.getDomainMethod(), amountCents, currencyCode, guid, "API", processorReference, getTtl(dmp));
			t = service.updatePaymentTypeAndAdditionalReference(t, paymentType, additionalReference);
			pubSubWalletTransactionService.buildAndSendWalletTransactionMessage(t, null);
			service.setData(t, "amount", CurrencyAmount.fromCents(amountCents).toAmount().toPlainString(), 1, false);
			service.setData(t, "processorReference", processorReference, 2, true);
			createContext(t, guid);

			context.setSessionId(sessionId);
			context.setProcessor(dmp);
			DoResponse response;
			if (success) {
				response = finish(SUCCESS);
			} else {
				context.getTransaction().setDeclineReason(getError(CASHIER_API_TRANSACTION_DECLINED));
				response = finish(DECLINED);
			}
			log.info("Finished doAPITransaction " + transaction + " : " + context);
			return response;

		} catch (TransactionUniqueProcessorReferenceException e) {
			return DoResponse.builder()
					.error(true)
					.errorMessage("A transaction with this reference already exists.")
					.build();
		} catch (Exception e) {
			log.error("Transaction ref=" + processorReference + " user=[" + guid + "] got error :" + e.getMessage(), e);
			if (context != null) {
				finish(DoMachineState.FATALERROR);
			}
			return DoResponse.builder()
					.error(true)
					.errorMessage(e.getMessage())
					.build();
		}
	}

	public DoResponse markSuccess(String domain, Long transactionId, BigDecimal amount, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");
			//TODO: Chat about which status is allowed to move onto success
			//if (!context.getTransaction().getCurrent().getStatus().isWaitForProcessor()) throw new Exception("This transaction is not awaiting a processor response");
			context.setState(DoMachineState.SUCCESS);
			Transaction transaction = service.markForcedSuccess(context.getTransaction());
			context.setTransaction(transaction);
			if (amount != null) {
				CurrencyAmount originalAmount = CurrencyAmount.fromAmountString(service.getData(context.getTransaction(), "amount", 1, false));
				CurrencyAmount overriddenAmount = CurrencyAmount.fromAmount(amount);

				// Forced success amount differs from original transaction amount
				if (originalAmount.toCents().compareTo(overriddenAmount.toCents()) != 0) {
					if (context.getTransaction().getTransactionType().equals(TransactionType.WITHDRAWAL)) {
						// Withdrawal funds were reserved in PLAYER_BALANCE_PENDING_WITHDRAWAL and have not been transferred back to PLAYER_BALANCE.
						// This would most likely already be done, unless the transaction is forced successful without there even being a failure
						// in processing.
						if (context.isWithdrawalFundsReserved() && context.getTransaction().getAccRefFromWithdrawalPending() == null) {
							log.warn("Forced success on withdrawal tran with amount overridden. Pending funds about"
									+ " to be transferred back into main balance [context=" + context + "]");
							reverseReserveWithdrawalFunds();
						}
					}
					service.setData(context.getTransaction(), "amount", overriddenAmount.toAmount().toString(), 1, false);
					String amountAlteredNote = "***Amount altered on forced success from " +
							originalAmount.toAmount().toPlainString() + " to "
							+ overriddenAmount.toAmount().toPlainString() + " by " + token.guid();
					service.addComment(
							context.getTransaction(),
							userService.findOrCreate(token.guid()),
							comment += "\r\n\r\n" + amountAlteredNote
					);
				}
			}
			setTransactionReviewedBy(context, token);
			DoResponse response = finish(DoMachineState.SUCCESS);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			return response;
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse retry(String domain, Long transactionId, LithiumTokenUtil token) throws Exception {
		return retry(domain, transactionId, token, "Admin/Backoffice Initiated.");
	}
	public DoResponse retry(String domain, Long transactionId, LithiumTokenUtil token, final String source) throws Exception {
		try {
			createAdminContext(transactionId, token, domain);
			context.setSource(source);
			loadMethodStageFromDatabase();
			return processState();
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse processTransaction(String domain, Long transactionId, LithiumTokenUtil token, final String source) {
		try {
			createAdminContext(transactionId, token, domain);
			context.setSource(source);
			loadMethodStageFromDatabase();
			return processState();
		} catch (Throwable t) {
			return handleProcessTransactionException(t);
		}
	}

	public DoResponse retryOnExpire(Long transactionId) throws Exception {
		createContextFromTransactionId(transactionId, SYSTEM_GUID);
		context.setSource("State check before expiration");
		context.setTransactionExpired(true);
		loadMethodStageFromDatabase();
		return processState();
	}

	public DoResponse cancel(Long transactionId, String comment) throws Exception {
		try {
			createContextFromTransactionId(transactionId, SYSTEM_GUID);
			context.setSource(comment);
            if (isInFinalState(context)) {
                return transactionInFinalStateResponse();
            }
			Transaction transaction = context.getTransaction();
			transaction.setTtl(-1L);
			if (TransactionType.WITHDRAWAL.equals(transaction.getTransactionType()) && isNull(transaction.getAccRefToWithdrawalPending())) {
				log.info("The 'acc_ref_to_withdrawal_pending' of transaction " + transactionId + " is null, trying to retrieve from accounting-service");
				CashierTranType transactionTypeCode = context.isBalanceLimitEscrow() ? CashierTranType.TRANSFER_FROM_BALANCE_LIMIT_ESCROW : CashierTranType.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL;
				Long accountingTransactionId = cashierService.getRelatedAccountingTransactionId(transactionId, transactionTypeCode.toString());
				if (nonNull(accountingTransactionId)) {
					log.info("Updated 'acc_ref_to_withdrawal_pending' of transaction " + transactionId + " with " + accountingTransactionId);
					transaction.setAccRefToWithdrawalPending(accountingTransactionId);
				}
			}
            context.getTransaction().setDeclineReason(comment);
			finish(DoMachineState.CANCEL);
			context.setState(DoMachineState.CANCEL);
			DoResponse response = context.getResponse();
			return response;
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse cancel(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");
			TransactionStatus status = context.getTransaction().getCurrent().getStatus();
			if (status.isApproved() || status.isSuccess() || status.isDeclined() || status.isFatalError()) {
				throw new DoErrorException("The transaction could not be cancelled because it is not in a cancellable state.");
			}
			setTransactionReviewedBy(context, token);
			context.getTransaction().setDeclineReason(comment);
			DoResponse response = finish(DoMachineState.CANCEL);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			return response;
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse onHold(String domain, Long transactionId, String remark, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");
			TransactionStatus status = context.getTransaction().getCurrent().getStatus();
			if (!status.isAbleToHold()) {
				throw new DoErrorException("This transaction is not awaiting approval.");
			}
			DoResponse response = finish(DoMachineState.ON_HOLD);
			transactionService.addTransactionRemark(context.getTransaction(), token.guid(), remark, TransactionRemarkType.OPERATOR);
			registerOutcomeActionChangeLog("place", "Transaction " +context.getTransaction().getId()+ " moved to ON_HOLD state", token);
			return response;
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse reprocessOnHold(String domain, Long transactionId, String remark, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain,"Admin/Backoffice Initiated.");
			Transaction transaction = context.getTransaction();
			if (!transaction.getCurrent().getStatus().isOnHold()) {
				throw new DoErrorException("This transaction is not on hold");
			}
			DoResponse response =  finish(DoMachineState.ON_HOLD_REPROCESS, 1l);
			transactionService.addTransactionRemark(context.getTransaction(), token.guid(), remark, TransactionRemarkType.OPERATOR);
			registerOutcomeActionChangeLog("lift", "Reprocess transaction " +context.getTransaction().getId()+ " from ON_HOLD state", token);
			return response;
		}  catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

    public DoResponse processDelayedApprove(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
        try {
            createAdminContextWithSource(transactionId, token, domain,"Admin/Backoffice Initiated.");
            Transaction transaction = context.getTransaction();
            if (!transaction.getCurrent().getStatus().isAbleToApprove()) {
                throw new DoErrorException("This transaction is not waiting for approve");
            }
            DoResponse response =  finish(DoMachineState.APPROVED_DELAYED, 1l);
            transactionService.addTransactionRemark(context.getTransaction(), token.guid(), comment, TransactionRemarkType.OPERATOR);
            return response;
        }  catch (DoErrorException exception) {
            DoResponse response = getResponse();
            response.setError(true);
            response.setErrorMessage(exception.getMessage());
            return context.getResponse();
        }
    }

    private void registerOutcomeActionChangeLog(String type, String comment , LithiumTokenUtil token){
        String userGuid = context.getTransaction().getUser().guid();
        try {
            lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(userGuid);
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.restriction", type, user.getId(), token.guid(), token,
                    comment, null, null, Category.ACCOUNT, SubCategory.RESTRICTION, 40, userGuid.substring(0, userGuid.indexOf('/')));
        } catch (Throwable ex) {
            log.error("Cant register outcome action note for player guid=" + userGuid, ex);
        }
    }

	private Long calculateTtlAfterHold(Transaction transaction) {
		long createdTime = transaction.getCreatedOn().getTime();
		long ttl = Optional.ofNullable(transaction.getTtl()).orElse(0l);
		return System.currentTimeMillis() - createdTime + ttl;
	}

	public Response<Boolean> playerCancelPayout(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Player Initiated.");
			if (context.getState() != DoMachineState.WAITFORAPPROVAL && context.getState() != DoMachineState.ON_HOLD
                    && context.getState() != DoMachineState.AUTO_APPROVED_DELAYED
                    && context.getState() != DoMachineState.APPROVED_DELAYED
            ) {
				// TODO: 2020/03/31 Perform translation on the message
				throw new DoErrorException("The transaction could not be cancelled because it is not in a cancellable state.");
			}
			DoResponse response = finish(DoMachineState.PLAYER_CANCEL);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			return Response.<Boolean>builder().status(Response.Status.OK).data(true).build();
		} catch (Status407TransactionInFinalStateException e) {
			String msg = "Transaction is already in a final state";
			log.warn(msg + " [context="+context+"]");
			return Response.<Boolean>builder().status(Response.Status.OK).data(false).message(msg).build();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return Response.<Boolean>builder().status(Response.Status.OK).data(false).message(exception.getMessage()).build();
		}
	}

	public DoResponse clearProvider(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");
			List<DomainMethodProcessor> processors = frontendService.domainMethodProcessors(context.getDomainMethod().getId(), context.getUser(), context.getExternalUser().getLastLogin().getIpAddress(), context.getExternalUser().getLastLogin().getUserAgent());
			if (processors.isEmpty()) throw new DoErrorException("No processors for this method");
			DomainMethodProcessor p = processors.get(0);
			context.setProcessor(p);

			DoResponse response = finish(DoMachineState.VALIDATEINPUT);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			return response;
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse approve(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");

			if (!context.getTransaction().getStatus().isAbleToApprove()) {
				context.getResponse().setError(true);
				context.getResponse().setErrorMessage("This transaction is not awaiting approval");
				return context.getResponse();
			}

			if (!hasEnoughBalance(context.isWithdrawalFundsReserved())) {
				return context.getResponse();
			}

			setTransactionReviewedBy(context, token);
			finish(DoMachineState.APPROVED);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			context.setState(DoMachineState.WAITFORPROCESSOR);
			registerStat(Type.CASHIER, Event.MANUAL_APPROVED_WITHDRAWAL,
					context.getExternalUser().getDomain().getName(), context.getExternalUser().guid());
			return processState();
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(exception.getMessage());
			context.getResponse().setDeclineReason(getError(CASHIER_APPROVE_ERROR));
			return context.getResponse();
		}
	}

	public DoResponse autoApprove(AutoWithdrawalRuleSet ruleset, Long transactionId, String authorGuid, Date queuedOn)
			throws Exception {
		try {
			createContextFromTransactionId(transactionId, authorGuid);
			context.setSource("Auto-Approval Process [" + authorGuid + " queued "
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm").format(queuedOn) + "]");
			if (!context.getTransaction().getCurrent().getStatus().isWaitForApproval())
				throw new Exception("This transaction is not awaiting approval");

			AutoApproveResult autoApprove = autoApprove(ruleset);
			if (autoApprove.isApproved()) {
				return processAutoApproval(autoApprove.getTrace(), autoApprove.getProcessDelay());
			}
			return context.getResponse();
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	public DoResponse expire(Long transactionId) throws Exception {
		return expire(transactionId, "Admin/Backoffice Initiated.");
	}

	public DoResponse expire(Long transactionId, String source) throws Exception {
		try {
			retryOnExpire(transactionId);
			if (isInFinalState(context)) throw new Status407TransactionInFinalStateException();
			createContextFromTransactionId(transactionId, SYSTEM_GUID);
			context.setSource(source);
//			if (!context.getTransaction().getCurrent().getStatus().isWaitForApproval()) throw new Exception("This transaction is not awaiting approval");
			context.getTransaction().setTtl(-1L);
			finish(DoMachineState.EXPIRED);
			context.setState(DoMachineState.EXPIRED);
			return context.getResponse();
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	private boolean isInFinalState(DoMachineContext context) {
		return nonNull(context.getTransaction()) && nonNull(context.getTransaction().getCurrent())
				&& finalStateCodes().contains(context.getTransaction().getCurrent().getStatus().getCode());
	}

	public DoResponse reject(String domain, Long transactionId, String comment, LithiumTokenUtil token) throws Exception {
		try {
			createAdminContextWithSource(transactionId, token, domain, "Admin/Backoffice Initiated.");
			if (!context.getTransaction().getCurrent().getStatus().isWaitForApproval() && !context.getTransaction().getCurrent().getStatus().isOnHold()) throw new Exception("This transaction is not awaiting approval");

			DoResponse response = finish(DoMachineState.REJECTED);
			service.addComment(context.getTransaction(), userService.findOrCreate(token.guid()), comment);
			return response;
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		} catch (DoErrorException exception) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(exception.getMessage());
			return context.getResponse();
		}
	}

	private DoResponse transactionInFinalStateResponse() {
		log.warn("Transaction is already in a final state [context="+context+"]");
		try {
			createContextFromTransactionId(context.getTransaction().getId(), SYSTEM_GUID);
		} catch (Exception ex1) {
			log.warn("Failed to refresh context before returning [context="+context+"]");
		}
		return context.getResponse();
	}

	private boolean amountDifferenceAcceptable(DoProcessorResponse response) {
		String amountString = service.getData(context.getTransaction(), "amount", 1, false);
		if(amountString==null) {
			amountString = service.getData(context.getTransaction(), "amount", 1, true);
		}
		Long amountCents = CurrencyAmount.fromAmountString(amountString).toCents();
		findAndSetAmountDifference();
		BigDecimal acceptedDifference = (context.getAmountDifference()!=null)?context.getAmountDifference():new BigDecimal(0);
		BigDecimal amountReceived = new BigDecimal((response.getAmountCentsReceived()!=null)?response.getAmountCentsReceived():amountCents);
		BigDecimal amountEntered = new BigDecimal(amountCents);

		if (amountReceived.subtract(amountEntered).abs().subtract(acceptedDifference).longValue() > 0) {
			getResponse().setError(true);
			response.setMessage("We're sorry but the transaction amount is incorrect. Please contact support.");
			String comment = getInvalidTransactionAmountMessage(amountEntered, amountReceived);
			context.getTransaction().setDeclineReason(getError(CASHIER_NOT_ACCEPTABLE_AMOUNT_DIFFERENCE));
			log.error("Transaction (" + response.getTransactionId() + ") declined: " + comment);
			response.addRawResponseLog(comment);
			response.setStatus(DoProcessorResponseStatus.DECLINED);
			return false;
		} else {
			service.setData(context.getTransaction(), "amount", amountReceived.movePointLeft(2).toString(), 1, false);
			getResponse().setError(false);
			return true;
		}
	}

	//Used by internal callback
	public DoResponse processorCallback(DoProcessorResponse response, boolean isSafe) throws Exception {
		try {
			return processorCallback(response, null, isSafe);
		} catch (Status407TransactionInFinalStateException e) {
			return transactionInFinalStateResponse();
		}
	}

	//used by external callback
	public DoResponse processorCallback(DoProcessorResponse response, Object processorRequest, boolean isSafe) throws Exception {
		if (response.getTransactionId() == null) throw new Exception("Invalid transaction ID");

		createContextFromTransactionId(response.getTransactionId(), SYSTEM_GUID);

		// The reversal workflow needs to be started.
		// Performing normal response save will poison the original transaction with reversal specific data
		if (response.getStatus() == DoProcessorResponseStatus.REVERSAL_NEXTSTAGE) {
			reversalContext = new DoMachineContext();
			mapper.map(context, reversalContext);
			reversalContext.setType(TransactionType.REVERSAL);
			reversalContext.setState(DoMachineState.START);
			reversalContext.setProcessorResponse(response);
			reversalContext.setSource("Processor Callback");
			reversalContext.setProcessorCallbackRequest(processorRequest);
			reversalContext.setProcessorUser(userService.findOrCreateProcessorUser(response.getProcessorUserId(), context.getUser(), context.getProcessor()));
			reversalContext.setStage(0);
			reversalContext.setLinkedTransaction(context.getTransaction());
			forceStartTransaction(reversalContext);
			context.setLinkedTransaction(reversalContext.getTransaction());
			reversalContext.getTransaction().setAmountCents(context.getTransaction().getAmountCents());
			reversalContext.getTransaction().setAccountInfo(context.getTransaction().getAccountInfo());
			saveProcessorResponseOutputData(reversalContext);
			//we should not process callback in case transaction is in final state except of REVERSAL_NEXTSTAGE
			//isSafe is added for backward capability
		} else if ( isSafe && response.getStatus() != null
				&& !context.getTransaction().getCurrent().getStatus().getActive()) {
			return getResponse();
		} else {
			context.setProcessorResponse(response);
			context.setSource("Processor Callback");
			context.setProcessorCallbackRequest(processorRequest);
			context.setProcessorUser(userService.findOrCreateProcessorUser(response.getProcessorUserId(), context.getUser(), context.getProcessor()));
			saveProcessorResponseOutputData();
		}
		debug("processorCallback");

		if (response.getStatus() == null)
			return finish(context.getState());

		//TODO callback IP security
		switch (response.getStatus()) {
			case EXPIRED: {
				return finish(DoMachineState.EXPIRED);
			}
			case NOOP: {
				return finish(DoMachineState.WAITFORPROCESSOR);
			}
			case NEXTSTAGE: {
				incrementStage();
				if (stageFieldService.findInputFieldsByMethodStage(context.getMethodStage()).size() > 0) context.setState(DoMachineState.NEEDINPUT);
//				context.setState(DoMachineState.NEEDINPUT);
				return processState();
			}
			case NEXTSTAGE_NOPROCESS: {
				incrementStage();
				return finish(DoMachineState.WAITFORPROCESSOR);
			}
			case NEXTSTAGE_NOPROCESS_WITH_RETRY: {
				incrementStage();
				service.saveDoRetryTransaction(context.getTransaction().getId(),true);
				return finish(DoMachineState.WAITFORPROCESSOR);
			}
			case REDIRECT_NEXTSTAGE: {
				incrementStage();
				return finish(DoMachineState.WAITFORPROCESSOR);
			}
			case DECLINED: {
				context.getTransaction().setDeclineReason(response.getDeclineReason() != null ? response.getDeclineReason(): getError(CASHIER_PROCESSOR_CALLBACK_DECLINED));
				DoResponse doResponse = finish(DoMachineState.DECLINED);
				return doResponse;
			}
			case PLAYER_CANCEL:
				return finish(DoMachineState.PLAYER_CANCEL);
			case FATALERROR:
				throw new DoErrorException("Fatal error from processor.");
			case INPUTERROR:
				context.setState(DoMachineState.NEEDINPUT);
				return processState();
			case SUCCESS:
				if (amountDifferenceAcceptable(response)) {
					return finish(DoMachineState.SUCCESS);
				} else {
					DoResponse doResponse = finish(DoMachineState.DECLINED);
					return doResponse;
				}
			case IFRAMEPOST:
				return finish(DoMachineState.IFRAMEPOST);
			case REDIRECT:
				return finish(DoMachineState.WAITFORPROCESSOR);
			case REVERSAL_NEXTSTAGE:
				incrementStage(reversalContext);
				return processReversalState();
			case PENDING_AUTO_RETRY:
				return finish(DoMachineState.WAITFORPROCESSOR);
		};

		throw new Exception("Invalid status " + response.getStatus());
	}

	/**
	 * Reversal state engine.
	 * Currently it will not be a true state engine since modifications to MethodStage DB is required for persisting
	 * the reversal transaction type.
	 *
	 * Reversal validitiy is verified by checking if the original transaction was completed. If not, no reversal is attemted.
	 * @return
	 */
	private DoResponse processReversalState() throws DoErrorException, Status407TransactionInFinalStateException {
		if (context.getTransaction().getCurrent().getStatus().isSuccess() ||
				context.getTransaction().getCurrent().getStatus().getCode().contentEquals(DoMachineState.REVERSALREJECTED.name())) {
			//update tran workflow to show reversal is being attempted.
			try {
				addReversalWorkflow(DoMachineState.REVERSALPENDING, DoMachineState.REVERSALPENDING);
			} catch (DoErrorException e) {
				log.warn("Unable to set reversal workflow to pending state for context: " + reversalContext + " original context: " + context);
			}
		} else {
			// The original tran is not in a reversable state, so we just ignore the reversal request.
			return finishReversal(DoMachineState.DECLINED, context.getState());
		}
		// If original tran was success, send to processor for stage 1, when it comes back, read state and approve or deny
		//This process could change in the future to add more states
		// When it comes back, set the original tran state to reversal pending, then do accounting, then set state to reversed or failed
		// We should let admin person know about the reversal so they can take action if needed by viewing transaction

		switch (callProcessor(reversalContext)) {
			case EXPIRED:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case NEXTSTAGE:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case NOOP:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case IFRAMEPOST:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case REDIRECT_NEXTSTAGE:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case REDIRECT:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case INPUTERROR:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case FATALERROR:
				throw new DoErrorException("Fatal error from processor during reversal.");
			case SUCCESS:
				return finishReversal(DoMachineState.SUCCESS, DoMachineState.REVERSALAPPROVED);
			case DECLINED:
				return finishReversal(DoMachineState.DECLINED, DoMachineState.REVERSALREJECTED);
			case PLAYER_CANCEL:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
			case NEXTSTAGE_NOPROCESS: ;
				break;
			case REVERSAL_NEXTSTAGE:
				log.warn("No implementation in reversal machine for " + reversalContext);
				break;
		}
		return null;
	}

	public DoProcessorRequest processorCallbackGetTransaction(
			long transactionId,
			String processorReference,
			String processorCode,
			Boolean checkOOB
	) throws Exception {
		createContextFromTransactionId(transactionId, SYSTEM_GUID);
		if (context.getProcessor() == null) throw new Exception("The transaction is not yet linked to a processor");
		if (!context.getProcessor().getProcessor().getCode().equals(processorCode))
			throw new Exception("The transaction does not belong to this processor");
		DoProcessorRequest request = createProcessorRequest();
		if (!checkOOB) return request;
		String currentProcessorReference = context.getTransaction().getProcessorReference();
		if ((currentProcessorReference != null) && (currentProcessorReference.equalsIgnoreCase(processorReference))) {
			return request;
		} else {
			Transaction transaction = service.findByProcessorReference(processorReference);
			if (transaction != null) {
				createContext(transaction, SYSTEM_GUID);
				if (context.getProcessor() == null)
					throw new Exception("The transaction is not yet linked to a processor");
				if (!context.getProcessor().getProcessor().getCode().equals(processorCode))
					throw new Exception("The transaction does not belong to this processor");
				return createProcessorRequest();
			}
			if (context.getTransaction().getCurrent().getStatus().codeIs(
					"START",
					"VALIDATEINPUT",
					"WAITFORPROCESSOR"
			)) {
				return request;
			} else {
				DoProcessorRequest previousRequest = request.toBuilder().build();
				log.trace("Start of Out of Band Transaction");
				context.setSource("Out Of Band Transaction");
				forceStartTransaction(context);
				request.setTransactionId(context.getTransaction().getId());
//				service.linkTransactions(previousRequest.getTransactionId(), request.getTransactionId());
				String amount = BigDecimal.valueOf(context.getTransaction().getLinkedTransaction().getAmountCents()).movePointLeft(2).toPlainString();
				service.setData(context.getTransaction(), "amount", amount, 1, false);
				addWorkflow("WAITFORPROCESSOR", true, null);
				request.setPreviousProcessorRequest(previousRequest);
				return request;
			}
		}
	}

	public DoProcessorRequest processorCallbackGetTransaction(long transactionId, String processorCode) throws Exception {
		createContextFromTransactionId(transactionId, SYSTEM_GUID);
		if (context.getProcessor() == null) throw new Exception("The transaction is not yet linked to a processor (transactionId: "+transactionId+")");
		if (!context.getProcessor().getProcessor().getCode().equals(processorCode)) throw new Exception("The transaction does not belong to this processor (transactionId: "+ transactionId+")");
		return createProcessorRequest();
	}

	public DoProcessorRequest processorCallbackGetTransaction(String processorReference, String processorCode) throws Exception {
		createContextFromProcessorReference(processorReference, SYSTEM_GUID);
		if (context.getProcessor() == null) throw new Exception("The transaction is not yet linked to a processor (processorReference: "+processorReference+")");
		if (!context.getProcessor().getProcessor().getCode().equals(processorCode)) throw new Exception("The transaction does not belong to this processor (processorReference: "+processorReference+")");
		return createProcessorRequest();
	}

	public DoProcessorRequest processorCallbackGetTransactionByAdditionalReference(String additionalReference, String processorCode) throws Exception {
		createContextFromAdditionalReference(additionalReference, SYSTEM_GUID);
		if (context.getProcessor() == null) throw new Exception("The transaction is not yet linked to a processor (additionalReference: "+additionalReference+")");
		if (!context.getProcessor().getProcessor().getCode().equals(processorCode)) throw new Exception("The transaction does not belong to this processor (additionalReference: "+additionalReference+")");
		return createProcessorRequest();
	}

	private DoResponse handleProcessTransactionException(Throwable t) {

		if (t instanceof Status407TransactionInFinalStateException) {
			return transactionInFinalStateResponse();
		}

		DoResponse response = getResponse();
		response.setError(true);
		if (response.getErrorMessage() == null || response.getErrorMessage().trim().isEmpty()) {
			response.setErrorMessage(getError(CASHIER_PAYMENT_METHOD_UNAVAILABLE));
			response.setDeclineReason(getError(CASHIER_PAYMENT_METHOD_UNAVAILABLE));
		}
		DoMachineState finalState = DoMachineState.FATALERROR;
		if (INSUFFICIENT_BALANCE_MESSAGE.equals(t.getMessage())) {
			finalState = DoMachineState.DECLINED;
			response.setDeclineReason(getError(CASHIER_INSUFFICIENT_BALANCE));
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("\r\n" + ExceptionUtils.getMessage(t) + "\r\n");
			sb.append("" + ExceptionUtils.getRootCauseMessage(t) + "\r\n\r\n");
			sb.append("Full Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(t));
			response.setStacktrace(sb.toString());
		}
		try {
			if (context.getTransaction() != null) {
				context.getTransaction().setDeclineReason(response.getDeclineReason());
			}
			DoResponse doResponse = finish(finalState);
			return doResponse;
		} catch (DoErrorException | Status407TransactionInFinalStateException exception2) {
			log("Fatal error");
			log.error("Fatal error during processing, and then a fatal error trying to flag the transaction as a fatal error. " + context.getTransaction().toString(), exception2);
			return context.getResponse();
		}
	}

	private Transaction startTransaction(
			Long sessionId,
			TransactionType type,
			DomainMethod domainMethod,
			Long amountCents,
			String currencyCode,
			String userGuid,
			String source,
			DomainMethodProcessor processor,
			Boolean directWithdrawal,
			User initiationAuthor,
			Transaction linkedTransaction
	) {
		Transaction transaction = service.startTransaction(
				sessionId,
				type,
				domainMethod,
				amountCents,
				currencyCode,
				userGuid,
				source,
				processor,
				directWithdrawal,
				initiationAuthor,
				getTtl(processor),
				linkedTransaction
		);
		doMetricsCounterUpdates(transaction, "start");
		pubSubWalletTransactionService.buildAndSendWalletTransactionMessage(transaction, null);
		return transaction;
	}

	private Long getTtl(DomainMethodProcessor processor) {
		if (isNull(processor)) {
			return properties.getTransactionDefaultTtl();
		}
		return dmpService.propertiesWithDefaults(processor.getId())
				.stream()
				.filter(domainMethodProcessorProperty -> "ttl".equalsIgnoreCase(domainMethodProcessorProperty.getProcessorProperty().getName()))
				.findFirst()
				.map(DomainMethodProcessorProperty::getValueOrDefault)
				.map(Long::valueOf)
				.orElse(properties.getTransactionDefaultTtl());
	}

	private void checkAccess() throws Status500LimitInternalSystemClientException, Status461DepositRestrictedException,
			Status462WithdrawRestrictedException {
		Access access = limitInternalSystemService.checkAccess(context.getUser().guid());
		TransactionType transactionType = context.getTransaction().getTransactionType();
		if (transactionType.equals(TransactionType.DEPOSIT) && !access.isDepositAllowed()) {
			throw new Status461DepositRestrictedException(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, context.getUser().domainName(), access.getDepositErrorMessage()));
		} else if (transactionType.equals(TransactionType.WITHDRAWAL) && !access.isWithdrawAllowed()) {
			throw new Status462WithdrawRestrictedException(RestrictionError.WITHDRAW.getResponseMessageLocal(messageSource, context.getUser().domainName(), access.getWithdrawErrorMessage()));
		}
	}

	private void checkDepositLimits(
			String userGuid, Long amountCents
	) throws
			Status477BalanceLimitReachedException,
			Status478TimeSlotLimitException,
			Status486DailyDepositLimitReachedException,
			Status487WeeklyDepositLimitReachedException,
			Status488MonthlyDepositLimitReachedException,
			Status511UpstreamServiceUnavailableException,
			Status438PlayTimeLimitReachedException {
		// TODO: Do user specific locale lookup if we want that at some point
		// FIXME: Keep local cache of keys on this service to speed up calls
		String domainName = userGuid.split("/")[0];
		log.debug("checkDepositLimits: domain name:" + domainName);
		String defaultLocale = null;
		try {
			defaultLocale = cachingDomainClientService.getDomainClient().findByName(domainName).getData().getDefaultLocale();
			log.debug("checkDepositLimits: locale:" + defaultLocale);

			DepositLimitClient depositLimitClient = serviceFactory.target(DepositLimitClient.class);
			depositLimitClient.allowedToDeposit(userGuid, amountCents, defaultLocale);
		} catch (
				Status438PlayTimeLimitReachedException |
						Status477BalanceLimitReachedException |
						Status478TimeSlotLimitException |
						Status486DailyDepositLimitReachedException |
						Status487WeeklyDepositLimitReachedException |
						Status488MonthlyDepositLimitReachedException e
		) {
			throw e;
		} catch (Exception e) {
			throw new Status511UpstreamServiceUnavailableException(e.getMessage());
		}
	}

	public DoResponse processState() throws DoErrorException, Status407TransactionInFinalStateException {
		if (!context.isDirectWithdraw() && !allowedToAccess()) {
			log.warn("Rejection by access rule service to perform transaction. " + context.getExternalUser() + " " + context.getProcessor());
			if (applyAccessRuleBlock) {
				try {
					context.setSource("Access rule validation check");
					// FIXME: 2020/02/20 Perform some additional info insertion here, could possibly have some reference to access rule checks
					context.getResponse().setErrorMessage("Further customer information is required to continue with your transaction. Please contact support.");
					findOrCreateTransaction(allowMultipleWithdrawals);
					String comment = getError(CASHIER_ACCESS_RULE_CHECK_FAILED);
					context.getTransaction().setDeclineReason(comment);
					DoResponse response = finish(DoMachineState.DECLINED);
					service.addComment(context.getTransaction(), userService.find("default/admin"), comment);
					return response;
				} catch (DoErrorException exception) {
					DoResponse response = getResponse();
					response.setError(true);
					response.setErrorMessage(exception.getMessage());
					return context.getResponse();
				}
			}
		}
		populateStageInputFields();
		populateStageOutputFields();

		debug("processState");

		if (log.isDebugEnabled()) {
			log.debug("------------------------------------------------------------------------------------------------------------");
			log.debug(context.getState().name()+"   ::   "+context.getStage());
			log.debug("------------------------------------------------------------------------------------------------------------");
			context.getInputFieldGroups().entrySet().forEach(e -> {
				e.getValue().getFields().entrySet().forEach(f -> {
					log.debug(e.getKey()+"  ::  "+f.getKey()+" ::Code: "+f.getValue().getCode()+" :Value: "+f.getValue().getValue());
				});
			});
			log.debug("------------------------------------------------------------------------------------------------------------");
		}

		if (context.getRequest().getState() != null && context.getRequest().getState().equals(DoMachineState.CANCEL.toString())) {
			return finish(DoMachineState.CANCEL);
		}

		if (context.getRequest().getState() != null && context.getRequest().getState().equals(DoMachineState.PLAYER_CANCEL.toString())) {
			return finish(DoMachineState.PLAYER_CANCEL);
		}

		switch (context.getState()) {
			case REQUIREDFIELDS: {
				if (populateMissingRequiredInputFields()) {
					sendStageInputFields();
					sendStageOutputFields();
					return finish(DoMachineState.CHECKREQUIREDFIELDS);
				} else {
					context.setState(DoMachineState.NEEDINPUT);
					incrementStage();
					return processState();
				}
			}
			case CHECKREQUIREDFIELDS: {
				populateMissingRequiredInputFields();
				if (!hasMissingRequiredInputFields()) {
					sendStageInputFields();
					sendStageOutputFields();
					context.setState(DoMachineState.NEEDINPUT);
					incrementStage();
					return processState();
				} else {
					sendStageInputFields();
					sendStageOutputFields();
					return finish(DoMachineState.CHECKREQUIREDFIELDS);
				}
			}
			case NEEDINPUT: {
				if (context.inAppAndHasProductGuid()) {
					findOrCreateTransaction(allowMultipleWithdrawals);
					saveStageInputFields();
				} else {
					sendStageInputFields();
					sendStageOutputFields();
				}
				return finish(DoMachineState.VALIDATEINPUT);
			}
			case VALIDATEINPUT: {
				chooseProcessor();
				if (isInitialValidationFailed()) {
					return context.getResponse();
				}
				findOrCreateTransaction(allowMultipleWithdrawals);
				if (!context.isDirectWithdraw()) {
					try {
						checkAccess();
					} catch (
							Status461DepositRestrictedException |
									Status462WithdrawRestrictedException |
									Status500LimitInternalSystemClientException e
					) {
						context.setSource("System");
						String comment = getError(CASHIER_ACCESS_RESTRICTED);
						context.getTransaction().setDeclineReason(comment);
						DoResponse doResponse = finish(DoMachineState.DECLINED);
						service.addComment(context.getTransaction(), userService.getSystemUser(), comment);
						doResponse.setError(true);
						doResponse.setErrorMessage(e.getMessage());
						return doResponse;
					}
				}
				if (context.inAppAndHasProductGuid()) {
					findOrCreateTransaction(allowMultipleWithdrawals);
					saveStageInputFields();
					if (context.getType() == TransactionType.DEPOSIT) {
						try {
							checkDepositLimits(context.getUser().guid(), getAmountCents());
						} catch (
								Status438PlayTimeLimitReachedException |
										Status477BalanceLimitReachedException |
										Status486DailyDepositLimitReachedException |
										Status487WeeklyDepositLimitReachedException |
										Status488MonthlyDepositLimitReachedException |
										Status511UpstreamServiceUnavailableException |
										Status478TimeSlotLimitException e
						) {
							context.setSource("User Limits Check");
							context.getResponse().setErrorMessage(e.getMessage()); // Already translated on svc-limit side.
							findOrCreateTransaction(allowMultipleWithdrawals);
							if (e.getCode() == 438){
								context.getTransaction().setDeclineReason(getPlayTimeCheckLimitMessage(e.getMessage()));
							} else {
								context.getTransaction().setDeclineReason(getDepositCheckLimitMessage(e.getMessage()));
							}
							DoResponse response = finish(DoMachineState.DECLINED);
							service.addComment(context.getTransaction(), userService.getSystemUser(), e.getMessage());
							return response;
						}
					}
					return finish(DoMachineState.WAITFORPROCESSOR);
				}
				if ((stageFieldService.findInputFieldsByMethodStage(context.getMethodStage()).size() > 0) &&
						(context.getRequest().getInputFieldGroups() == null || context.getRequest().getInputFieldGroups().isEmpty())) {
					sendStageInputFields();
					sendStageOutputFields();
					return getResponse();
				}
				if (!validateInputFields()) {
					sendStageInputFields();
					sendStageOutputFields();
					return finish(DoMachineState.VALIDATEINPUT);
				} else {
					findOrCreateTransaction(allowMultipleWithdrawals);
					saveStageInputFields();
					if (context.getType() == TransactionType.DEPOSIT) {
						try {
							checkDepositLimits(context.getUser().guid(), getAmountCents());
						} catch (
								Status478TimeSlotLimitException |
										Status438PlayTimeLimitReachedException |
										Status477BalanceLimitReachedException |
										Status486DailyDepositLimitReachedException |
										Status487WeeklyDepositLimitReachedException |
										Status488MonthlyDepositLimitReachedException |
										Status511UpstreamServiceUnavailableException e
						) {
							context.setSource("User Limits Check");
							context.getResponse().setErrorMessage(e.getMessage()); // Already translated on svc-limit side.
							findOrCreateTransaction(allowMultipleWithdrawals);

							if (e.getCode()==438){
								context.getTransaction().setDeclineReason(getPlayTimeCheckLimitMessage(e.getMessage()));
							} else {
								context.getTransaction().setDeclineReason(getDepositCheckLimitMessage(e.getMessage()));
							}
							DoResponse response = finish(DoMachineState.DECLINED);
							service.addComment(context.getTransaction(), userService.getSystemUser(), e.getMessage());
							return response;
						}
					}

					if (!context.isDirectWithdraw() && !validateProcessorLimits()) {
						sendStageInputFields();
						sendStageOutputFields();
						return finish(DoMachineState.VALIDATEINPUT);
					}

					addTransactionRemark();
					if(!verifyInputProcessorAccount()) {
						DoResponse response = finish(DoMachineState.DECLINED);
						return response;
					}

					if (context.getType() == TransactionType.DEPOSIT || context.getStage() > 1) {
						context.setState(DoMachineState.WAITFORPROCESSOR);
						return processState();
					} else {
						AutoApproveResult autoApprove = autoApprove(null);
						if (autoApprove.isApproved()) {
                            return processAutoApproval(autoApprove.getTrace(), autoApprove.getProcessDelay());
						} else {
							return finish(DoMachineState.WAITFORAPPROVAL);
						}
					}
				}
			}
			case ON_HOLD_REPROCESS: {
				chooseProcessor();
				if (context.getType() == TransactionType.WITHDRAWAL) {
					transactionService.updateTtl(context.getTransaction().getId(), calculateTtlAfterHold(context.getTransaction()));
					StringBuilder trace = new StringBuilder();
					AutoApproveResult autoApprove = autoApprove(null);
					if (autoApprove.isApproved()) {
                        return processAutoApproval(autoApprove.getTrace(), autoApprove.getProcessDelay());
					} else {
						return finish(DoMachineState.WAITFORAPPROVAL);
					}
				}
			}
			case WAITFORPROCESSOR: {
				chooseProcessor();
				switch (callProcessor()) {
					case NOOP: {
						sendStageInputFields();
						sendStageOutputFields();
						return finish(DoMachineState.WAITFORPROCESSOR);
					}
					case NEXTSTAGE: {
						incrementStage();
						List<MethodStageField> methodStageFields = stageFieldService.findInputFieldsByMethodStage(context.getMethodStage());
						if (methodStageFields.stream().anyMatch(f -> BooleanUtils.isTrue(f.getRequired()))) context.setState(DoMachineState.NEEDINPUT);
						addRetryOrNextStageWorkflow("Stage updated.", context.getProcessorResponse() != null ? context.getProcessorResponse().getRawResponseLog() : "No information available.");
						return processState();
					}
					case NEXTSTAGE_NOPROCESS: {
						incrementStage();
						return finish(DoMachineState.WAITFORPROCESSOR);
					}
					case NEXTSTAGE_NOPROCESS_WITH_RETRY: {
						incrementStage();
						service.saveDoRetryTransaction(context.getTransaction().getId(),true);
						return finish(DoMachineState.WAITFORPROCESSOR);
					}
					case DECLINED: {
						DoResponse doResponse = finish(DoMachineState.DECLINED);
						return doResponse;
					}
					case PLAYER_CANCEL:
						return finish(DoMachineState.PLAYER_CANCEL);
					case FATALERROR:
						throw new DoErrorException("Fatal error from processor.");
					case INPUTERROR:
						sendStageInputFields();
						sendStageOutputFields();
						return finish(DoMachineState.VALIDATEINPUT);
					case SUCCESS:
						if (amountDifferenceAcceptable(context.getProcessorResponse())) {
							return finish(DoMachineState.SUCCESS);
						} else {
							return finish(DoMachineState.DECLINED);
						}
					case IFRAMEPOST:
						return finish(DoMachineState.WAITFORPROCESSOR);
					case IFRAMEPOST_NEXTSTAGE:
					case REDIRECT_NEXTSTAGE:
						incrementStage();
						return finish(DoMachineState.WAITFORPROCESSOR);
					case REDIRECT:
						return finish(DoMachineState.WAITFORPROCESSOR);
					case EXPIRED: {
						return finish(DoMachineState.EXPIRED);
					}
					case PENDING_AUTO_RETRY: {
						if (!context.getTransaction().isRetryProcessing())
							service.saveDoRetryTransaction(context.getTransaction().getId(),true);
						sendStageInputFields();
						sendStageOutputFields();
						return finish(DoMachineState.WAITFORPROCESSOR);
					}
				};
			}

			case WAITFORAPPROVAL:
			case ON_HOLD: {
				return getResponse();
			}
			case AUTO_APPROVED_DELAYED: {
				Date processTime = context.getTransaction().getCurrent().getProcessTime();
				if (processTime != null && processTime.before(new Date())) {
					finish(DoMachineState.AUTO_APPROVED);
					return processState();
				} else {
					return getResponse();
				}
            }
            case APPROVED_DELAYED: {
                Date processTime = context.getTransaction().getCurrent().getProcessTime();
                if (processTime != null && processTime.before(new Date())) {
                    finish(DoMachineState.APPROVED);
                    return processState();
                } else {
                    return getResponse();
                }
            }
            case SUCCESS:
				return finish(DoMachineState.SUCCESS);
			case APPROVED:
				if (hasEnoughBalance(context.isWithdrawalFundsReserved())) {
					context.setState(DoMachineState.WAITFORPROCESSOR);
					return processState();
				} else {
					return finish(DoMachineState.APPROVED);
				}
			case AUTO_APPROVED:
				if (hasEnoughBalance(context.isWithdrawalFundsReserved())) {
					context.setState(DoMachineState.WAITFORPROCESSOR);
					return processState();
				} else {
					// Balance isn't sufficient for some reason, go back to waiting for CS approval.
					service.addComment(context.getTransaction(), userService.getSystemUser(),
							"Insufficient balance on auto-approved transaction." +
									" Transaction state moved back to waiting for a manual approval.");
					return finish(DoMachineState.WAITFORAPPROVAL);
				}
			case PLAYER_CANCEL:
			case PENDING_CANCEL:
			case CANCEL:
				return getResponse();
			case DECLINED:
				DoResponse doResponse = getResponse();
				return doResponse;
			case EXPIRED:
				return getResponse();
			default:
				throw new DoErrorException("Invalid state.");
		}
	}

	private DoResponse processAutoApproval(String trace, Long processDelay) throws DoErrorException, Status407TransactionInFinalStateException {
		User approver = userService.getSystemUser();
		Transaction transaction = context.getTransaction();
		log.info("Transaction(" + transaction.getId() + "): autoApproved=" + transaction.isAutoApproved() + ", version=" + transaction.getVersion() + " (before set autoapproved)");
		transactionService.addTagForTransaction(transaction, TransactionTagType.AUTO_APPROVED);
		log.info("Transaction(" + transaction.getId() + "): autoApproved=" + transaction.isAutoApproved() + ", version=" + transaction.getVersion() + " (after set autoapproved)");
		transaction.setReviewedBy(approver);
		// Setting this source because there is a conditional in workflow step
		// that causes source "Customer" to not run reversal on declined trans
		if (context.getSource().startsWith("Customer")) context.setSource("System");
		if (processDelay == null) {
			finish(DoMachineState.AUTO_APPROVED);
			service.addComment(context.getTransaction(), approver,
					"Withdrawal auto-approved by system.\r\n\r\n" + trace);
			log.debug("Auto approved transaction(" + transaction.getId() + "): autoApproved=" + transaction.isAutoApproved() + ", version=" + transaction.getVersion() + " (after commented)");
			registerStat(Type.CASHIER, Event.AUTO_APPROVED_WITHDRAWAL,
					context.getExternalUser().getDomain().getName(), context.getExternalUser().guid());

			return processState();
		} else {
			finish(DoMachineState.AUTO_APPROVED_DELAYED, processDelay);
			service.addComment(context.getTransaction(), approver,
					"Withdrawal auto-approved by system with delay " + processDelay +" ms.\r\n\r\n" + trace);
			log.debug("Auto approved delayed transaction(" + transaction.getId() + "): autoApproved=" + transaction.isAutoApproved() + ", version=" + transaction.getVersion() + " (after commented)");
			registerStat(Type.CASHIER, Event.AUTO_APPROVED_WITHDRAWAL,
					context.getExternalUser().getDomain().getName(), context.getExternalUser().guid());

			return getResponse();
		}

	}

	private AutoApproveResult autoApprove(AutoWithdrawalRuleSet ruleset) {
		boolean autoWithdrawalAllowedForUser = (context.getExternalUser().getAutoWithdrawalAllowed() == null)
				? true
				: context.getExternalUser().getAutoWithdrawalAllowed();
		if (autoWithdrawalAllowedForUser) {
			return autoWithdrawalService.shouldAutoApproveTransaction(ruleset, context.getExternalUser(),
					context.getDomainMethod(), context.getTransaction());
		} else {
			return AutoApproveResult.builder().approved(false).build();
		}
	}

	/**
	 * Performs a check on stage 1 of a deposit or payout to determine if a player is allowed to perform the transaction.
	 * Processor rule is evaluated first, if access is allowed, a check on domain method is attempted.
	 * Thus all rules are evaluated for a given processor / method.
	 * @return boolean
	 */
	private boolean allowedToAccess() {
		boolean allowAccess = true;
		if (context.getStage() != null && context.getStage() == 1) {
			if (context.getProcessor() != null) {
				allowAccess = accessRuleService.checkAuthorization(
						context.getProcessor(),
						context.getExternalUser().getLastLogin() == null ? "unknown" : context.getExternalUser().getLastLogin().getIpAddress(),
						context.getExternalUser().getLastLogin() == null ? "unknown" : context.getExternalUser().getLastLogin().getUserAgent(),
						context.getDeviceId(),
						context.getUser().getGuid());
			}
			if (allowAccess) {
				allowAccess = accessRuleService.checkAuthorization(
						context.getDomainMethod(),
						context.getExternalUser().getLastLogin() == null ? "unknown" : context.getExternalUser().getLastLogin().getIpAddress(),
						context.getExternalUser().getLastLogin() == null ? "unknown" : context.getExternalUser().getLastLogin().getUserAgent(),
						context.getDeviceId(),
						context.getUser().getGuid());
			}
		}
		return allowAccess;
	}

	private boolean isInitialValidationFailed() {
		if (context.getStage() != null && context.getStage() == 1 && context.getProcessor() != null) {
			Boolean initialValidationEnabled = dmpService.properties(context.getProcessor().getId())
					.stream()
					.filter(prop -> "initialValidation".equalsIgnoreCase(prop.getProcessorProperty().getName()) && prop.getValue() != null)
					.map(prop -> Boolean.parseBoolean(prop.getValue()))
					.findAny().orElse(false);

			if (initialValidationEnabled) {
				try {
					String url = context.getProcessor().getProcessor().getUrl();

					InitialValidateClient validateClient = serviceFactory.target(InitialValidateClient.class, url, true);

					String type = context.getDomainMethod().getDeposit() ? "deposit" : "withdraw";
					String domainName = context.getDomainMethod().getDomain().getName();

					Response<Boolean> validateResponse = validateClient.validate(context.getInputFieldGroups(), domainName, type);
					if (!validateResponse.getData()) {
						DoResponse response = getResponse();
						response.setError(true);
						response.setErrorMessage(validateResponse.getMessage());
						log.warn("Initial validation failed for '" + url + "' (" + type + ") due " + validateResponse.getMessage());
						return true;
					}
					if (nonNull(validateResponse.getData2()) && validateResponse.getData2() instanceof Map) {
						Map<String, Map<String, String>> stagesMap = (Map<String, Map<String, String>>) validateResponse.getData2();
						stagesMap.forEach((key, map) -> map.forEach((name, value) -> {
							if (context.getInputFieldGroups().get(key).getFields().containsKey(name)) {
								context.getInputFieldGroups().get(key).getFields().get(name).setValue(value);
								log.info("Stage " + key + ", field '" + name + "' replaced with " + value);
							} else {
								log.info("Unknown field: Stage " + key + ", '" + name + "' = " + value);
							}
						}));
					}
					log.info("Initial validation passed for '" + url + "' (" + type + "): " + validateResponse.getMessage());
				} catch (Exception e) {
					DoResponse response = getResponse();
					response.setError(true);
					response.setErrorMessage(e.getMessage());
					log.warn("Initial validation failed due " + e.getMessage());
					return true;
				}
			}

		}
		return false;
	}

	private boolean hasEnoughBalance(boolean isFundsReserved) {
		boolean enoughBalance = transactionService.hasEnoughBalance(context.getUser().domainName(), context.getUser().getGuid(),
				context.currencyCode(), ofNullable(context.getTransaction()).map(Transaction::getId).orElse(null), isFundsReserved);
		if (!enoughBalance) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(translate("SERVICE-CASHIER.ERROR_INSUFFICIENT_FUNDS"));
		}
		return enoughBalance;
	}

	private DoResponse finishReversal(DoMachineState reversalContextState, DoMachineState originalContextState) throws DoErrorException, Status407TransactionInFinalStateException {
		debug("finish1");
		if (reversalContext != null && context != null) {
			context.setState(originalContextState);
			reversalContext.setState(reversalContextState);
			if (reversalContext.getTransaction() != null) addReversalWorkflow(reversalContextState, originalContextState);
		}
		log("finish");
		return getResponse();
	}

	private DoResponse finish(DoMachineState state) throws DoErrorException, Status407TransactionInFinalStateException {
		return finish(state, null);
	}

	private DoResponse finish(DoMachineState state, Long processDelay) throws DoErrorException, Status407TransactionInFinalStateException {
		debug("finish1");
		String previousState = context.getTransaction().getCurrent().getStatus().getCode();
		context.setState(state);
		addWorkflow(context.getState().name(), context.getState().isActive(), processDelay);
		log("finish");
		DoResponse doResponse = getResponse();
		updatesForFirstSuccessTransactionCase(state);
		updateNullTransactionAmount();
		postProcessorService.proceedOnTransactionStateChange(context, previousState);
		return doResponse;
	}

	private void updatesForFirstSuccessTransactionCase(DoMachineState state) {

		if (!DoMachineState.SUCCESS.getCode().equals(state.getCode()) || !"SUCCESS".equals(context.getTransaction().getCurrent().getStatus().getCode())) {
			return;
		}

		if (transactionService.isFirstSuccessTransaction(context.getTransaction().getId(), context.getUser().getGuid(), context.getType())) {
			switch (context.getTransaction().getTransactionType()) {
				case DEPOSIT -> {
					context.setFirstDeposit(true);
					transactionService.addTagForTransaction(context.getTransaction(), FIRST_DEPOSIT);
				}
				case WITHDRAWAL -> transactionService.addTagForTransaction(context.getTransaction(), FIRST_WITHDRAWAL);
				default -> {}
			}
		}
	}

	private void updateNullTransactionAmount() {
		if (context.getTransaction().getAmountCents() == null) {
			updateContextTransactionWithAmountsCents(context);
		}
	}

	private void createContext(DomainMethod domainMethod, Long sessionId, String guid, String initiationAuthorGuid,
							   DoRequest request, String type, boolean balanceLimitEscrow, String ip, Map<String, String> headers, boolean isFirstDeposit, Long linkedTransactionId) throws DoErrorException {
		if (guid == null) throw new DoErrorException("Missing userGuid");

		context = new DoMachineContext();
		context.setSessionId(sessionId);
		context.setFirstDeposit(isFirstDeposit);
		context.setResponse(new DoResponse());
		context.setRequest(request);
		context.setStage(request.getStage());
		context.setDirectWithdraw("direct_withdraw".equals(type));
		context.setBalanceLimitEscrow(balanceLimitEscrow);
		context.setLinkedTransaction(Optional.ofNullable(linkedTransactionId).map(id -> transactionService.getTransactionById(linkedTransactionId)).orElse(null));
		if (context.isDirectWithdraw()) {
			context.setInitiationAuthor(userService.find(initiationAuthorGuid));
		}
		//This is a header used to transport the paypal fraudnet and similar fraud prevention payloads - correlation-id
		if (headers.containsKey("correlation-id")) {
			request.getInputFieldGroups().putIfAbsent("2", DoStateFieldGroup.builder().build());
			DoStateField field = DoStateField.builder().value(headers.get("correlation-id")).build();
			request.getInputFieldGroups().get("2").getFields().put("correlationId", field);
		}

		if ((request.getState() != null) && (!request.getState().isEmpty())) {
			try {
				context.setState(DoMachineState.valueOf(request.getState()));
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			};
		}
		if (context.getDeviceId() == null) {
			context.setDeviceId(request.getDeviceId());
		}

		if (headers.containsKey("X-Forwarded-For")) ip = headers.get("X-Forwarded-For");
		context.setUserRequest(
				UserRequest.builder().ipAddr(ip)
						.headers(headers)
						.build()
		);

		//TODO
//		if (context.getStage() == null) context.setStage(1);
//		if (context.getState() == null) context.setState(DoMachineState.NEEDINPUT);
		if (context.getStage() == null) context.setStage(0);
		if (context.getState() == null) context.setState(DoMachineState.REQUIREDFIELDS);

		User user = userService.findOrCreate(guid);
		context.setAuthor(user);
		context.setUser(user);

		try {
			context.setExternalUser(userService.retrieveUserFromUserService(user));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DoErrorException("Retrieval of external user failed: " + e.getMessage(), e);
		}

		try {
			context.setExternalDomain(retrieveDomainFromDomainService());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DoErrorException("Retrieval of external domain failed: " + e.getMessage(), e);
		}


		if (context.hasProductGuid()) {
			context.setProduct(
					productService.getProduct(
							context.getRequest().productGuid(),
							context.getUser().domainName(),
							context.getUserRequest().getIpAddr()
					)
			);
		}

		TransactionType transactionType = null;
		if ("deposit".equals(type)) {
			transactionType = TransactionType.DEPOSIT;
		} else if ("withdrawal".equals(type) || "direct_withdraw".equals(type)) {
			transactionType = TransactionType.WITHDRAWAL;
		}

		if (transactionType == null) throw new DoErrorException("Invalid transaction type.");
		context.setType(transactionType);

		if (domainMethod == null) throw new DoErrorException("Invalid method");
		context.setDomainMethod(domainMethod);

		if (user == null) throw new DoErrorException("Invalid user " + guid);

		Transaction transaction = null;
		if (request.getTransactionId() != null) {
			transaction = service.findById(request.getTransactionId());
			if (transaction.getCurrent().getProcessor() != null) {
				context.setProcessor(transaction.getCurrent().getProcessor());
				context.setProcessorUser(userService.findProcessorUser(context.getUser(), context.getProcessor()));
			}
			TransactionProcessingAttempt tpa = transactionProcessingAttemptService.lastAttempt(transaction);
			if ((tpa != null) && ((tpa.getProcessorMessages()!=null) && (!tpa.getProcessorMessages().isEmpty()))) {
				context.getResponse().setErrorMessage(tpa.getProcessorMessages());
			}
		} else {
			if (context.getType() == TransactionType.WITHDRAWAL && allowMultipleWithdrawals) {
				log.debug("Multiple withdrawals are active, no pending transaction checks will be conducted.");
			} else {
				transaction = service.findLastPendingTransaction(domainMethod, user);
				if (setPendingTransactionsAllowed(transaction))	{
					transaction = null;
				}
			}
		}
		if (transaction != null) {
			if (context.isDirectWithdraw()) {
				transaction.setDirectWithdrawal(true);
				transaction.setInitiationAuthor(context.getInitiationAuthor());
			}
			context.setStage(transaction.getCurrent().getStage());
			try {
				DoMachineState currentState = DoMachineState.valueOf(transaction.getCurrent().getStatus().getCode());
				if(currentState.equals(DoMachineState.WAITFORPROCESSOR) && context.getRequest().getInputFieldGroups() != null && !context.getRequest().getInputFieldGroups().isEmpty()) {
					currentState = DoMachineState.VALIDATEINPUT;
				}
				context.setState(currentState);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
		context.setTransaction(transaction);

		if ((context.getStage() > 1) && (transaction == null)) {
			throw new DoErrorException("The current stage is invalid. There is no transaction.");
		}

		context.getResponse().setMethodCode(domainMethod.getMethod().getCode());

		loadMethodStageFromDatabase();
	}

	//allow pending transactions according to the corresponding processor's "allow_multi_transactions" property, default false
	private boolean setPendingTransactionsAllowed(Transaction transaction) {

		if (transaction == null || transaction.getCurrent().getProcessor() == null) {
			return false;
		}

		try {
			Optional<ProcessorProperty> property = transaction.getCurrent().getProcessor().getProcessor().getProperties()
					.stream()
					.filter(p -> p.getName().equalsIgnoreCase("allow_multi_transactions"))
					.findFirst();
			if (property.isPresent()) {
				Optional<DomainMethodProcessorProperty> dmpProperty = dmpService.properties(transaction.getCurrent().getProcessor().getId())
						.stream()
						.filter(p -> p.getProcessorProperty().getId() == property.get().getId())
						.findFirst();
				if (dmpProperty.isPresent() && dmpProperty.get().getValue().equalsIgnoreCase("true")) {
					context.setAllowMultipleTransactions(true);
					return true;
				}
			}
		} catch (Exception ex) {
			log.warn("Failed to set pending transactions allowed", ex);
		}
		return false;
	}

	public void createAdminContext(Long transactionId, LithiumTokenUtil token, String domain) throws Exception {
		createContextFromTransactionId(transactionId, token.guid());
		if (!context.getTransaction().getDomainMethod().getDomain().getName().equals(domain))
			throw new Exception("This transaction does not belong to this domain");
	}

	public void  createAdminContextWithSource(Long transactionId, LithiumTokenUtil token, String domain, String source) throws Exception {
		createAdminContext(transactionId, token, domain);
		context.setSource(source);
	}

	private void createContextFromProcessorReference(String processorReference, String authorGuid) throws Exception {
		Transaction transaction = service.findByProcessorReference(processorReference);
		if (transaction == null) throw new Exception("Invalid transaction processorReference "+processorReference);
		createContext(transaction, authorGuid);
	}

	private void createContextFromAdditionalReference(String additionalReference, String authorGuid) throws Exception {
		Transaction transaction = service.findByAdditionalReference(additionalReference);
		if (transaction == null) throw new Exception("Invalid transaction additionalReference "+additionalReference);
		createContext(transaction, authorGuid);
	}

	private void createContextFromTransactionId(long transactionId, String authorGuid) throws Exception {
		Transaction transaction = service.findById(transactionId);
		if (transaction == null) throw new Exception("Invalid transaction ID "+transactionId);
		createContext(transaction, authorGuid);
	}

	private void createContext(Transaction transaction, String authorGuid) throws Exception {
		if (transaction.getCurrent() == null) throw new Exception("This transaction has no current workflow.");
		if (transaction.getCurrent().getStatus() == null) throw new Exception("The transaction has no current status");

		context = new DoMachineContext();
		context.setAuthor(userService.findOrCreate(authorGuid));
		context.setUser(transaction.getUser());
		try {
			context.setExternalUser(userService.retrieveUserFromUserService(transaction.getUser()));
			context.setExternalDomain(retrieveDomainFromDomainService());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		context.setDomainMethod(transaction.getDomainMethod());
		context.setStage(transaction.getCurrent().getStage());
		context.setState(DoMachineState.valueOf(transaction.getCurrent().getStatus().getCode()));
		context.setResponse(new DoResponse());
		context.setRequest(DoRequest.builder().transactionId(transaction.getId()).build());
		context.setTransaction(transaction);
		context.setType(transaction.getTransactionType());
		context.setProcessor(transaction.getCurrent().getProcessor());
		context.setProcessorUser(userService.findProcessorUser(context.getUser(), context.getProcessor()));
	}

	@Deprecated
	private void loadMethodStageFromDatabase() throws DoErrorException {
		loadMethodStageFromDatabase(context);
	}
	private void loadMethodStageFromDatabase(DoMachineContext pContext) throws DoErrorException {
		// TODO: 2019/07/26 Add reversal stage management in a future iteration
		if (pContext.getType() == TransactionType.REVERSAL) {
			log.warn("Need to build a DB load for method stage ");
			pContext.setMethodStage(null);
			return;
		}

		MethodStage methodStage = stageService.findByMethodAndStageNumberAndDeposit(pContext.getDomainMethod().getMethod(), pContext.getStage(), pContext.getType() == TransactionType.DEPOSIT);
		context.setMethodStage(methodStage);
		if ((methodStage == null) && (pContext.getStage() == 0)) {
			context.setState(DoMachineState.NEEDINPUT);
			incrementStage(pContext);
		}

		if (pContext.getMethodStage() == null) throw new DoErrorException("This method does not have " + context.getStage() + " stages.");
	}

	@Deprecated
	private void incrementStage() throws DoErrorException {
		incrementStage(context);
	}
	private void incrementStage(DoMachineContext pContext) throws DoErrorException {
		pContext.setStage(pContext.getStage() + 1);
		pContext.setInputFieldGroups(null);
		loadMethodStageFromDatabase(pContext);
	}

	@Deprecated
	private void findOrCreateTransaction(boolean allowMultipleWithdrawals) throws DoErrorException {
		findOrCreateTransaction(context, allowMultipleWithdrawals);
	}

	private void findOrCreateTransaction(DoMachineContext pContext, boolean allowMultipleWithdrawals) throws DoErrorException {
		Transaction t = pContext.getTransaction();

		if ((t == null) && (pContext.getRequest().getTransactionId() != null)) {
			t = service.findById(pContext.getRequest().getTransactionId());
			if (t == null) throw new DoErrorException("The referenced transaction does not exist");
		}

		if (t == null && !(pContext.getType() == TransactionType.WITHDRAWAL && allowMultipleWithdrawals)
				&& !pContext.isAllowMultipleTransactions()) {
			t = service.findLastPendingTransaction(pContext.getDomainMethod(), pContext.getUser());
		}

		if (t == null) {
			try {
				chooseProcessor();
				if ((pContext.getStage() == 1) && (!containsFieldNoAmount())) {
					String amountString = pContext.getInputFieldGroups().get("1").getFields().get("amount").getValue();
					TransactionAmountsData amountsData = service.calculateAmounts(amountString, Optional.ofNullable(pContext.getProcessor().getFees()).orElse(Fees.builder().build()));
					t = startTransaction(pContext.getSessionId(), pContext.getType(), pContext.getDomainMethod(), amountsData.getDepositAmountCents(), pContext.currencyCode(), pContext.getUser().getGuid(), null, pContext.getProcessor(), pContext.isDirectWithdraw(), pContext.getInitiationAuthor(), context.getLinkedTransaction());
				} else {
					t = startTransaction(pContext.getSessionId(), pContext.getType(), pContext.getDomainMethod(), null, pContext.currencyCode(), pContext.getUser().getGuid(), null, pContext.getProcessor(), pContext.isDirectWithdraw(), pContext.getInitiationAuthor(), context.getLinkedTransaction());
				}
			} catch (Exception e) {
				throw new DoErrorException(e.getMessage());
			}
		}
			pContext.setTransaction(t);
            handleWDonBalanceLimitRichedTag(pContext);
		}

    private void handleWDonBalanceLimitRichedTag(DoMachineContext pContext) {
        Transaction transaction = pContext.getTransaction();
        if (transaction != null
                && transaction.getDirectWithdrawal()
                && pContext.isBalanceLimitEscrow()
                && !transaction.hasTag(WD_ON_BALANCE_LIMIT_RICHED)) {
            service.addTagForTransaction(transaction, WD_ON_BALANCE_LIMIT_RICHED);
        }
    }

    private void forceStartTransaction(
			DoMachineContext pContext
	) {
		Transaction t = startTransaction(null, pContext.getType(), pContext.getDomainMethod(), null, pContext.currencyCode(), pContext.getUser().getGuid(), null, pContext.getProcessor(), pContext.isDirectWithdraw(), pContext.getInitiationAuthor(), context.getLinkedTransaction());
		pContext.setTransaction(t);
	}
	private DoStateFieldGroup createAmountFieldGroup() {
		DoStateFieldGroup amountGroup = new DoStateFieldGroup();
		amountGroup.setHeader("Amount");

		DoStateField amountField = DoStateField.builder()
				.code("amount")
				.description((context.getType() == TransactionType.DEPOSIT)?translate("SERVICE-CASHIER.DEPOSIT_AMOUNT_DESCRIPTION"):translate("SERVICE-CASHIER.WITHDRAWAL_AMOUNT_DESCRIPTION"))
				.name(translate("SERVICE-CASHIER.AMOUNT_FIELD_LABEL"))
				.type("amount")
				.readOnly(false)
				.value(service.getData(context.getTransaction(), "amount", 1, false))
				.sizeMd(6)
				.sizeXs(12)
				.extra(context.currencyCode())
				.build();

//		String productId = context.getRequest().getProductGuid();//service.getData(context.getTransaction(), "productId", 1, false);
		if (context.getProduct()!=null) {
			//go to svc-product to get pricing info.
			amountField.setReadOnly(true);
			amountField.setValue(context.getProduct().getCurrencyAmount().toPlainString());
			amountField.setExtra(context.getProduct().getCurrencyCode());
		}

		log.trace("Extra : "+amountField.getExtra());
		amountGroup.getFields().put("amount", amountField);
		return amountGroup;
	}

	private DoStateFieldGroup createBonusFieldGroup() {
		DoStateFieldGroup bonusGroup = new DoStateFieldGroup();
		bonusGroup.setHeader("Bonus");

		DoStateField bonusField = DoStateField.builder()
				.code("bonusCode")
				.description(translate("SERVICE-CASHIER.DEPOSIT_BONUSCODE_DESCRIPTION"))
				.name(translate("SERVICE-CASHIER.DEPOSIT_BONUSCODE_LABEL"))
				.type("bonusCode")
				.value(service.getData(context.getTransaction(), "bonusCode", 1, false))
				.required(false)
				.sizeMd(6)
				.sizeXs(12)
				.build();

		DoStateField bonusIdField = DoStateField.builder()
				.code("bonusId")
				.description(translate(""))
				.name(translate("SERVICE-CASHIER.DEPOSIT_BONUSID_LABEL"))
				.type("bonusId")
				.value(service.getData(context.getTransaction(), "bonusId", 1, false))
				.required(false)
				.sizeMd(6)
				.sizeXs(12)
				.build();

		bonusGroup.getFields().put("bonusCode", bonusField);
		bonusGroup.getFields().put("bonusId", bonusIdField);
		return bonusGroup;
	}

	private boolean containsFieldNoAmount() {
		MethodStage methodStage = context.getMethodStage();
		List<MethodStageField> fields = stageFieldService.findInputFieldsByMethodStage(methodStage);
		for (MethodStageField field: fields) {
			if (field.getType().equalsIgnoreCase("noamount")) return true;
		}
		return false;
	}

	private boolean populateMissingRequiredInputFields() throws DoErrorException {
		if (context.getStage() != 0) return false;
		Map<String, DoStateFieldGroup> ifg = context.getInputFieldGroups();
		if (ifg.get("0") == null) return false;
		Map<String, DoStateField> fields = ifg.get("0").getFields();
		if ((fields==null) || (fields.isEmpty())) return false;
		lithium.service.user.client.objects.User user = context.getExternalUser();
		log.debug("Populating Missing Required input fields for: "+user);
		try {
			fields.entrySet().removeIf(entry -> {
				log.debug("entry :: "+entry);
				DoStateField f = entry.getValue();
				String value = "";
				try {
					if (f.getCode().indexOf('.') == -1) {
						value = (PropertyUtils.getProperty(user, f.getCode())!=null)?PropertyUtils.getProperty(user, f.getCode())+"":"";
					} else {
						value = (PropertyUtils.getNestedProperty(user, f.getCode())!=null)?PropertyUtils.getNestedProperty(user, f.getCode())+"":"";
					}
				} catch (Exception e) {
					log.error("Could not get value for: "+f.getCode(), e);
					value = "";
				}
				if ((value != null) && (!value.isEmpty())) {
					log.debug("1field: "+f.getCode()+" value: "+value);
					f.setReadOnly(true);
					f.setValue(value);
					return false;
				} else {
					log.debug("2field: "+f.getCode()+" value: "+value);
					f.setReadOnly(false);
					return false;
				}
			});
			if (fields.isEmpty()) {
				return false;
			}

			Comparator<DoStateField> c = (f1, f2) -> {
				if (f1.getDisplayOrder()==null) f1.setDisplayOrder(999);
				if (f2.getDisplayOrder()==null) f2.setDisplayOrder(999);
				return f1.getDisplayOrder().compareTo(f2.getDisplayOrder());
			};
			Map<String, DoStateField> fieldsSorted = new LinkedHashMap<>();
			fields.entrySet()
					.stream()
					.sorted(Map.Entry.<String, DoStateField>comparingByKey())
					.sorted(Map.Entry.<String, DoStateField>comparingByValue(c))
					.forEachOrdered(x -> {
						fieldsSorted.put(x.getKey(), x.getValue());
						if ((x.getValue().getRequired()) && (x.getValue().getValue()==null || x.getValue().getValue().isEmpty())) fieldsSorted.put("hasEmptyRequiredField", null);
					});
			if (fieldsSorted.containsKey("hasEmptyRequiredField")) {
				fieldsSorted.remove("hasEmptyRequiredField");
				ifg.put("0", DoStateFieldGroup.builder().fields(fieldsSorted).build());
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

	private boolean validateDate(String dateString, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(false);
		try {
			sdf.parse(dateString);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	private boolean hasMissingRequiredInputFields() throws DoErrorException {
		if (context.getStage() != 0) return false;
		Map<String, DoStateFieldGroup> ifg = context.getInputFieldGroups();
		if (ifg.get("0") == null) return false;
		Map<String, DoStateField> fields = ifg.get("0").getFields();
		if ((fields==null) || (fields.isEmpty())) return false;

		Map<String, String> map = new HashMap<>();

		log.debug("fields : "+fields);
		fields.entrySet().forEach(entry -> {
			DoStateField field = entry.getValue();
			if (
					(!context.getRequest().getInputFieldGroups().isEmpty()) &&
							(context.getInputFieldGroups().get("0").getFields().get(field.getCode())!=null) &&
							(context.getRequest().getInputFieldGroups().get("0").getFields().get(field.getCode())!=null)
			) {
				context.getInputFieldGroups().get("0").getFields().get(field.getCode()).setValue(
						context.getRequest().getInputFieldGroups().get("0").getFields().get(field.getCode()).getValue()
				);
			}
			String value = context.getInputFieldGroups().get("0").getFields().get(field.getCode()).getValue();
			if ((value!=null) && (!value.isEmpty())) {
				map.put(field.getCode(), field.getValue());
				context.getInputFieldGroups().get("0").getFields().get(field.getCode()).setValueError(null);
//				PropertyUtils.setProperty(context.getExternalUser(), field.getCode(), field.getValue());
			} else {
				if (field.getRequired()) {
					map.put("hasMissingFields", "true");
					context.getInputFieldGroups().get("0").getFields().get(field.getCode()).setValueError("This field is required!");
				}
			}
		});

		String dobYear = (context.getInputFieldGroups().get("0").getFields().get("dobYear")!=null)?context.getInputFieldGroups().get("0").getFields().get("dobYear").getValue():null;
		String dobMonth = (context.getInputFieldGroups().get("0").getFields().get("dobMonth")!=null)?context.getInputFieldGroups().get("0").getFields().get("dobMonth").getValue():null;
		String dobDay = (context.getInputFieldGroups().get("0").getFields().get("dobDay")!=null)?context.getInputFieldGroups().get("0").getFields().get("dobDay").getValue():null;
		String dob = dobYear+"-"+dobMonth+"-"+dobDay;

		if (dob.indexOf("null") == -1) {
			if (!validateDate(dob, "yyyy-MM-dd")) {
				map.put("hasMissingFields", "true");
				context.getInputFieldGroups().get("0").getFields().get("dobDay").setValueError("Invalid Birthday!");
			}
		}

		boolean hasMissingFields = Boolean.parseBoolean(map.getOrDefault("hasMissingFields", "false"));
		if ((map.size() > 0) && (!hasMissingFields)) {
			try {
				map.put("id", context.getExternalUser().getId()+"");
				log.info("Saving missing required information! "+map);
				if (!updateUserOnUserService(map)) {
					return true;
				}
			} catch (Exception e) {
				log.error("", e);
			}
			return false;
		}
		return hasMissingFields;
	}

	private void populateStageInputFields() {
		if (context.getInputFieldGroups()==null) context.setInputFieldGroups(new HashMap<>());

		Integer stage = context.getStage();
		Integer groupId = 1;
		if (stage == 0) groupId = 0;

		MethodStage methodStage = context.getMethodStage();
		List<MethodStageField> fields = stageFieldService.findInputFieldsByMethodStage(methodStage);
//		Map<String, DoStateFieldGroup> inputFieldGroups = context.getInputFieldGroups();

		if ((stage == 1) && (!containsFieldNoAmount())) {
			context.getInputFieldGroups().put(groupId.toString(), createAmountFieldGroup());
			try {
				context.getInputFieldGroups().get(groupId.toString()).getFields().get("amount").setValue(
						context.getRequest().getInputFieldGroups().get(groupId.toString()).getFields().get("amount").getValue()
				);
			} catch (NullPointerException npe) {};
			groupId ++;
		}

		if (fields.size() > 0) {
			DoStateFieldGroup group = new DoStateFieldGroup();
			context.getInputFieldGroups().put(groupId.toString(), group);
			for (MethodStageField field: fields) {
				if (!field.getType().equalsIgnoreCase("noamount")) {
					group.getFields().put(
							field.getCode(),
							DoStateField.builder()
									.code(field.getCode())
									.description(translate(field.getDescription()))
									.name(translate(field.getName()))
									.sizeMd(field.getSizeMd())
									.sizeXs(field.getSizeXs())
									.required(field.getRequired())
									.displayOrder(field.getDisplayOrder())
									.value(service.getData(context.getTransaction(), field.getCode(), stage, false))
									.type(field.getType())
									.build()
					);
					try {
						context.getInputFieldGroups().get(groupId.toString()).getFields().get(field.getCode()).setValue(
								context.getRequest().getInputFieldGroups().get(groupId.toString()).getFields().get(field.getCode()).getValue()
						);
					} catch (NullPointerException npe) {};
				}
			}
		}

		if ((stage == 1) && (groupId == 2) && (context.getType() == TransactionType.DEPOSIT)) {
			groupId++;
			context.getInputFieldGroups().put(groupId.toString(), createBonusFieldGroup());
			try {
				context.getInputFieldGroups().get(groupId.toString()).getFields().get("bonusCode").setValue(
						context.getRequest().getInputFieldGroups().get(groupId.toString()).getFields().get("bonusCode").getValue()
				);
				context.getInputFieldGroups().get(groupId.toString()).getFields().get("bonusId").setValue(
						context.getRequest().getInputFieldGroups().get(groupId.toString()).getFields().get("bonusId").getValue()
				);
			} catch (NullPointerException npe) {};
		}
	}

	private void populateStageOutputFields() {
		if (context.getStage() <= 1) return;

		Integer stage = context.getStage() - 1;
		context.setOutputFields(new HashMap<>());

		MethodStage methodStage = stageService.findByMethodAndStageNumberAndDeposit(context.getDomainMethod().getMethod(), stage, context.getType() == TransactionType.DEPOSIT);
		List<MethodStageField> fields = stageFieldService.findOutputFieldsByMethodStage(methodStage);

		if (fields.size() > 0) {
			for (MethodStageField field: fields) {
				context.getOutputFields().put(field.getCode(), DoStateField.builder()
						.code(field.getCode())
						.description(translate(field.getDescription()))
						.name(translate(field.getName()))
						.sizeMd(field.getSizeMd())
						.sizeXs(field.getSizeXs())
						.value(service.getData(context.getTransaction(), field.getCode(), stage, false))
						.type(field.getType())
						.build());
				try {
					context.getOutputFields().get(field.getCode()).setValue(
							service.getData(context.getTransaction(), field.getCode(), stage, true)
					);
				} catch (NullPointerException npe) {};
			}
		}
	}

	private void sendStageInputFields() {
		context.getResponse().setInputFieldGroups(context.getInputFieldGroups());
		context.getResponse().setTitle(translate(context.getMethodStage().getTitle()));
		context.getResponse().setDescription(translate(context.getMethodStage().getDescription()));
	}

	private void sendStageOutputFields() {
		if (context.getStage() <= 1) return;
		context.getResponse().setOutputFields(context.getOutputFields());
	}

	//TODO : BonusCode Checks
	private boolean validateBonusCode() {
		if (context.getInputFieldGroups().keySet().contains("3")) {
			for (DoStateField field: context.getInputFieldGroups().get("3").getFields().values()) {
				if ((field.getCode().equals("bonusCode") || field.getCode().equals("bonusId"))
						&& (field.getValue() != null && !field.getValue().isEmpty())) {
					try {
						Long amountCents = 0L;
						for (String groupId: context.getInputFieldGroups().keySet()) {
							for (DoStateField f: context.getInputFieldGroups().get(groupId).getFields().values()) {
								if (f.getName().equalsIgnoreCase("amount")) {
									try {
										amountCents = CurrencyAmount.fromAmountString(f.getValue()).toCents();
									} catch (NumberFormatException e) {
										f.setValueError("Please enter a valid amount");
										return false;
									}
								}
							}
						}
						CasinoBonusClient client = serviceFactory.target(CasinoBonusClient.class);
						Response<Boolean> findBonus = client.checkDepositBonusValidForPlayer(
								CasinoBonusCheck.builder()
										.domainName(context.getUser().domainName())
										.bonusCode((field.getCode().equals("bonusCode"))? field.getValue(): null)
										.bonusId((field.getCode().equals("bonusId"))? Long.parseLong(field.getValue()): null)
										.playerGuid(context.getUser().guid())
										.depositCents(amountCents)
										.build()
						);
						if (!findBonus.isSuccessful()) {
							field.setValueError(findBonus.getData2().toString());
							return false;
						} else {
//							context.setBonusRevision(client.findByBonusRevisionId(findBonus.getData()).getData());
							return true;
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean validateUserCardReference() {
		for (DoStateFieldGroup fieldGroup: context.getInputFieldGroups().values()) {
			for (DoStateField f: fieldGroup.getFields().values()) {
				if ("cardReference".equalsIgnoreCase(f.getCode()) && f.getValue() != null) {
					try {
						String cardReference = f.getValue();
						ProcessorUserCard processorAccount = processorAccountServiceOld.validateUserCardReference(context.getUser().getGuid(), cardReference);
						if (nonNull(context.getTransaction())) {
							context.getTransaction().setPaymentMethod(processorAccount);
						} else {
							throw new Exception("Failed to set paymentMethod id" + processorAccount.getId() + " cardReference: " + cardReference + " . No transaction yet.");
						}
						if (!isActiveAccountStatus(PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()), context.getTransaction().getTransactionType() == TransactionType.DEPOSIT)) {
							throw new Exception("Failed to set paymentMethod id (" + processorAccount.getId() + " cardReference: " + cardReference + ") . It's not active. Status" + processorAccount.getStatus().getName());
						}
					} catch (Exception e) {
						f.setValueError("User card reference is invalid.");
						log.error("User card reference is invalid. " + e.getMessage(), e);
						return false;
					}
				} else if ("processorAccountId".equalsIgnoreCase(f.getCode()) && f.getValue() != null) {
					try {
						Long processorAccountId = Long.valueOf(f.getValue());
						ProcessorUserCard processorAccount = processorAccountService.getProcessorUserCardById(processorAccountId);
						if (isNull(processorAccount)) {
							throw new Exception("Not found processor account (" + processorAccountId + ")");
						}
						if (!processorAccount.getUser().guid().equals(context.getUser().getGuid())) {
							throw new Exception("Failed to set processor account (" + processorAccountId + ") . It's related to another user");
						}
						if (!context.isDirectWithdraw() && !isActiveAccountStatus(PaymentMethodStatusType.fromName(processorAccount.getStatus().getName()), context.getTransaction().getTransactionType() == TransactionType.DEPOSIT)) {
							throw new Exception("Failed to set processor account (" + processorAccountId + ") . It's not active. Status" + processorAccount.getStatus().getName());
						}
						Transaction transaction = context.getTransaction();
						if (nonNull(transaction)) {
							transaction.setPaymentMethod(processorAccount);
							service.update(transaction);
						} else {
							throw new Exception("Failed to set processor account (" + processorAccountId + ") . No transaction yet.");
						}
					} catch (Exception e) {
						f.setValueError("Processor account id is invalid");
						log.error("Processor account id. " + e.getMessage(), e);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean validateInputFields() {
		boolean valid = true;

		for (String groupId: context.getInputFieldGroups().keySet()) {
			for (DoStateField field: context.getInputFieldGroups().get(groupId).getFields().values()) {
				field.setValueError(null); //clear field error state for the new validation attempt. If this is not done, there could be false positives in retry attempts when processor validation also took place in a previous attempt.
				if ((field.getRequired()) && (field.getValue() == null || field.getValue().trim().isEmpty())) {
					field.setValueError("This is a required field");
					valid = false;
				}
			}
		}

		if ((context.getType() == TransactionType.DEPOSIT) && (context.getStage() == 1)) valid &= validateBonusCode();
		if (context.getStage() == 1) valid &= validateUserCardReference();

		if (!valid) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(translate("SERVICE-CASHIER.ERROR_FORM_VALIDATION"));
		}

		return valid;
	}

	private boolean validateProcessorLimits() {
		boolean valid = true;
		if (context.inApp()) {
			log.warn("InApp method used. Skipping limits checks.");
			return valid;
		}
		Limits limits = context.getProcessor().getLimits();

		Long maxAmount = Optional.ofNullable(limits.getMaxAmount(context.isFirstDeposit())).orElse(Long.MAX_VALUE);
		Long minAmount = Optional.ofNullable(limits.getMinAmount(context.isFirstDeposit())).orElse(Long.MIN_VALUE);
		for (String groupId: context.getInputFieldGroups().keySet()) {
			for (DoStateField field: context.getInputFieldGroups().get(groupId).getFields().values()) {
				if (field.getName().equalsIgnoreCase("amount") && !containsFieldNoAmount()) {
					try {
						Long amountCents = CurrencyAmount.fromAmountString(field.getValue()).toCents();
						if (amountCents < minAmount) {
							field.setValueError("Amount smaller than minimum amount");
							valid = false;
						} else if (amountCents > maxAmount) {
							field.setValueError("Amount larger than maximum amount");
							valid = false;
						}
					} catch (NumberFormatException e) {
						field.setValueError("Please enter a valid amount");
						valid = false;
					}
				}
			}
		}
		if (!valid) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(translate("SERVICE-CASHIER.ERROR_FORM_VALIDATION"));
		}
		return valid;
	}

	@Deprecated
	private void saveStageInputFields() {
		saveStageInputFields(context);
	}
	private void saveStageInputFields(DoMachineContext pContext) {
		for (String groupId: pContext.getInputFieldGroups().keySet()) {
			for (DoStateField field: pContext.getInputFieldGroups().get(groupId).getFields().values()) {
				service.setData(pContext.getTransaction(), field.getCode(), field.getValue(), pContext.getStage(), false);
			}
		}
	}

	//TODO:
	private void processProductPurchase() {
		productService.triggerProductPurchase(context);
	}

	private String reserveWithdrawalFunds(String status) throws DoErrorException, Status407TransactionInFinalStateException {
		// Translated error text is already set on the context. Stacktrace contains context.
		if (!context.isBalanceLimitEscrow() && !hasEnoughBalance(false)) throw new DoErrorException(INSUFFICIENT_BALANCE_MESSAGE);
		try {
			AdjustmentTransaction adjustment = cashierService.reserveWithdrawalFunds(
					context.getTransaction().getId(),
					context.getUser().domainName(),
					context.getUser().guid(),
					getAmountCents(),
					context.getTransaction().getCurrencyCode(),
					context.getTransaction().getSessionId(),
					context.isBalanceLimitEscrow()
			);
			Transaction transaction = service.saveWithdrawalReservedFundsAccRefs(
					context.getTransaction(),
					adjustment.getTransactionId(),
					null);
			context.setTransaction(transaction);
		} catch (Status415NegativeBalanceException ex) {
			log.error("Trying to adjust the customer account for reservation of withdrawal funds failed with negative balance exception: ", ex);
			throw new DoErrorException(INSUFFICIENT_BALANCE_MESSAGE);
		} catch (Exception e) {
			log.error("Trying to adjust the customer account for reservation of withdrawal funds failed: ", e);
			status = moveTransactionToPendingCancel(status, e.getMessage());
		}
		return status;
	}

	private boolean hasAccountingRefInWorkflow() {
		return service.hasAccountingRefInWorkflow(context.getTransaction());
	}

	private void addWorkflow(String status, boolean active, Long processDelay) throws DoErrorException, Status407TransactionInFinalStateException {
		TransactionWorkflowHistory workflowFrom = context.getTransaction().getCurrent();

		Long accountingReference = null;

		if (context.getType() == TransactionType.DEPOSIT) {
			if (!workflowFrom.getStatus().getCode().equals("SUCCESS")) {
				if (status == "SUCCESS") {
					try {
						if (context.hasProductGuid()) {
							log.info("Product Price : "+context.getProduct().getCurrencyAmount().toString());
							service.setData(context.getTransaction(), "amount", context.getProduct().getCurrencyAmount().toString(), 1, false);
						}

						accountingReference = processAccountingTransaction();

						if (context.getExternalUser().getReferrerGuid() != null && !context.getExternalUser().getReferrerGuid().isEmpty()) {
							referralService.triggerRAFConversion(context.getExternalUser().guid());
						}
						if (!context.getTransaction().isManual()) {
							context.getTransaction().getCurrent().setAccountingReference(accountingReference);
						}
					} catch (Exception e) {
						log.error("Trying to adjust the customer account failed: " + e.toString() + " Full Stacktrace: " + ExceptionUtils.getFullStackTrace(e));
						throw new DoErrorException("Trying to adjust the customer account failed: " + e.toString());
					}
					try {
						if (context.hasProductGuid()) {
							log.info("Product purchase request processing.");
							processProductPurchase();
						}
					} catch (Exception e) {
						log.error("Trying to send data to svc-product failed: " + e.toString(), e);
						throw new DoErrorException("Trying to send data to svc-product failed: " + e.toString());
					}
				}
			}
			String bonusCode = service.getData(context.getTransaction(), "bonusCode", 1, false);
			context.getTransaction().setBonusCode(bonusCode);
			String bonusIdStr = service.getData(context.getTransaction(), "bonusId", 1, false);
			if (bonusIdStr != null && !bonusIdStr.isEmpty()) {
				Long bonusId = -1L;
				try {
					bonusId = Long.parseLong(bonusIdStr);
				} catch (NumberFormatException nfe) {}
				if (bonusId != -1) {
					context.getTransaction().setBonusId(Long.parseLong(bonusIdStr));
					try {
						CasinoBonusClient client = serviceFactory.target(CasinoBonusClient.class);
						Response<BonusRevision> bonusResponse = client.findByBonusId(bonusId);
						if (bonusResponse.isSuccessful()) {
							context.getTransaction().setBonusCode(bonusResponse.getData().getBonusCode());
						}
					} catch (Exception e) {}
				}
			}
		} else {
			if (status.contentEquals(DoMachineState.WAITFORAPPROVAL.name()) ||
                    status.contentEquals(DoMachineState.AUTO_APPROVED_DELAYED.name()) ||
                    status.contentEquals(DoMachineState.APPROVED_DELAYED.name())
            ) {
				if (context.isBalanceLimitEscrow() || (context.reserveFundsOnWithdrawal() && !context.isWithdrawalFundsReserved())) {
					log.debug("About to reserve funds | " + context);
					status = reserveWithdrawalFunds(status);
				}
			}

			boolean feDialogRefresh = false;
			if (context.getSource().contentEquals("Customer") && !status.contentEquals(DoMachineState.PLAYER_CANCEL.name())) {
				feDialogRefresh = true;
			}

			if ((!feDialogRefresh) &&
					(context.isWithdrawalFundsReserved()) &&
					(status.contentEquals(DoMachineState.DECLINED.name()) ||
							status.contentEquals(DoMachineState.CANCEL.name()) ||
							status.contentEquals(DoMachineState.PLAYER_CANCEL.name()) ||
							status.contentEquals(DoMachineState.FATALERROR.name()) ||
							status.contentEquals(DoMachineState.EXPIRED.name()))
			) {
				if (hasAccountingRefInWorkflow()) {
					log.warn("Attempt made to reverse reserved withdrawal funds, but transaction already has an accounting reference, payout has already been done | " + context);
				} else if (context.getTransaction().getAccRefFromWithdrawalPending() != null) {
					log.warn("Attempt made to reverse reserved withdrawal funds, but it had already been reversed | " + context);
				} else {
					log.debug("About to reverse reserve funds | " + context);
					try {
						reverseReserveWithdrawalFunds();
					} catch (Exception ex1) {
						// Log error from failed reversal of reserved withdrawal funds.
						// Add a workflow and continue processing.
						// Alert internal recipient of error. Required email template: withdrawal.reservedfundsreversalfailure.internal

						log.error("Failed to reverse reserve withdrawal funds | " + context + ", " + ex1.getMessage(), ex1);

						status = moveTransactionToPendingCancel(status, ex1.getMessage());

						log.warn("Transaction remains in current state after failed refund : " + context);
					}
				}
			}

			if (!workflowFrom.getStatus().getCode().equals("APPROVED") && !workflowFrom.getStatus().getCode().equals("AUTO_APPROVED")) {
				if (status == "APPROVED" || status == "AUTO_APPROVED") {
					if (!context.isWithdrawalFundsReserved()) {
						status = reserveWithdrawalFunds(status);
					}
				}
			}

			if (!workflowFrom.getStatus().getCode().equals("SUCCESS")) {
				if (status == "SUCCESS") {
					try {
						accountingReference = processAccountingTransaction();
						context.getTransaction().getCurrent().setAccountingReference(accountingReference);
					} catch (Exception e) {
						throw new DoErrorException("Trying to adjust the customer account failed: " + e.toString() + " Full Stacktrace: " + ExceptionUtils.getFullStackTrace(e));
					}
				}
			}
		}

		if (context.getAuthor() != context.getUser()) {
			if (context.getType() == TransactionType.DEPOSIT) service.sendUserEventDeposit(context.getTransaction(), status, (context.getProcessorResponse()!=null)?context.getProcessorResponse().getMessage() : "", context.getDepositCount());
			if (context.getType() == TransactionType.WITHDRAWAL) service.sendUserEventWithdraw(context.getTransaction(), status, (context.getProcessorResponse()!=null)?context.getProcessorResponse().getMessage():"");
			context.addRawResponseLog("\r\n\r\nUserEvent sent to player :: UserEvent("+context.getTransaction()+", "+status+", "+((context.getProcessorResponse()!=null)?context.getProcessorResponse().getMessage():"")+")\r\n");
		}

		String billingDescriptor = null;
		if (context.getProcessorResponse() != null) {
			billingDescriptor = context.getProcessorResponse().getBillingDescriptor();
		}
		TransactionWorkflowHistory workflowTo = addWorkflowEntry(context.getTransaction(), userService.getSystemUser(), context.getProcessor(), status, active, context.getStage(), accountingReference, context.getSource(), billingDescriptor, processDelay);

		if (context.getProcessorResponse() != null) {
			saveSuccessProcessingAttempt(workflowFrom, workflowTo, context);
		} else if ((context.getResponse().getError()!=null) && (context.getResponse().getError())) {
			saveUnSuccessProcessingAttempt(workflowFrom, workflowTo, context);
		}

		if ((context.getType() == TransactionType.DEPOSIT || context.getType() == TransactionType.WITHDRAWAL) && (!workflowFrom.getStatus().getCode().equalsIgnoreCase(status))) {
			//Do our custom metrics here
			doMetricsCounterUpdates(context.getTransaction(), status);

			doWorkflowChangedComms(workflowFrom, status);
		}

		if (!workflowFrom.getStatus().getCode().equalsIgnoreCase(status) &&
				status.equalsIgnoreCase(DoMachineState.SUCCESS.name())) {
			autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder()
					.userGuid(context.getExternalUser().guid()).build());
		}
	}

	private void saveUnSuccessProcessingAttempt(TransactionWorkflowHistory workflowFrom, TransactionWorkflowHistory workflowTo, DoMachineContext context) {
		transactionProcessingAttemptService.saveProcessingAttempt(
				context.getTransaction(),
				false,
				context.getResponse().getErrorMessage(),
				JsonStringify.objectToString(context.getRequest()),
				JsonStringify.objectToString(context.getResponse()) + "\r\n" + context.getResponse().getStacktrace(),
				"",
				workflowFrom,
				workflowTo
		);
	}

	private void saveSuccessProcessingAttempt(TransactionWorkflowHistory workflowFrom, TransactionWorkflowHistory workflowTo, DoMachineContext context) {
		transactionProcessingAttemptService.saveProcessingAttempt(
				context.getTransaction(),
				true,
				context.getProcessorResponse().getMessage(),
				context.getProcessorResponse().getRawRequestLog(),
				context.getProcessorResponse().getRawResponseLog(),
				context.getProcessorResponse().getProcessorReference(),
				workflowFrom,
				workflowTo
		);
	}

	private String moveTransactionToPendingCancel(String status, String errorMessage) throws Status407TransactionInFinalStateException, DoErrorException {
		addWorkflowEntry(
				context.getTransaction(),
				context.getAuthor(),
				context.getProcessor(),
				status,
				true,
				context.getStage(),
				null,
				context.getSource(),
				null,
				null
		);

		context.setState(PENDING_CANCEL);

		DoResponse response = context.getResponse();

		if (response != null) {
			response.setError(true);
			response.setErrorMessage(errorMessage);
		}
		return PENDING_CANCEL.name();
	}

	private void doMetricsCounterUpdates(Transaction transaction, String status) {

		DomainMethod domainMethod = transaction.getDomainMethod();

		List<Tag> tags = new ArrayList<>(Arrays.asList(Tag.of("domain", domainMethod.getDomain().getName()),
				Tag.of("payment_method_code", domainMethod.getMethod().getCode()),
				Tag.of("payment_type", ofNullable(transaction.getTransactionPaymentType())
						.map(TransactionPaymentType::getPaymentType).orElse("none")),
				Tag.of("status", status)));

		Optional.of(transaction)
				.map(Transaction::getDeclineReason)
				.filter(not(String::isEmpty))
				.ifPresent(reason -> tags.add(Tag.of("decline_reason", trimReasonLength(reason))));
		lithiumMetricsService.counter(transaction.getTransactionType().name(), tags).increment();
	}

    private String trimReasonLength(String reason) {
        return reason.length() > DECLINE_REASON_METRIC_SIZE ? reason.substring(0, DECLINE_REASON_METRIC_SIZE) : reason;
    }

    private void reverseReserveWithdrawalFunds() throws Exception {
		if (context.getTransaction().getAccRefFromWithdrawalPending() != null) {
			return;
		}
		Long accountingReverseTransactionId = cashierService.getRelatedAccountingReverseTransactionId(context.getTransaction().getId());
		if (isNull(accountingReverseTransactionId)) {
			AdjustmentTransaction adjustment = cashierService.reverseReserveWithdrawalFunds(
					context.getTransaction().getId(),
					context.getUser().domainName(),
					context.getUser().guid(),
					getAmountCents(),
					context.getTransaction().getCurrencyCode(),
					context.getTransaction().getSessionId()
			);
			accountingReverseTransactionId = adjustment.getTransactionId();
		} else {
			log.info("Accounting reference of reversed withdraw (" + accountingReverseTransactionId + ") retrieved from internal accounting service and will be updated in cashier transaction (" + context.getTransaction().getId() + ")");
		}

		Transaction transaction = service.saveWithdrawalReservedFundsAccRefs(
				context.getTransaction(),
				null,
				accountingReverseTransactionId);
		context.setTransaction(transaction);
	}

	public void sendProcessorNotification(ProcessorNotificationData notificationData) {
		List<String> recipientList = new ArrayList<>(Arrays.asList(notificationData.getRecipientTypes()));
		try {
			if ((recipientList.stream().anyMatch(t -> t.equalsIgnoreCase(CashierMailService.RECIPIENT_PLAYER)))) {
				ProcessorCommunicationType processorCommunicationType = getProcessorCommunicationType();
				if (processorCommunicationType != ProcessorCommunicationType.MAIL || processorCommunicationType != ProcessorCommunicationType.ALL) {
					recipientList.remove(CashierMailService.RECIPIENT_PLAYER);

					//handle not mail player notification here
				}
			}
			cashierMailService.sendProcessorNotificationMail(notificationData,
					recipientList.toArray(new String[0]),
					context);
		} catch (Exception e) {
			log.error("Failed to send processor notification " + notificationData, e);
		}
	}
	private TransactionWorkflowHistory addWorkflowEntry(Transaction transaction, User author,
														DomainMethodProcessor processor, String status, boolean active,
														int stage, Long accountingReference, String source,
														String billingDescriptor, Long processDelay) throws Status407TransactionInFinalStateException, DoErrorException
	{
		Transaction savedTransaction = service.addWorkflowEntrySafe(transaction, author, processor, status, active, stage, accountingReference, source, billingDescriptor, processDelay);
		context.setTransaction(savedTransaction);
		return context.getTransaction().getCurrent();
	}

	private List<String> finalStateCodes() {
		List<String> finalStateCodes = Arrays.asList(DoMachineState.values())
				.stream()
				.filter(state -> !state.isActive())
				.map(state -> state.name())
				.collect(Collectors.toList());
		return finalStateCodes;
	}

	private void doWorkflowChangedComms(TransactionWorkflowHistory workflowFrom, String status) {
		// Always send internal workflow status change alerts to via mail system.
		// For players there is a choice depending on configuration (on processor). Send workflow status change alert to mail system, notification system, or both.

		// Internal workflow status change alert
		try {
			cashierMailService.sendDepositOrWithdrawalMail(
					status,
					new String[] { CashierMailService.RECIPIENT_INTERNAL },
					context
			);
		} catch (Exception e) {
			log.error("Failed to send internal workflow status change email | " + context, e);
		}

		// Player workflow status change alert

		ProcessorCommunicationType processorCommunicationType = getProcessorCommunicationType();

		// Mail system
		if (processorCommunicationType.id().compareTo(ProcessorCommunicationType.MAIL.id()) == 0 ||
				processorCommunicationType.id().compareTo(ProcessorCommunicationType.ALL.id()) == 0) {
			try {
				cashierMailService.sendDepositOrWithdrawalMail(
						status,
						new String[] { CashierMailService.RECIPIENT_PLAYER },
						context
				);
				context.addRawResponseLog("\r\n\r\n Mail sent to player for status change to : "+status+"\r\n");
			} catch (Exception e) {
				log.error("Failed to send workflow status change email | " + context, e);
			}
		}

		// Sms system
		if (processorCommunicationType.id().compareTo(ProcessorCommunicationType.SMS.id()) == 0 ||
				processorCommunicationType.id().compareTo(ProcessorCommunicationType.ALL.id()) == 0) {
			try {
				cashierSmsService.sendDepositOrWithdrawalOrReversalSms(
						status,
						new String[] { CashierMailService.RECIPIENT_PLAYER },
						context
				);
				context.addRawResponseLog("\r\n\r\n Sms sent to player for status change to : "+status+"\r\n");
			} catch (Exception e) {
				log.error("Failed to send workflow status change sms | " + context, e);
				context.addRawResponseLog("\r\n\r\n Failed to send workflow status change sms : "+status+" error: "+e.getMessage()+"\r\n");
			}
		}

		// Notification system
		if (processorCommunicationType.id().compareTo(ProcessorCommunicationType.NOTIFICATION.id()) == 0 ||
				processorCommunicationType.id().compareTo(ProcessorCommunicationType.ALL.id()) == 0) {
			try {
				cashierNotificationService.queuePlayerNotification(status, context);
				context.addRawResponseLog("\r\n\r\n Notification queued for player for status change to : "+status+"\r\n");
			} catch (Exception e) {
				log.error("Failed to queue workflow status change notification | " + context, e);
			}
		}

		// TODO: nthDepositMail is always sent via the mail system, possibly add notification option here too. Outside scope of https://playsafe.atlassian.net/browse/GM-732
		if (context.getType() == TransactionType.DEPOSIT && !workflowFrom.getStatus().getCode().equalsIgnoreCase(status) && status.equalsIgnoreCase("SUCCESS")) {
			try {
				cashierMailService.sendNthDepositMail(context);
				context.addRawResponseLog("\r\n\r\n Mail sent to player for successful deposit.\r\n");
			} catch (Exception e) {
				log.error("Failed to send nth deposit email | " + context, e);
			}
			try {
				cashierMailService.sendAdjectiveSuccessfulDepositEmail(status, context);
				context.addRawResponseLog("\r\n\r\n Adjective Email sent to player for successful deposit.\r\n");
			} catch (Exception e) {
				log.error("Failed to send Adjective deposit email | " + context, e);
				context.addRawResponseLog("\r\n\r\n Failed to send Adjective  deposit email. Error: " + e.getMessage() + "\r\n");
			}
		}
		if (context.getType() == TransactionType.DEPOSIT && !workflowFrom.getStatus().getCode().equalsIgnoreCase(status) && status.equalsIgnoreCase("SUCCESS")) {
			try {
				cashierSmsService.sendNthDepositSms(context);
				context.addRawResponseLog("\r\n\r\n Sms sent to player for successful deposit.\r\n");
			} catch (Exception e) {
				log.error("Failed to send nth deposit sms | " + context, e);
				context.addRawResponseLog("\r\n\r\n Failed to send nth deposit sms. Error: " + e.getMessage() + "\r\n");
			}

			try {
				cashierSmsService.sendAdjectiveSuccessfulDepositSms(status, context);
				context.addRawResponseLog("\r\n\r\n Adjective Sms sent to player for successful deposit.\r\n");
			} catch (Exception e) {
				log.error("Failed to send Adjective deposit sms | " + context, e);
				context.addRawResponseLog("\r\n\r\n Failed to send Adjective  deposit sms. Error: " + e.getMessage() + "\r\n");
			}
		}

	}

	/**
	 * Add a workflow item to the original transaction but only affects the reversal status and stages.
	 * The original context will have workflow items added but no state modifications will take place.
	 *
	 * @param reversalContextState
	 * @param originalContextState
	 * @throws DoErrorException
	 */
	// If this were to form part of the original workflow it would cause problems for state management.
	private void addReversalWorkflow(DoMachineState reversalContextState, DoMachineState originalContextState) throws DoErrorException, Status407TransactionInFinalStateException {
		String status = reversalContextState.name();
		boolean active = reversalContextState.isActive();
		TransactionWorkflowHistory workflowReversalFrom = reversalContext.getTransaction().getCurrent();
		TransactionWorkflowHistory workflowOriginalFrom = context.getTransaction().getCurrent();

		Long accountingReference = null;

		if (reversalContext.getType() == TransactionType.REVERSAL) {
			if (!workflowReversalFrom.getStatus().getCode().equals("SUCCESS")) {
				if (status == "SUCCESS") {
					try {
						accountingReference = processAccountingReversalTransaction();
						if (!reversalContext.getTransaction().isManual())
							reversalContext.getTransaction().getCurrent().setAccountingReference(accountingReference);
					} catch (Exception e) {
						log.error("Trying to adjust the customer account failed: " + e.toString(), e);
						throw new DoErrorException("Trying to adjust the customer account failed: " + e.toString());
					}
				}
			}
		}

		//If there is a status change send a mail to support
		if (reversalContext.getType() == TransactionType.REVERSAL && (!workflowReversalFrom.getStatus().getCode().equalsIgnoreCase(status))) {
			try {
				cashierMailService.sendDepositOrWithdrawalOrReversalMail(
						status,
						new String[] { CashierMailService.RECIPIENT_INTERNAL },
						reversalContext
				);
				reversalContext.addRawResponseLog("\r\n\r\n Mail sent to support user for status change to : "+status+"\r\n");
			} catch (Exception e) {
				log.warn("Failed to send workflow status change email | " + reversalContext);
			}
		}

		//Add workflow
		TransactionWorkflowHistory workflowReversalTo = addWorkflowEntry(reversalContext.getTransaction(), reversalContext.getAuthor(), reversalContext.getProcessor(), status, active, reversalContext.getStage(), accountingReference, reversalContext.getSource(), null, null);
		TransactionWorkflowHistory workflowOriginalTo = addWorkflowEntry(context.getTransaction(), context.getAuthor(), context.getProcessor(), originalContextState.name(), originalContextState.isActive(), context.getStage(), accountingReference, context.getSource(), null, null);

		//Save processing attempt or processing attempt error
		if (reversalContext.getProcessorResponse() != null) {
			saveSuccessProcessingAttempt(workflowReversalFrom, workflowReversalTo, reversalContext);
		} else if ((reversalContext.getResponse().getError()!=null) && (context.getResponse().getError())) {
			saveUnSuccessProcessingAttempt(workflowReversalFrom, workflowReversalTo, reversalContext);
		}
	}

	/**
	 * Adds a processing attempt with the exception that got thrown by the processor processing attempt
	 * @param message Message to be displayed on the workflow entry
	 * @param errorStacktraceAndInfo String containing relevant information about the problem
	 */
	private void addRetryOrNextStageWorkflow(String message, String errorStacktraceAndInfo) throws Status407TransactionInFinalStateException, DoErrorException {
		String status = context.getState().name();
		boolean active = context.getState().isActive();
		TransactionWorkflowHistory workflowOriginalFrom = context.getTransaction().getCurrent();

		Long accountingReference = null;

		//Add workflow
		TransactionWorkflowHistory workflowOriginalTo = addWorkflowEntry(context.getTransaction(), context.getAuthor(), context.getProcessor(), status, active, context.getStage(), accountingReference, context.getSource(), null, null);

		//Save processing attempt or processing attempt error
		transactionProcessingAttemptService.saveProcessingAttempt(
				context.getTransaction(),
				false,
				message,
				JsonStringify.objectToString(context.getRequest()),
				errorStacktraceAndInfo,
				"",
				workflowOriginalFrom,
				workflowOriginalTo
		);
	}


	private void processBonusRegistration(String bonusCode, String userEventId) {
		CasinoBonus casinoBonus = CasinoBonus.builder()
				.bonusCode(bonusCode)
				.playerGuid(context.getUser().guid())
				.userEventId(Long.parseLong(userEventId))
				.build();
		try {
			CasinoBonusClient client = serviceFactory.target(CasinoBonusClient.class);
//			Response<Long> response =
			client.registerForDepositBonus(casinoBonus);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private void processBonusRegistration(Long bonusId, String userEventId) {
		CasinoBonus casinoBonus = CasinoBonus.builder()
				.bonusId(bonusId)
				.playerGuid(context.getUser().guid())
				.userEventId(Long.parseLong(userEventId))
				.build();
		try {
			CasinoBonusClient client = serviceFactory.target(CasinoBonusClient.class);
			client.registerForDepositBonusById(casinoBonus);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private Long processAccountingTransaction() throws Exception {
		//TODO If this fails we have a serious inconcistency. Cashier will show success but player won't have funds. We need to handle correctly. At the moment this
		//     will go out as a DoErrorException which means the status will be flagged as a fatal unhandled error, but is that enough?
		//TODO The amount should be parsed and handled better.

		String amountString = service.getData(context.getTransaction(), "amount", 1, false);
		if(amountString==null ) {
			amountString = service.getData(context.getTransaction(), "amount", 1, true);
		}
		Long amountCents = CurrencyAmount.fromAmountString(amountString).toCents();

		if (context.getType() == TransactionType.DEPOSIT) {
			Fees fees = Optional.ofNullable(context.getProcessor().getFees()).orElse(Fees.builder().build());
			Response<Long> response = cashierService.processDeposit(
					amountCents,
					context.getUser().domainName(),
					context.getUser().getGuid(),
					context.getTransaction().getId().toString(),
					context.getProcessor().getProcessor().getCode(),
					context.getProcessor().getDomainMethod().getMethod().getCode(),
					context.currencyCode(),
					fees,
					context.getProcessor().getId(),
					context.getTransaction().getProcessorReference(),
					context.getTransaction().getAdditionalReference(),
					context.getProcessor().getDescription(),
					context.getExternalUser().getCreatedDate(),
					context.getTransaction().getSessionId(),
					ofNullable(context.getTransaction().getPaymentMethod()).map(ProcessorUserCard::getId).orElse(null)
			);
			if (response.isSuccessful()) {
				if (context.getTransaction().getAmountCents() == null || context.getTransaction().getFeeCents() == null) {
					updateContextTransactionWithAmountsCents(context);
				}
				Transaction transaction = context.getTransaction();

				((Map<String,String>)response.getData2()).forEach((k, v) -> {
					if (k.equalsIgnoreCase("depositCount")) {
						context.setDepositCount(Long.valueOf(v));
					}
					service.setData(transaction, k, v, context.getStage(), true);
				});
				String userEventId = service.getData(transaction, "userEventId", context.getStage(), true);
				String bonusIdStr = service.getData(transaction, "bonusId", 1, false);
				if (bonusIdStr != null && !bonusIdStr.isEmpty()) {
					Long bonusId = -1L;
					try {
						bonusId = Long.parseLong(bonusIdStr);
					} catch (NumberFormatException nfe) {}
					if (bonusId != -1)
						processBonusRegistration(bonusId, userEventId);
				} else {
					String bonusCode = service.getData(transaction, "bonusCode", 1, false);
					if (bonusCode != null && !bonusCode.isEmpty())
						processBonusRegistration(bonusCode, userEventId);
				}
			}
			return response.getData();
		}
		if (context.getType() == TransactionType.WITHDRAWAL) {
			log.info("Processing payout");
			Fees fees = Optional.ofNullable(context.getProcessor().getFees()).orElse(Fees.builder().build());
			Response<Long> response = cashierService.processPayout(
					amountCents,
					context.getUser().domainName(),
					context.getUser().getGuid(),
					context.getTransaction().getId().toString(),
					context.getProcessor().getProcessor().getCode(),
					context.getProcessor().getDomainMethod().getMethod().getCode(),
					context.currencyCode(),
					fees,
					context.getProcessor().getId(),
					context.getTransaction().getProcessorReference(),
					context.getTransaction().getAdditionalReference(),
					context.getProcessor().getDescription(),
					context.getTransaction().getAccRefToWithdrawalPending(),
					context.getTransaction().getAccRefFromWithdrawalPending(), // If this is not null, it means the funds were transferred back to pb
					context.getTransaction().getSessionId(),
					ofNullable(context.getTransaction().getPaymentMethod()).map(ProcessorUserCard::getId).orElse(null)
			);
			if (response.isSuccessful()) {
				//I assume it was a success
				//Yes, this is horrible
				TransactionAmountsData transactionAmountsData = service.calculateAmounts(amountString, fees);
				Transaction transaction = context.getTransaction();
				transaction.setAmountCents(amountCents);
				transaction.setFeeCents(transactionAmountsData.getFeeAmountCents());
				((Map<String, String>) response.getData2()).forEach((k, v) -> {
					service.setData(transaction, k, v, context.getStage(), true);
				});
			}
			return response.getData();
		}
		throw new Exception("Invalid transactionType " + context.getType());
	}

	/**
	 * Check if the reversal context is populated and perform a reversal accounting call to either a deposit or payout
	 * @return
	 * @throws Exception
	 */
	private Long processAccountingReversalTransaction() throws Exception {
		if (reversalContext != null && reversalContext.getType() == TransactionType.REVERSAL) {
			Response<Long> response = null;
			if (context.getType() == TransactionType.DEPOSIT) {
				response = cashierService.processDepositReversal(
						context.getTransaction().getId().toString(),
						reversalContext.getUser().domainName(),
						reversalContext.getUser().getGuid(),
						reversalContext.getTransaction().getId().toString(),
						reversalContext.getProcessor().getProcessor().getCode(),
						reversalContext.getProcessor().getDomainMethod().getMethod().getCode(),
						reversalContext.currencyCode(),
						null, // FIXME: 2019/07/31 Add functions to extract fees for reversals
						reversalContext.getProcessor().getId());
			}

			if (context.getType() == TransactionType.WITHDRAWAL) {
				response = cashierService.processPayoutReversal(
						context.getTransaction().getId().toString(),
						reversalContext.getUser().domainName(),
						reversalContext.getUser().getGuid(),
						reversalContext.getTransaction().getId().toString(),
						reversalContext.getProcessor().getProcessor().getCode(),
						reversalContext.getProcessor().getDomainMethod().getMethod().getCode(),
						reversalContext.currencyCode(),
						null, // FIXME: 2019/07/31 Add functions to extract fees for reversals
						reversalContext.getProcessor().getId());
			}

			if (response != null && response.isSuccessful()) {
				return response.getData();
			}
		}
		throw new Exception("Invalid transactionType for " + reversalContext.getType());
	}


	private void findAndSetAmountDifference() {
		for (DomainMethodProcessorProperty prop: dmpService.propertiesWithDefaults(context.getProcessor().getId())) {
			if ((prop.getProcessorProperty().getName().equalsIgnoreCase("amountdiff")) && (prop.getValue()!=null)) {
				context.setAmountDifference(new BigDecimal(prop.getValue()));
			}
		}
	}

	private ProcessorCommunicationType getProcessorCommunicationType() {
		ProcessorCommunicationType processorCommunicationType = ProcessorCommunicationType.MAIL;

		if (context.getProcessor() == null) return processorCommunicationType;

		for (DomainMethodProcessorProperty prop: dmpService.propertiesWithDefaults(context.getProcessor().getId())) {
			if ((prop.getProcessorProperty().getName().equalsIgnoreCase("alertCommService")) && (prop.getValue()!=null)) {
				try {
					processorCommunicationType = ProcessorCommunicationType.fromId(Integer.parseInt(prop.getValue()));
				} catch (NumberFormatException nfe) {
					processorCommunicationType = ProcessorCommunicationType.MAIL;
				}
			}
		}
		if (processorCommunicationType == null) processorCommunicationType = ProcessorCommunicationType.MAIL;
		return processorCommunicationType;
	}

	//TODO we need to use better algorithm to choose processor than random.
	private void chooseProcessor() throws DoErrorException {
		if (context.getTransaction() != null && context.getTransaction().getCurrent().getProcessor() != null) {
			context.setProcessor(context.getTransaction().getCurrent().getProcessor());
			return;
		}

		List<DomainMethodProcessor> processors = frontendService.domainMethodProcessors(context.getDomainMethod().getId(), context.getUser(), context.getUserRequest().getIpAddr(), context.getUserRequest().userAgent());
		if (processors.isEmpty()) throw new DoErrorException("No processors for this method");
//		int processorIndex = new Random().nextInt(processors.size());
//		DomainMethodProcessor p = processors.get(processorIndex);
		DomainMethodProcessor p = processors.get(0);
		context.setProcessor(p);
		context.setProcessorUser(userService.findProcessorUser(context.getUser(), context.getProcessor()));
	}

	private boolean updateUserOnUserService(
//		lithium.service.user.client.objects.User user
			Map<String, String> user
	) throws Exception {
		UserApiInternalClient userClient = serviceFactory.target(UserApiInternalClient.class, "service-user", true);
		Response<lithium.service.user.client.objects.User> userSave = userClient.updateUser(user);
		if (!userSave.isSuccessful()) {
			context.getResponse().setError(true);
			context.getResponse().setErrorMessage(translate("SERVICE-CASHIER.ERROR_CUSTOMER_DETAIL_UPDATE"));
			return false;
		}
		return true;
	}

	private lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService() throws Exception {
		return userService.retrieveDomainFromDomainService(context.getUser().domainName());
	}

	private lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService(String domainName) throws Exception {
		return userService.retrieveDomainFromDomainService(domainName);
	}
	@Deprecated
	private DoProcessorRequest createProcessorRequest() throws Exception {
		return createProcessorRequest(context);
	}

	private DoProcessorRequest createProcessorRequest(DoMachineContext pContext) throws Exception {
		HashMap<String, String> properties = new HashMap<>();
		for (DomainMethodProcessorProperty prop: dmpService.propertiesWithDefaults(pContext.getProcessor().getId())) {
			properties.put(prop.getProcessorProperty().getName(), prop.getValue());
		}

		lithium.service.user.client.objects.User u = pContext.getExternalUser();
		DoProcessorRequestUser user = DoProcessorRequestUser.builder()
				.cellphoneNumber(u.getCellphoneNumber())
				.dobDay(u.getDobDay())
				.dobMonth(u.getDobMonth())
				.dobYear(u.getDobYear())
				.domain(pContext.getExternalDomain().getName())
				.email(u.getEmail())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.postalAddress(u.getPostalAddress())
				.residentialAddress(u.getResidentialAddress())
				.socialSecurityNumber(u.getSocialSecurityNumber())
				.telephoneNumber(u.getCellphoneNumber())
				.username(u.getUsername())
				.currency(pContext.currencyCode())
				.locale(pContext.getExternalDomain().getDefaultLocale())
				.language(pContext.getExternalDomain().getDefaultLocale().split("-")[0])
				.createdDate(u.getCreatedDate())
				.shortGuid(u.getShortGuid())
				.realGuid(u.guid())
				.countryCode(u.getCountryCode())
				.iban(u.getLabelAndValue() != null ? u.getLabelAndValue().get("iban") : null)
				.gender(u.getGender())
				.id(u.getId())
				.lastNamePrefix(u.getLastNamePrefix())
				.build();

		if (u.getLastLogin() != null) {
			user.setOs(u.getLastLogin().getOs());
			user.setBrowser(u.getLastLogin().getBrowser());
			user.setLastKnownIP(u.getLastLogin().getIpAddress());
			user.setLastKnownUserAgent(u.getLastLogin().getUserAgent());
		}

		if (pContext.getUserRequest() != null) {
			user.setLastKnownIP(pContext.getUserRequest().getIpAddr());
			user.setLastKnownUserAgent(pContext.getUserRequest().userAgent());
		}
		try {
			String lastKnownIP = user.getLastKnownIP();
			int indexOf = lastKnownIP.indexOf(",");
			if (indexOf != -1) lastKnownIP = lastKnownIP.substring(0, indexOf);
			user.setLastKnownIP(lastKnownIP);
		} catch (Exception e) {}

		DoProcessorRequest processorRequest = DoProcessorRequest.builder()
				.transactionId(pContext.getTransaction().getId())
				.transactionType(pContext.getType())
				.user(user)
				.properties(properties)
				.inputData(service.getData(pContext.getTransaction(), false))
				.outputData(service.getData(pContext.getTransaction(), true))
				.methodCode(pContext.getDomainMethod().getMethod().getCode())
				.stage(pContext.getStage())
				.mobile(pContext.getRequest().getMobile())
				.previousProcessorRequest(pContext.getProcessorCallbackRequest())
				.processorUserId(pContext.getProcessorUser() != null ? pContext.getProcessorUser().getProcessorUserId() : null)
				.processorReference(pContext.getTransaction().getProcessorReference())
				.additionalReference(pContext.getTransaction().getAdditionalReference())
				.processorAccount(processorAccountService.processorAccountFromEntity(pContext.getTransaction().getPaymentMethod(), true))
				.transactionFinalized(isInFinalState(pContext))
				.transactionExpired(pContext.isTransactionExpired())
				.build();

		return processorRequest;
	}

	@Deprecated
	private DoProcessorResponseStatus callProcessor() throws DoErrorException, Status407TransactionInFinalStateException {
		return callProcessor(context);
	}

	private DoProcessorResponseStatus callProcessor(DoMachineContext pContext) throws DoErrorException, Status407TransactionInFinalStateException {
		try {
			DoProcessorClient client = serviceFactory.target(DoProcessorClient.class, pContext.getProcessor().getProcessor().getUrl(), true);

			DoProcessorRequest processorRequest = createProcessorRequest(pContext);

			DoProcessorResponse processorResponse = client.doPost(processorRequest);
			//Retry logic
			if (processorResponse.getStatus() == DoProcessorResponseStatus.REMOTE_FAILURE_AUTO_RETRY) {
				if (pContext.getTransaction().getTransactionType() == TransactionType.WITHDRAWAL) {
					addRetryOrNextStageWorkflow("Problem in processing of the transaction. See log for additional info", processorResponse.getRawResponseLog());
					service.saveDoRetryTransaction(pContext.getTransaction().getId(), true);
					log.debug("callProcessor request retry logic executing" + processorRequest + " response " + processorResponse);

					return DoProcessorResponseStatus.NOOP;
				}
			}
			pContext.setProcessorResponse(processorResponse);
			pContext.setProcessorUser(userService.findOrCreateProcessorUser(processorResponse.getProcessorUserId(), pContext.getUser(), pContext.getProcessor()));
			log.debug("callProcessor request " + processorRequest + " response " + processorResponse);

			saveProcessorResponseOutputData(pContext);

			updateTtl(pContext);

			handleInputErrorResponse(pContext);

			return processorResponse.getStatus();
		} catch (Throwable e) {
			log.error("callProcessor failed: " + e.getMessage(), e);
			// Catchall retry logic
			if (pContext.getTransaction().getTransactionType() == TransactionType.WITHDRAWAL) {
				addRetryOrNextStageWorkflow("Problem in processing of the transaction. See log for additional info", ExceptionUtils.getStackTrace(e));
				service.saveDoRetryTransaction(pContext.getTransaction().getId(), true);
				return DoProcessorResponseStatus.NOOP;
			} else {
				throw new DoErrorException("Unhandled error while communicating with processor: " + e.getMessage(), e);
			}
		}
	}

	@Deprecated
	private void handleInputErrorResponse() throws Exception {
		handleInputErrorResponse(context);
	}
	/**
	 * Check if response was for an input error, if this is the case, read output data and modify input field error states accordingly.
	 * Persist input field error values to db.
	 */
	private void handleInputErrorResponse(DoMachineContext pContext) throws Exception {
		if (pContext.getProcessorResponse() == null) throw new Exception("No processor response to process.");

		if (pContext.getProcessorResponse().getStatus() != DoProcessorResponseStatus.INPUTERROR) return;

		pContext.getResponse().setErrorMessage(pContext.getProcessorResponse().getMessage());
		pContext.getResponse().setError(true);
		if (nonNull(pContext.getProcessorResponse().getDeclineReason()) && !pContext.getProcessorResponse().getDeclineReason().isEmpty()) {
			pContext.getResponse().setDeclineReason(pContext.getProcessorResponse().getDeclineReason());
		}

		if (pContext.getProcessorResponse().getOutputData() != null) {
			for (Integer stage: pContext.getProcessorResponse().getOutputData().keySet()) {
				for (String key: pContext.getProcessorResponse().getOutputData().get(stage).keySet()) {
					String outputFieldValue = pContext.getProcessorResponse().getOutputData().get(stage).get(key);
					if (outputFieldValue == null) continue;
					boolean foundMatchingInput = false;
					for (String inputKey: pContext.getInputFieldGroups().keySet()) {
						if (foundMatchingInput) continue;
						DoStateField field = pContext.getInputFieldGroups().get(inputKey).getFields().get(key);
						if (field != null) {
							field.setValueError(pContext.getProcessorResponse().getOutputData().get(stage).get(key));
							foundMatchingInput = true;
						}
					}
					if (!foundMatchingInput)
						log.warn("There is an input error coming from the processor on field: " + key + " at stage: " + stage + " with value: " + outputFieldValue + " but no input field key that matches.");
				}
			}
			saveStageInputFields(pContext);
		}
	}

	@Deprecated
	private void updateTtl() {
		updateTtl(context);
	}
	private void updateTtl(DoMachineContext pContext) {
		DoProcessorResponse processorResponse = pContext.getProcessorResponse();
		//Long ttl = service.getTtl(context.getTransaction().getId());
		if (processorResponse == null) return;
		if ((processorResponse.getRemoveTtl()!=null) && (processorResponse.getRemoveTtl())) {
			service.updateTtl(pContext.getTransaction().getId(), -1L);
			return;
		}
		if (processorResponse.getExpiryDate() == null) return;

		Long createdOn = pContext.getTransaction().getCreatedOn().getTime();
		Long expiry = processorResponse.getExpiryDate().getMillis();

		if (expiry != null) service.updateTtl(pContext.getTransaction().getId(), (expiry-createdOn));
	}

	private String getAccountInfo() {
		String accountInfo = service.getData(context.getTransaction(), "account_info", 1, false);
		if (accountInfo==null) accountInfo = service.getData(context.getTransaction(), "account_info", 1, true);
		if (accountInfo==null) accountInfo = service.getData(context.getTransaction(), "account_info", 2, false);
		if (accountInfo==null) accountInfo = service.getData(context.getTransaction(), "account_info", 2, true);

		if (accountInfo==null) accountInfo = "";
		//if (context.getTransaction()!=null) context.getTransaction().setAccountInfo(accountInfo);
		return accountInfo;
	}


	@Deprecated
	private void saveProcessorResponseOutputData() throws Exception {
		saveProcessorResponseOutputData(context);
	}

	private void saveProcessorResponseOutputData(DoMachineContext pContext) throws Exception {
		DoProcessorResponse processorResponse = pContext.getProcessorResponse();
		if (processorResponse == null) throw new Exception("No processor response to process.");
		if (processorResponse.getOutputData() != null)
			for (Integer stage: processorResponse.getOutputData().keySet())
				for (String key: processorResponse.getOutputData().get(stage).keySet())
					service.setData(pContext.getTransaction(), key,
							processorResponse.getOutputData().get(stage).get(key), stage, true);
		defineIframeData(pContext, processorResponse);

		if (processorResponse.getProcessorAccount() != null && context.getProcessor() != null && context.getTransaction().getPaymentMethod() == null) {
			//update processor account from response with verification status
			verifyProcessorAccount(processorResponse, processorResponse.getProcessorAccount());
			processorAccountService.addTransactionRemark(context.getTransaction(), processorResponse.getProcessorAccount());
			if (processorResponse.getProcessorAccount().shouldSave()) {
				processorAccountService.saveProcessorAccount(context.getUser(), context.getProcessor(), processorResponse.getProcessorAccount(), processorResponse.getUpdateProcessorAccount());
			}
			if (context.getTransaction().getCurrent().getStatus().getCode().equals("SUCCESS")) {
				processorAccountService.addProcessorAccountLabel(context.getTransaction(), processorResponse.getProcessorAccount());
				//check processor account restrictions
				autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder()
					.userGuid(context.getExternalUser().guid()).build());
			}
			processorResponse.setPaymentMethodId(processorResponse.getProcessorAccount().getId());
		}

		if (processorResponse.getRemark() != null) {
			transactionService.addTransactionRemark(context.getTransaction().getId(), processorResponse.getRemark().getRemark(), processorResponse.getRemark().getType());
		}

		context.setTransaction(service.updateTransactionFromProcessorResponse(context.getTransaction().getId(), processorResponse, getAccountInfo()));

		pContext.getResponse().setErrorMessage(processorResponse.getMessage());
		
		if (processorResponse.getDeclineReason() != null) {
			pContext.getTransaction().setDeclineReason(processorResponse.getDeclineReason());
		}

		if (processorResponse.getNotificationData() != null) {
			sendProcessorNotification(processorResponse.getNotificationData());
		}
	}
	private boolean verifyInputProcessorAccount() {

		if(context.isDirectWithdraw() || context.getTransaction() == null) {
			return true;
		}

		if (context.getTransaction().getPaymentMethod() == null) {
			return checkProcessorAccountsCount();
		}

		ProcessorAccount  processorAccount = processorAccountService.processorAccountFromEntity(context.getTransaction().getPaymentMethod(), true);

		try {
			//do we need to recheck unverified accounts?
			//do we need to update processor account with validation status
			//this will recheck all uk accounts
			//if (processorAccount.getVerified() == null) {
			//	processorAccount = processorAccountVerifyService.verifyProcessorAccount(context.getUser(), processorAccount, context.getProcessor(), false);
			//	if (processorAccount.getVerified() != null) {
			//		processorAccountService.updateProcessorAccount(processorAccount.getId(), null, null, null, null, processorAccount.getVerified(), processorAccount.getFailedVerification(), null, null, "Update processor account verification status", null);
			//	}
			//}
			if (BooleanUtils.isFalse(processorAccount.getVerified())) {
				DoResponse response = getResponse();
				ProcessorAccountVerificationType failedVerification = processorAccount.getFailedVerification();
				response.setError(true);
				response.setErrorMessage(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, context.getDomainMethod().getName()));
				context.getTransaction().setDeclineReason(getError(CASHIER_INVALID_ACCOUNT) + ": " + failedVerification.getDescription());
				log.error("Account is invalid. Verification: " + processorAccount.getFailedVerification() + " Processor account: " + processorAccount);
				return false;
			}
		} catch (Exception ex) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(GeneralError.INVALID_PROCESSOR_ACCOUNT.getResponseMessageLocal(messageSource, context.getDomainMethod().getName()));
			context.getTransaction().setDeclineReason(getError(CASHIER_INVALID_ACCOUNT)  + ": " + ": verification is failed");
			log.error("Account is invalid. Verification is failed. Processor account: " + processorAccount + " Exception: " + ex.getMessage(), ex);
			return false;
		}
		return true;
	}
	private boolean checkProcessorAccountsCount() {
		if (context.getType() == TransactionType.DEPOSIT && !processorAccountVerifyService.checkProcessorAccountsCount(context.getUser(), context.getProcessor())) {
			DoResponse response = getResponse();
			response.setError(true);
			response.setErrorMessage(GeneralError.REACHED_MAX_ACCOUNT_COUNT.getResponseMessageLocal(messageSource, context.getDomainMethod().getName()));
			context.getTransaction().setDeclineReason(getError(CASHIER_MAX_ACCOUNT_COUNT));
			log.error("Maximum number of active accounts is reached for user: " + context.getUser().getGuid() + " Processor: " + context.getProcessor().getDescription());
			return false;
		}
		return true;
	}

	private boolean verifyProcessorAccount(DoProcessorResponse processorResponse, ProcessorAccount processorAccount)  throws Exception {
		processorAccount = processorAccountVerifyService.verifyProcessorAccount(context.getUser(), processorAccount, context.getProcessor(), false);
		processorResponse.setProcessorAccount(processorAccount);
		if (BooleanUtils.isFalse(processorAccount.getVerified())) {
			ProcessorAccountVerificationType failedVerification = processorAccount.getFailedVerification();
			processorResponse.setErrorCode(failedVerification.getGeneralError().getCode());
			processorResponse.setMessage(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, context.getDomainMethod().getName()));
			processorResponse.setDeclineReason(getError(CASHIER_INVALID_ACCOUNT)  + ": " + failedVerification.getDescription());
			log.error("Account is invalid. Verification: " + processorAccount.getFailedVerification() + " Processor account: " + processorAccount);
			return false;
		}
		return true;
	}

	private void defineIframeData(DoMachineContext pContext, DoProcessorResponse processorResponse) {
		updateIframeStringData(pContext.getResponse(), processorResponse);
		if (processorResponse.getIframePostData() != null && processorResponse.getIframePostData().size() > 0) {
			pContext.getResponse().setIframePostData(processorResponse.getIframePostData());
		}
	}

	private void updateIframeStringData(DoResponse pContextResponse, DoProcessorResponse processorResponse) {
		String processorIframeUrl = processorResponse.getIframeUrl();
		String processorIframeMethod = processorResponse.getIframeMethod();
		String processorRedirectUrl = processorResponse.getRedirectUrl();
		String processorIframeWindowTarget = processorResponse.getIframeWindowTarget();
		log.info("Context Iframe Data before update: {}, {}, {}, {}", pContextResponse.getIframeUrl(), pContextResponse.getIframeMethod(), pContextResponse.getRedirectUrl(), pContextResponse.getIframeWindowTarget());
		log.info("Processor Response Iframe Data: {}, {}, {}, {}", processorIframeUrl, processorIframeMethod, processorRedirectUrl, processorIframeWindowTarget);
		if (processorIframeUrl != null) {
			if (pContextResponse.getIframeUrl() == null ||
					(pContextResponse.getIframeUrl() != null && !pContextResponse.getIframeUrl().equals(processorIframeUrl))) {
				pContextResponse.setIframeUrl(processorIframeUrl);
			}
		}
		if (processorIframeMethod != null) {
			if (pContextResponse.getIframeMethod() == null ||
					(pContextResponse.getIframeMethod() != null && !pContextResponse.getIframeMethod().equals(processorIframeMethod))) {
				pContextResponse.setIframeMethod(processorIframeMethod);
			}
		}
		if (processorRedirectUrl != null) {
			if (pContextResponse.getRedirectUrl() == null ||
					(pContextResponse.getRedirectUrl() != null && !pContextResponse.getRedirectUrl().equals(processorRedirectUrl))) {
				pContextResponse.setRedirectUrl(processorRedirectUrl);
			}
		}
		if (processorIframeWindowTarget != null) {
			if (pContextResponse.getIframeWindowTarget() == null ||
					(pContextResponse.getIframeWindowTarget() != null && !pContextResponse.getIframeWindowTarget().equals(processorIframeWindowTarget))) {
				pContextResponse.setIframeWindowTarget(processorIframeWindowTarget);
			}
		}
	}

	@Deprecated
	private DoResponse getResponse() {
		return getResponse(context);
	}
	private DoResponse getResponse(DoMachineContext pContext) {
		if (pContext == null) return new DoResponse();
		if (pContext.getResponse() == null) pContext.setResponse(new DoResponse());
		if (pContext.getTransaction() != null) {
			pContext.getResponse().setTransactionId(pContext.getTransaction().getId());
			if (pContext.getTransaction().getErrorCode() != null) {
				pContext.getResponse().setErrorMessage(GeneralError.fromErrorCode(pContext.getTransaction().getErrorCode()).getResponseMessageLocal(messageSource, context.getDomainMethod().getName()));
			}
			pContext.getResponse().setDeclineReason(context.getTransaction().getDeclineReason());
		}
		pContext.getResponse().setStage(pContext.getStage());
		if (pContext.getState() != null) pContext.getResponse().setState(pContext.getState().name());
		if (pContext.getRequest() != null && pContext.getRequest().getMobile() != null) pContext.getResponse().setMobile(pContext.getRequest().getMobile());
		log("getResponse1", Level.TRACE);
		if ((pContext.getResponse().getErrorMessage()==null || pContext.getResponse().getErrorMessage().isEmpty()) && (pContext.getProcessorResponse() != null)) pContext.getResponse().setErrorMessage(pContext.getProcessorResponse().getMessage());
		log("getResponse2", Level.TRACE);

		if (pContext.getProcessor() != null && pContext.getProcessor().getProcessor() != null) {
			pContext.getResponse().setProcessorCode(pContext.getProcessor().getProcessor().getCode());
		}

		pContext.getResponse().setAmountCents(getAmountCents());

		return pContext.getResponse();
	}

	private Long getAmountCents() {

		//TODO highly inefficient. Pulls from DB. We should populate input fields once on cycle
		// and use it to keep reading in future.
		try {
			String amountString = service.getData(context.getTransaction(), "amount", 1, false);
			if (amountString == null) return null;
			Long amountCents = CurrencyAmount.fromAmountString(amountString).toCents();
			return amountCents;
		} catch (Exception e) {
			log.error("Unhandled exception in getAmountCents", e);
			return null;
		}
	}

	public void log(String prefix) {
		log(prefix, Level.INFO);
	}

	public void debug(String prefix) {
		log(prefix, Level.DEBUG);
	}

	public void log(String prefix, Level level) {
		StringBuffer sb = new StringBuffer("DoMachine " + prefix);
		try {
			sb.append(" state " + ((context.getState()!=null)?context.getState().name():""));
			sb.append(" stage " + context.getStage());
			sb.append(" user " + ((context.getUser()!=null)?context.getUser().getGuid():""));
			sb.append(" author " + ((context.getAuthor()!=null)?context.getAuthor().getGuid():""));
			if (context.getDomainMethod() != null) {
				sb.append(" domain " + context.getDomainMethod().getDomain().getName());
				sb.append(" dm " + context.getDomainMethod().getId() + " " + context.getDomainMethod().getName());
			}
			if (context.getTransaction() != null) {
				sb.append(" transaction " + context.getTransaction().getId());
				if (context.getTransaction().getCurrent() != null) {
					if (context.getTransaction().getCurrent().getProcessor() != null) {
						sb.append(" dmp " + context.getTransaction().getCurrent().getProcessor().getId());
					}
				}
			}
			if (context.getResponse()!=null) {
				sb.append(" error "+context.getResponse().getError());
				sb.append(" msg "+context.getResponse().getErrorMessage());
			}
			switch (level) {
				case DEBUG:
					sb.append(" processorresponse "+context.getProcessorResponse());
					sb.append(" response "+context.getResponse());
					log.debug(sb.toString());
					break;
				case ERROR:
					log.error(sb.toString());
					break;
				case TRACE:
					log.trace(sb.toString());
					break;
				case WARN:
					log.warn(sb.toString());
					break;
				default:
					log.info(sb.toString());
			}
		} catch (NullPointerException e) {
			log.error(sb.toString()+" :: "+e.getMessage());
		}
	}

	private String translate(String key) {
		return Optional.ofNullable(key)
				.map(k -> translationService.translate(context.getExternalDomain().getName(), k, null))
				.orElse("");
	}

	private void registerStat(Type type, Event event, String domainName, String playerGuid) {
		stats.register(
				QueueStatEntry.builder()
						.type(type.type())
						.event(event.event())
						.entry(
								StatEntry.builder()
										.name("stats." + type.type() + "."
												+ playerGuid.replaceAll("/", ".")
												+ "." + event.event()
										)
										.domain(domainName)
										.ownerGuid(playerGuid)
										.build()
						)
						.build()
		);
	}

	private void addTransactionRemark() {
		String reference = service.getData(context.getTransaction(), "cardReference", 1, false);
		if (reference != null) {
			processorAccountService.addTransactionRemark(context.getTransaction().getId(), reference);
			return;
		}

		Long processorAccountId = ofNullable(service.getData(context.getTransaction(), "processorAccountId", 1, false))
				.map(Long::valueOf).orElse(null);
		if (processorAccountId != null) {
			processorAccountService.addTransactionRemark(context.getTransaction().getId(), processorAccountId);
		}
	}

	private Transaction updateContextTransactionWithAmountsCents(DoMachineContext pContext) {
		Transaction transaction = pContext.getTransaction();
		TransactionAmountsData transactionAmountsData = service.calculateAmounts(getAmountStringFromTransaction(transaction), Optional.ofNullable(pContext.getProcessor().getFees()).orElse(Fees.builder().build()));
		transaction.setAmountCents(transactionAmountsData.getDepositAmountCents());
		transaction.setFeeCents(transactionAmountsData.getFeeAmountCents());
		pContext.setTransaction(transaction);
		return transaction;
	}

	private String getAmountStringFromTransaction(Transaction transaction) {
		String amountString = service.getData(transaction, "amount", 1, false);
		if (amountString == null) {
			amountString = service.getData(transaction, "amount", 1, true);
		}
		return amountString;
	}

	private void setTransactionReviewedBy(DoMachineContext context, LithiumTokenUtil token) {
		context.getTransaction().setReviewedBy(userService.findOrCreate(token.guid()));
	}
}
