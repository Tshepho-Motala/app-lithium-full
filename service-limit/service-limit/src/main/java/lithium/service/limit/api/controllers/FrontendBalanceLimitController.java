package lithium.service.limit.api.controllers;

import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.services.BalanceLimitService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/frontend/balance-limit/v1")
public class FrontendBalanceLimitController {
	@Autowired
	private BalanceLimitService balanceLimitService;

 	@GetMapping
	public List<PlayerLimitFE> get(LithiumTokenUtil tokenUtil) throws
            Status476DomainBalanceLimitDisabledException,
			Status500LimitInternalSystemClientException
	{
		return balanceLimitService.findAllFE(tokenUtil.guid());
	}

	@PostMapping
	public List<PlayerLimitFE> set(@RequestParam BigDecimal amount, LithiumTokenUtil util) throws
			Status100InvalidInputDataException,
            Status476DomainBalanceLimitDisabledException,
			Status500LimitInternalSystemClientException {
		return balanceLimitService.saveFE(util.guid(), amount, util);
	}

	@DeleteMapping("/pending")
	public void cancelPendingLimit (LithiumTokenUtil token) {
		balanceLimitService.removePending(token.guid(), token.guid(), token);
	}
}
