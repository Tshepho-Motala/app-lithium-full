package lithium.service.accounting.provider.internal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status409DuplicateTransactionException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.stream.auxlabel.AuxLabelStream;
import lithium.service.accounting.domain.summary.stream.AdjustmentStream;
import lithium.service.accounting.domain.summary.v2.stream.AdjustmentStreamV2;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.AuxLabelStreamData;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.ConstraintValidation;
import lithium.service.accounting.objects.PlayerBalanceLimitReachedEvent;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.context.adjust.AdjustmentContext;
import lithium.service.accounting.provider.internal.controllers.AccountCodeController;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountLabelValueConstraint;
import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.BalanceLimit;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.repositories.AccountLabelValueConstraintRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;
import lithium.service.affiliate.client.stream.TransactionStream;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.objects.UserEvent;
import lithium.util.ExceptionMessageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lithium.service.client.util.LabelManager.TRANSACTION_ID;

@Service
@Slf4j
public class TransactionServiceWrapper implements ITransactionServiceWrapper {
	@Autowired AccountService accountService;
	@Autowired AdjustmentStream adjustmentStream;
	@Autowired AdjustmentStreamV2 adjustmentStreamV2;
	@Autowired TransactionService transactionService;
	@Autowired AccountTypeService accountTypeService;
	@Autowired AccountCodeController accountCodeService;
	@Autowired AccountRepository accountRepository;
	@Autowired DomainRepository domainRepository;
	@Autowired EntityManager entityManager;
	@Autowired UserRepository userRepository;
	@Autowired CurrencyService currencyService;
	@Autowired AccountLabelValueConstraintRepository accountLabelValueConstraintRepository;
	@Autowired TransactionEntryRepository transactionEntryRepository;
	@Autowired TransactionLabelValueRepository transactionLabelValueRepository;
	@Autowired LithiumMetricsService metrics;
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired ModelMapper mapper;
	@Autowired RabbitEventService rabbitEventService;
	@Autowired ApplicationEventPublisher publisher;
	@Autowired PeriodService periodService;
	@Autowired CachingDomainClientService domainClientService;
	@Autowired BalanceLimitService balanceLimitService;
	@Autowired LabelValueService labelValueService;
	@Autowired Properties properties;

    /**
	* Passed in from ServiceAccountingProviderInternalApplication so that we have a proxied version to be able
	* to start the @Transactional for the adjustMultiInternal() method.
	* @see lithium.service.accounting.provider.internal.ServiceAccountingProviderInternalApplication
	*/
	@Setter ITransactionServiceWrapper serviceWrapper;

	/**
	 * Used to keep an eye on retry attempts in the logs.
	 */
	private static int COUNTER = 0;

