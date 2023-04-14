package lithium.service.limit.api.controllers;

import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.services.PlayerLimitsSummaryService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/frontend/limits-summary/v1")
public class FrontendLimitsSummaryController {

	@Autowired
	private PlayerLimitsSummaryService playerLimitsSummaryService;

	@GetMapping("/find-player-summary-limits")
	public PlayerLimitSummaryFE get(LithiumTokenUtil tokenUtil, Locale locale) throws
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException, Status414UserNotFoundException,
			Status438PlayTimeLimitConfigurationNotFoundException,
			Status476DomainBalanceLimitDisabledException, Status550ServiceDomainClientException {
		return playerLimitsSummaryService.findPlayerLimitSummary(tokenUtil.guid(), locale);
	}
}
