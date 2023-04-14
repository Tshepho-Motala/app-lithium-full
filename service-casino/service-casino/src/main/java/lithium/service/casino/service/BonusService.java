package lithium.service.casino.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.casino.api.backoffice.schema.ActiveBonusResponse;
import lithium.service.casino.api.frontend.schema.BonusHistoryResponse;
import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusExternalGameConfig;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusRepository;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.data.specifications.PlayerBonusHistorySpecification;
import lithium.service.client.datatable.DataTableRequest;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.ChangeLogType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BonusService {

    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    PlayerBonusHistoryRepository playerBonusHistoryRepository;

    @Autowired
    ChangeLogService changeLogService;

    public List<ActiveBonusResponse> getActiveCashBonuses(String domainName) {

        List<Bonus> activeCashBonuses = bonusRepository.findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentFreeMoneyWagerRequirementIsNull(BonusRevision.BONUS_TYPE_TRIGGER, BonusRevision.TRIGGER_TYPE_MANUAL, domainName);

        return activeCashBonuses.stream().map(bonus -> ActiveBonusResponse.builder().bonusCode(bonus.getCurrent().getBonusCode()).bonusName(bonus.getCurrent().getBonusName()).build()).collect(Collectors.toList());
    }

    public boolean isCashBonus(String domainName, String bonusCode) {
        Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentEnabledTrueAndCurrentFreeMoneyWagerRequirementIsNull(bonusCode, domainName, BonusRevision.BONUS_TYPE_TRIGGER, BonusRevision.TRIGGER_TYPE_MANUAL);
        return bonus == null ? false : true;
    }

    public Page<PlayerBonusHistory> find(String playerGuid, String[] bonusCodes, Date dateRangeStart, Date dateRangeEnd, DataTableRequest request) {

        Specification<PlayerBonusHistory> spec = null;

        spec = addToSpec(playerGuid, spec, PlayerBonusHistorySpecification::playerGuid);
        spec = addToSpec(dateRangeStart, false, spec, PlayerBonusHistorySpecification::startedDateRangeStart);
        spec = addToSpec(dateRangeEnd, true, spec, PlayerBonusHistorySpecification::startedDateRangeEnd);
        spec = addToSpec(bonusCodes, spec, PlayerBonusHistorySpecification::bonusCodes);
        spec = addToSpec(spec, PlayerBonusHistorySpecification.zeroWagerRequirement());

        Page<PlayerBonusHistory> page = playerBonusHistoryRepository.findAll(spec, request.getPageRequest());

        return page;
    }

    public Specification<PlayerBonusHistory> addToSpec(Specification<PlayerBonusHistory> spec,
                                                         Specification<PlayerBonusHistory> predicateMethod) {
            Specification<PlayerBonusHistory> localSpec = Specification.where(predicateMethod);
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
    }

    public Specification<PlayerBonusHistory> addToSpec(final String aString, Specification<PlayerBonusHistory> spec,
                                                         Function<String, Specification<PlayerBonusHistory>> predicateMethod) {
        if (aString != null && !aString.isEmpty()) {
            Specification<PlayerBonusHistory> localSpec = Specification.where(predicateMethod.apply(aString));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    public Specification<PlayerBonusHistory> addToSpec(final String[] arrayOfStrings, Specification<PlayerBonusHistory> spec,
                                                Function<String[], Specification<PlayerBonusHistory>> predicateMethod) {
        if (arrayOfStrings != null && arrayOfStrings.length > 0) {
            Specification<PlayerBonusHistory> localSpec = Specification.where(predicateMethod.apply(arrayOfStrings));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    public Specification<PlayerBonusHistory> addToSpec(final Date aDate, boolean addDay, Specification<PlayerBonusHistory> spec,
                                                         Function<Date, Specification<PlayerBonusHistory>> predicateMethod) {
        if (aDate != null) {
            DateTime someDate = new DateTime(aDate);
            if (addDay) {
                someDate = someDate.plusDays(1).withTimeAtStartOfDay();
            } else {
                someDate = someDate.withTimeAtStartOfDay();
            }
            Specification<PlayerBonusHistory> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    public Specification<PlayerBonusHistory> addToSpec(final List<String> domainList, Specification<PlayerBonusHistory> spec,
                                                        Function<List<String>, Specification<PlayerBonusHistory>> predicateMethod) {
        if (domainList != null && domainList.size() > 0) {
            Specification<PlayerBonusHistory> localSpec = Specification.where(predicateMethod.apply(domainList));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    public Specification<BonusRevision> addToSpecBonusCodes(final List<String> activeDomains,
                                                   Function<List<String>, Specification<BonusRevision>> predicate) {
        Specification<BonusRevision> spec = null;
        if(activeDomains != null && activeDomains.size() > 0) {
            spec = Specification.where(predicate.apply(activeDomains));
        }
        return spec;
    }

    public List<ActiveBonusResponse> getAllActiveBonuses(String playerGuid, String provider, Long campaignId ) {

        Specification<PlayerBonusHistory> bonusHistoryResponseSpecifications = Specification.where(PlayerBonusHistorySpecification.playerGuid(playerGuid))
                .and(PlayerBonusHistorySpecification.notCancelled())
                .and(PlayerBonusHistorySpecification.notExpired())
                .and(PlayerBonusHistorySpecification.notCompleted());

        if(provider != null && provider.length() > 0) {
            bonusHistoryResponseSpecifications = bonusHistoryResponseSpecifications.and(PlayerBonusHistorySpecification.forProvider(provider));
        }

        if(campaignId != null) {
            bonusHistoryResponseSpecifications = bonusHistoryResponseSpecifications.and(PlayerBonusHistorySpecification.forCampaign(campaignId));
        }

        List<PlayerBonusHistory> bonuses = playerBonusHistoryRepository.findAll(bonusHistoryResponseSpecifications);

        return bonuses.stream().map(pbh -> {
                   ActiveBonusResponse activeBonusResponse =  ActiveBonusResponse.builder()
                            .playerBonusHistoryId(pbh.getId())
                            .bonusCode(pbh.getBonus().getBonusCode())
                            .bonusName(pbh.getBonus().getBonusName())
                            .build();

                   Optional<BonusExternalGameConfig> result = getBonusExternalConfigForProviderAndCampaign(provider, campaignId,pbh.getBonus().getBonusExternalGameConfigs());

                   if(result.isPresent()) {
                       BonusExternalGameConfig config = result.get();
                       activeBonusResponse.setProvider(config.getProvider());
                       activeBonusResponse.setCampaignId(config.getCampaignId());
                   }
                   return activeBonusResponse;
                })
                .collect(Collectors.toList());
    }


    public Optional<BonusExternalGameConfig> getBonusExternalConfigForProviderAndCampaign(String provider, Long campaignId, List<BonusExternalGameConfig> externalGameConfigs) {

        Optional<BonusExternalGameConfig> bonusExternalGameConfig = Optional.empty();

        if(externalGameConfigs == null) {
            return bonusExternalGameConfig;
        }

        if(provider != null && campaignId != null) {
            bonusExternalGameConfig = externalGameConfigs.stream().filter(config -> Objects.equals(provider, config.getProvider()) && Objects.equals(campaignId, config.getCampaignId()))
                    .findFirst();
        }
        else if(provider != null) {
            bonusExternalGameConfig =  externalGameConfigs.stream().filter(config -> Objects.equals(provider, config.getProvider()))
                    .findFirst();
        }
        else if(campaignId != null) {
            bonusExternalGameConfig =  externalGameConfigs.stream().filter(config -> Objects.equals(campaignId, config.getCampaignId()))
                    .findFirst();
        }
        else {
            bonusExternalGameConfig =  externalGameConfigs.stream().findFirst();
        }


        return bonusExternalGameConfig;
    }


    public boolean markBonusComplete(Long playerBonusHistoryId, LithiumTokenUtil util) throws Exception {
        PlayerBonusHistory playerBonusHistory = playerBonusHistoryRepository.findOne(playerBonusHistoryId);

        if(playerBonusHistory == null) {
            throw new Exception("An invalid playerHistoryId was provided");
        }

        boolean alreadyMarkedAsCompleted = Optional.ofNullable(playerBonusHistory.getCompleted()).orElse(false);

        if(alreadyMarkedAsCompleted) {
            log.info(String.format("Player bonus history has already been marked as complete for player:%s", playerBonusHistory.getPlayerBonus().getPlayerGuid()));
            return true;
        }

        playerBonusHistory.setCompleted(true);
        playerBonusHistoryRepository.save(playerBonusHistory);

        List<ChangeLogFieldChange> changeLogFieldChanges = Arrays.asList(ChangeLogFieldChange.builder()
                .field("completed")
                .fromValue("false")
                .toValue("true")
                .build());

        String comment = String.format("Bonus %s was completed successfully", playerBonusHistory.getBonus().getBonusCode());

        changeLogService.registerChangesForNotesWithFullNameAndDomain("playerBonusHistory", ChangeLogType.EDIT.name(), playerBonusHistoryId, util.guid(), util, comment, null, changeLogFieldChanges, Category.BONUSES, SubCategory.BONUS_REGISTER, 10, util.domainName());

        return true;
    }
}