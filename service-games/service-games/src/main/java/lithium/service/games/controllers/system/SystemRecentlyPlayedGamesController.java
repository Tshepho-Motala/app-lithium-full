package lithium.service.games.controllers.system;

import lithium.service.games.client.objects.RecentlyPlayedGameBasic;
import lithium.service.games.services.RecentlyPlayedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/recently-played-games")
@Slf4j
public class SystemRecentlyPlayedGamesController {
	@Autowired private RecentlyPlayedService service;

	@GetMapping
	public List<RecentlyPlayedGameBasic> recentlyPlayedGames(@RequestParam("userGuid") String userGuid,
															 @RequestParam(name = "liveCasino", required = false) Boolean liveCasino,
															 @RequestParam(name = "channel", required = false) String channel) {
		return service.getRecentlyPlayedGames(userGuid, liveCasino, channel);
	}
}
