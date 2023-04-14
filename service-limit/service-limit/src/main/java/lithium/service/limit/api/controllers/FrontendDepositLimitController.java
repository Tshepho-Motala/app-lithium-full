package lithium.service.limit.api.controllers;

import lithium.metrics.TimeThisMethod;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status477BalanceLimitReachedException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status486DailyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status487WeeklyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status488MonthlyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status498SupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status499EmptySupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.data.entities.PlayerLimitSetRequest;
import lithium.service.limit.services.DepositLimitService;
import lithium.service.limit.services.PlayerTimeSlotLimitService;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/frontend/depositlimit/v1")
public class FrontendDepositLimitController {
	@Autowired DepositLimitService depositLimitService;
	@Autowired
	private PlayerTimeSlotLimitService playerTimeSlotLimitService;

	@TimeThisMethod
	@GetMapping("/allowedToDeposit")
	public boolean allowedToDeposit(
		@RequestParam("amountCents") Long amountCents,
		LithiumTokenUtil tokenUtil
	) throws
			Status477BalanceLimitReachedException,
			Status478TimeSlotLimitException,
			Status486DailyDepositLimitReachedException,
			Status487WeeklyDepositLimitReachedException,
			Status488MonthlyDepositLimitReachedException,
			Status500LimitInternalSystemClientException,
			Status478TimeSlotLimitException,
			Status438PlayTimeLimitReachedException {
		return depositLimitService.allowedToDeposit(tokenUtil.guid(), amountCents, null);
	}

 	@GetMapping
	public List<PlayerLimitFE> get(
		LithiumTokenUtil tokenUtil,
		Locale locale
	) throws
		Status481DomainDepositLimitDisabledException,
		Status500LimitInternalSystemClientException
	{
		return depositLimitService.findAllFE(tokenUtil.guid(), locale);
	}

	@PostMapping
	public List<PlayerLimitFE> set(
			@RequestBody PlayerLimitSetRequest playerLimitSetRequest,
			Locale locale,
			LithiumTokenUtil util
	) throws Status100InvalidInputDataException,
			Status498SupposedDepositLimitException,
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException {
		return depositLimitService.saveFE(util.guid(), playerLimitSetRequest.getGranularity(), playerLimitSetRequest.getAmounts(), locale, util);
	}

	@PostMapping("/supposed")
	public PlayerLimitFE proceedSupposedLimit (
			@RequestParam(name="granularity") Granularity granularity,
			@RequestParam(name="action") boolean action,
			Locale locale,
			LithiumTokenUtil token
	) throws Status499EmptySupposedDepositLimitException, Status500LimitInternalSystemClientException {
			return depositLimitService.proceedSupposedLimit(token.guid(), granularity, action, locale, token);
	}
}
