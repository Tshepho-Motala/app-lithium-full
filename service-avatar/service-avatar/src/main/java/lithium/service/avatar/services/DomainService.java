package lithium.service.avatar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.avatar.data.entities.Domain;
import lithium.service.avatar.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired DomainRepository repository;
	
	public Domain findOrCreate(String name) {
		Domain domain = repository.findByName(name);
		if (domain == null) domain = repository.save(Domain.builder().name(name).build());
		return domain;
	}
}
