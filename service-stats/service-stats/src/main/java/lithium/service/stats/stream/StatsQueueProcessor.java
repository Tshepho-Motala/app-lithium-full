package lithium.service.stats.stream;

import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.services.StatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding({StatsQueueSink.class, StatsQueueSinkV2.class})
public class StatsQueueProcessor {
	@Autowired
	private StatService statService;

	@Deprecated // Just kept here to clear out any entries on the old queue. New stats should now be using v2.
	@StreamListener(StatsQueueSink.INPUT) 
	void handle(StatEntry statEntry) throws Exception {
		log.info("Received a stat from the queue for processing: " + statEntry);

		String statName = statEntry.getStat().getName();
		int start = statName.indexOf(".") + 1;
		int end = statName.indexOf(".", start + 1);
		String type = statName.substring(start, end);

		QueueStatEntry queueStatEntry = QueueStatEntry.builder()
		.type(type)
		.event(statEntry.event())
		.entry(statEntry)
		.build();

		statService.register(queueStatEntry);
	}

	@StreamListener(StatsQueueSinkV2.INPUT)
	void handlev2(QueueStatEntry queueStatEntry) throws Exception {
		log.info("Received a stat from the v2 queue for processing: " + queueStatEntry);

		statService.register(queueStatEntry);
	}
}
