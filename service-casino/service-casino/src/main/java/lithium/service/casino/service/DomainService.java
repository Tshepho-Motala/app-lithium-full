package lithium.service.casino.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository domainRepository;
	
	public Domain findOrCreate(String name) {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			domain = domainRepository.save(Domain.builder().name(name).build());
		}
		return domain;
	}
	
}
