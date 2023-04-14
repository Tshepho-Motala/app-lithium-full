package lithium.service.cdn.cms.data.repositories;

import lithium.service.cdn.cms.data.entities.Domain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long>, JpaSpecificationExecutor<Domain> {
    Domain findByName(String name);
}
