package lithium.service.limit.client;

import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status477BalanceLimitReachedException;
import lithium.service.limit.client.exceptions.Status486DailyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status487WeeklyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status488MonthlyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status479DepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name="service-limit")
public interface DepositLimitClient {
	@RequestMapping(method=RequestMethod.POST, path="/system/depositlimit/allowedToDeposit")
	boolean allowedToDeposit(
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
			Status438PlayTimeLimitReachedException;

    @RequestMapping(method = RequestMethod.POST, path = "/system/depositlimit/allowed-deposit-value")
    BigDecimal getAllowedDepositValue(
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("locale") String locale
    ) throws
        Status500LimitInternalSystemClientException,
        Status550ServiceDomainClientException,
		Status478TimeSlotLimitException,
        Status479DepositLimitReachedException;
}
