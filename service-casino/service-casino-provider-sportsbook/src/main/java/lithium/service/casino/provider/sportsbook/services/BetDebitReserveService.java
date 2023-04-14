package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status408ReservationClosedException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve.BetDebitReserveRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.BetDebitReserveContext;
import lithium.service.casino.provider.sportsbook.shared.service.ReservationService;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationStatus;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.casino.provider.sportsbook.storage.repositories.BetRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationStatusRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class BetDebitReserveService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    ReservationService reservationService;

    @Autowired @Setter
    ReservationRepository reservationRepository;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    OpenBetsOperatorMigrationService openBetsOperatorMigrationService;

    @Autowired @Setter
    CachingDomainClientService cachingDomainClientService;

    @Autowired @Setter
    CurrencyRepository currencyRepository;

    @Autowired @Setter
    DomainService domainService;

    @Autowired @Setter
    UserService userService;

    @Autowired @Setter
    ReservationStatusRepository reservationStatusRepository;

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

    public User findOrCreateUserByGuid(String userGuid) throws Status500InternalServerErrorException {
        String[] domainAndUser = userGuid.split("/");
        if (domainAndUser.length != 2) {
            // This should not be possible
            throw new Status500InternalServerErrorException("Invalid user");
        }
        // Creation of the user requires a domain and currency
        String domainName = domainAndUser[0];
        String domainCurrencyCode = cachingDomainClientService.getDefaultDomainCurrency(domainName);
        Currency currency = currencyRepository.findOrCreateByCode(domainCurrencyCode, Currency::new);
        Domain domain = domainService.findOrCreateByName(domainName, currency);
        return userService.findOrCreateByGuidNoLock(userGuid, domain);
    }

    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betDebitReserveOpenBetOperatorMigration(BetDebitReserveContext context)
            throws Status422DataValidationError, Status444ReferencedEntityNotFound,
            Status512ProviderNotConfiguredException, Status409DuplicateSubmissionException,
            Status500UnhandledCasinoClientException, Status438ReservationPendingException,
            Status408ReservationClosedException {
        // Obtain a lock on the user
        User user = userService.findUserForUpdate(context.getUser().getId());
        context.setCurrency(user.getDomain().getCurrency());

        validateRequest(context.getRequest());
        validateSha256(context);
        checkDuplicateSubmission(context);

        // Try to locate a reservation
        Reservation reservation = reservationRepository.findByReserveId(context.getReservationId());
        if (reservation == null) {
            // Then create it and initialise values
            ReservationStatus rsCompleted = reservationStatusRepository.findOrCreateByName(
                    lithium.service.casino.provider.sportsbook.enums.ReservationStatus.COMPLETED.name(),
                    ReservationStatus::new);
            reservation = reservationRepository.save(
                    Reservation.builder()
                    .user(user)
                    .reserveId(context.getReservationId())
                    .amount(context.getRequest().getAmount())
                    .timestamp(new Date(context.getRequest().getTimestamp()))
                    .balanceAfter(0.0)
                    .currency(user.getDomain().getCurrency())
                    .sessionId(-1L)
                    .bonusUsedAmount(0.0)
                    .totalBetAmount(0.0)
                    .reservationStatus(rsCompleted)
                    .betCount(0L)
                    .build());
        } else {
            // Increment reservation amount with bet amount
            reservation.setAmount(reservation.getAmount() + context.getRequest().getAmount());
        }
        reservation.setBetCount(reservation.getBetCount() + 1);
        context.setReservation(reservation);

        validateReservationState(context);
        prePersist(context);
        persist(context);

        openBetsOperatorMigrationService.addAuditTrail(reservation, context.getBet(), null);
    }

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betDebitReserve(BetDebitReserveContext context)
            throws Status422DataValidationError, Status444ReferencedEntityNotFound,
            Status512ProviderNotConfiguredException, Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status500UnhandledCasinoClientException,
            Status500InternalServerErrorException, Status438ReservationPendingException,
            Status408ReservationClosedException {
        validateRequest(context.getRequest());
        reservationService.findReservation(context);
        validateSha256(context);
        validateRequestData(context);
        checkDuplicateSubmission(context);
        validateReservationState(context);
        prePersist(context);
        persist(context);
        callServiceCasinoForBalance(context);
    }

    private void validateRequest(BetDebitReserveRequest request) throws Status422DataValidationError {
        if (request.getReserveId() == null) throw new Status422DataValidationError("reserveId is required");
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
        if (request.getAmount() == null) throw new Status422DataValidationError("amount is required");
        if (request.getAmount().doubleValue() < 0.0) throw new Status422DataValidationError("negative bet not allowed");
        if (StringUtil.isEmpty(request.getPurchaseId())) throw new Status422DataValidationError("purchaseId is required");
        if (StringUtil.isEmpty(request.getBetId())) throw new Status422DataValidationError("betId is required");
        if (request.getRequestId() == null) throw new Status422DataValidationError("requestId is required");
    }

    private void validateSha256(BetDebitReserveContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        BetDebitReserveRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getReserveId());
        hasher.addItem(request.getRequestId());
        hasher.validate(request.getSha256(), log, request);

    }

    private void checkDuplicateSubmission(
        BetDebitReserveContext context
    ) throws
        Status409DuplicateSubmissionException,
        Status422DataValidationError
    {
        BetDebitReserveRequest request = context.getRequest();
        Bet bet = betRepository.findByBetId(request.getBetId());
        if (bet != null) {
            Bet betByRequestId = betRepository.findByRequestId(request.getRequestId());
            if (betByRequestId == null) {
            	// A new request but an existing bet. See LSNOC-86 INCLS-880
                // throw new Status422DataValidationError("Existing bet found but no existing requestId");
            	// So in this case, we append the request ID to the bet ID in the database.
            	log.warn("checkduplicate same bet id with different request id " + context.toString());
            	request.setBetId(request.getBetId() + "_" + request.getRequestId());
            	// Recheck based on the new bet ID
            	checkDuplicateSubmission(context);
            	return;
            }
            if (betByRequestId.getId().longValue() != bet.getId().longValue()) {
                throw new Status422DataValidationError("Bet ID does not correlate to request ID");
            }
            context.setBet(bet);
            throw new Status409DuplicateSubmissionException("reserveId already submitted");
        }
    }

    private void validateRequestData(BetDebitReserveContext context) throws Status422DataValidationError {
        if (context.getReservation().getAmount() == 0 && context.getRequest().getAmount() != 0)
            throw new Status422DataValidationError("A zero reservation should only contain zero debits");
    }

    private void validateReservationState(BetDebitReserveContext context) throws Status422DataValidationError, Status408ReservationClosedException {
        if (context.getReservation().getReservationCancel() != null)
            throw new Status422DataValidationError("Reservation is cancelled");
        if (context.getReservation().getReservationCommit() != null){
            context.setBet(betRepository.findByReservation(reservationRepository.findByReserveId(context.getReservationId())).get(0));
           throw new Status408ReservationClosedException();
        }
    }

    private void prePersist(BetDebitReserveContext context) {
        Bet bet = new Bet();
        bet.setReservation(context.getReservation());
        bet.setAmount(context.getRequest().getAmount());
        bet.setBetId(context.getRequest().getBetId());
        bet.setPurchaseId(context.getRequest().getPurchaseId());
        bet.setRequestId(context.getRequest().getRequestId());
        bet.setUser(context.getUser());
        bet.setTimestamp(new Date(context.getRequest().getTimestamp()));
        Reservation reservation = context.getReservation();
        reservation.setTotalBetAmount(reservation.getTotalBetAmount() + bet.getAmount());
        context.setBet(bet);
    }

    private void persist(BetDebitReserveContext context) {
        context.setBet(betRepository.save(context.getBet()));
        context.setReservation(reservationRepository.save(context.getReservation()));
    }

    private void callServiceCasinoForBalance(BetDebitReserveContext context) throws Status500UnhandledCasinoClientException {
        BalanceResponse balanceResponse = casinoService.getPlayerBalance(context.getDomain().getName(),
                context.getUser().getGuid(), context.getCurrency().getCode());
        context.getResponse().setBalance(CurrencyAmount.fromCents(balanceResponse.getBalanceCents()));
        context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
    }
}
