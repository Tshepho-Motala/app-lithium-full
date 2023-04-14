package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status415NegativeBalanceException;
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
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settledebit.SettleDebitRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.context.SettleDebitContext;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementDebit;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.casino.provider.sportsbook.storage.repositories.SettlementDebitRepository;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lithium.service.user.client.objects.LoginEvent;
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

import java.util.Date;

@Service
@Slf4j
public class SettleDebitService {

    @Autowired @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @Autowired @Setter
    SettlementDebitRepository settlementDebitRepository;

    @Autowired @Setter
    CasinoClientService casinoService;

    @Autowired @Setter
    UserRepository userRepository;

    @Autowired @Setter
    private AccountingClientService accountingService;

    @Autowired
    private LoginEventClientService loginEventHelperService;

    /** There used to be a @Retryable here but since the caller is also retrying, we end up just queueing
     * unnecessary resource hogs. We should rather try to fail fast if more than one request for the same user
     * comes in. This call will block on the DB based on player guid. LIVESCORE-1634 */
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void settleDebit(SettleDebitContext context) throws Status470HashInvalidException,
            Status512ProviderNotConfiguredException, Status409DuplicateSubmissionException,
            Status500UnhandledCasinoClientException, Status422DataValidationError,
            Status471InsufficientFundsException, Status500InternalServerErrorException {

        validateRequest(context.getRequest());
        findUser(context);
        validateSha256(context);
        checkDuplicateSubmission(context);
        prePersist(context);
        callServiceAccounting(context);
        callServiceCasinoForBalance(context);
        persist(context);
    }

    private void validateRequest(SettleDebitRequest request) throws Status422DataValidationError {
        if (StringUtil.isEmpty(request.getGuid())) throw new Status422DataValidationError("guid is required");
        if (StringUtil.isEmpty(request.getSha256())) throw new Status422DataValidationError("sha256 is required");
        if (request.getTimestamp() == null) throw new Status422DataValidationError("timestamp is required");
        if (request.getAmount() == null) throw new Status422DataValidationError("amount is required");
        if (request.getAmount().doubleValue() < 0.0) throw new Status422DataValidationError("negative amount not allowed");
        if (request.getRequestId() == null) throw new Status422DataValidationError("requestId is required");
    }

    private void findUser(SettleDebitContext context) throws Status422DataValidationError, Status500InternalServerErrorException {
        try {
            String guid = context.getConvertedGuid();
            User user = userRepository.findByGuidAlwaysLock(guid);
            if (user == null) throw new Status422DataValidationError("The user guid provided has no bets");
            context.setUser(user);
            context.setCurrency(user.getDomain().getCurrency());
            context.setDomain(user.getDomain());
        } catch (CannotAcquireLockException e) {
            throw new Status500InternalServerErrorException("Unable to lock user. Did you send more than one " +
                    "request for the same user at the same time?", e);
        }
    }

    private void validateSha256(SettleDebitContext context)
            throws Status512ProviderNotConfiguredException, Status470HashInvalidException {
        SettleDebitRequest request = context.getRequest();
        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), context.getDomain().getName());
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(config.getHashPassword());
        hasher.addItem(request.getGuid());
        hasher.addItem(request.getTimestamp());
        hasher.addItem(request.getRequestId());
        hasher.validate(request.getSha256(), log, request);
    }

    private void checkDuplicateSubmission(SettleDebitContext context) throws Status409DuplicateSubmissionException {
        SettleDebitRequest request = context.getRequest();
        SettlementDebit settlementDebit = settlementDebitRepository.findByRequestId(request.getRequestId());
        if (settlementDebit != null) {
            context.setSettlementDebit(settlementDebit);
            throw new Status409DuplicateSubmissionException("requestId already submitted");
        }
    }

    private void prePersist(SettleDebitContext context) {
        SettlementDebit settlementDebit = new SettlementDebit();
        settlementDebit.setCurrency(context.getCurrency());
        settlementDebit.setTimestamp(new Date(context.getRequest().getTimestamp()));
        settlementDebit.setRequestId(context.getRequest().getRequestId());
        settlementDebit.setAmount(context.getRequest().getAmount());
        context.setSettlementDebit(settlementDebit);
    }

    private void callServiceAccounting(SettleDebitContext context) throws Status500InternalServerErrorException,
            Status471InsufficientFundsException {

        try {
            AdjustmentRequest request = AdjustmentRequest.builder()
                    .domainName(context.getDomain().getName())
                    .build();

            String[] labels = new String[3];
            labels[0] = "provider_guid=" + moduleInfo.getModuleName();
            labels[1] = "transaction_id=" + context.getRequest().getRequestId();

            // FIXME: We're missing a link to the bet/reservation from here. There is a requestId in the context, but i'm
            //        not convinced it can be used to link the bet. I've done a check on staging on settlement_credit,
            //        and the request id contained in that table does not link up to the bet.

            LoginEvent loginEvent = loginEventHelperService.findLastLoginEventByUserGuidOrNull(context.getUser().getGuid());

            if (loginEvent != null) {
                labels[2] = "login_event_id=" + loginEvent.getId();
            }

            request.add(AdjustmentRequestComponent.builder()
                    .accountTypeCode(CasinoAccountTypeCodes.PLAYER_BALANCE.toString())
                    .accountCode(CasinoAccountCodes.PLAYER_BALANCE.toString())
                    .allowNegativeAdjust(true)
                    .amountCents(CurrencyAmount.fromAmount(context.getSettlementDebit().getAmount()).toCents() * -1)
                    .authorGuid(context.getUser().getGuid())
                    .ownerGuid(context.getUser().getGuid())
                    .date(DateTime.now())
                    .contraAccountCode(CasinoAccountCodes.SPORTS_DEBIT.toString())
                    .contraAccountTypeCode(CasinoAccountTypeCodes.SPORTS_DEBIT.toString())
                    .currencyCode(context.getCurrency().getCode())
                    .transactionTypeCode(CasinoTranType.SPORTS_DEBIT.toString())
                    .domainName(context.getDomain().getName())
                    .labels(labels)
                    .build());

            AdjustmentResponse response = accountingService.adjust(request);
            context.getSettlementDebit().setAccountingTransactionId(response.getAdjustments().get(0).getTransactionId());

        } catch (Status415NegativeBalanceException ne) {
            throw new Status471InsufficientFundsException();
        } catch (Exception e) {
            throw new Status500InternalServerErrorException("service-accounting", ExceptionMessageUtil.allMessages(e), e);
        }

    }

    private void callServiceCasinoForBalance(SettleDebitContext context)
            throws Status500UnhandledCasinoClientException {
        lithium.math.CurrencyAmount balance =
                lithium.math.CurrencyAmount.fromCents(
                        casinoService.getPlayerBalance(
                                context.getDomain().getName(),
                                context.getUser().getGuid(),
                                context.getCurrency().getCode())
                                .getBalanceCents());
        context.getSettlementDebit().setBalanceAfter(balance.toAmount().doubleValue());
    }

    private void persist(SettleDebitContext context) {
        context.setSettlementDebit(settlementDebitRepository.save(context.getSettlementDebit()));
    }

}
