package lithium.service.accounting.domain.summary.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.SystemSummaryReconciliationClient;
import lithium.service.accounting.domain.summary.config.Properties;
import lithium.service.accounting.domain.summary.context.SummaryContext;
import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomain;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainLabelValue;
import lithium.service.accounting.domain.summary.storage.entities.SummaryDomainTransactionType;
import lithium.service.accounting.domain.summary.storage.entities.SummaryReconciliation;
import lithium.service.accounting.domain.summary.storage.repositories.PeriodRepository;
import lithium.service.accounting.domain.summary.storage.repositories.SummaryReconciliationRepository;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationRequest;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.shards.objects.Shard;
import lithium.shards.ShardsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
public class SummaryReconciliationService {
	@Autowired private SummaryReconciliationService self;
	@Autowired private AccountCodeService accountCodeService;
	@Autowired private CurrencyService currencyService;
	@Autowired private DomainService domainService;
	@Autowired private LabelValueService labelValueService;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private ModelMapper modelMapper;
	@Autowired private PeriodRepository periodRepository;
	@Autowired private Properties properties;
	@Autowired private ShardsRegistry shardsRegistry;
	@Autowired private SummaryDomainService summaryDomainService;
	@Autowired private SummaryDomainLabelValueService summaryDomainLabelValueService;
	@Autowired private SummaryDomainTransactionTypeService summaryDomainTransactionTypeService;
	@Autowired private SummaryReconciliationRepository repository;
	@Autowired private TransactionTypeService transactionTypeService;

	private static final String SHARD_POOL = "SummaryReconciliation";

	private LocalDateTime pausedUntil = null;

	@Transactional(rollbackOn = Exception.class)
	@TimeThisMethod(infoThresholdMillis = 3500, warningThresholdMillis = 4000, errorThresholdMillis = 4500)
	public void process(String shardKey, String dateStr, String dateFormat)
			throws Status500InternalServerErrorException, Status510AccountingProviderUnavailableException {
		boolean adHoc = ((dateStr != null) && (dateFormat != null));

		LocalDateTime nowDt = LocalDateTime.now();

		if (!adHoc) {
			if (pausedUntil != null && nowDt.isBefore(pausedUntil)) {
				log.trace("Summary reconciliation job paused | nowDt: {}, pausedUntil: {}", nowDt, pausedUntil);
				return;
			} else {
				if (pausedUntil != null) {
					log.info("Unpaused summary reconciliation job | nowDt: {}, pausedUntil: {}", nowDt, pausedUntil);
					pausedUntil = null;
				}
			}
		}

		Shard shard = shardsRegistry.get(SHARD_POOL, shardKey);
		log.trace("Processing summary reconciliation | properties: {}, shard: {}, adHoc: {}, dateStr: {},"
						+ " dateFormat: {}",
				properties.getReconciliation(), shard, adHoc, dateStr, dateFormat);

		if (dateFormat == null) {
			dateFormat = properties.getReconciliation().getSeedDateFormat();
		}

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
		LocalDate date = null;

		SummaryReconciliation progress = null;
		if (!adHoc) {
			progress = repository.findOne(1L);
			if (progress == null) {
				LocalDate seedDate = LocalDate.parse(
						properties.getReconciliation().getSeedDateValue(), dateTimeFormatter).minusDays(1);
				progress = SummaryReconciliation.builder()
						.id(1L)
						.lastDateProcessed(seedDate)
						.build();
				progress = repository.save(progress);
			}
			date = progress.getLastDateProcessed().plusDays(1);
		} else {
			date = LocalDate.parse(dateStr, dateTimeFormatter);
			log.info("Running ad-hoc for {}", date);
		}

		log.trace("now: {}, reconciliationDate: {}", nowDt.toLocalDate(), date);
		if (nowDt.toLocalDate().isEqual(date)) {
			log.warn("Current day!! It is not safe to compare data as the period is still open. Aborting... | now: {},"
					+ " reconciliationDate: {}", nowDt.toLocalDate(), date);
			if (!adHoc) {
				pausedUntil = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0);
				log.info("Pausing summary reconciliation job | pausedUntil: {}", pausedUntil);
			}
			return;
		}
		if (date.isAfter(nowDt.toLocalDate())) {
			log.error("Future date for reconciliation is not possible | {}", date);
			return;
		}

