package lithium.service.affiliate.provider.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.ServiceAffiliateProviderPapModuleInfo;
import lithium.service.affiliate.provider.data.entities.Domain;
import lithium.service.affiliate.provider.data.repositories.DomainRepository;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DomainService {
	@Autowired private DomainRepository repository;
	@Autowired LithiumServiceClientFactory services;
	@Autowired ServiceAffiliateProviderPapModuleInfo info;
	
	public Domain findOrCreate(String machineName) {
		Domain domain = repository.findByMachineName(machineName.toLowerCase());
		if (domain == null) {
			domain = Domain.builder().machineName(machineName).name(machineName).build();
			domain = repository.save(domain);
		}
		return domain;
	}
	
	public List<Provider> getPapAffiliateDomains() {
		Response<Iterable<Provider>> dataResponse = getProviderClient().get().listAllProvidersByTypeAndUrl(ProviderType.PROVIDER_TYPE_AFFILIATE, info.getModuleName());
		List<Provider> papDomainProviderList = new ArrayList<>();
		if (dataResponse.getStatus() == Status.OK) {
			dataResponse.getData().forEach((provider) -> { if (provider.getUrl().equals(info.getModuleName())) papDomainProviderList.add(provider); });
		}
		
		return papDomainProviderList;
	}
	
	public Optional<ProviderClient> getProviderClient() {
		return getClient(ProviderClient.class, "service-domain");
	}
	
	public Optional<DomainClient> getDomainClient() {
		return getClient(DomainClient.class, "service-domain");
	}
	
	@Cacheable(value="lithium.service.domain.data.findbyname",key="#root.args[0]", unless="#result == null")
	public lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService(String domainName) throws Exception {
		Response<lithium.service.domain.client.objects.Domain> domain = getDomainClient().get().findByName(domainName);
		if (domain.isSuccessful() && domain.getData() != null) {
			return domain.getData();
		}
		throw new DoErrorException("Unable to retrieve domain from domain service: " + domainName);
	}
	
	public <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
		
	}
}