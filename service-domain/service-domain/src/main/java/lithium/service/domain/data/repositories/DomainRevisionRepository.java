package lithium.service.domain.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.DomainRevision;

public interface DomainRevisionRepository extends PagingAndSortingRepository<DomainRevision, Long>, JpaSpecificationExecutor<DomainRevision> {
}