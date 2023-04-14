package lithium.service.user.threshold.client;


import java.util.Date;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "service-user-threshold" )
public interface UserThresholdClient {

  @PostMapping( "/system/threshold/warnings/v1/find" )
  DataTableResponse<PlayerThresholdHistoryDto> getThresholdLimits(
      @RequestParam( "domainName" ) String domainName,
      @RequestParam( required = false ) String playerGuid,
      @RequestParam( required = false ) String[] typeName,
      @RequestParam( required = false ) Integer granularity,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateStart,
      @RequestParam( required = false ) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME ) Date dateEnd);
}
