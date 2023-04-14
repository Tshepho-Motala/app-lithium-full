package lithium.service.access.client.gamstop;

import lithium.service.Response;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckResponse;
import lithium.service.access.client.gamstop.objects.CheckExclusionRequest;
import lithium.service.access.client.gamstop.objects.CheckExclusionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-access-provider-gamstop")
public interface GamstopClient {

    @RequestMapping("/system/batch/checkAuthorization")
    public Response<BatchExclusionCheckResponse> batchExclusionCheck(
            @RequestBody BatchExclusionCheckRequest batchExclusionCheckRequest
    ) throws Exception;

    @RequestMapping("/system/check-exclusion")
    public Response<CheckExclusionResponse> checkExclusion(@RequestBody CheckExclusionRequest checkExclusionRequest);
}
