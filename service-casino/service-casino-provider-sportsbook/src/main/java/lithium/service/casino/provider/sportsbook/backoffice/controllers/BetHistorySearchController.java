package lithium.service.casino.provider.sportsbook.backoffice.controllers;

import java.util.TreeMap;
import lithium.exceptions.Status400BadRequestException;
import lithium.modules.ModuleInfo;
import lithium.rest.EnableRestTemplate;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.data.SportsBetPlayerBonus;
import lithium.service.casino.provider.sportsbook.builders.BetHistorySearchBuilder;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.data.MultipleSelection;
import lithium.service.casino.provider.sportsbook.request.BetHistorySearchRequest;
import lithium.service.casino.provider.sportsbook.request.BetSearchRequest;
import lithium.service.casino.provider.sportsbook.request.EventSearchRequest;
import lithium.service.casino.provider.sportsbook.request.LeagueMarketSportSearchRequest;
import lithium.service.casino.provider.sportsbook.response.BetHistorySearchResponse;
import lithium.service.casino.provider.sportsbook.response.EventLeagueMarketSportSearchResponse;
import lithium.service.casino.provider.sportsbook.services.FreeBetServices;
import lithium.service.casino.provider.sportsbook.storage.objects.Bet;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping(path = "/backoffice/bet/{domainName}")
@EnableRestTemplate
public class BetHistorySearchController {

    @Autowired
    ProviderConfigService providerConfigService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    @Setter
    ModuleInfo moduleInfo;

    @Autowired
    FreeBetServices freeBetServices;

