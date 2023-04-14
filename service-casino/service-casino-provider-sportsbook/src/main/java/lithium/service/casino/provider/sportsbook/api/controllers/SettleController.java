package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status472NotAllowedToTransactException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiResponse;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditResponse;
import lithium.service.casino.provider.sportsbook.context.SettleCreditContext;
import lithium.service.casino.provider.sportsbook.context.SettleMultiContext;
import lithium.service.casino.provider.sportsbook.services.OpenBetsOperatorMigrationService;
import lithium.service.casino.provider.sportsbook.services.SettleCreditService;
import lithium.service.casino.provider.sportsbook.services.SettleService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.math.CurrencyAmount;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@Slf4j
public class SettleController {
    @Autowired @Setter
    SettleService service;

    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    OpenBetsOperatorMigrationService openBetsOperatorMigrationService;

    @PostMapping("/settle")
    public SettleMultiResponse settleMulti(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody SettleMultiRequest multiRequest
    ) throws
            Status401UnAuthorisedException, Status405UserDisabledException, Status422DataValidationError,
            Status444ReferencedEntityNotFound, Status470HashInvalidException,
            Status471InsufficientFundsException, Status472NotAllowedToTransactException,
            Status473DomainBettingDisabledException, Status474DomainProviderDisabledException,
            Status500InternalServerErrorException, Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException, Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException, Status493MonthlyLossLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status496PlayerCoolingOffException, Status491PermanentSelfExclusionException,
            Status490SoftSelfExclusionException, Status492DailyLossLimitReachedException,
            Status494DailyWinLimitReachedException, Status484WeeklyLossLimitReachedException, Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        SettleMultiContext context = SettleMultiContext.builder()
                .request(multiRequest)
                .response(new SettleMultiResponse())
                .locale(new Locale(locale))
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(multiRequest.getGuid()))
                .build();

        try {
            log.debug("settlemulti pre " + context);
            try {
                service.settle(context);
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("settlemulti duplicate " + de + " " + context);
            }

            context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getSettlement().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setTransactionId(context.getSettlement().getId());

            log.debug("settlemulti post " + context);
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("settlemulti " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("settlemulti " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
