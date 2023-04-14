package lithium.service.entity.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.entity.data.entities.Entity;

public interface EntityRepository extends PagingAndSortingRepository<Entity, Long>, JpaSpecificationExecutor<Entity> {
	Entity findByUuid(String uuid);

	default Entity findOne(Long id) {
		return findById(id).orElse(null);
	}
}