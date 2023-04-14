package lithium.service.domain.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.domain.client.objects.AddressBasic;
import lithium.service.domain.client.objects.DomainAttributesData;
import lithium.service.domain.client.stream.DomainAttributesStream;
import lithium.service.domain.data.entities.Address;
import lithium.service.domain.data.entities.BankingDetails;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.repositories.AddressRepository;
import lithium.service.domain.data.repositories.BankingDetailsRepository;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.services.DomainCurrencyService;
import lithium.service.domain.services.DomainSettingsService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/domain/{domainName}")
@Slf4j
public class DomainController {
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private BankingDetailsRepository bankingDetailsRepo;
	@Autowired
	private DomainCurrencyService domainCurrencyService;
	@Autowired
	private DomainSettingsService domainSettingsService;

  @Autowired
  private DomainAttributesStream domainAttributesStream;

	@GetMapping
	public Response<Domain> get(@PathVariable("domainName") String domainName) {
		Domain domain = domainRepository.findByName(domainName);
		return Response.<Domain>builder().data(domain).status(OK).build();
	}

	@PostMapping("/updateCurrency")
	public Response<Domain> updateCurrency(
		@PathVariable("domainName") String domainName,
		@RequestParam("symbol") String symbol,
		@RequestParam("code") String code,
		Principal principal
	) {
		log.info("Updating domain ("+domainName+") default currency to :: "+code+"|"+symbol);
		Domain domain = domainRepository.findByName(domainName);
		domain.setCurrency(code);
		domain.setCurrencySymbol(symbol);
		return Response.<Domain>builder().data(domainRepository.save(domain)).status(OK).build();
	}

	@PostMapping
	@Transactional(rollbackOn=Exception.class)
	public Response<Domain> save(@PathVariable("domainName") String domainName, @Valid @RequestBody Domain update,
			Principal principal) throws Exception {
		Domain oldDomain = domainRepository.findOne(update.getId());
		
		boolean needToSyncCurrency = false;
		
		if (!oldDomain.getCurrency().equalsIgnoreCase(update.getCurrency()) ||
			!oldDomain.getCurrencySymbol().equalsIgnoreCase(update.getCurrencySymbol())) {
			needToSyncCurrency = true;
    }

    List<ChangeLogFieldChange> clfc = changeLogService.copy(update, oldDomain,
				new String[] { "name", "displayName", "description", "enabled", "deleted", "players", "url",
						"supportUrl", "supportEmail", "url", "parent", "currency", "currencySymbol", "defaultLocale", "defaultCountry" });
    changeLogService.registerChangesWithDomain("domain", "edit", update.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);
		Domain domain = domainRepository.save(update);
//		domainSettingsService.getValuesFromDomain(domain.getName(),domain,update.getTimeout());
		log.info("needToSyncCurrency " + needToSyncCurrency);
		if (needToSyncCurrency) domainCurrencyService.syncDefaultCurrency(domain);
//		domainSettingsService.addTimeoutIfAvailable(domain);

    domainAttributesStream.process(DomainAttributesData.builder()
        .defaultTimezone(oldDomain.getDefaultTimezone())
        .domainName(oldDomain.getName())
        .build());
		return Response.<Domain>builder().data(domain).status(OK).build();
	}

	@GetMapping(value = "/enable")
	private Response<Domain> enable(@PathVariable("domainName") String domainName, Principal principal)
			throws Exception {

		Domain domain = domainRepository.findByName(domainName);

		boolean oldValue = domain.getEnabled();
		domain.setEnabled(true);

		List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
		ChangeLogFieldChange c = ChangeLogFieldChange.builder().field("enabled").fromValue(String.valueOf(oldValue))
				.toValue(String.valueOf(domain.getEnabled())).build();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("domain", "enable", domain.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.EDIT_DOMAIN, 0, domainName);

		domainRepository.save(domain);

		return Response.<Domain>builder().data(domain).build();
	}

	@GetMapping(value = "/disable")
	private Response<Domain> disable(@PathVariable("domainName") String domainName, Principal principal)
			throws Exception {
		Domain domain = domainRepository.findByName(domainName);

		boolean oldValue = domain.getEnabled();
		domain.setEnabled(false);

		List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
		ChangeLogFieldChange c = ChangeLogFieldChange.builder().field("enabled").fromValue(String.valueOf(oldValue))
				.toValue(String.valueOf(domain.getEnabled())).build();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("domain", "disable", domain.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.EDIT_DOMAIN, 0, domainName);

		domainRepository.save(domain);

		return Response.<Domain>builder().data(domain).build();
	}

	@RequestMapping("/children")
	public Response<List<Domain>> children(@PathVariable("domainName") String domainName) {
		Domain domain = domainRepository.findByName(domainName);
		List<Domain> domains = findChildren(domain);
		domains.add(0, domain);
		return Response.<List<Domain>>builder().data(domains).status(OK).build();
	}

	@RequestMapping("/ancestors")
	@Cacheable(cacheNames = "lithium.service.domain.controllers.ancestors", unless = "#result.getData().isEmpty()")
	public Response<List<Domain>> ancestors(@PathVariable("domainName") String domainName) {
		Domain domain = domainRepository.findByName(domainName);
		List<Domain> domains = findAncestors(domain);
		return Response.<List<Domain>>builder().data(domains).status(OK).build();
	}

	private List<Domain> findChildren(Domain domain) {
		List<Domain> domains = new ArrayList<Domain>();
		if (domain != null) {
			List<Domain> subDomains = domainRepository.findByParentId(domain.getId());
			subDomains.forEach(d -> {
				if (d.getDeleted() == null || (d.getDeleted() != null && d.getDeleted() != true)) {
					d.setSuperId(d.getParent().getId());
					d.setSuperName(d.getParent().getName());
					d.setParent(null);
					domains.add(d);
					domains.addAll(findChildren(d));
				}
			});
		}
		return domains;
	}

