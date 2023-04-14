package lithium.service.machine.jobs;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lithium.leader.LeaderCandidate;
import lithium.service.machine.data.repositories.DomainRepository;
import lithium.service.machine.services.MachineStatsService;
import lombok.extern.slf4j.Slf4j;

@Service
@EnableScheduling
@Slf4j
public class MachineStatsReporter {
	
	@Autowired MeterRegistry meterRegistry;
	@Autowired MachineStatsService statsService;
	@Autowired DomainRepository domainsRepo;
	@Autowired LeaderCandidate leaderCandidate;
	
	@Scheduled(fixedDelayString="${lithium.services.machine.stat-rate:10000}")
	public void send() {
		if (leaderCandidate.iAmTheLeader()) {
			log.debug("Registering machine stats");
			
			domainsRepo.findAll().forEach((domain) -> {
				meterRegistry.gauge("important.machines." + domain.getName().toLowerCase() + ".online", statsService.getOnlineMachines(domain));
				meterRegistry.gauge("important.machines." + domain.getName().toLowerCase() + ".active", statsService.getActiveMachines(domain));
			});
		}
	}
	
}

//http://devops-graphite.openshift-metrics.cloud.playsafesa.com/render?target=maxSeries(service-machine.*.gauge.important.machines.online)&format=json&from=-10min