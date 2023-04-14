package lithium.service.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired DomainRepository repo;
	
	public Domain findByName(String name) {
		return repo.findByName(name);
	}
}
