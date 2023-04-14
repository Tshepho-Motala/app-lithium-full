package lithium.service.accounting.provider.internal.services;

import lithium.cashier.CashierTransactionLabels;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status409DuplicateTransactionException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.domain.summary.stream.AsyncLabelValueStream;
import lithium.service.accounting.domain.summary.v2.stream.AsyncLabelValueStreamV2;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountLabelValueConstraint;
import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionComment;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeAccount;
import lithium.service.accounting.provider.internal.data.entities.TransactionTypeLabel;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.projection.entities.TransactionLabelValueProjection;
import lithium.service.accounting.provider.internal.data.repositories.AccountLabelValueConstraintRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionCommentRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeAccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeLabelRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.TransactionLabelValueSpecifications;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.util.LabelManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lithium.service.accounting.provider.internal.services.TransactionServiceWrapper.resolveLabel;

@Service
@Slf4j
public class TransactionService {

	@Autowired LithiumMetricsService metrics;

	@Autowired AccountService accountService;
	@Autowired AsyncLabelValueStream asyncLabelValueStream;
	@Autowired LabelValueService labelValueService;

	@Autowired SummaryAccountService summaryAccountService;
	@Autowired SummaryAccountTransactionTypeService summaryAccountTransactionTypeService;
	@Autowired SummaryAccountLabelValueService summaryAccountLabelValueService;

	@Autowired CurrencyService currencyService;
	@Autowired UserRepository userRepository;
	@Autowired AccountRepository accountRepository;
	@Autowired AccountTypeService accountTypeService;
	@Autowired DomainRepository domainRepository;

	@Autowired TransactionRepository transactionRepository;
	@Autowired TransactionEntryRepository transactionEntryRepository;
	@Autowired TransactionCommentRepository transactionCommentRepository;
	@Autowired TransactionLabelValueRepository transactionLabelValueRepository;
	@Autowired TransactionTypeService transactionTypeService;
	@Autowired TransactionTypeAccountRepository transactionTypeAccountRepository;
	@Autowired TransactionTypeLabelRepository transactionTypeLabelRepository;
	@Autowired AccountLabelValueConstraintRepository accountLabelValueConstraintRepository;
	@Autowired	AsyncLabelValueStreamV2 asyncLabelValueStreamV2;

	@Autowired ModelMapper mapper;
	@Autowired PeriodService periodService;
	@Autowired Properties properties;

	/** ;( **/
	public static final List<String> PERIOD_DOMAIN_SUMMARY_EXCLUDED_LABELS = Arrays.asList(LabelManager.LOGIN_EVENT_ID,
			LabelManager.PLAYER_BONUS_HISTORY_ID, CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE);

	public Transaction beginTransaction(
			@RequestParam String transactionTypeCode,
			@RequestParam String authorGuid) throws Status414AccountingTransactionDataValidationException {

		TransactionType tt = transactionTypeService.findByCode(transactionTypeCode);
		if (tt == null) throw new Status414AccountingTransactionDataValidationException("Transaction type code is invalid: " + transactionTypeCode);
		User owner = accountService.findOrCreateUser(authorGuid);
		Transaction t = Transaction.builder()
				.author(owner)
				.createdOn(new Date())
				.transactionType(tt)
				.cancelled(false)
				.open(true)
				.build();
		transactionRepository.save(t);
		return t;
	}

	public TransactionLabelValue label(
			@PathVariable Long transactionId,
			@RequestParam String key,
			@RequestParam String value) {

		TransactionLabelValue tlv = null;

		LabelValue lv = labelValueService.findOrCreate(key, value);

		tlv = TransactionLabelValue.builder()
				.transactionId(transactionId)
				.labelValue(lv)
				.build();
		tlv = transactionLabelValueRepository.save(tlv);

		return tlv;
	}

