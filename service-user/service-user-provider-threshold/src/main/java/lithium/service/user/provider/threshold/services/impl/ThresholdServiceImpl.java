package lithium.service.user.provider.threshold.services.impl;

import java.util.List;
import java.util.Optional;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.data.repositories.ThresholdAgeGroupRepository;
import lithium.service.user.provider.threshold.data.repositories.ThresholdRepository;
import lithium.service.user.provider.threshold.services.ThresholdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThresholdServiceImpl implements ThresholdService {

  @Autowired
  private ThresholdRepository thresholdRepository;
  @Autowired
  private ThresholdAgeGroupRepository thresholdAgeGroupRepository;

  @Override
  public Threshold save(Threshold threshold) {
    return thresholdRepository.save(threshold);
  }

  @Override
  public Iterable<Threshold> findAll() {
    return thresholdRepository.findAll();
  }

  @Override
  public Optional<Threshold> findOne(Long id) {
    return thresholdRepository.findById(id);
  }

  @Override
  public Optional<Threshold> findCurrentDomainThreshold(int granularity, String domainName, lithium.service.user.provider.threshold.data.enums.Type type) {
    List<Threshold> thresholds = thresholdRepository.findByCurrentDomainNameAndCurrentGranularityAndCurrentTypeNameAndActiveTrue(domainName, granularity, type.typeName());
    if (thresholds.isEmpty()) return Optional.empty();
    //TODO: The native queries that was in use needs to be converted to Specifications to avoid the extra query performed below
    List<ThresholdAgeGroup> ageGroups = thresholdAgeGroupRepository.findByThresholdRevisionDomainNameAndThresholdRevisionGranularityAndThresholdRevisionTypeNameAndActiveTrue(domainName, granularity, type.typeName());
    if (thresholds.size() == ageGroups.size()) return Optional.empty();
    return thresholds.stream().filter(t ->
        ageGroups.stream().noneMatch(ag ->
            ag.getThresholdRevision().getId() == t.getCurrent().getId()
        )
    ).findFirst();
  }

  @Override
  public Optional<Threshold> findCurrentAgeBasedThreshold(String domainName, int granularity, String typeName, int age) {
    Optional<ThresholdAgeGroup> ageGroup = thresholdAgeGroupRepository.findByThresholdRevisionDomainNameAndThresholdRevisionGranularityAndThresholdRevisionTypeNameAndAgeMinLessThanEqualAndAgeMaxGreaterThanEqualAndActiveTrue(domainName, granularity, typeName, age, age);
    if (ageGroup.isEmpty()) {
      return Optional.empty();
    } else {
      return thresholdRepository.findByCurrentId(ageGroup.get().getThresholdRevision().getId());
    }
  }

  @Override
  public Optional<Threshold> findCurrentAgeBasedThresholdBetween(String domainName, int granularity, String typeName, int minAge, int maxAge) {
    Optional<ThresholdAgeGroup> ageGroup = thresholdAgeGroupRepository.findByThresholdRevisionDomainNameAndThresholdRevisionGranularityAndThresholdRevisionTypeNameAndAgeMinLessThanEqualAndAgeMaxGreaterThanEqualAndActiveTrue(domainName, granularity, typeName, minAge, maxAge);
    if (ageGroup.isEmpty()) {
      return Optional.empty();
    } else {
      return thresholdRepository.findByCurrentId(ageGroup.get().getThresholdRevision().getId());
    }
  }
}
