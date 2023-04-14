package lithium.service.report.players.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckResponse;
import lithium.service.report.players.data.entities.Report;
import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.services.ReportRunService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/report/players/{reportId}/runs/{reportRunId}")
public class ReportRunController {
	
	@Autowired TokenStore tokenStore;

	@Autowired
	private ReportRunService reportRunService;

	@GetMapping
	public Response<ReportRun> get(@PathVariable("reportId") Report report, @PathVariable("reportRunId") ReportRun reportRun, Principal principal) throws Exception {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (!tokenUtil.hasRole(report.getDomainName(), "REPORT_PLAYERS")) throw new AccessDeniedException("User does not have access to reports for this domain");
		return Response.<ReportRun>builder().data(reportRun).status(Status.OK).build();
	}

	@PostMapping("/batch/updateReportRunResult")
	public Response<BatchExclusionCheckResponse> updateReportRunResult (
			@PathVariable("reportId") Report report,
			@PathVariable("reportRunId") ReportRun reportRun,
			@RequestBody BatchExclusionCheckResponse batchResponse) {
		reportRunService.updateReportRunResult(batchResponse);
		return Response.<BatchExclusionCheckResponse>builder().data(batchResponse).status(Status.OK).build();
	}

	
}
