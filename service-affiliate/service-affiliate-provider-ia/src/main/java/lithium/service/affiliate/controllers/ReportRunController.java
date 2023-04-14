package lithium.service.affiliate.controllers;

import java.security.Principal;

import lithium.service.affiliate.data.entities.Report;
import lithium.service.affiliate.data.entities.ReportRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/report/ia/{reportId}/runs/{reportRunId}")
public class ReportRunController {
	
	@Autowired TokenStore tokenStore;

	@GetMapping
	public Response<ReportRun> get(@PathVariable("reportId") Report report, @PathVariable("reportRunId") ReportRun reportRun, Principal principal) throws Exception {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (!tokenUtil.hasRole(report.getDomainName(), "REPORT_IA")) throw new AccessDeniedException("User does not have access to income access reports for this domain");
		return Response.<ReportRun>builder().data(reportRun).status(Status.OK).build();
	}

	
}
