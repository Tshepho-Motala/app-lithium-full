package lithium.service.casino.provider.sgs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response.Status;
import lithium.service.casino.provider.sgs.config.APIAuthentication;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.casino.provider.sgs.service.BalanceService;
import lithium.service.casino.provider.sgs.service.EndgameService;
import lithium.service.casino.provider.sgs.service.LoginService;
import lithium.service.casino.provider.sgs.service.PlayService;
import lithium.service.casino.provider.sgs.service.RefreshTokenService;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RequestController extends BaseController {

	@Autowired private LoginService loginService;
	@Autowired private BalanceService balanceService;
	@Autowired private RefreshTokenService refreshTokenService;
	@Autowired private PlayService playService;
	@Autowired private EndgameService endgameService;

	@PostMapping(path="/endpoint", produces = "application/xml")
	Response<? extends Result<? extends Extinfo>> processRequest(@RequestBody Request request, APIAuthentication apiAuthentication) {
		try {
			log.debug("Request: " + request + " | apiAuthentication: " + apiAuthentication);
			MessageTypes requestType = MessageTypes.getMessageTypeFromString(request.getMethodCall().getName());

			if(! sgsService.isApiCredentialsCorrect(request, apiAuthentication)) {
				log.error("API credentials provided by SGS is incorrect. Request: " + request + " | apiAuthentication: " + apiAuthentication);
				return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_AUTH_CREDENTIALS_INCORRECT);
			}

			lithium.service.Response<User> userLookupResponse = sgsService.getUserApiService().getUserByApiToken(request.getMethodCall().getCall().getToken());

			if (userLookupResponse.getStatus() != Status.OK) {
				log.error("Unable to extract player info from token provided: Request: " + request + " | apiAuthentication: " + apiAuthentication);
				return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_PLAYER_TOKEN_INVALID);
			}

			switch (requestType) {
			case LOGIN: {
				return loginService.login(request, userLookupResponse.getData());
			}

			case GET_BALANCE: {
				return balanceService.getBalance(request, userLookupResponse.getData());
			}

			case PLAY: {
				return playService.play(request, userLookupResponse.getData(), apiAuthentication);
			}

			case END_GAME: {
				return endgameService.endGame(request, userLookupResponse.getData());
			}

			case REFRESH_TOKEN: {
				return refreshTokenService.refreshToken(request, userLookupResponse.getData());
			}

			case UNKNOWN: {
				log.error("Unknown message tpye. Request: " + request + " | apiAuthentication: " + apiAuthentication);
				return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
			}
			}
		} catch (Exception ex) {
			log.error("Fatal error in sgs request handling.", ex);
			return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
		}
		
		log.error("Ended up in a place where we should never be. This means we don't know the request type that was sent and the type case fallback failed. Request: " + request + " | apiAuthentication: " + apiAuthentication);
		return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
	}
}
