package lithium.service.domain.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainProviderLink;
import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderType;
import lithium.service.domain.data.objects.ProviderBasic;
import lithium.service.domain.data.objects.ProviderLinkBasic;
import lithium.service.domain.data.repositories.DomainProviderLinkRepository;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.ProviderPropertyRepository;
import lithium.service.domain.data.repositories.ProviderRepository;
import lithium.service.domain.data.repositories.ProviderTypeRepository;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/domain/{domainName}/providers")
@Slf4j
public class ProvidersController {
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	protected ProviderRepository providerRepository;
	@Autowired
	protected ProviderPropertyRepository providerPropertyRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private ProviderTypeRepository providerTypeRepository;
	@Autowired
	private DomainProviderLinkRepository domainProviderLinkRepo;
	@Autowired
	private DomainController domainController;

	@GetMapping("/auth")
	public Response<List<Provider>> authList(
		@PathVariable("domainName") String domainName
	) {
		Domain domain = domainRepository.findByName(domainName);
		Iterable<DomainProviderLink> dpl = domainProviderLinkRepo.findByDomainNameAndProviderProviderTypeNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(domainName, "AUTH");
		List<Provider> providers = new ArrayList<>();

		if (dpl != null && dpl.iterator().hasNext()) {
			dpl.forEach(d -> {
				providers.add(d.getProvider());
			});
		}

		Iterable<Provider> allProv = providerRepository.findByDomainIdAndProviderTypeNameOrderByPriority(domain.getId(), "AUTH");
		if (allProv != null && allProv.iterator().hasNext()) {
			allProv.forEach(d -> {
				providers.add(d);
			});
		}

		if (!providers.isEmpty()) {
			providers.forEach(p -> { p.setProperties(null); p.setDomain(null); p.setProviderType(null); });
			providers.removeIf(p -> p.getEnabled() == false);
			providers.removeIf(p -> p.internal());
			return Response.<List<Provider>>builder().data(providers).status(Status.OK).build();
		}

		return Response.<List<Provider>>builder().data(Collections.emptyList()).status(Status.NOT_FOUND).build();
	}

	@GetMapping("/listbydomainandtype")
	public Response<Iterable<Provider>> listByDomainAndType(@PathVariable("domainName") String domainName, @RequestParam("type") String type) {
		Domain domain = domainRepository.findByName(domainName);
		Iterable<DomainProviderLink> dpl = domainProviderLinkRepo.findByDomainNameAndProviderEnabledTrueAndProviderProviderTypeNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(domainName, type);
		ArrayList<Provider> all = new ArrayList<>();
		if(dpl != null && dpl.iterator().hasNext()) {
			dpl.forEach(d -> {
				  all.add(d.getProvider());
			});
		}

		Iterable<Provider> allProv = providerRepository.findByDomainIdAndProviderTypeNameAndEnabledTrueOrderByPriority(domain.getId(), type);
		if(allProv != null && allProv.iterator().hasNext()) {
			allProv.forEach(d -> {
				all.add(d);
			});
		}

		if(!all.isEmpty()) {
			return Response.<Iterable<Provider>>builder().data(all).status(Status.OK).build();
		}

		return Response.<Iterable<Provider>>builder().data(all).status(Status.NOT_FOUND).build();
	}

	@GetMapping("/listbytype")
	public Response<Iterable<Provider>> listByType(@PathVariable("domainName") String domainName, @RequestParam("type") String type) {
		Domain domain = domainRepository.findByName(domainName);
		Iterable<DomainProviderLink> dpl = domainProviderLinkRepo.findByDomainNameAndProviderProviderTypeNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(domainName, type);
		ArrayList<Provider> all = new ArrayList<>();
		if(dpl != null && dpl.iterator().hasNext()) {
			dpl.forEach(d -> {
				all.add(d.getProvider());
			});
		}

		Iterable<Provider> allProv = providerRepository.findByDomainIdAndProviderTypeNameOrderByPriority(domain.getId(), type);
		if(allProv != null && allProv.iterator().hasNext()) {
			allProv.forEach(d -> {
				all.add(d);
			});
		}

		if(!all.isEmpty()) {
			return Response.<Iterable<Provider>>builder().data(all).status(Status.OK).build();
		}

		return Response.<Iterable<Provider>>builder().data(all).status(Status.NOT_FOUND).build();
	}