		boolean process = true;
		int iteration = 0;
		SummaryReconciliationRequest request = SummaryReconciliationRequest.builder()
				.date(date.format(dateTimeFormatter))
				.dateFormat(dateFormat)
				.dataFetchSizePerType(properties.getReconciliation().getDataFetchSizePerType())
				.summaryDomainPageNum(0)
				.summaryDomainTransactionTypePageNum(0)
				.summaryDomainLabelValuePageNum(0)
				.build();
		while (process) {
			iteration++;
			SW.start("accounting.query." + iteration);
			SummaryReconciliationResponse response = getSystemSummaryReconciliationClient().get()
					.getSummaryDataForDate(request);
			SW.stop();
			if (response.isSafeToProcessThisDate()) {
				SW.start("processExtSummaries." + iteration);
				processExtSummaries(shard.getUuid(), response);
				SW.stop();

				process = updateState(request, response);

				if (!process) {
					if (!adHoc) {
						progress.setLastDateProcessed(date);
						progress = repository.save(progress);
					}

					log.trace("Completed summary reconciliation for {} in {} iterations", date, iteration);
				}
			} else {
				process = false;
				String msg = "SVC-accounting has indicated that it is not yet safe to process this date | " + date;
				if (!adHoc) {
					pausedUntil = LocalDateTime.now().plusHours(1);
					log.warn(msg + " | pausedUntil: {}", pausedUntil);
				} else {
					log.warn(msg);
				}
				if (iteration > 1) {
					// Need to rollback. Unlikely that the safety of processing this date will change after the 1st
					// iteration.
					throw new Status500InternalServerErrorException(msg);
				}
			}
		}
	}

	public void processCurrentAndTotalSummaryData(String shardKey)
			throws Status510AccountingProviderUnavailableException, Status500InternalServerErrorException {
		SummaryReconciliation reconciliation = repository.findOne(2L);
		if (reconciliation != null) {
			log.warn("processCurrentAndTotalSummaryData is already running. It will be removed automatically when the"
					+ " process completes successfully. If there was an error and the process"
					+ " stopped, and now you need to restart the process, then manually delete the record with ID 2"
					+ " from lithium_accounting_domain_summary.summary_reconciliation.");
			return;
		} else {
			repository.save(SummaryReconciliation.builder().id(2L).build());
			self.processCurrentAndTotalSummaryDataTransactional(shardKey);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	@TimeThisMethod
	public void processCurrentAndTotalSummaryDataTransactional(String shardKey) throws Status500InternalServerErrorException,
			Status510AccountingProviderUnavailableException {
		Shard shard = shardsRegistry.get(SHARD_POOL, shardKey);

		boolean process = true;
		int iteration = 0;
		SummaryReconciliationRequest request = SummaryReconciliationRequest.builder()
				.dataFetchSizePerType(properties.getReconciliation().getDataFetchSizePerType())
				.summaryDomainPageNum(0)
				.summaryDomainTransactionTypePageNum(0)
				.summaryDomainLabelValuePageNum(0)
				.build();
		while (process) {
			iteration++;
			SW.start("accounting.query." + iteration);
			SummaryReconciliationResponse response = getSystemSummaryReconciliationClient().get()
					.getCurrentAndTotalSummaryData(request);
			SW.stop();
			SW.start("processExtSummaries." + iteration);
			processExtSummaries(shard.getUuid(), response);
			SW.stop();

			process = updateState(request, response);

			log.info("processCurrentAndTotalSummaryData | completed iteration: {}, summaryDomainNextPage: {},"
					+ " summaryDomainTransactionTypeNextPage: {}, summaryDomainLabelValueNextPage: {}",
					iteration, request.getSummaryDomainPageNum(),
					request.getSummaryDomainTransactionTypePageNum(),
					request.getSummaryDomainLabelValuePageNum());
		}

		repository.deleteById(2L);

		log.info("Completed summary reconciliation for current and total data in {} iterations", iteration);
	}

	private boolean updateState(SummaryReconciliationRequest request, SummaryReconciliationResponse response) {
		int listsCompleted = 0;

		if (response.getSummaryDomainList().size() < request.getDataFetchSizePerType()) {
			request.setSummaryDomainPageNum(-1);
			listsCompleted++;
		} else {
			request.setSummaryDomainPageNum(request.getSummaryDomainPageNum() + 1);
		}

		if (response.getSummaryDomainTransactionTypeList().size() < request.getDataFetchSizePerType()) {
			request.setSummaryDomainTransactionTypePageNum(-1);
			listsCompleted++;
		} else {
			request.setSummaryDomainTransactionTypePageNum(request.getSummaryDomainTransactionTypePageNum() + 1);
		}

		if (response.getSummaryDomainLabelValueList().size() < request.getDataFetchSizePerType()) {
			request.setSummaryDomainLabelValuePageNum(-1);
			listsCompleted++;
		} else {
			request.setSummaryDomainLabelValuePageNum(request.getSummaryDomainLabelValuePageNum() + 1);
		}

		return !(listsCompleted == 3);
	}

	private void processExtSummaries(String shard, SummaryReconciliationResponse response) {
		log.debug("summaryData.summaryDomainList: {}", response.getSummaryDomainList());
		log.debug("summaryData.summaryDomainLabelValueList: {}", response.getSummaryDomainLabelValueList());
		log.debug("summaryData.summaryDomainTransactionTypeList: {}",
				response.getSummaryDomainTransactionTypeList());

		SW.start("processExtSummaryDomain");
		for (lithium.service.accounting.objects.SummaryDomain extData: response.getSummaryDomainList()) {
			processExtSummaryDomain(shard, extData);
		}
		SW.stop();

		SW.start("processExtSummaryDomainLabelValue");
		for (lithium.service.accounting.objects.SummaryDomainLabelValue extData:
				response.getSummaryDomainLabelValueList()) {
			processExtSummaryDomainLabelValue(shard, extData);
		}
		SW.stop();

		SW.start("processExtSummaryDomainTransactionType");
		for (lithium.service.accounting.objects.SummaryDomainTransactionType extData:
				response.getSummaryDomainTransactionTypeList()) {
			processExtSummaryDomainTransactionType(shard, extData);
		}
		SW.stop();
	}

	private void processExtSummaryDomain(String shard, lithium.service.accounting.objects.SummaryDomain extData) {
		SummaryContext context = createContext(extData.getPeriod().getDomain(), extData.getPeriod(),
				extData.getAccountCode(), extData.getCurrency(), null, null);
		SummaryDomain intData = summaryDomainService.findByPeriodAndAccountCodeAndCurrency(context.getPeriod(),
				context.getAccountCode(), context.getCurrency());
		log.trace("processExtSummaryDomain | intData: {}, context: {}", intData, context);
		if (intData == null) {
			intData = SummaryDomain.builder()
					.shard(shard)
					.accountCode(context.getAccountCode())
					.currency(context.getCurrency())
					.period(context.getPeriod())
					.debitCents(extData.getDebitCents())
					.creditCents(extData.getCreditCents())
					.openingBalanceCents(extData.getOpeningBalanceCents())
					.closingBalanceCents(extData.getClosingBalanceCents())
					.tranCount(extData.getTranCount())
					.build();
			intData = summaryDomainService.save(intData);
		} else {
			if (isSummaryDataMismatched(extData, intData)) {
				if (properties.getReconciliation().isLogErrorOnMismatchedDataEnabled()) {
					log.error("SummaryDomain data mismatched | extData: {}, intData: {}", extData, intData);
				}
				if (properties.getReconciliation().isUpdateMismatchedDataEnabled()) {
					updateMismatchedSummaryData(shard, extData, intData);
				}
			}
		}
	}

	private void processExtSummaryDomainLabelValue(String shard,
	        lithium.service.accounting.objects.SummaryDomainLabelValue extData) {
		SummaryContext context = createContext(extData.getPeriod().getDomain(), extData.getPeriod(),
				extData.getAccountCode(), extData.getCurrency(), extData.getLabelValue(),
				extData.getTransactionType());
		SummaryDomainLabelValue intData = summaryDomainLabelValueService
				.findByAccountCodeAndTransactionTypeAndCurrencyAndPeriodAndLabelValue(
						context.getAccountCode(), context.getTransactionType(), context.getCurrency(),
						context.getPeriod(), context.getLabelValue());
		log.trace("processExtSummaryDomainLabelValue | intData: {}, context: {}", intData, context);
		if (intData == null) {
			intData = SummaryDomainLabelValue.builder()
					.shard(shard)
					.accountCode(context.getAccountCode())
					.transactionType(context.getTransactionType())
					.currency(context.getCurrency())
					.period(context.getPeriod())
					.labelValue(context.getLabelValue())
					.debitCents(extData.getDebitCents())
					.creditCents(extData.getCreditCents())
					.tranCount(extData.getTranCount())
					.build();
			intData = summaryDomainLabelValueService.save(intData);
		} else {
			if (isSummaryDataMismatched(extData, intData)) {
				if (properties.getReconciliation().isLogErrorOnMismatchedDataEnabled()) {
					log.error("SummaryDomain data mismatched | extData: {}, intData: {}", extData, intData);
				}
				if (properties.getReconciliation().isUpdateMismatchedDataEnabled()) {
					updateMismatchedSummaryData(shard, extData, intData);
				}
			}
		}
	}

	private void processExtSummaryDomainTransactionType(String shard,
			lithium.service.accounting.objects.SummaryDomainTransactionType extData) {
		SummaryContext context = createContext(extData.getPeriod().getDomain(), extData.getPeriod(),
				extData.getAccountCode(), extData.getCurrency(), null,
				extData.getTransactionType());
		SummaryDomainTransactionType intData = summaryDomainTransactionTypeService
				.findByAccountCodeAndTransactionTypeAndCurrencyAndPeriod(context.getAccountCode(),
						context.getTransactionType(), context.getCurrency(), context.getPeriod());
		log.trace("processExtSummaryDomainTransactionType | intData: {}, context: {}", intData, context);
		if (intData == null) {
			intData = SummaryDomainTransactionType.builder()
					.shard(shard)
					.accountCode(context.getAccountCode())
					.transactionType(context.getTransactionType())
					.currency(context.getCurrency())
					.period(context.getPeriod())
					.debitCents(extData.getDebitCents())
					.creditCents(extData.getCreditCents())
					.tranCount(extData.getTranCount())
					.build();
			intData = summaryDomainTransactionTypeService.save(intData);
		} else {
			if (isSummaryDataMismatched(extData, intData)) {
				if (properties.getReconciliation().isLogErrorOnMismatchedDataEnabled()) {
					log.error("SummaryDomainTransactionType data mismatched | extData: {}, intData: {}", extData,
							intData);
				}
				if (properties.getReconciliation().isUpdateMismatchedDataEnabled()) {
					updateMismatchedSummaryData(shard, extData, intData);
				}
			}
		}
	}

	private boolean isSummaryDataMismatched(lithium.service.accounting.objects.SummaryDomain extData,
	        SummaryDomain intData) {
		return ((extData.getTranCount().compareTo(intData.getTranCount()) != 0) ||
				(extData.getDebitCents().compareTo(intData.getDebitCents()) != 0) ||
				(extData.getCreditCents().compareTo(intData.getCreditCents()) != 0) ||
				(extData.getOpeningBalanceCents().compareTo(intData.getOpeningBalanceCents()) != 0) ||
				(extData.getClosingBalanceCents().compareTo(intData.getClosingBalanceCents()) != 0));
	}

	private boolean isSummaryDataMismatched(lithium.service.accounting.objects.SummaryDomainLabelValue extData,
	        SummaryDomainLabelValue intData) {
		return ((extData.getTranCount().compareTo(intData.getTranCount()) != 0) ||
				(extData.getDebitCents().compareTo(intData.getDebitCents()) != 0) ||
				(extData.getCreditCents().compareTo(intData.getCreditCents()) != 0));
	}

	private boolean isSummaryDataMismatched(lithium.service.accounting.objects.SummaryDomainTransactionType extData,
			SummaryDomainTransactionType intData) {
		return ((extData.getTranCount().compareTo(intData.getTranCount()) != 0) ||
				(extData.getDebitCents().compareTo(intData.getDebitCents()) != 0) ||
				(extData.getCreditCents().compareTo(intData.getCreditCents()) != 0));
	}

	private void updateMismatchedSummaryData(String shard, lithium.service.accounting.objects.SummaryDomain extData,
	        SummaryDomain intData) {
		SummaryDomain update = SummaryDomain.builder()
				.shard(shard)
				.tranCount(extData.getTranCount() - intData.getTranCount())
				.debitCents(extData.getDebitCents() - intData.getDebitCents())
				.creditCents(extData.getCreditCents() - intData.getCreditCents())
				.openingBalanceCents(extData.getOpeningBalanceCents() - intData.getOpeningBalanceCents())
				.closingBalanceCents(extData.getClosingBalanceCents() - intData.getClosingBalanceCents())
				.accountCode(intData.getAccountCode())
				.currency(intData.getCurrency())
				.period(intData.getPeriod())
				.build();
		update = summaryDomainService.save(update);
		log.trace("Saved SummaryDomain update shard | {}", update);
	}

	private void updateMismatchedSummaryData(String shard,
	        lithium.service.accounting.objects.SummaryDomainLabelValue extData, SummaryDomainLabelValue intData) {
		SummaryDomainLabelValue update = SummaryDomainLabelValue.builder()
				.shard(shard)
				.tranCount(extData.getTranCount() - intData.getTranCount())
				.debitCents(extData.getDebitCents() - intData.getDebitCents())
				.creditCents(extData.getCreditCents() - intData.getCreditCents())
				.labelValue(intData.getLabelValue())
				.transactionType(intData.getTransactionType())
				.accountCode(intData.getAccountCode())
				.currency(intData.getCurrency())
				.period(intData.getPeriod())
				.build();
		update = summaryDomainLabelValueService.save(update);
		log.trace("Saved SummaryDomainLabelValue update shard | {}", update);
	}

	private void updateMismatchedSummaryData(String shard,
	        lithium.service.accounting.objects.SummaryDomainTransactionType extData,
	        SummaryDomainTransactionType intData) {
		SummaryDomainTransactionType update = SummaryDomainTransactionType.builder()
				.shard(shard)
				.tranCount(extData.getTranCount() - intData.getTranCount())
				.debitCents(extData.getDebitCents() - intData.getDebitCents())
				.creditCents(extData.getCreditCents() - intData.getCreditCents())
				.transactionType(intData.getTransactionType())
				.accountCode(intData.getAccountCode())
				.currency(intData.getCurrency())
				.period(intData.getPeriod())
				.build();
		update = summaryDomainTransactionTypeService.save(update);
		log.trace("Saved SummaryDomainTransactionType update shard | {}", update);
	}

	private SummaryContext createContext(lithium.service.accounting.objects.Domain extDomain,
			lithium.service.accounting.objects.Period period,
			lithium.service.accounting.objects.AccountCode accountCode,
			lithium.service.accounting.objects.Currency currency,
			lithium.service.accounting.objects.LabelValue labelValue,
			TransactionType transactionType) {
		Domain domain = domainService.findOrCreate(extDomain.getName());
		SummaryContext context = SummaryContext.builder()
				.domain(domain)
				.period(findOrCreatePeriod(domain, period))
				.accountCode(accountCodeService.findOrCreate(accountCode.getCode()))
				.currency(currencyService.findOrCreate(currency.getCode(), currency.getName()))
				.build();
		if (labelValue != null) {
			context.setLabelValue(labelValueService.findOrCreate(labelValue.getLabel().getName(),
					labelValue.getValue()));
		}
		if (transactionType != null) {
			context.setTransactionType(transactionTypeService.findOrCreate(transactionType.getCode()));
		}
		return context;
	}

	private Period findOrCreatePeriod(Domain domain, lithium.service.accounting.objects.Period extPeriod) {
		Period intPeriod = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain,
				extPeriod.getYear(), extPeriod.getMonth(), extPeriod.getDay(), extPeriod.getWeek(),
				extPeriod.getGranularity());
		if (intPeriod == null) {
			intPeriod = modelMapper.map(extPeriod, Period.class);
			intPeriod.setId(null);
			intPeriod.setDomain(domain);
			intPeriod = periodRepository.save(intPeriod);
		}
		return intPeriod;
	}

	private Optional<SystemSummaryReconciliationClient> getSystemSummaryReconciliationClient() {
		return getClient(SystemSummaryReconciliationClient.class, "service-accounting");
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}
}
