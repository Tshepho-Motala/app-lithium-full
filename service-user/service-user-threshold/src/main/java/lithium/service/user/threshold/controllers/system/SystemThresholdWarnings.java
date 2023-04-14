package lithium.service.user.threshold.controllers.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Date;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement( name = "LithiumSystemToken" )
@RequestMapping( "/system/threshold/warnings/v1" )
public interface SystemThresholdWarnings {

  @PostMapping( "/find" )
  @Operation( summary = "Find applicable warnings issued to a player account.", tags = {"Player Thresholds"}, description = "", operationId = "find" )
  DataTableResponse<PlayerThresholdHistoryDto> find(
      @RequestParam( value = "domainName", required = false ) String domainName,
      @RequestParam( required = false ) String playerGuid,
      @RequestParam( required = false ) String[] typeName,
      @RequestParam( required = false ) Integer granularity,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateStart,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateEnd, DataTableRequest tableRequest)
  throws Status500InternalServerErrorException;
}
