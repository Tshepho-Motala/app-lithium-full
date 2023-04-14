package lithium.service.changelog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.changelog.data.entities.Domain;
import lithium.service.changelog.data.repositories.DomainRepository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DomainService {

	@Autowired DomainRepository domainRepository;
	
	@Retryable
	public Domain findOrCreate(String name) {
		Domain domain = domainRepository.findByName(name.toLowerCase());
		if (domain != null) return domain;
		return domainRepository.save(Domain.builder().name(name.toLowerCase()).build());
	}

	public Domain findByName(String name) {
		return domainRepository.findByName(name.toLowerCase());
	}

	public Long[] findDomainIdsByDomainNames(String[] domainNames) {
		if (!ObjectUtils.isEmpty(domainNames)) {
			Long[] longArray = new Long[domainNames.length];
			List<Domain> domains = domainRepository.findAllByNameIn(domainNames);
			for (int i = 0; i < domains.size(); i++) {
				longArray[i] = domains.get(i).getId();
			}
			return longArray;
		}

		return null;
	}
}
