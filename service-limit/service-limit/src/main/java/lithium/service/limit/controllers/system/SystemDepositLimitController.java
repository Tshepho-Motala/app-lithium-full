package lithium.service.limit.controllers.system;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status477BalanceLimitReachedException;
import lithium.service.limit.client.exceptions.Status479DepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status486DailyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status487WeeklyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status488MonthlyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.services.DepositLimitService;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/system/depositlimit")
public class SystemDepositLimitController {
	@Autowired DepositLimitService depositLimitService;

	@TimeThisMethod
	@PostMapping("/allowedToDeposit")
	public boolean allowedToDeposit(
		@RequestParam("playerGuid") String playerGuid,
		@RequestParam("amountCents") Long amountCents,
		@RequestParam("locale") String locale
	) throws
			Status477BalanceLimitReachedException,
			Status478TimeSlotLimitException,
			Status486DailyDepositLimitReachedException,
			Status487WeeklyDepositLimitReachedException,
			Status488MonthlyDepositLimitReachedException,
			Status500LimitInternalSystemClientException,
			Status438PlayTimeLimitReachedException {
		log.debug("depositlimit/allowedToDeposit :: "+playerGuid+" amount: "+amountCents+" locale: "+locale);
		return depositLimitService.allowedToDeposit(playerGuid, amountCents, locale);
	}

    @TimeThisMethod
    @PostMapping("/allowed-deposit-value")
    public BigDecimal getAllowedDepositValue(
            @RequestParam(name = "playerGuid") String playerGuid,
            @RequestParam(name = "locale") String locale
    ) throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException, Status479DepositLimitReachedException,
			Status478TimeSlotLimitException {
        return depositLimitService.getAllowedDepositValue(playerGuid, locale);
    }

	@PostMapping
	public PlayerLimit save(
		@RequestParam(name="playerGuid") String playerGuid,
		@RequestParam(name="granularity") Granularity granularity,
		@RequestParam(name="amount") String amount,
		LithiumTokenUtil lithiumTokenUtil
	) throws
		Status100InvalidInputDataException,
		Status480PendingDepositLimitException,
		Status481DomainDepositLimitDisabledException,
		Status500LimitInternalSystemClientException,
		Status550ServiceDomainClientException
	{
		//TODO: Locale?
		return depositLimitService.saveBO(playerGuid, granularity, amount, playerGuid, Locale.US, lithiumTokenUtil);
	}
}
