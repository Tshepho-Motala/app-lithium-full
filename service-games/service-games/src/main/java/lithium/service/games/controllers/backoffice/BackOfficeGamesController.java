package lithium.service.games.controllers.backoffice;

import java.security.Principal;
import lithium.service.Response;
import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.objects.Game;
import lithium.service.games.data.entities.GameGraphic;
import lithium.service.games.services.GameService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/backoffice/games/{domainName}")
@Slf4j
public class BackOfficeGamesController {
    @Autowired GameService gameService;

    @RequestMapping("/listDomainGames")
    public Response<Iterable<Game>> listDomainGames(
            @PathVariable("domainName") String domainName,
            @RequestParam(value = "freeSpinEnabled", required = false) Boolean freeSpinEnabled,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestParam(value = "visible", required = false) Boolean visible,
            @RequestParam(value = "channel", required = false) String channel
    ) throws Exception {
        Iterable<lithium.service.games.client.objects.Game> gameIterable = gameService.getDomainGameList(domainName, freeSpinEnabled, enabled, visible, channel);

        return Response.<Iterable<lithium.service.games.client.objects.Game>>builder()
                .data(gameIterable)
                .status(Response.Status.OK)
                .build();
    }

    @GetMapping("/cdn-external-graphic/find")
    public Response<GameGraphic> findCdnExternalGameGraphic(@PathVariable("domainName") String domainName,
            @RequestParam("gameId") Long gameId, @RequestParam("liveCasino") Boolean liveCasino) {
        try {
            return Response.<GameGraphic>builder().data(gameService.findCdnExternalGraphic(domainName, gameId, liveCasino))
                    .status(OK).build();
        } catch (Exception e) {
            log.error("Unable to find cdn/external game graphic [domainName="+domainName+", gameId="+gameId+"] "
                    + e.getMessage(), e);
            return Response.<GameGraphic>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/cdn-external-graphic/save")
    public Response<GameGraphic> saveCdnExternalGameGraphic(@PathVariable("domainName") String domainName,
            @RequestParam("gameId") Long gameId, @RequestParam("url") String url, @RequestParam("liveCasino") Boolean liveCasino) {
        try {
            return Response.<GameGraphic>builder().data(gameService.saveCdnExternalGraphic(domainName, gameId, url, liveCasino))
                    .status(OK).build();
        } catch (Exception e) {
            log.error("Unable to save cdn/external game graphic [domainName="+domainName+", gameId="+gameId
                    +", url="+url+"] " + e.getMessage(), e);
            return Response.<GameGraphic>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/cdn-external-graphic/remove")
    public Response<Boolean> removeCdnExternalGameGraphic(@PathVariable("domainName") String domainName,
            @RequestParam("gameId") Long gameId, @RequestParam("liveCasino") Boolean liveCasino) {
        try {
            gameService.removeCdnExternalGraphic(domainName, gameId, liveCasino);
            return Response.<Boolean>builder().data(true).status(OK).build();
        } catch (Exception e) {
            log.error("Unable to remove cdn/external game graphic [domainName="+domainName+", gameId="+gameId+"] "
                    + e.getMessage(), e);
            return Response.<Boolean>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @RequestMapping("/table")
    public DataTableResponse<lithium.service.games.data.entities.Game> table(
            @PathVariable("domainName") String domainName,
            @RequestParam("order[0][column]") String orderColumn,
            @RequestParam("order[0][dir]") String orderDirection,
            DataTablePostRequest request,
            LithiumTokenUtil lithiumTokenUtil
    ) throws Status550ServiceDomainClientException {
        Sort sort = request.getPageRequest().getSort();
        request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
                request.getPageRequest().getPageSize() > 100 ? 100 : request.getPageRequest().getPageSize(),
                sort));

        log.debug("Game table request " + request + "");

        Page<lithium.service.games.data.entities.Game> games = gameService.buildGameTable(request,domainName);
        log.debug("Page<Game> games : " + games);
        return new DataTableResponse<>(request, games);
    }
}
