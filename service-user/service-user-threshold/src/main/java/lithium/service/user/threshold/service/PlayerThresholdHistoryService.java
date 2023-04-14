package lithium.service.user.threshold.service;

import java.math.BigDecimal;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryRequest;
import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory;

public interface PlayerThresholdHistoryService {

  PlayerThresholdHistory savePlayerThresholdHistory(ProcessingContext context, PlayerLimitV2Dto limit, boolean sendNotifications)
  throws Status500InternalServerErrorException;

  PlayerThresholdHistory savePlayerThresholdHistory(ProcessingContext context, PlayerLimitV2Dto limit, BigDecimal amount, BigDecimal depositAmount,
      BigDecimal withdrawalAmount, BigDecimal netLifetimeDepositAmount, boolean sendNotifications)
  throws Status500InternalServerErrorException;

  DataTableResponse<PlayerThresholdHistoryDto> find(PlayerThresholdHistoryRequest request)
  throws Status500InternalServerErrorException;
}
