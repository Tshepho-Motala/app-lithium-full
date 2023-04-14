package lithium.service.user.threshold.data.repositories;

import lithium.service.user.threshold.data.entities.ThresholdRevision;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThresholdRevisionRepository extends PagingAndSortingRepository<ThresholdRevision, Long> {

}
