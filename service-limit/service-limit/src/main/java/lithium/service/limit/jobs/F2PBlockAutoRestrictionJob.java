package lithium.service.limit.jobs;

import java.util.Iterator;
import lithium.leader.LeaderCandidate;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.services.AutoRestrictionRulesetService;
import lithium.service.limit.services.AutoRestrictionService;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "lithium.service.limit.jobs.f2p-auto-restriction.enabled")
@Component
@Slf4j
public class F2PBlockAutoRestrictionJob {
	@Autowired private AutoRestrictionRulesetService rulesetService;
	@Autowired private AutoRestrictionService restrictionService;
	@Autowired private DomainRepository domainRepository;
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private LithiumServiceClientFactory services;

	@Scheduled(cron="${lithium.service.limit.jobs.f2p-auto-restriction.cron:0 0 2 * * *}")
	public void process() throws InterruptedException {
		log.info("F2PBlockAutoRestrictionJob running");
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		Iterable<Domain> iterable = domainRepository.findAll();
		Iterator<Domain> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			Domain domain = iterator.next();

			if (needToRunJobForDomain(domain)) {
				runJobForDomain(domain);
			}
		}
		log.info("F2PBlockAutoRestrictionJob has completed");
	}

	/**
	 * This job should only run on domains where a F2P_BLOCK auto-restriction ruleset is configured
	 *
	 * @param domain
	 * @return
	 */
	private boolean needToRunJobForDomain(Domain domain) {
		boolean needToRunJobForDomain = false;

		int page = 0;
		boolean process = true;
		while (process) {
			Pageable pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, new String[] { "id" });
			Page<AutoRestrictionRuleSet> pageResult = rulesetService.find(new String[] { domain.getName() },
				true, null, null,null, null, pageRequest);

			for (AutoRestrictionRuleSet ruleset: pageResult.getContent()) {
				needToRunJobForDomain = ruleset.getRules()
				.stream()
						.filter(rule -> {
							return (rule.isEnabled() &&
									rule.getRuleset().getRestrictionSet().getName().equals(SystemRestriction.F2P_BLOCK.restrictionName()));
						})
				.findAny().isPresent();

				if (needToRunJobForDomain) {
					return true;
				}
			}

			page++;
			if (!pageResult.hasNext()) process = false;
		}

		return needToRunJobForDomain;
	}

	private void runJobForDomain(Domain domain) throws InterruptedException {
		UserClient userClient = getUserClient();
		if (userClient == null) {
			log.error("F2PBlockAutoRestrictionJob failed. Could not retrieve users.");
			return;
		}

		long count = 0;
		long position = 0;
		boolean process = true;
		while (process) {
			DataTableResponse<User> response = userClient.table(domain.getName(), "1", position, 1L);
			count += response.getData().size();
			position += response.getData().size();
			if (count >= response.getRecordsTotal()) {
				process = false;
			}

			for (User user: response.getData()) {
				restrictionService.processAutoRestrictionRulesets(user.guid());

				Thread.sleep(100L);
			}
		}
	}

	private UserClient getUserClient() {
		UserClient client = null;
		try {
			client = services.target(UserClient.class, "service-user", true);
			return client;
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting user client | " + e.getMessage(), e);
			return null;
		}
	}
}
