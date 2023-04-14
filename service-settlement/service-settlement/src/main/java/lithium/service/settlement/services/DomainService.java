package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.settlement.data.entities.Domain;
import lithium.service.settlement.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository repository;
	
	@Retryable
	public Domain findOrCreate(String name) {
		Domain domain = repository.findByName(name.toLowerCase());
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			repository.save(domain);
		}
		return domain;
	}
}