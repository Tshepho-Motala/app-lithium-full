package lithium.service.casino.provider.rival.service;

import java.math.BigDecimal;
import java.util.Map;

import lithium.service.casino.provider.rival.RivalModuleInfo;
import lithium.service.casino.provider.rival.config.BrandsConfigurationBrand;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.data.request.Request;
import lithium.service.casino.provider.rival.data.request.RollbackRequest;
import lithium.service.casino.provider.rival.data.request.UpdateBalanceRequest;
import lithium.service.casino.provider.rival.data.response.RollbackResponse;
import lithium.service.casino.provider.rival.data.response.UpdateBalanceResponse;
import lithium.service.casino.provider.rival.util.HashCalculator;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RivalService {
	@Autowired
	private LithiumServiceClientFactory services;
	
	private boolean hashMatch(String hashPassword, Map<String,String> allRequestParams) {
		HashCalculator hashCalc = new HashCalculator(hashPassword);
		
		final String incomingHash = allRequestParams.get("hmac");
		
		allRequestParams.entrySet().forEach(e -> {
			
			if(!(e.getKey().contentEquals("hmac"))) {
				hashCalc.addItem(e.getKey()+"="+e.getValue());
			}
			
		});
		
		String calculatedHash = hashCalc.calculateHash();
		log.info("Incoming hash: " + incomingHash + " | calculated hash: " + calculatedHash);
		
		if(incomingHash == null || incomingHash.contentEquals(calculatedHash)) return true;
		
		return false;
	}

	/**
	 * All the request parameters except hmac needs to be passed into this function to produce a hash
	 * @param hashPassword
	 * @param allRequestParams
	 * @return hashString
	 */
	public String generateHash(String hashPassword, Map<String,String> allRequestParams) {
		HashCalculator hashCalc = new HashCalculator(hashPassword);

		allRequestParams.entrySet().forEach(e -> {

			if(!(e.getKey().contentEquals("hmac"))) {
				hashCalc.addItem(e.getKey()+"="+e.getValue());
			}

		});

		String calculatedHash = hashCalc.calculateHash();
		return calculatedHash;
	}
	
	public boolean isHashValid(Map<String, String> allParams, APIAuthentication apiAuth) {
		log.info("Hashpassword going to be used:"+ apiAuth.getBrandConfiguration().getHashPassword());
//		log.info("The calculated hash: "+request.calculateHash(apiAuth.getBrandConfiguration().getHashPassword()));

		return hashMatch(apiAuth.getBrandConfiguration().getHashPassword(), allParams);
	}
	
	public Long getBalance(String userGuid, APIAuthentication apiAuth) throws Exception {
		BalanceRequest br = new BalanceRequest();
		br.setDomainName(getDomainNameFromPlayerGuid(userGuid));
		br.setProviderGuid(getDomainNameFromPlayerGuid(userGuid)+"/"+apiAuth.getProviderUrl());
		br.setUserGuid(userGuid);
		
		BalanceResponse response = getCasinoService().handleBalanceRequest(br);
		
		return response.getBalanceCents();
	}
	
	public boolean isSessionValid(String userGuid, String userApiToken) {
		Response<Boolean> response;
		try {
			response = getCasinoService().authenticate(userGuid, userApiToken);
		} catch (Exception e) {
			log.error("Session validation exception: " + userGuid + " token: " + userApiToken, e);
			return false;
		}
		
		return response.getData();
	}
	
	public UpdateBalanceResponse updateBalance(UpdateBalanceRequest updateBalanceRequest, APIAuthentication auth) {
		log.info("UpdateBalanceRequest: " + updateBalanceRequest);
		log.info("APIAuthentication: " + auth);
		UpdateBalanceResponse updateBalanceResponse = new UpdateBalanceResponse();
		
//		if(!isHashValid(updateBalanceRequest, auth)) {
//			updateBalanceResponse.setError("Invalid hash");
//			log.info("UpdateBalanceResponse: " + updateBalanceResponse);
//			return updateBalanceResponse;
//		}
		
		try {
			if(updateBalanceRequest.getMinBalanceCents() > getBalance(updateBalanceRequest.getPlayerId(), auth)) {
				updateBalanceResponse.setError("Not enough min balance to perform transaction");
				log.info("UpdateBalanceResponse: " + updateBalanceResponse);
				return updateBalanceResponse;
			}
		} catch (Exception e1) {
			updateBalanceResponse.setError("Internal error getting player balance");
			log.info("UpdateBalanceResponse: " + updateBalanceResponse);
			return updateBalanceResponse;
		}
		//User root game id to tie back to original game, we don't care about bonus games, only main games
		String gameId = updateBalanceRequest.getGameId();
		if(updateBalanceRequest.getRootGameId() != null && !updateBalanceRequest.getRootGameId().isEmpty()) {
			gameId = updateBalanceRequest.getRootGameId();
		}
		
		BetRequest betRequest = new BetRequest();
		betRequest.setDomainName(getDomainNameFromPlayerGuid(updateBalanceRequest.getPlayerId()));
		betRequest.setGameGuid(auth.getProviderUrl()+"/"+gameId);
		betRequest.setGameSessionId(updateBalanceRequest.getTranId());
		betRequest.setProviderGuid(getDomainNameFromPlayerGuid(updateBalanceRequest.getPlayerId()) +"/"+ auth.getProviderUrl());
		betRequest.setTransactionId(updateBalanceRequest.getRequestId());
		betRequest.setUserGuid(updateBalanceRequest.getPlayerId());
		betRequest.setWin(updateBalanceRequest.getMinBalanceCents() + updateBalanceRequest.getAmountCents());
		betRequest.setBet(updateBalanceRequest.getMinBalanceCents());
		betRequest.setRoundFinished(true);
		betRequest.setRoundId(updateBalanceRequest.getTranId());
		
		try {
			BetResponse betResponse = getCasinoService().handleBetRequest(betRequest);
			if (Long.parseLong(betResponse.getExtSystemTransactionId()) <= 0) {
				throw new Exception("Problem getting transaction number in response from service casino");
			}
			updateBalanceResponse.setBalance(betResponse.getBalanceCents());
			updateBalanceResponse.setCurrency(auth.getBrandConfiguration().getCurrency());
		} catch (Exception e) {
			log.error("Error writing rival transaction to casino service: "+ updateBalanceRequest + " bet request: " + betRequest, e);
			updateBalanceResponse.setError("Unable to perform transaction, internal error");
			log.info("UpdateBalanceResponse: " + updateBalanceResponse);
			return updateBalanceResponse;
		}
		
		log.info("UpdateBalanceResponse: " + updateBalanceResponse);
		return updateBalanceResponse;
	}
	
	public RollbackResponse rollbackTransaction(RollbackRequest rollbackRequest, APIAuthentication auth) {
		log.info("RollbackRequest: " + rollbackRequest);
		log.info("APIAuthentication: " + auth);
		RollbackResponse rollbackResponse = new RollbackResponse();
		
//		if(!isHashValid(rollbackRequest, auth)) {
//			rollbackResponse.setError("Invalid hash");
//			log.info("RollbackResponse: " + rollbackResponse);
//			return rollbackResponse;
//		}
		
		RollbackTranRequest rollbackBetRequest = new RollbackTranRequest();
		rollbackBetRequest.setDomainName(getDomainNameFromPlayerGuid(rollbackRequest.getPlayerId()));
		rollbackBetRequest.setProviderGuid(getDomainNameFromPlayerGuid(rollbackRequest.getPlayerId())+"/"+auth.getProviderUrl());

		rollbackBetRequest.setTransactionId(rollbackRequest.getRequestId());
		rollbackBetRequest.setUserGuid(rollbackRequest.getPlayerId());
		
		try {
			RollbackTranResponse rollbackBetResponse = getCasinoService().rollbackTran(rollbackBetRequest);
			rollbackResponse.setBalance(rollbackBetResponse.getBalanceCents());
			rollbackResponse.setCurrency(auth.getBrandConfiguration().getCurrency());
		} catch (Exception e) {
			log.error("Unable to rollback transaction: " + rollbackRequest + " rollbackBetRequest: " + rollbackBetRequest, e);
			rollbackResponse.setError("Unable to rollback transaction, internal error");
		}
		
		log.info("RollbackResponse: " + rollbackResponse);
		return rollbackResponse;
	}
	
	private CasinoClient getCasinoService() {
		CasinoClient cl = null;
		try {
			cl = services.target(CasinoClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino service", e);
		}
		
		return cl;
	}
	
	public String getDomainNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[0];
	}

	public String getPlayerNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[1];
	}
	
//	private Long usdToCent(String usdAmount) {
//		if(usdAmount == null) return 0L;
//		
//		BigDecimal bd = new BigDecimal(usdAmount);
//		return bd.movePointRight(2).longValue();
//	}

	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for(ProviderProperty p: pp.getData()) {
			if(p.getName().equalsIgnoreCase(RivalModuleInfo.ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(RivalModuleInfo.ConfigProperties.HASH_PASSWORD.getValue())) brandConfiguration.setHashPassword(p.getValue());
			if(p.getName().equalsIgnoreCase(RivalModuleInfo.ConfigProperties.CURRENCY.getValue())) brandConfiguration.setCurrency(p.getValue());
			if(p.getName().equalsIgnoreCase(RivalModuleInfo.ConfigProperties.MOCK_FLAG.getValue())) brandConfiguration.setMockActive(Boolean.parseBoolean(p.getValue()));

		}

		return brandConfiguration;
	}

	public ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class,"service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}
}
