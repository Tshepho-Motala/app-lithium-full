package lithium.service.casino.cms.api.controllers.frontend;

import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status404LobbyConfigNotFoundException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.cms.api.schema.lobby.LobbyRequest;
import lithium.service.casino.cms.api.schema.lobby.LobbyResponse;
import lithium.service.casino.cms.services.LobbyService;
import lithium.service.games.client.objects.GameUserStatus;
import lithium.service.games.client.service.GameUserStatusClientService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/frontend/lobby")
@Slf4j
public class FrontendLobbyController {
	@Autowired private LobbyService service;

	@Autowired private UserApiInternalClientService userApiInternalClientService;

	@Autowired private GameUserStatusClientService gameUserStatusClientService;

	@Autowired private LimitInternalSystemService limitInternalSystemService;

	@PostMapping("/load")
	public @ResponseBody LobbyResponse load(@RequestBody LobbyRequest request, LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException, Status483PlayerCasinoNotAllowedException, Status404LobbyConfigNotFoundException {
			String userGuid = null;
		try {
			if(tokenUtil != null) {
				userGuid = tokenUtil.guid();
				limitInternalSystemService.checkPlayerCasinoAllowed(userGuid);
			}
			LobbyResponse config = service.getLobbyConfig(request.getBrand(), request.getChannel(),
					request.getPrimaryNavCode(), request.getSecondaryNavCode(),
					userGuid);
			if (userGuid != null) {
				List<GameUserStatus> gameUserStatusList = gameUserStatusClientService.findUnlockedForUser(userGuid);
				config.setFreeGamesLocked(gameUserStatusList.isEmpty());
				config.setTestAccount(userApiInternalClientService.isTestAccount(userGuid));
			}
			return config;
		} catch (Status404LobbyConfigNotFoundException e) {
			String errorMsg = String.format("Lobby configuration not found: userGuid: %s brand: %s channel: %s " +
							"primaryNavCode: %s secondaryNavCode: %s", userGuid, request.getBrand(), request.getChannel(),
					request.getPrimaryNavCode(), request.getSecondaryNavCode());
			log.error(errorMsg);
			throw e;
		} catch (Status483PlayerCasinoNotAllowedException e) {
			log.debug("Player Casino Not Allowed: " + e.getMessage(), e);
			throw e;
		} catch (UserClientServiceFactoryException | Exception e) {
			String errorMsg = "Failed to load lobby configuration.";
			log.error(errorMsg + " " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(errorMsg);
		}
	}
}
