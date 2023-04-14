package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.cashier.client.service.CashierSystemClientService;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Settlement;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementEntry;
import lithium.service.casino.provider.sportsbook.storage.repositories.BetRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.SettlementRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lithium.service.domain.client.CachingDomainClientService;
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
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class SettleService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    SettlementRepository settlementRepository;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    CachingDomainClientService cachingDomainClientService;

    @Autowired @Setter
    PubSubBetService pubSubBetService;

    @Autowired @Setter
    UserRepository userRepository;

    @Autowired
    CashierSystemClientService cashierService;

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void settle(
        SettleMultiContext context
    ) throws
            Status401UnAuthorisedException, Status405UserDisabledException, Status409DuplicateSubmissionException,
            Status422DataValidationError, Status444ReferencedEntityNotFound,
            Status470HashInvalidException, Status471InsufficientFundsException,
            Status474DomainProviderDisabledException, Status473DomainBettingDisabledException,
            Status500UnhandledCasinoClientException, Status511UpstreamServiceUnavailableException,
            Status512ProviderNotConfiguredException, Status550ServiceDomainClientException,
            Status493MonthlyLossLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status496PlayerCoolingOffException, Status484WeeklyLossLimitReachedException,
            Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
            Status492DailyLossLimitReachedException, Status494DailyWinLimitReachedException,
            Status485WeeklyWinLimitReachedException, Status500InternalServerErrorException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {
        validateRequest(context.getRequest());
        findBet(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        prePersist(context);
        SW.start("callServiceCasino");
        callServiceCasino(context);
        SW.stop();
        SW.start("persist");
        persist(context);
        SW.stop();
        SW.start("callPubSubService");
        if (pubSubBetService.isChannelActivated(context.getDomain().getName())) {
            pubSubBetService.buildMultipleSettlementsMessages(context);
        }
        SW.stop();
        if (cachingDomainClientService.allowNegativeBalanceAdjustment(context.getDomain().getName())) {
            SW.start("negativeBalanceCheck");
            negativeBalanceCheck(context);
            SW.stop();
        }
    }


    private void validateRequest(SettleMultiRequest request) throws Status422DataValidationError {
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
        if (request.getRequestId() == null) throw new Status422DataValidationError("requestId is required");
        for (int p = 0; p < request.getSettleRequests().size(); ++p) {
            if (request.getSettleRequests().get(p).getAmount() == null) throw new Status422DataValidationError("amount is required");
            if (StringUtil.isEmpty(request.getSettleRequests().get(p).getBetId())) throw new Status422DataValidationError("betId is required");
        }
    }

    private void validateSha256(SettleMultiContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        SettleMultiRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getRequestId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(SettleMultiContext context) throws Status409DuplicateSubmissionException {
        SettleMultiRequest request = context.getRequest();
        Settlement settlement = settlementRepository.findByRequestId(request.getRequestId());
        if (settlement != null) {
            context.setSettlement(settlement);
            throw new Status409DuplicateSubmissionException("requestId already submitted");
        }
    }

    public void findBet(SettleMultiContext context) throws Status444ReferencedEntityNotFound,
            Status500InternalServerErrorException {
        HashMap<String, Bet> betMap = new HashMap<>();
//        variable not being used
//        ArrayList<SettleRequest> settleRequests = context.getRequest().getSettleRequests();
        for (SettleRequest settleItem : context.getRequest().getSettleRequests()) {
            if (betMap.containsKey(settleItem.getBetId())) continue;
            Bet bet = betRepository.findByBetId(settleItem.getBetId());
            if (bet == null) throw new Status444ReferencedEntityNotFound("Bet with the supplied ID not found: " + settleItem.getBetId());
            betMap.put(bet.getBetId(), bet);
            context.setDomain(bet.getUser().getDomain());
            context.setUser(bet.getUser());
            context.setCurrency(bet.getReservation().getCurrency());

            try {
                userRepository.findByGuidAlwaysLock(bet.getUser().getGuid());
            } catch (CannotAcquireLockException e) {
                throw new Status500InternalServerErrorException("Unable to lock user. Did you send more than one " +
                        "request for the same user at the same time?", e);
            }

        }
        context.setBetMap(betMap);
    }

    private void prePersist(SettleMultiContext context) {
        Settlement settlement = new Settlement();
        settlement.setTimestamp(new Date(context.getRequest().getTimestamp()));
        settlement.setRequestId(context.getRequest().getRequestId());
        settlement.setCurrency(context.getCurrency());
        ArrayList<SettlementEntry> settlementEntryList = new ArrayList<>(context.getRequest().getSettleRequests().size());
        for (SettleRequest request : context.getRequest().getSettleRequests()) {
            SettlementEntry settlementEntry = new SettlementEntry();
            settlementEntry.setAmount(request.getAmount());
            settlementEntry.setBet(context.getBetMap().get(request.getBetId()));
            settlementEntry.setType(resolveSportsType(request.getAmount()));
            settlementEntryList.add(settlementEntry);
        }
        settlement.setSettlementEntries(settlementEntryList);
        context.setSettlement(settlement);
    }

    /**
     * Negative balance adjustments as per :
     * LIVESCORE-1112 - PLAT-166 Handling Of Negative Balance
     * https://playsafe.atlassian.net/browse/LIVESCORE-1112
     *
     * Business:
     * As a rule of thumb - an account balance can never be negative.
     * Sometimes we get re-settlements of bets which need to take money away from an account,
     * and if the player has already spent it, then his account might go into negative.
     * The account might also go into negative in cases there are odds that are something like 1-2.34534543645
     * and the system did not round up the amount properly and the balance is left with -0.000003432567.
     *
     * We need the system to adjust these cases to 0, and mark those adjustments for reporting purposes.
     *
     */
    private void negativeBalanceCheck(
        SettleMultiContext context
    ) throws
        Status511UpstreamServiceUnavailableException,
        Status550ServiceDomainClientException, Status500UnhandledCasinoClientException
    {
        if (CurrencyAmount.fromAmount(context.getSettlement().getBalanceAfter()).isNegative()) {
            log.error("Negative Balance Adjustment.");
            SW.start("settle.negativebalancecheck." + context.getRequest());
            try {
                //try to cancel all pending withdrawals in case there is bet resettlement
                if (cachingDomainClientService.cancelPendingWithdrawalsOnBetResettlement(context.getDomain().getName()) && isMultiResettlement(context)) {
                    Long balance = cancelPendingWithdrawals(context);
                    //we should set the balance to current after withdraw cancellation in any case, in order to make correct negative balance adjustment
                    log.info("Balance after pending transaction cancellation: " + balance + ", player: " + context.getUser().getGuid());
                    context.getSettlement().setBalanceAfter(CurrencyAmount.fromCents(balance).toAmount().doubleValue());
                    if (balance >= 0) {
                        log.info("Skip Negative Balance Adjustment for player: " + context.getUser().getGuid());
                        return;
                    }
                }
                BalanceAdjustmentRequest casinoRequest = new BalanceAdjustmentRequest();
                casinoRequest.setDomainName(context.getDomain().getName());
                casinoRequest.setCurrencyCode(context.getCurrency().getCode());
                casinoRequest.setProviderGuid(context.getDomain().getName() + "/" + moduleInfo.getModuleName());
                casinoRequest.setUserGuid(context.getUser().getGuid());
                casinoRequest.setBonusTran(false);
                casinoRequest.setRoundId(context.getRequest().getRequestId().toString());
                casinoRequest.setRoundFinished(true);
                casinoRequest.setTransactionTiebackId(context.getRequest().getRequestId().toString());
                casinoRequest.setTransactionId(null); //We are using adjustment component overrides for transaction id
                casinoRequest.setRealMoneyOnly(true);
                casinoRequest.setAllowNegativeBalanceAdjustment(true);
                casinoRequest.setPerformAccessChecks(false);

                BalanceAdjustmentComponent adjustmentComponent = BalanceAdjustmentComponent.builder()
                .adjustmentType(EBalanceAdjustmentComponentType.SPORTS_NEGATIVE_BALANCE_ADJUSTMENT)
                .amount(CurrencyAmount.fromAmount(context.getSettlement().getBalanceAfter()).toCents())
                .transactionIdLabelOverride(context.getRequest().getRequestId().toString())
                .build();
                casinoRequest.setAdjustmentComponentList(Collections.singletonList(adjustmentComponent));

                casinoService.negativeBalanceAdjust(casinoRequest, context.getLocale().toString());
                context.getSettlement().setBalanceAfter(0d);
            } finally {
                SW.stop();
            }
        }
    }

    private void callServiceCasino(
        SettleMultiContext context
    ) throws
            Status401UnAuthorisedException, Status405UserDisabledException, Status471InsufficientFundsException,
            Status473DomainBettingDisabledException, Status474DomainProviderDisabledException,
            Status511UpstreamServiceUnavailableException, Status550ServiceDomainClientException,
            Status485WeeklyWinLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status496PlayerCoolingOffException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status493MonthlyLossLimitReachedException,
            Status492DailyLossLimitReachedException, Status494DailyWinLimitReachedException,
            Status484WeeklyLossLimitReachedException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {
        SW.start("settle.callservicecasino.handle." + context.getRequest());

        try {
            BalanceAdjustmentRequest casinoRequest = new BalanceAdjustmentRequest();
            casinoRequest.setDomainName(context.getDomain().getName());
            casinoRequest.setCurrencyCode(context.getCurrency().getCode());
            casinoRequest.setProviderGuid(context.getDomain().getName() + "/" + moduleInfo.getModuleName());
            casinoRequest.setUserGuid(context.getUser().getGuid());
            casinoRequest.setBonusTran(false);
            casinoRequest.setRoundId(context.getRequest().getRequestId().toString());
            casinoRequest.setRoundFinished(true);
            casinoRequest.setTransactionTiebackId(context.getRequest().getRequestId().toString());
            casinoRequest.setTransactionId(null); //We are using adjustment component overrides for transaction id
            casinoRequest.setRealMoneyOnly(true);
            casinoRequest.setAllowNegativeBalanceAdjustment(true);
            casinoRequest.setPerformAccessChecks(false); //Settlements don't need to be checked for limit/or account enabled etc
            // Riaan, can I make this assumption?
            Long sessionId = context.getSettlement().getSettlementEntries().get(0).getBet().getReservation().getSessionId();
            casinoRequest.setSessionId(sessionId);

            ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();
            List<SettlementEntry> settlementEntries = context.getSettlement().getSettlementEntries();
            for (int i = 0; i < settlementEntries.size(); i++) {
                SettlementEntry settlementEntry = settlementEntries.get(i);
                BalanceAdjustmentComponent adjustmentComponent =
                        new BalanceAdjustmentComponent(
                                resolveCasinoSportsType(settlementEntry),
                                CurrencyAmount.fromAmount(settlementEntry.getAmount()).toCents(),
                                null,
                                settlementEntry.getBet().getBetId() + ":" + context.getRequest().getRequestId() + ":" + i);
                adjustmentComponents.add(adjustmentComponent);
            }
            casinoRequest.setAdjustmentComponentList(adjustmentComponents);
            BalanceAdjustmentResponse response = casinoService.multiBetV1(casinoRequest, context.getLocale().toString());

            log.debug("Response from casino " + response);
            context.getSettlement().setBalanceAfter(CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue());

            for (int i = 0; i < response.getAdjustmentResponseComponentList().size(); i++) {
                context.getSettlement().getSettlementEntries().get(i)
                        .setAccountingTransactionId(
                                Long.parseLong(
                                        response.getAdjustmentResponseComponentList().get(i).getExtSystemTransactionId()));
                context.getSettlement().getSettlementEntries().get(i).setRequestIndex(i);
            }
        } finally {
            SW.stop();
        }
    }

    private void persist(SettleMultiContext context) {
        //Persist settlement, then entries, then settlement
        context.setSettlement(settlementRepository.save(context.getSettlement()));
    }

    private Integer resolveSportsType(Double amount) {
        // Negative = SPORTS_RESETTLEMENT
        // Positive = SPORTS_WIN
        // 0 = SPORTS_LOSS
        //TODO: Maybe make an enum with string and numeric assignment
        if (amount == 0.00) return 0; // SPORTS_LOSS
        if (amount > 0.00) return 1; // SPORTS_WIN
        if (amount < 0.00) return 2; // SPORTS_RESETTLEMENT
        return null;
    }

    private EBalanceAdjustmentComponentType resolveCasinoSportsType(final SettlementEntry entry) {
        long returns = CurrencyAmount.fromAmount(entry.getAmount()).toCents();
        EBalanceAdjustmentComponentType tranType = null;
        boolean free = entry.getBet().getAmount() == 0;
        if (returns > 0) tranType = (free)? EBalanceAdjustmentComponentType.SPORTS_FREE_WIN : EBalanceAdjustmentComponentType.SPORTS_WIN;
        if (returns < 0) tranType = (free)? EBalanceAdjustmentComponentType.SPORTS_FREE_RESETTLEMENT: EBalanceAdjustmentComponentType.SPORTS_RESETTLEMENT;
        if (returns == 0) tranType = (free)? EBalanceAdjustmentComponentType.SPORTS_FREE_LOSS: EBalanceAdjustmentComponentType.SPORTS_LOSS;
        return tranType;
    }

    private Long cancelPendingWithdrawals(SettleMultiContext context) throws Status500UnhandledCasinoClientException {
        try {
            log.info("Cancel pending withdrawals is requested for domain: " + context.getDomain().getName() + " guid: " + context.getUser().getGuid() + " Settlement request: " + context.getRequest());
            return cashierService.cancelPendingWithdrawals(context.getDomain().getName(), context.getUser().getGuid(), "Cancelled on bet resettlement");
        } catch (Exception e) {
            log.error("Failed to cancel pending withdrawals on bet resettlement. User: " + context.getUser().getGuid() + " Settlement request: " + context.getRequest(), e);
            return casinoService.getPlayerBalance(context.getDomain().getName(), context.getUser().getGuid(), context.getCurrency().getCode()).getBalanceCents();
        }
    }

    private boolean isMultiResettlement(SettleMultiContext context) {
        return context.getSettlement().getSettlementEntries().stream().anyMatch(e -> {
            EBalanceAdjustmentComponentType type = resolveCasinoSportsType(e);
            return type.equals(EBalanceAdjustmentComponentType.SPORTS_FREE_RESETTLEMENT) || type.equals(EBalanceAdjustmentComponentType.SPORTS_RESETTLEMENT);
        });
    }
}