	@GetMapping
	public Response<Iterable<Provider>> list(
		@PathVariable("domainName") String domainName) {

		Iterable<Provider> all = providerRepository.findByDomainNameOrderByPriority(domainName);
		return Response.<Iterable<Provider>>builder().data(all).status(Status.OK).build();
	}

	@GetMapping("/linksList")
	public Response<Iterable<DomainProviderLink>> listLink(
		@PathVariable("domainName") String domainName) {

		Iterable<DomainProviderLink> all = domainProviderLinkRepo.findByDomainNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(domainName);
		return Response.<Iterable<DomainProviderLink>>builder().data(all).status(Status.OK).build();
	}

	@GetMapping("/availableProviderLinksList")
	public Response<Iterable<DomainProviderLink>> availableProviderLinksList(
		@PathVariable("domainName") String domainName,
		@RequestParam("providerUrl") String providerUrl) {
		List<DomainProviderLink> linkList = new ArrayList<>();
		List<Domain> domainList = domainController.ancestors(domainName).getData();

		if(domainList != null) {
			for(Domain d: domainList) {
				DomainProviderLink dpl = domainProviderLinkRepo.findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(d.getName(), providerUrl);
				if(dpl != null) {
					linkList.add(dpl);
				}
			}
		}
		return Response.<Iterable<DomainProviderLink>>builder().data(linkList).status(Status.OK).build();
	}

	@PostMapping("/add")
	@CacheEvict(cacheNames = {"lithium.service.kyc.api.controller.method-list", "lithium.service.datafeed.provider.google.provider-config"}, allEntries = true)
	@Transactional
	public Response<Provider> add(
		@PathVariable("domainName") String domainName,
		@RequestBody @Valid ProviderBasic prov,
		BindingResult br,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		Domain domain = domainRepository.findByName(domainName);
		Provider p = providerRepository.findByUrlAndDomainNameAndProviderTypeName(prov.getUrl(), domainName, prov.getType());

		if (p != null) {
			return Response.<Provider>builder()
					.message("Provider already exists, not adding")
					.status(Status.CONFLICT)
					.build();
		}
		ProviderType pt = providerTypeRepository.findByName(prov.getType());
		if (pt == null) {
			return Response.<Provider>builder()
					.message("Provider type not found")
					.status(Status.NOT_FOUND)
					.build();
		}

		p = providerRepository.save(
			Provider.builder()
			.domain(domain)
			.name(prov.getName())
			.url(prov.getUrl())
			.priority(1)
			.providerType(pt)
			.enabled(false)
			.build()
		);

		DomainProviderLink dpl = DomainProviderLink.builder()
				.domain(domain)
				.provider(p)
				.enabled(true)
				.deleted(false)
				.ownerLink(true)
				.build();
		domainProviderLinkRepo.save(dpl);

		try {
			// Linked by domain id
			List<ChangeLogFieldChange> clfc = changeLogService.copy(
				p,
				new Provider(),
				new String[] {"priority", "enabled", "name", "url", "properties"}
			);
			changeLogService.registerChangesWithDomain("domain.provider", "create", domain.getId(), tokenUtil.guid(), p.getId() + " (" + p.getName() + ") " + " - " + p.getUrl(),null, clfc, Category.SUPPORT, SubCategory.PROVIDER, 0, domainName);
		} catch (Exception e) {
			log.error("Provider added, but changelog failed. (" + p + ")", e);
		}

		return Response.<Provider>builder().data(p).status(Status.OK).build();
	}

