package lithium.service.machine.controllers;

import static lithium.service.Response.Status.OK;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.graphite.GraphiteData;
import lithium.service.Response;
import lithium.service.machine.config.ServiceMachineConfigurationProperties;
import lithium.service.machine.data.objects.Stats;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{domainName}/machines/stats/{granularity}")
@Slf4j
public class MachineStatsController {
	
	@Autowired ServiceMachineConfigurationProperties properties;
	@Autowired @Qualifier("lithium.service.machine.RestTemplate") RestTemplate rest;
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm_yyyyMMdd");
	
	@GetMapping("/{type}")
	public Response<Stats> stats(@PathVariable("domainName") String domainName,
			@PathVariable("granularity") Integer granularity, @PathVariable("type") String type) {
		DateTime now = new DateTime(), current = null, last = null,
				lastUntil = null, previous = null, previousUntil = null;
		if (granularity == 1) { // year
			current = new DateTime(now.getYear(), 1, 1, 0, 0);
			last = current.minusYears(1);
			lastUntil = last.plusYears(1);
			previous = current.minusYears(2);
			previousUntil = previous.plusYears(1);
		} else if (granularity == 2) { // month
			current = new DateTime(now.getYear(), now.getMonthOfYear(), 1, 0, 0);
			last = current.minusMonths(1);
			lastUntil = last.plusMonths(1);
			previous = current.minusMonths(2);
			previousUntil = previous.plusMonths(1);
		} else if (granularity == 3) { // day
			current = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
			last = current.minusDays(1);
			lastUntil = last.plusDays(1);
			previous = current.minusDays(2);
			previousUntil = previous.plusDays(1);
		} else if (granularity == 4) { // week
			current = new DateTime(now.getYear(), 1, 1, 0, 0).withWeekyear(now.getWeekyear())
					.withWeekOfWeekyear(now.getWeekOfWeekyear()).withDayOfWeek(1);
			last = current.minusWeeks(1);
			lastUntil = last.plusWeeks(1);
			previous = current.minusWeeks(2);
			previousUntil = previous.plusWeeks(1);
		}
		Stats stats = Stats.builder()
		.total(maxValue(getGraphiteData(domainName, type, new Date(0L), null)))
		.current(maxValue(getGraphiteData(domainName, type, current.toDate(), null)))
		.last1(maxValue(getGraphiteData(domainName, type, last.toDate(), lastUntil.toDate())))
		.last2(maxValue(getGraphiteData(domainName, type, previous.toDate(), previousUntil.toDate())))
		.build();
		return Response.<Stats>builder().data(stats).status(OK).build();
	}
	
	private GraphiteData[] getGraphiteData(String domainName, String type, Date from, Date until) {
		String url = properties.getGraphiteBaseUrl()
			+ "render?target=maxSeries(service-machine.*.gauge.important.machines." + domainName + "."
			+ type + ")&format=json";
		if (from != null) {
			url += "&from=" + SDF.format(from);
		}
		if (until != null) {
			url += "&until=" + SDF.format(until);
		}
		log.debug(url);
		return rest.getForObject(url, GraphiteData[].class);
	}
	
	private int maxValue(GraphiteData[] response) {
		Double max = 0.0;
		for (int i = 0; i < response.length; i++) {
			GraphiteData data = response[i];
			for (String[] point: data.getDatapoints()) {
				Double value = (point[0] != null) ? Double.parseDouble(point[0]) : null;
				if (value != null && value > max) {
					max = value;
				}
			}
		}
		return max.intValue();
	}
	
}
