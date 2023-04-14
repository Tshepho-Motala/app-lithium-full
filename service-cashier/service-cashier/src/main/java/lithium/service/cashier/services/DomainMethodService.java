package lithium.service.cashier.services;

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
import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.ProcessorProperty;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.objects.DomainMethodOrder;
import lithium.service.cashier.data.repositories.DomainMethodRepository;
import lithium.service.cashier.data.specifications.DomainMethodSpecification;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class DomainMethodService {
	@Autowired
	private MethodService methodService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private DomainService domainService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private UserService userService;
	@Autowired
	private DomainMethodRepository domainMethodRepository;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private CashierFrontendService cashierFrontendService;
    @Autowired
    private ChangeLogService changeLogService;

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);

	}
	private Optional<AccountingSummaryDomainLabelValueClient> getAccountingSummaryDomainLabelValueClient() {
		return getClient(AccountingSummaryDomainLabelValueClient.class, "service-accounting-provider-internal");
	}
	private Optional<AccountingSummaryAccountLabelValueClient> getAccountingSummaryAccountLabelValueClient() {
		return getClient(AccountingSummaryAccountLabelValueClient.class, "service-accounting-provider-internal");
	}

    public DomainMethod create(String name, byte[] imageData, String imageName, String filetype, boolean enabled, int priority, String methodCode, String methodUrl, String domainName, String authorGuid, String userLegalName) throws Exception {
		Method method = methodService.findByCode(methodCode);
		if (method==null) throw new Exception("Method not found!");

		return create(DomainMethod.builder()
		.name(name)
		.image((imageData == null)? null: Image.builder().filename(imageName).filetype(filetype).base64(imageData).build())
		.method(method)
		.domain(domainService.findOrCreateDomain(domainName))
		.enabled(enabled)
		.deleted(false)
		.priority(priority)
         .build(), authorGuid, userLegalName);
	}

	public DomainMethod findOneEnabledByCode(String domainName, String methodCode, boolean deposit)
			throws NoMethodWithCodeException, MoreThanOneMethodWithCodeException {
		List<DomainMethod> dms = domainMethodRepository.findByDomainNameAndMethodCodeAndDepositAndEnabledTrueAndDeletedFalse(
				domainName, methodCode, deposit
		);
		if (dms.size() == 0) throw new NoMethodWithCodeException();
		if (dms.size() > 1) throw new MoreThanOneMethodWithCodeException();
		return dms.get(0);
	}

	public List<DomainMethod> findAllEnabledByCode(String domainName, String methodCode, boolean deposit) {
		return domainMethodRepository.findByDomainNameAndMethodCodeAndDepositAndEnabledTrueAndDeletedFalse(
				domainName, methodCode, deposit);
	}

    public DomainMethod findOrCreate(String name, byte[] imageData, String imageName, String filetype, boolean enabled, boolean deposit, int priority, Long methodId, String domainName, String authorGuid, String userLegalName) throws Exception {
		DomainMethod dm = domainMethodRepository.findByNameAndDomainNameAndMethodIdAndDeposit(name, domainName, methodId, deposit);
		if (dm != null) return dm;
        return create(name, imageData, imageName, filetype, enabled, deposit, priority, methodId, domainName, authorGuid, userLegalName);
	}

    public DomainMethod create(String name, byte[] imageData, String imageName, String filetype, boolean enabled, boolean deposit, int priority, Long methodId, String domainName, String authorGuid, String userLegalName) throws Exception {
		Method method = methodService.findOne(methodId);
		if (method==null) throw new Exception("Method not found!");

		return create(DomainMethod.builder()
		.name(name)
		.image((imageData == null)? null: Image.builder().filename(imageName).filetype(filetype).base64(imageData).build())
		.method(method)
		.domain(domainService.findOrCreateDomain(domainName))
		.enabled(enabled)
		.deposit(deposit)
		.deleted(false)
		.priority(priority)
                .build(),authorGuid, userLegalName);
	}

    public DomainMethod create(DomainMethod domainMethod, String authorGuid, String userLegalName) {
		log.info("Creating DomainMethod : "+domainMethod);
		DomainMethod dm = domainMethodRepository.findByDomainAndMethodAndDeletedFalseAndNameAndDeposit(
			domainMethod.getDomain(),
			domainMethod.getMethod(),
			domainMethod.getName(),
			domainMethod.getDeposit()
		);
		if (dm == null) {
			if (domainMethod.getImage() != null) {
				Image image = imageService.create(domainMethod.getImage().getFilename(), domainMethod.getImage().getFiletype(), domainMethod.getImage().getBase64());
				domainMethod.setImage(image);
			} else {
				domainMethod.setImage(null);
			}
			if (domainMethod.getPriority() == 999) {
				if (domainMethod.getDeposit()) {
					domainMethod.setPriority(domainMethodRepository.findByDomainAndDepositTrueAndDeletedFalseOrderByPriority(domainMethod.getDomain()).size());
				} else {
					domainMethod.setPriority(domainMethodRepository.findByDomainAndDepositFalseAndDeletedFalseOrderByPriority(domainMethod.getDomain()).size());
				}
			}
            return saveDomainMethod(domainMethod, authorGuid, userLegalName);
		} else {
			Image image = dm.getImage();

			if ((image == null) && domainMethod.getImage() != null) {
				image = imageService.create(domainMethod.getImage().getFilename(), domainMethod.getImage().getFiletype(), domainMethod.getImage().getBase64());
			}

			if ((image != null) && (domainMethod.getImage() != null)) {
				image.setFilename(domainMethod.getImage().getFilename());
				image.setFiletype(domainMethod.getImage().getFiletype());
				image.setBase64(domainMethod.getImage().getBase64());
				imageService.update(image);
			}

			dm.setImage(image);
			dm.setEnabled(domainMethod.getEnabled());
			dm.setDeleted(domainMethod.getDeleted());

//			dm.setPriority(domainMethod.getPriority());
			dm.setName(domainMethod.getName());
            return saveDomainMethod(dm, authorGuid, userLegalName);
        }
    }

    public DomainMethod saveDomainMethod(DomainMethod domainMethod, String authorGuid, String userLegalName) {
        return saveDomainMethod(domainMethod, authorGuid, false, userLegalName);
    }

    public DomainMethod saveDomainMethod(DomainMethod domainMethod, String authorGuid, Boolean multipleUpdate, String userLegalName) {
	  DomainMethodOrder domainMethodsOrder = null;
	  if (!multipleUpdate) domainMethodsOrder = getDomainMethodsOrder(domainMethod);
	  domainMethod = domainMethodRepository.save(domainMethod);
	  registerChangeLogForDomainMethodOrder(domainMethod, domainMethodsOrder, authorGuid, userLegalName);
	  return domainMethod;
    }

    private void registerChangeLogForDomainMethodOrder(DomainMethod domainMethod, DomainMethodOrder originalDomainMethodsOrder, String authorGuid, String userLegalName) {
      if (nonNull(originalDomainMethodsOrder)) {
        DomainMethodOrder domainMethodsOrder = getDomainMethodsOrder(domainMethod);
        saveChangelogForDomainMethodOrder(domainMethod, originalDomainMethodsOrder, domainMethodsOrder, authorGuid, userLegalName);
      }
    }
	
    public void saveChangelogForDomainMethodOrder(DomainMethod domainMethod, DomainMethodOrder domainMethodsOrderOld, DomainMethodOrder domainMethodsOrder, String authorGuid, String userLegalName) {
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(domainMethodsOrder, domainMethodsOrderOld, new String[]{ "elements" });
            changeLogService.registerChangesWithDomainAndFullName("dm." + (domainMethod.getDeposit() ? "deposit" : "withdraw"), "edit", domainMethod.getDomain().getId(), authorGuid, null, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, domainMethod.getDomain().getName(), userLegalName);
        } catch (Exception e) {
            log.error("Can't save changelog of DomainMethod order (" + domainMethod.getId() + ") due ", e);
        }
  }
	
    public DomainMethodOrder getDomainMethodsOrder(DomainMethod domainMethod) {
        List<DomainMethodOrder.ShortDomainMethod> domainMethodsOrder = domainMethodRepository.findByDomainAndDepositAndDeletedFalseOrderByPriority(domainMethod.getDomain(), domainMethod.getDeposit()).stream()
                .map(method -> DomainMethodOrder.ShortDomainMethod.builder()
                        .id(method.getId())
                        .name(method.getName())
                        .priority(method.getPriority())
                        .enabled(method.getEnabled())
                        .feDefault(method.getFeDefault())
                        .build())
                .collect(Collectors.toList());
        return new DomainMethodOrder(domainMethodsOrder);
    }

    public DomainMethod delete(DomainMethod domainMethod, String authorGuid, String userLegalName) {
      DomainMethodOrder domainMethodsOrder = getDomainMethodsOrder(domainMethod);
      domainMethod.setDeleted(true);
      domainMethod.setEnabled(false);
      domainMethod.setName(domainMethod.getName() + "_" + new Date().getTime());
      domainMethod = domainMethodRepository.save(domainMethod);
      registerChangeLogForDomainMethodOrder(domainMethod, domainMethodsOrder, authorGuid, userLegalName);
      return domainMethod;
    }
	
	public List<DomainMethod> list(String domainName, ProcessorType type) {
		Domain domain = domainService.findOrCreateDomain(domainName);
		List<DomainMethod> domainMethods = new ArrayList<>();
		switch (type) {
			case DEPOSIT:
				domainMethods = domainMethodRepository.findByDomainAndDepositTrueAndDeletedFalseOrderByPriority(domain);
				break;
			case WITHDRAW:
				domainMethods = domainMethodRepository.findByDomainAndDepositFalseAndDeletedFalseOrderByPriority(domain);
				break;
			default:
				return domainMethods;
		}
		return domainMethods.stream()
			.map(dm -> {
				if (dm.getImage() == null) {
					dm.setImage(
						Image.builder()
						.base64(dm.getMethod().getImage().getBase64())
						.filename(dm.getMethod().getImage().getFilename())
						.filesize((long)dm.getMethod().getImage().getBase64().length)
						.filetype(dm.getMethod().getImage().getFiletype())
						.build()
					);
				}
				return dm;
			})
			.collect(Collectors.toList());
	}

	public Map<String, SummaryLabelValue> accountingTotals(DomainMethod dm) throws Exception {
		Map<String, SummaryLabelValue> summaryLabelValues = new HashMap<>();
		String currency = userService.retrieveDomainFromDomainService(dm.getDomain().getName()).getCurrency();;
		String accountCode = (dm.getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		String transactionType = (dm.getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();

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
			dm.getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			lastMonthStart.toString(),
			lastMonthEnd.toString()
		);
		if (domainLastMonth.isSuccessful() && domainLastMonth.getData().size() > 0) {
			summaryLabelValues.put("lastmonth", domainLastMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainMonth = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			monthStart.toString(),
			monthEnd.toString()
		);
		if (domainMonth.isSuccessful() && domainMonth.getData().size() > 0) {
			summaryLabelValues.put("month", domainMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainWeek = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_WEEK,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			weekStart.toString(),
			weekEnd.toString()
		);
		if (domainWeek.isSuccessful() && domainWeek.getData().size() > 0) {
			summaryLabelValues.put("week", domainWeek.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainDay = getAccountingSummaryDomainLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_DAY,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			dayStart.toString(),
			dayEnd.toString()
		);
		if (domainDay.isSuccessful() && domainDay.getData().size() > 0) {
			summaryLabelValues.put("day", domainDay.getData().get(0));
		}
		return summaryLabelValues;
	}

	public Map<String, SummaryLabelValue> accountingTotals(DomainMethod dm, String username) throws Exception {
		Map<String, SummaryLabelValue> summaryLabelValues = new HashMap<>();
		String currency = userService.retrieveDomainFromDomainService(dm.getDomain().getName()).getCurrency();
		String accountCode = (dm.getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		String transactionType = (dm.getDeposit())?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();

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
			dm.getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			lastMonthStart.toString(),
			lastMonthEnd.toString(),
			dm.getDomain().getName()+"/"+username
		);
		if (domainLastMonth.isSuccessful() && domainLastMonth.getData().size() > 0) {
			summaryLabelValues.put("lastmonth", domainLastMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainMonth = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_MONTH,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			monthStart.toString(),
			monthEnd.toString(),
			dm.getDomain().getName()+"/"+username
		);
		if (domainMonth.isSuccessful() && domainMonth.getData().size() > 0) {
			summaryLabelValues.put("month", domainMonth.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainWeek = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_WEEK,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			weekStart.toString(),
			weekEnd.toString(),
			dm.getDomain().getName()+"/"+username
		);
		if (domainWeek.isSuccessful() && domainWeek.getData().size() > 0) {
			summaryLabelValues.put("week", domainWeek.getData().get(0));
		}
		Response<List<SummaryLabelValue>> domainDay = getAccountingSummaryAccountLabelValueClient().get().findLimited(
			dm.getDomain().getName(),
			Period.GRANULARITY_DAY,
			accountCode,
			transactionType,
			CashierTransactionLabels.PROCESSING_METHOD_LABEL,
			dm.getMethod().getCode(),
			currency,
			dayStart.toString(),
			dayEnd.toString(),
			dm.getDomain().getName()+"/"+username
		);
		if (domainDay.isSuccessful() && domainDay.getData().size() > 0) {
			summaryLabelValues.put("day", domainDay.getData().get(0));
		}
		return summaryLabelValues;
	}

	public List<DomainMethod> findAll(String domainName) {
		Domain domain = domainService.findOrCreateDomain(domainName);
		return domainMethodRepository.findAll(DomainMethodSpecification.table("", domain));
	}

	public DomainMethod find(Long id) {
		return domainMethodRepository.findOne(id);
	}

    public DomainMethod toggleEnable(DomainMethod dm, String authorGuid, String userLegalName) {
		dm.setEnabled(!dm.getEnabled());
        return saveDomainMethod(dm, authorGuid, userLegalName);
	}

	public DomainMethod fillInMissingImage(DomainMethod dm) {
		if (dm.getImage() == null) {
			dm.setImage(
				Image.builder()
				.base64(dm.getMethod().getImage().getBase64())
				.filename(dm.getMethod().getImage().getFilename())
				.filesize((long)dm.getMethod().getImage().getBase64().length)
				.filetype(dm.getMethod().getImage().getFiletype())
				.build()
			);
		}
		return dm;
	}

	public List<DomainMethod> getDomainMethods(String domainName) {
		List<DomainMethod> domainMethods = getAllByDomainNameAndDepositFalseAndDeletedFalse(domainName);
		domainMethods = domainMethods
				.stream()
				.filter(dM -> domainMethodProcessorService.getDomainMethodProcessorsByDomainMethodIdAndEnabledTrueAndDeletedFalse(dM.getId()).size() > 0)
				.collect(Collectors.toList());
		log.info("domainMethods={}", domainMethods);
		return domainMethods;
	}

	public List<DomainMethod> getAllByDomainNameAndDepositFalseAndDeletedFalse(String domainName) {
		return domainMethodRepository.findByDomainNameAndDepositFalseAndEnabledTrueAndDeletedFalse(domainName);
	}

	public List<DomainMethod> getAllowedDirectWithdrawMethods(String directWithdrawalParamName, String domainName, String guid, String ipAddr, String userAgent) {
		User user = userService.findOrCreate(guid);
		return getDomainMethods(domainName).stream()
				.filter(domainMethod -> isDirectWithdrawEnabled(directWithdrawalParamName, domainMethod, user, ipAddr, userAgent))
				.collect(Collectors.toList());
	}

	private boolean isDirectWithdrawEnabled(String directWithdrawalParamName, DomainMethod domainMethod, User user, String ipAddr, String userAgent) {
		try {
			List<DomainMethodProcessor> processors = cashierFrontendService.domainMethodProcessors(domainMethod.getId(), user, ipAddr, userAgent);
			if (processors.size() != 1) {
				log.error("Wrong processors count for domainMethod = " + domainMethod.getName());
				return false;
			}

			for (DomainMethodProcessorProperty domainMethodProcessorProperty : domainMethodProcessorService.properties(processors.get(0).getId())) {
				ProcessorProperty property = domainMethodProcessorProperty.getProcessorProperty();
				if (property.getName().equals(directWithdrawalParamName)) {
					String currentValue = domainMethodProcessorProperty.getValue();
					if (currentValue == null) {
						return Boolean.parseBoolean(property.getDefaultValue());
					} else {
						return Boolean.parseBoolean(domainMethodProcessorProperty.getValue());
					}
				}
			}

			return false;
		} catch (Exception ex) {
			log.error("Failed to get processor properties for domain method =" + domainMethod.getName(), ex);
			return false;
		}
	}
}
