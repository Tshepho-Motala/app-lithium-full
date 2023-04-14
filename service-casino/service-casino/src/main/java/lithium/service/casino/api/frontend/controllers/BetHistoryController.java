package lithium.service.casino.api.frontend.controllers;

import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.casino.api.frontend.schema.BetHistorySummary;
import lithium.service.casino.service.BetHistoryService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/bethistory")
public class BetHistoryController {
	@Autowired private BetHistoryService betHistoryService;

	@GetMapping("/summary")
	private BetHistorySummary betHistorySummary(
		@RequestParam("last") Integer last,
		@RequestParam("granularity") Integer granularity,
		LithiumTokenUtil tokenUtil
	) throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
		return betHistoryService.getBetHistorySummary(tokenUtil.domainName(), tokenUtil.guid(), last, granularity);
	}
}
