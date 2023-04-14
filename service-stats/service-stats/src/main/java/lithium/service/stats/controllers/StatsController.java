package lithium.service.stats.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.Label;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatSummary;
import lithium.service.stats.data.repositories.StatRepository;
import lithium.service.stats.data.repositories.StatSummaryRepository;
import lithium.service.stats.services.DomainService;
import lithium.service.stats.services.LabelService;
import lithium.service.stats.services.PeriodService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/stats")
public class StatsController {
	@Autowired
	private PeriodService periodService;
	@Autowired
	private DomainService domainService;
	@Autowired
	private StatRepository statRepository;
	@Autowired
	private StatSummaryRepository statSummaryRepository;
	
	@GetMapping
	public Response<List<StatSummary>> stats(
		@RequestParam(name="playerGuid", required=true) String playerGuid,
		@RequestParam(name="type", required=false, defaultValue="user") String type,
		@RequestParam(name="event", required=false, defaultValue="login-success") String event,
		@RequestParam(name="date", required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date date,
		@RequestParam(name="granularity", required=false, defaultValue="-1") Integer granularity
	) throws Status513StatsServiceUnavailableException {
		String[] domainAndPlayer = playerGuid.split("/");
		String statName = "stats."+type+"."+domainAndPlayer[0]+"."+domainAndPlayer[1]+"."+event;
		log.info("Search for "+statName);
		
		Stat stat = statRepository.findByName(statName);
		if (stat == null) {
			log.error("No stats found for : "+statName);
			return Response.<List<StatSummary>>builder().status(Status.NOT_FOUND).build();
		}
		
		if (date == null) date = new Date();
		
		Domain domain = domainService.findOrCreate(domainAndPlayer[0]);
		
		List<StatSummary> statSummaries = new ArrayList<>();
		
		if (granularity == -1) {
			for (int g = 1; g <= 6; g++) {
				Period period = periodService.findOrCreatePeriod(new DateTime(date.getTime()), domain, g);
				
				StatSummary statSummary = statSummaryRepository.findByPeriodAndStat(period, stat);
				
				if (statSummary!=null) statSummaries.add(statSummary);
			}
		} else {
			Period period = periodService.findOrCreatePeriod(new DateTime(date.getTime()), domain, granularity);
			
			StatSummary statSummary = statSummaryRepository.findByPeriodAndStat(period, stat);
			
			if (statSummary!=null) statSummaries.add(statSummary);
		}
		
		return Response.<List<StatSummary>>builder().status(Status.OK).data(statSummaries).build();
	}
}