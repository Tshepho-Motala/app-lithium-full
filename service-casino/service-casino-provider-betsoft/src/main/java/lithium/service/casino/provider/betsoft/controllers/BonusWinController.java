package lithium.service.casino.provider.betsoft.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.provider.betsoft.config.APIAuthentication;
import lithium.service.casino.provider.betsoft.data.request.BonusWinRequest;
import lithium.service.casino.provider.betsoft.data.requestresponse.BonusWinRequestResponse;
import lithium.service.casino.provider.betsoft.data.response.BonusWinResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class BonusWinController extends BaseController {
	@RequestMapping(value = "/bonuswin", produces = "application/xml")
	BonusWinRequestResponse bonusWin(
		@RequestParam String userId,
		@RequestParam Integer bonusId,
		@RequestParam Long amount,
		@RequestParam String transactionId,
		@RequestParam String hash,
		APIAuthentication apiAuthentication
	) {
		BonusWinRequest request = new BonusWinRequest(userId, bonusId, amount, transactionId);
		request.setHash(hash);
		
		log.info("BonusWinController " + request);

		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword());
		if (!disableHash) {
			if (!calculatedHash.equals(hash)) {
				log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
				return new BonusWinRequestResponse(request, new BonusWinResponse(BonusWinResponse.RESPONSE_CODE_FRBW_INVALID_HASH, "Invalid hash"));
			}
		} else {
			log.warn("Hash function disabled. | remoteHash: " + hash + " | localHash: " + calculatedHash);
		}
		
		if (amount == null) {
			return new BonusWinRequestResponse(request, new BonusWinResponse(BonusWinResponse.RESPONSE_CODE_INVALID_AMOUNT, "Invalid amount"));
		}
		
		try {
			BetRequest br = new BetRequest();
			br.setWin(amount);
			br.setDomainName(getDomainNameFromPlayerGuid(userId));
			br.setProviderGuid(getDomainNameFromPlayerGuid(userId)+"/"+apiAuthentication.getProviderUrl());
			br.setGameGuid("frbwin/"+bonusId);
//			br.setGameGuid(apiAuthentication.getProviderUrl()+"/"+bonusId);
			br.setUserGuid(userId);
			br.setTransactionId(transactionId);
			br.setBonusTran(true);
			br.setBonusId(bonusId);
			
			log.info("handleBetRequest : "+br);
			BetResponse betResponse = betsoftService.getCasinoService().handleBetRequest(br);
			log.info("betResponse : "+betResponse);
			
			lithium.service.casino.client.objects.response.BonusWinResponse response = new lithium.service.casino.client.objects.response.BonusWinResponse();
			response.setBalanceCents(betResponse.getBalanceCents());
			
			if (response.getBalanceCents() == null) {
				BonusWinRequestResponse bwrr = new BonusWinRequestResponse(request, new BonusWinResponse(BonusWinResponse.RESPONSE_CODE_INVALID_USER, "Unknown user or accounting service down"));
				log.error(""+bwrr);
				return bwrr;
			}
			
			if (Long.parseLong(betResponse.getExtSystemTransactionId()) <= 0) {
				BonusWinRequestResponse bwrr = new BonusWinRequestResponse(request, new BonusWinResponse(BonusWinResponse.RESPONSE_CODE_OPERATION_FAILED, "Problem performing transaction. Balance might be too low."));
				log.error(""+bwrr);
				return bwrr;
			}
			
			BonusWinResponse result =  mapper.map(response, BonusWinResponse.class);
			log.info("BonusWinController " + result);
			result.setResult(BonusWinResponse.RESPONSE_SUCCESS);
			
			return new BonusWinRequestResponse(request, result);
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new BonusWinRequestResponse(request, new BonusWinResponse(BonusWinResponse.RESPONSE_CODE_FRBW_INTERNAL_ERROR, e.getMessage()));
		}
	}
}