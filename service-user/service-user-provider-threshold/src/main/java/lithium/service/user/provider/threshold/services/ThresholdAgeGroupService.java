package lithium.service.user.provider.threshold.services;

import java.util.List;
import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.provider.threshold.data.dto.AgeRangeDto;
import lithium.service.user.provider.threshold.data.dto.DomainAgeLimitDto;
import lithium.service.user.provider.threshold.data.dto.ThreshholdAgeGroupDto;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.tokens.LithiumTokenUtil;


public interface ThresholdAgeGroupService extends AbstractService<ThresholdAgeGroup> {

  List<ThresholdAgeGroup>  saveList(List<DomainAgeLimitDto> domainAgeLimitDtoList, LithiumTokenUtil tokenUtil)
  throws Status500InternalServerErrorException;
  List<ThresholdAgeGroup> findByDomainMaxAndMinAge(Domain domain,int max, int min);
  Optional<ThresholdAgeGroup> updateThresholdAgeGroup(ThreshholdAgeGroupDto thresholdAgeGroupDto,LithiumTokenUtil tokenUtil)
  throws Status500InternalServerErrorException;
  List<ThresholdAgeGroup>  updateMinAndMaxAge(AgeRangeDto ageRangeDto,LithiumTokenUtil tokenUtil);
  List<ThresholdAgeGroup>  deactivateThresholdAgeGroup(ThreshholdAgeGroupDto thresholdAgeGroupDto);
  Optional<ThresholdAgeGroup> findByThresholdRevision(ThresholdRevision thresholdRevision);
  Optional<ThresholdAgeGroup> deactivateThresholdRevision(ThreshholdAgeGroupDto threshholdAgeGroupDto);


}
