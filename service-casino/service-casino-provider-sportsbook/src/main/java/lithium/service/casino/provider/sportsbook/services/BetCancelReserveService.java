package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
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
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betcancelreserve.BetCancelReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betcancelreserve.BetCancelReserveResponse;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.BetCancelReserveContext;
import lithium.service.casino.provider.sportsbook.shared.service.ReservationService;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCancel;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.casino.provider.sportsbook.storage.repositories.DomainRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationCancelRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.ReservationRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.service.LoginEventClientService;
import lithium.util.ExceptionMessageUtil;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BetCancelReserveService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    private ReservationRepository reservationRepository;

    @Autowired @Setter
    private DomainRepository domainRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired @Setter
    private ReservationCancelRepository reservationCancelRepository;

    @Autowired @Setter
    private CasinoClientService casinoService;

    @Autowired @Setter
    private ReservationService reservationService;

    @Autowired @Setter
    private AccountingClientService accountingService;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private LoginEventClientService loginEventHelperService;

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
      * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
      * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void betCancelReserve(BetCancelReserveContext context) throws Status470HashInvalidException,
            Status512ProviderNotConfiguredException, Status444ReferencedEntityNotFound,
            Status500UnhandledCasinoClientException, Status409DuplicateSubmissionException,
            Status422DataValidationError, Status500InternalServerErrorException,
            Status438ReservationPendingException {

        validateRequest(context.getRequest());
        resolveDomain(context);
        validateSha256(context);
        findAndLockUser(context);
        reservationService.findReservation(context);
        checkDuplicateSubmission(context);
        validateReservationState(context);
        prePersist(context);
        callServiceAccounting(context);
        callServiceCasinoForBalance(context);
        persist(context);

    }

    private void findAndLockUser(BetCancelReserveContext context)
    throws
    Status500InternalServerErrorException {
        try {
            User user = userRepository.findByGuidAlwaysLock(context.getRequest().getGuid());
            context.setUser(user);
        } catch (CannotAcquireLockException e) {
            throw new Status500InternalServerErrorException("Unable to lock user. Did you send more than one " +
                                                                    "request for the same user at the same time?", e);
        }
    }

    private void validateRequest(BetCancelReserveRequest request) throws Status422DataValidationError {
        if (request.getReserveId() == null) throw new Status422DataValidationError("reserveId is required");
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
    }

    private void resolveDomain(BetCancelReserveContext context) throws Status422DataValidationError {
        String domainString = context.getRequest().getGuid().split("/")[0];
        lithium.service.casino.provider.sportsbook.storage.entities.Domain domain = domainRepository.findByName(domainString);
        if (domain == null) {
            throw new Status422DataValidationError("Invalid domain provided: " + domainString);
        }
        context.setDomain(domain);
    }

    private void validateSha256(BetCancelReserveContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        BetCancelReserveRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getReserveId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(BetCancelReserveContext context) throws Status409DuplicateSubmissionException {
        BetCancelReserveRequest request = context.getRequest();
        ReservationCancel reservationCancel = reservationCancelRepository.findByReservationReserveId(request.getReserveId());
        if (reservationCancel != null) {
            context.setReservationCancel(reservationCancel);
            throw new Status409DuplicateSubmissionException("reserveId already submitted");
        }
    }

    private void validateReservationState(BetCancelReserveContext context) throws Status422DataValidationError {
        if (context.getReservation().getTotalBetAmount() > 0.0)
            throw new Status422DataValidationError("Reservation already contains debits " +
                    context.getReservation().getTotalBetAmount());
        if (context.getReservation().getReservationCancel() != null)
            throw new Status422DataValidationError("Reservation is already cancelled");
        if (context.getReservation().getReservationCommit() != null)
            throw new Status422DataValidationError("Reservation is committed");
    }

    private void prePersist(BetCancelReserveContext context) {
        ReservationCancel reservationCancel = new ReservationCancel();
        reservationCancel.setReservation(context.getReservation());
        context.setReservationCancel(reservationCancel);
    }

    private void callServiceAccounting(BetCancelReserveContext context) throws Status500InternalServerErrorException {

        try {
            AdjustmentRequest request = AdjustmentRequest.builder()
                    .domainName(context.getDomain().getName())
                    .build();

            LabelManager labelManager = LabelManager.instance()
                    .addLabel(LabelManager.TRANSACTION_ID, context.getRequest().getReserveId() + "_CANCEL")
                    .addLabel(LabelManager.PROVIDER_GUID, moduleInfo.getModuleName())
                    .addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(context.getReservation().getSessionId()));

            AdjustmentRequestComponent arc = AdjustmentRequestComponent.builder()
                    .accountTypeCode(CasinoAccountTypeCodes.PLAYER_BALANCE.toString())
                    .accountCode(CasinoAccountCodes.PLAYER_BALANCE.toString())
                    .allowNegativeAdjust(false)
                    .amountCents(lithium.math.CurrencyAmount.fromAmount(context.getReservation().getAmount()).toCents())
                    .authorGuid(context.getUser().getGuid())
                    .ownerGuid(context.getUser().getGuid())
                    .date(DateTime.now())
                    .contraAccountCode(CasinoAccountCodes.SPORTS_RESERVED_FUNDS.toString())
                    .contraAccountTypeCode(CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString())
                    .currencyCode(context.getCurrency().getCode())
                    .transactionTypeCode(CasinoTranType.SPORTS_RESERVE_CANCEL.toString())
                    .domainName(context.getDomain().getName())
                    .labels(labelManager.getLabelArray())
                    .build();

            request.add(arc);

            AdjustmentResponse response = accountingService.adjust(request);
            context.getReservationCancel().setAccountingTransactionId(response.getAdjustments().get(0).getTransactionId());

        } catch (Exception e) {
            throw new Status500InternalServerErrorException("service-accounting", ExceptionMessageUtil.allMessages(e), e);
        }

    }

    private void callServiceCasinoForBalance(BetCancelReserveContext context) throws Status500UnhandledCasinoClientException {
        BalanceResponse balanceResponse = casinoService.getPlayerBalance(context.getDomain().getName(),
                context.getUser().getGuid(), context.getCurrency().getCode());
        context.getReservationCancel().setBalanceAfter(balanceResponse.getBalance().doubleValue());
    }

    private void persist(BetCancelReserveContext context) {
        context.setReservationCancel(reservationCancelRepository.save(context.getReservationCancel()));
        context.getReservation().setReservationCancel(context.getReservationCancel());
        context.setReservation(reservationRepository.save(context.getReservation()));
    }

    public BetCancelReserveResponse getDefaultCancelReserveResponse(BetCancelReserveContext context) throws Status422DataValidationError, Status500UnhandledCasinoClientException, Status550ServiceDomainClientException {
        validateRequest(context.getRequest());
        BetCancelReserveRequest request = context.getRequest();
        context.getResponse().setTransactionId(0L);
        String guid = context.getConvertedGuid();
        String [] domainUser = guid.split("/");
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainUser[0]);
        if (domain != null ) {
            context.getResponse().setBalanceCurrencyCode(domain.getCurrency());
            BalanceResponse balanceResponse = casinoService.getPlayerBalance(domain.getName(), guid, domain.getCurrency());
            if (balanceResponse != null && balanceResponse.getBalance() != null) {
                context.getResponse().setBalance(CurrencyAmount.fromAmount(balanceResponse.getBalance()));
            }
        }
        return  context.getResponse();
    }

}
