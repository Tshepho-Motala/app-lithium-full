package lithium.service.domain.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.validation.Valid;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.domain.services.DomainSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.objects.DomainBasic;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.services.DomainCurrencyService;
import lithium.service.domain.services.DomainSettingsService;
import lithium.tokens.JWTDomain;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/domains")
public class DomainsController {
	@Autowired
	private DomainRepository domains;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private DomainCurrencyService domainCurrencyService;
	@Autowired
	private DomainSettingsService domainSettingsService;
	
	@PostMapping
	@Transactional(rollbackOn=Exception.class)
	public Response<Domain> add(@RequestBody @Valid DomainBasic domainBasic, BindingResult bindingResult, Principal principal) throws Exception {
		if (bindingResult.hasErrors()) 
			return Response.<Domain>builder().data2(bindingResult).status(Status.INVALID_DATA).build();
		
		Domain parent = domains.findOne(domainBasic.getParentId());
		Domain domain = Domain.builder()
				.name(domainBasic.getName())
				.displayName(domainBasic.getDisplayName())
				.description(domainBasic.getDescription())
				.deleted(false)
				.enabled(true)
				.players(domainBasic.getPlayers())
				.playerDepositLimits(false)
        		.playerTimeSlotLimits(false)
        		.playerBalanceLimit(false)
				.parent(parent)
				.supportUrl(domainBasic.getSupportUrl())
				.supportEmail(domainBasic.getSupportEmail())
				.url(domainBasic.getUrl())
				.loginAccessRule(domainBasic.getLoginAccessRule())
				.signupAccessRule(domainBasic.getSignupAccessRule())
				.preSignupAccessRule(domainBasic.getPreSignupAccessRule())
				.currency(domainBasic.getCurrency())
				.currencySymbol(domainBasic.getCurrencySymbol())
				.defaultLocale(domainBasic.getDefaultLocale())
				.defaultCountry(domainBasic.getDefaultCountry())
				.build();
		domain = domains.save(domain);

		domainCurrencyService.syncDefaultCurrency(domain);
//		domainSettingsService.getValuesFromDomain(domain.getName(),domain,domainBasic.getTimeout());

		List<ChangeLogFieldChange> clfc = changeLogService.copy(domain, new Domain(),
				new String[] { "name", "displayName", "description", "deleted", "enabled", "players", "parent", "supportUrl", "supportEmail", "url", "currency", "currencySymbol", "defaultLocale" ,"defaultCountry"});
		changeLogService.registerChangesWithDomain("domain", "create", domain.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.CREATE_DOMAIN, 0, domain.getName());
		
		return Response.<Domain>builder().data(domain).build();
	}
	
	@RequestMapping("/findByName")
	public Response<Domain> findByName(@RequestParam("name") String name) {
		Domain domain = domains.findByName(name);
		if (domain == null) return Response.<Domain>builder().status(Status.NOT_FOUND).build();
		return Response.<Domain>builder().data(domain).build();
	}
	
	/**
	 * System auth required. Used for populating default currencies on service-accounting-provider-internal
	 */
	@RequestMapping("/findAllDomains")
	public Response<Iterable<Domain>> findAllDomains() {
		return Response.<Iterable<Domain>>builder().data(domains.findAll()).status(Status.OK).build();
	}

  @RequestMapping("/findAllPlayerDomains")
  public Response<List<Domain>> findAllPlayerDomains() {
    return Response.<List<Domain>>builder().data(domains.findByPlayersIsTrue()).status(Status.OK).build();
  }

  @RequestMapping("/find-all-player-domains")
  public Response<List<Domain>> findAllPlayerDomains(
      @RequestParam("role") String role,
      @RequestParam("domainSetting") String domainSetting,
      @RequestParam("domainSettingValue") String domainSettingValue,
      LithiumTokenUtil tokenUtil
  ) {
    List<String> permittedDomainNames = tokenUtil.playerDomainsWithRole(role).stream().map(JWTDomain::getName).collect(Collectors.toList());
    List<Domain> domainsList = domains.findByPlayersIsTrue().stream()
        .filter(d -> permittedDomainNames.contains(d.getName()))
        .filter(d -> d.getCurrent() != null && d.getCurrent().getLabelValueList().stream()
            .anyMatch(lvl ->
                lvl.getLabel().getName().equals(domainSetting) && lvl.getLabelValue().getValue().equals(domainSettingValue)
            )
        )
        .collect(Collectors.toList());
    return Response.<List<Domain>>builder().data(domainsList).status(Status.OK).build();
  }
}
