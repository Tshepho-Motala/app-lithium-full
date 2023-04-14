package lithium.service.casino.provider.incentive.api.controllers.frontend;


import lithium.service.casino.provider.incentive.services.BetHistoryService;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/frontend/bethistory")
public class FrontendBetHistoryController {
	@Autowired private BetHistoryService service;

	@GetMapping
	public Page<Bet> history(
		@RequestParam(name="betId", required = false) String betId,
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
		@RequestParam("pageSize") int pageSize,
		@RequestParam("page") int page,
		LithiumTokenUtil tokenUtil
	) {
		if (pageSize > 100) pageSize = 100;
		Pageable pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, new String[] {"id"});
		Page<Bet> betsPage = service.find(null, betId, tokenUtil.guid(), eventName, sport, market, competition,
			isSettled, settlementId, settlementResult, betTimestampRangeStart, betTimestampRangeEnd,
			settlementTimestampRangeStart, settlementTimestampRangeEnd, false, null, pageRequest);
		return betsPage;
	}
}
