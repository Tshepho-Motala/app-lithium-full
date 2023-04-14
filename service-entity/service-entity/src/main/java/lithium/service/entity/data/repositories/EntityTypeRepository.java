package lithium.service.entity.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.entity.data.entities.Domain;
import lithium.service.entity.data.entities.EntityType;

public interface EntityTypeRepository extends PagingAndSortingRepository<EntityType, Long> {
	public List<EntityType> findByDomainOrderByName(Domain domain);

	default EntityType findOne(Long id) {
		return findById(id).orElse(null);
	}
}