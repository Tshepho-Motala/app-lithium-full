package lithium.service.casino.provider.slotapi.api.controllers.backoffice;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.casino.provider.slotapi.services.BetHistoryService;
import lithium.service.casino.provider.slotapi.services.BetResultKindService;
import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/backoffice/history")
public class HistoryController {
  @Autowired private BetHistoryService betHistoryService;
  @Autowired private BetResultKindService betResultKindService;

  @PostMapping(value = "/table")
  public DataTableResponse<Bet> table(
      @RequestParam(name = "userGuid", required = false) String userGuid,
      @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeStart,
      @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeEnd,
      @RequestParam(name = "statuses", required = false) String statuses,
      @RequestParam(name = "games", required = false) String games,
      @RequestParam(name = "betRoundGuid", required = false) String betRoundGuid,
      DataTableRequest request,
      LithiumTokenUtil tokenUtil) throws Status425DateParseException {
    String domainName = userGuid.split("/")[0];
    Page<Bet> table = betHistoryService.findBetHistory(userGuid, dateRangeStart, dateRangeEnd,
      statuses, games, betRoundGuid, true, request.getSearchValue(), request.getPageRequest(), tokenUtil);
    return new DataTableResponse<>(request, table);
  }

  @GetMapping(value = "/status")
  public Response<Iterable<BetResultKind>> all() throws Exception {
    return Response.<Iterable<BetResultKind>>builder().data(betResultKindService.findAll()).build();
  }
}
