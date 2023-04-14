package lithium.service.stats.client.stream;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.stats.client.objects.StatSummary;

@Service
public class StatsStream {
	
	@Autowired
	private StatsOutputQueue statsOutputQueue;

	@Autowired
	private StatsLoginOutputQueue statsLoginOutputQueue;

	public void register(QueueStatEntry queueStatEntry) {
		statsOutputQueue.outputQueue().send(MessageBuilder.<QueueStatEntry>withPayload(queueStatEntry).build());
	}
	
	public void sendLoginStats(List<StatSummary> statSummaries) {
		statsLoginOutputQueue.outputQueue().send(MessageBuilder.<List<StatSummary>>withPayload(statSummaries).build());
	}
}