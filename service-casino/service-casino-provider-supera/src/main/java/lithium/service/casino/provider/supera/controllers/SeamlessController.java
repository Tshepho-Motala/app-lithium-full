package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.data.seamless.response.SeamlessResponse;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SeamlessController extends BaseController {
	
	@Autowired
	private SuperaService superaService;
	
	@RequestMapping("/")
	public SeamlessResponse handleRequest(
		HttpServletRequest req,
		APIAuthentication authentication
	) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String action = req.getParameter("action") != null ? req.getParameter("action") : null;
		if (action == null) {
			log.warn("Attempt to access SeamlessController with no action");
			return new SeamlessResponse(
				SeamlessResponse.HTTP_STATUS_INTERNAL_SERVER_ERROR,
				new BigDecimal(0).movePointLeft(2),
				"Required parameter 'action' is missing"
			);
		}
		String key = req.getParameter("key") != null ? req.getParameter("key") : null;
		
		if (key == null) {
			log.warn("Attempt to access SeamlessController with no key");
			return new SeamlessResponse(
				SeamlessResponse.HTTP_STATUS_INTERNAL_SERVER_ERROR,
				new BigDecimal(0).movePointLeft(2),
				"Required parameter 'key' is missing"
			);
		}
		
		boolean okToProceed = false;
		try {
			okToProceed = superaService.validateRequest(req, authentication.getBrandConfiguration().getSaltKey());
		} catch (Exception e) {
			log.error("Problem with hashmatching on request: " + req.toString());
		}
		
		if (okToProceed) {
			if (action.equals("balance")) {
				return balance(
					req.getParameter("remote_id") != null ? req.getParameter("remote_id") : null,
					req.getParameter("remote_data") != null ? req.getParameter("remote_data") : null,
					req.getParameter("session_id") != null ? req.getParameter("session_id") : null,
					authentication
				);
			} else if (action.equals("credit")) {
				return credit(
					req.getParameter("action_type") != null ? req.getParameter("action_type") : null,
					req.getParameter("remote_id") != null ? req.getParameter("remote_id") : null,
					req.getParameter("amount") != null ? new BigDecimal(req.getParameter("amount")) : null,
					req.getParameter("game_id") != null ? req.getParameter("game_id") : null,
					req.getParameter("transaction_id") != null ? req.getParameter("transaction_id") : null,
					req.getParameter("round_id") != null ? req.getParameter("round_id") : null,
					req.getParameter("remote_data") != null ? req.getParameter("remote_data") : null,
					req.getParameter("session_id") != null ? req.getParameter("session_id") : null,
					authentication
				);
			} else if (action.equals("debit")) {
				return debit(
					req.getParameter("action_type") != null ? req.getParameter("action_type") : null,
					req.getParameter("remote_id") != null ? req.getParameter("remote_id") : null,
					req.getParameter("amount") != null ? new BigDecimal(req.getParameter("amount")) : null,
					req.getParameter("game_id") != null ? req.getParameter("game_id") : null,
					req.getParameter("transaction_id") != null ? req.getParameter("transaction_id") : null,
					req.getParameter("round_id") != null ? req.getParameter("round_id") : null,
					req.getParameter("remote_data") != null ? req.getParameter("remote_data") : null,
					req.getParameter("session_id") != null ? req.getParameter("session_id") : null,
					authentication
				);
			} else {
				log.error("Attempted to access SeamlessController with unknown action");
				return new SeamlessResponse(SeamlessResponse.HTTP_STATUS_UNAUTHORIZED, new BigDecimal(0).movePointLeft(2), "Unknown action");
			}
		} else {
			
			return new SeamlessResponse(
				SeamlessResponse.HTTP_STATUS_UNAUTHORIZED,
				new BigDecimal(0).movePointLeft(2),
				"Key did not match"
			);
		}
	}
	
	private SeamlessResponse balance(String remoteId, String remoteData, String sessionId, APIAuthentication authentication) {
		lithium.service.casino.client.objects.request.BalanceRequest clRequest = 
				new lithium.service.casino.client.objects.request.BalanceRequest();
		clRequest.setDomainName(getDomainNameFromPlayerGuid(remoteId));
		clRequest.setProviderGuid(getDomainNameFromPlayerGuid(remoteId)+"/"+authentication.getProviderUrl());
		clRequest.setUserGuid(remoteId);
		BalanceResponse clResponse = null;
		try {
			clResponse = superaService.getCasinoService().handleBalanceRequest(clRequest);
		} catch (Exception e) {
			 log.error("Problem with balance request for user: " + remoteId, e);
		}
		
		SeamlessResponse response = new SeamlessResponse();
		if(clResponse != null && clResponse.getBalanceCents() != null) {
			response.setBalance(new BigDecimal(clResponse.getBalanceCents()).movePointLeft(2));
			response.setStatus(SeamlessResponse.HTTP_STATUS_OK);
		} else {
			response.setStatus(SeamlessResponse.HTTP_STATUS_INTERNAL_SERVER_ERROR);
		}
		
		log.info("Processed balance request for user " + remoteId + " | sent json response: " + response.toString());
		return response;
	}
	
	private SeamlessResponse credit(String actionType, String remoteId, BigDecimal amount, String gameId, String transactionId,
			String roundId, String remoteData, String sessionId, APIAuthentication authentication) {
		BetRequest clRequest = new lithium.service.casino.client.objects.request.BetRequest();
		
		clRequest.setDomainName(getDomainNameFromPlayerGuid(remoteId));
		clRequest.setProviderGuid(getDomainNameFromPlayerGuid(remoteId)+"/"+authentication.getProviderUrl());
		clRequest.setUserGuid(remoteId);
		clRequest.setTransactionId(transactionId);
		
		if(actionType.equalsIgnoreCase("win") || actionType.equalsIgnoreCase("win_free")) {
			clRequest.setWin(amount.movePointRight(2).longValue());
			clRequest.setGameGuid(getDomainNameFromPlayerGuid(remoteId)+"/"+authentication.getProviderUrl()+"_"+gameId);
			clRequest.setGameSessionId(sessionId);
			clRequest.setRoundId(roundId);
			if(actionType.equalsIgnoreCase("win_free")) {
				clRequest.setGameGuid("frbwin/"+clRequest.getGameGuid());
			}
		} else if(actionType.equalsIgnoreCase("ref")){
			clRequest.setNegativeBet(amount.movePointRight(2).longValue());
		} else {
			log.error("Unknown action type: " + actionType + " Remote transaction id: " + transactionId + " user: " + remoteId);
		}
		

	
		BetResponse clResponse = null;
		Long tranId = -1L;
		try {
			clResponse = superaService.getCasinoService().handleBetRequest(clRequest);
			tranId = Long.parseLong(clResponse.getExtSystemTransactionId());
		} catch (Exception e) {
			 log.error("Problem with win request for user: " + remoteId, e);
		}
		
		SeamlessResponse response = new SeamlessResponse();
		if(clResponse != null && clResponse.getBalanceCents() != null && tranId > 0) {
			response.setBalance(new BigDecimal(clResponse.getBalanceCents()).movePointLeft(2));
			response.setStatus(SeamlessResponse.HTTP_STATUS_OK);
		} else {
			response.setStatus(SeamlessResponse.HTTP_STATUS_INTERNAL_SERVER_ERROR);
		}
		
		log.info("Processed credit request for user " + remoteId + " | sent json response: " + response.toString());
		return response;
	}
	
	private SeamlessResponse debit(String actionType, String remoteId, BigDecimal amount, String gameId, String transactionId,
			String roundId, String remoteData, String sessionId, APIAuthentication authentication) {
		BetRequest clRequest = new lithium.service.casino.client.objects.request.BetRequest();
		
		clRequest.setDomainName(getDomainNameFromPlayerGuid(remoteId));
		clRequest.setProviderGuid(getDomainNameFromPlayerGuid(remoteId)+"/"+authentication.getProviderUrl());
		clRequest.setUserGuid(remoteId);
		clRequest.setTransactionId(transactionId);
		
		clRequest.setBet(amount.movePointRight(2).longValue());
		clRequest.setGameGuid(getDomainNameFromPlayerGuid(remoteId)+"/"+authentication.getProviderUrl()+"_"+gameId);
		clRequest.setGameSessionId(sessionId);
		clRequest.setRoundId(roundId);
		if(actionType.equalsIgnoreCase("bet_free")) {
			clRequest.setGameGuid("frbbet/"+clRequest.getGameGuid());
		}
	
		BetResponse clResponse = null;
		Long tranId = -1L;
		try {
			clResponse = superaService.getCasinoService().handleBetRequest(clRequest);
			tranId = Long.parseLong(clResponse.getExtSystemTransactionId());
		} catch (Exception e) {
			 log.error("Problem with bet request for user: " + remoteId + " Response: " + clResponse, e);
		}
		
		SeamlessResponse response = new SeamlessResponse();
		if(clResponse != null && clResponse.getBalanceCents() != null && tranId > 0) {
			response.setBalance(new BigDecimal(clResponse.getBalanceCents()).movePointLeft(2));
			response.setStatus(SeamlessResponse.HTTP_STATUS_OK);
		} else {
			response.setStatus(SeamlessResponse.HTTP_STATUS_INTERNAL_SERVER_ERROR);
		}
		log.info("Processed debit request for user " + remoteId + " | sent json response: " + response.toString());
		return response;
	}
}