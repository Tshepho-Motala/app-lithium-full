package lithium.service.limit.client;

import java.security.Principal;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status428AccountFrozenGamstopSelfExcludedException;
import lithium.exceptions.Status433AccountBlockedPlayerRequestException;
import lithium.exceptions.Status434AccountBlockedResponsibleGamingException;
import lithium.exceptions.Status435AccountBlockedAMLException;
import lithium.exceptions.Status436AccountBlockedDuplicatedAccountException;
import lithium.exceptions.Status437AccountBlockedOtherException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.exceptions.Status446AccountFrozenCRUKSSelfExcludedException;
import lithium.exceptions.Status449AccountFrozenCoolingOffException;
import lithium.exceptions.Status450AccountFrozenSelfExcludedException;
import lithium.exceptions.Status455AccountBlockedFraudException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status482PlayerBetPlacementNotAllowedException;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status475AnnualLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.DomainRestriction;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerCoolOff;
import lithium.service.limit.client.objects.PlayerExclusionV2;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.limit.client.objects.Restrictions;
import lithium.service.limit.client.objects.User;
import lithium.service.limit.client.objects.UserRestrictionSet;
import lithium.service.limit.client.objects.UserRestrictionsRequest;
import lithium.service.limit.client.objects.VerificationStatusDto;
import lithium.service.translate.client.objects.Domain;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.PlayTimeLimitPubSubDTO;
import lithium.service.user.client.system.SystemPlayTimeLimitsV2Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class LimitInternalSystemService {
    public static final String USER_CONTRA_ACCOUNT_NOT_SET_RESTRICTION = "User contra account is not set";

    @Autowired private LithiumServiceClientFactory factory;
    @Autowired private MessageSource messageSource;
    @Autowired private CachingDomainClientService cachingDomainClientService;


    public void checkPlayerRestrictions(String playerGuid, String locale)
            throws  Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status496PlayerCoolingOffException {
        log.debug("checkPlayerRestrictions " + playerGuid + " " + locale);
        Restrictions restrictions = getPlayerRestrictions(playerGuid, locale);
        if (restrictions != null) {
            checkPlayerExclusionV2(restrictions.getPlayerExclusionV2());
            checkPlayerCoolOff(restrictions.getPlayerCoolOff());
        }
    }
    public void checkAccountStatus(String playerGuid, Integer errorCode)
            throws Status405UserDisabledException,
            Status437AccountBlockedOtherException,
            Status436AccountBlockedDuplicatedAccountException,
            Status455AccountBlockedFraudException,
            Status435AccountBlockedAMLException,
            Status434AccountBlockedResponsibleGamingException,
            Status433AccountBlockedPlayerRequestException,
            Status450AccountFrozenSelfExcludedException,
            Status496PlayerCoolingOffException,
            Status449AccountFrozenCoolingOffException,
            Status460LoginRestrictedException,
            Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException,
            Status428AccountFrozenGamstopSelfExcludedException {
        String domainName = playerGuid.split("/")[0];
        if (errorCode == Status490SoftSelfExclusionException.ERROR_CODE) {
            throw new Status490SoftSelfExclusionException(LoginError.SOFT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status491PermanentSelfExclusionException.ERROR_CODE) {
            throw new Status491PermanentSelfExclusionException(LoginError.PERMANENT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status460LoginRestrictedException.ERROR_CODE) {
            throw new Status460LoginRestrictedException(LoginError.RESTRICTED.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status449AccountFrozenCoolingOffException.ERROR_CODE) {
            throw new Status449AccountFrozenCoolingOffException(LoginError.ACCOUNT_FROZEN_COOLING_OFF.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status496PlayerCoolingOffException.ERROR_CODE) {
            throw new Status496PlayerCoolingOffException(LoginError.FLAGGED_AS_COOLING_OFF.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status446AccountFrozenCRUKSSelfExcludedException.ERROR_CODE) {
            throw new Status450AccountFrozenSelfExcludedException(LoginError.ACCOUNT_FROZEN_CRUKS_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status433AccountBlockedPlayerRequestException.ERROR_CODE) {
            throw new Status433AccountBlockedPlayerRequestException(LoginError.ACCOUNT_BLOCKED_PLAYER_REQUEST.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status434AccountBlockedResponsibleGamingException.ERROR_CODE) {
            throw new Status434AccountBlockedResponsibleGamingException(LoginError.ACCOUNT_BLOCKED_RESPONSIBLE_GAMING.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status435AccountBlockedAMLException.ERROR_CODE) {
            throw new Status435AccountBlockedAMLException(LoginError.ACCOUNT_BLOCKED_AML.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status455AccountBlockedFraudException.ERROR_CODE) {
            throw new Status455AccountBlockedFraudException(LoginError.ACCOUNT_BLOCKED_FRAUD.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status436AccountBlockedDuplicatedAccountException.ERROR_CODE) {
            throw new Status436AccountBlockedDuplicatedAccountException(LoginError.ACCOUNT_BLOCKED_DUPLICATE_ACCOUNT.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status437AccountBlockedOtherException.ERROR_CODE) {
            throw new Status437AccountBlockedOtherException(LoginError.ACCOUNT_BLOCKED_OTHER.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status405UserDisabledException.ERROR_CODE) {
            throw new Status405UserDisabledException(LoginError.USER_DISABLED.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status428AccountFrozenGamstopSelfExcludedException.ERROR_CODE) {
            throw new Status428AccountFrozenGamstopSelfExcludedException(LoginError.ACCOUNT_FROZEN_GAMESTOP_SELF_EXCLUDED.getResponseMessageLocal(messageSource, domainName));
        }
        if (errorCode == Status450AccountFrozenSelfExcludedException.ERROR_CODE) {
            throw new Status450AccountFrozenSelfExcludedException(LoginError.ACCOUNT_FROZEN_SELF_EXCLUDED.getResponseMessageLocal(messageSource, domainName));
        }
    }
    public void checkLimits(final String domainName, final String playerGuid, final String currency,
                            final Long betAmountCents, final String locale)
            throws
            Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status492DailyLossLimitReachedException, Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status500LimitInternalSystemClientException, Status478TimeSlotLimitException,
            Status475AnnualLossLimitReachedException {
        getClient().checkLimits(domainName, playerGuid, currency, betAmountCents, locale);
    }

    public Access checkAccess(final String playerGuid) throws Status500LimitInternalSystemClientException {
        return getClient().checkAccess(playerGuid);
    }

    public Access checkAccessLocalized(final String playerGuid, String locale) throws Status500LimitInternalSystemClientException {
        return getClient().checkAccessLocalized(playerGuid, locale);
    }

    public void checkPlayerBetPlacementAllowed(final String playerGuid) throws Status500LimitInternalSystemClientException, Status482PlayerBetPlacementNotAllowedException, Status438PlayTimeLimitReachedException {
        log.debug("checkPlayerBetPlacementAccess " + playerGuid);
        Access access = checkAccess(playerGuid);
        if (!access.isBetPlacementAllowed()) {
            throw new Status482PlayerBetPlacementNotAllowedException(RestrictionError.BET_PLACEMENT.getResponseMessageLocal(messageSource, playerGuid.split("/")[0], access.getBetPlacementErrorMessage()));
        }
        try {
            checkPlayTimeLimit(playerGuid);
        } catch (Status438PlayTimeLimitReachedException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }

    public void validatePlayTime(String userGuid)
            throws Status438PlayTimeLimitReachedException, Status500LimitInternalSystemClientException {
        try {
            checkPlayTimeLimit(userGuid);
        } catch (Status438PlayTimeLimitReachedException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }

    public void checkPlayTimeLimit(String userGuid)
            throws Status550ServiceDomainClientException, Status500LimitInternalSystemClientException, Status438PlayTimeLimitReachedException, Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException, Status426InvalidParameterProvidedException {
        String domainName = userGuid.split("/")[0];
        log.debug("checkPlayLimit: domain name:" + domainName);

        lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        if (domain.isPlaytimeLimit()) {
            getSystemPlayTimeLimitsV2Client().isAllowedToPlay(userGuid);
        }
    }

    private SystemPlayTimeLimitsV2Client getSystemPlayTimeLimitsV2Client () throws Status500LimitInternalSystemClientException {
        try {
            return factory.target(SystemPlayTimeLimitsV2Client.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, "default"), e.getStackTrace());
        }
    }

    public PlayTimeLimitPubSubDTO getCurrentPlayTimeLimitForUser(String userGuid) throws Status500LimitInternalSystemClientException,
            Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
        return getSystemPlayTimeLimitsV2Client().updateAndGetPlayerEntry(userGuid);
    }

    public List<PlayTimeLimitPubSubDTO> getPlayTimeTimeForUserLimits(String userGuid) throws Status500LimitInternalSystemClientException,
            Status414UserNotFoundException, Status550ServiceDomainClientException,Status426InvalidParameterProvidedException {
        String domainName=userGuid.split("/")[0];
        return getSystemPlayTimeLimitsV2Client().getPlayTimeTimeForUserLimits(userGuid);
    }

    public void checkPlayerCasinoAllowed(final String playerGuid) throws Status500LimitInternalSystemClientException, Status483PlayerCasinoNotAllowedException {
        log.debug("checkPlayerCasinoAccess " + playerGuid);
        Access access = checkAccess(playerGuid);
        if (!access.isCasinoAllowed()) {
            throw new Status483PlayerCasinoNotAllowedException(RestrictionError.CASINO.getResponseMessageLocal(messageSource, playerGuid.split("/")[0], access.getCasinoErrorMessage()));
        }
    }

    public String getVerificationStatusCode(final Long verificationStatusId) throws Status500LimitInternalSystemClientException {
        return getClient().getVerificationStatusCode(verificationStatusId);
    }

    public List<VerificationStatusDto> getAllVerificationStatuses() throws Status500LimitInternalSystemClientException {
        return getClient().getVerificationStatuses();
    }

    public Integer getVerificationStatusLevel(final Long verificationStatusId) throws Status500LimitInternalSystemClientException {
        return getClient().getVerificationStatusLevel(verificationStatusId);
    }

    public Integer getVerificationStatusLevel(final Long verificationStatusId, final String domainName) throws Status500LimitInternalSystemClientException {
        return getClient().getVerificationStatusLevelAgeOverride(verificationStatusId, domainName);
    }

    private void checkPlayerExclusionV2(PlayerExclusionV2 exclusion)
            throws Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException {
        if (exclusion != null) {
            if (exclusion.isPermanent()) {
                log.debug("Status491PermanentSelfExclusionException: " + exclusion.getMessage());
                throw new Status491PermanentSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PERMANENT_SELF_EXCLUSION", new Object[]{new lithium.service.translate.client.objects.Domain(exclusion.getPlayerGuid().split("/")[0])}, "Permanent self exclusion.", LocaleContextHolder.getLocale()), exclusion.getPlayerGuid());
            } else {
                log.debug("Status490SoftSelfExclusionException: " + exclusion.getMessage());
                throw new Status490SoftSelfExclusionException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.SOFT_SELF_EXCLUSION", new Object[]{new lithium.service.translate.client.objects.Domain(exclusion.getPlayerGuid().split("/")[0])}, "Soft self exclusion.", LocaleContextHolder.getLocale()), exclusion.getPlayerGuid());
            }
        }
    }

    public Boolean isPermanentSelfExcluded(String playerGuid) {
        try {
            checkPlayerRestrictions(playerGuid, LocaleContextHolder.getLocale().getLanguage());
        } catch (Status491PermanentSelfExclusionException e) {
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private void checkPlayerCoolOff(PlayerCoolOff playerCoolOff) throws Status496PlayerCoolingOffException {
        if (playerCoolOff != null) {
            throw new Status496PlayerCoolingOffException(LoginError.FLAGGED_AS_COOLING_OFF.getResponseMessageLocal(messageSource, playerCoolOff.getPlayerGuid().split("/")[0], new Object[] {playerCoolOff.getExpiryDateDisplay()}));
        }
    }

    public Restrictions getPlayerRestrictions(String playerGuid, String locale)
            throws Status500LimitInternalSystemClientException {
        try {
            return getClient().lookupRestrictions(playerGuid, locale);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
            throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, playerGuid.split("/")[0]), e.getStackTrace());
        }
    }

    public PlayerLimitSummaryFE getPlayerLimitSummary(String userGuid) throws Status500LimitInternalSystemClientException {
        try {
            return getClient().getPlayerLimitSummary(userGuid);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
            throw new Status500LimitInternalSystemClientException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new Domain(userGuid.split("/")[0])}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
        }
    }


    public Response<List<UserRestrictionSet>> setMany(@RequestBody UserRestrictionsRequest userRestrictionsRequest) {
        try {
            return getClient().setMany(userRestrictionsRequest);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
        }

        return Response.<List<UserRestrictionSet>>builder().data(new ArrayList()).build();
    }


    public Response<List<UserRestrictionSet>> liftMany(@RequestBody UserRestrictionsRequest userRestrictionsRequest) {
        try {
            return getClient().liftMany(userRestrictionsRequest);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
        }

        return Response.<List<UserRestrictionSet>>builder().data(new ArrayList()).build();
    }

    private LimitInternalSystemClient getClient() throws Status500LimitInternalSystemClientException {
        try {
            return factory.target(LimitInternalSystemClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, "default"), e.getStackTrace());
        }
    }


    public List<DomainRestriction> getDomainRestrictions(String domainName) throws Status500LimitInternalSystemClientException {
        try {
            return factory.target(DomainRestrictionsSystemClient.class, true).getDomainRestrictions(domainName);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
            throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, domainName), e.getStackTrace());
        }
    }

    public boolean contraAccountRestrictionInDomain(String domainName) throws Status500LimitInternalSystemClientException {
        return getDomainRestrictions(domainName).stream()
                .map(DomainRestriction::getName)
                .anyMatch(USER_CONTRA_ACCOUNT_NOT_SET_RESTRICTION::equals);
    }

    public List<DomainRestriction> getUserDomainRestrictions(String userGuid) throws Status500LimitInternalSystemClientException {
        try {
            return factory.target(DomainRestrictionsSystemClient.class, true).getUserDomainRestrictions(userGuid);
        } catch (Exception e) {
            log.debug("Status500LimitInternalSystemClientException: message = " + e.getMessage());
            throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, userGuid.split("/")[0]), e.getStackTrace());
        }
    }

    public boolean isContraAccountSet(String userGuid) throws Status500LimitInternalSystemClientException {
        return getUserDomainRestrictions(userGuid).stream()
                .map(DomainRestriction::getName)
                .noneMatch(USER_CONTRA_ACCOUNT_NOT_SET_RESTRICTION::equals);
    }

    public void checkPromotionsAllowed(String playerGuid) throws Status416PlayerPromotionsBlockedException {

        try {
            Access access  = checkAccess(playerGuid);
            if(!access.isCompsAllowed()) {
                throw new Status416PlayerPromotionsBlockedException(access.getCompsErrorMessage());
            }

        } catch (Status500LimitInternalSystemClientException e) {
            log.error(String.format("Failed to check if promotions is allowed for player: %s ", playerGuid));
        }
    }

    public void setPromotionsOptout(String domainName, String playerGuid, boolean optOut, Long userId) throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException, Status500LimitInternalSystemClientException, Status500InternalServerErrorException {
        getClient().setPromotionsOptOut(domainName, playerGuid, optOut, userId);
    }

    public void autoRestrictionTrigger(String playerGuid) throws Status500LimitInternalSystemClientException {
        getClient().autoRestrictionTrigger(playerGuid);
    }

    /**
     * This is an attempt to replace the method below (lithium.service.limit.client.LimitInternalSystemService#findPlayerLossLimits(java.lang.String, int))
     * This method will return the domain limit if the player limit is not found, and will also retrieve the net loss.
     * @param domainName
     * @param playerGuid
     * @param granularity
     * @param type
     * @return lithium.service.limit.client.objects.PlayerLimitV2Dto
     */
    public PlayerLimitV2Dto findPlayerLimitV2WithNetLoss(String domainName, String playerGuid, Integer granularity, Integer type) {
        return getClient().findPlayerLimitV2WithNetLoss(domainName, playerGuid, granularity, type);
    }

    public User getLossLimitVisibility(String playerGuid) {
        return getClient().getLossLimitVisibility(playerGuid);
    }
    public User setLossLimitVisibility(String playerGuid, LossLimitsVisibility visibility) {
        return getClient().setLossLimitVisibility(playerGuid, visibility);
    }

    public PlayerLimit findPlayerLossLimits(String playerGuid, int granularity) {
        try {
            //TODO: This Principal needs to be removed from this endpoint.
            // This is a system call, and system auth is used by default.
            // This principal is ignored on the receiving end!
            // lithium.service.limit.controllers.system.SystemPlayerLimitsController.findPlayerLimitPost
            Principal principal = () -> "system";
            switch (Granularity.fromGranularity(granularity)) {
                case GRANULARITY_MONTH:
                    Response<PlayerLimit> findPlayerLimitMonth = getClient().findPlayerLimit(playerGuid, playerGuid.split("/")[0], Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
                    if (findPlayerLimitMonth.isSuccessful()) {
                        return findPlayerLimitMonth.getData();
                    }
                    break;
                case GRANULARITY_WEEK:
                    Response<PlayerLimit> findPlayerLimitWeek = getClient().findPlayerLimit(playerGuid, playerGuid.split("/")[0], Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
                    if (findPlayerLimitWeek.isSuccessful()) {
                        return findPlayerLimitWeek.getData();
                    }
                    break;
                case GRANULARITY_DAY:
                    Response<PlayerLimit> findPlayerLimitDay = getClient().findPlayerLimit(playerGuid, playerGuid.split("/")[0], Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
                    if (findPlayerLimitDay.isSuccessful()) {
                        return findPlayerLimitDay.getData();
                    }
                    break;
                default:
                    break;

            }
        } catch (Exception e) {
            log.error("Could not retrieve player limits for {}", playerGuid);
        }
        return null;

    }
}

