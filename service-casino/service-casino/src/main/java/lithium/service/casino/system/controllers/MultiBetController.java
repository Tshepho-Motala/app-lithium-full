package lithium.service.casino.system.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.Response;
import lithium.service.casino.client.MultiBetClient;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.service.CasinoBalanceAdjustmentService;
import lithium.service.casino.service.CasinoService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
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
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MultiBetController
        implements MultiBetClient {

    private final CasinoBalanceAdjustmentService balanceAdjustmentService;
    private final CasinoService casinoService;
    private final LithiumServiceClientFactory services;
    private final UserApiInternalClientService userApiInternalClientService;
    private final CachingDomainClientService cachingDomainClientService;
    private final ProviderClientService providerClientService;
    private final LimitInternalSystemService limitInternalSystemService;
    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @Override
    @RequestMapping("/system/casino/negative-balance-asjust/v1")
    @ResponseBody
    public BalanceAdjustmentResponse negativeBalanceAdjust(
            @RequestBody
                    BalanceAdjustmentRequest request,
            @RequestParam(name = "locale", defaultValue = "en")
                    String locale
    ) {
        Response<BalanceAdjustmentResponse> balanceAdjustmentResponse = balanceAdjustmentService.processNegativeBalanceAdjustment(
                request,
                new Locale(locale)
        );

        return balanceAdjustmentResponse.getData();
    }

    @RequestMapping("/system/casino/multi-bet/v1")
    @Override
    @ResponseBody
    public BalanceAdjustmentResponse multiBetV1(
            @RequestBody BalanceAdjustmentRequest request,
            @RequestParam(name = "locale", defaultValue = "en") String locale
    )
            throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status471InsufficientFundsException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status496PlayerCoolingOffException,
            Status511UpstreamServiceUnavailableException,
            Status550ServiceDomainClientException,
            Status478TimeSlotLimitException,
            Status438PlayTimeLimitReachedException {
        String[] domainAndPlayer = request.getUserGuid().split("/");
        localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);
        performAllAccessChecks(
                request,
                locale
        );

        Response<BalanceAdjustmentResponse> balanceAdjustmentResponse =
                balanceAdjustmentService.processBalanceAdjustment(
                        request,
                        new Locale(locale)
                );

        return balanceAdjustmentResponse.getData();
    }

    /**
     * This will do all the access checks, it should most likely go and live in some central lib where all checks can be done
     *
     * @param request
     */
    private void performAllAccessChecks(
            BalanceAdjustmentRequest request,
            String locale
    )
            throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status474DomainProviderDisabledException,
            Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492DailyLossLimitReachedException,

            Status493MonthlyLossLimitReachedException,
            Status494DailyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status496PlayerCoolingOffException,
            Status511UpstreamServiceUnavailableException,
            Status550ServiceDomainClientException,
            Status478TimeSlotLimitException,
            Status438PlayTimeLimitReachedException {

        if (!request.getPerformAccessChecks()) {
            return;
        }

        try {
            String currency = (request.getCurrencyCode() != null && !request
                    .getCurrencyCode()
                    .isEmpty())
                    ? request.getCurrencyCode()
                    : casinoService.getCurrency(request.getDomainName());

            limitInternalSystemService.checkPlayerRestrictions(
                    request.getUserGuid(),
                    locale
            );
            limitInternalSystemService.checkLimits(
                    request.getDomainName(),
                    request.getUserGuid(),
                    currency,
                    request.getTotalWageredBetAmountCents(),
                    locale
            );
            limitInternalSystemService.checkPlayerBetPlacementAllowed(request.getUserGuid());
        } catch (
                Status438PlayTimeLimitReachedException |
                        Status478TimeSlotLimitException |
                        Status484WeeklyLossLimitReachedException |
                        Status485WeeklyWinLimitReachedException |
                        Status490SoftSelfExclusionException |
                        Status491PermanentSelfExclusionException |
                        Status492DailyLossLimitReachedException |
                        Status493MonthlyLossLimitReachedException |
                        Status494DailyWinLimitReachedException |
                        Status495MonthlyWinLimitReachedException |
                        Status496PlayerCoolingOffException errorCodeException
        ) {
            throw errorCodeException;
        } catch (Exception ex) {
            log.error(
                    "Problem performing player restriction and limit checks for bet request: " + request,
                    ex
            );
            throw new Status511UpstreamServiceUnavailableException(ex.getMessage() + " for limit service");
        }
        cachingDomainClientService.checkBettingEnabled(
                request.getDomainName(),
                locale
        );
        providerClientService.checkProviderEnabled(
                request.getDomainName(),
                request
                        .getProviderGuid()
                        .split("/")[1],
                locale
        );
        try {
            userApiInternalClientService.performUserChecks(
                    request.getUserGuid(),
                    locale,
                    null,
                    true,
                    false,
                    false
            );
        } catch (Status500UserInternalSystemClientException e) {
            throw new Status511UpstreamServiceUnavailableException(e.getMessage() + " for user service");
        }
    }
}
