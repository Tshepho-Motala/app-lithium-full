package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.data.repositories.UserRestrictionSetRepository;
import lithium.service.limit.data.specifications.UserRestrictionSetSpecification;
import lithium.service.limit.enums.AutoRestrictionRuleField;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.services.AutoRestrictionRulesetService;
import lithium.service.limit.services.AutoRestrictionService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UnderAgeAutoRestrictionJob {
	@Autowired private AutoRestrictionRulesetService rulesetService;
	@Autowired private AutoRestrictionService restrictionService;
	@Autowired private DomainRepository domainRepository;
	@Autowired private LeaderCandidate leaderCandidate;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private UserRestrictionSetRepository userRestrictionSetRepository;

	@Value("${lithium.service.limit.jobs.under-age-auto-restriction.page-size:1000}")
	private int underageBirthdayPageSize;

	@Value("${lithium.service.limit.jobs.under-age-auto-restriction.limit-page-size:1000}")
	private int limitPageSize;

	@Scheduled(cron="${lithium.service.limit.jobs.under-age-auto-restriction.cron:0 1 0 * * *}")
	public void process() throws InterruptedException {
		log.debug("UnderAgeAutoRestrictionJob running");
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
	}

	/**
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
						.anyMatch(rule -> rule.isEnabled() && rule.getField().equals(AutoRestrictionRuleField.AGE));

				if (needToRunJobForDomain) {
					return true;
				}
			}

			page++;
			if (!pageResult.hasNext()) process = false;
		}

		return needToRunJobForDomain;
	}

	private void runJobForDomain(Domain domain) throws InterruptedException{

		Page<UserRestrictionSet> userRestrictionSets = null;
		List<String> processedLocalGuid = new ArrayList<>();
		DateTime startingTime = DateTime.now();
		int page = 1;

		log.debug(String.format("Running UnderAgeAutoRestrictionJob#runJobForDomain for domain %s, starting time: %s", domain.getName(), startingTime));

		do {
			try {

				log.debug(String.format("Fetching local userGuids with UNDERAGE_COMPS_BLOCK restriction"));
				List<String> restrictions = Arrays.asList(SystemRestriction.UNDERAGE_COMPS_BLOCK.restrictionName());

				//Will get the first page everytime since we are modifying the restrictions
				PageRequest pageRequest = PageRequest.of(0, limitPageSize);

				Specification<UserRestrictionSet> specifications = Specification.where(UserRestrictionSetSpecification.withRestrictions(restrictions))
						.and(UserRestrictionSetSpecification.domain(domain))
						.and(UserRestrictionSetSpecification.active());

				if(!processedLocalGuid.isEmpty()) {
					specifications = specifications.and(UserRestrictionSetSpecification.withoutUsers(processedLocalGuid));
					log.debug(String.format("%s users have already been processed, skipping them on the next query", processedLocalGuid.size()));
				}

				userRestrictionSets = userRestrictionSetRepository.findAll(specifications, pageRequest);

				if(!userRestrictionSets.hasContent()) {
					log.debug("No local userGuids with with UNDERAGE_COMPS_BLOCK restriction");
					break;
				}

				List<String> localUserGuids = userRestrictionSets.getContent().stream().map(rs -> rs.getUser().getGuid()).collect(Collectors.toList());
				processedLocalGuid.addAll(localUserGuids);

				log.info(String.format("Local userGuids with UNDERAGE_COMPS_BLOCK, found %s on page %s", localUserGuids.size(), page), userRestrictionSets);
				log.debug("Fetching remote userGuids for users who have a birthday today");

				SimplePageImpl<String> remoteResults =  userApiInternalClientService.getUserGuidsWhosBirthdayIsToday(localUserGuids, 0 , underageBirthdayPageSize);

				log.debug(String.format("Remote userGuids found %s, page %s of %s", remoteResults.getTotalElements(), 0, remoteResults.getTotalPages()));

				processRemoteUserGuids(remoteResults.getContent());

				while(remoteResults.hasNext()) {
					Pageable next = remoteResults.nextPageable();
					log.debug(String.format("Fetching page %s userGuids for users who have a birthday today", next.getPageNumber()));
					remoteResults = userApiInternalClientService.getUserGuidsWhosBirthdayIsToday(localUserGuids, next.getPageNumber(), next.getPageSize());
					log.debug(String.format("Remote userGuids found %s, page %s of %s", remoteResults.getTotalElements(), next.getPageNumber(), remoteResults.getTotalPages()));
					processRemoteUserGuids(remoteResults.getContent());
					log.debug("Now sleeping for 2 seconds before, updating the next batch of birthdays");
					Thread.sleep(2000);
				}
			}
			catch (Exception e) {
				log.error("There was an issue running the Auto Lift restriction CRON job. The exception details are: " + e.getMessage(), e);
			}

			page = page + 1;
		}
		while(userRestrictionSets != null && userRestrictionSets.hasNext());

		Instant endingTime = Instant.now();
		Duration timeTakenToComplete = Duration.between(startingTime.toDate().toInstant(), endingTime);

		log.debug(String.format("Finished running UnderAgeAutoRestrictionJob#runJobForDomain for domain %s, went over %s records in hours:%s, minutes:%s, seconds:%s, milliseconds:%s", domain.getName(),
				processedLocalGuid.size(),timeTakenToComplete.toHours(), timeTakenToComplete.toMinutes(), timeTakenToComplete.getSeconds(), timeTakenToComplete.toMillis()));
	}

	public void processRemoteUserGuids(List<String> remoteUserGuids) {
		for(String remoteGuid: remoteUserGuids) {
			try {
				restrictionService.processAutoRestrictionRulesets(remoteGuid, false, true);
			}
			catch (Exception e) {
				log.error("Failed white processing AutoRestrictionSet for user:" + remoteGuid, e);
			}
		}
	}
}
