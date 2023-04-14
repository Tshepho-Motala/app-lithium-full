package lithium.service.sms.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
}