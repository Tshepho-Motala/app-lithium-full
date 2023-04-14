package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status472NotAllowedToTransactException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status408ReservationClosedException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve.BetDebitReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve.BetDebitReserveResponse;
import lithium.service.casino.provider.sportsbook.context.BetDebitReserveContext;
import lithium.service.casino.provider.sportsbook.services.BetDebitReserveService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.math.CurrencyAmount;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BetDebitReserveController {
    @Autowired @Setter
    BetDebitReserveService service;

    @Autowired
    GuidConverterService guidConverterService;

    @PostMapping("/bet/debitreserve")
    public BetDebitReserveResponse betDebitReserve(
        @RequestParam(defaultValue = "en_US") String locale,
        @RequestBody BetDebitReserveRequest request
    ) throws
            Status405UserDisabledException,
            Status422DataValidationError,
            Status423InvalidBonusTokenException,
            Status424InvalidBonusTokenStateException,
            Status444ReferencedEntityNotFound,
            Status470HashInvalidException,
            Status471InsufficientFundsException,
            Status472NotAllowedToTransactException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status500UnhandledCasinoClientException,
            Status500InternalServerErrorException,
            Status512ProviderNotConfiguredException,
            Status438ReservationPendingException {

        BetDebitReserveContext context = BetDebitReserveContext.builder()
                .locale(locale)
                .request(request)
                .response(new BetDebitReserveResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();

        boolean doCheck = true;

        try {
            log.info("betdebitreserve pre " + context);
            try {
                // Remove after VB migration of open bets
                if (service.shouldUseOpenBetOperatorMigrationExecution(context.getConvertedGuid())) {
                    User user = service.findOrCreateUserByGuid(context.getConvertedGuid());
                    context.setUser(user);
                    context.setDomain(user.getDomain());
                    service.betDebitReserveOpenBetOperatorMigration(context);
                } else {
                    service.betDebitReserve(context);
                }
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("betdebitreserve duplicate " + de + " " + context);
            }
            catch (Status408ReservationClosedException rc) {
                log.warn("betdebitreserve closed " + rc + " " + context);
                context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getReservation().getBalanceAfter()));
                doCheck = false;
            }
            if (doCheck) {
                if ((context.getBet() != null) && (context.getBet().getBalanceAfter() != null)) {
                    context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getBet().getBalanceAfter()));
                } else {
                    if ((context.getReservation() != null) && (context.getReservation().getBalanceAfter() != null)) {
                        context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getReservation().getBalanceAfter()));
                    }
                }
            }
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());

            if(!ObjectUtils.isEmpty(context.getBet()))context.getResponse().setTransactionId(context.getBet().getId());

            log.info("betdebitreserve post " + context);
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("betdebitreserve " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("betdebitreserve " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
