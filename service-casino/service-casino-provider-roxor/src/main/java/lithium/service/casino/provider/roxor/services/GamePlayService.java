package lithium.service.casino.provider.roxor.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.RoxorErrorCodeException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status402InsufficientFundsException;
import lithium.service.casino.provider.roxor.api.exceptions.Status404NotFoundException;
import lithium.service.casino.provider.roxor.api.exceptions.Status440LossLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status441TurnoverLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status442LifetimeDepositException;
import lithium.service.casino.provider.roxor.api.exceptions.Status443TimeLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status444DepositLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status445GeoLocationException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Money;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase1Persist;
import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase2Validate;
import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase3Process;
import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase4PersistResult;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
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
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class GamePlayService {
    @Autowired ValidationHelper validationHelper;
    @Autowired CasinoClientService casinoService;
    @Autowired @Setter UserApiInternalClientService userApiInternalClientService;
    @Autowired GamePlayPhase1Persist phase1Persist;
    @Autowired GamePlayPhase2Validate phase2Validate;
    @Autowired GamePlayPhase3Process phase3Process;
    @Autowired GamePlayPhase4PersistResult phase4PersistResult;

    @Retryable(exclude = { RoxorErrorCodeException.class }, include = { Exception.class })
    @TimeThisMethod
    public SuccessResponse gamePlay(
            String gamePlayId,
            String sessionKey,
            String xForwardFor,
            String gamePlayRequestJsonString,
            String locale
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status404NotFoundException,
            Status402InsufficientFundsException,
            Status440LossLimitException,
            Status441TurnoverLimitException,
            Status442LifetimeDepositException, //TODO where do we validate this
            Status443TimeLimitException,
            Status444DepositLimitException, //TODO where do we validate this
            Status445GeoLocationException, //TODO where do we validate this
            Status500RuntimeException {
        GamePlayContext gamePlayContext = new GamePlayContext();
        gamePlayContext.setRequestJsonString(gamePlayRequestJsonString);

        try {
            //phase 1 - persist Request
            phase1Persist.persist(
                    gamePlayContext,
                    sessionKey,
                    gamePlayId
            );

            //PHASE 2 - Validate Request
            phase2Validate.validateGamePlayRequest(
                    gamePlayContext,
                    sessionKey,
                    gamePlayId,
                    xForwardFor,
                    locale
            );

            //PHASE 3 - Invoke downstream services
            phase3Process.processGamePlay(gamePlayContext);

            //construct success response
            SuccessResponse.SuccessResponseBuilder responseBuilder = SuccessResponse.builder();
            responseBuilder.status(SuccessStatus.builder().code(SuccessStatus.Code.OK).build());
            if (sessionKey != null) {
                responseBuilder.balance(Money.builder()
                        .currency(gamePlayContext.getDomain().getCurrency())
                        .amount(gamePlayContext.getBalanceAfter())
                        .build());
            }

            gamePlayContext.setResponse(responseBuilder.build());

            //PHASE 4 - UPDATED PERSISTENCE
            phase4PersistResult.persistResult(gamePlayContext);

            return gamePlayContext.getResponse();
        } catch (
                Status400BadRequestException |
                Status401NotLoggedInException e
        ) {
            log.warn("game-play-validation [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            if (Objects.isNull(gamePlayContext.getGamePlayRequestErrorReason())
                    || gamePlayContext.getGamePlayRequestErrorReason().isEmpty()) {
                gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            }
            throw e;
        } catch (Status406DisabledGameException e) {
            log.error("game-play-disabled [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    ExceptionMessageUtil.allMessages(e), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status500RuntimeException(gamePlayContext);
        } catch (
                Status500RuntimeException e
        )  {
            log.error("game-play-validation [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    ExceptionMessageUtil.allMessages(e), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw e;
        } catch (
                Status471InsufficientFundsException e
        ) {
            log.info("game-play-insufficient-funds [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status402InsufficientFundsException(gamePlayContext);
        } catch (
                Status485WeeklyWinLimitReachedException |
                Status495MonthlyWinLimitReachedException |
                Status494DailyWinLimitReachedException |
                Status473DomainBettingDisabledException e
        ) {
            log.warn("game-play-domain-status [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status441TurnoverLimitException(gamePlayContext);
        } catch (
                Status484WeeklyLossLimitReachedException |
                Status493MonthlyLossLimitReachedException |
                Status492DailyLossLimitReachedException e
        ) {
            log.info("game-play-player-loss-limit [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status440LossLimitException(gamePlayContext);
        } catch (
                Status512ProviderNotConfiguredException |
                Status500UserInternalSystemClientException |
                //Status500UnhandledCasinoClientException |
                Status500LimitInternalSystemClientException |
                Status511UpstreamServiceUnavailableException |
                Status474DomainProviderDisabledException |
                Status550ServiceDomainClientException e
        ) {
            log.warn("game-play-internal-config [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status500RuntimeException(gamePlayContext);
        } catch (
                Status401UnAuthorisedException |
                Status405UserDisabledException |
                Status490SoftSelfExclusionException |
                Status491PermanentSelfExclusionException |
                Status496PlayerCoolingOffException e
        ) {
            log.warn("game-play-player-status-validation [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status401NotLoggedInException(gamePlayContext);
        } catch (Status478TimeSlotLimitException |
                Status438PlayTimeLimitReachedException e) {
            log.warn("game-play-time-limit [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    e.getMessage(), e);
            throw new Status443TimeLimitException(gamePlayContext);
        } catch (Exception e) {
            log.error("game-play-unknown [userGuid="+gamePlayContext.getUserGuid()
                    +", sessionKey="+sessionKey
                    +", gamePlayId="+gamePlayId
                    +", gamePlayJSON="+gamePlayRequestJsonString+"] " +
                    ExceptionMessageUtil.allMessages(e), e);
            gamePlayContext.setGamePlayRequestErrorReason(e.getMessage());
            throw new Status500RuntimeException(gamePlayContext);
        }
    }
}
