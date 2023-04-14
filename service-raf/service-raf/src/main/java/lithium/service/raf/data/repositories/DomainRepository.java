package lithium.service.raf.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.raf.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long>, JpaSpecificationExecutor<Domain> {
	Domain findByName(String name);
}
