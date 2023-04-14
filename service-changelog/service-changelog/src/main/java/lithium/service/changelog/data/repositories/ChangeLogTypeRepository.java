package lithium.service.changelog.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.changelog.data.entities.ChangeLogType;

public interface ChangeLogTypeRepository extends PagingAndSortingRepository<ChangeLogType, Long> {

	ChangeLogType findByName(String name);
	
}
