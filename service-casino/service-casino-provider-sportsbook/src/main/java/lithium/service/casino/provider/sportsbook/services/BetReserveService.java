package lithium.service.casino.provider.sportsbook.services;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.jpa.exceptions.CannotAcquireLockException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.casino.CasinoAccountCodes;
import lithium.service.casino.CasinoAccountTypeCodes;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.BetReserveContext;
import lithium.service.casino.provider.sportsbook.enums.ReservationStatus;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.casino.provider.sportsbook.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationStatusRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
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
import lithium.util.ExceptionMessageUtil;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class BetReserveService {

    @Autowired @Setter
    private ProviderConfigService configService;

    @Autowired @Setter
    private ModuleInfo moduleInfo;

    @Autowired @Setter
    private LimitInternalSystemService limits;

    @Autowired @Setter
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired @Setter
    private UserRepository userRepository;

    @Autowired @Setter
    private CurrencyRepository currencyRepository;

    @Autowired @Setter
    private ReservationRepository reservationRepository;

    @Autowired @Setter
    private CasinoClientService casinoService;

    @Autowired @Setter
    private CachingDomainClientService domainClientService;

    @Autowired @Setter
    private AccountingClientService accountingService;

    @Autowired @Setter
    private DomainService domainService;

    @Autowired @Setter
    private UserService userService;

    @Autowired @Setter
    private ReservationStatusRepository reservationStatusRepository;

    /** Initial persist of reservation with status = PENDING.
     *
     * There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betReserveInit(BetReserveContext context) throws Status422DataValidationError,
            Status484WeeklyLossLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status493MonthlyLossLimitReachedException, Status482PlayerBetPlacementNotAllowedException,
            Status473DomainBettingDisabledException, Status492DailyLossLimitReachedException,
            Status500InternalServerErrorException, Status491PermanentSelfExclusionException,
            Status500UserInternalSystemClientException, Status405UserDisabledException,
            Status550ServiceDomainClientException, Status494DailyWinLimitReachedException,
            Status500LimitInternalSystemClientException, Status490SoftSelfExclusionException,
            Status485WeeklyWinLimitReachedException, Status496PlayerCoolingOffException,
            Status470HashInvalidException, Status512ProviderNotConfiguredException,
            Status409DuplicateSubmissionException, Status478TimeSlotLimitException,
            Status438PlayTimeLimitReachedException {
        validateRequest(context.getRequest());
        callServiceUserAndDomainAndLimit(context);
        validateRequestData(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        prePersist(context);
        SW.start("persist");
        persist(context);
        SW.stop();
    }

    @TimeThisMethod
//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betReserve(BetReserveContext context) throws Status471InsufficientFundsException,
            Status500InternalServerErrorException, Status500UnhandledCasinoClientException {
        try {
            SW.start("callServiceAccounting");
            callServiceAccounting(context);
            SW.stop();
            SW.start("callServiceCasinoForBalance");
            callServiceCasinoForBalance(context);
            SW.stop();
            SW.start("updateReservation");
            persist(context);
            SW.stop();
        } catch (Status471InsufficientFundsException insufficientFundsException) {
            log.info("Player has insufficient funds: context: " + context);
            reservationRepository.delete(context.getReservation());
            throw insufficientFundsException;
        } catch (Exception e) {
            log.error("An error occurred while processing the accounting adjustment. Deleting reservation"
                    + " [reservation="+context.getReservation()+"] " + e.getMessage(), e);
            reservationRepository.delete(context.getReservation());
            throw e;
        }
    }

    /**
     * This was created to handle LSPLAT-1429
     *
     * @param context
     */
    public BetReserveContext checkRequired(BetReserveContext context) {
        try {
            //Get the domainName
            String domainName = userApiInternalClientService.performUserChecks(
                    context.getConvertedGuid(), context.getLocale(), null, true,
                    false, false).getDomain().getName();

            //Sort the currency out
            String currencyCode = domainClientService.getDefaultDomainCurrency(domainName);
            Currency currency = currencyRepository.findOrCreateByCode(currencyCode, Currency::new);

            //Finally sort the domain out
            Domain domain = domainService.findOrCreateByName(domainName, currency);

            if (domain != null) {
                User user = userService.findOrCreateByGuid(context.getConvertedGuid(), domain);

                context.setUser(user);
                context.setDomain(domain);
                context.setCurrency(currency);
            } else {
                throw new Status500InternalServerErrorException("Unable to create domain. "
                        + "This potentially means the domain provided is not a valid player domain.");
            }
        } catch (Status500InternalServerErrorException exception) {
            log.error(exception.getMessage(), exception);
        } catch (Status500UserInternalSystemClientException exception) {
            log.error(exception.getMessage(), exception);
        } catch (Status401UnAuthorisedException exception) {
            log.error(exception.getMessage(), exception);
        } catch (Status405UserDisabledException exception) {
            log.error(exception.getMessage(), exception);
        } catch (Status550ServiceDomainClientException exception) {
            log.error(exception.getMessage(), exception);
        }

        return context;
    }

    private void validateRequest(BetReserveRequest request) throws Status422DataValidationError {
        if (request.getReserveId() == null) throw new Status422DataValidationError("reserveId is required");
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
        if (request.getAmount() == null) throw new Status422DataValidationError("amount is required");
        if (request.getAmount().doubleValue() < 0.0) throw new Status422DataValidationError("negative amount not allowed");
        if (request.getSessionId() == null) throw new Status422DataValidationError("sessionId is required");
    }

    private void callServiceUserAndDomainAndLimit(BetReserveContext context) throws Status500LimitInternalSystemClientException,
            Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
            Status550ServiceDomainClientException, Status473DomainBettingDisabledException,
            Status496PlayerCoolingOffException, Status493MonthlyLossLimitReachedException,
            Status492DailyLossLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status494DailyWinLimitReachedException, Status482PlayerBetPlacementNotAllowedException, Status500InternalServerErrorException,
            Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        //This is our final fail safe for LSPLAT-1429
        try {
            context.setUser(userRepository.findOrCreateByGuidAlwaysLock(context.getConvertedGuid(),
                    () -> User.builder().domain(context.getDomain()).build()));
        } catch (CannotAcquireLockException e) {
            throw new Status500InternalServerErrorException("User could not be locked in time. " +
                    "Did you send multiple requests for the same user?", e);
        }

        domainClientService.checkBettingEnabled(context.getDomain().getName(), context.getLocale());

        limits.checkPlayerRestrictions(context.getConvertedGuid(), context.getLocale());
        limits.checkPlayerBetPlacementAllowed(context.getConvertedGuid());
        limits.checkLimits(context.getDomain().getName(), context.getConvertedGuid(), context.getCurrency().getCode(),
                context.getRequest().getAmountAsCents(), context.getLocale());
    }

    private void validateRequestData(BetReserveContext context) throws Status422DataValidationError {

        BetReserveRequest request = context.getRequest();

        if (request.getAmount() == null || request.getAmount() < 0) {
            throw new Status422DataValidationError("Amount should be bigger than or equal to zero");
        }

        if (request.getGuid() == null || request.getGuid().length() == 0) {
            throw new Status422DataValidationError("Guid may not be null or empty");
        }
    }

    private void validateSha256(BetReserveContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        BetReserveRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getReserveId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(BetReserveContext context) throws Status409DuplicateSubmissionException {
        BetReserveRequest request = context.getRequest();
        Reservation reservation = reservationRepository.findByReserveId(request.getReserveId());
        if (reservation != null) {
            context.setReservation(reservation);
            throw new Status409DuplicateSubmissionException("reserveId already submitted");
        }
    }

    private void prePersist(BetReserveContext context) {
        BetReserveRequest request = context.getRequest();
        Reservation reservation = Reservation.builder()
                .amount(request.getAmount())
                .currency(context.getCurrency())
                .reserveId(request.getReserveId())
                .timestamp(new Date(request.getTimestamp()))
                .totalBetAmount(0.0)
                .user(context.getUser())
                .sessionId(request.getSessionId())
                .betCount(request.getBetsCount())
                .build();
        context.setReservation(reservation);
        context.setReservationStatus(ReservationStatus.PENDING);
    }

    private void callServiceAccounting(BetReserveContext context) throws Status500InternalServerErrorException,
            Status471InsufficientFundsException {

        try {
            AdjustmentRequest request = AdjustmentRequest.builder()
                    .domainName(context.getDomain().getName())
                    .build();

            LabelManager labelManager = LabelManager.instance()
                    .addLabel(LabelManager.TRANSACTION_ID, String.valueOf(context.getRequest().getReserveId()))
                    .addLabel(LabelManager.PROVIDER_GUID, moduleInfo.getModuleName())
                    .addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(context.getRequest().getSessionId()));

            request.add(AdjustmentRequestComponent.builder()
                    .accountTypeCode(CasinoAccountTypeCodes.PLAYER_BALANCE.toString())
                    .accountCode(CasinoAccountCodes.PLAYER_BALANCE.toString())
                    .allowNegativeAdjust(false)
                    .amountCents(lithium.math.CurrencyAmount.fromAmount(context.getReservation().getAmount()).toCents() * -1)
                    .authorGuid(context.getUser().getGuid())
                    .ownerGuid(context.getUser().getGuid())
                    .date(DateTime.now())
                    .contraAccountCode(CasinoAccountCodes.SPORTS_RESERVED_FUNDS.toString())
                    .contraAccountTypeCode(CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString())
                    .currencyCode(context.getCurrency().getCode())
                    .transactionTypeCode(CasinoTranType.SPORTS_RESERVE.toString())
                    .domainName(context.getDomain().getName())
                    .labels(labelManager.getLabelArray())
                    .build());

            AdjustmentResponse response = accountingService.adjust(request);
            context.getReservation().setAccountingTransactionId(response.getAdjustments().get(0).getTransactionId());

            context.setReservationStatus(ReservationStatus.COMPLETED);
        } catch (HystrixRuntimeException e) {
            // On accounting timeout, don't rollback, mark the reservation status as timeout. Accounting will be checked
            // periodically until the transaction succeeds or a time limit has been reached. We're returning a response
            // as if it suceeded, thus the "balanceAfter" is incorrect because accounting has not yet adjusted the balance.
            // Accounting will lock the user account, so it's unlikely that another transaction will affect the balance
            // while this one is running.
            if (e.getFailureType().equals(HystrixRuntimeException.FailureType.TIMEOUT)) {
                context.setReservationStatus(ReservationStatus.TIMEOUT);
                log.warn("Accounting timed out on sports reserve adjustment, reservation status will be updated to "
                        + "TIMEOUT [reservation="+context.getReservation()+"]");
            } else {
                // Some other hystrix exception, need to rethrow!!!
                log.error("Accounting adjustment failed | " + e.getMessage(), e);
                throw e;
            }
        } catch (Status415NegativeBalanceException ne) {
            throw new Status471InsufficientFundsException();
        } catch (Exception e) {
            log.error("Accounting adjustment failed | " + e.getMessage(), e);
            throw new Status500InternalServerErrorException("service-accounting", ExceptionMessageUtil.allMessages(e), e);
        }

    }

    private void callServiceCasinoForBalance(BetReserveContext context)
        /*throws Status500UnhandledCasinoClientException*/ {
        // FIXME: If we got to this point, the accounting adjustment most likely succeeded. Thowing an exception here
        //        would mean the reservation is rolled back, however, the accounting transaction won't be rolled back.
        //        It is probably better to return incorrect/null balance, right?
        Long playerBalanceCents = null;
        try {
            playerBalanceCents = casinoService.getPlayerBalance(
                    context.getDomain().getName(),
                    context.getUser().getGuid(),
                    context.getCurrency().getCode())
                    .getBalanceCents();
        } catch (Status500UnhandledCasinoClientException e) {
            log.error("Failed to get player balance [reservation="+context.getReservation()+"]", e);
        }
        if (playerBalanceCents != null) {
            CurrencyAmount balance = CurrencyAmount.fromCents(playerBalanceCents);
            context.getReservation().setBalanceAfter(balance.toAmount().doubleValue());
        }
    }

    private void persist(BetReserveContext context) {
        lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus status =
                reservationStatusRepository.findOrCreateByName(context.getReservationStatus().name(),
                        () -> new lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus());
        context.getReservation().setReservationStatus(status);
        context.getReservation().setBonusUsedAmount(0.0);
        Reservation reservation = reservationRepository.save(context.getReservation());
        context.setReservation(reservation);
        log.trace("Saved reservation " + reservation);
    }

}
