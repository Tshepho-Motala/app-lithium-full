package lithium.service.casino.provider.slotapi.api.controllers.frontend;

import lithium.exceptions.Status425DateParseException;
import lithium.service.casino.provider.slotapi.api.schema.history.BetSummaryFE;
import lithium.service.casino.provider.slotapi.services.BetHistoryService;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import lithium.tokens.LithiumTokenUtil;
import lithium.math.CurrencyAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
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
import java.util.Locale;

@RestController
@RequestMapping("/frontend/history")
public class FrontendHistoryController {
	@Autowired
	private BetHistoryService betHistoryService;

	@Autowired
	private MessageSource messageSource;

	private static final String BET_RESULT_KIND_WIN = "WIN";
	private static final String BET_RESULT_KIND_LOSS = "LOSS";
	private static final String BET_RESULT_KIND_VOID = "VOID";

	@GetMapping
	public Page<BetSummaryFE> history(
		@RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeStart,
		@RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeEnd,
		@RequestParam("pageSize") int pageSize,
        @RequestParam("page") int page,
        Locale locale,
        LithiumTokenUtil tokenUtil
	) throws Status425DateParseException {
		if (pageSize > 100) pageSize = 100;
		Pageable pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, new String[]{"id"});
		Page<Bet> result = betHistoryService.findBetHistory(tokenUtil.guid(),
			dateRangeStart, dateRangeEnd, null, null, null, false, null, pageRequest, null);
		Page<BetSummaryFE> resultFE = result.map(bet -> toBetSummaryFE(bet, locale));
		return resultFE;
	}

	private BetSummaryFE toBetSummaryFE(Bet bet, Locale locale) {
		BetResult betResult = bet.getBetRound().getBetResult();
		boolean isLoss = (betResult != null &&
			betResult.getBetResultKind().getCode().contentEquals(BET_RESULT_KIND_LOSS));
		String transactionTypeDisplay = "";
		if (betResult != null) {
			switch (betResult.getBetResultKind().getCode()) {
				case BET_RESULT_KIND_WIN:
					transactionTypeDisplay = messageSource.getMessage(
						"SERVICE_CASINO.BETHISTORY.TRAN_TYPE.WIN", null, locale);
					break;
				case BET_RESULT_KIND_LOSS: transactionTypeDisplay =
					transactionTypeDisplay = messageSource.getMessage(
						"SERVICE_CASINO.BETHISTORY.TRAN_TYPE.LOSS", null, locale);
					break;
				case BET_RESULT_KIND_VOID: transactionTypeDisplay =
					transactionTypeDisplay = messageSource.getMessage(
						"SERVICE_CASINO.BETHISTORY.TRAN_TYPE.VOID", null, locale);
					break;
				default:
					transactionTypeDisplay = messageSource.getMessage(
						"SERVICE_CASINO.BETHISTORY.TRAN_TYPE.OPEN", null, locale);
					break;
			}
		}
		return BetSummaryFE.builder()
			.id(bet.getId())
			.betRoundGuid(bet.getBetRound().getGuid())
			.date(bet.getTransactionTimestamp())
			.stake(bet.getAmount())
			.won((betResult != null) ? betResult.getReturns() : 0)
			.loss((isLoss) ? bet.getAmount() : 0)
			.transactionType("CASINO_BET")
			.amountCents(CurrencyAmount.fromAmount(bet.getAmount()).toCents())
			.transactionTypeDisplay(transactionTypeDisplay)
			.provider(bet.getBetRound().getUser().getDomain().getName() + "/service-casino-provider-slotapi")
			.providerTranId(bet.getBetTransactionId())
			.gameName(bet.getBetRound().getGame().getGameName())
			.build();
	}
}
