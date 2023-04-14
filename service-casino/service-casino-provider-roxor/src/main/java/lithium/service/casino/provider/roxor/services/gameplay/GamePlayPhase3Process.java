package lithium.service.casino.provider.roxor.services.gameplay;

import java.util.ArrayList;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTransactionLabels;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TypeEnum;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.storage.repositories.RewardBonusMapRepository;
import lithium.service.client.LithiumServiceClientFactory;
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
import lithium.service.reward.client.dto.RewardType;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GamePlayPhase3Process {
    @Autowired ModuleInfo moduleInfo;
    @Autowired LithiumServiceClientFactory services;
    @Autowired @Setter CasinoClientService casinoService;
    @Autowired OperationRepository operationRepository;
    @Autowired RewardBonusMapRepository rewardBonusMapRepository;

    public void processGamePlay(GamePlayContext context)
            throws
            Status511UpstreamServiceUnavailableException,
            Status471InsufficientFundsException,
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status474DomainProviderDisabledException,
            Status473DomainBettingDisabledException,
            Status550ServiceDomainClientException,
            Status500RuntimeException, Status485WeeklyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status496PlayerCoolingOffException,
            Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
            Status493MonthlyLossLimitReachedException, Status492DailyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status484WeeklyLossLimitReachedException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {
        SW.start("gameplay.process.callservicecasino.handle." + context.getGamePlayId());

        try {
            BalanceAdjustmentRequest casinoRequest = new BalanceAdjustmentRequest();
            casinoRequest.setRoundId(context.getGamePlayId());
            casinoRequest.setExternalTimestamp(context.getExternalTimestamp());
            casinoRequest.setGameGuid(context.getDomain().getName() + "/" + context.getLithiumGame().getGuid());
            casinoRequest.setRoundFinished(context.getRoxorFinishPresent());
            casinoRequest.setBonusTran(false);
            casinoRequest.setBonusId(-1);
            casinoRequest.setDomainName(context.getDomain().getName());
            casinoRequest.setCurrencyCode(context.getDomain().getCurrency());
            casinoRequest.setProviderGuid(context.getDomain().getName() + "/" + moduleInfo.getModuleName());
            casinoRequest.setUserGuid(context.getUserGuid());
            casinoRequest.setTransactionTiebackId(null); //TODO transferId -> Roxor GamePlayID?
            casinoRequest.setRealMoneyOnly(true);
            casinoRequest.setAllowNegativeBalanceAdjustment(context.getAllowNegativeBalances());
            casinoRequest.setGameSessionId(context.getSessionKey());
            casinoRequest.setPerformAccessChecks(false);
            casinoRequest.setPersistRound(true);
            casinoRequest.setSessionId((context.getLoginEvent() != null) ? context.getLoginEvent().getId() : -1L);

            if (context.getPlayerRewardTypeHistory() != null) {
                casinoRequest.setPlayerRewardTypeHistoryId(context.getPlayerRewardTypeHistory().getId());
            }

            ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();

            for (Operation gamePlayOperation : context.getOperationEntityList()) {
                String betTransactionId = String.valueOf(gamePlayOperation.getId());

                OperationTypeEnum operationTypeEnum = OperationTypeEnum.valueOf(gamePlayOperation.getOperationType().getCode());

                if (gamePlayOperation.getStatus().equals(Operation.Status.IGNORE) ||
                    gamePlayOperation.getStatus().equals(Operation.Status.DUPLICATE)) {
                    continue;
                }

                switch (operationTypeEnum) {
                    case START_GAME_PLAY: {
                        gamePlayOperation.setStatus(Operation.Status.RESULT);
                        break;
                    }
                    case FINISH_GAME_PLAY: {
                        gamePlayOperation.setStatus(Operation.Status.RESULT);
                        BalanceAdjustmentComponent bac = buildFinishGameAdjustmentComponent(context, betTransactionId, gamePlayOperation);
                        if (bac != null) adjustmentComponents.add(bac);
                        break;
                    }
                    case TRANSFER: {
                        TypeEnum typeEnum = TypeEnum.valueOf(gamePlayOperation.getType().getCode());
                        gamePlayOperation.setStatus(Operation.Status.PROCESSING);
                        switch (typeEnum) {
                            case DEBIT: {
                                adjustmentComponents.add(buildTransferDebitAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                                break;
                            }
                            case CREDIT: {
                                setupBonusAndRewardHistory(context, casinoRequest);
                                adjustmentComponents.add(buildTransferCreditAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                                break;
                            }
                            case JACKPOT_CREDIT: {
                                // DEBIT || FREEPLAY
                                //freeplay operationType to CASINO_WIN_FREESPIN done...

                                EBalanceAdjustmentComponentType adjustmentType = (context.getFreePlayExists())
                                    ? EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_JACKPOT
                                    : EBalanceAdjustmentComponentType.CASINO_WIN_JACKPOT; //debit operationType --map to CASINO_WIN (default) done

                                adjustmentComponents.add(
                                    BalanceAdjustmentComponent.builder()
                                        .betTransactionId(betTransactionId)
                                        .adjustmentType(adjustmentType)
                                        .amount(gamePlayOperation.getAmountCents())
                                        .transactionIdLabelOverride(gamePlayOperation.getId() + "_JACKPOT_CREDIT")
                                        .additionalReference(gamePlayOperation.getTransferId())
                                        .build()
                                );
                                break;
                            }
                        }
                        break;
                    }
                    case CANCEL_TRANSFER: {
                        TypeEnum typeEnum = TypeEnum.valueOf(gamePlayOperation.getType().getCode());
                        gamePlayOperation.setStatus(Operation.Status.PROCESSING);

                        switch (typeEnum) {
                            case DEBIT: {
                                adjustmentComponents.add(buildTransferDebitCancelAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                                break;
                            }
                            case CREDIT: {
                                setupBonusAndRewardHistory(context, casinoRequest);
                                adjustmentComponents.add(buildTransferCreditCancelAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                                break;
                            }
                            case JACKPOT_CREDIT: {
                                EBalanceAdjustmentComponentType adjustmentType = (context.getFreePlayExists())
                                    ? EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_JACKPOT_REVERSAL
                                    : EBalanceAdjustmentComponentType.CASINO_WIN_JACKPOT_REVERSAL;
                                adjustmentComponents.add(
                                    BalanceAdjustmentComponent.builder()
                                        .betTransactionId(betTransactionId)
                                        .adjustmentType(adjustmentType)
                                        .amount(gamePlayOperation.getAmountCents())
                                        .transactionIdLabelOverride(gamePlayOperation.getId() + "_CANCEL_JACKPOT_CREDIT")
                                        .additionalReference(gamePlayOperation.getTransferId())
                                        .reversalBetTransactionId(gamePlayOperation.getAssociatedOperation().getId() + "_JACKPOT_CREDIT")
                                        .build()
                                );
                                break;
                            }
                        }
                        break;
                    }
                    case ACCRUAL: {
                        BalanceAdjustmentComponent balanceAdjustmentComponent = BalanceAdjustmentComponent.builder()
                            .betTransactionId(betTransactionId)
                            .adjustmentType(EBalanceAdjustmentComponentType.JACKPOT_ACCRUAL)
                            .amount(gamePlayOperation.getAmountCents())
                            .transactionIdLabelOverride(gamePlayOperation.getId()+"_ACCRUAL")
                            .additionalReference(gamePlayOperation.getAccrualId())
                            .build();
                        balanceAdjustmentComponent.setLabelValues(new String[] {
                            CasinoTransactionLabels.ACCRUAL_ID+"="+gamePlayOperation.getAccrualId(),
                            CasinoTransactionLabels.PLATFORM_CODE+"="+gamePlayOperation.getGamePlay().getPlatform().getCode()
                        });
                        adjustmentComponents.add(balanceAdjustmentComponent);
                        break;
                    }
                    case CANCEL_ACCRUAL: {
                        BalanceAdjustmentComponent balanceAdjustmentComponent = BalanceAdjustmentComponent.builder()
                            .betTransactionId(betTransactionId)
                            .adjustmentType(EBalanceAdjustmentComponentType.JACKPOT_ACCRUAL_CANCEL)
                            .amount(gamePlayOperation.getAmountCents())
                            .transactionIdLabelOverride(gamePlayOperation.getId()+"_ACCRUAL_CANCEL")
                            .additionalReference(gamePlayOperation.getAccrualId())
                            .build();
                        balanceAdjustmentComponent.setLabelValues(new String[] {
                            CasinoTransactionLabels.ACCRUAL_ID+"="+gamePlayOperation.getAccrualId(),
                            CasinoTransactionLabels.PLATFORM_CODE+"="+gamePlayOperation.getGamePlay().getPlatform().getCode()
                        });
                        adjustmentComponents.add(balanceAdjustmentComponent);
                        break;
                    }
                    case FREE_PLAY: {
                        gamePlayOperation.setStatus(Operation.Status.PROCESSING);
                        setupBonusAndReward(context, casinoRequest);

                        adjustmentComponents.add(buildFreePlayAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                        break;
                    }
                    case CANCEL_FREE_PLAY: {
                        gamePlayOperation.setStatus(Operation.Status.PROCESSING);
                        setupBonusAndReward(context, casinoRequest);

                        adjustmentComponents.add(buildFreePlayCancelAdjustmentComponent(context, betTransactionId, gamePlayOperation));
                        break;
                    }
                }
            }

            context.setOperationEntityList(operationRepository.saveAll(context.getOperationEntityList()));

            if (adjustmentComponents.isEmpty()) {
                if (casinoRequest.getRoundFinished() && context.getShouldCompleteBetRound()) {
                    // There are no adjustments being made, but we received a FINISH_GAME_PLAY operation and need to
                    // update the bet round to indicate that it is complete.
                    try {
                        casinoService.completeBetRound(context.getDomain().getName(), casinoRequest.getProviderGuid(),
                                casinoRequest.getRoundId());
                    } catch (Status474BetRoundNotFoundException | Status500UnhandledCasinoClientException e) {
                        log.error("Could not complete bet round [roundId="+casinoRequest.getRoundId()+"] "
                                + e.getMessage(), e);
                    }
                }
                if (context.getPlayerBalanceCheck()) {
                    Long balanceAfter = getPlayerBalance(context);
                    context.setBalanceAfter(balanceAfter);
                }
                context.setOperationOutcomeList(new ArrayList<>());
            } else {
                casinoRequest.setAdjustmentComponentList(adjustmentComponents);
                log.debug("gamePlay-Request to casino : " + casinoRequest);
                BalanceAdjustmentResponse response = casinoService.multiBetV1(casinoRequest, context.getLocale());
                log.debug("gamePlay-Response from casino : " + response);
                switch (response.getResult()) {
                    case TRANSACTION_DATA_VALIDATION_ERROR:
                    case NEGATIVE_BALANCE_ERROR:
                    case INTERNAL_ERROR: {
                        context.setGamePlayRequestErrorReason("Internal error received from Casino");
                        throw new Status500RuntimeException(context);
                    }
                    case INSUFFICIENT_FUNDS: {
                        throw new Status471InsufficientFundsException();
                    }
                    case SUCCESS: {
                        if (context.getPlayerBalanceCheck()) {
                            Long balanceAfter = response.getBalanceCents();
                            context.setBalanceAfter(balanceAfter);
                        }
                        context.setOperationOutcomeList(response.getAdjustmentResponseComponentList());
                        break;
                    }
                }
            }
        } finally {
            SW.stop();
        }
    }

    private BalanceAdjustmentComponent buildFinishGameAdjustmentComponent(GamePlayContext context, String betTransactionId, Operation gamePlayOperation) {
        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .amount(0L)
            // NOTE : no transferId supplied by roxor for start/finish
            //.additionalReference(gamePlayOperation.getTransferId())
            .build();

        if (!context.getRoxorFinishWinPresent() && context.getRoxorTransferDebitExists()) {
            if (context.getLithiumGame() != null) {
                if (BooleanUtils.isTrue(context.getLithiumGame().getFreeGame())) {
                    bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEGAME_LOSS");
                    bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_LOSS_FREEGAME);
                } else {
                    bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_LOSS");
                    bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_LOSS);
                }
            }
            return bac;
        } else if (!context.getRoxorFinishWinPresent() && context.getFreePlayExists()) {
            bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_REWARD_LOSS");
            if (context.getPlayerRewardTypeHistory() != null) {
                RewardType rt = context.getPlayerRewardTypeHistory().getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_LOSS);
                bac.setAccountCodeSuffix(rewardFullSuffix);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_" + rt.getName().toUpperCase()+"_LOSS");
                return bac;
            } else {
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_LOSS);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEROUND_LOSS");
                if ((context.getLithiumGame() != null) && (BooleanUtils.isTrue(context.getLithiumGame().getFreeGame()))) {
                    bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_LOSS);
                    bac.setAccountCodeSuffix("RX_FREESPIN_FREEGAME"); //DFG
                    bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREESPIN_FREEGAME_LOSS");
                }
                return bac;
            }
        }
        return null;
    }

    private void setupBonusAndReward(GamePlayContext context, BalanceAdjustmentRequest casinoRequest) {
        casinoRequest.setBonusId(getLithiumExtBonusId(context)); //Only used for old bonus system
        setupBonusAndRewardHistory(context, casinoRequest);
    }
    private void setupBonusAndRewardHistory(GamePlayContext context, BalanceAdjustmentRequest casinoRequest) {
        if (context.getRewardBonusMap() != null) {
            // Bonus picked up through legacy bonus system. (svc-casino)
            casinoRequest.setPlayerBonusHistoryId(context.getRewardBonusMap().getLithiumExtBonusId());
        } else if (context.getPlayerRewardTypeHistory() != null) {
            // Bonus picked up from comps-engine (svc-reward)
            casinoRequest.setPlayerRewardTypeHistoryId(context.getPlayerRewardTypeHistory().getId());
        }
    }

    private Long getPlayerBalance(GamePlayContext context) {
        try {
            return casinoService.getPlayerBalance(
                    context.getDomain().getName(),
                    context.getUserGuid(),
                    context.getDomain().getCurrency()
            ).getBalanceCents();
        } catch (Status500UnhandledCasinoClientException e) {
            log.warn("game-play-phase3 player-balance " + ExceptionMessageUtil.allMessages(e), e);
            return null;
        }
    }

    private Integer getLithiumExtBonusId(GamePlayContext context) { //TODO: should not be Integer, don't know whos brain child this was.
        //Only used for old bonus system
        if (context.getRewardBonusMap() != null) { // Old bonus awarded through svc-casino
            //TODO: Figure out if needed.
            return context.getRewardBonusMap().getLithiumExtBonusId().intValue();
        }
        return -1;
    }

    private BalanceAdjustmentComponent buildFreePlayAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation) {
        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .amount(0L)
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_FREE_PLAY")
            .additionalReference(gamePlayOperation.getTransferId())
            .build();

        if (gamePlayContext.getPlayerRewardTypeHistory() != null) {
            RewardType rt = gamePlayContext.getPlayerRewardTypeHistory().getRewardRevisionType().getRewardType();
            String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
            gamePlayContext.setSetFreePlayValue(Boolean.TRUE);
            bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET);
            bac.setAccountCodeSuffix(rewardFullSuffix);
            bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_" + rt.getName().toUpperCase()+"_DEBIT");
            bac.setAmount(gamePlayOperation.getAmountCents());
            return bac;
        }

        if (gamePlayOperation.getSource() != null) {
            if (gamePlayOperation.getSource().getSourceType().equalsIgnoreCase("DFG")) {
                gamePlayContext.setSetFreePlayValue(Boolean.TRUE);
               // bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET_FREEGAME);
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET);
                bac.setAmount(0L); //TODO: figure how the none lithium rewards are processed
                bac.setAccountCodeSuffix("RX_FREESPIN_FREEGAME");
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREESPIN_FREEGAME_DEBIT");
                return bac;
            }
        }
        bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET);
        return bac;
    }


    private BalanceAdjustmentComponent buildFreePlayCancelAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation
    ) {

        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .amount(gamePlayContext.getSetFreePlayValue() ? gamePlayOperation.getAmountCents() : 0L)
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_CANCEL_FREE_PLAY")
            .additionalReference(gamePlayOperation.getTransferId())
            .reversalBetTransactionId(gamePlayOperation.getAssociatedOperation().getId() + "_FREE_PLAY")
            .build();

        if (gamePlayContext.getPlayerRewardTypeHistory() != null) {
            RewardType rt = gamePlayContext.getPlayerRewardTypeHistory().getRewardRevisionType().getRewardType();
            String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
            gamePlayContext.setSetFreePlayValue(Boolean.TRUE);
            bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET_REVERSAL);
            bac.setAccountCodeSuffix(rewardFullSuffix);
            bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_" + rt.getName().toUpperCase()+"_CANCEL");
            bac.setAmount(gamePlayOperation.getAmountCents());
            return bac;
        }

        //non lithium DFG granted bonus
        //NOTE : DFG Granted bonus
        if (gamePlayOperation.getSource() != null) {
            if (gamePlayOperation.getSource().getSourceType().equalsIgnoreCase("DFG")) {
                gamePlayContext.setSetFreePlayValue(Boolean.TRUE);
//                return EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET_FREEGAME_REVERSAL;
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_BET_REVERSAL);
                bac.setAmount(gamePlayOperation.getAmountCents());
                bac.setAccountCodeSuffix("RX_FREESPIN_FREEGAME");
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREESPIN_FREEGAME_CANCEL");
                return bac;
            }
        }

        bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_BET_REVERSAL);
        return bac;
    }

    private BalanceAdjustmentComponent buildTransferDebitCancelAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation) {
        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .adjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_REVERSAL)
            .amount(gamePlayOperation.getAmountCents())
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_CANCEL_DEBIT")
            .additionalReference(gamePlayOperation.getTransferId())
            .reversalBetTransactionId(gamePlayOperation.getAssociatedOperation().getId() + "_DEBIT")
            .build();

        if (gamePlayContext.getLithiumGame() != null) {
            if (BooleanUtils.isTrue(gamePlayContext.getLithiumGame().getFreeGame())) {
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_FREEGAME_REVERSAL);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEGAME_DEBIT_CANCEL");
            }
        }

        return bac;
    }

    private BalanceAdjustmentComponent buildTransferDebitAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation) {
        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .adjustmentType(EBalanceAdjustmentComponentType.CASINO_BET)
            .amount(gamePlayOperation.getAmountCents())
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_DEBIT")
            .additionalReference(gamePlayOperation.getTransferId())
            .build();

        if (gamePlayContext.getLithiumGame() != null) {
            if (BooleanUtils.isTrue(gamePlayContext.getLithiumGame().getFreeGame())) {
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_BET_FREEGAME);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEGAME_DEBIT");
            }
        }
        return bac;
    }

    private BalanceAdjustmentComponent buildTransferCreditAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation) {

        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .amount(gamePlayOperation.getAmountCents())
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_CREDIT")
            .additionalReference(gamePlayOperation.getTransferId())
            .build();

        // NOTE : Credit due to free spin wager
        if (gamePlayContext.getFreePlayExists()) {
            if (gamePlayContext.getPlayerRewardTypeHistory() != null) {
                RewardType rt = gamePlayContext.getPlayerRewardTypeHistory().getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_WIN);
                bac.setAccountCodeSuffix(rewardFullSuffix);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_" + rt.getName().toUpperCase()+"_CREDIT");
                return bac;
            }

            // NOTE : DFG originating bonuses
            if (gamePlayContext.getFreePlayOperation() != null
                && gamePlayContext.getFreePlayOperation().getSource() != null
                && gamePlayContext.getFreePlayOperation().getSource().getSourceType() != null
                && gamePlayContext.getFreePlayOperation().getSource().getSourceType().equalsIgnoreCase("DFG")
            ) {
//                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_FREEGAME);
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_WIN);
                bac.setAccountCodeSuffix("RX_FREESPIN_FREEGAME");
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREESPIN_FREEGAME_CREDIT");
                return bac;
            }
            //NOTE : default to V1 if none of the above is met
            bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_REAL_MONEY_WIN);
            return bac;
        }

        //NOTE : Credit due to cash wager
        if (gamePlayContext.getLithiumGame() != null) {
            if (BooleanUtils.isTrue(gamePlayContext.getLithiumGame().getFreeGame())) {
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN_FREEGAME);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEGAME_CREDIT");
                return bac;
            }
        }

        bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN);
        return bac;
    }

    private BalanceAdjustmentComponent buildTransferCreditCancelAdjustmentComponent(
        GamePlayContext gamePlayContext, String betTransactionId, Operation gamePlayOperation) {

        BalanceAdjustmentComponent bac = BalanceAdjustmentComponent.builder()
            .betTransactionId(betTransactionId)
            .amount(gamePlayOperation.getAmountCents())
            .transactionIdLabelOverride(gamePlayOperation.getId() + "_CANCEL_CREDIT")
            .additionalReference(gamePlayOperation.getTransferId())
            .reversalBetTransactionId(gamePlayOperation.getAssociatedOperation().getId() + "_CREDIT")
            .build();

        // NOTE : Credit rollback due to free spin wager
        if (gamePlayContext.getFreePlayExists()) {
            if (gamePlayContext.getPlayerRewardTypeHistory() != null) {
                RewardType rt = gamePlayContext.getPlayerRewardTypeHistory().getRewardRevisionType().getRewardType();
                String rewardFullSuffix = ((rt.getCode()!=null)?(rt.getCode().toUpperCase() + "_"):"") + rt.getName().toUpperCase();
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_WIN_REVERSAL);
                bac.setAccountCodeSuffix(rewardFullSuffix);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_" + rt.getName().toUpperCase()+"_CANCEL_CREDIT");
                return bac;
            }

            //NOTE : DFG originating bonuses
            if (gamePlayContext.getFreePlayOperation() != null
                && gamePlayContext.getFreePlayOperation().getSource() != null
                && gamePlayContext.getFreePlayOperation().getSource().getSourceType() != null
                && gamePlayContext.getFreePlayOperation().getSource().getSourceType().equalsIgnoreCase("DFG")) {
//                return EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_FREEGAME_REVERSAL;
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.REWARD_WIN_REVERSAL);
                bac.setAccountCodeSuffix("RX_FREESPIN_FREEGAME_REVERSAL");
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREESPIN_FREEGAME_CANCEL_CREDIT");
                return bac;
            }
//            return EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_REVERSAL;
            bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN_REVERSAL);
            return bac;
        }

        //NOTE : Credit reversal due to cash wager
        if (gamePlayContext.getLithiumGame() != null) {
            if (BooleanUtils.isTrue(gamePlayContext.getLithiumGame().getFreeGame())) {
//                return EBalanceAdjustmentComponentType.CASINO_WIN_FREEGAME_REVERSAL;
                bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN_FREEGAME_REVERSAL);
                bac.setTransactionIdLabelOverride(gamePlayOperation.getId() + "_FREEGAME_CANCEL_CREDIT");
                return bac;
            }
        }

        //NOTE : default to V1
//        return EBalanceAdjustmentComponentType.CASINO_WIN_REVERSAL;
        bac.setAdjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN_REVERSAL);
        return bac;
    }
}
