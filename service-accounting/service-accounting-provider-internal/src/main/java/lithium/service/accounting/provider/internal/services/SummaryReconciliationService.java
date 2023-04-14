package lithium.service.accounting.provider.internal.services;

import lithium.cashier.CashierTransactionLabels;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.objects.SummaryDomain;
import lithium.service.accounting.objects.SummaryDomainLabelValue;
import lithium.service.accounting.objects.SummaryDomainTransactionType;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationRequest;
import lithium.service.accounting.objects.reconciliation.SummaryReconciliationResponse;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryProcessingBoundary;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.repositories.PeriodRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryDomainTransactionTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.SummaryProcessingBoundaryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionRepository;
import lithium.service.client.util.LabelManager;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SummaryReconciliationService {
	@Autowired private DomainService domainService;
	@Autowired private ModelMapper modelMapper;
	@Autowired private PeriodRepository periodRepository;
	@Autowired private SummaryDomainRepository summaryDomainRepository;
	@Autowired private SummaryDomainLabelValueRepository summaryDomainLabelValueRepository;
	@Autowired private SummaryDomainTransactionTypeRepository summaryDomainTransactionTypeRepository;
	@Autowired private SummaryProcessingBoundaryRepository summaryProcessingBoundaryRepository;
	@Autowired private TransactionRepository transactionRepository;

	private static final String[] excludedLabels = { LabelManager.LOGIN_EVENT_ID,
			LabelManager.PLAYER_BONUS_HISTORY_ID, CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE };

	@TimeThisMethod
	public SummaryReconciliationResponse getSummaryDataForDate(SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(request.getDateFormat());
		LocalDate date = LocalDate.parse(request.getDate(), dateTimeFormatter);
		log.trace("getSummaryDataForDate | {}", request);

		SummaryProcessingBoundary summaryProcessingBoundary = summaryProcessingBoundaryRepository
				.findFirstBySummaryType(SummaryProcessingBoundary.DOMAIN_ALL);
		if (summaryProcessingBoundary == null) {
			String msg = "Failed to find summary processing boundary";
			log.error(msg);
			throw new Status500InternalServerErrorException(msg);
		}

		Transaction transaction = transactionRepository
				.findOne(summaryProcessingBoundary.getLastTransactionIdProcessed());
		LocalDate lastTranProcessedCreatedOn = transaction.getCreatedOn().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		if (lastTranProcessedCreatedOn.isBefore(date)) {
			log.trace("It is not safe to compile summary data for this date | date: {},"
					+ " lastTranProcessedCreatedOn.createdOn: {}", date, lastTranProcessedCreatedOn);
			return SummaryReconciliationResponse.builder()
					.safeToProcessThisDate(false)
					.build();
		}

		List<Domain> domains = domainService.findAll();

		List<SummaryDomain> summaryDomainList = new ArrayList<>();
		List<SummaryDomainLabelValue> summaryDomainLabelValueList = new ArrayList<>();
		List<SummaryDomainTransactionType> summaryDomainTransactionTypeList = new ArrayList<>();

		List<Granularity> granularities = new ArrayList<>();
		if (isFirstDayOfYear(date)) granularities.add(Granularity.GRANULARITY_YEAR); // Last year
		if (isFirstDayOfMonth(date)) granularities.add(Granularity.GRANULARITY_MONTH); // Last month
		if (isFirstDayOfWeek(date)) granularities.add(Granularity.GRANULARITY_WEEK); // Last week
		granularities.add(Granularity.GRANULARITY_DAY); // This day

		List<Period> periods = new ArrayList<>();
		for (Domain domain: domains) {
			for (Granularity granularity: granularities) {
				findAndAddPeriodToList(domain, date, granularity,
						granularity.id() != Granularity.GRANULARITY_DAY.id(), periods);
			}
		}

		SummaryReconciliationResponse response = compile(request, summaryDomainList, summaryDomainLabelValueList,
				summaryDomainTransactionTypeList, periods);
		log.trace("Compiled summary data for {} | summaryDomainList.size: {}, summaryDomainLabelValueList.size: {},"
				+ " summaryDomainTransactionTypeList.size: {}", date, summaryDomainList.size(),
				summaryDomainLabelValueList.size(), summaryDomainTransactionTypeList.size());
		return response;
	}

	@TimeThisMethod
	public SummaryReconciliationResponse getCurrentAndTotalSummaryData(SummaryReconciliationRequest request)
			throws Status500InternalServerErrorException {
		log.trace("getCurrentAndTotalSummaryData | {}", request);

		LocalDate date = LocalDate.now();

		List<Domain> domains = domainService.findAll();

		List<SummaryDomain> summaryDomainList = new ArrayList<>();
		List<SummaryDomainLabelValue> summaryDomainLabelValueList = new ArrayList<>();
		List<SummaryDomainTransactionType> summaryDomainTransactionTypeList = new ArrayList<>();

		List<Granularity> granularities = new ArrayList<>();
		granularities.add(Granularity.GRANULARITY_YEAR);
		granularities.add(Granularity.GRANULARITY_MONTH);
		granularities.add(Granularity.GRANULARITY_DAY);
		granularities.add(Granularity.GRANULARITY_WEEK);

		List<Period> periods = new ArrayList<>();
		for (Domain domain: domains) {
			for (Granularity granularity: granularities) {
				findAndAddPeriodToList(domain, date, granularity, false, periods);

				boolean addPreviousPeriod = false;

				switch (granularity) {
					case GRANULARITY_YEAR: if (isFirstDayOfYear(date)) addPreviousPeriod = true; break;
					case GRANULARITY_MONTH: if (isFirstDayOfMonth(date)) addPreviousPeriod = true; break;
					case GRANULARITY_WEEK: if (isFirstDayOfWeek(date)) addPreviousPeriod = true; break;
				}

				if (addPreviousPeriod) {
					findAndAddPeriodToList(domain, date, granularity, true, periods);
				}
			}

			findAndAddPeriodToList(domain, date, Granularity.GRANULARITY_TOTAL, false, periods);
		}

		SummaryReconciliationResponse response = compile(request, summaryDomainList, summaryDomainLabelValueList,
				summaryDomainTransactionTypeList, periods);
		log.trace("Compiled current and total summary data | summaryDomainList.size: {},"
				+ " summaryDomainLabelValueList.size: {}, summaryDomainTransactionTypeList.size: {}",
				summaryDomainList.size(), summaryDomainLabelValueList.size(), summaryDomainTransactionTypeList.size());
		return response;
	}

	private void findAndAddPeriodToList(Domain domain, LocalDate date, Granularity granularity, boolean previous,
			List<Period> periods) throws Status500InternalServerErrorException {
		Period period = findPeriod(domain, date, granularity.id(), previous);
		if (period != null) periods.add(period);
	}
	
	private SummaryReconciliationResponse compile(SummaryReconciliationRequest request,
			List<SummaryDomain> summaryDomainList,
			List<SummaryDomainLabelValue> summaryDomainLabelValueList,
			List<SummaryDomainTransactionType> summaryDomainTransactionTypeList, List<Period> periods) {
		if (!periods.isEmpty()) {
			log.trace("Compiling summaries for periods: {}", periods);
			if (request.getSummaryDomainPageNum() != -1) {
				summaryDomainList.addAll(findSummaryDomainEntriesForPeriods(request.getSummaryDomainPageNum(),
						request.getDataFetchSizePerType(), periods));
			}
			if (request.getSummaryDomainLabelValuePageNum() != -1) {
				summaryDomainLabelValueList.addAll(findSummaryDomainLabelValueEntriesForPeriods(
						request.getSummaryDomainLabelValuePageNum(), request.getDataFetchSizePerType(), periods,
						excludedLabels));
			}
			if (request.getSummaryDomainTransactionTypePageNum() != -1) {
				summaryDomainTransactionTypeList.addAll(findSummaryDomainTransactionTypeEntriesForPeriods(
						request.getSummaryDomainTransactionTypePageNum(), request.getDataFetchSizePerType(),
						periods));
			}
		}

		SummaryReconciliationResponse response = SummaryReconciliationResponse.builder()
				.safeToProcessThisDate(true)
				.summaryDomainList(summaryDomainList)
				.summaryDomainLabelValueList(summaryDomainLabelValueList)
				.summaryDomainTransactionTypeList(summaryDomainTransactionTypeList)
				.build();
		return response;
	}
	
	private List<SummaryDomain> findSummaryDomainEntriesForPeriods(int pageNum, int dataFetchSize,
			List<Period> periods) {
		List<SummaryDomain> summaryDomainList = new ArrayList<>();
		SW.start("summaryDomain.lookup");
		Pageable pageRequest = PageRequest.of(pageNum, dataFetchSize);
		List<lithium.service.accounting.provider.internal.data.entities.SummaryDomain> pageData =
				summaryDomainRepository.findByPeriodIn(periods, pageRequest);
		SW.stop();
		SW.start("summaryDomain.map");
		pageData.forEach(summaryDomain -> {
			summaryDomainList.add(modelMapper.map(summaryDomain, SummaryDomain.class));
		});
		SW.stop();
		return summaryDomainList;
	}
	
	private List<SummaryDomainLabelValue> findSummaryDomainLabelValueEntriesForPeriods(int pageNum, int dataFetchSize,
			List<Period> periods, String[] excludedLabels) {
		List<SummaryDomainLabelValue> summaryDomainLabelValueList = new ArrayList<>();
		SW.start("summaryDomainLabelValue.lookup");
		Pageable pageRequest = PageRequest.of(pageNum, dataFetchSize);
		List<lithium.service.accounting.provider.internal.data.entities.SummaryDomainLabelValue> pageData =
				summaryDomainLabelValueRepository.findByPeriodInAndLabelValueLabelNameNotIn(periods, excludedLabels,
						pageRequest);
		SW.stop();
		SW.start("summaryDomainLabelValue.map");
		pageData.forEach(summaryDomainLabelValue -> {
			summaryDomainLabelValueList.add(modelMapper.map(summaryDomainLabelValue, SummaryDomainLabelValue.class));
		});
		SW.stop();
		return summaryDomainLabelValueList;
	}
	
	private List<SummaryDomainTransactionType> findSummaryDomainTransactionTypeEntriesForPeriods(int pageNum,
			int dataFetchSize, List<Period> periods) {
		List<SummaryDomainTransactionType> summaryDomainTransactionTypeList = new ArrayList<>();
		SW.start("summaryDomainTransactionType.lookup");
		Pageable pageRequest = PageRequest.of(pageNum, dataFetchSize);
		List<lithium.service.accounting.provider.internal.data.entities.SummaryDomainTransactionType> pageData =
				summaryDomainTransactionTypeRepository.findByPeriodIn(periods, pageRequest);
		SW.stop();
		SW.start("summaryDomainTransactionType.map");
		pageData.forEach(summaryDomainTransactionType -> {
			summaryDomainTransactionTypeList.add(modelMapper.map(summaryDomainTransactionType,
					SummaryDomainTransactionType.class));
		});
		SW.stop();
		return summaryDomainTransactionTypeList;
	}

	private boolean isFirstDayOfYear(LocalDate date) {
		LocalDate firstDayOfYear = date.with(TemporalAdjusters.firstDayOfYear());
		boolean result = date.isEqual(firstDayOfYear);
		log.trace("isFirstDayOfYear | date: {}, firstDayOfYear: {}, result: {}", date, firstDayOfYear, result);
		return result;
	}

	private boolean isFirstDayOfMonth(LocalDate date) {
		LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
		boolean result = date.isEqual(firstDayOfMonth);
		log.trace("isFirstDayOfMonth | date: {}, firstDayOfMonth: {}, result: {}", date, firstDayOfMonth, result);
		return result;
	}

	private boolean isFirstDayOfWeek(LocalDate date) {
		WeekFields weekFields = WeekFields.ISO;
		DayOfWeek firstDayOfWeek = weekFields.getFirstDayOfWeek();
		boolean result = date.getDayOfWeek().compareTo(firstDayOfWeek) == 0;
		log.trace("isFirstDayOfWeek | date: {}, firstDayOfWeek: {}, result: {}", date, firstDayOfWeek, result);
		return result;
	}

	private Period findPeriod(Domain domain, LocalDate date, int granularity, boolean previous)
			throws Status500InternalServerErrorException {
		int year = -1;
		int month = -1;
		int day = -1;
		int week = -1;

		Granularity granularityEnum = Granularity.fromId(granularity);

		switch (granularityEnum) {
			case GRANULARITY_YEAR: {
				if (previous) {
					LocalDate lastYear = date.minusYears(1);
					year = lastYear.getYear();
				} else {
					year = date.getYear();
				}
				break;
			}
			case GRANULARITY_MONTH: {
				if (previous) {
					LocalDate lastMonth = date.minusMonths(1);
					year = lastMonth.getYear();
					month = lastMonth.getMonthValue();
				} else {
					year = date.getYear();
					month = date.getMonthValue();
				}
				break;
			}
			case GRANULARITY_DAY: {
				if (previous) {
					LocalDate yesterday = date.minusDays(1);
					year = yesterday.getYear();
					month = yesterday.getMonthValue();
					day = yesterday.getDayOfMonth();
				} else {
					year = date.getYear();
					month = date.getMonthValue();
					day = date.getDayOfMonth();
				}
				break;
			}
			case GRANULARITY_WEEK: {
				WeekFields weekFields = WeekFields.ISO;
				if (previous) {
					LocalDate lastWeek = date.minusWeeks(1);
					year = lastWeek.get(weekFields.weekBasedYear());
					week = lastWeek.get(weekFields.weekOfWeekBasedYear());
				} else {
					year = date.get(weekFields.weekBasedYear());
					week = date.get(weekFields.weekOfWeekBasedYear());
				}
				break;
			}
			case GRANULARITY_TOTAL: {
				break;
			}
			default: throw new Status500InternalServerErrorException("Granularity " + granularity + " is not handled");
		}

		Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain,
				year, month, day, week, granularity);
		log.trace("Period lookup | domain: {}, year: {}, month: {}, day: {}, week: {}, granularity: {} | {}", domain,
				year, month, day, week, granularity, period);
		return period;
	}
}
