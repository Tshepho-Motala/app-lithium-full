package lithium.service.user.threshold.controllers.system;

import java.util.Date;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryRequest;
import lithium.service.user.threshold.service.PlayerThresholdHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SystemThresholdWarningsV1Controller implements SystemThresholdWarnings {

  @Autowired
  private PlayerThresholdHistoryService playerThresholdHistoryService;

  @Override
  public DataTableResponse<PlayerThresholdHistoryDto> find(
      @RequestParam( value = "domainName", required = false ) String domainName,
      @RequestParam( required = false ) String playerGuid,
      @RequestParam( required = false ) String[] typeName,
      @RequestParam( required = false ) Integer granularity,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateStart,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateEnd,
      DataTableRequest tableRequest
  ) throws Status500InternalServerErrorException {
    PlayerThresholdHistoryRequest request = PlayerThresholdHistoryRequest.builder()
        .playerGuid(playerGuid)
        .domainName(domainName)
        .typeName(typeName)
        .granularity(granularity)
        .dateStart(dateStart)
        .dateEnd(dateEnd)
        .tableRequest(tableRequest)
        .build();
    return playerThresholdHistoryService.find(request);
  }
}
