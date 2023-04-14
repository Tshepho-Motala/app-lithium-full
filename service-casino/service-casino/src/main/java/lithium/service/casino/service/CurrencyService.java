package lithium.service.casino.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurrencyService {
	@Autowired
	private LithiumServiceClientFactory services;
	
	@Cacheable(value="lithium.service.domain.data.findbyname",key="#root.args[0]", unless="#result == null")
	public lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService(String domainName) throws Exception {
		Response<lithium.service.domain.client.objects.Domain> domain = getDomainClient().get().findByName(domainName);
		if (domain.isSuccessful() && domain.getData() != null) {
			return domain.getData();
		}
		throw new Exception("Unable to retrieve domain from domain service: " + domainName);
	}
	
	public Optional<DomainClient> getDomainClient() {
		return getClient(DomainClient.class, "service-domain");
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
