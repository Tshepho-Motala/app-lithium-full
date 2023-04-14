package lithium.service.cashier.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSetProcess;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.repositories.AutoWithdrawalRuleSetProcessRepository;
import lithium.service.cashier.data.repositories.DomainRepository;
import lithium.service.cashier.services.AutoWithdrawalRulesetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AutoWithdrawalProcessJob {
	@Autowired private AutoWithdrawalRulesetService service;
	@Autowired private AutoWithdrawalRuleSetProcessRepository repository;
	@Autowired private DomainRepository domainRepository;
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private LithiumMetricsService metrics;


	@Scheduled(fixedDelay=300000)
	public void process() throws Exception {
		log.debug("AutoWithdrawalJobs running");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		processAutoWithdrawalProcessJob();
	}

	private void processAutoWithdrawalProcessJob() throws Exception {
		log.debug("AutoWithdrawalProcessJob running");
		metrics.timer(log).time("autoWithdrawalProcessJob", (StopWatch sw) -> {
			Iterable<Domain> domains = domainRepository.findAll();
			Iterator<Domain> iterator = domains.iterator();
			while (iterator.hasNext()) {
				Domain domain = iterator.next();
				sw.start("processList");
				List<AutoWithdrawalRuleSetProcess> processList = repository.findByRulesetDomainAndStartedNotNullAndCompletedNull(domain);
				sw.stop();
				if (processList.isEmpty()) {
					log.debug("AutoWithdrawalRuleSetProcess list is empty " + domain.getName());
					sw.start("toProcess");
					AutoWithdrawalRuleSetProcess toProcess = repository.findTop1ByRulesetDomainAndStartedIsNull(domain);
					sw.stop();
					log.debug("AutoWithdrawalRuleSetProcess toProcess " + toProcess);
					if (toProcess != null) {
						service.processRuleset(toProcess);
					}
				} else {
					if (log.isDebugEnabled()) {
						String strProcessList = processList.stream().map(AutoWithdrawalRuleSetProcess::toString)
							.collect(Collectors.joining(", "));
						log.debug("Not checking for new queued process requests on " + domain.getName()
							+ " | Currently processing [" + strProcessList + "]");
					}
					// TODO: Check for and handle stuck processes caused by service shutdown etc before processing completion?
				}
			}
		});
	}
}
