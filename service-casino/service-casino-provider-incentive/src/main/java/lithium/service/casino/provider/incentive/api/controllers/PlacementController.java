package lithium.service.casino.provider.incentive.api.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequest;
import lithium.service.casino.provider.incentive.api.schema.placement.response.PlacementResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.services.PlacementService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
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

import java.security.Principal;

@RestController
@Slf4j
public class PlacementController {

    @Autowired @Setter
    PlacementService service;

    @Autowired @Setter
    LocaleContextProcessor localeContextProcessor;

    @PostMapping("/placement")
    public PlacementResponse placement(
        @RequestParam(defaultValue = "en") String locale,
        @RequestBody PlacementRequest placementRequest,
        Principal principal
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status409DuplicateSubmissionException,
            Status422DataValidationError,
            Status423InvalidBonusTokenException,
            Status424InvalidBonusTokenStateException,
            Status438PlayTimeLimitReachedException,
            Status470HashInvalidException,
            Status471InsufficientFundsException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status478TimeSlotLimitException,
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
            Status500UserInternalSystemClientException,
            Status550ServiceDomainClientException,
            Status482PlayerBetPlacementNotAllowedException,
            Status438PlayTimeLimitReachedException {
        try {
            localeContextProcessor.setLocaleContextHolder(locale, principal);
            PlacementResponse response = service.placement(placementRequest, principal, locale);
            log.debug("placement " + placementRequest + " " + response);
            return response;
        }catch (Status471InsufficientFundsException insufficientFundsException){
            log.info("Player has insufficient funds: context: " + placementRequest);
            throw insufficientFundsException;
        } catch (Exception e) {
            log.warn("placement error " + ExceptionMessageUtil.allMessages(e) + " " + placementRequest);
            throw e;
        }
    }
}
