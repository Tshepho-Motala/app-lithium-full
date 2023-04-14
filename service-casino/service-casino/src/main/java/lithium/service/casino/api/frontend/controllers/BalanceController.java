package lithium.service.casino.api.frontend.controllers;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.Response;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.exceptions.Status410AccountingAccountTypeNotFoundException;
import lithium.service.accounting.exceptions.Status411AccountingUserNotFoundException;
import lithium.service.accounting.exceptions.Status412AccountingDomainNotFoundException;
import lithium.service.accounting.exceptions.Status413AccountingCurrencyNotFoundException;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.api.frontend.schema.BalanceV2Response;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class BalanceController extends FrontendController {
    @Autowired AccountingClientService accountingClientService;
    @Autowired UserApiInternalClientService userService;
    @Autowired LimitInternalSystemService limitInternalSystemService;
    @Autowired CachingDomainClientService domainClientService;

    @PostMapping("/frontend/balance/v1/usable")
    @ResponseBody
    public BigDecimal getUsableBalance(
            LithiumTokenUtil tokenUtil
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status511UpstreamServiceUnavailableException,
            Status496PlayerCoolingOffException {

        allowedToTransact(tokenUtil);

        return getBalance(tokenUtil, false);
    }

    @PostMapping("/frontend/balance/v2/usable")
    @ResponseBody
    public BalanceV2Response getUsableBalanceV2(
            LithiumTokenUtil tokenUtil
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status511UpstreamServiceUnavailableException,
            Status496PlayerCoolingOffException,
            UserNotFoundException,
            UserClientServiceFactoryException,
            Status500LimitInternalSystemClientException {

        allowedToTransact(tokenUtil);

        BigDecimal balance = getBalance(tokenUtil, false);
        Integer verificationLevel = getVerificationStatusLevel(tokenUtil);

        return BalanceV2Response.builder()
                .balance(balance)
                .verificationLevel(verificationLevel)
                .build();
    }


    @PostMapping("/frontend/balance/v1/withdrawable")
    @ResponseBody
    public BigDecimal getWithdrawableBalance(
            LithiumTokenUtil tokenUtil
    ) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status473DomainBettingDisabledException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status511UpstreamServiceUnavailableException,
            Status496PlayerCoolingOffException {

        allowedToTransact(tokenUtil);

        return getBalance(tokenUtil, true);
    }

    private BigDecimal getBalance(LithiumTokenUtil tokenUtil, boolean withdrawable
    ) throws
            Status473DomainBettingDisabledException,
            Status511UpstreamServiceUnavailableException {

        BigDecimal resultBalance = new BigDecimal(0.00);
        Response<Map<String,Long>> balanceResponse = null;
        try {
            balanceResponse = accountingClientService.getBalanceMapByAccountType(
                    tokenUtil.domainName(),
                    CasinoTranType.PLAYERBALANCE.value(),
                    cachingDomainClientService.getDefaultDomainCurrency(tokenUtil.domainName()),
                    tokenUtil.guid());
        } catch (Status510AccountingProviderUnavailableException |
                Status550ServiceDomainClientException hardException) {
            log.error("Error performing balance lookup: " + tokenUtil.guid() +" message: "+ hardException.getMessage(), hardException);
            throw new Status511UpstreamServiceUnavailableException(hardException.getMessage());
        } catch (Status410AccountingAccountTypeNotFoundException |
                Status411AccountingUserNotFoundException |
                Status412AccountingDomainNotFoundException |
                Status413AccountingCurrencyNotFoundException softException) {
            log.debug("Soft failure on balance request: " + tokenUtil.guid() +" message: "+ softException.getMessage());
            return resultBalance;
        }

        long balance = balanceResponse.getData().getOrDefault("PLAYER_BALANCE", 0L);

        if (Boolean.parseBoolean(domainClientService.getDomainSetting(tokenUtil.domainName(), DomainSettings.OVERRIDE_NEGATIVE_BALANCE_DISPLAY))) {
            balance = balance < 0 ? 0 : balance;
        }

        if (!withdrawable) { // Only do this if we want total balance
            balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS", 0L);
            balance += balanceResponse.getData().getOrDefault("PLAYER_BALANCE_CASINO_BONUS_PENDING", 0L);
        }

        CurrencyAmount currencyBalance = CurrencyAmount.fromCents(balance);
        resultBalance = currencyBalance.toAmount();
        return resultBalance;
    }

    private Integer getVerificationStatusLevel(LithiumTokenUtil tokenUtil) throws UserNotFoundException, UserClientServiceFactoryException, Status500LimitInternalSystemClientException {
        User user = userService.getUserByGuid(tokenUtil.guid());
        Integer verificationStatusLevel = limitInternalSystemService.getVerificationStatusLevel(user.getVerificationStatus());
        return verificationStatusLevel;
    }
}
