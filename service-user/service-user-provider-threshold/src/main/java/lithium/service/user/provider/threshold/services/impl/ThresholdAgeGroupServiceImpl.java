package lithium.service.user.provider.threshold.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.provider.threshold.config.Properties;
import lithium.service.user.provider.threshold.data.dto.AgeRangeDto;
import lithium.service.user.provider.threshold.data.dto.DomainAgeLimitDto;
import lithium.service.user.provider.threshold.data.dto.ThreshholdAgeGroupDto;
import lithium.service.user.provider.threshold.data.dto.ThresholdRevisionDto;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.repositories.ThresholdAgeGroupRepository;
import lithium.service.user.provider.threshold.services.DomainService;
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
public class ThresholdAgeGroupServiceImpl implements ThresholdAgeGroupService {

  @Autowired
  private ThresholdAgeGroupRepository thresholdAgeGroupRepository;
  @Autowired
  private ThresholdRevisionService thresholdRevisionService;
  @Autowired
  private DomainService domainService ;
  @Autowired
  private ThresholdService thresholdService;
  @Autowired
  private TypeService typeService;
  @Autowired
  private UserService userService;
  @Autowired
  private Properties properties;
  @Autowired
  private NotificationService notificationService;


  @Override
  @Transactional
  public ThresholdAgeGroup save(ThresholdAgeGroup thresholdAgeGroup) {
    return thresholdAgeGroupRepository.save(thresholdAgeGroup);
  }

  @Override
  public Iterable<ThresholdAgeGroup> findAll() {
    return thresholdAgeGroupRepository.findAll();
  }

  @Override
  public Optional<ThresholdAgeGroup> findOne(Long id) {
    return thresholdAgeGroupRepository.findById(id);
  }

