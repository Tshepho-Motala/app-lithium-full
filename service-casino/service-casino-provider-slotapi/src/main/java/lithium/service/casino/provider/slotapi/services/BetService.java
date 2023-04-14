package lithium.service.casino.provider.slotapi.services;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequest;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetResponse;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.context.BetContext;
import lithium.service.casino.provider.slotapi.services.bet.BetPhase1Validate;
import lithium.service.casino.provider.slotapi.services.bet.BetPhase2Persist;
import lithium.service.casino.provider.slotapi.services.bet.BetPhase3CallCasino;
import lithium.service.casino.provider.slotapi.storage.repositories.BetRepository;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
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
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

/**
 *
 */
@Service
@Slf4j
public class BetService {

    @Autowired
    BetRepository repository;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    LithiumTokenUtilService tokenService;

    @Autowired @Setter
    private BetPhase1Validate phase1Validate;

    @Autowired @Setter
    private BetPhase2Persist phase2Persist;

    @Autowired @Setter
    private BetPhase3CallCasino phase3CallCasino;

    @Autowired @Setter
    private LimitInternalSystemService limits;

    @Autowired @Setter
    private UserApiInternalClientService userApiInternalClientService;


    /**
     *
     * @param request
     * @param principal
     * @param locale
     * @return
     * @throws Status405UserDisabledException
     * @throws Status422DataValidationError
     * @throws Status423InvalidBonusTokenException
     * @throws Status424InvalidBonusTokenStateException
     * @throws Status470HashInvalidException
     * @throws Status471InsufficientFundsException
     * @throws Status473DomainBettingDisabledException
     * @throws Status474DomainProviderDisabledException
     * @throws Status483PlayerCasinoNotAllowedException
     * @throws Status484WeeklyLossLimitReachedException
     * @throws Status485WeeklyWinLimitReachedException
     * @throws Status490SoftSelfExclusionException
     * @throws Status491PermanentSelfExclusionException
     * @throws Status492DailyLossLimitReachedException
     * @throws Status493MonthlyLossLimitReachedException
     * @throws Status494DailyWinLimitReachedException
     * @throws Status495MonthlyWinLimitReachedException
     * @throws Status496PlayerCoolingOffException
     * @throws Status500UnhandledCasinoClientException
     * @throws Status500ProviderNotConfiguredException
     * @throws Status500LimitInternalSystemClientException
     * @throws Status500UserInternalSystemClientException
     */
    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public BetResponse bet(
        BetRequest request,
        Principal principal,
        String locale
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status422DataValidationError,
            Status423InvalidBonusTokenException,
            Status424InvalidBonusTokenStateException,
            Status438PlayTimeLimitReachedException,
            Status470HashInvalidException,
            Status471InsufficientFundsException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status478TimeSlotLimitException,
            Status483PlayerCasinoNotAllowedException,
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
            Status500UserInternalSystemClientException {
        BetContext context = new BetContext();
        BetResponse response = new BetResponse();

        context.setRequest(request);
        context.setResponse(response);

        LithiumTokenUtil token = tokenService.getUtil(principal);
        context.setSessionId(token.sessionId());

        try {
            context.setDomainName(token.domainName());
            context.setUserGuid(token.guid());
            userApiInternalClientService.performUserChecks(token.guid(), locale, token.sessionId(), true,
                    true, false);
            limits.checkPlayerRestrictions(token.guid(), locale);
            limits.checkPlayerCasinoAllowed(token.guid());
            phase1Validate.validate(context, request, token.domainName());

            try {
                phase2Persist.persist(context, token.guid(), token.domainName());
                phase3CallCasino.callCasinos(context);
            } catch (Status409DuplicateSubmissionException e) {
                log.warn("Duplicate submission. Returning original response: " + context);
            }

            response.setBalance(context.getBet().getBalanceAfter());
            response.setLithiumBetId(context.getBet().getLithiumAccountingId());

            log.info("bet " + context);

        }catch (Status471InsufficientFundsException insufficientFundsException){
            log.info("Player has insufficient funds: request: " + request);
            throw insufficientFundsException;
        } catch (ErrorCodeException ece) {
            log.warn("betresult error " + ExceptionMessageUtil.allMessages(ece) + " " + context);
            throw ece;
        } catch (Exception e) {
            log.error("bet error " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw e;
        }

        return response;
    }
}
