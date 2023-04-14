package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.SettleCreditContext;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import lithium.service.casino.provider.sportsbook.storage.repositories.BetRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.SettlementCreditRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class SettleCreditService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    SettlementCreditRepository settlementCreditRepository;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    BetRepository betRepository;

    @Autowired @Setter
    UserRepository userRepository;

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void settleCredit(
        SettleCreditContext context
    ) throws
            Status409DuplicateSubmissionException,
            Status422DataValidationError,
            Status444ReferencedEntityNotFound,
            Status470HashInvalidException,
            Status500UnhandledCasinoClientException,
            Status512ProviderNotConfiguredException,
            Status500InternalServerErrorException {

        validateRequest(context.getRequest());
        findBet(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        prePersist(context);
        callServiceCasino(context);
        persist(context);
    }

    private void validateRequest(SettleCreditRequest request) throws Status422DataValidationError {
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
        if (request.getAmount() == null) throw new Status422DataValidationError("amount is required");
        if (request.getAmount().doubleValue() < 0.0) throw new Status422DataValidationError("negative amount not allowed");
        if (StringUtil.isEmpty(request.getBetId())) throw new Status422DataValidationError("betId is required");
        if (request.getRequestId() == null) throw new Status422DataValidationError("requestId is required");
    }

    private void validateSha256(SettleCreditContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        SettleCreditRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getRequestId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(SettleCreditContext context) throws Status409DuplicateSubmissionException {
        SettleCreditRequest request = context.getRequest();
        SettlementCredit settlementCredit = settlementCreditRepository.findByRequestId(request.getRequestId());
        if (settlementCredit != null) {
            context.setSettlementCredit(settlementCredit);
            throw new Status409DuplicateSubmissionException("requestId already submitted");
        }
    }

    public void findBet(SettleCreditContext context) throws Status444ReferencedEntityNotFound,
            Status500InternalServerErrorException {
        Bet bet = betRepository.findByBetId(context.getRequest().getBetId());
        if (bet == null) throw new Status444ReferencedEntityNotFound("Bet with the supplied ID not found");
        context.setBet(bet);
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

    private void prePersist(SettleCreditContext context) {
        SettlementCredit settlementCredit = new SettlementCredit();
        settlementCredit.setBet(context.getBet());
        settlementCredit.setTimestamp(new Date(context.getRequest().getTimestamp()));
        settlementCredit.setRequestId(context.getRequest().getRequestId());
        settlementCredit.setAmount(context.getRequest().getAmount());
        context.setSettlementCredit(settlementCredit);
    }

    private void callServiceCasino(
        SettleCreditContext context
    ) throws
        Status500UnhandledCasinoClientException
    {
        SW.start("settlecredit.callservicecasino.handle." + context.getRequest().getBetId());

        try {
            long returns = CurrencyAmount.fromAmount(context.getRequest().getAmount()).toCents();

            CasinoTranType tranType = CasinoTranType.SPORTS_WIN;

            boolean free = context.getBet().getAmount() == 0;

            if (returns > 0) tranType = (free)? CasinoTranType.SPORTS_FREE_WIN: CasinoTranType.SPORTS_WIN;
            if (returns == 0) tranType = (free)? CasinoTranType.SPORTS_FREE_LOSS: CasinoTranType.SPORTS_LOSS;

            BetResponse response = casinoService.handleSettle(
                context.getDomain().getName(),
                context.getCurrency().getCode(),
                tranType,
                moduleInfo.getModuleName(),
                returns,
                context.getBet().getBetId() + ":" + context.getRequest().getRequestId(),
                context.getUser().getGuid(),
                null,
                null,
                null,
                null,
                context.getBet().getReservation().getSessionId()
            );

            log.debug("Response from casino " + response);

            // TODO Why is this a string?
            context.getSettlementCredit().setAccountingTransactionId(Long.parseLong(response.getExtSystemTransactionId()));
            context.getSettlementCredit().setBalanceAfter(CurrencyAmount.fromCents(response.getBalanceCents()).toAmount().doubleValue());
        } finally {
            SW.stop();
        }
    }

    private void persist(SettleCreditContext context) {
        context.setSettlementCredit(settlementCreditRepository.save(context.getSettlementCredit()));
    }

}
