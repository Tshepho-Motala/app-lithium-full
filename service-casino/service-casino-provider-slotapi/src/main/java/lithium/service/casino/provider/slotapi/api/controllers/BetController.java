package lithium.service.casino.provider.slotapi.api.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetRequest;
import lithium.service.casino.provider.slotapi.api.schema.bet.BetResponse;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.services.BetService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
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

import java.security.Principal;

@RestController
@Slf4j
public class BetController {

    @Autowired @Setter
    BetService service;

    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @PostMapping("/bet")
    public BetResponse bet(
        @RequestParam(defaultValue = "en") String locale,
        @RequestBody BetRequest betRequest,
        Principal principal
    ) throws
        Status401UnAuthorisedException,
        Status405UserDisabledException,
        Status422DataValidationError,
        Status423InvalidBonusTokenException,
        Status424InvalidBonusTokenStateException,
        Status438PlayTimeLimitReachedException,
        Status470HashInvalidException,
        Status471InsufficientFundsException,
        Status473DomainBettingDisabledException,
        Status474DomainProviderDisabledException,
        Status478TimeSlotLimitException,
        Status483PlayerCasinoNotAllowedException,
        Status484WeeklyLossLimitReachedException,
        Status485WeeklyWinLimitReachedException,
        Status490SoftSelfExclusionException,
        Status491PermanentSelfExclusionException,
        Status492DailyLossLimitReachedException,
        Status493MonthlyLossLimitReachedException,
        Status494DailyWinLimitReachedException,
        Status495MonthlyWinLimitReachedException,
        Status496PlayerCoolingOffException,
        Status500UnhandledCasinoClientException,
        Status500ProviderNotConfiguredException,
        Status500LimitInternalSystemClientException,
            Status500UserInternalSystemClientException
    {
        try {
            localeContextProcessor.setLocaleContextHolder(locale, principal);
            BetResponse response = service.bet(betRequest, principal, locale);
            log.debug("bet " + betRequest + " " + response);
            return response;
        } catch (Status471InsufficientFundsException insufficientFundsException) {
            log.info("Player has insufficient funds: betRequest: " + betRequest);
            throw insufficientFundsException;
        } catch (Exception e) {
            log.warn("bet error " + ExceptionMessageUtil.allMessages(e) + " " + betRequest);
            throw e;
        }
    }
}
