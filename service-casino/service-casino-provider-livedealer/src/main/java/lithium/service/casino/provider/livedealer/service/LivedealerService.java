package lithium.service.casino.provider.livedealer.service;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.provider.livedealer.LivedealerModuleInfo.ConfigProperties;
import lithium.service.casino.provider.livedealer.config.APIAuthentication;
import lithium.service.casino.provider.livedealer.config.BrandsConfigurationBrand;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LivedealerService {
	@Autowired
	private LithiumServiceClientFactory services;
	
	@Autowired
	private ApplicationContext appContext;
	
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
	
	public String updateBalance(String transactionId, Long amountCents, String userGuid, String gameId, APIAuthentication auth) {
		log.info("UpdateBalanceRequest: amountcents=" + amountCents +" playerGuid="+userGuid + "transactionId="+transactionId);
		log.info("APIAuthentication: " + auth);
		String returnTranId = "";
		
		Long transactionAmount = amountCents;
		log.info("Update balance amount cents: "+ transactionAmount);
		BetRequest betRequest = new BetRequest();
		betRequest.setDomainName(getDomainNameFromPlayerGuid(userGuid));
		betRequest.setGameGuid(auth.getProviderUrl()+"/"+gameId);
		betRequest.setProviderGuid(getDomainNameFromPlayerGuid(userGuid)+"/"+auth.getProviderUrl());
		//betRequest.setTransactionId(constructTransactionId());
		betRequest.setTransactionId(transactionId);
		betRequest.setUserGuid(userGuid);
		if(transactionAmount > 0) {
			betRequest.setWin(transactionAmount);
		} else {
			betRequest.setBet(transactionAmount);
		}
		
		try {
			BetResponse betResponse = getCasinoService().handleBetRequest(betRequest);
			returnTranId = betResponse.getExtSystemTransactionId();
			
			if(returnTranId == null || returnTranId.isEmpty() || Long.parseLong(returnTranId) <= 0)  returnTranId = "";
			
		} catch (Exception e) {
			log.error("Error writing livedealer transaction to casino service bet request: " + betRequest, e);
		}
		
		return returnTranId;
	}
	
	public String getLiveDealerToken(String providerUrl, String domainName, String userGuid) {
		
		BrandsConfigurationBrand brandConfiguration = getBrandConfiguration(providerUrl, domainName);
		
		
		StringBuffer sb = new StringBuffer();
		sb.append(brandConfiguration.getBaseUrl());
		sb.append("/masterclientwebservice/masterclientwebservice.asmx/getEncryptedToken?");
		sb.append("clientUser=");
		sb.append(brandConfiguration.getClientUser());
		sb.append("&");
		sb.append("clientPassword=");
		sb.append(brandConfiguration.getClientPassword());
		sb.append("&");
		sb.append("customerID=");
		sb.append(userGuid);
		sb.append("&");
		sb.append("agentID=0&");
		sb.append("nickname=");
		sb.append(userGuid.substring(userGuid.lastIndexOf("/")+1));

		String outputString = sb.toString();
		log.info("Token request url for user("+userGuid+"): " + outputString);
		
		Resource r = appContext.getResource(outputString);
		String token = null;
		final String prefix = "<string xmlns=\"http://games.golivedealer.com/\">";
		//if(r.exists()) {
			try {
				for(String data: IOUtils.readLines(r.getInputStream())) {
					if (data.startsWith(prefix)) {
						token = data.substring(prefix.length(), data.indexOf("</"));
						log.info("Token value for user("+userGuid+"): " + token);
						return token;
					}
				}
			} catch (Exception e) {
				log.warn("Unable to read data from: " + r.getDescription(), e);
			}
		//}
		log.warn("Unable to get token value for user("+userGuid+")");
		return token;
	}
	
	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for(ProviderProperty p: pp.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CLIENT_USER.getValue())) brandConfiguration.setClientUser(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CLIENT_PASSWORD.getValue())) brandConfiguration.setClientPassword(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CURRENCY.getValue())) brandConfiguration.setCurrency(p.getValue());
		}
		
		return brandConfiguration;
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
	
	private ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class,"service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}
	
	private String constructTransactionId() {
		return UUID.randomUUID().toString();
	}
	
	public String getDomainNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[0];
	}

	public String getPlayerNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[1];
	}
}
