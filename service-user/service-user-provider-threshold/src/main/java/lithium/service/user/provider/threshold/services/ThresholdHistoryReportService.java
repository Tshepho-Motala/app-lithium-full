package lithium.service.user.provider.threshold.services;


import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface ThresholdHistoryReportService {

  Page<PlayerThresholdHistory> getPlayerThresholdHistoryPage(ThresholdsFilterRequest transactionFilterPageRequest, PageRequest pageRequest);
}
