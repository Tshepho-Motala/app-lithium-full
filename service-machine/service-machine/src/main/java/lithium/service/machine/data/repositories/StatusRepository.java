package lithium.service.machine.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Status;

public interface StatusRepository extends PagingAndSortingRepository<Status, Long> {
	Status findByDomainAndName(Domain domain, String name);

	default Status findOne(Long id) {
		return findById(id).orElse(null);
	}
}