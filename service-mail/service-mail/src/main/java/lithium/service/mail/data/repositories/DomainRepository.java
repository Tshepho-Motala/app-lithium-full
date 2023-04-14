package lithium.service.mail.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	
	default Domain findOne(Long id) {
		return findById(id).orElse(null);
	}

	Domain findByName(String name);

}
