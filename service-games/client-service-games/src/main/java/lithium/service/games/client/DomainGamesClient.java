package lithium.service.games.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="service-games")
public interface DomainGamesClient {
	
//	@RequestMapping("/games/{domainName}/listDomainGames")
//	public Iterable<Game> listDomainGames(@PathVariable("domainName") String domainName)  throws Exception;
//	
//	@RequestMapping("/games/{domainName}/editDomainGame")
//	public Response<Boolean> editDomainGames(@PathVariable("domainName") String domainName, 
//			@RequestParam("gameGuid") String gameGuid, @RequestParam("selected") Boolean selected) throws Exception;
//
//	@RequestMapping("/games/{domainName}/domainGameData")
//	public Iterable<DomainGameData> domainGameData(@PathVariable("domainName") String domainName)  throws Exception;
	
//	@RequestMapping("/games/{domainName}/startGame")
//	public void startGame(@PathVariable("domainName") String domainName, 
//			@RequestParam("token") String token, @RequestParam("gameguid") String gameguid, 
//			@RequestParam("lang") String lang, @RequestParam("currency") String currency) throws Exception;
//	
//	@RequestMapping("/games/{domainName}/demoGame")
//	public RedirectView demoGame(@PathVariable("domainName") String domainName, 
//			@RequestParam("gameguid") String gameguid, 
//			@RequestParam("lang") String lang, @RequestParam("currency") String currency) throws Exception;
}