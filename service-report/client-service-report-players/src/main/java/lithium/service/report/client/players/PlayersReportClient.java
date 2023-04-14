package lithium.service.report.client.players;

import lithium.service.Response;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-report-players")
public interface PlayersReportClient {

    @RequestMapping("/report/players/{reportId}/runs/{reportRunId}/batch/updateReportRunResult")
    public Response<BatchExclusionCheckResponse> updateReportRunResult(
            @PathVariable("reportId") Long reportId,
            @PathVariable("reportRunId") Long reportRunId,
            @RequestBody BatchExclusionCheckResponse batchResponse);
}