	@Retryable(maxAttempts=5,backoff=@Backoff(random = true, delay=50, maxDelay = 1000), exclude={ NotRetryableErrorCodeException.class }, include=Exception.class)
	@TimeThisMethod
	public Response<AdjustmentTransaction> adjustMulti(
		Long amountCents,
		DateTime date,
		String accountCode,
		String accountTypeCode,
		String transactionTypeCode,
		String contraAccountCode,
		String contraAccountTypeCode,
		String[] labels,
		String currencyCode,
		String domainName,
		String ownerGuid,
		String authorGuid,
		Boolean allowNegativeAdjust,
		String[] negAdjProbeAccCodes,
		TransactionStreamData transactionStreamData,
		List<LabelValue> summaryLabelValues,
		Long internalTransactionId,
		BalanceAdjustEvent evt,
		boolean parentHandlesEvent
	) throws
	  Status414AccountingTransactionDataValidationException,
	  Status415NegativeBalanceException, Status500InternalServerErrorException
	{
		COUNTER++; // Retry counter
		log.info("adjustMulti counter " + COUNTER + " date " + date + " accountCode " + accountCode + " accountTypeCode " + accountTypeCode +
						 " transactionTypeCode " + transactionTypeCode + " contraAccountCode " + contraAccountCode + " contraAccountTypeCode " + contraAccountTypeCode +
						 " currencyCode " + currencyCode + " domainName " + domainName + " ownerGuid " + ownerGuid + " authorGuid " + authorGuid);

		/**
		 * The Currency and Account is created before the transaction is started, these do not need to be transactional.
		 */
		SW.start("accountService.lock."+COUNTER);
		Currency currency = currencyService.findByCode(currencyCode);
		if (currency == null) throw new RuntimeException("Invalid currency code " + currencyCode);
		Account a = accountService.findOrCreate(accountCode, accountTypeCode, currencyCode, domainName, ownerGuid);
		SW.stop();

		/**
		 * Before we start the transaction - we need to precreate the label values outside of the transaction, but nothing that ties it
		 * to an accounting transaction yet.
		 */
		precreateLabelValues(labels);

		/**
		 * Call the internal method from the proxied class, thus starting the transaction.
		 */
		SW.start("adjustMultiInternal");
		Response<AdjustmentTransaction> adjustMultiInternal = serviceWrapper.adjustMultiInternal(
			null, true, amountCents, date, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode,
			labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, negAdjProbeAccCodes, transactionStreamData,
			summaryLabelValues, evt, parentHandlesEvent, a.getId(), null
		);
		SW.stop();


		if (amountCents > 0) {
			/**
			 *
			 */
			SW.start("adjustForBalanceLimits");
			adjustForBalanceLimits(
					date,
					accountCode,
					accountTypeCode,
					transactionTypeCode,
					labels,
					currencyCode,
					domainName,
					ownerGuid,
					authorGuid,
					allowNegativeAdjust,
					transactionStreamData, summaryLabelValues, evt, a.getId());
			SW.stop();
		}

		return adjustMultiInternal;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	@TimeThisMethod
	public Response<AdjustmentTransaction> adjustMultiInternal(
			AdjustmentContext context,
			boolean forceFlushAndClear,
			Long amountCents,
			DateTime date,
			String accountCode,
			String accountTypeCode,
			String transactionTypeCode,
			String contraAccountCode,
			String contraAccountTypeCode,
			String[] labels,
			String currencyCode,
			String domainName,
			String ownerGuid,
			String authorGuid,
			Boolean allowNegativeAdjust,
			String[] negAdjProbeAccCodes,
			TransactionStreamData transactionStreamData,
			List<LabelValue> summaryLabelValues,
			BalanceAdjustEvent evt,
			boolean parentHandlesEvent,
			Long accountId,
			List<ConstraintValidation> constraintValidations
	) throws
		Status414AccountingTransactionDataValidationException,
		Status415NegativeBalanceException,
		Status500InternalServerErrorException
	{
		if (forceFlushAndClear) forceFlushAndClear();

		ArrayList<Object> logContext = new ArrayList<>();

		logContext.add("adjustMulti amountCents " + amountCents + " date " + date + " accountCode " + accountCode + " accountTypeCode " + accountTypeCode +
				" transactionTypeCode " + transactionTypeCode + " contraAccountCode " + contraAccountCode + " contraAccountTypeCode " + contraAccountTypeCode +
				" currencyCode " + currencyCode + " domainName " + domainName + " ownerGuid " + ownerGuid + " authorGuid " + authorGuid +
				" allowNegativeAdjust " + allowNegativeAdjust + " negAdjProbeAccCodes " + negAdjProbeAccCodes);

		try {
			SW.start("lockingUpdate");
			accountService.lockingUpdate(accountId);
			SW.stop();

			// Attempt 1 at handling constraint validations for reversal transactions.
			// The standalone rollback method below also did a dupe check. But I think that's unnecessary, we should catch
			// that because of the unique label, however, that is way down the line.
			// That method also copied labels from the original transaction, however, we have the labels here. And we added
			// reverse_transaction_id and original_transaction_id labels if reversalBetTransactionId is present.
			// TODO: Validate and make sure it is present if the adjustment type is one of the reversal types. Error if
			//		 we try to process without.
			if (constraintValidations != null && !constraintValidations.isEmpty()) {
				for (ConstraintValidation cv: constraintValidations) {
					AccountLabelValueConstraint constraint = accountLabelValueConstraintRepository.
							findByAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndAccountDomainNameAndLabelValueLabelNameAndLabelValueValue(
									cv.getAccountCode(), cv.getAccountTypeCode(), ownerGuid, currencyCode, domainName,
									cv.getLabelName(), cv.getLabelValue());

					switch (cv.getType()) {
						case REQUIRED:
							if (constraint == null) {
								throw new Status414AccountingTransactionDataValidationException("Constraint validation"
										+ " failed | cv: " + cv);
							}
							break;
					}
				}
			}

			SW.start("begintran");
			Transaction t = transactionService.beginTransaction(transactionTypeCode, authorGuid.toString());
			evt.setAuthor(t.getAuthor());
			Long tid = t.getId();
			SW.stop();

			if (labels != null) {
				SW.start("labels");
				transactionService.labels(tid, labels);
				SW.stop();
			}

			SW.start("adjustplb");
			evt.setTranEntry(transactionService.adjust(tid, accountCode, accountTypeCode, currencyCode, amountCents * -1, date, domainName, ownerGuid));
			SW.stop();

			SW.start("contra");
			evt.setTranContraEntry(transactionService.adjust(tid, contraAccountCode, contraAccountTypeCode, currencyCode, amountCents, date, domainName, ownerGuid));
			SW.stop();

			SW.start("end");
			transactionService.endTransaction(tid, transactionStreamData, summaryLabelValues);
			SW.stop();

//			try {
//				if (ownerGuid.equals("livescore_nigeria/loadtestsports_1")) {
//					Thread.sleep(20000);
//				}
//			} catch (Exception e) {
//				log.error("Interupted");
//			}

			SW.start("negative_adjust_check");
			if (!allowNegativeAdjust) {
				if ((negAdjProbeAccCodes == null) || (negAdjProbeAccCodes != null && negAdjProbeAccCodes.length == 0)) {
					negAdjProbeAccCodes = new String[] {accountCode};
				}
				long combinedBalance = 0;
				List<Account> accounts = accountRepository.findByOwnerGuidAndDomainNameAndCurrencyCodeAndAccountCodeCodeInAndAccountTypeCode(ownerGuid, domainName, currencyCode, negAdjProbeAccCodes, accountTypeCode);
				for (Account account: accounts) {
					combinedBalance += (account.getBalanceCents() * -1);
				}
				String accountsToStr = accounts.stream().map(Account::toString).collect(Collectors.joining(", "));
				log.debug("Balance check on negative check " + currencyCode + " " + domainName + " " + ownerGuid + " " + accountsToStr);
				if (combinedBalance < 0) {
					log.warn("Adjustment caused a negative balance which was not allowed " + currencyCode + " " + domainName + " " + ownerGuid + " " + accountsToStr + " negAdjProbeAccCodes " + String.join(",", negAdjProbeAccCodes));
					TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
					throw new Status415NegativeBalanceException(amountCents, date, transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, "Negative adjustment error");
				}
			}
			SW.stop();

			Response<AdjustmentTransaction> response = Response.<AdjustmentTransaction>builder().status(Status.OK).data(AdjustmentTransaction.builder().transactionId(tid).status(AdjustmentTransaction.AdjustmentResponseStatus.NEW).build()).build();

			ArrayList<lithium.service.accounting.objects.TransactionEntry> entryList = new ArrayList<>();
			entryList.add(mapper.map(evt.getTranEntry(), lithium.service.accounting.objects.TransactionEntry.class));
			entryList.add(mapper.map(evt.getTranContraEntry(), lithium.service.accounting.objects.TransactionEntry.class));

			List<TransactionLabelBasic> transactionLabelBasicList = transactionService.getLabelsForTransaction(
					transactionStreamData.getTransactionId());
			setSummarizeProperty(transactionLabelBasicList, summaryLabelValues);

			CompleteTransaction completeTransaction = CompleteTransaction.builder()
					.transactionId(transactionStreamData.getTransactionId())
					.createdOn(date.toString())
					.transactionType(transactionTypeCode)
					.transactionLabelList(transactionLabelBasicList)
					.transactionEntryList(entryList)
					.build();

			rabbitEventService.send(completeTransaction);

			CompleteTransactionV2 completeTransactionV2 = CompleteTransactionV2.builder()
					.transactionId(transactionStreamData.getTransactionId())
					.createdOn(date.toString())
					.transactionType(transactionTypeCode)
					.transactionLabelList(transactionLabelBasicList)
					.transactionEntryList(entryList)
					.testUser(entryList.get(0).getAccount().getOwner().isTestAccount())
					.build();

			if (context != null) {
				context.getCompletedTransactions().add(completeTransaction);
				context.getCompleteTransactionV2s().add(completeTransactionV2);
			} else {
				List<CompleteTransaction> completeTransactions = new ArrayList<>();
				completeTransactions.add(completeTransaction);
				if (properties.getBalanceAdjustments().isSummarizeDomainEnabled()) {
					adjustmentStream.register(completeTransactions);
				}

				List<CompleteTransactionV2> completeTransactionV2List = new ArrayList<>();
				completeTransactionV2List.add(completeTransactionV2);
				if (properties.getBalanceAdjustments().isSummarizeDomainEnabled()) {
					adjustmentStreamV2.register(completeTransactionV2List);
				}
			}

			log.info("adjustmulti success " + logContext);

			return response;
		} catch (Status414AccountingTransactionDataValidationException e) {
			log.error("labels: "+ Arrays.toString(labels));
			log.error("adjustmulti Status414AccountingTransactionDataValidationException " + e + " " + ExceptionMessageUtil.allMessages(e) + " " + logContext, e);
			throw e;
		} catch (Status409DuplicateTransactionException dte) {
			log.warn("adjustmulti duplicate " + logContext);
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			return Response.<AdjustmentTransaction>builder().status(Status.OK).data(
					AdjustmentTransaction.builder().transactionId(Long.parseLong(dte.getTransactionId()))
							.status(AdjustmentTransaction.AdjustmentResponseStatus.DUPLICATE).build()).build();
//		} catch (ErrorCodeException e) {
//			log.error("adjustmulti error " + e + " " + ExceptionMessageUtil.allMessages(e) + " " + context);
//			throw e;
		} catch (Exception e) {
			log.error("adjustmulti error " + e + " " + ExceptionMessageUtil.allMessages(e) + " " + logContext, e);
			throw e;
		} finally {
			if (!parentHandlesEvent) {
				publisher.publishEvent(evt);
			}
		}

	}

	private void setSummarizeProperty(List<TransactionLabelBasic> transactionLabelBasicList,
			List<LabelValue> summaryLabelValues) {
		transactionLabelBasicList.forEach(transactionLabelBasic -> {
			// Hack. We need to introduce a new field in the transaction type label definition to specify the type
			// of summary, if any: player, domain.
			// Here, for now, we want to exclude these labels from domain summaries, as these are unique values.
			if (TransactionService.PERIOD_DOMAIN_SUMMARY_EXCLUDED_LABELS
					.contains(transactionLabelBasic.getLabelName())) {
				return;
			}
			boolean summarize = false;
			for (LabelValue labelValue: summaryLabelValues) {
				if (labelValue.getLabel().getName().contentEquals(transactionLabelBasic.getLabelName())) {
					summarize = true;
					break;
				}
			}
			transactionLabelBasic.setSummarize(summarize);
		});
	}

	@Async
	public void dispatchUserBalanceEvent(String ownerGuid, String domainName, String currencyCode) {
		try {
			metrics.timer(log).time("balanceDispatch", (StopWatch sw) -> {
				sw.start("balanceLookup");
				HashMap<String, Long> balances = new LinkedHashMap<String, Long>();
				balances.put("PLAYER_BALANCE", getBalance(domainName, "PLAYER_BALANCE", "PLAYER_BALANCE", currencyCode, ownerGuid).getData());
				balances.put("PLAYER_BALANCE_CASINO_BONUS", getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, ownerGuid).getData());
				balances.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, ownerGuid).getData());
				sw.stop();
				sw.start("balanceEventDispatch");
				UserEventClient cl = lithiumServiceClientFactory.target(UserEventClient.class, "service-user", true);
				cl.streamUserEvent(
						domainName,
						ownerGuid.substring(ownerGuid.indexOf("/") + 1),
						UserEvent.builder()
								.type("BALANCE")
								.message(currencyCode)
								.data(new ObjectMapper().writeValueAsString(balances))
								.build()
				);
				sw.stop();
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Retryable(maxAttempts=5,backoff=@Backoff(random = true, delay=50, maxDelay = 1000), exclude={ NotRetryableErrorCodeException.class }, include=Exception.class)
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public Response<AdjustmentTransaction> rollback(
			DateTime date,
			String reversalTransactionTypeCode,
			String reversalLabelName,
			String domainName,
			String ownerGuid,
			String authorGuid,
			String currencyCode,
			String labelName,
			String labelValue,
			String originalAccountCode,
			String originalAccountTypeCode,
			TransactionStreamData transactionStreamData,
			List<LabelValue> summaryLabelValues
	) throws Exception {
		log.info("rollback date " + date + " reversalTransactionTypeCode " + reversalTransactionTypeCode +
				" reversalLabelName " + reversalLabelName +
				" domainName " + domainName + " ownerGuid " + ownerGuid + " authorGuid " + authorGuid +
				" labelName " + labelName + " labelValue " + labelValue + " originalAccountCode " + originalAccountCode +
				" originalAccountTypeCode " + originalAccountTypeCode);

		//TODO:Pull labels of original tran and attach to rollback tran + label to original tran
		return metrics.timer(log).time("rollback", (StopWatch sw) -> {

			sw.start("find_rollback_entry");
			AccountLabelValueConstraint rollbackConstraint = accountLabelValueConstraintRepository.
					findByAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndAccountDomainNameAndLabelValueLabelNameAndLabelValueValue(
							originalAccountCode, originalAccountTypeCode, ownerGuid, currencyCode, domainName, reversalLabelName, labelValue);

			if (rollbackConstraint != null) {
				log.warn("Duplicate Transaction :: "+rollbackConstraint);
				return Response.<AdjustmentTransaction>builder().data(AdjustmentTransaction.builder().transactionId(rollbackConstraint.getTransactionEntry().getTransaction().getId()).status(AdjustmentTransaction.AdjustmentResponseStatus.DUPLICATE).build()).status(Status.OK).build();
			}
			sw.stop();

			sw.start("find_tran_to_roll_back");
			//Find transaction that should be rolled back
			AccountLabelValueConstraint transactionConstraint = accountLabelValueConstraintRepository.
					findByAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndAccountDomainNameAndLabelValueLabelNameAndLabelValueValue(
							originalAccountCode, originalAccountTypeCode, ownerGuid, currencyCode, domainName, labelName, labelValue);
			sw.stop();

			if (transactionConstraint == null) {
				log.warn("Could not find transaction to rollback :: ");
				log.warn("rollback date " + date + " reversalTransactionTypeCode " + reversalTransactionTypeCode +
						" reversalLabelName " + reversalLabelName +
						" domainName " + domainName + " ownerGuid " + ownerGuid + " authorGuid " + authorGuid +
						" labelName " + labelName + " labelValue " + labelValue + " originalAccountCode " + originalAccountCode +
						" originalAccountTypeCode " + originalAccountTypeCode);
				return Response.<AdjustmentTransaction>builder().status(Status.NOT_FOUND).build();
			}

			List<TransactionLabelValue> tlvList = transactionLabelValueRepository.findByTransactionId(transactionConstraint.getTransactionEntry().getTransaction().getId());
			ArrayList<String> labels = new ArrayList<>();
			for(TransactionLabelValue tlv : tlvList) {
				String name = tlv.getLabelValue().getLabel().getName();
				if (name.equals(labelName)) name = reversalLabelName;
				labels.add(name+"="+tlv.getLabelValue().getValue());
			}
			//Original internal tran id label
			labels.add("original_transaction_id="+transactionConstraint.getTransactionEntry().getTransaction().getId());
			String[] labelsArr = new String[labels.size()];
			labelsArr = labels.toArray(labelsArr);

			Long originalTranId = transactionConstraint.getTransactionEntry().getTransaction().getId();
			List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(originalTranId);

			Transaction t = transactionService.beginTransaction(reversalTransactionTypeCode, authorGuid.toString());
			Long tid = t.getId();

			transactionService.labels(tid, labelsArr);

			ArrayList<TransactionEntry> rollbackEntries = new ArrayList<>();
			for (TransactionEntry entry: entries) {
				sw.start("adjustentry_" + entry.getAccount().getAccountCode().toString());
				rollbackEntries.add(transactionService.adjust(tid, entry.getAccount().getAccountCode().getCode(),
						entry.getAccount().getAccountType().getCode(), currencyCode, entry.getAmountCents() * -1, date, domainName, ownerGuid));
				sw.stop();
			}

			sw.start("end");
			transactionService.endTransaction(tid, transactionStreamData, summaryLabelValues);
			sw.stop();

			ArrayList<lithium.service.accounting.objects.TransactionEntry> entryList = new ArrayList<>();
			rollbackEntries.forEach(entry -> {
				entryList.add(mapper.map(entry, lithium.service.accounting.objects.TransactionEntry.class));
			});

			List<TransactionLabelBasic> transactionLabelBasicList = transactionService.getLabelsForTransaction(
					transactionStreamData.getTransactionId());
			setSummarizeProperty(transactionLabelBasicList, summaryLabelValues);

			CompleteTransaction completeTransaction = CompleteTransaction.builder()
					.transactionId(transactionStreamData.getTransactionId())
					.createdOn(date.toString())
					.transactionType(reversalTransactionTypeCode)
					.transactionLabelList(transactionLabelBasicList)
					.transactionEntryList(entryList)
					.build();

			rabbitEventService.send(completeTransaction);

			CompleteTransactionV2 completeTransactionV2 = CompleteTransactionV2.builder()
					.transactionId(transactionStreamData.getTransactionId())
					.createdOn(date.toString())
					.transactionType(reversalTransactionTypeCode)
					.transactionLabelList(transactionLabelBasicList)
					.transactionEntryList(entryList)
					.testUser(entryList.get(0).getAccount().getOwner().isTestAccount())
					.build();

			List<CompleteTransaction> completeTransactions = new ArrayList<>();
			completeTransactions.add(completeTransaction);
			if (properties.getBalanceAdjustments().isSummarizeDomainEnabled()) {
				adjustmentStream.register(completeTransactions);
			}
			List<CompleteTransactionV2> completeTransactionV2List = new ArrayList<>();
			completeTransactionV2List.add(completeTransactionV2);
			if (properties.getBalanceAdjustments().isSummarizeDomainEnabled()) {
				adjustmentStreamV2.register(completeTransactionV2List);
			}

			return Response.<AdjustmentTransaction>builder().status(Status.OK).data(AdjustmentTransaction.builder()
					.transactionId(tid).status(AdjustmentTransaction.AdjustmentResponseStatus.NEW).build()).build();

		});
	}

	@TimeThisMethod
	public Response<Long> getBalance(
			@PathVariable("domainName") String domainName,
			@PathVariable("accountCode") String accountCodeStr,
			@PathVariable("accountType") String accountTypeStr,
			@PathVariable("currencyCode") String currencyCode,
			@PathVariable("ownerGuid") String ownerGuid
	) {

		log.debug("currencyCode : "+currencyCode+" domainName: "+domainName+" ownerGuid: "+ownerGuid);

		String accountCodeStrInternal = "PLAYER_BALANCE";
		if ((accountCodeStr != null) && (!accountCodeStr.isEmpty())) {
			accountCodeStrInternal = accountCodeStr;
		}
		String accountTypeStrInternal = "PLAYER_BALANCE";
		if ((accountTypeStr != null) && (!accountTypeStr.isEmpty())) {
			accountTypeStrInternal = accountTypeStr;
		}

		SW.start("accountcode");
		AccountCode accountCode = accountCodeService.findOrCreateAccountCode(accountCodeStrInternal).getBody();
		SW.stop();
		if (accountCode == null) return Response.<Long>builder().status(Status.OK).data(0L).build();
		if (accountCode != null) log.debug("Found accountCode " + accountCode);

		SW.start("accounttype");
		AccountType accountType = accountTypeService.findByCode(accountTypeStrInternal);
		SW.stop();
		if (accountType == null) return Response.<Long>builder().status(Status.OK).data(0L).build();
		if (accountType != null) log.debug("Found accountType " + accountType);

		SW.start("owner");
		User owner = userRepository.findByGuid(ownerGuid);
		SW.stop();
		if (owner == null) return Response.<Long>builder().status(Status.OK).data(0L).build();
		if (owner != null) log.debug("Found owner " + owner);

		SW.start("domain");
		Domain domain = domainRepository.findByName(domainName);
		SW.stop();
		if (domain == null) return Response.<Long>builder().status(Status.OK).data(0L).build();
		if (domain != null) log.debug("Found domain " + domain);

		SW.start("currency");
		Currency currency = currencyService.findByCode(currencyCode);
		SW.stop();
		if (currency == null) return Response.<Long>builder().status(Status.OK).data(0L).build();
		if (currency != null) log.debug("Found currency " + domain);

		SW.start("account");
		Account a = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountCodeAndAccountType(owner, domain, currency, accountCode, accountType);
		SW.stop();
		if (a == null) return Response.<Long>builder().status(Status.OK).data(0L).build();

		return Response.<Long>builder().status(Status.OK).data(a.getBalanceCents() * -1).build();
	}

	/**
	 * Retrieve all the account code account balances for a specific account type
	 * @param domainName
	 * @param accountTypeStr
	 * @param currencyCode
	 * @param ownerGuid
	 * @return Map containing account code as key with account balance cents value
	 * @throws Exception
	 */
	public Response<Map<String, Long>> getBalanceByAccountType(
			String domainName,
			String accountTypeStr,
			String currencyCode,
			String ownerGuid
	) throws Exception {
		return metrics.timer(log).time("getBalanceByAccountType", (StopWatch sw) -> {
			log.debug("currencyCode : "+currencyCode+" domainName: "+domainName+" ownerGuid: "+ownerGuid);

			String accountTypeStrInternal = "PLAYER_BALANCE";
			if ((accountTypeStr != null) && (!accountTypeStr.isEmpty())) {
				accountTypeStrInternal = accountTypeStr;
			}

			sw.start("accounttype");
			AccountType accountType = accountTypeService.findByCode(accountTypeStrInternal);
			sw.stop();
			if (accountType == null) return Response.<Map<String,Long>>builder().status(Status.NOT_FOUND).message("Could not find account type.").build();
			if (accountType != null) log.debug("Found accountType " + accountType);

			sw.start("owner");
			User owner = userRepository.findByGuid(ownerGuid);
			sw.stop();
			if (owner == null) return Response.<Map<String,Long>>builder().status(Status.NOT_FOUND).message("Could not find user.").build();
			if (owner != null) log.debug("Found owner " + owner);

			sw.start("domain");
			Domain domain = domainRepository.findByName(domainName);
			sw.stop();
			if (domain == null) return Response.<Map<String,Long>>builder().status(Status.NOT_FOUND).message("Could not find domain.").build();
			if (domain != null) log.debug("Found domain " + domain);

			sw.start("currency");
			Currency currency = currencyService.findByCode(currencyCode);
			sw.stop();
			if (currency == null) return Response.<Map<String,Long>>builder().status(Status.NOT_FOUND).message("Could not find currency").build();
			if (currency != null) log.debug("Found currency " + domain);

			sw.start("account");
			ArrayList<Account> accountList = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountType(owner, domain, currency, accountType);
			Map<String, Long> accountCodeValueMap = new HashMap<>();
			accountList.forEach(account -> { accountCodeValueMap.put(account.getAccountCode().getCode(), account.getBalanceCents()  * -1); });
			sw.stop();

			return Response.<Map<String, Long>>builder().status(Status.OK).data(accountCodeValueMap).build();
		});
	}

	@TimeThisMethod
	public Response<Map<String, Long>> getBalanceMapByAccountType(
			String domainName,
			String accountTypeStr,
			String currencyCode,
			String ownerGuid
	) throws
			Status410AccountingAccountTypeNotFoundException,
			Status411AccountingUserNotFoundException,
			Status412AccountingDomainNotFoundException,
			Status413AccountingCurrencyNotFoundException {
		log.debug("currencyCode : "+currencyCode+" domainName: "+domainName+" ownerGuid: "+ownerGuid);

		String accountTypeStrInternal = "PLAYER_BALANCE";
		if ((accountTypeStr != null) && (!accountTypeStr.isEmpty())) {
			accountTypeStrInternal = accountTypeStr;
		}

		SW.start("accounttype");
		AccountType accountType = accountTypeService.findByCode(accountTypeStrInternal);
		SW.stop();
		if (accountType == null)  throw new Status410AccountingAccountTypeNotFoundException(accountTypeStrInternal);
		log.debug("Found accountType " + accountType);

		SW.start("owner");
		User owner = accountService.findOrCreateUser(ownerGuid);
		SW.stop();
		if (owner == null) throw new Status411AccountingUserNotFoundException(ownerGuid);
		log.debug("Found owner " + owner);

		SW.start("domain");
		Domain domain = domainRepository.findByName(domainName);
		SW.stop();
		if (domain == null) throw new Status412AccountingDomainNotFoundException(domainName);
		log.debug("Found domain " + domain);

		SW.start("currency");
		Currency currency = currencyService.findByCode(currencyCode);
		SW.stop();
		if (currency == null) throw new Status413AccountingCurrencyNotFoundException(currencyCode);
		log.debug("Found currency " + domain);

		SW.start("account");
		ArrayList<Account> accountList = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountType(owner, domain, currency, accountType);
		Map<String, Long> accountCodeValueMap = new HashMap<>();
		accountList.forEach(account -> { accountCodeValueMap.put(account.getAccountCode().getCode(), account.getBalanceCents()  * -1); });
		SW.stop();

		return Response.<Map<String, Long>>builder().status(Status.OK_SUCCESS).data(accountCodeValueMap).build();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	@TimeThisMethod
	public void adjustMultiBatchInternal(AdjustmentContext context) throws
		Status414AccountingTransactionDataValidationException,
		Status415NegativeBalanceException, Status500InternalServerErrorException
	{
		forceFlushAndClear();
		
	    ArrayList<AdjustmentRequestComponent> adjustmentRequestList = context.getRequest().getAdjustments();
	    ArrayList<TransactionStreamData> transactionStreamDataList = context.getTransactionStreamDataList();
	    ArrayList<ArrayList<LabelValue>> summaryLabelValueList = context.getSummaryLabelValueList();
		
		ArrayList<BalanceAdjustEvent> balanceAdjustEventList = new ArrayList<>(adjustmentRequestList.size());
		try {
			// Clear the return lists before starting work. 
			// I suspect if a retry happens, the lists will retain their modifications from the previous attempt.
			transactionStreamDataList.clear();
			summaryLabelValueList.clear();

			for (int p=0; p < adjustmentRequestList.size(); ++p) {
				// Init objects for this adjustment run
				AdjustmentRequestComponent adjReq = adjustmentRequestList.get(p);
				TransactionStreamData tsd = new TransactionStreamData();
				ArrayList<LabelValue> lvList = new ArrayList<>();
				BalanceAdjustEvent evt = new BalanceAdjustEvent();
				balanceAdjustEventList.add(evt);

				// Perform adjustment

				Long accountId = context.getAccountIdList().get(p);

				Response<AdjustmentTransaction> adjRes = adjustMultiInternal(context, false, adjReq.getAmountCents(),
					adjReq.getDate(),
					adjReq.getAccountCode(),
					adjReq.getAccountTypeCode(),
					adjReq.getTransactionTypeCode(),
					adjReq.getContraAccountCode(),
					adjReq.getContraAccountTypeCode(),
					adjReq.getLabels(),
					adjReq.getCurrencyCode(),
					adjReq.getDomainName(),
					adjReq.getOwnerGuid(),
					adjReq.getAuthorGuid(),
					adjReq.getAllowNegativeAdjust(),
					null,
					tsd,
					lvList,
					evt,
					true,
					accountId,
					adjReq.getConstraintValidations()
				);

				// Populate response objects with relevant adjustment info
				if (adjRes.getData() != null) context.getResponse().add(adjRes.getData());

				if (adjReq.getAmountCents() > 0) {
					adjustForBalanceLimits(
							adjReq.getDate(),
							adjReq.getAccountCode(),
							adjReq.getAccountTypeCode(),
							adjReq.getTransactionTypeCode(),
							adjReq.getLabels(),
							adjReq.getCurrencyCode(),
							adjReq.getDomainName(),
							adjReq.getOwnerGuid(),
							adjReq.getAuthorGuid(),
							adjReq.getAllowNegativeAdjust(),
							tsd, lvList, evt, accountId);
				}

				transactionStreamDataList.add(tsd);
				summaryLabelValueList.add(lvList);
			}

			// When we get a Status409DuplicateTransactionException from the adjustMultiInternal call above, there isn't
			// an exception thrown. It's caught and a valid response is returned, thus we still get to this point, but
			// with an empty completedTransactions list. Since this is a multibet, there's a chance we get a duplicate on
			// the first transaction, but the second transaction succeeds, or vice versa, thus i'm checking for an empty
			// completedTransactions list before registering the transactions.
			if (!context.getCompletedTransactions().isEmpty() && properties.getBalanceAdjustments().isSummarizeDomainEnabled()) {
				adjustmentStream.register(context.getCompletedTransactions());
				adjustmentStreamV2.register(context.getCompleteTransactionV2s());
			}
		} finally {
			// Handle rollback cache clearing for all affected caches
			balanceAdjustEventList.forEach(event -> {
				publisher.publishEvent(event);
			});
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	@TimeThisMethod
	protected void adjustForBalanceLimits(
		DateTime date,
		String accountCode,
		String accountTypeCode,
		String transactionTypeCode,
		String[] labels,
		String currencyCode,
		String domainName,
		String ownerGuid,
		String authorGuid,
		Boolean allowNegativeAdjust,
		TransactionStreamData tsd,
		List<LabelValue> lvList,
		BalanceAdjustEvent evt,
		Long accountId
	) throws Status415NegativeBalanceException, Status414AccountingTransactionDataValidationException, Status500InternalServerErrorException {
		try {
			if (!domainClientService.retrieveDomainFromDomainService(domainName).getPlayerBalanceLimit()) {
				return;
			}
		} catch (Status550ServiceDomainClientException e) {
			throw new Status500InternalServerErrorException("Can't retrieve domain", e);
		}

		log.debug("searching for balanceLimit :: "+ownerGuid+" :: "+accountCode+"|"+accountTypeCode+" ("+transactionTypeCode+")");
		BalanceLimit bl = balanceLimitService.find(ownerGuid, accountCode, accountTypeCode);
		if (bl != null) {
			Account a = accountService.findOrCreate(accountCode, accountTypeCode, currencyCode, domainName, ownerGuid);
			Long balanceAfterAdjust = (a.getBalanceCents()*-1);
			log.debug("balanceLimit found for :: "+ownerGuid+" :: "+accountCode+"|"+accountTypeCode+" :: limit: "+bl.getBalanceCents()+"c | account_balance: "+balanceAfterAdjust+"c");

			// check balanceAfterAdjust
			if (balanceAfterAdjust > bl.getBalanceCents()) {
				Long amountToMove = balanceAfterAdjust-bl.getBalanceCents();

                String providerTransactionId = Arrays.stream(labels)
                        .map(s -> resolveLabel(s))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(keyValue -> TRANSACTION_ID.equals(keyValue.getKey()))
                        .findAny()
                        .map(KeyValue::getValue)
                        .orElse(String.valueOf(System.currentTimeMillis()));

				String[] label = new String[1];
				label[0] = TRANSACTION_ID + "=" + providerTransactionId + "_OVER_BALANCE_LIMIT";

				log.debug("Moving funds to escrow. ("+bl.getTransactionTypeTo().getCode()+") ("+amountToMove+"c) accnt: "+bl.getAccount()+" | contra: "+bl.getContraAccount());

				serviceWrapper.adjustMultiInternal(
						null,
					false,
					(amountToMove*-1),
					date,
					bl.getAccount().getAccountCode().getCode(),
					bl.getAccount().getAccountType().getCode(),
					bl.getTransactionTypeTo().getCode(),
					bl.getContraAccount().getAccountCode().getCode(),
					bl.getContraAccount().getAccountType().getCode(),
					label,
					currencyCode,
					domainName,
					ownerGuid,
					authorGuid,
					allowNegativeAdjust,
					null,
					tsd,
					lvList,
					evt,
					true,
					accountId,
					null
				);

				String amount = CurrencyAmount.fromCents(amountToMove).toAmount().setScale(2).toPlainString();
				String limitAmount = CurrencyAmount.fromCents(bl.getBalanceCents()).toAmount().setScale(2).toPlainString();

				PlayerBalanceLimitReachedEvent data = PlayerBalanceLimitReachedEvent.builder()
						.domainName(domainName)
						.amountCents(amountToMove)
						.ownerGuid(ownerGuid)
						.authorGuid(authorGuid)
						.comment("Created DW for " + amount + " due Balance Limit reached (" + limitAmount + ")")
						.balanceLimitEscrow(true)
						.build();

				log.info("Created DW request for " + data);
				rabbitEventService.sendPlayerBalanceLimitReachedEvent(data);
			}
		}
	}
	/**
	 * <p>At the start of the adjustment, any pending changes need to be flushed, and the persistence context needs to be
	 * cleared.</p>
	 * <br>
	 * <p>This is needed because above the adjustment @Transactional, we do a findOrCreate on an account. However, by the
	 * time we are ready to apply the pessimistic lock on the account, what we have in the persistence context may now
	 * be outdated.</p>
	 * <br>
	 * <p>The javadoc below explains the reason for doing this and how entities with @Version are handled when pessimistic
	 * locking is attempted.</p>
	 * <br>
	 * <p>As of 10 Jun 2021, livescore-production branch, a single adjustment:
	 * <ul>
	 *     <li>
	 *         With flush and clear:
	 *         <p>
	 *             2021-06-14 11:57:45.509  INFO 99393 --- [nio-9102-exec-1] i.StatisticalLoggingSessionEventListener : Session Metrics {<br>
	 *              16364 nanoseconds spent acquiring 1 JDBC connections;<br>
	 *              0 nanoseconds spent releasing 0 JDBC connections;<br>
	 *              6036948 nanoseconds spent preparing 250 JDBC statements;<br>
	 *              110301208 nanoseconds spent executing 250 JDBC statements;<br>
	 *              0 nanoseconds spent executing 0 JDBC batches;<br>
	 *              0 nanoseconds spent performing 0 L2C puts;<br>
	 *              0 nanoseconds spent performing 0 L2C hits;<br>
	 *              0 nanoseconds spent performing 0 L2C misses;<br>
	 *              1919765 nanoseconds spent executing 6 flushes (flushing a total of 250 entities and 0 collections);<br>
	 *              41629905 nanoseconds spent executing 93 partial-flushes (flushing a total of 6242 entities and 6242 collections)<br>
	 *             }
	 *         </p>
	 *     </li>
	 *     <li>
	 *         Without flush and clear:
	 *         <p>
	 *             2021-06-14 12:01:04.929  INFO 8911 --- [nio-9102-exec-1] i.StatisticalLoggingSessionEventListener : Session Metrics {<br>
	 *              15204 nanoseconds spent acquiring 1 JDBC connections;<br>
	 *              0 nanoseconds spent releasing 0 JDBC connections;<br>
	 *              6535001 nanoseconds spent preparing 233 JDBC statements;<br>
	 *              117824909 nanoseconds spent executing 233 JDBC statements;<br>
	 *              0 nanoseconds spent executing 0 JDBC batches;<br>
	 *              0 nanoseconds spent performing 0 L2C puts;<br>
	 *              0 nanoseconds spent performing 0 L2C hits;<br>
	 *              0 nanoseconds spent performing 0 L2C misses;<br>
	 *              1688701 nanoseconds spent executing 5 flushes (flushing a total of 196 entities and 0 collections);<br>
	 *              47061103 nanoseconds spent executing 93 partial-flushes (flushing a total of 8514 entities and 8514 collections)<br>
	 *             }
	 *         </p>
	 *     </li>
	 * </ul>
	 * </p>
	 *
	 * @see EntityManager#lock(java.lang.Object, javax.persistence.LockModeType)
	 *
	 */
	private void forceFlushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

	public void precreateLabelValues(String[] labels) {
		for (String labelAndValue : labels) {
            resolveLabel(labelAndValue).ifPresent(keyValue -> labelValueService.findOrCreate(keyValue.getKey(), keyValue.getValue()));
		}
	}

    public static Optional<KeyValue> resolveLabel(String labelAndValue) {
        String[] labelAndValueSplit = labelAndValue.split("=", 2);
        if (labelAndValueSplit.length == 2) {
            String label = labelAndValueSplit[0];
            String value = labelAndValueSplit[1];
            return Optional.of(KeyValue.of(label, value));
        }
        return Optional.empty();
    }

    @Data
    @AllArgsConstructor(staticName="of")
    public static class KeyValue {
        private final String key;
        private final String value;
    }
}
