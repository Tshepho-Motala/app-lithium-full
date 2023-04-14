package lithium.service.accounting.provider.internal.services;

import lithium.cashier.CashierTransactionLabels;
import lithium.casino.CasinoTransactionLabels;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.LabelValue;
import lithium.service.accounting.objects.NetDeposit;
import lithium.service.accounting.objects.frontend.FrontendSummary;
import lithium.service.accounting.objects.frontend.FrontendTransaction;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.objects.group.SummaryAccountCodeGroup;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.PeriodRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryAccountTransactionTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryAccountSpecifications;
import lithium.service.accounting.provider.internal.data.repositories.specifications.SummaryAccountTransactionTypeSpecifications;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.objects.Granularity;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlayerService {
	@Autowired SummaryAccountTransactionTypeRepository summaryAccountTransactionTypeRepository;
	@Autowired TransactionLabelValueRepository transactionLabelValueRepository;
	@Autowired TransactionEntryRepository transactionEntryRepository;
	@Autowired TransactionRepository transactionRepository;
	@Autowired SummaryAccountRepository summaryAccountRepository;
	@Autowired SummaryAccountRepository summaryAccountRepo;
	@Autowired TransactionService transactionService;
	@Autowired PeriodRepository periodRepo;
	@Autowired CurrencyRepository currencyRepo;
	@Autowired UserRepository userRepo;
	@Autowired LithiumMetricsService metrics;
	@Autowired MessageSource messageSource;

	private static final String CASHIER_DEPOSIT_CODE = "CASHIER_DEPOSIT";
	private static final String CASHIER_PAYOUT_CODE = "CASHIER_PAYOUT";
	private static final int PAGE_SIZE = 100;

	@TimeThisMethod
	protected Long netLossForPlayer(String domainName, Long periodId, String currency, String playerGuid) throws Exception {
		SW.start(playerGuid+"_"+currency+"_"+periodId);
		SW.stop();
		SummaryAccountCodeGroup resultGroup = SummaryAccountCodeGroup.builder()
				.creditCents(0L)
				.debitCents(0L)
				.tranCount(0L)
				.period(periodRepo.findOne(periodId))
				.currency(currencyRepo.findByCode(currency))
				.owner(userRepo.findByGuid(playerGuid))
				.build();

		//Get a list of grouped values on accountCodes for the player for a specific period
		List<SummaryAccountCodeGroup> accountTypeList = summaryAccountRepo.groupBy(resultGroup.getPeriod(), resultGroup.getCurrency(), resultGroup.getOwner());
		log.trace("Net-loss player result: " + accountTypeList);
		//Extract the player balance code as the main value to work with and remove the deposit, withdrawal and manual adjustment from net-play value that we will be returning
		// The adjustments and deposit and withdrawals are contra accounts, so just summing the debit and credit will negate their original effect on total play value
		for (SummaryAccountCodeGroup summaryAccountCodeGroup : accountTypeList) {
			switch (summaryAccountCodeGroup.getAccountCode().getCode()) {
				case "PLAYER_BALANCE":
				case "GF_MANUAL_BALANCE_ADJUST":
				case "PLAYER_BALANCE_OPERATOR_MIGRATION":
				case "CASHIER_DEPOSIT":
				case "PLAYER_BALANCE_PENDING_WITHDRAWAL":
				case "CASHIER_PAYOUT": {
					resultGroup.setCreditCents(resultGroup.getCreditCents() + summaryAccountCodeGroup.getCreditCents());
					resultGroup.setDebitCents(resultGroup.getDebitCents() + summaryAccountCodeGroup.getDebitCents());
					break;
				}
				default:
					break;
			}
		}
		Long netLoss = resultGroup.getDebitCents() - resultGroup.getCreditCents();
		log.debug("Net-loss for player: " + resultGroup);
		return netLoss;
	}

	//This will need to be updated if we have new methods in future for affecting player balance but it is not seen as a win/loss to house
	@TimeThisMethod
	public Response<Long> findNetLossForPlayer(String domainName, Long periodId, String currency, String playerGuid) throws Exception {
		return Response.<Long>builder().status(Status.OK).data(netLossForPlayer(domainName, periodId, currency, playerGuid)).build();
	}

	public FrontendSummary summary(String guid, String dateStart, String dateEnd, String currency) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date ds = new DateTime(sdf.parse(dateStart)).withTimeAtStartOfDay().toDate();
		Date de = new DateTime(sdf.parse(dateEnd)).plusDays(1).withTimeAtStartOfDay().toDate();

		int granularity = Granularity.GRANULARITY_DAY.granularity();
		// TODO: This needs to be expanded to do a smarter lookup of periods, instead of doing counts on day summaries.
//		Period period = new Period(dateStart, dateEnd);
//		log.debug("Period : y:"+period.getYears()+" m:"+period.getMonths()+" w:"+period.getWeeks()+" d:"+period.getDays());

		/**
		 * These are transactions on PLAYER_BALANCE account, but they need to be excluded.
		 */
		List<String> excludedTransactionTypes = new ArrayList<>();
		excludedTransactionTypes.add("SPORTS_RESERVE");
		excludedTransactionTypes.add("SPORTS_RESERVE_CANCEL");
		excludedTransactionTypes.add("SPORTS_RESERVE_RETURN");

		List<SummaryAccountTransactionType> summaryAccountList = summaryAccountTransactionType(guid, currency,
			"PLAYER_BALANCE", excludedTransactionTypes, granularity, ds, de);

		long tranCount = 0L;
		long debitCents = 0L;
		long creditCents = 0L;
		for (SummaryAccountTransactionType summaryAccount: summaryAccountList) {
			tranCount += summaryAccount.getTranCount();
			debitCents += summaryAccount.getDebitCents();
			creditCents += summaryAccount.getCreditCents();
		}
		return FrontendSummary.builder()
			.tranCount(tranCount)
			.debitCents(debitCents)
			.debit(CurrencyAmount.fromCents(debitCents).toAmount())
			.creditCents(creditCents)
			.credit(CurrencyAmount.fromCents(creditCents).toAmount())
			.dateStart(ds)
			.dateEnd(de)
			.build();
	}

	private List<lithium.service.accounting.objects.SummaryAccount> summaryAccount(
		String guid,
		String currencyCode,
		String accountCode,
		int granularity,
		Date dateStart,
		Date dateEnd
	) {
		String logMsg = ("retrieving player account summary for request : g: "+guid+", c: "+currencyCode+", g: "+granularity+", ds: "+dateStart+", de: "+dateEnd);
		log.debug(logMsg);
		Specification<lithium.service.accounting.provider.internal.data.entities.SummaryAccount> spec = Specification.where(
			SummaryAccountSpecifications.find(
				guid.split("/")[0],
				currencyCode,
				accountCode,
				granularity,
				dateStart,
				dateEnd,
				guid
			)
		);
		List<lithium.service.accounting.objects.SummaryAccount> results = new ArrayList<>();
		List<lithium.service.accounting.provider.internal.data.entities.SummaryAccount> summaryAccounts = summaryAccountRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "period.dateStart"));
		log.trace("summaryAccounts : "+summaryAccounts);

		for (lithium.service.accounting.provider.internal.data.entities.SummaryAccount summaryAccount:summaryAccounts) {
			log.trace("summaryAccount : "+summaryAccount);
			results.add(
				lithium.service.accounting.objects.SummaryAccount.builder()
				.tranCount(summaryAccount.getTranCount())
				.debitCents(summaryAccount.getDebitCents())
				.creditCents(summaryAccount.getCreditCents())
				.openingBalanceCents(summaryAccount.getOpeningBalanceCents())
				.closingBalanceCents(summaryAccount.getClosingBalanceCents())
				.dateStart(summaryAccount.getPeriod().getDateStart())
				.dateEnd(summaryAccount.getPeriod().getDateEnd())
				.build()
			);
		};
		return results;
	}

	private List<SummaryAccountTransactionType> summaryAccountTransactionType(
		String guid,
		String currencyCode,
		String accountCode,
		List<String> excludedTransactionTypes,
		int granularity,
		Date dateStart,
		Date dateEnd
	) {
		String strExcludedTranTypes = excludedTransactionTypes.stream().map(String::toString)
			.collect(Collectors.joining(", "));
		String logMsg = ("retrieving player account summary for request : g: "+guid
			+", c: "+currencyCode+", g: "+granularity+", ds: "+dateStart+", de: "+dateEnd+", ac: "+accountCode
			+", excludedTransactionTypes: "+strExcludedTranTypes);
		log.debug(logMsg);
		Specification<lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType> spec =
			Specification.where(
				SummaryAccountTransactionTypeSpecifications.findAllByAccountExcludeTranTypes(
				guid.split("/")[0],
				currencyCode,
				accountCode,
				excludedTransactionTypes,
				granularity,
				dateStart,
				dateEnd,
				guid
			)
		);
		List<SummaryAccountTransactionType> summaryAccounts =
			summaryAccountTransactionTypeRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "period.dateStart"));
		log.trace("summaryAccounts : "+summaryAccounts);
		return summaryAccounts;
	}

	public DataTableResponse<FrontendTransaction> transactions(
		String guid,
		String dateStart,
		String dateEnd,
		String currencyCode,
		int pageSize,
		int page,
		Locale locale
	) throws Exception {
		if (pageSize > 100) pageSize = PAGE_SIZE;
		DataTableRequest request = new DataTableRequest();
		request.setPageRequest(PageRequest.of(page, pageSize));

		String logMsg = ("retrieving player account transactions for request : g: "+guid+", ds: "+dateStart+", de: "+dateEnd+", c: "+currencyCode+", p: "+page+", ps: "+pageSize+", locale: "+locale);
		log.debug(logMsg);

		DataTableResponse<FrontendTransaction> dtResponse = getTransactionsForDateRangeAndUserGuid(request, dateStart, dateEnd, guid, currencyCode, locale);

		log.trace("dtResponse :: "+dtResponse);
		return dtResponse;
	}

	private DataTableResponse<FrontendTransaction> getTransactionsForDateRangeAndUserGuid(DataTableRequest request, String dateStart, String dateEnd, String userGuid, String currencyCode, Locale locale) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date ds = new DateTime(sdf.parse(dateStart)).withTimeAtStartOfDay().toDate();
		Date de = new DateTime(sdf.parse(dateEnd)).withTime(23, 59, 59, 999).toDate();

		String logMsg = ("getTransactionsForDateRangeAndUserGuid : g: "+userGuid+", ds: "+ds+", de: "+de+", c: "+currencyCode+", locale: "+locale);
		log.debug(logMsg);

		List<String> excludedAccountCodes = new ArrayList<>();
		excludedAccountCodes.add("PLAYER_BALANCE_PENDING_WITHDRAWAL");
		excludedAccountCodes.add("PLAYER_BALANCE_CASINO_BONUS");
		excludedAccountCodes.add("PLAYER_BALANCE_CASINO_BONUS_PENDING");

		/**
		 * These are transactions on PLAYER_BALANCE account, but they need to be excluded.
		 */
		List<String> excludedTransactionTypes = new ArrayList<>();
		excludedTransactionTypes.add("SPORTS_RESERVE");
		excludedTransactionTypes.add("SPORTS_RESERVE_CANCEL");
		excludedTransactionTypes.add("SPORTS_RESERVE_RETURN");
		excludedTransactionTypes.add("TRANSFER_TO_CASINO_BONUS");
		excludedTransactionTypes.add("TRANSFER_FROM_CASINO_BONUS");
		excludedTransactionTypes.add("TRANSFER_TO_CASINO_BONUS_PENDING");
		excludedTransactionTypes.add("TRANSFER_FROM_CASINO_BONUS_PENDING");

		Page<lithium.service.accounting.provider.internal.data.entities.TransactionEntry> dbPage = transactionEntryRepository.findByAccountOwnerGuidAndDateIsBetweenAndAccountAccountTypeCodeAndAccountCurrencyCodeAndAccountAccountCodeCodeNotInAndTransactionTransactionTypeCodeNotInOrderByIdDesc(userGuid, ds, de, "PLAYER_BALANCE", currencyCode, excludedAccountCodes, excludedTransactionTypes, request.getPageRequest());

		List<FrontendTransaction> teList = new ArrayList<>();
		Map<String, String> transactionTypeTranslations = new HashMap<>();
		for (lithium.service.accounting.provider.internal.data.entities.TransactionEntry entry : dbPage.getContent()) {
			String tranType = entry.getTransaction().getTransactionType().getCode();
			String tranTypeDisplay = getTranTypeDisplay(tranType, transactionTypeTranslations, locale);
			FrontendTransaction tf = FrontendTransaction.builder()
				.id(entry.getTransaction().getId())
				.amountCents(entry.getAmountCents()*-1)
				.amount(CurrencyAmount.fromCents(entry.getAmountCents()*-1).toAmount())
				.date(entry.getDate())
				.postEntryAccountBalanceCents(entry.getPostEntryAccountBalanceCents()*-1)
				.postEntryAccountBalance(CurrencyAmount.fromCents(entry.getPostEntryAccountBalanceCents()*-1).toAmount())
//				.tranEntryAccountType(entry.getAccount().getAccountType().getCode())
				.tranEntryAccountCode(entry.getAccount().getAccountCode().getCode())
				.transactionType(tranType)
				.transactionTypeDisplay(tranTypeDisplay)
				.build();
//			List<TransactionLabelValue> transactionLabelValues = transactionLabelValueRepository.findByTransactionId(entry.getTransactionId());
			List<LabelValue> labelValues = transactionService.findLabelsForTransaction(entry.getTransaction().getId());
			for (LabelValue lv : labelValues) {
				if (lv.getValue() == null) continue;
				switch (lv.getLabel().getName()) {
					case CashierTransactionLabels.PROCESSOR_REFERENCE:
						tf.setProcessorReference(lv.getValue());
						break;
					case CashierTransactionLabels.PROCESSOR_DESCRIPTION:
						tf.setProcessorDescription(lv.getValue());
						break;
					case CasinoTransactionLabels.PROVIDER_GUID_LABEL:
						tf.setProviderGuid(lv.getValue());
						break;
					case CasinoTransactionLabels.BONUS_REVISION_ID: {
						Long bonusRevisionId = Long.parseLong(lv.getValue());
						tf.setBonusRevisionId(bonusRevisionId);
						break;
					}
					case CasinoTransactionLabels.TRAN_ID_LABEL:
						tf.setExternalTranId(lv.getValue());
						break;
					case CasinoTransactionLabels.GAME_GUID_LABEL:
						tf.setGameGuid(lv.getValue());
						break;
					case CashierTransactionLabels.PROCESSING_METHOD_LABEL:
						tf.setProcessingMethod(lv.getValue());
						break;
					case CasinoTransactionLabels.ACCOUNTING_CLIENT_RESPONSE_LABEL:
						tf.setAccountingClientExternalId(lv.getValue());
						break;
				}
			}
			teList.add(tf);
		}

		final Page<FrontendTransaction> resultPage = new PageImpl<>(teList, request.getPageRequest(), dbPage.getTotalElements());

