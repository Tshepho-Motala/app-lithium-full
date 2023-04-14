package lithium.service.entity.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.entity.data.entities.Status;

public interface StatusRepository extends PagingAndSortingRepository<Status, Long> {
	Status findByNameIgnoreCase(String name);

	default Status findOne(Long id) {
		return findById(id).orElse(null);
	}
}