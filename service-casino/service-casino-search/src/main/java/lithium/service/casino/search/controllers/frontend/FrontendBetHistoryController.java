package lithium.service.casino.search.controllers.frontend;

import java.util.Date;
import java.util.Locale;
import lithium.exceptions.Status425DateParseException;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.search.objects.BetSummaryFE;
import lithium.service.casino.search.services.casino.BetHistoryService;
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

@RestController
@RequestMapping("/frontend/bethistory")
public class FrontendBetHistoryController {
  @Autowired @Qualifier("casino.BetHistoryService")
  private BetHistoryService betHistoryService;

  @Autowired
  private MessageSource messageSource;

  private static final String BET_RESULT_KIND_FREE_LOSS = "FREE_LOSS";
  private static final String BET_RESULT_KIND_LOSS = "LOSS";

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
    Pageable pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, new String[]{"createdDate"});
    Page<BetRound> result = betHistoryService.findBetHistory(tokenUtil.guid(),
        dateRangeStart, dateRangeEnd, null, null, null, null, false, null, pageRequest, null);
    Page<BetSummaryFE> resultFE = result.map(betRound -> toBetSummaryFE(betRound, locale));
    return resultFE;
  }

  private BetSummaryFE toBetSummaryFE(BetRound betRound, Locale locale) {
    BetResult betResult = betRound.getLastBetResult();
    boolean isLoss = ((betResult != null) &&
        (betResult.getBetResultKind().getCode().contentEquals(BET_RESULT_KIND_FREE_LOSS) ||
            betResult.getBetResultKind().getCode().contentEquals(BET_RESULT_KIND_LOSS)));
    String transactionTypeDisplay = "OPEN";
    if (betResult != null) {
      transactionTypeDisplay = messageSource.getMessage(
          "SERVICE_CASINO.BETHISTORY.TRAN_TYPE." + betResult.getBetResultKind().getCode(), null, locale);
    } else if (betRound.isComplete()) {
      transactionTypeDisplay = "CLOSED";
    }
    return BetSummaryFE.builder()
        .id(betRound.getId())
        .betRoundGuid(betRound.getGuid())
        .date(new Date(betRound.getCreatedDate()))
        .stake(betRound.getBetAmount())
        .won(betRound.getRoundReturnsTotal())
        .loss((isLoss) ? betRound.getBetAmount() : 0)
        .transactionType("CASINO_BET")
        .amountCents(CurrencyAmount.fromAmount(betRound.getBetAmount()).toCents())
        .transactionTypeDisplay(transactionTypeDisplay)
        .provider(betRound.getProvider().getGuid())
//        .providerTranId(betWithGame.getBet().getBetTransactionId())
        .gameName(betRound.getGame().getName())
        .gameProvider(betRound.getGame().getProviderGuid())
        .gameCategory("")
        .build();
  }
}
