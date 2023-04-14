package lithium.service.raf.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.raf.data.entities.Referrer;

public interface ReferrerRepository extends PagingAndSortingRepository<Referrer, Long>, JpaSpecificationExecutor<Referrer> {
	Referrer findByPlayerGuid(String playerGuid);
}
