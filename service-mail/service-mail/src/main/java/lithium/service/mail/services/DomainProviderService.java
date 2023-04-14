package lithium.service.mail.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.mail.data.entities.DomainProvider;
import lithium.service.mail.data.entities.DomainProviderProperty;
import lithium.service.mail.data.entities.Provider;
import lithium.service.mail.data.entities.ProviderProperty;
import lithium.service.mail.data.repositories.DomainProviderPropertyRepository;
import lithium.service.mail.data.repositories.DomainProviderRepository;
import lithium.service.mail.data.repositories.ProviderRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DomainProviderService {
	@Autowired DomainProviderRepository domainProviderRepository;
	@Autowired DomainService domainService;
	@Autowired ProviderRepository providerRepository;
	@Autowired DomainProviderPropertyRepository domainProviderPropertyRepository;
	@Autowired ProviderService providerService;

	public List<DomainProvider> findByDomainNameAndDeletedFalseAndEnabledTrueOrderByPriority(String domainName) {
		return domainProviderRepository.findByDomainNameAndDeletedFalseAndEnabledTrueOrderByPriority(domainName);
	}
	
	public List<DomainProvider> findAll(String domainName) {
		return domainProviderRepository.findByDomainNameAndDeletedFalseOrderByPriority(domainName);
	}
	
	public DomainProvider addDomainProvider(String domainName, String description, Long providerId) {
		return domainProviderRepository.save(
			DomainProvider.builder()
			.domain(domainService.findOrCreate(domainName))
			.description(description)
			.enabled(true)
			.deleted(false)
			.provider(providerService.findOne(providerId))
			.build()
		);
	}
	
	public DomainProvider find(Long domainProviderId) {
		return domainProviderRepository.findOne(domainProviderId);
	}
	
	public List<DomainProviderProperty> properties(Long domainProviderId) {
		return domainProviderPropertyRepository.findByDomainProviderIdOrderByProviderPropertyName(domainProviderId);
	}
	
	public List<DomainProviderProperty> propertiesWithDefaults(Long domainProviderId) {
		return propertiesProcessed(domainProviderId, true);
	}
	
	public List<DomainProviderProperty> propertiesNoDefaults(Long domainProviderId) {
		return propertiesProcessed(domainProviderId, false);
	}
	
	private List<DomainProviderProperty> propertiesProcessed(Long domainProviderId, boolean defaultValue) {
		List<DomainProviderProperty> domainProviderProperties = properties(domainProviderId);
		DomainProvider domainProvider = find(domainProviderId);
		Provider provider = domainProvider.getProvider();
		List<ProviderProperty> providerProperties = provider.getProperties();
		int providerPropertiesSize = providerProperties.size();
		
		if (domainProviderProperties == null) domainProviderProperties = new ArrayList<>();
		
		if (domainProviderProperties.size() == providerPropertiesSize) {
			return domainProviderProperties;
		} else {
			List<DomainProviderProperty> domainProviderPropertiesDefault = new ArrayList<>();
			for (ProviderProperty providerProperty:providerProperties) {
				DomainProviderProperty domainProviderProperty = DomainProviderProperty.builder()
					.domainProvider(domainProvider)
					.providerProperty(providerProperty)
					.value((defaultValue)?providerProperty.getDefaultValue():null)
					.build();
				domainProviderPropertiesDefault.add(domainProviderProperty);
			}
			Comparator<DomainProviderProperty> c = (dpp1, dpp2) -> dpp1.getProviderProperty().getName().compareTo(dpp2.getProviderProperty().getName());
			
			List<DomainProviderProperty> finalList = Stream.concat(
				domainProviderProperties.stream(),
				domainProviderPropertiesDefault.stream()
			).filter(new TreeSet<>(c)::add)
			.collect(Collectors.toList());
			return finalList;
		}
	}
	
	public List<DomainProviderProperty> saveProperties(DomainProvider domainProvider, List<DomainProviderProperty> domainProviderProperties) throws Exception {
		List<DomainProviderProperty> savedDomainProviderProperties = new ArrayList<>();
		
		for (DomainProviderProperty dpp:domainProviderProperties) {
			log.trace("dpp : "+dpp);
			if (dpp.isOverride()) {
				dpp.setDomainProvider(domainProvider);
				dpp = domainProviderPropertyRepository.save(dpp);
			} else {
				dpp.setDomainProvider(domainProvider);
				dpp = removeProperty(dpp);
			}
			savedDomainProviderProperties.add(dpp);
		}
		return savedDomainProviderProperties;
	}
	
	public DomainProviderProperty removeProperty(DomainProviderProperty dpp) {
		log.debug("Delete : "+dpp);
		domainProviderPropertyRepository.delete(dpp);
		dpp.setId(null);
		dpp.setValue(null);
		return dpp;
	}
	
	public DomainProviderProperty setProperty(DomainProvider domainProvider, String name, String value) throws Exception {
		log.debug("Save : "+domainProvider+" : "+name+" : "+value);
		ProviderProperty providerProperty = providerService.findPropertyByProviderIdAndName(domainProvider.getProvider(), name);
		if (providerProperty == null) throw new Exception("Could not find ProviderProperty with name : " + name);
		
		DomainProviderProperty domainProviderProperty = domainProviderPropertyRepository.findByDomainProviderIdAndProviderPropertyId(domainProvider.getId(), providerProperty.getId());
		if (domainProviderProperty == null) {
			return domainProviderPropertyRepository.save(
				DomainProviderProperty.builder()
				.providerProperty(providerProperty)
				.domainProvider(domainProvider)
				.value(value)
				.build()
			);
		} else {
			domainProviderProperty.setValue(value);
			return domainProviderPropertyRepository.save(domainProviderProperty);
		}
	}
	
	public DomainProvider save(DomainProvider domainProvider) {
		return domainProviderRepository.save(domainProvider);
	}
	
	public DomainProvider delete(DomainProvider domainProvider) {
		domainProvider.setDeleted(true);
		domainProvider.setEnabled(false);
		domainProvider.setDescription(domainProvider.getDescription()+"_"+new Date().getTime());
		return save(domainProvider);
	}

	public Iterable<DomainProvider> findByDomainNameAndProviderType(String domainName, String providerType) {
		return domainProviderRepository.findByDomainNameAndProviderProviderTypeNameAndDeletedFalseAndEnabledTrue(domainName, providerType);
	}

	public DomainProvider findByDomainNameAndProviderUrl(String domainName, String providerUrl) {
		return domainProviderRepository.findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(domainName, providerUrl);
	}
}