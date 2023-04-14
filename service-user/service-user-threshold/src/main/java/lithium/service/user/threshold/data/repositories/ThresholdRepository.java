package lithium.service.user.threshold.data.repositories;

import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.data.entities.Threshold;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThresholdRepository extends PagingAndSortingRepository<Threshold, Long>, JpaSpecificationExecutor<Threshold> {

  Threshold findByDomainNameAndTypeNameAndGranularityAndAgeMinAndAgeMaxAndActiveTrue(String domainName, String typeName, Granularity granularity,
      Integer ageMin, Integer ageMax);

  Threshold findByDomainNameAndTypeNameAndGranularityAndAgeMinAndAgeMax(String domainName, String typeName, Granularity granularity, Integer ageMin,
      Integer ageMax);
}
