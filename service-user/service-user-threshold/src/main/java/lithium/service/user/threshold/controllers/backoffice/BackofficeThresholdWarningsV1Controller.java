package lithium.service.user.threshold.controllers.backoffice;

import java.util.Date;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryRequest;
import lithium.service.user.threshold.service.PlayerThresholdHistoryService;
import lithium.service.user.threshold.service.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/backoffice/threshold/warnings/{domainName}/v1" )
public class BackofficeThresholdWarningsV1Controller implements IBackofficeThresholdWarnings {

  @Autowired
  PlayerThresholdHistoryService playerThresholdHistoryService;
  @Autowired
  UserService userService;

  @Override
  public DataTableResponse<PlayerThresholdHistoryDto> find(String domainName, String playerGuid, String[] typeName, Integer granularity,
      Date dateStart, Date dateEnd, DataTableRequest tableRequest, LithiumTokenUtil lithiumTokenUtil)
      throws Status500InternalServerErrorException {
    PlayerThresholdHistoryRequest request = PlayerThresholdHistoryRequest.builder()
        .playerGuid(playerGuid)
        .domainName(domainName)
        .typeName(typeName)
        .granularity(granularity)
        .dateStart(dateStart)
        .dateEnd(dateEnd)
        .tableRequest(tableRequest)
        .build();
    // Extra check, but not really needed, its checked on ModuleInfo?
    DomainValidationUtil.validate(request.getDomainName(), "USER_THRESHOLD_HISTORY_VIEW", lithiumTokenUtil);
    return playerThresholdHistoryService.find(request);
  }

  @Override
  public boolean setNotifications(String playerGuid, boolean notifications, LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException
  {
    return userService.updateNotifications(playerGuid, notifications);
  }

  @Override
  public boolean getNotifications(String playerGuid, LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException
  {
    return userService.getNotifications(playerGuid);
  }
}
