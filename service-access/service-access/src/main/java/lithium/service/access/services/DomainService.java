package lithium.service.access.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.access.data.entities.Domain;
import lithium.service.access.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository repository;
	
	public Domain findOrCreate(String name) {
		Domain domain = repository.findByName(name.toLowerCase());
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			repository.save(domain);
		}
		return domain;
	}
}