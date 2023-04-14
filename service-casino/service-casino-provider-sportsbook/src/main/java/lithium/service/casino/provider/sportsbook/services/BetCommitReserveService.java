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
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.BalanceAdjustmentResponseComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve.BetCommitReserveRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.BetCommitReserveContext;
import lithium.service.casino.provider.sportsbook.shared.service.ReservationService;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCommit;
import lithium.service.casino.provider.sportsbook.storage.repositories.BetRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationCommitRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BetCommitReserveService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    private BetRepository betRepository;

    @Autowired @Setter
    private ReservationService reservationService;

    @Autowired @Setter
    private ReservationCommitRepository reservationCommitRepository;

    @Autowired @Setter
    private CasinoClientService casinoService;

    @Autowired @Setter
    private ReservationRepository reservationRepository;

    @Autowired @Setter
    private OpenBetsOperatorMigrationService openBetsOperatorMigrationService;

//    @Autowired @Setter
//    private AccountingClientService accountingService;

    public boolean shouldUseOpenBetOperatorMigrationExecution(String userGuid)
            throws Status500InternalServerErrorException {
        String[] domainAndUser = userGuid.split("/");
        if (domainAndUser.length != 2) {
            // This should not be possible
            throw new Status500InternalServerErrorException("Invalid user");
        }
        String domainName = domainAndUser[0];
        return openBetsOperatorMigrationService.isOpenBetsOperatorMigrationEnabled(domainName);
    }

    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betCommitReserveOpenBetOperatorMigration(BetCommitReserveContext context)
            throws Status401UnAuthorisedException, Status422DataValidationError, Status444ReferencedEntityNotFound,
            Status512ProviderNotConfiguredException, Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status473DomainBettingDisabledException,
            Status493MonthlyLossLimitReachedException, Status484WeeklyLossLimitReachedException,
            Status474DomainProviderDisabledException, Status490SoftSelfExclusionException,
            Status405UserDisabledException, Status485WeeklyWinLimitReachedException,
            Status491PermanentSelfExclusionException, Status471InsufficientFundsException,
            Status492DailyLossLimitReachedException, Status550ServiceDomainClientException,
            Status511UpstreamServiceUnavailableException, Status496PlayerCoolingOffException,
            Status500InternalServerErrorException, Status438ReservationPendingException,
            Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {
        validateRequest(context.getRequest());
        reservationService.findReservation(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        validateReservationState(context);
        prePersist(context);
        context.getReservationCommit().setBalanceAfter(0.0);
        persist(context);
        openBetsOperatorMigrationService.addAuditTrail(context.getReservation(), null,
                context.getReservationCommit());
    }

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betCommitReserve(BetCommitReserveContext context)
            throws Status401UnAuthorisedException, Status422DataValidationError, Status444ReferencedEntityNotFound,
            Status512ProviderNotConfiguredException, Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status473DomainBettingDisabledException,
            Status493MonthlyLossLimitReachedException, Status484WeeklyLossLimitReachedException,
            Status474DomainProviderDisabledException, Status490SoftSelfExclusionException,
            Status405UserDisabledException, Status485WeeklyWinLimitReachedException,
            Status491PermanentSelfExclusionException, Status471InsufficientFundsException,
            Status492DailyLossLimitReachedException, Status550ServiceDomainClientException,
            Status511UpstreamServiceUnavailableException, Status496PlayerCoolingOffException,
            Status500InternalServerErrorException, Status438ReservationPendingException,
            Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        validateRequest(context.getRequest());
        reservationService.findReservation(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        validateReservationState(context);
        validateBetsCount(context);
        prePersist(context);
        callServiceCasino(context);
        persist(context);
    }

    private void validateRequest(BetCommitReserveRequest request) throws Status422DataValidationError {
        if (request.getReserveId() == null) throw new Status422DataValidationError("reserveId is required");
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
    }

    private void validateSha256(BetCommitReserveContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        BetCommitReserveRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getReserveId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(BetCommitReserveContext context) throws Status409DuplicateSubmissionException {
        BetCommitReserveRequest request = context.getRequest();
        ReservationCommit reservationCommit = reservationCommitRepository.findByReservationReserveId(request.getReserveId());
        if (reservationCommit != null) {
            context.setReservationCommit(reservationCommit);
            throw new Status409DuplicateSubmissionException("reserveId already submitted");
        }
    }

    private void validateReservationState(BetCommitReserveContext context) throws Status422DataValidationError {
        if (context.getReservation().getReservationCancel() != null)
            throw new Status422DataValidationError("Reservation is cancelled");
        if (context.getReservation().getReservationCommit() != null)
            throw new Status422DataValidationError("Reservation is already committed");
        if ((context.getReservation().getAmount() > 0) && (context.getReservation().getTotalBetAmount() == 0))
            throw new Status422DataValidationError("Reservation contains no bets");
    }

    private void validateBetsCount(BetCommitReserveContext context) throws Status422DataValidationError {

        if (context.getReservation().getBetCount() != null) {
            List<Bet> bets = betRepository.findByReservation(context.getReservation());
            Long reservationBetCount = context.getReservation().getBetCount();

            if (context.getReservation().getBetCount() > bets.size()) {
                throw new Status422DataValidationError("There are less Bets received [" + bets.size() + "] " +
                        " than expected Bet Count of [" + reservationBetCount + "] for " +
                        "ReservationId : " + context.getReservation().getId());
            } else if (reservationBetCount < bets.size()) {
                log.warn("There are more Bets received ["
                        + bets.size() + "] than expected Bet Count of[" + reservationBetCount + "] for " +
                        "ReservationId : " + context.getReservation().getId());
            }
        }
    }

    private void prePersist(BetCommitReserveContext context) {
        ReservationCommit reservationCommit = new ReservationCommit();
        reservationCommit.setReservation(context.getReservation());
        context.setReservationCommit(reservationCommit);
    }

    private void callServiceCasino(BetCommitReserveContext context)
            throws Status401UnAuthorisedException, Status405UserDisabledException, Status471InsufficientFundsException,
            Status473DomainBettingDisabledException, Status474DomainProviderDisabledException,
            Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException, Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status511UpstreamServiceUnavailableException, Status550ServiceDomainClientException,
            Status496PlayerCoolingOffException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {
        SW.start("betcommitreserve.callservicecasino.handle." + context.getRequest());

        try {
            BalanceAdjustmentRequest casinoRequest = new BalanceAdjustmentRequest();
            casinoRequest.setDomainName(context.getDomain().getName());
            casinoRequest.setCurrencyCode(context.getCurrency().getCode());
            casinoRequest.setProviderGuid(context.getDomain().getName() + "/" + moduleInfo.getModuleName());
            casinoRequest.setUserGuid(context.getUser().getGuid());
            casinoRequest.setTransactionTiebackId(context.getReservationId().toString());
            casinoRequest.setRealMoneyOnly(true);
            casinoRequest.setAllowNegativeBalanceAdjustment(true);
            casinoRequest.setGameSessionId(context.getReservation().getSessionId().toString());
            casinoRequest.setPerformAccessChecks(false);

            ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();
            adjustmentComponents.add(BalanceAdjustmentComponent.builder()
                .adjustmentType(EBalanceAdjustmentComponentType.SPORTS_RESERVE_RETURN)
                .amount(lithium.math.CurrencyAmount.fromAmount(context.getReservation().getAmount()).toCents())
                .transactionIdLabelOverride(context.getRequest().getReserveId() + "_COMMIT")
                .build()
            );

            EBalanceAdjustmentComponentType adjustmentType = EBalanceAdjustmentComponentType.SPORTS_BET;
            if (context.getReservation().getAmount() == 0) {
                adjustmentType = EBalanceAdjustmentComponentType.SPORTS_FREE_BET;
            }

            Reservation reservation = reservationRepository.findByReserveId(context.getReservationId());
            List<Bet> bets = betRepository.findByReservation(reservation);
            for (Bet bet: bets) {
                adjustmentComponents.add(BalanceAdjustmentComponent.builder()
                    .adjustmentType(adjustmentType)
                    .amount(lithium.math.CurrencyAmount.fromAmount(bet.getAmount()).toCents())
                    .transactionIdLabelOverride(bet.getBetId())
                    .additionalReference(bet.getPurchaseId())
                    .build()
                );
            }

            casinoRequest.setSessionId(reservation.getSessionId());
            casinoRequest.setAdjustmentComponentList(adjustmentComponents);
            BalanceAdjustmentResponse response = casinoService.multiBetV1(casinoRequest, context.getLocale());
            log.debug("Response from casino " + response);

            double balanceAfter = CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue();

            context.getReservationCommit().setBalanceAfter(balanceAfter);

            for (int i = 0; i < response.getAdjustmentResponseComponentList().size(); i++) {
                BalanceAdjustmentResponseComponent adjustmentResponse = response.getAdjustmentResponseComponentList()
                    .get(i);
                if (i == 0) {
                    context.getReservationCommit()
                        .setAccountingTransactionId(Long.parseLong(adjustmentResponse.getExtSystemTransactionId()));
                    continue;
                }
                Bet bet = bets.get(i - 1);
                bet.setAccountingTransactionId(Long.valueOf(adjustmentResponse.getExtSystemTransactionId()));
                bet.setBalanceAfter(balanceAfter);
            }

            betRepository.saveAll(bets);
        } finally {
            SW.stop();
        }
    }

    private void persist(BetCommitReserveContext context) {
        context.setReservationCommit(reservationCommitRepository.save(context.getReservationCommit()));
        context.getReservation().setReservationCommit(context.getReservationCommit());
        context.setReservation(reservationRepository.save(context.getReservation()));
    }

}
