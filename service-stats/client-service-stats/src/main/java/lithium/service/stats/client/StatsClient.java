package lithium.service.stats.client;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.stats.client.objects.StatSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient(name="service-stats")
public interface StatsClient {
	@RequestMapping(method=RequestMethod.GET, path="/stats")
	public Response<List<StatSummary>> stats(
		@RequestParam(name="playerGuid", required=true) String playerGuid,
		@RequestParam(name="type", required=false, defaultValue="user") String type,
		@RequestParam(name="event", required=false, defaultValue="login-success") String event,
		@RequestParam(name="date", required=false) String date, // yyyy-MM-dd HH:mm
		@RequestParam(name="granularity", required=false, defaultValue="-1") Integer granularity
	) throws Status513StatsServiceUnavailableException;
}