	private List<Domain> findAncestors(Domain domain) {
		List<Domain> domains = new ArrayList<Domain>();
		findParents(domains, domain);
		return domains;
	}

	private void findParents(List<Domain> domainList, Domain domain) {
		if (domain != null) {
			if (domain.getParent() != null) {
				domainList.add(domain.getParent());
				findParents(domainList, domain.getParent());
			}
		}
	}

	@PostMapping(value = "/saveaddress")
	public Response<Domain> saveAddress(@PathVariable("domainName") String domainName,
			@RequestBody @Valid AddressBasic addressBasic, BindingResult bindingResult, Authentication authentication)
			throws Exception {
		log.debug("AddressBasic : " + addressBasic);
		Address address = modelMapper.map(addressBasic, Address.class);
		log.debug("Address : " + address);

		if (address.getId() != null) {
			address.setId(null);
		}

		address = addressRepository.save(address);
		log.debug("Saved Address : " + address);

		Domain domain = domainRepository.findByName(domainName);
		Domain oldDomain = new Domain();
		oldDomain.setPostalAddress(domain.getPostalAddress());
		oldDomain.setPhysicalAddress(domain.getPhysicalAddress());

		if (addressBasic.isPostalAddress()) {
			domain.setPostalAddress(address);
		} else if (addressBasic.isPhysicalAddress()) {
			domain.setPhysicalAddress(address);
		}

		List<ChangeLogFieldChange> clfc = changeLogService.copy(domain, oldDomain,
				new String[] { "postalAddress", "physicalAddress" });
		changeLogService.registerChangesWithDomain("domain", "edit", domain.getId(), authentication.getName(), null, null, clfc, Category.SUPPORT, SubCategory.EDIT_DOMAIN, 0, domainName);

		domain = domainRepository.save(domain);
		log.debug("Saved Domain : " + domain);

		return Response.<Domain>builder().status(OK).data(domain).build();
	}

	@PostMapping(value = "/savebankingdetails")
	public Response<BankingDetails> saveBankingDetails(@PathVariable("domainName") String domainName,
			@RequestBody lithium.service.domain.client.objects.BankingDetails bankingDetails,
			Authentication authentication) {
		log.info("BankingDetails : " + bankingDetails);
		Domain domain = domainRepository.findByName(domainName);

		BankingDetails bd = null;

		if (domain.getBankingDetails() != null) {
			bd = domain.getBankingDetails();
			if (bankingDetails.getOrgId() != null) {
				bd.setOrgId(bankingDetails.getOrgId());
			}
			if (bankingDetails.getBankIdentifierCode() != null) {
				bd.setBankIdentifierCode(bankingDetails.getBankIdentifierCode());
			}
			if (bankingDetails.getAccountHolder() != null) {
				bd.setAccountHolder(bankingDetails.getAccountHolder());
			}
			if (bankingDetails.getAccountNumber() != null) {
				bd.setAccountNumber(bankingDetails.getAccountNumber());
			}
		} else {
			bd = modelMapper.map(bankingDetails, BankingDetails.class);
		}

		bd = bankingDetailsRepo.save(bd);

		domain.setBankingDetails(bd);
		domain = domainRepository.save(domain);

		return Response.<BankingDetails>builder().data(bd).status(OK).build();
	}

	@PostMapping("/bettingenabled/toggle")
	public Response<Domain> toggleBettingEnabled(
		@PathVariable("domainName") String domainName,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		try {
			Domain domain = domainRepository.findByName(domainName);
			if (domain == null) throw new Exception("Domain " + domainName + " not found");
			boolean bettingEnabled = domain.getBettingEnabled();
			domain.setBettingEnabled(!bettingEnabled);
			ChangeLogFieldChange c = ChangeLogFieldChange.builder()
			.field("bettingEnabled")
			.fromValue(String.valueOf(bettingEnabled))
			.toValue(String.valueOf(!bettingEnabled))
			.build();
			List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
			clfc.add(c);
			String clType = (!bettingEnabled) ? "enable" : "disable";
			// Entity is domain.providers.casino.betting. This way I can add it to the main domain page, and providers page. It is a requirement.
			// The domain id has to be used to tie it all together.
			changeLogService.registerChangesWithDomain("domain.providers.casino.betting", clType, domain.getId(), tokenUtil.guid(), null, null, clfc, Category.SUPPORT, SubCategory.EDIT_DOMAIN, 0, domainName);
			domain = domainRepository.save(domain);
			return Response.<Domain>builder().data(domain).status(OK).build();
		} catch (Exception e) {
			log.error("Failed to toggle betting enabled for " + domainName + " | " + e.getMessage());
			return Response.<Domain>builder().message(e.getMessage()).status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(value = "/{id}/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("id") Long id, @RequestParam int p) throws Exception {
		return changeLogService.listLimited(
			ChangeLogRequest.builder()
			.entityRecordId(id)
			.entities(new String[] { "domain", "domain.providers.casino.betting" })
			.page(p)
			.build()
		);
	}

	@GetMapping(value = "/{id}/changelogs/limits")
	public @ResponseBody Response<ChangeLogs> changeLogsLimits(@PathVariable("id") Long id, @RequestParam int p) throws Exception {
		return changeLogService.listLimited(
			ChangeLogRequest.builder()
			.entityRecordId(id)
			.entities(new String[] { "domain.ageLimit", "domain.limit", "domain.maxlimit", "threshold.thresholdrevision"})
			.page(p)
			.build()
		);
	}
}
