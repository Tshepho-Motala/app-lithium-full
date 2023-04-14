package lithium.service.casino.search.controllers.backoffice;

import java.util.Date;
import java.util.List;
import lithium.exceptions.Status425DateParseException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.data.entities.BetResultKind;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.search.services.casino.BetHistoryService;
import lithium.service.casino.search.services.casino.BetResultKindService;
import lithium.service.casino.search.services.casino.ProviderService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/bethistory")
public class BetHistoryController {
  @Autowired @Qualifier("casino.BetHistoryService")
  private BetHistoryService service;

  @Autowired @Qualifier("casino.BetResultKindService")
  private BetResultKindService betResultKindService;

  @Autowired @Qualifier("casino.ProviderService")
  private ProviderService providerService;

  @PostMapping(value = "/table")
  public DataTableResponse<BetRound> table(
      @RequestParam(name = "userGuid", required = false) String userGuid,
      @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeStart,
      @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeEnd,
      @RequestParam(name = "providers", required = false) String providers,
      @RequestParam(name = "statuses", required = false) String statuses,
      @RequestParam(name = "games", required = false) String games,
      @RequestParam(name = "betRoundGuid", required = false) String betRoundGuid,
      DataTableRequest request,
      LithiumTokenUtil tokenUtil) throws Status425DateParseException, Status500InternalServerErrorException {
    String domainName = userGuid.split("/")[0];
    DomainValidationUtil.validate(domainName, tokenUtil, "PLAYER_CASINO_HISTORY_VIEW");
    Page<BetRound> table = service.findBetHistory(userGuid, dateRangeStart, dateRangeEnd, providers, statuses, games, betRoundGuid, true,
        request.getSearchValue(), request.getPageRequest(), tokenUtil);
    return new DataTableResponse<>(request, table);
  }

  @GetMapping(value = "/status")
  public Response<Iterable<BetResultKind>> statuses() throws Exception {
    return Response.<Iterable<BetResultKind>>builder().data(betResultKindService.findAll()).build();
  }

  @GetMapping(value = "/providers")
  public Response<List<Provider>> providers(@RequestParam("domainName") String domainName) {
    return Response.<List<Provider>>builder().data(providerService.findByDomainName(domainName)).build();
  }
}
