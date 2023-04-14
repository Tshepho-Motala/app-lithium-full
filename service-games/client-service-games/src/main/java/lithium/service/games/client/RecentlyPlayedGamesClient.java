package lithium.service.games.client;

import lithium.service.games.client.objects.RecentlyPlayedGameBasic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-games")
public interface RecentlyPlayedGamesClient {
	@RequestMapping("/system/recently-played-games")
	public List<RecentlyPlayedGameBasic> recentlyPlayedGames(@RequestParam("userGuid") String userGuid,
															 @RequestParam("liveCasino") Boolean liveCasino,
															 @RequestParam("channel") String channel);
}
