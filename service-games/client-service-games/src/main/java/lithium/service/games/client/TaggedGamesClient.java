package lithium.service.games.client;

import lithium.service.games.client.objects.TaggedGameBasic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-games")
public interface TaggedGamesClient {
	@RequestMapping("/system/tagged-games")
	public List<TaggedGameBasic> getTaggedGames(@RequestParam("domainName") String domainName,
												@RequestParam("tags") List<String> tags,
												@RequestParam(name = "liveCasino", required = false) Boolean liveCasino,
												@RequestParam(name = "channel", required = false) String channel,
												@RequestParam(name = "enabled", required = true) Boolean enabled
	);
}
