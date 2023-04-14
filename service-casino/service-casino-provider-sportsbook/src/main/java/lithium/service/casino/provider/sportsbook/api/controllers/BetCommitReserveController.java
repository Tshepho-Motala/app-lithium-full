package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve.BetCommitReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve.BetCommitReserveResponse;
import lithium.service.casino.provider.sportsbook.context.BetCommitReserveContext;
import lithium.service.casino.provider.sportsbook.services.BetCommitReserveService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
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
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BetCommitReserveController {
    @Autowired @Setter
    BetCommitReserveService service;

    @Autowired
    GuidConverterService guidConverterService;

    @PostMapping("/bet/commitreserve")
    public BetCommitReserveResponse betCommitReserve(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody BetCommitReserveRequest request)
            throws Status401UnAuthorisedException, Status470HashInvalidException,
            Status444ReferencedEntityNotFound, Status500UnhandledCasinoClientException,
            Status422DataValidationError, Status500InternalServerErrorException,
            Status512ProviderNotConfiguredException, Status511UpstreamServiceUnavailableException,
            Status471InsufficientFundsException, Status405UserDisabledException,
            Status474DomainProviderDisabledException, Status473DomainBettingDisabledException,
            Status550ServiceDomainClientException, Status493MonthlyLossLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status496PlayerCoolingOffException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status492DailyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status484WeeklyLossLimitReachedException,
            Status438ReservationPendingException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        BetCommitReserveContext context = BetCommitReserveContext.builder()
                .locale(locale)
                .request(request)
                .response(new BetCommitReserveResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();
        try {
            log.info("betcommitreserve pre " + context);
            try {
                // Remove after VB migration of open bets
                if (service.shouldUseOpenBetOperatorMigrationExecution(context.getConvertedGuid())) {
                    service.betCommitReserveOpenBetOperatorMigration(context);
                } else {
                    service.betCommitReserve(context);
                }
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("betcommitreserve duplicate " + de + " " + context);
            }
            context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getReservationCommit().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setTransactionId(context.getReservationCommit().getAccountingTransactionId());
            log.info("betcommitreserve post " + context);
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("betcommitreserve " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("betcommitreserve " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
