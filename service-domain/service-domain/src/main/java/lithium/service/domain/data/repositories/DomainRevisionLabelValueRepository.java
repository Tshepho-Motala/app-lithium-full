package lithium.service.domain.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.DomainRevisionLabelValue;

public interface DomainRevisionLabelValueRepository extends PagingAndSortingRepository<DomainRevisionLabelValue, Long> {

}