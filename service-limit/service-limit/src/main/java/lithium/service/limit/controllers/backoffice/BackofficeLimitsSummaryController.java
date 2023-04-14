package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.services.PlayerLimitsSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/backoffice/limits-summary/{domainName}")
public class BackofficeLimitsSummaryController {


	private final PlayerLimitsSummaryService playerLimitsSummaryService;

	public BackofficeLimitsSummaryController(PlayerLimitsSummaryService playerLimitsSummaryService) {
		this.playerLimitsSummaryService = playerLimitsSummaryService;
	}

	@GetMapping("/find-player-summary-limits")
	public Response<PlayerLimitSummaryFE> findPlayerSummaryLimits(
			@PathVariable("domainName") String domainName,
			@RequestParam("playerGuid") String playerGuid,
			Locale locale) throws
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException, Status476DomainBalanceLimitDisabledException,
			Status550ServiceDomainClientException{
		PlayerLimitSummaryFE playerLimitSummaryFE = playerLimitsSummaryService.findPlayerLimitSummary(playerGuid, locale);
		return Response
				.<PlayerLimitSummaryFE>builder()
				.data(playerLimitSummaryFE)
				.status(Response.Status.OK)
				.build();
	}
}