	@TimeThisMethod
	public TransactionLabelValue findOrCreateTransactionLabelValue(List<TransactionLabelValue> asyncAddedLabelValues,
				Long transactionId, String key, String value) {
		SW.start("lv");
		LabelValue lv = labelValueService.findOrCreate(key, value);
		SW.stop();
		SW.start("tlv.find");
		TransactionLabelValue tlv = null;
		List<TransactionLabelValue> tlvs = transactionLabelValueRepository.findByTransactionIdAndLabelValueId(
				transactionId, lv.getId());
		if (!tlvs.isEmpty()) {
			if (tlvs.size() > 1) {
				log.warn("Found {} duplicated TLV's for transaction {} on label {} and value {}", tlvs.size(),
						transactionId, key, value);
			}
			tlv = tlvs.get(0);
		}
		SW.stop();
		if (tlv == null) {
			SW.start("tlv.create");
			tlv = TransactionLabelValue.builder()
					.transactionId(transactionId)
					.labelValue(lv)
					.build();
			tlv = transactionLabelValueRepository.save(tlv);
			SW.stop();

			asyncAddedLabelValues.add(tlv);
		}
		return tlv;
	}

	public void labels(Long transactionId, String[] labels) {
		if (labels != null) {
			for (String labelAndValue: labels) {
                resolveLabel(labelAndValue).ifPresent(keyValue -> label(transactionId, keyValue.getKey(), keyValue.getValue()));
			}
		}
	}

	public TransactionComment comment(
			@PathVariable Long transactionId,
			@RequestParam String comment) {

		TransactionComment c = TransactionComment.builder()
				.transactionId(transactionId)
				.comment(comment)
				.build();
		transactionCommentRepository.save(c);

		return c;
	}

	public List<TransactionEntry> find(Long transactionId) {
//		Transaction t = transactionRepository.findOne(transactionId);
		List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(transactionId);
		return entries;
	}

