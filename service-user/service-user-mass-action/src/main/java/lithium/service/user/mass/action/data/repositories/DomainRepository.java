package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.Domain;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String domainName);
}