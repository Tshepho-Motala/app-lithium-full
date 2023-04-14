package lithium.service.user.threshold.client;


import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "service-user-provider-threshold" )
public interface UserProviderThresholdReportClient {

//  @PostMapping( "/backoffice/threshold-history/search" )
//  DataTableResponse<ThresholdHistoryDTO> getThresholdHistoryDTOPage(@RequestBody ThresholdsFilterRequest transactionFilterPageRequest,
//      @RequestParam Integer page, @RequestParam Integer size);
}