	@PostMapping("/addLink")
	public Response<DomainProviderLink> addLink(
		@PathVariable("domainName") String domainName,
		@RequestParam("linkId") Long linkId
	) throws Exception {
		Domain domain = domainRepository.findByName(domainName);
		DomainProviderLink dplOrig = domainProviderLinkRepo.findOne(linkId);
		Provider p = dplOrig.getProvider();
		if(domainProviderLinkRepo.findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(domainName, p.getUrl()) != null) {
			return Response.<DomainProviderLink>builder().status(Status.CONFLICT).build();
		}
		if (p != null) {
			DomainProviderLink dpl = DomainProviderLink.builder()
					.domain(domain)
					.provider(p)
					.enabled(true)
					.deleted(false)
					.ownerLink(false)
					.build();
			domainProviderLinkRepo.save(dpl);
			return Response.<DomainProviderLink>builder().data(dpl).status(Status.OK).build();
		}
		return Response.<DomainProviderLink>builder().status(Status.INVALID_DATA).build();
	}

	@PostMapping("/editLink")
	public Response<DomainProviderLink> editLink(
		@PathVariable("domainName") String domainName,
		@RequestBody @Valid ProviderLinkBasic domainProviderLinkBasic, BindingResult br) throws Exception {
		if (br != null && br.hasErrors()) {
			return Response.<DomainProviderLink>builder().status(Status.INVALID_DATA).build();
		}
		Domain domain = domainRepository.findByName(domainName);
		DomainProviderLink dplOrig = domainProviderLinkRepo.findOne(domainProviderLinkBasic.getOwnerLinkId());
		DomainProviderLink currentProviderLink = domainProviderLinkRepo.findOne(domainProviderLinkBasic.getLinkId());

		if(currentProviderLink == null || dplOrig == null) {
			return Response.<DomainProviderLink>builder().status(Status.INVALID_DATA).build();
		}

		currentProviderLink.setProvider(dplOrig.getProvider());
		currentProviderLink.setEnabled(domainProviderLinkBasic.getEnabled());
		currentProviderLink.setDeleted(domainProviderLinkBasic.getDeleted());
		currentProviderLink = domainProviderLinkRepo.save(currentProviderLink);

		return Response.<DomainProviderLink>builder().data(currentProviderLink).status(Status.OK).build();
	}

	@GetMapping("/viewLink")
	public Response<DomainProviderLink> viewLink(
		@PathVariable("domainName") String domainName,
		@RequestParam("linkId") Long linkId
	) throws Exception {
		DomainProviderLink p = domainProviderLinkRepo.findOne(linkId);
		if (p == null) {
			return Response.<DomainProviderLink>builder().status(Status.NOT_FOUND).build();
		}

		return Response.<DomainProviderLink>builder().data(p).status(Status.OK).build();
	}

	@GetMapping("/findOwnerLink")
	public Response<DomainProviderLink> findOwnerLink(
		@PathVariable("domainName") String domainName,
		@RequestParam("providerId") Long providerId) {
		DomainProviderLink p = domainProviderLinkRepo.findByProviderIdAndOwnerLinkTrueAndDeletedFalse(providerId);
		if (p == null) {
			return Response.<DomainProviderLink>builder().status(Status.NOT_FOUND).build();
		}

		return Response.<DomainProviderLink>builder().data(p).status(Status.OK).build();
	}

	@GetMapping("/findByUrlAndDomainName")
	public Response<Provider> findByUrlAndDomainName(@RequestParam("url") String url, @PathVariable("domainName") String domainName) {
		Provider provider = null;
		try {
			provider = providerRepository.findByUrlAndDomainName(url, domainName);
			if (provider == null) {
				return Response.<Provider>builder().status(Status.NOT_FOUND).build();
			} else {
				return Response.<Provider>builder().data(provider).status(Status.OK).build();
			}
		} catch (Exception e) {
			log.error("Error occurred while trying to find provider | " + domainName + " | " + url);
			return Response.<Provider>builder().message(e.getMessage()).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(
		@PathVariable("domainName") String domainName,
		@RequestParam int p
	) throws Exception {
		Domain domain = domainRepository.findByName(domainName);
		if (domain == null) throw new Exception("Domain " + domainName + " not found");
		return changeLogService.listLimited(
			ChangeLogRequest.builder()
			.entityRecordId(domain.getId())
			.entities(new String[] { "domain.provider", "domain.providers.casino.betting" })
			.page(p)
			.build()
		);
	}
}
