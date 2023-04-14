package lithium.service.casino.provider.twowinpower.controllers;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.provider.twowinpower.config.APIAuthentication;
import lithium.service.casino.provider.twowinpower.response.Response;
import lithium.service.casino.provider.twowinpower.response.Response.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CallbackController extends BaseController {
	
	@RequestMapping(value = "/endpoint", consumes="application/x-www-form-urlencoded", produces="application/json")
	Response callback(
		WebRequest webRequest,
		APIAuthentication apiAuthentication
	) {
		log.info("Callback received for : "+webRequest.getParameter("action"));
		Response error = apiAuthentication.error(webRequest);
		if (error !=null) return error;
		
		switch (webRequest.getParameter("action")) {
			case "balance":
				return handleBalanceRequest(webRequest, apiAuthentication);
			case "bet":
			case "win":
				return handleBetWinRequest(webRequest, apiAuthentication);
			case "refund":
				return handleRefundRequest(webRequest, apiAuthentication);
			default:
				break;
		}
		return null;
		
	}
	
	private Response handleRefundRequest(WebRequest webRequest, APIAuthentication apiAuthentication) {
//		String action = webRequest.getParameter("action");
		String amount = webRequest.getParameter("amount");
//		String currency = webRequest.getParameter("currency");
//		String gameUuid = webRequest.getParameter("game_uuid");
		String playerId = twpService.playerGuid(webRequest);
		String transactionId = webRequest.getParameter("transaction_id");
//		String sessionId = webRequest.getParameter("session_id");
//		String type = webRequest.getParameter("type");
		String betTransactionId = webRequest.getParameter("bet_transaction_id");
		
//		Long amountCents = new BigDecimal(amount).movePointRight(2).longValue();
		
		RollbackTranRequest rtr = RollbackTranRequest.builder()
			.transactionId(betTransactionId)
			.userGuid(playerId)
			.build();
		rtr.setDomainName(getDomainNameFromPlayerGuid(playerId));
		rtr.setProviderGuid(getDomainNameFromPlayerGuid(playerId)+"/"+apiAuthentication.getProviderUrl());
		
		try {
			log.warn("RollbackTranRequest : "+rtr);
			RollbackTranResponse rbtr = twpService.getCasinoService().rollbackTran(rtr);
			log.warn("RollbackTranResponse : "+rbtr);
			if (rbtr.getBalanceCents() == null) {
				// new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_UNKNOWN_USERID, "Unknown user id or accounting service down"));
				return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process refund transaction. Unknown user.").build(); 
			}
			
			if (Long.parseLong(rbtr.getTranId()) <= 0) {
				// new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_UNKNOWN_TRANSACTIONID, "Unknown transaction id or accounting service down"));
				return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process refund transaction. Invalid transaction.").build(); 
			}
			log.info("RollbackTranResponse " + rbtr);
			
			Response response = Response.builder()
				.balance(
					new BigDecimal(rbtr.getBalanceCents()).movePointLeft(2).toString()
					)
				.transactionId(rbtr.getTranId())
				.build();
			log.info("Rollback Response: " + response);
			return response;
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
		}
		//new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
		return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process refund transaction.").build(); 
	}
	
	private Response handleBetWinRequest(WebRequest webRequest, APIAuthentication apiAuthentication) {
		String action = webRequest.getParameter("action");
		String amount = webRequest.getParameter("amount");
//		String currency = webRequest.getParameter("currency");
		String gameUuid = webRequest.getParameter("game_uuid");
		String playerId = twpService.playerGuid(webRequest);
		String transactionId = webRequest.getParameter("transaction_id");
		String sessionId = webRequest.getParameter("session_id");
//		String type = webRequest.getParameter("type");
		log.debug("action : "+action+" | amount : "+amount+" | gameUuid : "+gameUuid+" | playerId : "+playerId+" | transactionId : "+transactionId+" | sessionId : "+sessionId);
		
		Long amountCents = new BigDecimal(amount).movePointRight(2).longValue();
		boolean bet = true;
		
		BetRequest br = BetRequest.builder()
			.transactionId(transactionId)
			.userGuid(playerId)
			.gameGuid(apiAuthentication.getProviderUrl()+"/"+gameUuid)
			.gameSessionId(sessionId)
			.build();
		
		switch (action) {
			case "bet":
				br.setBet(amountCents);
				break;
			case "win":
				bet = false;
				log.info("Won : "+amount);
				br.setWin(amountCents);
				break;
		}
		
		br.setDomainName(getDomainNameFromPlayerGuid(playerId));
		br.setProviderGuid(getDomainNameFromPlayerGuid(playerId)+"/"+apiAuthentication.getProviderUrl());
		
		try {
			log.debug("BetRequest :: "+br);
			BetResponse betResponse = null;
			if ((!bet) && (amountCents == 0L)) {
				betResponse = twpService.getCasinoService().handleZeroWinRequest(br);
			} else {
				betResponse = twpService.getCasinoService().handleBetRequest(br);
			}
			
			if (betResponse.getBalanceCents() == null) {
				//new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_UNKNOWN_USERID, "Unknown user id or accounting service down"));
				return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process transaction. Unknown user.").build();
			}
			if (Long.parseLong(betResponse.getExtSystemTransactionId()) <= 0) {
				//new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_OPERATION_FAILED, "Problem performing transaction. Balance might be too low."));
				if (bet) {
					return Response.builder().errorCode(ErrorCode.INSUFFICIENT_FUNDS.value()).errorDescription("Insufficient balance.").build();
				} else {
					return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process refund transaction.").build();
				}
			}
			Response response = Response.builder()
				.balance(
					new BigDecimal(betResponse.getBalanceCents()).movePointLeft(2).toString()
				)
				.transactionId(betResponse.getExtSystemTransactionId())
				.build();
			log.debug("Bet Response: " + response);
			return response;
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
		}
		//new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_INTERNAL_ERROR.value(), e.toString() + e.getMessage()));
		return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Could not process transaction.").build();
	}
	
	private Response handleBalanceRequest(WebRequest webRequest, APIAuthentication apiAuthentication) {
		String playerId = twpService.playerGuid(webRequest);
//		String currency = webRequest.getParameter("currency");
	
		BalanceRequest br = BalanceRequest.builder().userGuid(playerId).build();
		br.setDomainName(getDomainNameFromPlayerGuid(playerId));
		br.setProviderGuid(getDomainNameFromPlayerGuid(playerId) + "/" + apiAuthentication.getProviderUrl());
		
		log.debug("BalanceRequest : "+br);
		try {
			BalanceResponse remoteResult = twpService.getCasinoService().handleBalanceRequest(br);
			
			log.debug("BalanceResponse : "+remoteResult);
			if (remoteResult.getBalanceCents() == null) {
				return null;
			}
			
			Response response = Response.builder()
			.balance(
				new BigDecimal(remoteResult.getBalanceCents()).movePointLeft(2).toString()
			).build();
			
			log.debug("Balance response: " + response);
			return response;
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
		}
		return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Balance unavailable.").build();
	}
}