package lithium.service.cashier.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.cashier.CashierTransactionLabels;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorProperty;
import lithium.service.cashier.data.repositories.DomainMethodProcessorPropertyRepository;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class DomainMethodProcessorService {
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private LimitsService limitsService;
	@Autowired
	private FeesService feesService;
	@Autowired
	private ProcessorService processorService;
	@Autowired
	private InternalMethodsService internalMethodsService;
	@Autowired
	private DomainMethodProcessorRepository domainMethodProcessorRepository;
	@Autowired
	private DomainMethodProcessorPropertyRepository domainMethodProcessorPropertyRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private LithiumServiceClientFactory services;
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return ofNullable(clientInstance);
		
	}
	private Optional<AccountingSummaryDomainLabelValueClient> getAccountingSummaryDomainLabelValueClient() {
		return getClient(AccountingSummaryDomainLabelValueClient.class, "service-accounting-provider-internal");
	}
	private Optional<AccountingSummaryAccountLabelValueClient> getAccountingSummaryAccountLabelValueClient() {
		return getClient(AccountingSummaryAccountLabelValueClient.class, "service-accounting-provider-internal");
	}
	
	public void copy(DomainMethodProcessor from, DomainMethodProcessor to) {
		to.setId(from.getId());
		to.setDescription(from.getDescription());
		to.setProcessor(from.getProcessor());
		to.setDomainMethod(from.getDomainMethod());
		to.setWeight(from.getWeight());
		to.setEnabled(from.getEnabled());
		to.setDeleted(from.getDeleted());
		to.setFees(from.getFees());
		to.setLimits(from.getLimits());
		to.setDomainMethodProcessorProfiles(from.getDomainMethodProcessorProfiles());
		to.setDomainMethodProcessorUsers(from.getDomainMethodProcessorUsers());
		to.setAccessRule(from.getAccessRule());
		to.setAccessRuleOnTranInit(from.getAccessRuleOnTranInit());
	}

	public DomainMethodProcessor create(DomainMethod domainMethod, Processor processor, String description, boolean enabled, double weight, String authorGuid, String userLegalName) throws Exception {
		return create(domainMethod, processor, description, enabled, weight, null, authorGuid, userLegalName);
	}
	
	public DomainMethodProcessor create(DomainMethod domainMethod, Processor processor, String description, boolean enabled, double weight, Boolean reserveFundsOnWithdrawal, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor dmp = DomainMethodProcessor.builder()
		.enabled(enabled)
		.deleted(false)
		.weight(weight)
		.description(description)
		.domainMethod(domainMethod)
		.processor(processor)
		.reserveFundsOnWithdrawal(reserveFundsOnWithdrawal)
		.build();
		List<ChangeLogFieldChange> clfc = changeLogService.copy(dmp, new DomainMethodProcessor(), new String[] {
			"description", "weight", "enabled", "deleted", "fees", "limits", "accessRule", "accessRuleOnTranInit", "reserveFundsOnWithdrawal"
		});
		dmp = domainMethodProcessorRepository.save(dmp);
		changeLogService.registerChangesWithDomainAndFullName("dmp", "create", dmp.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return dmp;
	}
	
	public DomainMethodProcessor create(DomainMethodProcessor domainMethodProcessor, String authorGuid, String userLegalName) throws Exception {
		Fees fees = null;
		if (domainMethodProcessor.getFees() != null) {
			fees = feesService.create(
				domainMethodProcessor.getFees().getFlat(),
				domainMethodProcessor.getFees().getPercentage(),
				domainMethodProcessor.getFees().getMinimum(),
				domainMethodProcessor.getFees().getStrategy()
			);
		}
		domainMethodProcessor.setFees(fees);
		
		Limits limits = null;
		if (domainMethodProcessor.getLimits() != null) {
			limits = limitsService.create(
				domainMethodProcessor.getLimits().getMinAmount(),
				domainMethodProcessor.getLimits().getMaxAmount(),
				domainMethodProcessor.getLimits().getMinFirstTransactionAmount(),
				domainMethodProcessor.getLimits().getMaxFirstTransactionAmount(),
				domainMethodProcessor.getLimits().getMaxAmountDay(),
				domainMethodProcessor.getLimits().getMaxAmountWeek(),
				domainMethodProcessor.getLimits().getMaxAmountMonth(),
				domainMethodProcessor.getLimits().getMaxTransactionsDay(),
				domainMethodProcessor.getLimits().getMaxTransactionsWeek(),
				domainMethodProcessor.getLimits().getMaxTransactionsMonth()
			);
		}
		domainMethodProcessor.setLimits(limits);
		
		Limits dl = null;
		if (domainMethodProcessor.getDomainLimits() != null) {
			dl = limitsService.create(
				0L, 0L, 0L, 0L,
				domainMethodProcessor.getDomainLimits().getMaxAmountDay(),
				domainMethodProcessor.getDomainLimits().getMaxAmountWeek(),
				domainMethodProcessor.getDomainLimits().getMaxAmountMonth(),
				domainMethodProcessor.getDomainLimits().getMaxTransactionsDay(),
				domainMethodProcessor.getDomainLimits().getMaxTransactionsWeek(),
				domainMethodProcessor.getDomainLimits().getMaxTransactionsMonth()
			);
		}
		domainMethodProcessor.setDomainLimits(dl);
		
		domainMethodProcessor.setDeleted(false);
		
		domainMethodProcessor.setProcessor(processorService.find(domainMethodProcessor.getProcessor().getId()));
		
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, new DomainMethodProcessor(), new String[] {
			"description", "weight", "enabled", "deleted", "fees", "limits", "accessRule"
		});
		changeLogService.registerChangesWithDomainAndFullName("dmp", "create", domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		
		return domainMethodProcessor;
	}
	
	public DomainMethodProcessor save(DomainMethodProcessor domainMethodProcessor, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor dmp = domainMethodProcessorRepository.findOne(domainMethodProcessor.getId());
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		copy(dmp, oldDmp);
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] {
			"description", "weight", "enabled", "deleted", "fees", "limits", "accessRule"
		});
		changeLogService.registerChangesWithDomainAndFullName("dmp", "edit", domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	
	public DomainMethodProcessor saveFees(DomainMethodProcessor domainMethodProcessor, Fees fees, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		String type = "";
		if (domainMethodProcessor.getFees() == null) {
			type = "create";
		} else {
			type = "edit";
			Fees oldFees = new Fees();
			feesService.copy(domainMethodProcessor.getFees(), oldFees);
			oldDmp.setFees(oldFees);
		}
		fees = feesService.create(fees);
		domainMethodProcessor.setFees(fees);
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] { "fees" });
		changeLogService.registerChangesWithDomainAndFullName("dmp.fees", type, domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	public DomainMethodProcessor saveLimits(DomainMethodProcessor domainMethodProcessor, Limits limits, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		String type = "";
		if (domainMethodProcessor.getLimits() == null) {
			type = "create";
		} else {
			type = "edit";
			Limits oldLimits = new Limits();
			limitsService.copy(domainMethodProcessor.getLimits(), oldLimits);
			oldDmp.setLimits(oldLimits);
		}
		limits = limitsService.create(limits);
		domainMethodProcessor.setLimits(limits);
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] { "limits" });
		changeLogService.registerChangesWithDomainAndFullName("dmp.limits", type, domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	public DomainMethodProcessor saveDomainLimits(DomainMethodProcessor domainMethodProcessor, Limits limits, String authorGuid, String userLegalName) throws Exception {
		limits = limitsService.create(limits);
		domainMethodProcessor.setDomainLimits(limits);
		domainMethodProcessor = save(domainMethodProcessor, authorGuid, userLegalName);
		return domainMethodProcessor;
	}
	
	public DomainMethodProcessor delete(DomainMethodProcessor domainMethodProcessor, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		copy(domainMethodProcessor, oldDmp);
		domainMethodProcessor.setDeleted(true);
		domainMethodProcessor.setEnabled(false);
		domainMethodProcessor.setDescription(domainMethodProcessor.getDescription()+"_"+new Date().getTime());
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] { "deleted", "enabled", "description" });
		changeLogService.registerChangesWithDomainAndFullName("dmp", "delete", domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	public DomainMethodProcessor deleteFees(DomainMethodProcessor domainMethodProcessor, String authorGuid, String userLegalName) throws Exception {
		if (domainMethodProcessor.getFees() == null) return domainMethodProcessor;
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		Fees oldFees = new Fees();
		feesService.copy(domainMethodProcessor.getFees(), oldFees);
		oldDmp.setFees(oldFees);
		feesService.delete(domainMethodProcessor.getFees());
		domainMethodProcessor.setFees(null);
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] { "fees" });
		changeLogService.registerChangesWithDomainAndFullName("dmp.fees", "delete", domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	public DomainMethodProcessor deleteLimits(DomainMethodProcessor domainMethodProcessor, String authorGuid, String userLegalName) throws Exception {
		if (domainMethodProcessor.getLimits() == null) return domainMethodProcessor;
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		Limits oldLimits = new Limits();
		limitsService.copy(domainMethodProcessor.getLimits(), oldLimits);
		oldDmp.setLimits(oldLimits);
		limitsService.delete(domainMethodProcessor.getLimits());
		limitsService.delete(domainMethodProcessor.getDomainLimits());
		domainMethodProcessor.setLimits(null);
		domainMethodProcessor.setDomainLimits(null);
		domainMethodProcessor = domainMethodProcessorRepository.save(domainMethodProcessor);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodProcessor, oldDmp, new String[] { "limits" });
		changeLogService.registerChangesWithDomainAndFullName("dmp.limits", "delete", domainMethodProcessor.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return domainMethodProcessor;
	}
	
	public DomainMethodProcessor update(Long id, boolean enabled, double weight, String description, Boolean reserveFundsOnWithdrawal, String authorGuid, String userLegalName) throws Exception {
		DomainMethodProcessor dmp = domainMethodProcessorRepository.findOne(id);
		if (dmp == null) throw new Exception("DomainMethodProcessor not found.");
		DomainMethodProcessor oldDmp = new DomainMethodProcessor();
		copy(dmp, oldDmp);
		dmp.setEnabled(enabled);
		dmp.setWeight(weight);
		dmp.setDescription(description);
		if (reserveFundsOnWithdrawal != null) dmp.setReserveFundsOnWithdrawal(reserveFundsOnWithdrawal);
		dmp = domainMethodProcessorRepository.save(dmp);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(dmp, oldDmp, new String[] { "enabled", "weight", "description", "reserveFundsOnReversal" });
		changeLogService.registerChangesWithDomainAndFullName("dmp", "edit", dmp.getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, authorGuid.substring(0, authorGuid.indexOf('/')), userLegalName);
		return dmp;
	}
	
	public DomainMethodProcessor find(Long domainMethodProcessorId) {
		return domainMethodProcessorRepository.findOne(domainMethodProcessorId);
	}
	
	public Map<String, SummaryLabelValue> accountingTotals(DomainMethodProcessor dmp) throws Exception {
		Map<String, SummaryLabelValue> summaryLabelValues = new HashMap<>();
		String currency = userService.retrieveDomainFromDomainService(dmp.getDomainMethod().getDomain().getName()).getCurrency();
		String accountCode = (dmp.getDomainMethod().getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		String transactionType = (dmp.getDomainMethod().getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		
		DateTimeZone timeZone = DateTimeZone.getDefault();
		DateTime now = DateTime.now(timeZone);
		//per day
		DateTime dayStart = now.withTimeAtStartOfDay();
		DateTime dayEnd = now.plusDays(1).withTimeAtStartOfDay();
		//per week
		DateTime weekStart = dayStart.dayOfWeek().withMinimumValue();
		DateTime weekEnd = dayStart.dayOfWeek().withMaximumValue().plusDays(1);
		//per month
		DateTime monthStart = dayStart.dayOfMonth().withMinimumValue();
		DateTime monthEnd = dayStart.dayOfMonth().withMaximumValue().plusDays(1);
		//last month
		DateTime lastMonthStart = monthStart.minusMonths(1);
		DateTime lastMonthEnd = monthEnd.minusMonths(1);
		
		Response<List<SummaryLabelValue>> domainLastMonth = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			lastMonthStart.toString(),
			lastMonthEnd.toString()
		);
		if (domainLastMonth.isSuccessful() && domainLastMonth.getData().size() > 0) {
			summaryLabelValues.put("lastmonth", domainLastMonth.getData().get(0));
		}
		
		Response<List<SummaryLabelValue>> domainMonth = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			monthStart.toString(),
			monthEnd.toString()
		);
		if (domainMonth.isSuccessful() && domainMonth.getData().size() > 0) {
			summaryLabelValues.put("month", domainMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainWeek = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_WEEK,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			weekStart.toString(),
			weekEnd.toString()
		);
		if (domainWeek.isSuccessful() && domainWeek.getData().size() > 0) {
			summaryLabelValues.put("week", domainWeek.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainDay = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_DAY,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			dayStart.toString(),
			dayEnd.toString()
		);
		if (domainDay.isSuccessful() && domainDay.getData().size() > 0) {
			summaryLabelValues.put("day", domainDay.getData().get(0));
		}
		return summaryLabelValues;
	}
	public Map<String, SummaryLabelValue> accountingTotals(DomainMethodProcessor dmp, String username) throws Exception {
		Map<String, SummaryLabelValue> summaryLabelValues = new HashMap<>();
		String currency = userService.retrieveDomainFromDomainService(dmp.getDomainMethod().getDomain().getName()).getCurrency();
		String accountCode = (dmp.getDomainMethod().getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		String transactionType = (dmp.getDomainMethod().getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		
		DateTimeZone timeZone = DateTimeZone.getDefault();
		DateTime now = DateTime.now(timeZone);
		//per day
		DateTime dayStart = now.withTimeAtStartOfDay();
		DateTime dayEnd = now.plusDays(1).withTimeAtStartOfDay();
		//per week
		DateTime weekStart = dayStart.dayOfWeek().withMinimumValue();
		DateTime weekEnd = dayStart.dayOfWeek().withMaximumValue().plusDays(1);
		//per month
		DateTime monthStart = dayStart.dayOfMonth().withMinimumValue();
		DateTime monthEnd = dayStart.dayOfMonth().withMaximumValue().plusDays(1);
		//last month
		DateTime lastMonthStart = monthStart.minusMonths(1);
		DateTime lastMonthEnd = monthEnd.minusMonths(1);
		
		Response<List<SummaryLabelValue>> domainLastMonth = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			lastMonthStart.toString(),
			lastMonthEnd.toString(),
			dmp.getDomainMethod().getDomain().getName()+"/"+username
		);
		if (domainLastMonth.isSuccessful() && domainLastMonth.getData().size() > 0) {
			summaryLabelValues.put("lastmonth", domainLastMonth.getData().get(0));
		}
		
		Response<List<SummaryLabelValue>> domainMonth = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			monthStart.toString(),
			monthEnd.toString(),
			dmp.getDomainMethod().getDomain().getName()+"/"+username
		);
		if (domainMonth.isSuccessful() && domainMonth.getData().size() > 0) {
			summaryLabelValues.put("month", domainMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainWeek = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_WEEK,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			weekStart.toString(),
			weekEnd.toString(),
			dmp.getDomainMethod().getDomain().getName()+"/"+username
		);
		if (domainWeek.isSuccessful() && domainWeek.getData().size() > 0) {
			summaryLabelValues.put("week", domainWeek.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainDay = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dmp.getDomainMethod().getDomain().getName(),
			Period.GRANULARITY_DAY,
			accountCode,
			transactionType,
			CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
			dmp.getId()+"",
			currency,
			dayStart.toString(),
			dayEnd.toString(),
			dmp.getDomainMethod().getDomain().getName()+"/"+username
		);
		if (domainDay.isSuccessful() && domainDay.getData().size() > 0) {
			summaryLabelValues.put("day", domainDay.getData().get(0));
		}
		return summaryLabelValues;
	}
	
	public Fees fees(DomainMethodProcessor domainMethodProcessor) {
		return feesService.find(domainMethodProcessor.getFees().getId());
	}
	public Limits limits(DomainMethodProcessor domainMethodProcessor) {
		return limitsService.find(domainMethodProcessor.getLimits().getId());
	}
	
	public List<DomainMethodProcessor> list(Long domainMethodId) {
		return domainMethodProcessorRepository.findByDomainMethodIdAndDeletedFalseOrderByWeightDesc(domainMethodId);
	}
	
	public List<DomainMethodProcessorProperty> properties(Long domainMethodProcessorId) {
		return domainMethodProcessorPropertyRepository.findByDomainMethodProcessorIdOrderByProcessorPropertyName(domainMethodProcessorId);
	}
	public List<DomainMethodProcessorProperty> propertiesByType(Long domainMethodProcessorId, String processorPropertyType) {
		return domainMethodProcessorPropertyRepository.findByDomainMethodProcessorIdAndProcessorPropertyTypeOrderByProcessorPropertyName(domainMethodProcessorId, processorPropertyType);
	}
	
	public List<DomainMethodProcessorProperty> propertiesWithDefaults(Long domainMethodProcessorId) {
		return propertiesProcessed(domainMethodProcessorId, true);
	}
	public List<DomainMethodProcessorProperty> propertiesNoDefaults(Long domainMethodProcessorId) {
		return propertiesProcessed(domainMethodProcessorId, false);
	}
	public List<DomainMethodProcessorProperty> propertiesForFrontend(Long domainMethodProcessorId) {
		List<DomainMethodProcessorProperty> domainMethodProcessorProperties = propertiesByType(domainMethodProcessorId, "frontend");
		DomainMethodProcessor domainMethodProcessor = find(domainMethodProcessorId);
		Processor processor = domainMethodProcessor.getProcessor();
		List<ProcessorProperty> processorProperties = processor.getProperties();
		int processorPropertiesSize = processorProperties.size();
		
		if (domainMethodProcessorProperties == null) domainMethodProcessorProperties = new ArrayList<>();
		
		if (domainMethodProcessorProperties.size() == processorPropertiesSize) {
			return domainMethodProcessorProperties;
		} else {
			List<DomainMethodProcessorProperty> domainMethodProcessorPropertiesDefault = new ArrayList<>();
			for (ProcessorProperty processorProperty:processorProperties) {
				if (processorProperty.getType().equalsIgnoreCase("frontend")) {
					DomainMethodProcessorProperty domainMethodProcessorProperty = DomainMethodProcessorProperty.builder()
						.domainMethodProcessor(domainMethodProcessor)
						.processorProperty(processorProperty)
						.value(processorProperty.getDefaultValue())
						.build();
					domainMethodProcessorPropertiesDefault.add(domainMethodProcessorProperty);
				}
			}
			Comparator<DomainMethodProcessorProperty> c = (dmpp1, dmpp2) -> {
				ProcessorProperty pp1 = dmpp1.getProcessorProperty();
				ProcessorProperty pp2 = dmpp2.getProcessorProperty();
				if ((pp1!=null) && (pp2!=null)) {
					return pp1.getName().compareTo(pp2.getName());
				} else {
					return -1;
				}
			};
			
			if ((domainMethodProcessorProperties.isEmpty()) && (domainMethodProcessorPropertiesDefault.isEmpty())) {
				return Collections.emptyList();
			} else if ((domainMethodProcessorProperties.isEmpty()) && (!domainMethodProcessorPropertiesDefault.isEmpty())) {
				return domainMethodProcessorPropertiesDefault;
			} else if ((!domainMethodProcessorProperties.isEmpty()) && (domainMethodProcessorPropertiesDefault.isEmpty())) {
				return domainMethodProcessorProperties;
			}
			
			List<DomainMethodProcessorProperty> finalList = Stream.concat(
				domainMethodProcessorProperties.stream(),
				domainMethodProcessorPropertiesDefault.stream()
			).filter(new TreeSet<>(c)::add)
			.collect(Collectors.toList());
			return finalList;
		}
	}
	
	private List<DomainMethodProcessorProperty> propertiesProcessed(Long domainMethodProcessorId, boolean defaultValue) {
		List<DomainMethodProcessorProperty> domainMethodProcessorProperties = properties(domainMethodProcessorId);
		DomainMethodProcessor domainMethodProcessor = find(domainMethodProcessorId);
		Processor processor = domainMethodProcessor.getProcessor();
		List<ProcessorProperty> processorProperties = processor.getProperties();
		int processorPropertiesSize = processorProperties.size();
		
		if (domainMethodProcessorProperties == null) domainMethodProcessorProperties = new ArrayList<>();
		
		if (domainMethodProcessorProperties.size() == processorPropertiesSize) {
			return domainMethodProcessorProperties;
		} else {
			List<DomainMethodProcessorProperty> domainMethodProcessorPropertiesDefault = new ArrayList<>();
			for (ProcessorProperty processorProperty:processorProperties) {
				DomainMethodProcessorProperty domainMethodProcessorProperty = DomainMethodProcessorProperty.builder()
					.domainMethodProcessor(domainMethodProcessor)
					.processorProperty(processorProperty)
					.value((defaultValue)?processorProperty.getDefaultValue():null)
					.build();
				domainMethodProcessorPropertiesDefault.add(domainMethodProcessorProperty);
			}
			Comparator<DomainMethodProcessorProperty> c = (dmpp1, dmpp2) -> dmpp1.getProcessorProperty().getName().compareTo(dmpp2.getProcessorProperty().getName());
			
			if ((domainMethodProcessorProperties.isEmpty()) && (domainMethodProcessorPropertiesDefault.isEmpty())) {
				return Collections.emptyList();
			} else if ((domainMethodProcessorProperties.isEmpty()) && (!domainMethodProcessorPropertiesDefault.isEmpty())) {
				return domainMethodProcessorPropertiesDefault;
			} else if ((!domainMethodProcessorProperties.isEmpty()) && (domainMethodProcessorPropertiesDefault.isEmpty())) {
				return domainMethodProcessorProperties;
			}
			
			List<DomainMethodProcessorProperty> finalList = Stream.concat(
				domainMethodProcessorProperties.stream(),
				domainMethodProcessorPropertiesDefault.stream()
			).filter(new TreeSet<>(c)::add)
			.collect(Collectors.toList());
			return finalList;
		}
	}
	
	public DomainMethodProcessorProperty findProperty(Long id) {
		return domainMethodProcessorPropertyRepository.findOne(id);
	}
	
	public List<DomainMethodProcessorProperty> saveProperties(DomainMethodProcessor domainMethodProcessor, List<DomainMethodProcessorProperty> domainMethodProcessorProperties) throws Exception {
		List<DomainMethodProcessorProperty> savedDomainMethodProcessorProperties = new ArrayList<>();
		
		for (DomainMethodProcessorProperty dmpp:domainMethodProcessorProperties) {
			log.trace("dmpp : "+dmpp);
			if (dmpp.isOverride()) {
				dmpp.setDomainMethodProcessor(domainMethodProcessor);
				dmpp = domainMethodProcessorPropertyRepository.save(dmpp);
			} else {
				dmpp.setDomainMethodProcessor(domainMethodProcessor);
				dmpp = removeProperty(dmpp);
			}
			savedDomainMethodProcessorProperties.add(dmpp);
		}
		return savedDomainMethodProcessorProperties;
	}
	
	public DomainMethodProcessorProperty removeProperty(DomainMethodProcessorProperty dmpp) {
		log.debug("Delete : "+dmpp);
		domainMethodProcessorPropertyRepository.delete(dmpp);
		dmpp.setId(null);
		dmpp.setValue(null);
		return dmpp;
	}
	
	public DomainMethodProcessorProperty setProperty(DomainMethodProcessor dmp, String name, String value) throws Exception {
		log.debug("Save : "+dmp+" : "+name+" : "+value);
		ProcessorProperty processorProperty = processorService.findPropertyByProcessorIdAndName(dmp.getProcessor(), name);
		if (processorProperty == null) throw new Exception("Could not find ProcessorProperty with name : " + name);
		
		DomainMethodProcessorProperty domainMethodProcessorProperty = domainMethodProcessorPropertyRepository.findByDomainMethodProcessorIdAndProcessorPropertyId(dmp.getId(), processorProperty.getId());
		if (domainMethodProcessorProperty == null) {
			return domainMethodProcessorPropertyRepository.save(
				DomainMethodProcessorProperty.builder()
				.processorProperty(processorProperty)
				.domainMethodProcessor(dmp)
				.value(value)
				.build()
			);
		} else {
			domainMethodProcessorProperty.setValue(value);
			return domainMethodProcessorPropertyRepository.save(domainMethodProcessorProperty);
		}
	}

	public List<DomainMethodProcessor> getDomainMethodProcessorsByDomainMethodIdAndEnabledTrueAndDeletedFalse(long domainMethodId) {
		return domainMethodProcessorRepository.findByDomainMethodIdAndEnabledTrueAndDeletedFalse(domainMethodId);
	}

	public List<lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor> getProcessorsByDomainNameAndDeposit(String domainName, boolean deposit) {
		List<DomainMethodProcessor> dmpEntities =
				domainMethodProcessorRepository
						.findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodDeletedFalseAndDomainMethodEnabledTrueAndDeletedFalseAndEnabledTrue(
								domainName, deposit
						);
		List<lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor> dmps = dmpEntities.stream()
				.map(dmpEntity -> {
					lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor dmp = new lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor();
					dmp.setAccessRule(dmpEntity.getAccessRule());
					dmp.setDeleted(dmpEntity.getDeleted());
					dmp.setDomainMethod(mapper.convertValue(dmpEntity.getDomainMethod(), lithium.service.cashier.client.objects.transaction.dto.DomainMethod.class));
					dmp.setEnabled(dmpEntity.getEnabled());
					dmp.setId(dmpEntity.getId());
					dmp.setProcessor(mapper.convertValue(dmpEntity.getProcessor(), lithium.service.cashier.client.objects.transaction.dto.Processor.class));
					dmp.setProperties(new HashMap<>());
					dmp.setWeight(dmpEntity.getWeight());
					List<ProcessedProcessorProperty> properties = internalMethodsService.findProcessedProcessorProperties(dmpEntity);
					if (properties != null) {
						for (ProcessedProcessorProperty property : properties) {
							dmp.getProperties().put(property.getName(), property.getValue());
						}
					}
					return dmp;
				}).collect(Collectors.toList());
		return dmps;
	}

	public String getPropertyValue(DomainMethodProcessor domainMethodProcessor, String propertyName) {
		Optional<ProcessorProperty> property = ofNullable(domainMethodProcessor)
			.map(DomainMethodProcessor::getProcessor)
			.map(Processor::getProperties)
			.orElse(Collections.emptyList())
			.stream()
			.filter(prop -> prop.getName().equalsIgnoreCase(propertyName))
			.findFirst();

		if (!property.isPresent()) {
			log.error("Failed to get processor property value for domain processor method: " + domainMethodProcessor + "property name: " + propertyName);
			return null;
		}

		Optional<DomainMethodProcessorProperty> storedProperty = property.
			map(prop -> domainMethodProcessorPropertyRepository.findByDomainMethodProcessorIdAndProcessorPropertyId(domainMethodProcessor.getId(), prop.getId()));

		return storedProperty.isPresent() ? storedProperty.get().getValue() : property.get().getDefaultValue();
	}
}
