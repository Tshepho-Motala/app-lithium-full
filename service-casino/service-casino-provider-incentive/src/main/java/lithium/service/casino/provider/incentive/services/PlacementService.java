package lithium.service.casino.provider.incentive.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequest;
import lithium.service.casino.provider.incentive.api.schema.placement.response.PlacementResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase1Validate;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase2Persist;
import lithium.service.casino.provider.incentive.services.placement.PlacementPhase3CallCasino;
import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status482PlayerBetPlacementNotAllowedException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Slf4j
public class PlacementService {

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    LithiumTokenUtilService tokenService;

    @Autowired @Setter
    private PlacementPhase1Validate phase1Validate;

    @Autowired @Setter
    private PlacementPhase2Persist phase2Persist;

    @Autowired @Setter
    private PlacementPhase3CallCasino phase3CallCasino;

    @Autowired @Setter
    private LimitInternalSystemService limits;

    @Autowired @Setter
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired @Setter
    private CachingDomainClientService cachingDomainClientService;

    @Autowired @Setter
    private ProviderClientService providerClientService;

    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public PlacementResponse placement(
        PlacementRequest placementRequest,
        Principal principal,
        String locale
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status409DuplicateSubmissionException,
            Status422DataValidationError,
            Status423InvalidBonusTokenException,
            Status424InvalidBonusTokenStateException,
            Status438PlayTimeLimitReachedException,
            Status470HashInvalidException,
            Status471InsufficientFundsException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status478TimeSlotLimitException,
            Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status496PlayerCoolingOffException,
            Status500UnhandledCasinoClientException,
            Status500ProviderNotConfiguredException,
            Status500LimitInternalSystemClientException,
            Status500UserInternalSystemClientException,
            Status550ServiceDomainClientException,
            Status482PlayerBetPlacementNotAllowedException,
            Status438PlayTimeLimitReachedException {
        LithiumTokenUtil token = tokenService.getUtil(principal);

        cachingDomainClientService.checkBettingEnabled(token.domainName(), locale);

        providerClientService.checkProviderEnabled(token.domainName(), moduleInfo.getModuleName(), locale);

        userApiInternalClientService.performUserChecks(token.guid(), locale, token.sessionId(), true,
                true, false);

        limits.checkPlayerRestrictions(token.guid(), locale);
        limits.checkPlayerBetPlacementAllowed(token.guid());

        phase1Validate.validateVirtualCoin(placementRequest, token.guid());

        phase1Validate.validate(placementRequest, token.domainName());

        PlacementResponse response = new PlacementResponse();

        Placement placement = phase2Persist.persist(placementRequest, token);
        double balance = phase3CallCasino.callAccounting(placement);

        response.setBalance(CurrencyAmount.fromAmount(balance));
        response.setLithiumPlacementId(placement.getId());
        return response;
    }
}
