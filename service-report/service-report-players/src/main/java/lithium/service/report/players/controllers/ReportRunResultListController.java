package lithium.service.report.players.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.entities.ReportRunResults;
import lithium.service.report.players.data.repositories.ReportRunResultsRepository;
import lithium.service.report.players.data.specifications.ReportRunResultSpecifications;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/report/players/{reportId}/runs/{reportRunId}/results")
@Slf4j
public class ReportRunResultListController {
	
	@Autowired ReportRunResultsRepository repo;
	@Autowired TokenStore tokenStore;
	
	@PostMapping("/table")
	public DataTableResponse<ReportRunResults> table(@PathVariable("reportRunId") ReportRun reportRun, DataTableRequest request, Principal principal) throws Exception {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		
		ReportRunResultSpecifications rrrs = new ReportRunResultSpecifications();
		
		if (!tokenUtil.hasRole(reportRun.getReport().getDomainName(), "REPORT_PLAYERS")) throw new AccessDeniedException("User does not have access to reports for this domain");
		
		
		Specification<ReportRunResults> spec = Specification.where(rrrs.reportRunId(reportRun.getId()));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			for (String search: request.getSearchValue().split(" ")) {
				log.info("Search term: " + search);
				Specification<ReportRunResults> s = Specification.where(rrrs.any(search));
				spec = (spec == null)? s: spec.and(s);
			}
		}
		log.info(spec.toString());
		Page<ReportRunResults> results = repo.findAll(spec, request.getPageRequest());		
		return new DataTableResponse<>(request, results);
	}

}
