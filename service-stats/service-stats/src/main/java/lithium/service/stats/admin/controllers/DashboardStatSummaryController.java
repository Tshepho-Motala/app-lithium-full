package lithium.service.stats.admin.controllers;

import lithium.service.Response;
import lithium.service.stats.admin.services.DashboardStatSummaryService;
import lithium.service.stats.data.entities.DomainStatSummary;
import lithium.service.stats.objects.DashboardStatSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/dashboard/stats/{domainName}/{type}/{event}/{granularity}")
public class DashboardStatSummaryController {
	@Autowired private DashboardStatSummaryService service;

	@GetMapping
	public Response<DashboardStatSummary> dashboardStatSummary(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") String type,
		@PathVariable("event") String event,
		@PathVariable("granularity") Integer granularity
	) {
		log.debug("dashboardStatSummary [domainName="+domainName+", type="+type+", event="+event+", granularity="+granularity+"]");
		DashboardStatSummary dashboardStatSummary = null;
		try {
			dashboardStatSummary = service.compile(domainName, type, event, granularity);
			return Response.<DashboardStatSummary>builder().data(dashboardStatSummary).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Problem getting dashboard stat summary [domainName="+domainName+", type="+type+", event="+event
				+", granularity="+granularity+"] " + e.getMessage(), e);
			return Response.<DashboardStatSummary>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
