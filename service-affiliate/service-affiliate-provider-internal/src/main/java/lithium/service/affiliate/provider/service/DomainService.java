package lithium.service.affiliate.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.Domain;
import lithium.service.affiliate.provider.data.repositories.DomainRepository;

@Service
public class DomainService {
	@Autowired
	private DomainRepository repository;
	
	public Domain findOrCreate(String machineName) {
		Domain domain = repository.findByMachineName(machineName.toLowerCase());
		if (domain == null) {
			domain = Domain.builder().machineName(machineName).build();
			repository.save(domain);
		}
		return domain;
	}
}