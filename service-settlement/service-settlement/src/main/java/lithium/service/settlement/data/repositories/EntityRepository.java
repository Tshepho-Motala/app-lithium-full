package lithium.service.settlement.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.Entity;

public interface EntityRepository extends PagingAndSortingRepository<Entity, Long> {
	Entity findByUuid(String uuid);
}
