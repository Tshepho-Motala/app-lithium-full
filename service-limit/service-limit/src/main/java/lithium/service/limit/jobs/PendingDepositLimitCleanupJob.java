package lithium.service.limit.jobs;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.leader.LeaderCandidate;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.services.DepositLimitService;
import lithium.service.limit.services.ExternalUserService;
import lithium.service.user.client.objects.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static lithium.service.domain.client.DomainSettings.DEFAULT_DEPOSIT_LIMIT_PLAYER_CONFIRMATION;

@Slf4j
@Component
public class PendingDepositLimitCleanupJob {
    @Autowired
    ExternalUserService externalUserService;
    @Autowired
    LeaderCandidate leaderCandidate;
    @Autowired
    PlayerLimitRepository playerLimitRepository;
    @Autowired
    ServiceLimitConfigurationProperties properties;
    @Autowired
    DepositLimitService depositLimitService;
    @Autowired
    ChangeLogService changeLogService;

    @Autowired
    @Setter
    MessageSource messageSource;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @TimeThisMethod
    @Scheduled(cron = "${lithium.service.limit.jobs.pending-deposit-limit-cleanup.cron:0 * * * * *}")
    public void process() throws Exception {
        log.debug("PendingDepositLimitCleanupJob running");
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        int page = 0;
        boolean hasMore = true;
        while (hasMore) {
            PageRequest pageRequest = PageRequest.of(page, properties.getJobs().getPendingDepositLimitCleanup().getPageSize());
            Page<PlayerLimit> allPending = depositLimitService.findAllPending(pageRequest);
            log.debug("Found " + allPending.getContent().size() + " entries. Page " + allPending.getNumber() + " of " + allPending.getTotalPages());
            SW.start("PendingDepositLimitCleanupJob_pagerequest_" + page);
            for (PlayerLimit pl : allPending.getContent()) {
                int pendingPeriodInHours = depositLimitService.getPendingPeriodFromSettings(pl.getDomainName());
                if (new DateTime(pl.getCreatedDate(), DateTimeZone.getDefault()).plusHours(pendingPeriodInHours).isBeforeNow()) {
                    log.warn(new DateTime(pl.getCreatedDate(), DateTimeZone.getDefault()).plusHours(pendingPeriodInHours) + " is before : " + DateTime.now());
                    log.warn("Moving pending deposit limit to current : " + pl);
                    User player = externalUserService.findByGuid(pl.getPlayerGuid());

                    Integer typeDepositLimit;
                    String granularityTranslation;

                    if(!getPlayerConfirmationFromSettings(pl.getDomainName())){
                        typeDepositLimit = LimitType.TYPE_DEPOSIT_LIMIT.type();
                        granularityTranslation = "PENDING_TO_DEPOSIT";
                    } else {
                        typeDepositLimit = LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type();
                        granularityTranslation = "PENDING_TO_SUPPOSED";
                    }

                    PlayerLimit plCurrent = pl.toBuilder().id(null).type(typeDepositLimit).amount(0).build();

                    String granularityStr = "";
                    if (pl.getGranularity() == (Granularity.GRANULARITY_DAY.granularity()))
                        granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.".concat(granularityTranslation).concat(".DAILY");
                    else if (pl.getGranularity() == (Granularity.GRANULARITY_WEEK.granularity()))
                        granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.".concat(granularityTranslation).concat(".WEEKLY");
                    else if (pl.getGranularity() == (Granularity.GRANULARITY_MONTH.granularity()))
                        granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.".concat(granularityTranslation).concat(".MONTHLY");

                    String comment = messageSource.getMessage(granularityStr, new Object[]{new DateTime(pl.getCreatedDate(), DateTimeZone.getDefault())}, LocaleContextHolder.getLocale());
                    List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").toValue(CurrencyAmount.fromCents(pl.getAmount()).toAmount().toPlainString()).build());

                    changeLogService.registerChangesWithDomain("user.depositlimit", "edit", player.getId(), User.SYSTEM_GUID, comment, null, clfc, Category.FINANCE, SubCategory.FINANCE, 0, pl.getDomainName());

                    plCurrent.setAmount(pl.getAmount());
                    depositLimitService.deleteDepositLimit(pl, User.SYSTEM_GUID);
                    depositLimitService.deleteDepositLimit(plCurrent, User.SYSTEM_GUID);

                    playerLimitRepository.save(plCurrent);
                }
            }
            SW.stop();
            page++;
            if (!allPending.hasNext()) hasMore = false;
        }
    }

    private Boolean getPlayerConfirmationFromSettings(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Optional<String> labelValue = domain.findDomainSettingByName(DEFAULT_DEPOSIT_LIMIT_PLAYER_CONFIRMATION.key());
        return Boolean.parseBoolean(labelValue.orElse(DEFAULT_DEPOSIT_LIMIT_PLAYER_CONFIRMATION.defaultValue()));
    }
}
