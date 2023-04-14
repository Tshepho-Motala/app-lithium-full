package lithium.service.sms.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.sms.data.entities.Provider;
import lithium.service.sms.data.entities.ProviderProperty;
import lithium.service.sms.data.repositories.ProviderPropertyRepository;
import lithium.service.sms.data.repositories.ProviderRepository;

@Service
public class ProviderService {
	@Autowired ProviderPropertyRepository providerPropertyRepository;
	@Autowired ProviderRepository providerRepository;
	
	public Provider findByCode(String code) {
		return providerRepository.findByCode(code);
	}
	
	public Provider findOne(Long providerId) {
		return providerRepository.findOne(providerId);
	}
	
	public Provider save(String name, String url, Boolean enabled) {
		return save(
			Provider.builder()
			.name(name)
			.url(url)
			.enabled(enabled)
			.build()
		);
	}
	
	public Provider save(Provider provider) {
		return providerRepository.save(provider);
	}
	
	public ProviderProperty findPropertyByProviderIdAndName(Provider provider, String name) {
		return providerPropertyRepository.findByProviderAndName(provider, name);
	}
	
	public ProviderProperty saveProperty(ProviderProperty property) {
		return providerPropertyRepository.save(property);
	}
	
	public ProviderProperty saveProperty(Provider provider, 
			lithium.service.sms.client.objects.ProviderProperty property) {
		return providerPropertyRepository.save(
			ProviderProperty.builder()
			.provider(provider)
			.name(property.getName())
			.defaultValue(property.getDefaultValue())
			.type(property.getType())
			.description(property.getDescription())
			.build()
		);
	}
	
	public List<Provider> findAll() {
		Iterable<Provider> providers = providerRepository.findAll();
		List<Provider> providerList = new ArrayList<>();
		providers.forEach(providerList::add);
		return providerList;
	}
}