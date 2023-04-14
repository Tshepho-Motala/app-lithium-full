package lithium.service.user.threshold.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.objects.Granularity;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.service.user.threshold.data.entities.Type;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.data.repositories.ThresholdRepository;
import lithium.service.user.threshold.data.repositories.ThresholdRevisionRepository;
import lithium.service.user.threshold.data.specifications.ThresholdSpecification;
import lithium.service.user.threshold.service.DomainService;
import lithium.service.user.threshold.service.NotificationService;
import lithium.service.user.threshold.service.ThresholdService;
import lithium.service.user.threshold.service.TypeService;
import lithium.service.user.threshold.service.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class ThresholdServiceImpl implements ThresholdService {

  @Autowired
  ThresholdRepository thresholdRepository;
  @Autowired
  ThresholdRevisionRepository thresholdRevisionRepository;
  @Autowired
  DomainService domainService;
  @Autowired
  TypeService typeService;
  @Autowired
  UserService userService;
  @Autowired
  private NotificationService notificationService;
  @Override
  public Threshold findByAgeOrDefault(String domainName, EType eType, Integer granularity, Integer age) {
    Optional<Threshold> one = thresholdRepository.findOne(ThresholdSpecification.findByAge(domainName, eType, granularity, age));
    if (one.isPresent()) {
      return one.get();
    }
    one = thresholdRepository.findOne(ThresholdSpecification.findDefault(domainName, eType, granularity));
    return (one.isPresent()) ? one.get() : null;
  }

  @Override
  public Threshold find(String domainName, EType eType, Integer granularity, Integer ageMin, Integer ageMax, LithiumTokenUtil lithiumTokenUtil) {
    Threshold threshold = thresholdRepository.findByDomainNameAndTypeNameAndGranularityAndAgeMinAndAgeMaxAndActiveTrue(domainName, eType.name(),
        Granularity.fromGranularity(granularity), (ageMin == null ? -1 : ageMin), (ageMax == null ? -1 : ageMax));

    log.debug("Retrieving Threshold for {}, {}, {}, {}, {}, result: {}", domainName, eType.name(), granularity, ageMin, ageMax, threshold);
    return threshold;
  }

  @Override
  public Threshold save(String domainName, Threshold threshold, BigDecimal percentage, BigDecimal amount, EType eType, Integer granularity,
      Integer ageMin, Integer ageMax, LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException
  {
    Domain domain = domainService.findOrCreate(domainName);
    User user = userService.findOrCreate(lithiumTokenUtil.guid(), domain);
    Type type = typeService.findOrCreate(eType);

    if (ObjectUtils.isEmpty(threshold)) {  // Null if id not found, or id not provided
      threshold = thresholdRepository.findByDomainNameAndTypeNameAndGranularityAndAgeMinAndAgeMax(domainName, type.getName(),
          Granularity.fromGranularity(granularity), (ageMin != null) ? ageMin : -1, (ageMax != null) ? ageMax : -1);
      if (ObjectUtils.isEmpty(threshold)) { // Null if threshold does not exist for the provided parameters.
        threshold = thresholdRepository.save(Threshold.builder()
            .active(true)
            .domain(domain)
            .type(type)
            .ageMin(ageMin)
            .ageMax(ageMax)
            .granularity(Granularity.fromGranularity(granularity))
            .build());
      } else {
        threshold.setActive(true);
        threshold.setAgeMin(ageMin);
        threshold.setAgeMax(ageMax);
        threshold = thresholdRepository.save(threshold);

      }
    } else {
      if (ageMin != null) {
        threshold.setAgeMin(ageMin);
      }
      if (ageMax != null) {
        threshold.setAgeMax(ageMax);
      }
      threshold.setActive(true);
      threshold = thresholdRepository.save(threshold);
    }
    ThresholdRevision thresholdRevision = saveRevision(threshold, percentage, amount, user);
    threshold.setCurrent(thresholdRevision);
    notificationService.createOrUpdateThresholdNotification(threshold);
    return thresholdRepository.save(threshold);
  }

  @Override
  public Threshold disable(String domainName, Threshold threshold, EType type, LithiumTokenUtil lithiumTokenUtil) {
    threshold.setActive(false);
    threshold = thresholdRepository.save(threshold);
    return threshold;
  }

  private ThresholdRevision saveRevision(Threshold threshold, BigDecimal percentage, BigDecimal amount, User user) {
    return thresholdRevisionRepository.save(
        ThresholdRevision.builder().percentage(percentage).amount(amount).threshold(threshold).createdDate(new Date()).createdBy(user).build());
  }
}
