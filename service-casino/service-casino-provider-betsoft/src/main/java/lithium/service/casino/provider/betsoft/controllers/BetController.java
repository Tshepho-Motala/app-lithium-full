package lithium.service.casino.provider.betsoft.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.betsoft.config.APIAuthentication;
import lithium.service.casino.provider.betsoft.data.request.BetRequest;
import lithium.service.casino.provider.betsoft.data.requestresponse.BetRequestResponse;
import lithium.service.casino.provider.betsoft.data.response.BetResponse;

@RestController
public class BetController extends BaseController {
    
	private static final Log log = LogFactory.getLog(BetController.class);
	
//	@Autowired
//	BetHandler handler;
	
	@RequestMapping(value = "/bet", produces = "application/xml")
    BetRequestResponse bet(@RequestParam String userId, 
    		@RequestParam(required=false) String bet,
    		@RequestParam(required=false) String win,
    		@RequestParam String roundId,
    		@RequestParam Integer gameId,
    		@RequestParam(required=false) Boolean isRoundFinished,
    		@RequestParam String hash,
    		@RequestParam(required=false) String gameSessionId,
    		@RequestParam(required=false) Integer negativeBet,
    		APIAuthentication apiAuthentication
    		) {

			BetRequest request = new BetRequest(
				userId, bet, win, roundId, gameId, isRoundFinished, 
				gameSessionId, negativeBet);
		request.setHash(hash);
		
		log.info("BetController " + request);

		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword());
		if (!disableHash) {
			if (!calculatedHash.equals(hash)) {
				log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
				return new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_INVALID_HASH, "Invalid hash"));
			}
		} else {
			log.warn("Hash function disabled. | remoteHash: " + hash + " | localHash: " + calculatedHash);
		}

		lithium.service.casino.client.objects.request.BetRequest br = lithium.service.casino.client.objects.request.BetRequest.builder().build();
		br.setBet(request.getBetCents());
		if(br.getBet() != null) {
			br.setTransactionId(request.getBetTransactionId().toString());
		}
		br.setWin(request.getWinCents());
		if(br.getWin() != null) {
			br.setTransactionId(request.getWinTransactionId().toString());
		}
		br.setDomainName(getDomainNameFromPlayerGuid(userId));
		br.setProviderGuid(getDomainNameFromPlayerGuid(userId)+"/"+apiAuthentication.getProviderUrl());
		br.setUserGuid(userId);
		br.setGameGuid(apiAuthentication.getProviderUrl()+"/"+gameId);
		br.setGameSessionId(request.getGameSessionId());
		br.setNegativeBet(request.getNegativeBet() == null?null:request.getNegativeBet().longValue());
		br.setRoundFinished(request.getRoundFinished());
		br.setRoundId(request.getRoundId());
		
		try {
			lithium.service.casino.client.objects.response.BetResponse response = betsoftService.getCasinoService().handleBetRequest(br);
			
			if (response.getBalanceCents() == null) {
				return new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_UNKNOWN_USERID, "Unknown user id or accounting service down"));
			}
			
			if (Long.parseLong(response.getExtSystemTransactionId()) <= 0) {
				return new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_OPERATION_FAILED, "Problem performing transaction. Balance might be too low."));
			}
			
			BetResponse result = mapper.map(response, BetResponse.class);
			result.setResult(BetResponse.RESPONSE_SUCCESS);
			log.info("BetController " + result);
			return new BetRequestResponse(request, result);
//Handled in accounting on adjust method level
//		} catch (DuplicateTransactionException dte) {
//			log.warn("Received duplicate transaction error: " + dte.getMessage(), dte);
//			BetResponse result = betsoftService.handleDuplicateTransactionException(dte, userId);
//			return new BetRequestResponse(request, result);
			
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new BetRequestResponse(request, new BetResponse(BetResponse.RESPONSE_CODE_INTERNAL_ERROR, e.toString() + e.getMessage()));
		}
    }
	
}
