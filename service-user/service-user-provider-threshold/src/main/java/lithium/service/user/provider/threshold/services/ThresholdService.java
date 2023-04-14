package lithium.service.user.provider.threshold.services;

import java.util.Optional;
import lithium.service.user.provider.threshold.data.entities.Threshold;


public interface ThresholdService extends AbstractService<Threshold> {

  Optional<Threshold> findCurrentDomainThreshold(int granularity, String domainName, lithium.service.user.provider.threshold.data.enums.Type type);
  Optional<Threshold> findCurrentAgeBasedThreshold(String domainName, int granularity, String typeName, int age);
  Optional<Threshold> findCurrentAgeBasedThresholdBetween(String domainName, int granularity, String typeName, int minAge, int maxAge);

}