	public TransactionEntry adjust(
			@PathVariable Long transactionId,
			@RequestParam String accountCode,
			@RequestParam String accountTypeCode,
			@RequestParam String currencyCode,
			@RequestParam Long amountCents,
			@RequestParam @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
			@RequestParam String domainName,
			@RequestParam String accountOwnerGuid
		) {

		Transaction t = transactionRepository.findOne(transactionId);
		if (t == null) throw new RuntimeException("The transaction " + transactionId + " could not be found");
		if (!t.getOpen()) throw new RuntimeException("The transaction " + transactionId + " is already closed");

		Currency currency = currencyService.findByCode(currencyCode);
		if (currency == null) throw new RuntimeException("Invalid currency code " + currencyCode);
		Account a = accountService.findOrCreate(accountCode, accountTypeCode, currencyCode, domainName, accountOwnerGuid, t.getTransactionType());

		Long balanceAfterAdjust = a.getBalanceCents() + amountCents;

		TransactionEntry te = TransactionEntry.builder()
				.transaction(t)
				.account(a)
				.date(date.toDate())
				.amountCents(amountCents)
				.postEntryAccountBalanceCents(balanceAfterAdjust)
				.build();

		transactionEntryRepository.save(te);

		return te;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void endTransaction(
		@PathVariable("transactionId") Long transactionId,
		TransactionStreamData transactionStreamData,
		List<LabelValue> summaryLabelValues
	) throws
		Status409DuplicateTransactionException,
		Status414AccountingTransactionDataValidationException,
		Status500InternalServerErrorException
	{
		StopWatch sw = new StopWatch(this.getClass().getSimpleName().toLowerCase() + ".endtransaction");

		sw.start("transactionRepository.findForUpdate");
		Transaction t = transactionRepository.findForUpdate(transactionId);
		if (t == null) throw new RuntimeException("The transaction " + transactionId + " could not be found");
		if (!t.getOpen()) throw new RuntimeException("The transaction " + transactionId + " is already closed");
		sw.stop();

		sw.start("trantypedefinitions");
		TransactionType tt = t.getTransactionType();
		List<TransactionTypeAccount> allowedAccountTypes = transactionTypeAccountRepository.findByTransactionType(tt);
		List<TransactionTypeLabel> tranLabels = transactionTypeLabelRepository.findByTransactionType(tt);
		List<TransactionLabelValue> labelValues = transactionLabelValueRepository.findByTransactionId(t.getId());
		List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(t.getId());
		List<AccountLabelValueConstraint> accountLabelValueConstraintList = new ArrayList<>();
		HashMap<String, LabelValue> uniqueLabelValues = new HashMap<>();
		sw.stop();

		long sum = 0;

		try {
			for (TransactionTypeLabel tranLabel : tranLabels) {
				boolean found = tranLabel.isOptional();

				for (TransactionLabelValue labelValue : labelValues) {
					if (tranLabel.getLabel().equals(labelValue.getLabelValue().getLabel().getName())) {
						found = true;
						if (tranLabel.isSummarize()) summaryLabelValues.add(labelValue.getLabelValue());
						if (tranLabel.getAccountTypeCode() != null && !tranLabel.getAccountTypeCode().isEmpty())
							uniqueLabelValues.put(tranLabel.getAccountTypeCode(), labelValue.getLabelValue());
						break;
					}
				}
				if (!found) {
					// Yea, it's hacky. I need to bypass the requirement on login_event_id/session_id instead of saving
					// a -1 value (but only on the roxor environments.) This is the quickest solution I can come up
					// with.
					if (tranLabel.getLabel().contentEquals(LabelManager.LOGIN_EVENT_ID) &&
							properties.getBalanceAdjustments().isBypassSessionIdRequirement()) {
						continue;
					}

					throw new Status414AccountingTransactionDataValidationException("The transaction type '" + tt.getCode() + "' used in transaction '" + t.getId()
							+ "' requires the use of the label '" + tranLabel.getLabel() + "'");
				}
			}

			sw.start("uniquecheck");
			if (!uniqueLabelValues.isEmpty()) {
				for (String key : uniqueLabelValues.keySet()) {
					AccountType accountType = accountTypeService.findByCode(key);
					AccountLabelValueConstraint constraint = isUnique(accountType, uniqueLabelValues.get(key), entries);
					if (constraint != null) {
						String error = "A transaction with this unique label value already exists ("
							+ uniqueLabelValues.get(key).getLabel() + ")";
						throw new Status409DuplicateTransactionException(constraint.getTransactionEntry().getTransaction().getId() + "", error);
					}

					for (TransactionEntry entry : entries) {
						if (entry.getAccount().getAccountType().getId().longValue() == accountType.getId().longValue()) {
							accountLabelValueConstraintList.add(AccountLabelValueConstraint.builder()
								.account(entry.getAccount())
								.labelValue(uniqueLabelValues.get(key))
								.transactionEntry(entry)
								.build());
						}
					}
				}
			}
			sw.stop();

			for (TransactionEntry e : entries) {
				sw.start("transactionentryvalidate " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents());

				boolean isDebit = e.getAmountCents() > 0;
				boolean isCredit = e.getAmountCents() < 0;

				if (!allowedAccountTypes.isEmpty()) {
					boolean found = false;
					for (TransactionTypeAccount allowedAccountType : allowedAccountTypes) {
						if (allowedAccountType.getAccountTypeCode().equals(e.getAccount().getAccountType().getCode())) {
							found = true;

							if (isDebit && !allowedAccountType.isDebit() && e.getAmountCents().longValue() != 0)
								throw new Status414AccountingTransactionDataValidationException("The transaction type does not allow this account to be debited: "
									+ allowedAccountType.toString());

							if (isCredit && !allowedAccountType.isCredit() && e.getAmountCents().longValue() != 0)
								throw new Status414AccountingTransactionDataValidationException("The transaction type does not allow this account to be credited: "
									+ allowedAccountType.toString());

							break;
						}
					}
					if (!found)
						throw new Status414AccountingTransactionDataValidationException("Account type '" + e.getAccount().getAccountType().getCode()
							+ "' is not allowed on the transaction type '" + tt.getCode() + "' for transaction id '" + t.getId() + "'");
				}
				sum += e.getAmountCents();

				sw.stop();
			}

			if (sum != 0)
				throw new Status414AccountingTransactionDataValidationException("The transaction " + transactionId + " is not balanced. " + sum);

			for (TransactionEntry e : entries) {
				sw.start("transactionentryadjustbal " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents());
				//TODO handle mutliple currencies and conversions
				Account account = accountRepository.findOne(e.getAccount().getId());
				account.setBalanceCents(account.getBalanceCents() + e.getAmountCents());
				account = accountRepository.save(account);
				sw.stop();
				if (properties.getBalanceAdjustments().isSummarizeEnabled()) {
					sw.start("transactionentry summaryaccount " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents());
					summaryAccountService.adjust(e);
					sw.stop();
					sw.start("transactionentry summaryaccounttrantype " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents());
					summaryAccountTransactionTypeService.adjust(e, tt);
					sw.stop();
				}
//			for (LabelValue lv: summaryLabelValues) {
//				sw.start("transactionentry lvadjust " + e.getAccount().getAccountCode().getCode() + " " + e.getAmountCents() + " " + lv.getLabel().getName() + "=" + lv.getValue());
//				summaryAccountLabelValueService.adjust(e, tt, lv);
//				sw.stop();
//			}
			}

			sw.start("transactionRepository.save");
			t.setClosedOn(new Date());
			t.setOpen(false);
			t = transactionRepository.save(t);
			sw.stop();

			sw.start("AccountLabelValueConstraint.save");
			for (AccountLabelValueConstraint constraint : accountLabelValueConstraintList) {
				accountLabelValueConstraintRepository.save(constraint);
			}
			sw.stop();

			sw.start("AffiliateTransaction.populate");
			//need to set it like this so paas by reference work, I think. Doing a builder will be like a value swap and this will produce an implicit clone of the object
			transactionStreamData.setTransactionId(t.getId());
			transactionStreamData.setOwnerGuid(entries.get(0).getAccount().getOwner().getGuid());
			transactionStreamData.setTransactionType(t.getTransactionType().getCode());
			sw.stop();

			if (sw.getTotalTimeMillis() > 1000) {
				log.error(sw.prettyPrint());
			} else {
				log.debug(sw.prettyPrint());
			}
		} catch (Status409DuplicateTransactionException e) {
			log.warn("endTransaction duplicate " + t + " " + tt + " " + allowedAccountTypes + " " + tranLabels + " " + labelValues +
				entries + " " + accountLabelValueConstraintList + " " + uniqueLabelValues
			);
			throw e;
		} catch (RuntimeException re) {

			log.error("endTransaction runtime exception " + t + " " + tt + " " + allowedAccountTypes + " " + tranLabels + " " + labelValues +
					entries + " " + accountLabelValueConstraintList + " " + uniqueLabelValues, re
			);
			throw re;

		} catch (Exception e) {

			log.error("endTransaction exception " + t + " " + tt + " " + allowedAccountTypes + " " + tranLabels + " " + labelValues +
					entries + " " + accountLabelValueConstraintList + " " + uniqueLabelValues, e
			);
			throw e;

		} finally {
			log.debug("endTransaction " + t + " " + tt + " " + allowedAccountTypes + " " + tranLabels + " " + labelValues +
					entries + " " + accountLabelValueConstraintList + " " + uniqueLabelValues
			);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void cancel(@PathVariable("transactionId") Long transactionId) {
		Transaction t = transactionRepository.findForUpdate(transactionId);
		if (t == null) throw new RuntimeException("The transaction " + transactionId + " could not be found");
		if (!t.getOpen()) throw new RuntimeException("The transaction " + transactionId + " is already closed");
		t.setCancelled(true);
		transactionRepository.save(t);
	}

	private AccountLabelValueConstraint isUnique(AccountType accountType, LabelValue labelValue, List<TransactionEntry> entries) {
		for(TransactionEntry entry : entries) {
			if(entry.getAccount().getAccountType().getId().longValue() == accountType.getId().longValue()) {
				AccountLabelValueConstraint constraint = accountLabelValueConstraintRepository
					.findOneByAccountIdAndLabelValueId(entry.getAccount().getId(), labelValue.getId());

				if(constraint != null) return constraint;

				return null;
			}
		}

		return null;
	}

	public DataTableResponse<lithium.service.accounting.objects.TransactionEntry> getTransactionsForDateRangeAndUserGuid(DataTableRequest request, DateTime startDate, DateTime endDate, String userGuid) throws Exception {
		//Page<TransactionEntry> dbPage = transactionEntryRepository.findByAccountOwnerGuidAndDateIsBetween(userGuid, startDate.toDate(), endDate.toDate(), request.getPageRequest());
		Page<TransactionEntry> dbPage = transactionEntryRepository.findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCode(userGuid, startDate.toDate(), endDate.toDate(), "PLAYER_BALANCE",request.getPageRequest());

		List<lithium.service.accounting.objects.TransactionEntry> teList = new ArrayList<>();
		for (TransactionEntry entry : dbPage.getContent()) {
			entry.setAmountCents(entry.getAmountCents()*-1);
			entry.setPostEntryAccountBalanceCents(entry.getPostEntryAccountBalanceCents()*-1);

			lithium.service.accounting.objects.TransactionEntry te = mapper.map(entry, lithium.service.accounting.objects.TransactionEntry.class);
			te.setTransactionType(entry.getTransaction().getTransactionType().getCode());
			teList.add(te);
		}

		final Page<lithium.service.accounting.objects.TransactionEntry> resultPage = new PageImpl<>(teList, request.getPageRequest(), teList.size());

//		Type pageType = new TypeToken<Page<lithium.service.accounting.objects.TransactionEntry>>() {}.getType();

//		Page<lithium.service.accounting.objects.TransactionEntry> resultPage = mapper.map(dbPage, pageType);

		return new DataTableResponse<lithium.service.accounting.objects.TransactionEntry>(request, resultPage);
	}

	public List<lithium.service.accounting.objects.LabelValue> findLabelsForTransaction(Long tranId) throws Exception {

		List<TransactionLabelValue> tranLabelValueList = transactionLabelValueRepository.findByTransactionId(tranId);

		Type listType = new TypeToken<List<lithium.service.accounting.objects.LabelValue>>() {}.getType();

		List<lithium.service.accounting.objects.LabelValue> result = mapper.map(tranLabelValueList, listType);

		return result;
	}

	/**
	 * This method should not start the transaction, it will be started after doing the findOrCreates for the period and the summaryAccountLabelValue.
	 * Both of those have a @Retryable on, with a very short backoff and delay, no transaction!
	 *
	 * @param entry
	 * @throws Exception
	 */
	@TimeThisMethod
	public void summarizeAdditionalTransactionLabels(TransactionLabelContainer entry) throws Exception {
		List<TransactionLabelValue> summaryLabelList = new ArrayList<>();
		List<TransactionLabelValue> asyncAddedLabelValues = new ArrayList<>();

		SW.start("labels");
		for (TransactionLabelBasic labelBasic : entry.getLabelList()) {
			TransactionLabelValue tlv = findOrCreateTransactionLabelValue(asyncAddedLabelValues,
					entry.getTransactionId(), labelBasic.getLabelName(), labelBasic.getLabelValue());

			if (labelBasic.isSummarize()) {
				log.debug("Going to add  " + labelBasic +" to the summary required list.");
				summaryLabelList.add(tlv);
			}
		}
		SW.stop();

		boolean hasLabelsToSummarise = !summaryLabelList.isEmpty();
		boolean wereLabelsAddedAsync = !asyncAddedLabelValues.isEmpty();

		if (wereLabelsAddedAsync || hasLabelsToSummarise) {
			log.debug("Got the transaction id: " + entry.getTransactionId() + " that will be used to find the tran entries.");
			Transaction transaction = findTransaction(entry.getTransactionId());
			List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(entry.getTransactionId());

			if (hasLabelsToSummarise) {
				for (TransactionEntry e : entries) {
					for (TransactionLabelValue tlv: summaryLabelList) {
						for (Granularity granularity: Granularity.values()) {
							SW.start("findOrCreatePeriod " + granularity);
							Period period = periodService.findOrCreatePeriod(new DateTime(e.getDate().getTime()), e.getAccount().getDomain(), granularity.id());
							SW.stop();
							SW.start("summaryAccountLabelValueService.findOrCreate");
							summaryAccountLabelValueService.findOrCreate(period, e.getAccount(), transaction.getTransactionType(), tlv.getLabelValue(), true);
							SW.stop();
						}
					}
				}
				summaryAccountLabelValueService.summarizeAdditionalTransactionLabelsTransactional(transaction, entries, summaryLabelList);
			}

			if (wereLabelsAddedAsync) {
				List<lithium.service.accounting.objects.TransactionEntry> tranEntriesCO = entries.stream()
						.map(te -> mapper.map(te, lithium.service.accounting.objects.TransactionEntry.class))
						.collect(Collectors.toList());
				List<TransactionLabelBasic> tranLabelBasicList = asyncAddedLabelValues.stream()
						.filter(tlv -> {
							return !PERIOD_DOMAIN_SUMMARY_EXCLUDED_LABELS.contains(
									tlv.getLabelValue().getLabel().getName());
						})
						.map(tlv -> {
							return TransactionLabelBasic.builder()
									.labelName(tlv.getLabelValue().getLabel().getName())
									.labelValue(tlv.getLabelValue().getValue())
									.summarize(true)
									.build();
						})
						.collect(Collectors.toList());

				asyncLabelValueStream.register(CompleteTransaction.builder()
						.transactionId(transaction.getId())
						.transactionType(transaction.getTransactionType().getCode())
						.createdOn(transaction.getCreatedOn().toString())
						.transactionEntryList(tranEntriesCO)
						.transactionLabelList(tranLabelBasicList)
						.build()
				);

				asyncLabelValueStreamV2.register(CompleteTransactionV2.builder()
						.transactionId(transaction.getId())
						.transactionType(transaction.getTransactionType().getCode())
						.createdOn(transaction.getCreatedOn().toString())
						.transactionEntryList(tranEntriesCO)
						.transactionLabelList(tranLabelBasicList)
						.testUser(tranEntriesCO.get(0).getAccount().getOwner().isTestAccount())
						.build()
				);
			}
		}
	}

	/**
	 * This method should not start the transaction, it will be started after doing the findOrCreates for the period and the summaryAccountLabelValue.
	 * Both of those have a @Retryable on, with a very short backoff and delay, no transaction!
	 * @param tranId
	 * @param summaryLabelValues
	 * @throws Exception
	 */
	@TimeThisMethod
	public void summarizeAdditionalTransactionLabels(long tranId, List<LabelValue> summaryLabelValues) throws Exception {
		log.debug("Got the transaction id: " + tranId + " that will be used to find the tran entries.");
		Transaction transaction = findTransaction(tranId);
		List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(tranId);

		for (TransactionEntry e : entries) {
			for (LabelValue lv: summaryLabelValues) {
				for (Granularity granularity: Granularity.values()) {
					SW.start("findOrCreatePeriod " + granularity);
					Period period = periodService.findOrCreatePeriod(new DateTime(e.getDate().getTime()), e.getAccount().getDomain(), granularity.id());
					SW.stop();
					SW.start("summaryAccountLabelValueService.findOrCreate");
					summaryAccountLabelValueService.findOrCreate(period, e.getAccount(), transaction.getTransactionType(), lv, true);
					SW.stop();
				}
			}
		}
		summaryAccountLabelValueService.summarizeAdditionalAuxTransactionLabelsTransactional(transaction, entries, summaryLabelValues);
	}

	/*
		NOTE: Here was removed legacy code: *aux label summary reverse* feature.
		You can find removed source using git blame on this line
	 */

	public Transaction findTransaction(Long internalTransactionId) {
		return transactionRepository.findOne(internalTransactionId);
	}

	/**
	 * Return the transaction linked to the specific unique constraint applied to the transaction.
	 * The account code is a provider specific code with a type usually being the general form of the code used.
	 * A typical account code would be CASINO_BET_PROVIDER with the type being CASINO_BET
	 * @param domainName
	 * @param ownerGuid
	 * @param currencyCode
	 * @param labelName
	 * @param labelValue
	 * @param originalAccountCode
	 * @param originalAccountTypeCode
	 * @return Transaction facade
	 * @throws Exception
	 */

	@Retryable(maxAttempts=5,backoff=@Backoff(random = true, delay=50, maxDelay = 1000), exclude={ NotRetryableErrorCodeException.class }, include=Exception.class)
	public Response<CompleteTransaction> findByLabelConstraint(
			String domainName,
			String ownerGuid,
			String currencyCode,
			String labelName,
			String labelValue,
			String originalAccountCode,
			String originalAccountTypeCode
			) throws Exception {

			// Transaction constrain lookup
			AccountLabelValueConstraint transactionConstraint = accountLabelValueConstraintRepository.
					findByAccountAccountCodeCodeAndAccountAccountTypeCodeAndAccountOwnerGuidAndAccountCurrencyCodeAndAccountDomainNameAndLabelValueLabelNameAndLabelValueValue(
							originalAccountCode, originalAccountTypeCode, ownerGuid, currencyCode, domainName, labelName, labelValue);

			if (transactionConstraint == null) {
				return Response.<CompleteTransaction>builder().status(Status.NOT_FOUND).build();
			}

			CompleteTransaction responseTransaction = new CompleteTransaction();
			// Core transaction lookup
			Transaction transaction = transactionConstraint.getTransactionEntry().getTransaction();
			responseTransaction.setTransactionId(transaction.getId());
			responseTransaction.setCreatedOn((new DateTime(transaction.getCreatedOn()).toString()));
			// Transaction entry lookup and mapping to client object type
			responseTransaction.setTransactionEntryList(new ArrayList<lithium.service.accounting.objects.TransactionEntry>());
			List<TransactionEntry> entries = transactionEntryRepository.findByTransactionId(transaction.getId());
			entries.forEach(entry -> {
				responseTransaction.getTransactionEntryList().add(mapper.map(entry, lithium.service.accounting.objects.TransactionEntry.class));
			});
			// Transaction label lookup and mapping to client label type
			responseTransaction.setTransactionLabelList(getLabelsForTransaction(transaction.getId()));

			return Response.<CompleteTransaction>builder().status(Status.OK).data(responseTransaction).build();
	}

	/**
	 * Helper method to assist in returning inter-service labels for transactions
	 * @param transactionId
	 * @return
	 */
	public ArrayList<TransactionLabelBasic> getLabelsForTransaction(long transactionId) {
		ArrayList<TransactionLabelBasic> responseList = new ArrayList<TransactionLabelBasic>();
		List<TransactionLabelValue> tlvList = transactionLabelValueRepository.findByTransactionId(transactionId);
		tlvList.forEach(tlv -> {
			responseList.add(
					TransactionLabelBasic.builder()
							.labelName(tlv.getLabelValue().getLabel().getName())
							.labelValue(tlv.getLabelValue().getValue())
							.build());
		});
		return responseList;
	}

	public List<TransactionLabelValueProjection> getLabelsForTransactionToDto(long transactionId){
	 return transactionLabelValueRepository.findByTransactionIdProjection(transactionId);
	}

	/**
	 * A convenience method allowing for multiple account and account type input pairs.
	 * @see TransactionService#findByLabelConstraint(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * @param domainName
	 * @param ownerGuid
	 * @param currencyCode
	 * @param labelName
	 * @param labelValue
	 * @param originalAccountCodeList
	 * @param originalAccountTypeCodeList
	 * @return Transaction facade list
	 * @throws Exception
	 */
	@Retryable(maxAttempts=5,backoff=@Backoff(random = true, delay=50, maxDelay = 1000), exclude={ NotRetryableErrorCodeException.class }, include=Exception.class)
	public Response<List<CompleteTransaction>> findByLabelConstraintList(
			String domainName,
			String ownerGuid,
			String currencyCode,
			String labelName,
			String labelValue,
			List<String> originalAccountCodeList,
			List<String> originalAccountTypeCodeList
			) throws Exception {

		if (originalAccountCodeList.size() != originalAccountTypeCodeList.size()) {
			log.error("Attempting to perform constraint lookups with unmatched account codes and account types. Owner: "+ ownerGuid +
					" Currency: "+ currencyCode + " AccCodeList: "+originalAccountCodeList +" AccTypeList: "+ originalAccountTypeCodeList);
			return Response.<List<CompleteTransaction>>builder().status(Status.INVALID_DATA).build();
		}

		List<CompleteTransaction> tranList = new ArrayList<>();

		for (int i=0; i < originalAccountCodeList.size(); ++i) {
			Response<CompleteTransaction> tranResp = findByLabelConstraint(domainName, ownerGuid, currencyCode, labelName, labelValue, originalAccountCodeList.get(i), originalAccountTypeCodeList.get(i));
			if (tranResp.getStatus() == Status.OK) {
				tranList.add(tranResp.getData());
			}
		}

		return  Response.<List<CompleteTransaction>>builder().status(Status.OK).data(tranList).build();
	}

	public Long findExternalTransactionId(String externalTransactionId, String labelName, String transactionTypeCode) {
		Specification<TransactionLabelValue> spec = Specification.where(
				TransactionLabelValueSpecifications.find(transactionTypeCode, labelName, externalTransactionId));
		try {
			TransactionLabelValue transactionLabelValue = transactionLabelValueRepository.findOne(spec).orElse(null);
			return Optional.ofNullable(transactionLabelValue)
					.map(TransactionLabelValue::getTransactionId)
					.orElse(null);
		} catch (Exception e) {
			log.error("Can't get transactionId ("+externalTransactionId, transactionTypeCode+") due " + e.getMessage(), e);
			return null;
		}
	}

	@TimeThisMethod
	public List<lithium.service.accounting.objects.TransactionEntry> findTransactionEntriesByExternalTransactionId(String externalTransactionId,
			String transactionTypeCode) {
		SW.start("findTransactionEntriesByExternalTransactionId.findExternalTransactionId");
		Long tranId = findExternalTransactionId(externalTransactionId, "transaction_id", transactionTypeCode);
		SW.stop();
		SW.start("findTransactionEntriesByExternalTransactionId.find");
		List<lithium.service.accounting.objects.TransactionEntry> tranEntries = new ArrayList<>();
		if (tranId != null) {
			ModelMapper modelMapper = new ModelMapper();
			tranEntries = find(tranId).stream()
			.map(transactionEntry -> {
				lithium.service.accounting.objects.TransactionEntry objTranEntry =
						new lithium.service.accounting.objects.TransactionEntry();
				modelMapper.map(transactionEntry, objTranEntry);
				return objTranEntry;
			}).collect(Collectors.toList());
		}
		SW.stop();
		return tranEntries;
	}

	public Boolean isUsedFreeBet(String guid,
	                                  String currencyCode,
	                                  String accountCodeName,
	                                  String accountTypeName) {
		User owner = accountService.findOrCreateUser(guid);
		Domain domain = domainRepository.findByName(guid.split("/")[0]);
		Currency currency = currencyService.findByCode(currencyCode);

		return transactionEntryRepository.countByAccount_OwnerAndAccount_DomainAndAccount_CurrencyAndAccount_AccountCode_CodeAndAccount_AccountType_Code(
				owner, domain, currency, accountCodeName, accountTypeName ) > 0;
	}

	@Transactional(rollbackFor = Exception.class)
	public void findAndDeleteTransactionsBatchById(List<Long> transactionIdsList) {
		try {
			Long deletedTransactionLabelValueConstraintCount = accountLabelValueConstraintRepository.deleteByTransactionEntryTransactionIdIn(transactionIdsList);
			log.debug("Marking {} entries for deletion from Transaction Label Value Constraint table", deletedTransactionLabelValueConstraintCount);
			Long deletedTranEntryCount = transactionEntryRepository.deleteAllByTransactionIdIn(transactionIdsList);
			log.debug("Marking {} entries for deletion from Transaction Entry table", deletedTranEntryCount);
			Long deletedTranLabelValueCount = transactionLabelValueRepository.deleteByTransactionIdIn(transactionIdsList);
			log.debug("Marking {} entries for deletion from Transaction Label Value table", deletedTranLabelValueCount);
			Long deletedTranCount = transactionRepository.deleteByIdIn(transactionIdsList);
			log.debug("Marking {} entries for deletion from Transaction table", deletedTranCount);
		}catch(Exception ex){
			log.error("Unable to process batch delete, rolling back");
			throw ex;
		}
		log.debug("Batch operation completed for data cleanup, handing back to service casino");
	}
}
