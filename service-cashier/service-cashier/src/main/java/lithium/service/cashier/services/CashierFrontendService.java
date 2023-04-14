package lithium.service.cashier.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProfile;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.DomainMethodProcessorUser;
import lithium.service.cashier.data.entities.DomainMethodProfile;
import lithium.service.cashier.data.entities.DomainMethodUser;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.objects.FrontendMethod;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.DomainRestriction;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class CashierFrontendService {
	@Autowired
	private DomainMethodService domainMethodService;
	@Autowired
	private DomainMethodProfileService domainMethodProfileService;
	@Autowired
	private DomainMethodUserService domainMethodUserService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private DomainMethodProcessorProfileService domainMethodProcessorProfileService;
	@Autowired
	private DomainMethodProcessorUserService domainMethodProcessorUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccessRuleService accessRuleService;
	@Autowired
	private CashierAccountingChecksService cashierAccountingChecksService;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private LimitInternalSystemService limitInternalSystemService;
	@Autowired
	private TransactionService transactionService;

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
		
	}
	private Optional<AccountingClient> getAccountingClient() {
		return getClient(AccountingClient.class, "service-accounting");
	}
	
	public List<FrontendMethod> methodsDeposit(String userGuid, String ipAddr, String userAgent) {
		return methods(userGuid, ProcessorType.DEPOSIT, ipAddr, userAgent);
	}
	public List<FrontendMethod> methodsWithdraw(String userGuid, String ipAddr, String userAgent) {
		return methods(userGuid, ProcessorType.WITHDRAW, ipAddr, userAgent);
	}
	
	private List<FrontendMethod> methods(String userGuid, ProcessorType type, String ipAddr, String userAgent) {
		User user = userService.findOrCreate(userGuid);
		return domainMethodsFrontend(user, user.domainName(), type, ipAddr, userAgent);
	}
	
	private List<DomainMethod> domainMethods(User user, String domainName, ProcessorType type, String ipAddr, String userAgent, boolean isFirstDeposit) {
		List<DomainMethod> methods = domainMethodService.list(domainName, type).stream()
			.filter(dm -> checkDomainMethodUserAndProfile(dm, user, ipAddr, userAgent, isFirstDeposit))
			.sorted((dm1, dm2) -> dm1.getPriority().compareTo(dm2.getPriority()))
			.collect(Collectors.toList());
		return methods;
	}

	public boolean checkDomainMethodUserAndProfile(DomainMethod domainMethod, User user, String ipAddr, String userAgent, boolean isFirstDeposit) {
		log.debug("{}>>>>>>>>>>>>>>{}", domainMethod, user.getGuid());

		DomainMethodUser dmuser = domainMethodUserService.find(domainMethod, user.getGuid());
		DomainMethodProfile dmprofile = domainMethodProfileService.find(domainMethod, user.getProfile());

		log.debug("{}>>>>>>>>>>>>>>{}", dmuser, dmprofile);
		if (dmprofile != null) {
			if (dmprofile.getEnabled() != null) domainMethod.setEnabled(dmprofile.getEnabled());
			if (dmprofile.getPriority() != null) domainMethod.setPriority(dmprofile.getPriority());
		}
		if (dmuser != null) {
			if (dmuser.getEnabled() == null || !dmuser.getEnabled()) domainMethod.setEnabled(false);
			if (dmuser.getPriority() != null) domainMethod.setPriority(dmuser.getPriority());
		}
		List<DomainMethodProcessor> dmps = domainMethodProcessors(domainMethod.getId(), user, ipAddr, userAgent);

		return (domainMethod.getMethod().getEnabled() != null && domainMethod.getMethod().getEnabled() &&
				domainMethod.getEnabled() != null && domainMethod.getEnabled() &&
				!dmps.isEmpty() && applyRestrictionsOnDomainMethod(user, dmps.get(0)) &&
			    restrictOnFirstDeposit(dmps.get(0), isFirstDeposit) &&
				accessRuleService.checkAuthorization(domainMethod, ipAddr, userAgent));
	}

	private boolean restrictOnFirstDeposit(DomainMethodProcessor domainMethodProcessor, boolean isFirstDeposit) {
		String firstDepositEnabled = domainMethodProcessorService.getPropertyValue(domainMethodProcessor, "first_deposit");
		return !isFirstDeposit || firstDepositEnabled == null || BooleanUtils.isTrue(Boolean.parseBoolean(firstDepositEnabled));
	}

	private boolean applyRestrictionsOnDomainMethod(User user, DomainMethodProcessor domainMethodProcessor) {
		try {
			if (limitInternalSystemService.isContraAccountSet(user.guid())) {
				return true;
			}

			String setContraAccount = domainMethodProcessorService.getPropertyValue(domainMethodProcessor, "set_contra_account");
			return BooleanUtils.isTrue(Boolean.parseBoolean(setContraAccount));
		} catch (Exception e) {
			log.error("Failed to apply user contra account restriction on domain methods. Exception:" + e.getMessage(), e);
		}
		return false;
	}

	public List<FrontendMethod> domainMethodsFrontend(User user, String domainName, ProcessorType type, String ipAddr, String userAgent) {
		try {
			boolean isFirstDeposit = type.equals(ProcessorType.DEPOSIT) && transactionService.findFirstTransaction(user.getGuid(), type.equals(ProcessorType.DEPOSIT) ? TransactionType.DEPOSIT : TransactionType.WITHDRAWAL, DoMachineState.SUCCESS.name()) == null;
			List<DomainMethod> methods = domainMethods(user, domainName, type, ipAddr, userAgent, isFirstDeposit);
			return methods.stream()
				.map(dm -> domainMethodProcessors(dm.getId(), user, ipAddr, userAgent).get(0))
				.map(dmp -> {
					DomainMethod dm = dmp.getDomainMethod();
					List<DomainMethodProcessorProperty> propertiesForFrontend = domainMethodProcessorService.propertiesForFrontend(dmp.getId());
					List<String> allowCardTypes = propertiesForFrontend.stream()
						.filter(dmpp -> nonNull(dmpp.getProcessorProperty()) && "allow_card_types".equals(dmpp.getProcessorProperty().getName()))
						.map(dmpp -> dmpp.getValue().split(","))
						.flatMap(Arrays::stream)
						.map(String::trim)
						.collect(Collectors.toList());

					Limits limits = dmp.getLimits();
					Long balance = null;
					boolean blockProcessing = false;

					if (!dm.getDeposit()) {
						try {
							Response<Long> response = getAccountingClient().get().get(userService.retrieveDomainFromDomainService(domainName).getCurrency(), domainName, user.guid());
							if (response.isSuccessful()) balance = response.getData();
							if ((limits.getMinAmount() > balance)) blockProcessing = true;
						} catch (Exception e) {
						}
					}

					limits.setMinAmount(limits.getMinAmount(isFirstDeposit));
					limits.setMaxAmount(limits.getMaxAmount(isFirstDeposit));

					return FrontendMethod.builder()
						.methodId(dm.getMethod().getId())
						.methodCode(dm.getMethod().getCode())
						.feDefault(dm.getFeDefault())
						.domainMethodId(dm.getId())
						.inApp(dm.getMethod().getInApp())
						.platform(dm.getMethod().getPlatform())
						.name(dm.getName())
						.priority(dm.getPriority())
						.deposit(dm.getDeposit())
						.domain(dm.getDomain())
						.fees(dmp.getFees())
						.limits(limits)
						.userBalanceCents(balance)
						.blockProcessing(blockProcessing)
						.image(dm.getImage())
						.properties(propertiesForFrontend)
						.allowCardTypes(allowCardTypes)
						.build();
				})
				.sorted((dm1, dm2) -> dm1.getPriority().compareTo(dm2.getPriority()))
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Failed to get domain method list for:  userGuid:" + user.getGuid() + " ipAddr: " + ipAddr + " userAgent: " + userAgent + " type: " + type);
			return Collections.emptyList();
		}
	}

	//TODO: remove after strory LSPLAT-748 PLAT-1461 is released
	public List<DomainMethodProcessor> domainMethodProcessorsOld(Long domainMethodId, String username, String domainName, String ipAddr, String userAgent) {
		User user = userService.findOrCreate(domainName+"/"+username);
		return domainMethodProcessors(domainMethodId, user, ipAddr, userAgent);
	}

	public List<DomainMethodProcessor> domainMethodProcessors(Long domainMethodId, String userGuid, String ipAddr, String userAgent) {
		User user = userService.findOrCreate(userGuid);
		return domainMethodProcessors(domainMethodId, user, ipAddr, userAgent);
	}

	public DomainMethodProcessor firstEnabledProcessor(String domainName, String methodCode, boolean isDeposit,
			String userGuid, String ipAddr, String userAgent) throws MoreThanOneMethodWithCodeException, NoMethodWithCodeException {
		DomainMethod dm = domainMethodService.findOneEnabledByCode(domainName, methodCode, isDeposit);
		return domainMethodProcessors(dm.getId(), userGuid, ipAddr, userAgent)
				.stream().filter(DomainMethodProcessor::getEnabled)
				.findFirst()
				.orElseThrow(()->new NoMethodWithCodeException("No configured/enabled proessor"));
	}

	public Optional<DomainMethodProcessor> firstEnabledProcessor(String domainName, String methodCode, boolean isDeposit,
			User user, String ipAddr, String userAgent) throws MoreThanOneMethodWithCodeException, NoMethodWithCodeException {
		DomainMethod dm = domainMethodService.findOneEnabledByCode(domainName, methodCode, isDeposit);
		return domainMethodProcessors(dm.getId(), user, ipAddr, userAgent)
				.stream().filter(DomainMethodProcessor::getEnabled)
				.findFirst();
	}
	
	private Fees fillMissingFees(Fees from, Fees to) {
		if (to.getFlat()==null) to.setFlat(from.getFlat());
		if (to.getMinimum()==null) to.setMinimum(from.getMinimum());
		if (to.getPercentage()==null) to.setPercentage(from.getPercentage());
		if (to.getStrategy()<1) to.setStrategy(from.getStrategy());
		return to;
	}
	private Fees feesOverrides(Fees override, Fees original) {
		if (override.getFlat()!=null) original.setFlat(override.getFlat());
		if (override.getMinimum()!=null) original.setMinimum(override.getMinimum());
		if (override.getPercentage()!=null) original.setPercentage(override.getPercentage());
		if (override.getStrategy()<1) original.setStrategy(override.getStrategy());
		return original;
	}
	private Limits fillMissingLimits(Limits from, Limits to) {
		if (to.getMinAmount()==null) to.setMinAmount(from.getMinAmount());
		if (to.getMaxAmount()==null) to.setMaxAmount(from.getMaxAmount());
		if (to.getMinFirstTransactionAmount()==null) to.setMinFirstTransactionAmount(from.getMinFirstTransactionAmount());
		if (to.getMaxFirstTransactionAmount()==null) to.setMaxFirstTransactionAmount(from.getMaxFirstTransactionAmount());
		if (to.getMaxAmountDay()==null) to.setMaxAmountDay(from.getMaxAmountDay());
		if (to.getMaxAmountWeek()==null) to.setMaxAmountWeek(from.getMaxAmountWeek());
		if (to.getMaxAmountMonth()==null) to.setMaxAmountMonth(from.getMaxAmountMonth());
		if (to.getMaxTransactionsDay()==null) to.setMaxTransactionsDay(from.getMaxTransactionsDay());
		if (to.getMaxTransactionsWeek()==null) to.setMaxTransactionsWeek(from.getMaxTransactionsWeek());
		if (to.getMaxTransactionsMonth()==null) to.setMaxTransactionsMonth(from.getMaxTransactionsMonth());
		return to;
	}
	private Limits limitsOverrides(Limits override, Limits original) {
		if (override.getMinAmount()!=null) original.setMinAmount(override.getMinAmount());
		if (override.getMaxAmount()!=null) original.setMaxAmount(override.getMaxAmount());
		if (override.getMinFirstTransactionAmount()!=null) original.setMinFirstTransactionAmount(override.getMinFirstTransactionAmount());
		if (override.getMaxFirstTransactionAmount()!=null) original.setMaxFirstTransactionAmount(override.getMaxFirstTransactionAmount());
		if (override.getMaxAmountDay()!=null) original.setMaxAmountDay(override.getMaxAmountDay());
		if (override.getMaxAmountWeek()!=null) original.setMaxAmountWeek(override.getMaxAmountWeek());
		if (override.getMaxAmountMonth()!=null) original.setMaxAmountMonth(override.getMaxAmountMonth());
		if (override.getMaxTransactionsDay()!=null) original.setMaxTransactionsDay(override.getMaxTransactionsDay());
		if (override.getMaxTransactionsWeek()!=null) original.setMaxTransactionsWeek(override.getMaxTransactionsWeek());
		if (override.getMaxTransactionsMonth()!=null) original.setMaxTransactionsMonth(override.getMaxTransactionsMonth());
		return original;
	}

	public List<DomainMethodProcessor> domainMethodProcessors(Long domainMethodId, User user, String ipAddr, String userAgent) {
		List<DomainMethodProcessor> domainMethodProcessors = domainMethodProcessorService.list(domainMethodId).stream()
		.filter(dmp -> {
			if (dmp.getFees()==null) dmp.setFees(Fees.builder().build());
			dmp.setFees(fillMissingFees(dmp.getProcessor().getFees(), dmp.getFees()));
			if (dmp.getLimits()==null) dmp.setLimits(Limits.builder().build());
			dmp.setLimits(fillMissingLimits(dmp.getProcessor().getLimits(), dmp.getLimits()));
			if (dmp.getDomainLimits()==null) dmp.setDomainLimits(Limits.builder().build());
			dmp.setDomainLimits(fillMissingLimits(dmp.getProcessor().getLimits(), dmp.getDomainLimits()));
			try {
				return cashierAccountingChecksService.accountingChecks(dmp, user);
			} catch (Exception ex) {
				log.error("Problem performing accounting checks on: " + dmp, ex);
				return false;
			}
		})
		.map(dmp -> {
			DomainMethodProcessorUser dmpu = domainMethodProcessorUserService.findByDomainMethodProcessorAndUser(dmp, user);
			DomainMethodProcessorProfile dmpp = domainMethodProcessorProfileService.findByDomainMethodProcessorAndProfile(dmp, user.getProfile());
			if (dmpp != null) {
				if (dmpp.getEnabled() != null) dmp.setEnabled(dmpp.getEnabled());
				if (dmpp.getWeight() != null) dmp.setWeight(dmpp.getWeight());
				if (dmpp.getLimits() != null) dmp.setLimits(limitsOverrides(dmpp.getLimits(), dmp.getLimits()));
				if (dmpp.getFees() != null) dmp.setFees(feesOverrides(dmpp.getFees(), dmp.getFees()));
			}
			if (dmpu != null) {
				if (dmpu.getEnabled() != null) dmp.setEnabled(dmpu.getEnabled());
				if (dmpu.getWeight() != null) dmp.setWeight(dmpu.getWeight());
				if (dmpu.getLimits() != null) dmp.setLimits(limitsOverrides(dmpu.getLimits(), dmp.getLimits()));
				if (dmpu.getFees() != null) dmp.setFees(feesOverrides(dmpu.getFees(), dmp.getFees()));
			}
			return dmp;
		})
		.filter(dmp ->
			dmp.getProcessor().getEnabled() &&
			dmp.getEnabled() &&
			accessRuleService.checkAuthorization(dmp, ipAddr, userAgent)
		)
		.sorted((dmp1, dmp2) -> dmp2.getWeight().compareTo(dmp1.getWeight()))
		.collect(Collectors.toList());
		log.debug("DomainMethodProcessors (domainMethodId:"+domainMethodId+") Found : "+domainMethodProcessors);
		return domainMethodProcessors;
	}
	
	public List<ProcessedProcessorProperty> mapDomainMethodProcessorProperty(List<DomainMethodProcessorProperty> propertiesWithDefaults) {
		return propertiesWithDefaults.stream()
			.map(dmpp -> {
				return ProcessedProcessorProperty.builder()
					.id(dmpp.getId())
					.name(dmpp.getProcessorProperty().getName())
					.value(dmpp.getValue())
					.type(dmpp.getProcessorProperty().getType())
					.description(dmpp.getProcessorProperty().getDescription())
					.build();
			})
			.collect(Collectors.toList());
	}
}
