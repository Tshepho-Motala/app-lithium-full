package lithium.service.limit.controllers.system;

import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.services.BalanceLimitService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/system/balance-limit")
public class SystemBalanceLimitController {
	@Autowired
	private BalanceLimitService balanceLimitService;

	@PostMapping
	public PlayerLimit save(@RequestParam(name="playerGuid") String playerGuid, @RequestParam(name="amount") String amount, LithiumTokenUtil util
	) throws
			Status100InvalidInputDataException,
			Status480PendingDepositLimitException,
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException,
			Status550ServiceDomainClientException,
			Status510AccountingProviderUnavailableException,
            Status476DomainBalanceLimitDisabledException {
		return balanceLimitService.save(playerGuid, new BigDecimal(amount).movePointLeft(2), playerGuid, util).getCurrent();
	}
}
