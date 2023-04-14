package lithium.service.report.games.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.repositories.ReportRunRepository;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/report/games/{reportId}/runs")
public class ReportRunListController {
	@Autowired ReportRunRepository repo;
	@Autowired TokenStore tokenStore;
	
	@PostMapping("/table")
	public DataTableResponse<ReportRun> table(@PathVariable("reportId") Report report, DataTableRequest request, Principal principal) throws Exception {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (!tokenUtil.hasRole(report.getDomainName(), "REPORT_GAMES")) throw new AccessDeniedException("User does not have access to reports for this domain");
		
		Page<ReportRun> runs = repo.findByReportId(report.getId(), request.getPageRequest());
		return new DataTableResponse<>(request, runs);
	}
}