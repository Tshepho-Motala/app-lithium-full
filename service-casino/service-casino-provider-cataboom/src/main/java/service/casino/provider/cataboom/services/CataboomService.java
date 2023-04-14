package service.casino.provider.cataboom.services;


import com.netflix.discovery.converters.Auto;
import lithium.service.user.client.UserApiInternalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.response.BetResponse;
import service.casino.provider.cataboom.config.BrandsConfigurationBrand;
import service.casino.provider.cataboom.CataboomModuleInfo.ConfigProperties;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.config.APIAuthentication;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.entities.InitialLink;
import service.casino.provider.cataboom.entities.User;
import service.casino.provider.cataboom.repositories.CampaignRepository;
import service.casino.provider.cataboom.repositories.InitialLinkRepository;
import service.casino.provider.cataboom.repositories.UserRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
@Service
public class CataboomService {
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	CampaignRepository campaignRepository;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	InitialLinkRepository initialLinkRepository;

	public String updateBalance(String apiKey, String transactionId, Long amountCents, String userGuid, String gameId, String winlevel,
			APIAuthentication auth) {
		log.info("UpdateBalanceRequest: amountcents=" + amountCents + " playerGuid=" + userGuid + "transactionId="
				+ transactionId);
		log.info("APIAuthentication: " + auth);
//		if(!apiKey.equals(auth.getApiKey())) {
//			return "Unauthorized";
//		}
		
		int winLevel=Integer.parseInt(winlevel);
		if(winLevel<1 || winLevel>4) {
			return "Invalid Win Level";
		}
		String returnTranId = "";

		Long transactionAmount = amountCents;
		log.info("Update balance amount cents: " + transactionAmount);
		BetRequest betRequest = new BetRequest();
		betRequest.setDomainName(getDomainNameFromPlayerGuid(userGuid));
		betRequest.setGameGuid(auth.getProviderUrl() + "/" + gameId);
		betRequest.setProviderGuid(getDomainNameFromPlayerGuid(userGuid) + "/" + auth.getProviderUrl());
		// betRequest.setTransactionId(constructTransactionId());
		betRequest.setTransactionId(transactionId);
		betRequest.setUserGuid(userGuid);

		betRequest.setWin(transactionAmount);

		try {
			BetResponse betResponse = getCasinoService().handleBetRequest(betRequest);
			returnTranId = betResponse.getExtSystemTransactionId();
			if (returnTranId == null || returnTranId.isEmpty() || Long.parseLong(returnTranId) <= 0)
				returnTranId = "";

		} catch (Exception e) {
			log.error("Error writing cataboom transaction to casino service bet request: " + betRequest, e);
		}

		return returnTranId;
	}
	

	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for(ProviderProperty p: pp.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseurl(p.getValue());
		}
		
		return brandConfiguration;
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
	
	
	
	private CasinoClient getCasinoService() {
		CasinoClient cl = null;
		try {
			cl = services.target(CasinoClient.class, "service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino service", e);
		}

		return cl;
	}

	public String generateCataboomUniqueLink(String playerGuid, Long campaignId) throws Exception {
		Campaign campaign = campaignRepository.findOne(campaignId);
		log.debug("Player: " + playerGuid + " campaign: " + campaign.toString());
		userService.createUserIfNotExist(playerGuid);
		String url = getBrandConfiguration("service-casino-provider-cataboom", getDomainNameFromPlayerGuid(playerGuid))
						.getBaseurl()+"/"+campaign.getCampaignName()+"?username="+campaign.getCampaignUsername()+"&password="+campaign.getCampaignPassword()+"&AccountID=" +playerGuid;
		String data = getRemoteContents(url);
		User user = userRepository.findByPlayerGuid(playerGuid);
		String []arr = data.split("/");
		String playid = arr[arr.length-1];

		InitialLink obj = InitialLink.builder()
				.link(data)
				.campaignid(campaign.getCampaignName())
				.user(user)
				.playid(playid)
				.build();
		initialLinkRepository.save(obj);

		log.info("Unique link for user: " + playerGuid + " on campaign: " + campaign + " link: " + data);
		return data;
	}

	public String getDomainNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[0];
	}


	public String getPlayerNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[1];
	}

	//read file contents from the url
	public String getRemoteContents(String url) throws Exception {
		URL urlObject = new URL(url);
		URLConnection conn = urlObject.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine, output = "";
		while ((inputLine = in.readLine()) != null) {
			output += inputLine;
		}
		in.close();

		return output;
	}
}
