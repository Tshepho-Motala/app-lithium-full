package lithium.service.user.provider.threshold.controller;

import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.provider.threshold.data.dto.ThesholdRevisonResultDto;
import lithium.service.user.provider.threshold.data.dto.ThresholdRevisionDeactivateDto;
import lithium.service.user.provider.threshold.data.dto.ThresholdRevisionDto;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.services.ThresholdRevisionService;
import lithium.service.user.provider.threshold.services.ThresholdService;
import lithium.service.user.provider.threshold.services.TypeService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/threshold-revision")
public class ThresholdRevisionController {

  @Autowired
  private ThresholdRevisionService thresholdRevisionService;
  @Autowired
  private ThresholdService thresholdService;
  @Autowired TypeService typeService;

  @PostMapping("/save")
  public Response<ThresholdRevision> saveThresholdRevision(@RequestBody ThresholdRevisionDto dto,LithiumTokenUtil lithiumTokenUtil) throws Exception {
    log.info("Threshold Revision Request Received {}", dto);
    ThresholdRevision thresholdRevision = thresholdRevisionService.saveThresholdRevision(dto,lithiumTokenUtil);
    return Response.<ThresholdRevision>builder().data(thresholdRevision).status(Status.OK).build();
  }


  @GetMapping("/domain/{domainName}/granularity/{granularity}")
  public Response<ThesholdRevisonResultDto> findByDomainAndGranularity(@PathVariable("domainName") String domainName,
      @PathVariable("granularity") int granularity) {
    Optional<ThresholdRevision> thresholdRevision = thresholdRevisionService.findByDomainAndGranularity(
        domainName, granularity);
    ThesholdRevisonResultDto thesholdRevisonResultDto= new ThesholdRevisonResultDto();
    if (thresholdRevision.isPresent()) {
      thesholdRevisonResultDto.setPercentage(thresholdRevision.get().getPercentage());
    }
    return Response.<ThesholdRevisonResultDto>builder().data(thesholdRevisonResultDto).status(Status.OK).build();
  }

  @GetMapping("/age-based/domain/{domain-name}/granularity/{granularity}/min-age/{min-age}/max-age/{max-age}")
  public Response<ThesholdRevisonResultDto> findAgeBaseThresholdRevisionByDomainAndGranularity(@PathVariable("domain-name") String domainName,
      @PathVariable("granularity") int granularity,@PathVariable("min-age") int minAge,@PathVariable("max-age") int maxAge ) {
    Optional<ThresholdRevision> thresholdRevision = thresholdRevisionService.findAgeBasedRevisionByDomainAndGranularity(
        domainName, granularity,minAge,maxAge);
    ThesholdRevisonResultDto thesholdRevisonResultDto= new ThesholdRevisonResultDto();
    if (thresholdRevision.isPresent()) {
      thesholdRevisonResultDto.setPercentage(thresholdRevision.get().getPercentage());
    }
    return Response.<ThesholdRevisonResultDto>builder().data(thesholdRevisonResultDto).status(Status.OK).build();
  }

  @PostMapping("/remove-threshold-revision")
  public Response<ThresholdRevision> removeThresholdRevision(@RequestBody ThresholdRevisionDeactivateDto dto, LithiumTokenUtil tokenUtil) throws Exception {
    thresholdRevisionService.deleteThresholdRevision(dto.getDomain(), dto.getGranularity(), lithium.service.user.provider.threshold.data.enums.Type.LIMIT_TYPE_LOSS, tokenUtil);
    return Response.<ThresholdRevision>builder().status(Status.OK).build();
    }
}
