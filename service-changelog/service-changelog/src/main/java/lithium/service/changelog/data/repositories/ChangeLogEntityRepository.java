package lithium.service.changelog.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.changelog.data.entities.ChangeLogEntity;

public interface ChangeLogEntityRepository extends PagingAndSortingRepository<ChangeLogEntity, Long> {

	ChangeLogEntity findByName(String name);
	
}
