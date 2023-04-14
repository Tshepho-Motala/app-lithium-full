package lithium.service.casino.stats.stream;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.casino.service.CasinoTriggerBonusService;
import lithium.service.stats.client.objects.Period.Granularity;
import lithium.service.stats.client.objects.StatSummary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(StatsLoginQueueSink.class)
@Deprecated
/**
 * Deprecated, needs to be removed once comps engine is in use.
 */
public class StatsLoginQueueProcessor {
	@Autowired
	private CasinoTriggerBonusService casinoTriggerBonusService;
	
	@StreamListener(StatsLoginQueueSink.INPUT) 
	void handle(List<StatSummary> statSummaries) throws Exception {
		log.debug("Received stats login summary from the queue for processing: " + statSummaries);
		for (StatSummary ss:statSummaries) {
			log.trace("StatSummary : "+ss);
			log.trace("Domain : "+ss.getStat().getDomain().getName());
			log.trace("Player : "+ss.getStat().getOwner().guid());
			log.trace("Granularity : "+Granularity.fromGranularity(ss.getPeriod().getGranularity()));
			log.trace("Count : "+ss.getCount());
			log.trace("LabelValues : "+ss.getLabelValues());
			casinoTriggerBonusService.processLoginStats(ss);
			log.trace("================================================");
		}
	}
}
