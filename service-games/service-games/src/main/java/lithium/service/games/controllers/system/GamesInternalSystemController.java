package lithium.service.games.controllers.system;

import lithium.service.Response;
import lithium.service.games.client.GamesInternalSystemClient;
import lithium.service.games.client.objects.Domain;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.MultiGameUnlockRequest;
import lithium.service.games.client.objects.SimpleGameUserStatus;
import lithium.service.games.services.GameService;
import lithium.service.games.services.GameUserStatusService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/system/games-api-internal/")
public class GamesInternalSystemController implements GamesInternalSystemClient {

    @Autowired
    private GameService gameService;
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GameUserStatusService gameUserStatusService;

    @Override
    @RequestMapping(value = "/find-or-create-game", method = RequestMethod.POST)
    public Response<Game> findOrCreateGame(@RequestParam("domainName") String domainName,
                                           @RequestParam("providerGuid") String providerGuid,
                                           @RequestParam("gameName") String gameName,
                                           @RequestParam(value = "commercialName") String commercialName,
                                           @RequestParam("providerGameId") String providerGameId,
                                           @RequestParam("description") String description,
                                           @RequestParam("supplierGameGuid")String supplierGameGuid,
                                           @RequestParam(value ="rtp", required = false) BigDecimal rtp,
                                           @RequestParam(value ="introductionDate", required = false) Date introductionDate,
                                           @RequestParam(value ="activeDate", required = false) Date activeDate,
                                           @RequestParam(value ="inactiveDate", required = false) Date inactiveDate,
                                           @RequestParam(value = "freeSpinEnabled", required = false, defaultValue = "false") Boolean freeSpinEnabled,
                                           @RequestParam(value = "casinoChipEnabled", required = false, defaultValue = "false") Boolean casinoChipEnabled,
                                           @RequestParam(value = "instantRewardEnabled", required = false, defaultValue = "false") Boolean instantRewardEnabled,
                                           @RequestParam(value = "instantRewardFreespinEnabled", required = false, defaultValue = "false") Boolean instantRewardFreespinEnabled,
                                           @RequestParam(value = "freeSpinValueRequired", required = false, defaultValue = "false") Boolean freeSpinValueRequired,
                                           @RequestParam(value = "freeSpinPlayThroughEnabled", required = false, defaultValue = "false") Boolean freeSpinPlayThroughEnabled,
                                           @RequestParam(value = "progressiveJackpot", required = false, defaultValue = "false") Boolean progressiveJackpot,
                                           @RequestParam(value = "networkedJackpotPool", required = false, defaultValue = "false") Boolean networkedJackpotPool,
                                           @RequestParam(value = "localJackpotPool", required = false, defaultValue = "false") Boolean localJackpotPool,
                                           @RequestParam(value = "freeGame", required = false, defaultValue = "false") Boolean freeGame,
                                           @RequestParam(value = "isEnabled", required = false, defaultValue = "false") Boolean isEnabled,
                                           @RequestParam(value = "liveCasino", required = false, defaultValue = "false") Boolean liveCasino

    ) throws Exception {
        lithium.service.games.data.entities.Game gameEntity = this.gameService.findOrCreateGame(
                domainName, providerGuid, gameName, commercialName, providerGameId, description, supplierGameGuid, rtp,
                introductionDate, activeDate, inactiveDate, freeSpinEnabled, casinoChipEnabled,instantRewardEnabled,
                instantRewardFreespinEnabled, freeSpinValueRequired, freeSpinPlayThroughEnabled, progressiveJackpot,
                networkedJackpotPool, localJackpotPool, null, null, null, freeGame, isEnabled, liveCasino, null, null);

        return Response.<Game>builder()
                .data(Game.builder()
                      .id(gameEntity.getId())
                      .description(gameEntity.getDescription())
                      .rtp(gameEntity.getRtp()).introductionDate(gameEntity.getIntroductionDate())
                      .domain(Domain.builder()
                                .id(gameEntity.getDomain().getId())
                                .version(gameEntity.getDomain().getVersion())
                                .name(gameEntity.getDomain().getName())
                                .build())
                      .enabled(gameEntity.isEnabled())
                      .guid(gameEntity.getGuid())
                      .providerGameId(gameEntity.getProviderGameId())
                      .providerGuid(gameEntity.getProviderGuid())
                      .build())
                .status(Response.Status.OK)
                .build();
    }

    @Override
   // @RequestMapping(value = "/{domainName}/games/unlock-games-for-user", method = RequestMethod.POST)
    public List<SimpleGameUserStatus> unlockGamesForUser(@PathVariable("domainName") String domainName, @RequestBody MultiGameUnlockRequest request) {
        return gameUserStatusService.unlockMultipleGamesForUserOnDomain(request.userGuid(), request.gameGuids(), domainName);
    }

    @Override
    //@RequestMapping("/{domainName}/games/{provider}")
    public List<Game> getGamesForDomainAndProvider(@PathVariable("domainName") String domainName, @PathVariable("provider") String provider) {
        return mapper.map(gameService.getGamesForDomainAndProvider(domainName, provider), new TypeToken<List<Game>>() {}.getType() );
    }

    @RequestMapping(value = "/get-by-guids", method = RequestMethod.GET)
    public Response<List<Game>> getDomainGamesByGuids(@RequestParam("domainName") String domainName, @RequestParam("guids") Set<String> guids) {
        List<lithium.service.games.data.entities.Game> dbGameList = gameService.getByGuidsAndDomain(guids, domainName);
        List<Game> gamesResultsList = mapper.map(dbGameList, new TypeToken<List<Game>>(){}.getType());
        return Response.<List<Game>>builder().status(OK).data(gamesResultsList).build();
    }

}
