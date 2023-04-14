package lithium.service.pushmsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository domainRepository;
	
	public Domain findOrCreate(String name) {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			domainRepository.save(domain);
		}
		return domain;
	}
}