package lithium.service.user.provider.threshold.data.repositories;

import java.util.List;
import java.util.Optional;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.Type;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThresholdRepository extends PagingAndSortingRepository<Threshold, Long>, JpaSpecificationExecutor<Threshold> {
   Optional<Threshold> findByCurrentId(long currentRevisionId);
   List<Threshold> findByCurrentDomainNameAndCurrentGranularityAndCurrentTypeNameAndActiveTrue(String domainName, int granularity, String typeName);
}
