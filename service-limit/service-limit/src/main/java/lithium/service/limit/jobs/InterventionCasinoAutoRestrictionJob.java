package lithium.service.limit.jobs;

import lithium.leader.LeaderCandidate;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InterventionCasinoAutoRestrictionJob {

    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private AutoRestrictionRulesetService rulesetService;
    @Autowired
    private AutoRestrictionService restrictionService;
    @Autowired
    private UserRestrictionSetRepository userRestrictionSetRepository;

    private int batchSize = 1000;
    @Scheduled(cron="${lithium.service.limit.jobs.intervention-casino-auto-restriction.cron:0 0 0 * * *}")
    public void process() throws InterruptedException {
        log.debug("InterventionCasinoAutoRestrictionJob running");
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
                        .filter(rule -> (rule.isEnabled()
                                    && rule.getField().equals(AutoRestrictionRuleField.RESTRICTION_SUB_TYPE)))
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

        try {

            log.debug("Fetching local userguids with INTERVENTION_CASINO_BLOCK or PLAYER_CASINO_BLOCK restriction");

            List<String> restrictions = Arrays.asList(SystemRestriction.INTERVENTION_CASINO_BLOCK.restrictionName(), SystemRestriction.PLAYER_CASINO_BLOCK.restrictionName());

            Specification<UserRestrictionSet> specifications = Specification.where(UserRestrictionSetSpecification.withRestrictions(restrictions))
                    .and(UserRestrictionSetSpecification.domain(domain))
                    .and(UserRestrictionSetSpecification.active());

            List<UserRestrictionSet> userRestrictionSets = userRestrictionSetRepository.findAll(specifications);

            if (userRestrictionSets.isEmpty()) {
                log.debug("No local userGuids with with INTERVENTION_CASINO_BLOCK or PLAYER_CASINO_BLOCK restriction");
                return;
            }

            List<String> localUserGuids = userRestrictionSets.stream().map(rs -> rs.getUser().getGuid()).collect(Collectors.toList());

            log.info(String.format("Local userGuids with INTERVENTION_CASINO_BLOCK or PLAYER_CASINO_BLOCK, found:%s", userRestrictionSets.size()), userRestrictionSets);

            for (int i = 0; i < localUserGuids.size(); i++) {
                processRemoteUserGuids(localUserGuids);
                if (i % batchSize == 0)
                    Thread.sleep(2000);
            }

        } catch (Exception e) {
            log.error("There was an issue running the INTERVENTION_CASINO_BLOCK or PLAYER_CASINO_BLOCK Auto Lift restriction CRON job. The exception details are: " + e.getMessage(), e);
        }

    }

    public void processRemoteUserGuids(List<String> remoteUserGuids) {
        for(String remoteGuid: remoteUserGuids) {
            try {
                restrictionService.processAutoRestrictionRulesets(remoteGuid, false, true);
            }
            catch (Exception e) {
                log.error("Failed while processing InterventionCasinoAutoRestrictionJob for user:" + remoteGuid, e);
            }
        }
    }
}
