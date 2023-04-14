package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.EndgameResult;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EndgameService {
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected SGSService sgsService;
	
	public Response<? extends Result<? extends Extinfo>> endGame(Request request, User user) {
		//FIXME: We don't have an end game (round) function in the currently running system, the v2 will have that.
		//The roundId is the gameId from SGS
		BalanceResponse balanceResponse = null;
		try {
			balanceResponse = sgsService.getCasinoService().handleBalanceRequest(BalanceRequest.builder().userGuid(user.getDomain().getName()+"/"+ user.getUsername()).build());
		} catch (Exception e) {
			log.error("Unable to get player balance on endgame. User:" + user + " | Response: " + balanceResponse, e);
			return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
		}
		
		EndgameResult<Extinfo> endgameResult = new EndgameResult<>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken(), balanceResponse.getBalanceCents()+"", "");
		endgameResult.setExtinfo(new Extinfo());
		MethodResponse<EndgameResult<Extinfo>> methodResponse = new MethodResponse<>(MessageTypes.END_GAME.getTypeName(), new Date(), endgameResult);
		Response<EndgameResult<Extinfo>> response = new Response<>(methodResponse);
		
		return response;
	}
}
