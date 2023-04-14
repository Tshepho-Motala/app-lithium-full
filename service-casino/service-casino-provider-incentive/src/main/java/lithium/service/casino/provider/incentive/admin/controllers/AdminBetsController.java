package lithium.service.casino.provider.incentive.admin.controllers;

import lithium.service.Response;
import lithium.service.casino.provider.incentive.services.BetHistoryService;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/bets/")
public class AdminBetsController {
	@Autowired BetHistoryService service;

	@GetMapping("/resultcodes")
	public Response<Iterable<SettlementResult>> resultCodes() {
		return Response.<Iterable<SettlementResult>>builder()
			.data(service.findAllResultCodes())
			.status(Response.Status.OK)
			.build();
	}

	@GetMapping("/table")
	public DataTableResponse<Bet> table(
		@RequestParam(name="betId", required = false) String betId,
		@RequestParam(name="userGuid", required = false) String userGuid,
		@RequestParam(name="eventName", required = false) String eventName,
		@RequestParam(name="sport", required = false) String sport,
		@RequestParam(name="market", required = false) String market,
		@RequestParam(name="competition", required = false) String competition,
		@RequestParam(name="isSettled", required = false) Boolean isSettled,
		@RequestParam(name="settlementId", required = false) String settlementId,
		@RequestParam(name="settlementResult", required = false) String settlementResult,
		@RequestParam(name="betTimestampRangeStart", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date betTimestampRangeStart,
		@RequestParam(name="betTimestampRangeEnd", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date betTimestampRangeEnd,
		@RequestParam(name="settlementTimestampRangeStart", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date settlementTimestampRangeStart,
		@RequestParam(name="settlementTimestampRangeEnd", required = false)	@DateTimeFormat(pattern="yyyy-MM-dd") Date settlementTimestampRangeEnd,
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) {
		List<String> domains = tokenUtil.playerDomainsWithRole("PLAYER_INCENTIVE_GAME_VIEW")
		.stream()
		.map(jwtDomain -> jwtDomain.getName())
		.collect(Collectors.toList());

		Page<Bet> table = service.find(domains, betId, userGuid, eventName, sport, market, competition, isSettled,
			settlementId, settlementResult, betTimestampRangeStart, betTimestampRangeEnd, settlementTimestampRangeStart,
			settlementTimestampRangeEnd, true, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
}
