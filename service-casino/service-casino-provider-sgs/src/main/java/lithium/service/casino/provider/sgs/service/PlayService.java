package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.provider.sgs.config.APIAuthentication;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.PlayResult;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlayService {
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected SGSService sgsService;
	
	public Response<? extends Result<? extends Extinfo>> play(Request request, User user, APIAuthentication apiAuthentication) {
		
		BetResponse betResponse = null;
		RollbackTranResponse rollbackResponse = null;
		try {
			String playType = request.getMethodCall().getCall().getPlayType();
			Long amount = request.getMethodCall().getCall().getAmount();

			if (playType.contentEquals("refund")) {
				RollbackTranRequest rollbackRequest = new RollbackTranRequest();
				rollbackRequest.setDomainName(user.getDomain().getName());
				rollbackRequest.setProviderGuid(user.getDomain().getName()+"/"+apiAuthentication.getProviderUrl());
				rollbackRequest.setUserGuid(user.getDomain().getName()+"/"+ user.getUsername());
				rollbackRequest.setTransactionId(request.getMethodCall().getCall().getActionId()+"");
				rollbackResponse = sgsService.getCasinoService().rollbackTran(rollbackRequest);
			} else {
				BetRequest br = BetRequest.builder().build();
				
				if (playType.contentEquals("bet")) {
					br.setBet(amount);
				}
				
				if (playType.contentEquals("win")) {
					br.setWin(amount);
				}
				br.setTransactionId(request.getMethodCall().getCall().getActionId());
				br.setDomainName(user.getDomain().getName());
				br.setProviderGuid(user.getDomain().getName()+"/"+apiAuthentication.getProviderUrl());
				br.setUserGuid(user.getDomain().getName()+"/"+ user.getUsername());
				br.setGameGuid(apiAuthentication.getProviderUrl()+"/"+request.getMethodCall().getCall().getGameReference());
				br.setGameSessionId(request.getMethodCall().getCall().getGameId());
				
				log.debug("Request to casino for play: " + br.toString());
				betResponse = sgsService.getCasinoService().handleBetRequest(br);
				log.debug("Response from casino for play: " + betResponse.toString());
			}
			

		} catch (Exception e) {
			log.error("Unable to perform bet handling. Request: "+ request +" | User: " + user + " | Response: " + betResponse + " refund: " + rollbackResponse, e);
			return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
		}
		
		PlayResult<Extinfo> playResult = null;
		
		if (betResponse != null) {
			if (Long.parseLong(betResponse.getExtSystemTransactionId()) <= 0L) {
				log.error("Unable to process bet request. Request: "+ request + " | User:" + user + " | Response: " + betResponse + " refund: " + rollbackResponse);
				return sgsService.createErrorResponse(request, ErrorCodes.GAMEPLAY_INSUFFICIENT_FUNDS);
			} else if (betResponse.getCode() != null && betResponse.getCode().contentEquals("DUPLICATE")) {
				log.warn("Unable to bet request duplicate. Request: "+ request + " | User:" + user + " | Response: " + betResponse + " refund: " + rollbackResponse);
				return sgsService.createErrorResponse(request, ErrorCodes.GAMEPLAY_ALREADY_PROCESSED);
			}
			playResult = new PlayResult<Extinfo>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken(), betResponse.getBalanceCents()+"", "", betResponse.getExtSystemTransactionId()+"");
			playResult.setExtinfo(new Extinfo());
		} else {
			if (Long.parseLong(rollbackResponse.getTranId()) <= 0L ) {
				log.error("Unable to process refund request. Request: "+ request + " | User:" + user + " | Response: " + betResponse + " refund: " + rollbackResponse);
				return sgsService.createErrorResponse(request, ErrorCodes.GAMEPLAY_PROCESSED_DIFFERENT_DETAILS);
			} else if (rollbackResponse.getCode()!= null && rollbackResponse.getCode().contentEquals("DUPLICATE")) { 
				log.warn("Unable to process refund request duplicate. Request: "+ request + " | User:" + user + " | Response: " + betResponse + " refund: " + rollbackResponse);
				return sgsService.createErrorResponse(request, ErrorCodes.GAMEPLAY_PROCESSED_DIFFERENT_DETAILS);
			}
			log.debug("Rollback response: " + rollbackResponse + " more: " + rollbackResponse.getCode());
			playResult = new PlayResult<Extinfo>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken(), rollbackResponse.getBalanceCents()+"", "", rollbackResponse.getTranId()+"");
			playResult.setExtinfo(new Extinfo());
		}
		
		MethodResponse<PlayResult<Extinfo>> methodResponse = new MethodResponse<>(MessageTypes.PLAY.getTypeName(), new Date(), playResult);
		Response<PlayResult<Extinfo>> response = new Response<>(methodResponse);
		
		return response;
	}
}
