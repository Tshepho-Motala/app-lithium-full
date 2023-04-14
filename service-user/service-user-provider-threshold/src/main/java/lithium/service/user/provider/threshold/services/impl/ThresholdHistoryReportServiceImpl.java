package lithium.service.user.provider.threshold.services.impl;

import java.util.Date;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.specification.PlayerThresholdHistorySpecification;
import lithium.service.user.provider.threshold.services.PlayerThresholdHistoryService;
import lithium.service.user.provider.threshold.services.ThresholdHistoryReportService;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThresholdHistoryReportServiceImpl implements ThresholdHistoryReportService {
  @Autowired
  PlayerThresholdHistoryService playerThresholdHistoryService;

  @Override
  public Page<PlayerThresholdHistory> getPlayerThresholdHistoryPage(ThresholdsFilterRequest transactionFilterPageRequest, PageRequest pageRequest) {
    Specification<PlayerThresholdHistory> spec = Specification.where(
        PlayerThresholdHistorySpecification.domainIn(transactionFilterPageRequest.getDomains()));

    Date startDateTime = transactionFilterPageRequest.getStartDateTime();
    Date endDateTime = transactionFilterPageRequest.getEndDateTime();

    spec = spec.and(PlayerThresholdHistorySpecification.createdDateBetween(startDateTime, endDateTime));
    return playerThresholdHistoryService.findAll(spec, pageRequest);
  }
}
