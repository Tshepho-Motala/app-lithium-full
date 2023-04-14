package lithium.service.access.provider.sphonic.iban.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.access.provider.sphonic.iban.ServiceAccessProviderSphonicIBANModuleInfo;
import lithium.service.access.provider.sphonic.iban.storage.repositories.AuthenticationRepository;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationJob {
	@Autowired private AuthenticationRepository authenticationRepository;
	@Autowired private SphonicAuthenticationService sphonicAuthenticationService;
	@Autowired private ServiceAccessProviderSphonicIBANModuleInfo moduleInfo;
	@Autowired private LeaderCandidate leaderCandidate;

	@Scheduled(fixedDelay = 60000)
	public void process() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		sphonicAuthenticationService.evictExpiredTokens(authenticationRepository);
		// Hope sphonic doesn't get mad that we authenticate every few minutes (before token expiration) - even if there's
		// no actual workflow requests going through. I'm doing it this way to avoid an additional second or two (or three)
		// it takes to make an HTTP authentication request before performing the IBAN and CRUKS workflows. I also feel
		// it is better to authenticate and store the token preemptively rather than authenticate and store only when a
		// workflow request is being executed.
		sphonicAuthenticationService.authenticateAllDomains(moduleInfo, authenticationRepository);
	}
}
