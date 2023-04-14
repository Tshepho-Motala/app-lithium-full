package lithium.service.report.games.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.repositories.ReportRepository;
import lithium.service.report.games.data.specifications.ReportSpecifications;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/report/games/list")
public class ReportListController {
	
	@Autowired TokenStore tokenStore;
	@Autowired LithiumServiceClientFactory factory;
	@Autowired ReportRepository reportRepository;

	@GetMapping("/table")
	public DataTableResponse<Report> table(@RequestParam(required=false) String[] domainNames, DataTableRequest request, Principal principal) throws Exception {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		
		Specification<Report> spec = null;
		
		List<String> domains = new ArrayList<>();
		
		tokenUtil.domainsWithRole("REPORT_GAMES").forEach(jwtDomain -> {
			if (jwtDomain.getPlayerDomain()) {
				domains.add(jwtDomain.getName().toLowerCase());
			}
		});
		
		if (domainNames != null) {
			for (String n: domainNames) {
				if (tokenUtil.hasRole(n, "REPORT_GAMES")) {
					domains.add(n);
				}
			}
		}
		
		if (domains.size() > 0) {
			spec = Specification.where(ReportSpecifications.domainIn(domains));
		}
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<Report> s = Specification.where(ReportSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
			
		}
		
		Page<Report> users = reportRepository.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, users);
	}
}
