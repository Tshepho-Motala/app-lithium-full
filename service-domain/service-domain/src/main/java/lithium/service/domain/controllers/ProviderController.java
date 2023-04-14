package lithium.service.domain.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.DomainProviderLink;
import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderProperty;
import lithium.service.domain.data.repositories.DomainProviderLinkRepository;
import lithium.service.domain.data.repositories.ProviderPropertyRepository;
import lithium.service.domain.data.repositories.ProviderRepository;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.SecurityKeyPairGenerator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/domain/{domainName}/provider/{providerId}")
@Slf4j
public class ProviderController {
	@Autowired
	protected ChangeLogService changeLogService;
	@Autowired
	protected ProviderRepository providerRepository;
	@Autowired
	protected ProviderPropertyRepository providerPropertyRepository;
	@Autowired
	protected ModelMapper mapper;
	@Autowired
	protected DomainProviderLinkRepository domainProviderLinkRepo;
	@Autowired
	protected SecurityKeyPairGenerator kpg;
	
	@GetMapping("/view")
	public Response<Provider> view(
		@PathVariable("domainName") String domainName,
		@PathVariable("providerId") Provider provider
	) throws Exception {
		return Response.<Provider>builder().data(provider).status(Status.OK).build();
	}
	
	@PostMapping("/edit")
	@CacheEvict(cacheNames = {"lithium.service.kyc.api.controller.method-list", "lithium.service.datafeed.provider.google.provider-config"}, allEntries = true)
	public Response<Provider> edit(
			@PathVariable("domainName") String domainName,
			@PathVariable("providerId") Provider _provider,
			@RequestBody @Valid Provider provider, BindingResult br,
			LithiumTokenUtil tokenUtil
	) throws Exception {
		if (br.hasErrors()) {
			ArrayList<String> errors = new ArrayList<>();
			br.getGlobalErrors().forEach(error->{
				errors.add(error.toString());
			});
			return Response.<Provider>builder().status(Status.INVALID_DATA).message("Error happened on data validation").build();
		}

		// Yes I know this is a side effect
		KeyPair kp = null;

		for (ProviderProperty property: provider.getProperties()) {
			property.setProvider(_provider);
			if (property.getValue() == null) {
				property.setValue("");
			}
			if (property.getName().toLowerCase().contains("key")) {
				if (property.getValue().toLowerCase().contentEquals("generate")) {
					if (kp == null) {
						kp = kpg.generateKeyPair();
					}
				}
			}
		}

		if (kp != null) {
			for (ProviderProperty property : provider.getProperties()) {
				if (property.getName().equalsIgnoreCase("privatekey")) {
					property.setValue(kpg.printPrivateKeyInPemFormat(kp));
				}
				if (property.getName().equalsIgnoreCase("publickey")) {
					property.setValue(kpg.printPublicKeyInPemFormat(kp));
				}
			}
		}

		try {
			// Linked by domain id
			List<ChangeLogFieldChange> clfc = changeLogService.copy(
				provider,
				_provider,
				new String[] {"priority", "enabled", "name", "url", "properties"}
			);
			changeLogService.registerChangesWithDomain("domain.provider", "edit", _provider.getDomain().getId(), tokenUtil.guid(), _provider.getId() + " (" + _provider.getName() + ") " + " - " + _provider.getUrl(),null, clfc, Category.SUPPORT, SubCategory.PROVIDER, 0, domainName);
		} catch (Exception e) {
			log.error("Changelog failed. (" + provider + ")", e);
		}

		provider = providerRepository.save(provider);
		
		DomainProviderLink dpl = domainProviderLinkRepo.findByProviderIdAndOwnerLinkTrueAndDeletedFalse(provider.getId());
		dpl.setEnabled(provider.getEnabled());
		domainProviderLinkRepo.save(dpl);

		return Response.<Provider>builder().data(provider).status(Status.OK).build();
	}
	
	@PostMapping("/delete")
	public Response<Provider> delete(
		@PathVariable("domainName") String domainName,
		@PathVariable("providerId") Provider provider
	) throws Exception {
		providerRepository.delete(provider);
		
		return Response.<Provider>builder().data(provider).status(Status.OK).build();
	}
	
	@GetMapping("/propertiesByProviderId")
	public Response <Iterable<ProviderProperty>> properties(
		@PathVariable("providerId") Provider provider
	) throws Exception {
		return Response.<Iterable<ProviderProperty>>builder().data(provider.getProperties()).status(Status.OK).build();
	}
	
	@PostMapping("/properties/add")
	public Response<ProviderProperty> propertyAdd(
		@PathVariable("domainName") String domainName,
		@PathVariable("providerId") Provider provider,
		@RequestParam("propertyName") String propertyName,
		@RequestParam("propertyValue") String propertyValue
	) {
		ProviderProperty p = providerPropertyRepository.findByProviderAndName(provider, propertyName);
		if (p != null) {
			return Response.<ProviderProperty>builder().message("Provider property already exists, not adding").status(Status.CONFLICT).build();
		}
		p = providerPropertyRepository.save(
			ProviderProperty.builder()
			.provider(provider)
			.name(propertyName)
			.value(propertyValue)
			.build()
		);
		return Response.<ProviderProperty>builder().data(p).status(Status.OK).build();
	}
	
	@GetMapping("/linksListByProviderId")
	public Response<Iterable<DomainProviderLink>> listLinkByProviderId(
		@PathVariable("domainName") String domainName,
		@PathVariable("providerId") Provider provider) {
		return Response.<Iterable<DomainProviderLink>>builder()
				.data(domainProviderLinkRepo.findByProviderIdAndOwnerLinkFalseAndDeletedFalse(provider.getId()))
				.status(Status.OK).build();
	}
}
