package lithium.service.limit.controllers.system;

import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.services.PlayerLimitsSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/system/limits-summary")
public class SystemLimitsSummaryController {

	@Autowired
	private PlayerLimitsSummaryService playerLimitsSummaryService;

	@GetMapping("/find-player-summary-limits")
	public PlayerLimitSummaryFE get(@RequestParam("playerGuid") String playerGuid) throws
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException,
			Status476DomainBalanceLimitDisabledException, Status550ServiceDomainClientException,Status500LimitInternalSystemClientException{
		return playerLimitsSummaryService.findPlayerLimitSummary(playerGuid, Locale.getDefault());
	}
}