    /**
     * @return
     */
    @PostMapping("/search")
    @ResponseBody
    public BetHistorySearchResponse search(@PathVariable("domainName") String domainName, @RequestBody BetHistorySearchRequest request) throws Exception {
        try {
            //Get the provider config
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
            //Generate the request and fill the response
            HttpEntity<BetHistorySearchResponse> response;
            if (request.getBetId() != null && !request.getBetId().isEmpty()) {
                BetSearchRequest betSearchRequest = BetHistorySearchBuilder.buildBetSearch(config, request);
                response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search/bets",
                        betSearchRequest, BetHistorySearchResponse.class);
            } else {
                request = BetHistorySearchBuilder.buildBetHistorySearch(config.getBetSearchKey(), config.getPlayerOffset(), config.getBetSearchBrand(), request);
                response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search",
                        request, BetHistorySearchResponse.class);
            }
            //Return the response
            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when searching for bets using betType = " + request.getBetType() + " (" + exception.getMessage() + ")");
            return new BetHistorySearchResponse();
        }
    }

    /**
     * @return
     */
    @RequestMapping("/search/events")
    @ResponseBody
    public EventLeagueMarketSportSearchResponse searchEvents(@PathVariable("domainName") String domainName, @RequestBody EventSearchRequest request) {
        try {
            //Get the provider config
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            //Generate the request
            request = BetHistorySearchBuilder.buildEventSearch(config, request);

            //Get the response from the external api
            HttpEntity<EventLeagueMarketSportSearchResponse> response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search/events",
                    request, EventLeagueMarketSportSearchResponse.class);

            //Return the response
            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when searching for bets using leagues = " + request.getLeagues() + " (" + exception.getMessage() + ")");

            return new EventLeagueMarketSportSearchResponse();
        }
    }

    /**
     * @return
     */
    @RequestMapping("/search/leagues")
    @ResponseBody
    public EventLeagueMarketSportSearchResponse searchLeagues(@PathVariable("domainName") String domainName, @RequestBody LeagueMarketSportSearchRequest request) {
        try {
            //Get the provider config
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            //Generate the request
            request = BetHistorySearchBuilder.buildLeagueMarketSportSearch(config, request);

            //Get the response from the external api
            HttpEntity<EventLeagueMarketSportSearchResponse> response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search/leagues",
                    request, EventLeagueMarketSportSearchResponse.class);

            //Return the response
            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when searching for bets using sports = " + request.getSports() + " (" + exception.getMessage() + ")");

            return new EventLeagueMarketSportSearchResponse();
        }
    }

    /**
     * @return
     */
    @RequestMapping("/search/markets")
    @ResponseBody
    public EventLeagueMarketSportSearchResponse searchMarkets(@PathVariable("domainName") String domainName, @RequestBody LeagueMarketSportSearchRequest request) {
        try {
            //Get the provider config
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            //Generate the request
            request = BetHistorySearchBuilder.buildLeagueMarketSportSearch(config, request);

            //Get the response from the external api
            HttpEntity<EventLeagueMarketSportSearchResponse> response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search/markets",
                    request, EventLeagueMarketSportSearchResponse.class);

            //Return the response
            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when searching for bets using sports = " + request.getSports() + " (" + exception.getMessage() + ")");

            return new EventLeagueMarketSportSearchResponse();
        }
    }

    @GetMapping("/search/sports/list")
    public EventLeagueMarketSportSearchResponse getSportsList(@PathVariable("domainName") String domainName) {
        try {
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            HttpEntity<EventLeagueMarketSportSearchResponse> response =
                    restTemplate.postForEntity(config.getBetSearchUrl() + "/search/sports/list",
                            BetHistorySearchBuilder.buildSportList(config),
                            EventLeagueMarketSportSearchResponse.class);

            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when listing sports for bets = " + domainName + " (" + exception.getMessage() + ")");

            return new EventLeagueMarketSportSearchResponse();
        }

    }

    /**
     * @return
     */
    @RequestMapping("/search/sports")
    @ResponseBody
    public EventLeagueMarketSportSearchResponse searchSports(@PathVariable("domainName") String domainName, @RequestBody LeagueMarketSportSearchRequest request) {
        try {
            //Get the provider config
            ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            //Generate the request
            request = BetHistorySearchBuilder.buildLeagueMarketSportSearch(config, request);

            //Get the response from the external api
            HttpEntity<EventLeagueMarketSportSearchResponse> response = restTemplate.postForEntity(config.getBetSearchUrl() + "/search/sports",
                    request, EventLeagueMarketSportSearchResponse.class);

            //Return the response
            return response.getBody();
        } catch (Exception exception) {
            log.error("Exception when searching for bets using sports = " + request.getSports() + " (" + exception.getMessage() + ")");

            return new EventLeagueMarketSportSearchResponse();
        }
    }

    @PostMapping("/search/table")
    public DataTableResponse<Bet> searchTable(
            @PathVariable String domainName,
            @RequestParam(name = "betId", required = false) String betId,
            @RequestParam(name = "betAmountTypeIn", required = false) List<MultipleSelection> betAmountTypeIn,
            @RequestParam(name = "betTypeIn", required = false) List<MultipleSelection> betTypeIn,
            @RequestParam(name = "customerId", required = false) String customerId,
            @RequestParam(name = "betAmountType", required = false) String[] betAmountType,
            @RequestParam(name = "betType[]", required = false) String betType,
            @RequestParam(name = "status[]", required = false) String status,
            @RequestParam(name = "dateType", required = false) String dateType,
            @RequestParam(name = "matchType", required = false) String matchType,
            @RequestParam(name = "eventsByLeague[]", required = false) String eventsByLeague,
            @RequestParam(name = "sha256", required = false) String sha256,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "start", required = false) Integer start,
            @RequestParam(name = "length", required = false) Integer length,
            @RequestParam(name = "to", required = false) String to,
            @RequestParam(name = "timestamp", required = false) Long timestamp,
            @RequestParam(name = "order[0][column]", required = false) String orderColumn,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "sport[]", required = false) String sports,
            @RequestParam(name = "leaguesBySport[]", required = false) String leaguesBySports,
            @RequestParam(name = "marketTypesBySport[]", required = false) String marketTypesBySports
            ) throws Exception {
        DataTableRequest request = new DataTableRequest();
        PageRequest pageRequest = PageRequest.of(start, length);
        request.setPageRequest(pageRequest);
        BetHistorySearchResponse response = search(domainName, BetHistorySearchRequest.builder()
                .betId(betId).betAmountTypeIn(betAmountTypeIn)
                .customerId(customerId).betAmountType(betAmountType)
                .betType(betType != null ? new ArrayList<>(Arrays.asList(betType.split(","))) : null)
                .status(status != null  ? new ArrayList<>(Arrays.asList(status.split(","))) : null)
                .sports(sports != null  ? new ArrayList<>(Arrays.asList(sports.split(","))) : null)
                .leaguesBySport(leaguesBySports != null  ? new ArrayList<>(Arrays.asList(leaguesBySports.split(","))) : null)
                .marketTypesBySport(marketTypesBySports != null  ? new ArrayList<>(Arrays.asList(marketTypesBySports.split(","))) : null)
                .eventsByLeague(eventsByLeague != null  ? new ArrayList<>(Arrays.asList(eventsByLeague.split(","))) : null)
                .dateType(dateType)
                .matchType(matchType).sha256(sha256).from(from)
                .page(start/length).size(length).to(to).timestamp(timestamp)
                .sort(orderColumn != null  ? getColumnName(orderColumn) : null).order(orderDirection)
                .build());

        Page<Bet> bets = new SimplePageImpl<>(response.getBets() != null ? response.getBets() : new ArrayList<>(),
                pageRequest.getPageNumber(), pageRequest.getPageSize() ,
                response.getBets() != null ? (pageRequest.getPageNumber() == 0 ? response.getBets().size() + 1 : response.getBets().size()) : 0);

        return new DataTableResponse<>(request, bets);
    }

    @GetMapping("/freebets/history")
    public DataTableResponse<SportsBetPlayerBonus> getSportsFreeBets(@RequestParam("playerId") String playerId, @RequestParam("dateRangeFrom") @DateTimeFormat(pattern = "yyyy.MM.dd") String dateRangeFrom, @RequestParam("dateRangeTo")
    @DateTimeFormat(pattern = "yyyy.MM.dd") String dateRangeTo, @RequestParam(value = "status", required = false) String status, @PathVariable("domainName") String domainName, DataTableRequest dataTableRequest) throws Status400BadRequestException, Status512ProviderNotConfiguredException {
        dataTableRequest.setPageRequest(PageRequest.of(dataTableRequest.getPageRequest().getPageNumber(),
                dataTableRequest.getPageRequest().getPageSize() > 100 ? 100 : dataTableRequest.getPageRequest().getPageSize(),
                Sort.by(Sort.Direction.DESC, "dateRangeFrom")));

        Page<SportsBetPlayerBonus> page = null;
        TreeMap sportsBetBonusesMap = freeBetServices.getSportsBetBonuses(playerId, dateRangeFrom, dateRangeTo, status, dataTableRequest, domainName);

        Integer totalElements = (Integer)sportsBetBonusesMap.firstKey();
        List<SportsBetPlayerBonus> sportsBetBonuses = (List<SportsBetPlayerBonus>)sportsBetBonusesMap.get(totalElements);

        if(sportsBetBonuses != null && sportsBetBonuses.size() > 0) {
            page = new SimplePageImpl<>(sportsBetBonuses, dataTableRequest.getPageRequest().getPageNumber(), dataTableRequest.getPageRequest().getPageSize(), totalElements);
        } else {
            page = new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);
        }
        return new DataTableResponse<>(dataTableRequest, page);
    }

    private String getColumnName(String orderColumn) {
        switch (orderColumn){
            case "0":
                return "betId";
            case "1":
                return "betName";
            case "3":
                return "betDate";
            case "7":
                return "betStatus";
            case "9":
                return "betSettledDate";
            default:
                return null;
        }
    }
}
