package lithium.service.domain.client;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class ProviderClientService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired MessageSource messageSource;

	public void checkProviderEnabled(String domainName, String url, String locale) throws Status550ServiceDomainClientException, Status474DomainProviderDisabledException {
		Provider provider = findProviderByUrlAndDomainName(domainName, url);
		if (provider.getEnabled() != null && !provider.getEnabled())
			throw new Status474DomainProviderDisabledException(messageSource.getMessage("SERVICE_DOMAIN.DOMAIN.PROVIDER_DISABLED", new Object[] { domainName, url }, Locale.forLanguageTag(locale)));
	}

	public Provider findProviderByUrlAndDomainName(String domainName, String url) throws Status550ServiceDomainClientException {
		log.debug("Retrieving provider " + domainName + " " + url);
		Response<Provider> provider = getProviderClient().findByUrlAndDomainName(url, domainName);
		if (provider.isSuccessful() && provider.getData() != null) {
			log.info("Retrieved provider " + provider.toString());
			return provider.getData();
		}
		throw new Status550ServiceDomainClientException("Unable to retrieve provider from domain service: " + domainName + " " + url);
	}

	public Provider findProviderByUrlAndDomainNameIfExists(String url, String domainName)
			throws Status550ServiceDomainClientException {
		Response<Provider> provider = getProviderClient().findByUrlAndDomainName(url, domainName);
		if (provider.isSuccessful() && provider.getData() != null) {
			return provider.getData();
		}
		return null;
	}

	public Iterable<ProviderProperty> getProviderProperties(String providerUrl, String domainName)
			throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException {
		ProviderClient client = getProviderClient();
		Response<Iterable<ProviderProperty>> properties =
				client.propertiesByProviderUrlAndDomainName(providerUrl, domainName);

		if (!properties.isSuccessful() || properties.getData() == null) {
			throw new Status512ProviderNotConfiguredException(domainName);
		}

		return properties.getData();
	}

	@Cacheable(value="lithium.service.domain.enabledProvidersByDomainAndProviderType", key="{#root.args[0], #root.args[1].type()}", unless="#result == null")
	public List<Provider> providers(String domainName, ProviderConfig.ProviderType providerType)
			throws Status550ServiceDomainClientException {
		log.warn("####################### NOT FROM CACHE #######################");
		Response<Iterable<Provider>> response = getProviderClient().listByDomainAndType(domainName, providerType.type());
		List<Provider> providers = new ArrayList<>();

		if (response.isSuccessful()) {
			response.getData().forEach(providers::add);
			providers.removeIf(p -> p.getEnabled() == false);
			providers.sort(Comparator.comparingInt(Provider::getPriority));
		}

		if (providers.size() == 0) {
			Provider provider = Provider.builder().name("provider-internal").url("internal").build();
			provider.providerType(providerType.type());
			providers.add(provider);
		}

		return providers;
	}
	
	public ProviderClient getProviderClient() throws Status550ServiceDomainClientException {
		try {
			return services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException fe) {
			throw new Status550ServiceDomainClientException(fe);
		}
	}
}
