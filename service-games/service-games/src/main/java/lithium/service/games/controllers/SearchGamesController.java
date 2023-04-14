package lithium.service.games.controllers;

import lithium.service.games.client.objects.GameDto;
import lithium.service.games.config.ServiceGamesConfigurationProperties;
import lithium.service.games.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@EnableConfigurationProperties(ServiceGamesConfigurationProperties.class)
@Slf4j
@RestController
@RequestMapping("/frontend")
public class SearchGamesController {

	@Autowired
	private GameService gameService;

	@GetMapping("/search-games/{domainName}")
	public List<GameDto> searchGames(
			@PathVariable("domainName") String domainName,
			@RequestParam("searchValue") String searchValue,
			@RequestParam(value = "size", defaultValue = "20") int size) throws Exception {
		return gameService.searchGames(domainName.trim(), searchValue, size);
	}
}
