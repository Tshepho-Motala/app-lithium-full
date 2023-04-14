package lithium.service.cashier.scheduled;

import java.util.stream.StreamSupport;

import lithium.service.user.client.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CopyDomainLimits {
	@Autowired
	private DomainMethodProcessorRepository domainMethodProcessorRepository;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private LeaderCandidate leaderCandidate;
	
	@Async
	public void copyDomainLimits() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		log.info("Copy DomainMethodProcessor DomainLimits (DL)");
		StreamSupport.stream(domainMethodProcessorRepository.findAll().spliterator(), true)
		.filter(dmp -> (
			dmp.getEnabled() &&
			!dmp.getDeleted() &&
			dmp.getLimits()!=null &&
			dmp.getDomainLimits()==null
		))
		.forEach(dmp -> {
			try {
				Limits dl = dmp.getLimits().toBuilder()
				.id(null)
				.version(0)
				.build();
				domainMethodProcessorService.saveDomainLimits(dmp, dl, User.SYSTEM_GUID, User.SYSTEM_GUID);
			} catch (Exception e) {
				log.error("Could not copy limits to domainLimits.", e);
			}
		});
	}
}