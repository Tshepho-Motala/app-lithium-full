package lithium.service.casino.provider.iforium.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.casino.CasinoTransactionLabels;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.LastBetResultResponse;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.iforium.context.GameRoundContext;
import lithium.service.casino.provider.iforium.exception.AccountNotFoundException;
import lithium.service.casino.provider.iforium.exception.GameRoundNotFoundException;
import lithium.service.casino.provider.iforium.exception.InternalServerErrorException;
import lithium.service.casino.provider.iforium.exception.InvalidGameException;
import lithium.service.casino.provider.iforium.exception.InvalidGatewaySessionTokenException;
import lithium.service.casino.provider.iforium.exception.LossLimitReachedException;
import lithium.service.casino.provider.iforium.exception.SessionLengthLimitReachedException;
import lithium.service.casino.provider.iforium.exception.TransactionNotFoundException;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.GameRoundRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.GameRoundResponse;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResponse;
import lithium.service.casino.provider.iforium.service.GameRoundService;
import lithium.service.casino.provider.iforium.util.BalanceUtils;
import lithium.service.casino.provider.iforium.util.GameRoundUtils;
import lithium.service.casino.provider.iforium.util.LithiumClientUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.objects.Game;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.reward.client.QueryRewardClientService;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static lithium.service.casino.provider.iforium.constant.Constants.ZERO_AMOUNT;
import static lithium.service.casino.provider.iforium.util.BalanceUtils.buildBalanceResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoundServiceImpl implements GameRoundService {

    private static final String EN_GB_LOCALE = "en_GB";

    private final LithiumClientUtils lithiumClientUtils;
    private final CachingDomainClientService cachingDomainClientService;
    private final CasinoClientService casinoClientService;
    private final ModuleInfo moduleInfo;

    private final QueryRewardClientService queryRewardClientService;

    @Override
    public PlaceBetResponse placeBet(PlaceBetRequest placeBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException
    {
        LoginEvent loginEvent = lithiumClientUtils.getSystemLoginEventsClient()
                                                  .findBySessionKey(placeBetRequest.getGatewaySessionToken());

        GameRoundUtils.checkGatewaySessionTokenExpiration(placeBetRequest, loginEvent, () -> getPlayerBalance(domainName, placeBetRequest));
        GameRoundUtils.checkUserGuidValidity(placeBetRequest, loginEvent, InvalidGatewaySessionTokenException.class);


        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        GameRoundUtils.checkCurrencyValidity(placeBetRequest, domain, () -> getPlayerBalance(domainName, placeBetRequest));

        Game game = getGame(placeBetRequest, domainName);

        GameRoundContext context = buildGameRoundContext(placeBetRequest, game, placeBetRequest.getEndRound(), loginEvent.getId());
        context.setAmount(placeBetRequest.getAmount());
        context.setFreeBetCost(placeBetRequest.getFreeBetCost());

        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequestForPlaceBet(placeBetRequest, context);

        BalanceAdjustmentResponse balanceAdjustmentResponse;
        try {
            balanceAdjustmentResponse = casinoClientService.multiBetV1(balanceAdjustmentRequest, EN_GB_LOCALE);
        } catch (Status484WeeklyLossLimitReachedException | Status492DailyLossLimitReachedException | Status493MonthlyLossLimitReachedException e) {
            BigDecimal balance = getPlayerBalance(domainName, placeBetRequest);
            throw new LossLimitReachedException(e, e.getMessage(), balance, placeBetRequest.getCurrencyCode());
        } catch (Status478TimeSlotLimitException | Status438PlayTimeLimitReachedException e) {
            BigDecimal balance = getPlayerBalance(domainName, placeBetRequest);
            throw new SessionLengthLimitReachedException(e, e.getMessage(), balance, placeBetRequest.getCurrencyCode());
        }

        GameRoundUtils.validateBalanceAdjustmentResponse(balanceAdjustmentResponse, placeBetRequest.getCurrencyCode());
        BigDecimal balanceAmount = BalanceUtils.convertToCurrencyUnit(balanceAdjustmentResponse.getBalanceCents());

        return GameRoundUtils.buildPlaceBetResponse(placeBetRequest, balanceAmount);
    }

    @Override
    public BalanceResponse end(EndRequest endRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        try {
            LastBetResultResponse lastBetResultResponse = casinoClientService
                    .findLastBetResult(domain.getName(), domain.getName() + "/" + moduleInfo.getModuleName(), endRequest.getGameRoundId());
            if (lastBetResultResponse == null) {
                awardWinningsLoss(endRequest, domainName);
            }
            else {
                LoginEvent loginEvent = GameRoundUtils.validGatewaySessionToken(lithiumClientUtils, endRequest);

                if (loginEvent != null) {
                    GameRoundUtils.checkUserGuidValidity(endRequest, loginEvent, AccountNotFoundException.class);
                }

                casinoClientService
                        .completeBetRound(domain.getName(), domain.getName() + "/" + moduleInfo.getModuleName(), endRequest.getGameRoundId());
            }
        } catch (Status474BetRoundNotFoundException e) {
            final String gameRoundNotFoundMessage = String.format("GameRound %s is not found for player %s", endRequest.getGameRoundId(),
                                                                  endRequest.getOperatorAccountId());

            BigDecimal balance = getPlayerBalance(domainName, endRequest);
            throw new GameRoundNotFoundException(gameRoundNotFoundMessage, balance, endRequest.getCurrencyCode(), e);
        }

        BigDecimal balance = casinoClientService.getPlayerBalance(domain.getName(), endRequest.getOperatorAccountId(), domain.getCurrency())
                                                .getBalance();

        return buildBalanceResponse(domain, balance);
    }

    private void awardWinningsLoss(EndRequest endRequest, String domainName) throws LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException, Status511UpstreamServiceUnavailableException {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AwardWinningsRequest awardWinningsRequest = objectMapper.convertValue(endRequest, AwardWinningsRequest.class);
        awardWinningsRequest.setEndRound(true);
        awardWinningsRequest.setJackpotWinnings(BigDecimal.ZERO);
        awardWinningsRequest.setAmount(BigDecimal.ZERO);
        awardWinningsRequest.setStartRound(false);
        awardWinningsRequest.setGameRoundTransactionId(UUID.randomUUID() + "_AUTO");

        awardWinnings(awardWinningsRequest, domainName);
    }

    @Override
    public GameRoundResponse awardWinnings(AwardWinningsRequest awardWinningsRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException
    {
        LoginEvent loginEvent = GameRoundUtils.validGatewaySessionToken(lithiumClientUtils, awardWinningsRequest);

        if (loginEvent != null) {
            GameRoundUtils.checkUserGuidValidity(awardWinningsRequest, loginEvent, AccountNotFoundException.class);
        }

        Game game = getGame(awardWinningsRequest, domainName);
        Long loginEventId = Optional.ofNullable(loginEvent).map(LoginEvent::getId).orElse(null);

        GameRoundContext context = buildGameRoundContext(awardWinningsRequest, game, awardWinningsRequest.getEndRound(), loginEventId);
        context.setAmount(awardWinningsRequest.getAmount());
        context.setJackpotWinnings(awardWinningsRequest.getJackpotWinnings());

        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequestForAwardWinnings(awardWinningsRequest,
                                                                                                          context);

        BalanceAdjustmentResponse balanceAdjustmentResponse = casinoClientService.multiBetV1(balanceAdjustmentRequest, EN_GB_LOCALE);
        GameRoundUtils.validateBalanceAdjustmentResponse(balanceAdjustmentResponse);
        BigDecimal balanceAmount = BalanceUtils.convertToCurrencyUnit(balanceAdjustmentResponse.getBalanceCents());

        return GameRoundUtils.buildGameRoundResponse(awardWinningsRequest, balanceAmount);
    }

    @Override
    public GameRoundResponse rollBackBet(RollBackBetRequest rollBackBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException
    {
        LoginEvent loginEvent = GameRoundUtils.validGatewaySessionToken(lithiumClientUtils, rollBackBetRequest);

        if (loginEvent != null) {
            GameRoundUtils.checkUserGuidValidity(rollBackBetRequest, loginEvent, AccountNotFoundException.class);
        }

        Game game = getGame(rollBackBetRequest, domainName);

        Long loginEventId = Optional.ofNullable(loginEvent).map(LoginEvent::getId).orElse(null);
        GameRoundContext gameRoundContext = buildGameRoundContext(rollBackBetRequest, game, rollBackBetRequest.getEndRound(), loginEventId);
        gameRoundContext.setAmount(rollBackBetRequest.getAmount());
        gameRoundContext.setFreeBetCost(rollBackBetRequest.getFreeBetCost());
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequestForRollBackBet(rollBackBetRequest,
                                                                                                        gameRoundContext);

        BalanceAdjustmentResponse balanceAdjustmentResponse = casinoClientService.multiBetV1(balanceAdjustmentRequest, EN_GB_LOCALE);
        try {
            GameRoundUtils.validateBalanceAdjustmentResponse(balanceAdjustmentResponse);
        } catch (TransactionNotFoundException e) {
            String transactionNotFoundMessage = String.format("transaction %s not found",
                                                              rollBackBetRequest.getOriginalBetGameRoundTransactionId());
            BigDecimal balance = getPlayerBalance(domainName, rollBackBetRequest);
            throw new TransactionNotFoundException(transactionNotFoundMessage, e, balance, rollBackBetRequest.getCurrencyCode());
        }
        BigDecimal balanceAmount = BalanceUtils.convertToCurrencyUnit(balanceAdjustmentResponse.getBalanceCents());

        return GameRoundUtils.buildGameRoundResponse(rollBackBetRequest, balanceAmount);
    }

    @Override
    public GameRoundResponse voidBet(VoidBetRequest voidBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException
    {
        LoginEvent loginEvent = GameRoundUtils.validGatewaySessionToken(lithiumClientUtils, voidBetRequest);

        if (loginEvent != null) {
            GameRoundUtils.checkUserGuidValidity(voidBetRequest, loginEvent, AccountNotFoundException.class);
        }

        cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Game game = getGame(voidBetRequest, domainName);

        Long loginEventId = Optional.ofNullable(loginEvent).map(LoginEvent::getId).orElse(null);
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequestForVoidBet(voidBetRequest, loginEventId,
                                                                                                    domainName, game.getGuid());

        BalanceAdjustmentResponse balanceAdjustmentResponse = casinoClientService.multiBetV1(balanceAdjustmentRequest, EN_GB_LOCALE);
        GameRoundUtils.validateBalanceAdjustmentResponse(balanceAdjustmentResponse);
        BigDecimal balanceAmount = BalanceUtils.convertToCurrencyUnit(balanceAdjustmentResponse.getBalanceCents());

        return GameRoundUtils.buildGameRoundResponse(voidBetRequest, balanceAmount);
    }

    @Override
    public GameRoundResponse credit(CreditRequest creditRequest, String domainName) throws ErrorCodeException {
        GameRoundUtils.validateAccountTransactionTypeId(creditRequest);

        cachingDomainClientService.retrieveDomainFromDomainService(domainName);

        if (creditRequest.getAmount().compareTo(ZERO_AMOUNT) == 0) {
            BigDecimal balance = getPlayerBalance(domainName, creditRequest);
            return GameRoundUtils.buildGameRoundResponse(creditRequest, balance);
        }

        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequestForCredit(creditRequest, domainName);

        BalanceAdjustmentResponse balanceAdjustmentResponse = casinoClientService.multiBetV1(balanceAdjustmentRequest, EN_GB_LOCALE);
        GameRoundUtils.validateBalanceAdjustmentResponse(balanceAdjustmentResponse);
        BigDecimal balanceAmount = BalanceUtils.convertToCurrencyUnit(balanceAdjustmentResponse.getBalanceCents());

        return GameRoundUtils.buildGameRoundResponse(creditRequest, balanceAmount);
    }

    private Game getGame(GameRoundRequest gameRoundRequest, String domainName) {
        String gameGuid = moduleInfo.getModuleName() + "_" + gameRoundRequest.getGameId();
        Response<Game> gameResponse = null;
        try {
            gameResponse = lithiumClientUtils.getGamesClient().findByGuidAndDomainNameNoLabels(domainName, gameGuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (gameResponse != null && gameResponse.isSuccessful() && gameResponse.getData() != null) {
            return gameResponse.getData();
        }

        final String invalidGameMessage = String.format("GameGuid=%s is not configured for domain=%s", gameGuid, domainName);

        BigDecimal balance = getPlayerBalance(domainName, gameRoundRequest);
        throw new InvalidGameException(invalidGameMessage, balance, gameRoundRequest.getCurrencyCode());
    }

    private BigDecimal getPlayerBalance(String domainName, CreditRequest creditRequest) {
        return getPlayerBalance(domainName, creditRequest.getOperatorAccountId(), creditRequest.getCurrencyCode());
    }

    private BigDecimal getPlayerBalance(String domainName, GameRoundRequest gameRoundRequest) {
        return getPlayerBalance(domainName, gameRoundRequest.getOperatorAccountId(), gameRoundRequest.getCurrencyCode());
    }

    private BigDecimal getPlayerBalance(String domainName, EndRequest endRequest) {
        return getPlayerBalance(domainName, endRequest.getOperatorAccountId(), endRequest.getCurrencyCode());
    }

    private BigDecimal getPlayerBalance(String domainName, String operatorAccountId, String currencyCode) {
        try {
            return casinoClientService.getPlayerBalance(domainName, operatorAccountId, currencyCode).getBalance();
        } catch (Status500UnhandledCasinoClientException e) {
            throw new InternalServerErrorException(e);
        }
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequestForPlaceBet(PlaceBetRequest placeBetRequest, GameRoundContext gameRoundContext) {

        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequest(placeBetRequest, placeBetRequest.getEndRound(),
                                                                                          gameRoundContext.getLoginEventId(), gameRoundContext.getDomainName(), gameRoundContext.getGameGuid());

        PlayerRewardTypeHistory playerRewardTypeHistory = gameRoundContext.getPlayerRewardTypeHistory();
        String gameRoundTransactionId = gameRoundContext.getGameRoundTransactionId();
        BigDecimal jackpotContribution = placeBetRequest.getJackpotContribution();
        BigDecimal amount = gameRoundContext.getAmount();
        balanceAdjustmentRequest.setPlayerRewardTypeHistoryId(gameRoundContext.getPlayerRewardTypeHistoryId());

        if (jackpotContribution != null && jackpotContribution.compareTo(new BigDecimal(0)) == 1) {
            BalanceAdjustmentComponent balanceAdjustmentComponent = BalanceAdjustmentComponent.builder()
                    .betTransactionId(placeBetRequest.getGameRoundTransactionId())
                    .transactionIdLabelOverride(placeBetRequest.getGameRoundTransactionId())
                    .adjustmentType(EBalanceAdjustmentComponentType.JACKPOT_ACCRUAL)
                    .amount(BalanceUtils.convertJackpotContributionToCurrencyCent(placeBetRequest.getJackpotContribution()))
                    .build();
            balanceAdjustmentRequest.getAdjustmentComponentList().add(balanceAdjustmentComponent);
        }

        BalanceAdjustmentComponent betBalanceAdjustment = BalanceAdjustmentComponent.builder()
                .betTransactionId(gameRoundTransactionId)
                .amount(BalanceUtils.convertToCurrencyCent(amount))
                .build();
        betBalanceAdjustment.setLabelValues(new String[]{ MessageFormat.format("{0}={1}", CasinoTransactionLabels.GAME_PROVIDER_ID, placeBetRequest.getContentGameProviderId())});
        if (!gameRoundContext.isFreePlay()) {
            betBalanceAdjustment.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_BET);
            betBalanceAdjustment.setTransactionIdLabelOverride(MessageFormat.format("{0}_DEBIT", gameRoundTransactionId));

            if (gameRoundContext.hasFreeGame()) {
                betBalanceAdjustment.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_FREEGAME);
                betBalanceAdjustment.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEGAME_DEBIT", gameRoundTransactionId));
            }
        } else {

            betBalanceAdjustment.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEROUND_DEBIT", gameRoundTransactionId));
            betBalanceAdjustment.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET);

            if (playerRewardTypeHistory != null) {
                RewardType rewardType = playerRewardTypeHistory.getRewardRevisionType().getRewardType();

                String rewardFullSuffix = ((rewardType.getCode()!=null)?(MessageFormat.format("{0}_", rewardType.getCode().toUpperCase())):"") + rewardType.getName().toUpperCase();
                betBalanceAdjustment.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET);
                betBalanceAdjustment.setAccountCodeSuffix(rewardFullSuffix);
                betBalanceAdjustment.setTransactionIdLabelOverride(MessageFormat.format("{0}_{1}_DEBIT", gameRoundTransactionId, rewardType.getName().toUpperCase()));
                betBalanceAdjustment.setAmount(BalanceUtils.convertToCurrencyCent(amount));
            }
        }

        balanceAdjustmentRequest.getAdjustmentComponentList().add(betBalanceAdjustment);

        if (placeBetRequest.getEndRound()) {
            BalanceAdjustmentComponent awardWinningsBalanceAdjustmentComponent = buildLossAdjustmentComponent(gameRoundContext);
            balanceAdjustmentRequest.getAdjustmentComponentList().add(awardWinningsBalanceAdjustmentComponent);
        }

        return balanceAdjustmentRequest;
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequestForVoidBet(VoidBetRequest voidBetRequest, Long loginEventId,
                                                                             String domainName, String gameGuid) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequest(voidBetRequest, voidBetRequest.getEndRound(),
                                                                                          loginEventId, domainName, gameGuid);

        balanceAdjustmentRequest.getAdjustmentComponentList()
                                .add(buildBalanceAdjustmentComponent(EBalanceAdjustmentComponentType.CASINO_VOID,
                                                                     voidBetRequest.getGameRoundTransactionId(),
                                                                     voidBetRequest.getAmount()));
        return balanceAdjustmentRequest;
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequestForAwardWinnings(AwardWinningsRequest awardWinningsRequest,
                                                                                   GameRoundContext gameRoundContext) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequest(awardWinningsRequest,
                                                                                          awardWinningsRequest.getEndRound(),
                gameRoundContext.getLoginEventId(), gameRoundContext.getDomainName(), gameRoundContext.getGameGuid());

        balanceAdjustmentRequest.setPlayerRewardTypeHistoryId(gameRoundContext.getPlayerRewardTypeHistoryId());

        BalanceAdjustmentComponent balanceAdjustmentComponent;

        if (gameRoundContext.isWin()) {
            balanceAdjustmentComponent = buildWinAdjustmentComponent(gameRoundContext);
        } else {
            balanceAdjustmentComponent = buildLossAdjustmentComponent(gameRoundContext);
        }
        balanceAdjustmentComponent.setLabelValues(new String[]{ MessageFormat.format("{0}={1}", CasinoTransactionLabels.GAME_PROVIDER_ID, awardWinningsRequest.getContentGameProviderId())});
        balanceAdjustmentRequest.getAdjustmentComponentList().add(balanceAdjustmentComponent);
        return balanceAdjustmentRequest;
    }

    private static BalanceAdjustmentComponent buildAwardWinningsBalanceAdjustmentComponent(BigDecimal amount, BigDecimal jackpotWinnings, Boolean endRound, String gameRoundTransactionId) {
        EBalanceAdjustmentComponentType balanceAdjustmentComponentType;
        if (jackpotWinnings != null && endRound &&
                jackpotWinnings.compareTo(ZERO_AMOUNT) > 0) {
            balanceAdjustmentComponentType = EBalanceAdjustmentComponentType.CASINO_WIN_JACKPOT;
        } else {
            balanceAdjustmentComponentType = amount.compareTo(ZERO_AMOUNT) == 0 ?
                    EBalanceAdjustmentComponentType.CASINO_LOSS : EBalanceAdjustmentComponentType.CASINO_WIN;
        }
        BalanceAdjustmentComponent adjustmentComponent = buildBalanceAdjustmentComponent(balanceAdjustmentComponentType,
                gameRoundTransactionId, amount);
        return adjustmentComponent;
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequestForRollBackBet(RollBackBetRequest rollBackBetRequest, GameRoundContext gameRoundContext) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequest(rollBackBetRequest,
                                                                                          rollBackBetRequest.getEndRound(), gameRoundContext.getLoginEventId(),
                gameRoundContext.getDomainName(), gameRoundContext.getGameGuid());

        String gameRoundTransactionId = gameRoundContext.getGameRoundTransactionId();
        PlayerRewardTypeHistory playerRewardTypeHistory = gameRoundContext.getPlayerRewardTypeHistory();
        Long amount = BalanceUtils.convertToCurrencyCent(gameRoundContext.getAmount());

        balanceAdjustmentRequest.setPlayerRewardTypeHistoryId(gameRoundContext.getPlayerRewardTypeHistoryId());

        BalanceAdjustmentComponent balanceAdjustmentComponent = BalanceAdjustmentComponent.builder()
                .betTransactionId(gameRoundTransactionId)
                .amount(amount)
                .transactionIdLabelOverride(MessageFormat.format("{0}_CANCEL_DEBIT", gameRoundTransactionId))
                .reversalBetTransactionId(MessageFormat.format("{0}_DEBIT", rollBackBetRequest.getOriginalBetGameRoundTransactionId()))
                .adjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_REVERSAL)
                .build();

        if (gameRoundContext.isFreePlay()) {
            balanceAdjustmentComponent.setReversalBetTransactionId(MessageFormat.format("{0}_FREEROUND_DEBIT", rollBackBetRequest.getOriginalBetGameRoundTransactionId()));
            balanceAdjustmentComponent.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET_REVERSAL);
            balanceAdjustmentRequest.setTransactionId(MessageFormat.format("{0}_CANCEL_FREEROUND_DEBIT", gameRoundTransactionId));

            if (playerRewardTypeHistory != null) {
                RewardType rewardComponent = playerRewardTypeHistory.getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rewardComponent.getCode()!=null)?(rewardComponent.getCode().toUpperCase() + "_"):"") + rewardComponent.getName().toUpperCase();
                balanceAdjustmentComponent.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET_REVERSAL);
                balanceAdjustmentComponent.setAccountCodeSuffix(rewardFullSuffix);
                balanceAdjustmentComponent.setTransactionIdLabelOverride(MessageFormat.format("{0}_{1}_CANCEL", gameRoundTransactionId, rewardComponent.getName().toUpperCase()));
                balanceAdjustmentComponent.setAmount(amount);
                balanceAdjustmentComponent.setReversalBetTransactionId(MessageFormat.format("{0}_{1}_DEBIT", rollBackBetRequest.getOriginalBetGameRoundTransactionId(), rewardComponent.getName().toUpperCase()));
            }
        } else if (gameRoundContext.hasFreeGame()) {
            balanceAdjustmentComponent.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_FREEGAME_REVERSAL);
            balanceAdjustmentComponent.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEGAME_DEBIT_CANCEL", gameRoundTransactionId));
            balanceAdjustmentComponent.setReversalBetTransactionId(MessageFormat.format("{0}_FREEGAME_DEBIT", rollBackBetRequest.getOriginalBetGameRoundTransactionId()));
        }

        balanceAdjustmentRequest.getAdjustmentComponentList().add(balanceAdjustmentComponent);
        return balanceAdjustmentRequest;
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequestForCredit(CreditRequest creditRequest, String domainName) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBalanceAdjustmentRequest(creditRequest, domainName);
        BalanceAdjustmentComponent adjustmentComponent = buildBalanceAdjustmentComponentForCredit(creditRequest.getAmount(),
                                                                                                  creditRequest.getAccountTransactionId(),
                                                                                                  creditRequest.getAccountTransactionTypeId());

        balanceAdjustmentRequest.getAdjustmentComponentList().add(adjustmentComponent);
        return balanceAdjustmentRequest;
    }

    private BalanceAdjustmentRequest buildBalanceAdjustmentRequest(CreditRequest creditRequest, String domainName) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBasicBalanceAdjustmentRequest(false, domainName);
        balanceAdjustmentRequest.setCurrencyCode(creditRequest.getCurrencyCode());
        balanceAdjustmentRequest.setUserGuid(creditRequest.getOperatorAccountId());
        balanceAdjustmentRequest.setSessionId(-1L);
        return balanceAdjustmentRequest;
    }

    private <T extends GameRoundRequest> BalanceAdjustmentRequest buildBalanceAdjustmentRequest(T gameRoundRequest, Boolean endRound,
                                                                                                Long loginEventId,
                                                                                                String domainName, String gameGuid) {

        BalanceAdjustmentRequest balanceAdjustmentRequest = buildBasicBalanceAdjustmentRequest(endRound, domainName);
        balanceAdjustmentRequest.setRoundId(gameRoundRequest.getGameRoundId());
        balanceAdjustmentRequest.setGameGuid(domainName + "/" + gameGuid);
        balanceAdjustmentRequest.setCurrencyCode(gameRoundRequest.getCurrencyCode());
        balanceAdjustmentRequest.setUserGuid(gameRoundRequest.getOperatorAccountId());
        balanceAdjustmentRequest.setGameSessionId(gameRoundRequest.getGatewaySessionToken());
        balanceAdjustmentRequest.setSessionId(loginEventId == null ? -1 : loginEventId);

        return balanceAdjustmentRequest;
    }

    private BalanceAdjustmentRequest buildBasicBalanceAdjustmentRequest(Boolean endRound, String domainName) {
        BalanceAdjustmentRequest balanceAdjustmentRequest = new BalanceAdjustmentRequest();
        balanceAdjustmentRequest.setRoundFinished(endRound);
        balanceAdjustmentRequest.setBonusTran(false);
        balanceAdjustmentRequest.setBonusId(-1);
        balanceAdjustmentRequest.setDomainName(domainName);
        balanceAdjustmentRequest.setProviderGuid(domainName + "/" + moduleInfo.getModuleName());
        balanceAdjustmentRequest.setTransactionTiebackId(null);
        balanceAdjustmentRequest.setRealMoneyOnly(true);
        balanceAdjustmentRequest.setAllowNegativeBalanceAdjustment(false);
        balanceAdjustmentRequest.setPerformAccessChecks(true);
        balanceAdjustmentRequest.setPersistRound(true);
        balanceAdjustmentRequest.setAdjustmentComponentList(new ArrayList<>());
        return balanceAdjustmentRequest;
    }

    private static BalanceAdjustmentComponent buildBalanceAdjustmentComponent(
            EBalanceAdjustmentComponentType balanceAdjustmentComponentType,
            String gameRoundTransactionId, BigDecimal amount) {

        return BalanceAdjustmentComponent.builder()
                                         .adjustmentType(balanceAdjustmentComponentType)
                                         .amount(BalanceUtils.convertToCurrencyCent(amount))
                                         .betTransactionId(gameRoundTransactionId)
                                         .transactionIdLabelOverride(gameRoundTransactionId)
                                         .build();
    }

    private static BalanceAdjustmentComponent buildBalanceAdjustmentComponentForRollBackBet(String gameRoundTransactionId,
                                                                                            BigDecimal amount,
                                                                                            String reversalTransactionId) {

        return BalanceAdjustmentComponent.builder()
                                         .adjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_REVERSAL)
                                         .amount(BalanceUtils.convertToCurrencyCent(amount))
                                         .betTransactionId(gameRoundTransactionId)
                                         .transactionIdLabelOverride(gameRoundTransactionId)
                                         .reversalBetTransactionId(reversalTransactionId)
                                         .build();
    }

    private static BalanceAdjustmentComponent buildBalanceAdjustmentComponentForCredit(BigDecimal amount, String accountTransactionId,
                                                                                       String accountTransactionTypeId) {
        return BalanceAdjustmentComponent.builder()
                                         .adjustmentType(EBalanceAdjustmentComponentType.CASINO_ADHOC_CREDIT)
                                         .amount(BalanceUtils.convertToCurrencyCent(amount))
                                         .betTransactionId(accountTransactionId)
                                         .transactionIdLabelOverride(accountTransactionId)
                                         .additionalReference(accountTransactionTypeId)
                                         .build();

    }

    private void populatePlayerRewardTypeHistory(GameRoundContext gameRoundContext) {
        if (gameRoundContext.isFreePlay()) {
            PlayerRewardTypeHistory playerRewardTypeHistory;

            if (StringUtil.isNumeric(gameRoundContext.getFreeGameOfferCode())) {
                playerRewardTypeHistory = queryRewardClientService.findById(Long.parseLong(gameRoundContext.getFreeGameOfferCode()));
            } else {
                playerRewardTypeHistory = queryRewardClientService.findByRewardTypeReference(gameRoundContext.getFreeGameOfferCode());
            }

            gameRoundContext.setPlayerRewardTypeHistory(playerRewardTypeHistory);
        }
    }

    private GameRoundContext buildGameRoundContext(GameRoundRequest gameRoundRequest, Game game, Boolean endRound, Long loginEventId) {

        GameRoundContext gameRoundContext = GameRoundContext.builder()
                .game(game)
                .domainName(game.getDomain().getName())
                .gameRoundId(gameRoundRequest.getGameRoundId())
                .gameRoundTransactionId(gameRoundRequest.getGameRoundTransactionId())
                .currencyCode(gameRoundRequest.getCurrencyCode())
                .freeGameOfferCode(gameRoundRequest.getFreeGameOfferCode())
                .gatewaySessionToken(gameRoundRequest.getGatewaySessionToken())
                .operatorAccountId(gameRoundRequest.getOperatorAccountId())
                .endRound(BooleanUtils.toBoolean(endRound))
                .loginEventId(loginEventId)
                .build();

        populatePlayerRewardTypeHistory(gameRoundContext);

        return gameRoundContext;
    }

    public BalanceAdjustmentComponent buildLossAdjustmentComponent(GameRoundContext gameRoundContext) {

        String gameRoundTransactionId = gameRoundContext.getGameRoundTransactionId();
        PlayerRewardTypeHistory playerRewardTypeHistory = gameRoundContext.getPlayerRewardTypeHistory();

        BalanceAdjustmentComponent adjustmentCompletion = BalanceAdjustmentComponent.builder()
                .betTransactionId(gameRoundTransactionId)
                .transactionIdLabelOverride(MessageFormat.format("{0}_LOSS", gameRoundTransactionId))
                .amount(0L)
                .build();

        if (gameRoundContext.isFreePlay()) {
            adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_LOSS);
            adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEROUND_LOSS", gameRoundTransactionId));

            if (playerRewardTypeHistory != null) {
                RewardType rt = playerRewardTypeHistory.getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
                adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_LOSS);
                adjustmentCompletion.setAccountCodeSuffix(rewardFullSuffix);
                adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_{1}_LOSS", gameRoundTransactionId , rt.getName().toUpperCase()));
                return adjustmentCompletion;
            }
        } else {
            adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_LOSS);

            if (gameRoundContext.hasFreeGame()) {
                adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_LOSS_FREEGAME);
                adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEGAME_LOSS", gameRoundTransactionId));
            }
        }

        return adjustmentCompletion;
    }

    public BalanceAdjustmentComponent buildWinAdjustmentComponent(GameRoundContext gameRoundContext) {

        String gameRoundTransactionId = gameRoundContext.getGameRoundTransactionId();
        PlayerRewardTypeHistory playerRewardTypeHistory = gameRoundContext.getPlayerRewardTypeHistory();

        BalanceAdjustmentComponent adjustmentCompletion = BalanceAdjustmentComponent.builder()
                .betTransactionId(gameRoundTransactionId)
                .transactionIdLabelOverride(MessageFormat.format("{0}_CREDIT", gameRoundTransactionId))
                .amount(BalanceUtils.convertToCurrencyCent(gameRoundContext.getAmount()))
                .build();

        if (gameRoundContext.isFreePlay()) {
            adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN);
            adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEROUND_CREDIT", gameRoundTransactionId));

            if (playerRewardTypeHistory != null) {
                RewardType rewardComponent = playerRewardTypeHistory.getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rewardComponent.getCode()!=null)?(rewardComponent.getCode().toUpperCase() + "_"):"") + rewardComponent.getName().toUpperCase();
                adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_WIN);
                adjustmentCompletion.setAccountCodeSuffix(rewardFullSuffix);
                adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_{1}_CREDIT", gameRoundTransactionId , rewardComponent.getName().toUpperCase()));
                return adjustmentCompletion;
            }
        } else {
            adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN);
            if (gameRoundContext.hasJackpotWinnings()) {
                adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN_JACKPOT);
                adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_JACKPOT_CREDIT", gameRoundTransactionId));
                return adjustmentCompletion;
            }

            if (gameRoundContext.hasFreeGame()) {
                adjustmentCompletion.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN_FREEGAME);
                adjustmentCompletion.setTransactionIdLabelOverride(MessageFormat.format("{0}_FREEGAME_CREDIT", gameRoundTransactionId));
            }
        }

        return adjustmentCompletion;
    }
}