  @Override
  @Transactional
  public List<ThresholdAgeGroup> saveList(List<DomainAgeLimitDto> domainAgeLimitDtoList, LithiumTokenUtil tokenUtil)
  throws Status500InternalServerErrorException
  {
    List<ThresholdAgeGroup> list = new ArrayList<>();
    if (!domainAgeLimitDtoList.isEmpty()) {
      String authorGuid = tokenUtil.guid();
      User user = userService.findOrCreate(authorGuid);
      for (DomainAgeLimitDto domainAgeLimitDto : domainAgeLimitDtoList) {
        Domain domain = domainService.findOrCreate(domainAgeLimitDto.getDomainName());

        ThresholdRevisionDto thresholdRevisionDto = ThresholdRevisionDto.builder()
            .amount(BigDecimal.valueOf(domainAgeLimitDto.getAmount()))
            .domain(domainAgeLimitDto.getDomainName())
            .granularity(domainAgeLimitDto.getGranularity())
            .percentage(domainAgeLimitDto.getWarningThreshold())
            .type(domainAgeLimitDto.getType())
            .build();

        ThresholdRevision thresholdRevision = ThresholdRevision.builder()
            .amount(thresholdRevisionDto.getAmount().doubleValue() != 0 ? thresholdRevisionDto.getAmount().divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
            .domain(domain)
            .granularity(thresholdRevisionDto.getGranularity())
            .percentage(thresholdRevisionDto.getPercentage())
            .type(typeService.findOrCreate(lithium.service.user.provider.threshold.data.enums.Type.fromId(thresholdRevisionDto.getType())))
            .createdBy(user)
            .createdDate(new Date())
            .build();
        Threshold threshold = thresholdService.save(Threshold.builder().active(true).build());
          threshold.setActive(true);
        thresholdRevision = thresholdRevisionService.save(thresholdRevision);

        threshold.setCurrent(thresholdRevision);
        threshold = thresholdService.save(threshold);

        if (properties.enableAutoCreate()) {
          // Registers a new notification to be used for notification messaging on threshold create & update
          notificationService.createOrUpdateThresoldNotification(threshold);
        }
        ThresholdAgeGroup thresholdAgeGroup = new ThresholdAgeGroup();
        thresholdAgeGroup.setAgeMax(domainAgeLimitDto.getAgeMax());
        thresholdAgeGroup.setAgeMin(domainAgeLimitDto.getAgeMin());
        thresholdAgeGroup.setActive(true);
        thresholdAgeGroup.setThresholdRevision(thresholdRevision);
        thresholdAgeGroupRepository.save(thresholdAgeGroup);
        list.add(thresholdAgeGroup);
      }
    }
    return list;
  }

  @Override
  public List<ThresholdAgeGroup> findByDomainMaxAndMinAge(Domain domain, int max, int min) {
    //TODO: make domain part of query!?
    return thresholdAgeGroupRepository.findByAgeMaxAndAgeMin(max, min)
        .stream().filter(thresholdAgeGroup -> thresholdAgeGroup.getThresholdRevision().getDomain().getName().equalsIgnoreCase(domain.getName())).toList();
  }

  @Override
  public Optional<ThresholdAgeGroup> updateThresholdAgeGroup(ThreshholdAgeGroupDto threshholdAgeGroupDto,LithiumTokenUtil tokenUtil)
  throws Status500InternalServerErrorException
  {
    int max=threshholdAgeGroupDto.getAgeMax();
    int min=threshholdAgeGroupDto.getAgeMin();
    int granularity=threshholdAgeGroupDto.getGranularity();

    lithium.service.user.provider.threshold.data.enums.Type type = lithium.service.user.provider.threshold.data.enums.Type.fromId(
        threshholdAgeGroupDto.getType());
    String domainName=threshholdAgeGroupDto.getDomainName();

    Optional<Threshold> currentAgeBasedThreshold = thresholdService.findCurrentAgeBasedThresholdBetween(
        domainName, granularity, type.typeName(), min, max);
    if(currentAgeBasedThreshold.isPresent()){
    Threshold threshold = currentAgeBasedThreshold.get();
    ThresholdRevision previousRevision=threshold.getCurrent();
    ThresholdRevision thresholdRevision = threshold.getCurrent();
      if (thresholdRevision != null && thresholdRevision.getGranularity() == threshholdAgeGroupDto.getGranularity()) {
        ThresholdRevisionDto thresholdRevisionDto = ThresholdRevisionDto.builder()
            .amount(threshholdAgeGroupDto.getAmount())
            .domain(thresholdRevision.getDomain().getName())
            .granularity(thresholdRevision.getGranularity())
            .percentage(threshholdAgeGroupDto.getThresholdPercentage())
            .type(threshholdAgeGroupDto.getType()).build();
     ThresholdRevision revision= thresholdRevisionService.saveAgeBasedThresholdRevision(thresholdRevisionDto,threshholdAgeGroupDto.getAgeMin(),threshholdAgeGroupDto.getAgeMax(), tokenUtil);

        Optional<ThresholdAgeGroup> ageGroup = findByThresholdRevision(previousRevision);
        if(ageGroup.isPresent()){
          ThresholdAgeGroup thresholdAgeGroup = ageGroup.get();
          thresholdAgeGroup.setThresholdRevision(revision);
          save(thresholdAgeGroup);
        }

        return ageGroup;
      }
    }else{
      DomainAgeLimitDto domainAgeLimitDto= new DomainAgeLimitDto();
      domainAgeLimitDto.setAgeMax(threshholdAgeGroupDto.getAgeMax());
      domainAgeLimitDto.setAgeMin(threshholdAgeGroupDto.getAgeMin());
      domainAgeLimitDto.setDomainName(threshholdAgeGroupDto.getDomainName());
      domainAgeLimitDto.setAmount(threshholdAgeGroupDto.getAmount().longValue());
      domainAgeLimitDto.setGranularity(threshholdAgeGroupDto.getGranularity());
      domainAgeLimitDto.setWarningThreshold(threshholdAgeGroupDto.getThresholdPercentage());
      domainAgeLimitDto.setType(threshholdAgeGroupDto.getType());
      List<DomainAgeLimitDto> list= new ArrayList<>();
      list.add(domainAgeLimitDto);
    return saveList(list,tokenUtil).stream().findFirst();
    }
    return Optional.empty();
  }

  @Override
  public List<ThresholdAgeGroup> deactivateThresholdAgeGroup(ThreshholdAgeGroupDto thresholdAgeGroupDto) {
    Domain domain=domainService.findOrCreate(thresholdAgeGroupDto.getDomainName());
    int max=thresholdAgeGroupDto.getAgeMax();
    int min=thresholdAgeGroupDto.getAgeMin();
    String domainName=thresholdAgeGroupDto.getDomainName();
    lithium.service.user.provider.threshold.data.enums.Type type = lithium.service.user.provider.threshold.data.enums.Type.fromId(
        thresholdAgeGroupDto.getType());
    List<ThresholdAgeGroup> list = findByDomainMaxAndMinAge(domain,max,min);
    list.forEach(thresholdAgeGroup -> {
      thresholdAgeGroup.setActive(false);
      ThresholdRevision thresholdRevision = thresholdAgeGroup.getThresholdRevision();
      int granularity=thresholdRevision.getGranularity();
      Optional<Threshold> currentAgeBasedThreshold = thresholdService.findCurrentAgeBasedThresholdBetween(
          domainName, granularity, type.typeName(), min, max);
      if (currentAgeBasedThreshold.isPresent()){
        Threshold threshold = currentAgeBasedThreshold.get();
        threshold.setEdit(threshold.getCurrent());
        threshold.setCurrent(null);
        thresholdService.save(threshold);
      }
      save(thresholdAgeGroup);
    });
    return list;
  }

  @Override
  public Optional<ThresholdAgeGroup> findByThresholdRevision(ThresholdRevision thresholdRevision) {
    return thresholdAgeGroupRepository.findByThresholdRevision(thresholdRevision);
  }

  @Override
  public Optional<ThresholdAgeGroup> deactivateThresholdRevision(ThreshholdAgeGroupDto thresholdAgeGroupDto) {
    int max=thresholdAgeGroupDto.getAgeMax();
    int min=thresholdAgeGroupDto.getAgeMin();
    int granularity=thresholdAgeGroupDto.getGranularity();
    lithium.service.user.provider.threshold.data.enums.Type type = lithium.service.user.provider.threshold.data.enums.Type.fromId(
        thresholdAgeGroupDto.getType());
    String domainName=thresholdAgeGroupDto.getDomainName();
    Optional<Threshold> currentAgeBasedThreshold = thresholdService.findCurrentAgeBasedThresholdBetween(
        domainName, granularity, type.typeName(), min, max);
    if(currentAgeBasedThreshold.isPresent()){
      Threshold threshold = currentAgeBasedThreshold.get();
      Optional<ThresholdAgeGroup> ageGroup = findByThresholdRevision(threshold.getCurrent());
      threshold.setEdit(threshold.getCurrent());
      threshold.setCurrent(null);
      thresholdService.save(threshold);
      return ageGroup;
    }
    return Optional.empty();
  }

  @Override
  public List<ThresholdAgeGroup> updateMinAndMaxAge(AgeRangeDto ageRangeDto, LithiumTokenUtil tokenUtil) {

    List<ThresholdAgeGroup> ageMaxAndAgeMin = thresholdAgeGroupRepository.findByAgeMaxAndAgeMin(
        ageRangeDto.getPreviousAgeMax(), ageRangeDto.getNextAgeMin());
     ageMaxAndAgeMin.forEach(item->{
       item.setAgeMax(ageRangeDto.getNextAgeMax());
       item.setAgeMin(ageRangeDto.getNextAgeMin());
       save(item);
     });
    return ageMaxAndAgeMin;
  }
}
