package lithium.service.user.provider.threshold.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.user.provider.threshold.config.Properties;
import lithium.service.user.provider.threshold.data.dto.ThresholdRevisionDto;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.service.user.provider.threshold.data.repositories.ThresholdRevisionRepository;
import lithium.service.user.provider.threshold.services.DomainService;
import lithium.service.user.provider.threshold.services.LimitService;
import lithium.service.user.provider.threshold.services.NotificationService;
import lithium.service.user.provider.threshold.services.ThresholdAgeGroupService;
import lithium.service.user.provider.threshold.services.ThresholdRevisionService;
import lithium.service.user.provider.threshold.services.ThresholdService;
import lithium.service.user.provider.threshold.services.TypeService;
import lithium.service.user.provider.threshold.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class ThresholdRevisionServiceImpl implements ThresholdRevisionService {

  @Autowired
  private ThresholdRevisionRepository thresholdRevisionRepository;
  @Autowired
  private LimitService limitService;
  @Autowired
  private DomainService domainService;
  @Autowired
  private TypeService typeService;
  @Autowired
  private ThresholdService thresholdService;
  @Autowired
  private UserService userService;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private Properties properties;
  @Autowired
  private ThresholdAgeGroupService thresholdAgeGroupService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;

  @Override
  public ThresholdRevision save(ThresholdRevision thresholdRevision) {
    return thresholdRevisionRepository.save(thresholdRevision);
  }

  @Override
  public Iterable<ThresholdRevision> findAll() {
    return thresholdRevisionRepository.findAll();
  }

  @Override
  public Optional<ThresholdRevision> findOne(Long id) {
    return thresholdRevisionRepository.findById(id);
  }

  @Override
  public boolean playerReachedThresholdLimit(ThresholdRevision thresholdRevision, CompleteSummaryAccountTransactionTypeDetail
      completeSummaryAccountTransactionTypeDetail, User user) {
    BigDecimal limitAmount = limitService.getLimitAmount(thresholdRevision, user);
    BigDecimal netLossToHouse = CurrencyAmount.fromCents(completeSummaryAccountTransactionTypeDetail.getNetLossToHouse()).toAmount();
    return (netLossToHouse.compareTo(limitAmount) >= 0);
  }

  @Override
  @Transactional
  public void deleteThresholdRevision(String domainName, int granularity, Type type, LithiumTokenUtil tokenUtil) throws Exception {
    Optional<Threshold> currentThreshold = thresholdService.findCurrentDomainThreshold(granularity, domainName, type);

    if (currentThreshold.isPresent()) {
      lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Threshold threshold = currentThreshold.get();
      threshold.setActive(false);
      ThresholdRevision thresholdRevision = threshold.getCurrent();
      thresholdRevision.setModifiedBy(userService.findOrCreate(tokenUtil.guid()));
      thresholdRevision.setModifiedDate(new Date());
      thresholdRevisionRepository.save(thresholdRevision);
      thresholdService.save(threshold);
      List<ChangeLogFieldChange> clfc = changeLogService.compare(new ThresholdRevision(), thresholdRevision, new String[]{"granularity","percentage"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("threshold.thresholdrevision", "delete", domain.getId(), tokenUtil.guid(),
      tokenUtil, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domainName);
    }
  }

  @Override
  @Transactional
  public ThresholdRevision saveThresholdRevision(ThresholdRevisionDto thresholdRevisionDto, LithiumTokenUtil tokenUtil) throws Exception {
    String authorGuid = tokenUtil.guid();
    Optional<Threshold> currentThreshold = thresholdService.findCurrentDomainThreshold(thresholdRevisionDto.getGranularity(),
        thresholdRevisionDto.getDomain(), lithium.service.user.provider.threshold.data.enums.Type.fromId(thresholdRevisionDto.getType()));

    Domain domain = domainService.findOrCreate(thresholdRevisionDto.getDomain());
    ThresholdRevision thresholdRevision = ThresholdRevision.builder()
        .amount(thresholdRevisionDto.getAmount().doubleValue() != 0 ? thresholdRevisionDto.getAmount().divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
        .domain(domain)
        .granularity(thresholdRevisionDto.getGranularity())
        .percentage(thresholdRevisionDto.getPercentage())
        .type(typeService.findOrCreate(Type.fromId(thresholdRevisionDto.getType())))
        .createdBy(userService.findOrCreate(authorGuid))
        .createdDate(new Date())
        .build();

    Threshold threshold;
    lithium.service.domain.client.objects.Domain domainCache = cachingDomainClientService.retrieveDomainFromDomainService(thresholdRevisionDto.getDomain());

    if (currentThreshold.isEmpty()) {
      threshold = thresholdService.save(Threshold.builder().active(true).build());
      List<ChangeLogFieldChange> clfc = changeLogService.compare(thresholdRevision, new ThresholdRevision(), new String[]{"granularity","percentage"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("threshold.thresholdrevision", "create", domainCache.getId(), tokenUtil.guid(),
      tokenUtil, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domain.getName());
    }
    else {
      threshold = currentThreshold.get();
      threshold.setActive(true);
      ThresholdRevision previousThresholdRevision = threshold.getCurrent();
      previousThresholdRevision.setModifiedBy(userService.findOrCreate(authorGuid));
      previousThresholdRevision.setModifiedDate(new Date());
      thresholdRevisionRepository.save(previousThresholdRevision);
      List<ChangeLogFieldChange> clfc = changeLogService.compare(thresholdRevision, previousThresholdRevision , new String[]{"granularity","percentage"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("threshold.thresholdrevision", "edit", domainCache.getId(), tokenUtil.guid(),
      tokenUtil, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domain.getName());
    }

    thresholdRevision = thresholdRevisionRepository.save(thresholdRevision);

    threshold.setCurrent(thresholdRevision);
    threshold = thresholdService.save(threshold);

    if (properties.enableAutoCreate()) {
      // Registers a new notification to be used for notification messaging on threshold create & update
      notificationService.createOrUpdateThresoldNotification(threshold);
    }
    return threshold.getCurrent();
  }


  @Override
  @Transactional
  public ThresholdRevision saveAgeBasedThresholdRevision(ThresholdRevisionDto thresholdRevisionDto,int minAge, int maxAge ,LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException
  {
    String authorGuid = lithiumTokenUtil.guid();
    Optional<Threshold> currentThreshold = thresholdService.findCurrentAgeBasedThresholdBetween(thresholdRevisionDto.getDomain(), thresholdRevisionDto.getGranularity(),
        Type.fromId(thresholdRevisionDto.getType()).typeName(), minAge, maxAge);
    Domain domain = domainService.findOrCreate(thresholdRevisionDto.getDomain());
    ThresholdRevision thresholdRevision = ThresholdRevision.builder()
        .amount(thresholdRevisionDto.getAmount().doubleValue() != 0 ? thresholdRevisionDto.getAmount().divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
        .domain(domain)
        .granularity(thresholdRevisionDto.getGranularity())
        .percentage(thresholdRevisionDto.getPercentage())
        .type(typeService.findOrCreate(Type.fromId(thresholdRevisionDto.getType())))
        .createdBy(userService.findOrCreate(authorGuid))
        .createdDate(new Date())
        .build();

    Threshold threshold;
    if (currentThreshold.isEmpty()) {
      threshold = thresholdService.save(Threshold.builder().active(true).build());
    } else {
      threshold = currentThreshold.get();
      threshold.setActive(true);
      ThresholdRevision previousThresholdRevision = threshold.getCurrent();
      previousThresholdRevision.setModifiedBy(userService.findOrCreate(authorGuid));
      previousThresholdRevision.setModifiedDate(new Date());
      thresholdRevisionRepository.save(previousThresholdRevision);
    }

    thresholdRevision = thresholdRevisionRepository.save(thresholdRevision);

    threshold.setCurrent(thresholdRevision);
    threshold = thresholdService.save(threshold);

    if (properties.enableAutoCreate()) {
      // Registers a new notification to be used for notification messaging on threshold create & update
      notificationService.createOrUpdateThresoldNotification(threshold);
    }
    return threshold.getCurrent();
  }

  @Override
  public Optional<ThresholdRevision> findByDomainAndGranularity(String domainName, int granularity) {
    Optional<Threshold> threshold = thresholdService.findCurrentDomainThreshold(granularity,
        domainName, lithium.service.user.provider.threshold.data.enums.Type.LIMIT_TYPE_LOSS);
    return getThresholdRevision(threshold);
  }
  @Override
  public Optional<ThresholdRevision> findAgeBasedRevisionByDomainAndGranularity(String domainName,
      int granularity,int minAge, int maxAge) {
    Optional<Threshold> threshold = thresholdService.findCurrentAgeBasedThresholdBetween(domainName, granularity, Type.LIMIT_TYPE_LOSS.typeName(), minAge, maxAge);
    return getThresholdRevision(threshold);
  }

  private Optional<ThresholdRevision> getThresholdRevision(Optional<Threshold> threshold) {
    if (threshold.isPresent() && threshold.get().isActive()) {
      ThresholdRevision revision = threshold.get().getCurrent();
      Optional<ThresholdAgeGroup> byThresholdRevision = thresholdAgeGroupService.findByThresholdRevision(
          revision);
      if (byThresholdRevision.isPresent()) {
        ThresholdAgeGroup thresholdAgeGroup = byThresholdRevision.get();
        if (thresholdAgeGroup.isActive()) {
          return Optional.of(revision);
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.of(revision);
      }

    }
    return Optional.empty();
  }
}

