package lithium.service.user.provider.threshold.controller;

import java.util.List;
import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.provider.threshold.data.dto.AgeRangeDto;
import lithium.service.user.provider.threshold.data.dto.DomainAgeLimitDto;
import lithium.service.user.provider.threshold.data.dto.ThreshholdAgeGroupDto;
import lithium.service.user.provider.threshold.data.entities.ThresholdAgeGroup;
import lithium.service.user.provider.threshold.services.DomainService;
import lithium.service.user.provider.threshold.services.ThresholdAgeGroupService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/threshold-age-group")
public class ThresholdAgeGroupController {

  @Autowired
  private ThresholdAgeGroupService thresholdAgeGroupService;
  @Autowired
  private DomainService domainService;

  @PostMapping("/set-age-limit-group")
  public Response<List<ThresholdAgeGroup>> setDomainAgeLimitThreshold(@RequestBody List<DomainAgeLimitDto> domainAgeLimitDtoList, LithiumTokenUtil tokenUtil) {
        try {
          List<ThresholdAgeGroup> thresholdAgeGroups = thresholdAgeGroupService.saveList(domainAgeLimitDtoList, tokenUtil);
          return Response.<List<ThresholdAgeGroup>>builder().status(Status.OK_SUCCESS).data(thresholdAgeGroups).build();
        } catch (Exception e) {
          return Response.<List<ThresholdAgeGroup>>builder().message(e.getLocalizedMessage()).status(Status.INTERNAL_SERVER_ERROR).build();
        }
  }

  @GetMapping("/find-by-age-group/p")
  public Response<List<ThresholdAgeGroup>> findByThresholdAgeGroup(@RequestParam String domainName, @RequestParam int maxAge, @RequestParam int minAge) {
    List<ThresholdAgeGroup> list = thresholdAgeGroupService.findByDomainMaxAndMinAge(domainService.findOrCreate(domainName), maxAge, minAge);
    return Response.<List<ThresholdAgeGroup>>builder().status(Status.OK_SUCCESS).data(list).build();
  }


  @PostMapping("/edit-age-limit-group")
  public Response<ThresholdAgeGroup> updateThresholdAgeGroup(@RequestBody ThreshholdAgeGroupDto thresholdAgeGroupDto,LithiumTokenUtil tokenUtil)
  throws Status500InternalServerErrorException
  {
    Optional<ThresholdAgeGroup>  optionalThresholdAgeGroup = thresholdAgeGroupService.updateThresholdAgeGroup(thresholdAgeGroupDto,tokenUtil);
    return Response.<ThresholdAgeGroup>builder().status(Status.OK_SUCCESS).data(optionalThresholdAgeGroup.orElse(null)).build();
  }

  @PostMapping("/edit-age-limit-min-max")
  public Response<List<ThresholdAgeGroup>> updateAgeRanges(@RequestBody AgeRangeDto ageRangeDto,LithiumTokenUtil tokenUtil) {
    List<ThresholdAgeGroup> list = thresholdAgeGroupService.updateMinAndMaxAge(ageRangeDto,tokenUtil);
    return Response.<List<ThresholdAgeGroup>>builder().status(Status.OK_SUCCESS).data(list).build();
  }


  @PostMapping("/deactivate-age-limit-group")
  public Response<List<ThresholdAgeGroup>> deactivateThresholdAgeGroup(@RequestBody ThreshholdAgeGroupDto threshholdAgeGroupDto) {
    List<ThresholdAgeGroup> list = thresholdAgeGroupService.deactivateThresholdAgeGroup(
        threshholdAgeGroupDto);
    return Response.<List<ThresholdAgeGroup>>builder().status(Status.OK_SUCCESS).data(list).build();
  }
  @PostMapping("/deactivate-single-revision")
  public Response<ThresholdAgeGroup> deactivateSingleRevision(@RequestBody ThreshholdAgeGroupDto threshholdAgeGroupDto) {
    Optional<ThresholdAgeGroup> optionalThresholdAgeGroup = thresholdAgeGroupService.deactivateThresholdRevision(
        threshholdAgeGroupDto);
    return Response.<ThresholdAgeGroup>builder().status(Status.OK_SUCCESS).data(optionalThresholdAgeGroup.orElse(null)).build();
  }

}
