package lithium.service.xp.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.xp.data.entities.Status;

public interface StatusRepository extends PagingAndSortingRepository<Status, Long>, JpaSpecificationExecutor<Status> {
	Status findByName(String name);
}
