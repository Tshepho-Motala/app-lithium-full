package lithium.service.shards.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.shards.services.ShardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "lithium.services.shards.cleanup-job-enabled", havingValue = "true")
@Component
@Slf4j
public class ShardEvictionJob {
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private ShardService service;

	@Scheduled(fixedRateString = "${lithium.services.shards.cleanup-job-delay-ms:120000}")
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.trace("I am not the leader");
			return;
		}
		service.evictStaleShards();
	}
}
