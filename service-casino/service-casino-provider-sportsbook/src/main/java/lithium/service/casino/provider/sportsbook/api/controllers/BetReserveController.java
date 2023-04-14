package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betreserve.BetReserveResponse;
import lithium.service.casino.provider.sportsbook.context.BetReserveContext;
import lithium.service.casino.provider.sportsbook.services.BetReserveService;
import lithium.service.casino.provider.sportsbook.services.PubSubBetService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
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
public class BetReserveController {
    @Autowired
    @Setter
    BetReserveService service;

    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    PubSubBetService pubSubBetService;

    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @PostMapping("/bet/reserve")
    public BetReserveResponse betReserve(
            @RequestParam(defaultValue = "en") String locale,
            @RequestBody BetReserveRequest request
    ) throws
            Status405UserDisabledException, Status422DataValidationError,
            Status470HashInvalidException, Status471InsufficientFundsException,
            Status473DomainBettingDisabledException, Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException, Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException, Status496PlayerCoolingOffException,
            Status500UserInternalSystemClientException, Status500LimitInternalSystemClientException,
            Status500UnhandledCasinoClientException, Status500InternalServerErrorException,
            Status512ProviderNotConfiguredException, Status550ServiceDomainClientException,
            Status493MonthlyLossLimitReachedException, Status492DailyLossLimitReachedException,
            Status495MonthlyWinLimitReachedException, Status494DailyWinLimitReachedException,
            Status482PlayerBetPlacementNotAllowedException, Status478TimeSlotLimitException,
            Status438PlayTimeLimitReachedException {
        String[] domainAndPlayer = request.getGuid().split("/");
        localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);
        BetReserveContext context = BetReserveContext.builder()
                .locale(locale)
                .request(request)
                .response(new BetReserveResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();
        try {
            //QWD - Added here for LSPLAT-1429
            context = service.checkRequired(context);

            log.debug("betreserve pre " + context);
            try {
                service.betReserveInit(context);
                service.betReserve(context);
            } catch (Status409DuplicateSubmissionException e) {
                log.warn("betreserve duplicate " + e + " " + context);
            }
            if (context.getReservation().getBalanceAfter() != null)
                context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getReservation().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setBonusUsedAmount(CurrencyAmount.fromAmount(context.getReservation().getBonusUsedAmount()));
            context.getResponse().setTransactionId(context.getReservation().getAccountingTransactionId());
            if (pubSubBetService.isChannelActivated(context.getDomain().getName())) {
                pubSubBetService.buildAndSendPubSubBetReserveMessage(context.getRequest(), context.getUser().getId());
            }
            log.debug("betreserve post " + context);
            return context.getResponse();
        } catch (Status471InsufficientFundsException insufficientFundsException) {
            log.warn("Player has insufficient funds: request: " + request);
            throw insufficientFundsException;
        } catch (ErrorCodeException ec) {
            log.warn("betreserve " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("betreserve " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
