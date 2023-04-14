package lithium.service.games.client;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.games.client.exceptions.Status429UserLoggedOutException;
import lithium.service.games.client.exceptions.Status502ProviderProcessingException;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.GameUserStatus;
import lithium.service.games.client.objects.User;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name="service-games")
public interface GamesClient {
	
	@RequestMapping("/games/{gameId}/unlock/toggle")
	public Response<GameUserStatus> toggleLocked(@PathVariable("gameId") Long gameId, @RequestBody User user);

	@RequestMapping("/games/{gameGuid}/unlock")
	Response<GameUserStatus> unlock(@PathVariable("gameGuid") String gameGuid, @RequestBody User user);
	
	@RequestMapping("/games/{domainName}/listGames")
	public List<Game> listGames(@PathVariable("domainName") String domainName) throws Exception;
	
	@RequestMapping("/games/{domainName}/listFrbGames")
	public List<Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception;
	
//	@RequestMapping("/games/{domainName}/listFrbGamesForUser")
//	public List<Game> listFrbGamesForUser(@PathVariable("domainName") String domainName, @RequestParam("userGuid") String userGuid) throws Exception;
	
	@RequestMapping("/games/{domainName}/startGame")
	public Response<String> startGame(@PathVariable("domainName") String domainName, 
			@RequestParam("token") String token, @RequestParam("gameId") String gameId, 
			@RequestParam("lang") String lang, @RequestParam("currency") String currency,
			@RequestParam(value = "os", required = false) String os,
			@RequestParam(value="machineGUID", required=false) String machineGUID,
			@RequestParam(value="tutorial", required=false) Boolean tutorial,
			@RequestParam(value = "platform", required = false) String platform
			) throws Status429UserLoggedOutException,
			Status483PlayerCasinoNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status502ProviderProcessingException,
			Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException;
	
	@RequestMapping("/games/{domainName}/demoGame")
	public Response<String> demoGame(@PathVariable("domainName") String domainName, 
			@RequestParam("gameId") String gameId, @RequestParam("lang") String lang,
			@RequestParam("os") String os
			) throws Status429UserLoggedOutException,
			Status483PlayerCasinoNotAllowedException,
			Status500LimitInternalSystemClientException,
			Status502ProviderProcessingException,
			Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException;
	
	@RequestMapping("/games/add")
	public Response<Game> addGame(@RequestParam("providerGuid")String providerGuid, 
			@RequestParam("providerGameId") String providerGameId, @RequestParam("gameName") String gameName) throws Exception;
	
	@RequestMapping("/games/{gameId}/findById")
	public Response<Game> findById(@PathVariable("gameId") Long gameId) throws Exception;
	
	@RequestMapping("/games/{gameId}/editGraphic/{graphicFunction}")
	public Response<Game> editGraphic(@PathVariable("gameId") Long gameId, @PathVariable("graphicFunction") String graphicFunction, @RequestParam("file") MultipartFile file) throws Exception;
	
	@RequestMapping("/games/edit")
	public Response<Game> edit(@RequestBody Game game) throws Exception;
	
	//Service-casino requires this
	@RequestMapping("/games/{domainName}/listDomainGames")
	public Response<Iterable<Game>> listDomainGames(@PathVariable("domainName") String domainName) throws Exception;

	@RequestMapping("/games/{domainName}/listDomainGamesPerChannel")
	public List<Game> listDomainGamesPerChannel(
			@PathVariable("domainName") String domainName,
			@RequestParam("channel") String channel,
			@RequestParam(name = "enabled", required = true) Boolean enabled,
			@RequestParam(name = "visible", required = true) Boolean visible
	) throws Exception;
	
	@RequestMapping("/games/{domainName}/find/guid/{gameGuid}")
	public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainName(
		@PathVariable("domainName") String domainName,
		@PathVariable("gameGuid") String gameGuid
	) throws Exception;

	@RequestMapping("/games/{domainName}/find/guid/{gameGuid}/no-labels")
	public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainNameNoLabels(
			@PathVariable("domainName") String domainName,
			@PathVariable("gameGuid") String gameGuid
	) throws Exception;
	
	@RequestMapping("/games/{domainName}/listDomainGamesDT")
	public DataTableResponse<Game> listDomainGames(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="enabled", defaultValue="true") Boolean enabled,
		@RequestParam("draw") String drawEcho,
		@RequestParam("start") Long start,
		@RequestParam("length") Long length
	);
	
	@RequestMapping("/games/{domainName}/listDomainGamesReport")
	public DataTableResponse<Game> listDomainGamesReport(
		@PathVariable("domainName") String domainName,
		@RequestParam("draw") String drawEcho,
		@RequestParam("start") Long start,
		@RequestParam("length") Long length
	);
	
	@RequestMapping("/games/{domainName}/isGameLockedForPlayer")
	public Response<Boolean> isGameLockedForPlayer(@PathVariable("domainName") String domainName, @RequestParam("gameGuid") String gameGuid, @RequestParam("playerGuid") String playerGuid);

}
