package lithium.service.promo.services;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.DomainAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.repositories.DomainRepository;

import java.util.Optional;

@Service
public class DomainService {
	@Autowired DomainRepository repository;
	@Autowired CachingDomainClientService domainClientService;

	public Domain findOrCreate(String name) {

		Domain domain = repository.findByName(name);

		if (domain == null) {
			lithium.service.domain.client.objects.Domain externalDomain = domainClientService.retrieveDomainFromDomainService(name);
			domain = findOrCreate(name, externalDomain.getDefaultTimezone());
		}
		return domain;
	}

	public Domain findOrCreate(String name, String timezone) {
		Domain domain = repository.findByName(name);

		if (domain == null) {
			return repository.save(Domain.builder().name(name).timezone(timezone).build());
		}

		domain.setTimezone(timezone);
		return repository.save(domain);
	}

	public Domain update(DomainAttributesData data) {
		Domain localDomain = repository.findByName(data.getDomainName());

		if (localDomain != null) {
			localDomain.setTimezone(Optional.ofNullable(data.getDefaultTimezone()).orElse(localDomain.getTimezone()));
			repository.save(localDomain);
		}

		return localDomain;
	}
}