//		Type pageType = new TypeToken<Page<lithium.service.accounting.objects.TransactionEntry>>() {}.getType();

//		Page<lithium.service.accounting.objects.TransactionEntry> resultPage = mapper.map(dbPage, pageType);

		return new DataTableResponse<FrontendTransaction>(request, resultPage);
	}

	private String getTranTypeDisplay(String tranType, Map<String, String> transactionTypeTranslations, Locale locale) {
		String translationKey = "GLOBAL.TRANTYPE." + tranType + ".LABEL";
		String tranTypeDisplay = null;
		if (transactionTypeTranslations.get(translationKey) != null) {
			tranTypeDisplay = transactionTypeTranslations.get(translationKey);
		}
		if (tranTypeDisplay != null && !translationKey.contentEquals(tranTypeDisplay)) {
			return tranTypeDisplay;
		}
		tranTypeDisplay = messageSource.getMessage(translationKey, null, locale);
		transactionTypeTranslations.put(translationKey, tranTypeDisplay);
		if (translationKey.contentEquals(tranTypeDisplay)) {
			tranTypeDisplay = tranType.substring(0, 1).toUpperCase() + tranType.substring(1).toLowerCase();
			tranTypeDisplay = tranTypeDisplay.replace("_", " ");
		}
		return tranTypeDisplay;
	}

	public NetDeposit calculateNetDeposit(String guid) {

		SummaryAccountTransactionType deposits = summaryAccountTransactionTypeRepository.findByAccountOwnerGuidAndAccountAccountCodeCodeAndTransactionTypeCodeAndPeriodGranularity(
				guid,
				CASHIER_DEPOSIT_CODE,
				CASHIER_DEPOSIT_CODE,
				Granularity.GRANULARITY_TOTAL.granularity());

		SummaryAccountTransactionType withdrawals = summaryAccountTransactionTypeRepository.findByAccountOwnerGuidAndAccountAccountCodeCodeAndTransactionTypeCodeAndPeriodGranularity(
				guid,
				CASHIER_PAYOUT_CODE,
				CASHIER_PAYOUT_CODE,
				Granularity.GRANULARITY_TOTAL.granularity());

		long depositTranCount = 0L;
		long debitCents = 0L;
		long creditCents = 0L;
		long withdrawalTranCount = 0L;
		String currency = "";
		if (deposits != null) {
			depositTranCount = deposits.getTranCount();
			debitCents = deposits.getDebitCents();
			currency = deposits.getAccount().getCurrency().getCode();
		}

		if (withdrawals != null) {
			withdrawalTranCount = withdrawals.getTranCount();
			creditCents = withdrawals.getCreditCents();
			if(currency.isEmpty()) {
				currency = withdrawals.getAccount().getCurrency().getCode();
			}
		}

		return NetDeposit.builder()
				.summaryDepositCents(debitCents)
				.summaryDepositCount(depositTranCount)
				.summaryWithdrawalCents(creditCents)
				.summaryWithdrawalCount(withdrawalTranCount)
				.currency(currency)
				.build();
	}
}
