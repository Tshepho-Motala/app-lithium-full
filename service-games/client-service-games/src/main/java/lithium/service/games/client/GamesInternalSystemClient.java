package lithium.service.games.client;

import lithium.service.Response;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.MultiGameUnlockRequest;
import lithium.service.games.client.objects.SimpleGameUserStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@FeignClient(name = "service-games", path = "/system/games-api-internal/")
public interface GamesInternalSystemClient {
    @RequestMapping(value = "/find-or-create-game", method = RequestMethod.POST)
    Response<Game> findOrCreateGame(@RequestParam("domainName") String domainName,
                                    @RequestParam("providerGuid") String providerGuid,
                                    @RequestParam("gameName") String gameName,
                                    @RequestParam(value = "commercialName") String commercialName,
                                    @RequestParam("providerGameId") String providerGameId,
                                    @RequestParam("description") String description,
                                    @RequestParam("supplierGameGuid") String supplierGameGuid,
                                    @RequestParam("rtp") BigDecimal rtp,
                                    @RequestParam(value = "introductionDate", required = false) Date introductionDate,
                                    @RequestParam(value = "activeDate", required = false) java.util.Date activeDate,
                                    @RequestParam(value = "inactiveDate", required = false) java.util.Date inactiveDate,
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
                                    @RequestParam(value = "liveCasino", required = false, defaultValue = "false") Boolean liveCasino) throws Exception;

    @RequestMapping(value = "/{domainName}/games/unlock-games-for-user", method = RequestMethod.POST)
    List<SimpleGameUserStatus> unlockGamesForUser(@PathVariable("domainName") String domainName, @RequestBody  MultiGameUnlockRequest request);

    @RequestMapping("/{domainName}/games/{provider}") //TODO: Parent Path added from develop, so this needs updating. @Riv
    List<Game> getGamesForDomainAndProvider(@PathVariable("domainName") String domainName, @PathVariable("provider") String provider);


    @RequestMapping (value = "/get-by-guids", method = RequestMethod.GET)
    Response<List<Game>> getDomainGamesByGuids(@RequestParam(value = "domainName") String domainName,
                                               @RequestParam(value = "guids") Set<String> guids);
}
