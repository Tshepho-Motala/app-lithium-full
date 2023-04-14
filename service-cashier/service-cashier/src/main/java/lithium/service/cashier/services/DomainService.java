package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository domainRepository;
	
	public Domain findOrCreateDomain(String name) {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			domainRepository.save(domain);
		}
		return domain;
	}

	public Domain findByName(String name) throws Exception {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			throw new Exception("Domain not found");
		}
		return domain;
	}
}