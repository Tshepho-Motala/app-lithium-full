package lithium.service.product.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long>, JpaSpecificationExecutor<Domain> {
	Domain findByName(String name);
}
