package lithium.service.user.provider.threshold.data.repositories;

import java.util.List;
import java.util.Optional;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThresholdAgeGroupRepository extends PagingAndSortingRepository<ThresholdAgeGroup, Long> {

  List<ThresholdAgeGroup> findByAgeMaxAndAgeMin(int max, int min);
  Optional<ThresholdAgeGroup> findByThresholdRevision(ThresholdRevision thresholdRevision);
  Optional<ThresholdAgeGroup> findByThresholdRevisionDomainNameAndThresholdRevisionGranularityAndThresholdRevisionTypeNameAndAgeMinLessThanEqualAndAgeMaxGreaterThanEqualAndActiveTrue(String domainName, int granularity, String typeName, int ageMin, int ageMax);
  List<ThresholdAgeGroup> findByThresholdRevisionDomainNameAndThresholdRevisionGranularityAndThresholdRevisionTypeNameAndActiveTrue(String domainName, int granularity, String typeName);
}