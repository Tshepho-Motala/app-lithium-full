package lithium.service.stats.controllers;

import lithium.service.Response;
import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.DomainStatSummary;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatSummary;
import lithium.service.stats.services.DomainStatService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/domain/stats/{domainName}")
public class DomainStatsController {
	@Autowired DomainStatService service;

	@GetMapping
	public Response<List<DomainStatSummary>> domainStats(
		@PathVariable("domainName") String domainName,
		@RequestParam("type") String type,
		@RequestParam("event") String event,
		@RequestParam(name="date", required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date date,
		@RequestParam(name="granularity", required=false, defaultValue="-1") Integer granularity
	) {
		log.debug("domainStats [domainName="+domainName+", type="+type+", event="+event+", date="+date+", granularity="+granularity+"]");
		List<DomainStatSummary> domainStatSummaries = null;
		try {
			domainStatSummaries = service.find(domainName, type, event, date, granularity);
			return Response.<List<DomainStatSummary>>builder().data(domainStatSummaries).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Problem getting domain stat summary data [domainName="+domainName+", type="+type+", event="+event+", date="+date+
				", granularity="+granularity+"] " + e.getMessage(), e);
			return Response.<List<DomainStatSummary>>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
