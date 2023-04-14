package lithium.service.games.controllers.system;

import lithium.service.games.client.objects.TaggedGameBasic;
import lithium.service.games.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/tagged-games")
public class SystemTaggedGamesController {
	@Autowired private GameService gameService;

	@GetMapping
	private List<TaggedGameBasic> getTaggedGames(@RequestParam("domainName") String domainName,
	        @RequestParam("tags") List<String> tags,
			@RequestParam(name = "liveCasino", required = false) Boolean liveCasino,
			@RequestParam(name = "channel", required = false) String channel,
			@RequestParam(name = "enabled", required = true) Boolean enabled
	) {
		return gameService.getTaggedGames(domainName, "TAG", tags, liveCasino, channel, enabled);
	}
}
