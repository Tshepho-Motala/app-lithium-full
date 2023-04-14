package lithium.service.stats.client.service;

import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status425DateParseException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.stats.client.StatsClient;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.stats.client.objects.Period;
import lithium.service.stats.client.objects.StatSummary;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class StatsClientService implements StatsClient {
	@Autowired private LithiumServiceClientFactory factory;

	public long getAllTimeStatCountForUser(String userGuid, String type, String event)
			throws Status513StatsServiceUnavailableException, Status500InternalServerErrorException {
		Response<List<StatSummary>> response = stats(userGuid, type, event, null,
			Period.Granularity.GRANULARITY_TOTAL.granularity());
		if (response.getStatus().equals(Response.Status.NOT_FOUND)) {
			// TODO: This means that there is no stat for this user. The stats lookup controller should probably be reworked.
			//       For now, just returning 0.
			return 0;
		}
		if (!response.isSuccessful()) {
			String errorMsg = "Stats service returned an unhealthy response. " + response.getMessage() + " " + response.getStatus();
			log.error(errorMsg + " | " + userGuid + " | " + response);
			throw new Status500InternalServerErrorException(errorMsg);
		}
		List<StatSummary> statSummaries = response.getData();
		StatSummary allTimeSummary = statSummaries.get(0);
		return allTimeSummary.getCount();
	}

	@Override
	public Response<List<StatSummary>> stats(String playerGuid, String type, String event, String date,
                                             Integer granularity) throws Status513StatsServiceUnavailableException {
		return getStatsClient().stats(playerGuid, type, event, date, granularity);
	}

	private StatsClient getStatsClient() throws Status513StatsServiceUnavailableException {
		try {
			return factory.target(StatsClient.class, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status513StatsServiceUnavailableException(e.getMessage());
		}
	}
}
