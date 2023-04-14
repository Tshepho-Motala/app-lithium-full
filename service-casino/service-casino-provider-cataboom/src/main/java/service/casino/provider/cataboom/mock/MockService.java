package service.casino.provider.cataboom.mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import service.casino.provider.cataboom.controllers.UpdateBalanceController;
import service.casino.provider.cataboom.controllers.UpdateBalanceController.CataboomFailedException;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.mock.ReceivedParamsMockController;
import service.casino.provider.cataboom.objects.ReceivedParams;
import service.casino.provider.cataboom.repositories.CampaignRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MockService {
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	UpdateBalanceController ub;
	@Autowired
	CampaignRepository res;
	@Bean
	public HashMap<String, String> map() {
		return new HashMap<String, String>();
	}
	
	@Async("asyncExecutor")	//simulate win screen
	public CompletableFuture<String> congratulations() throws InterruptedException {
		log.info("reached congratulations method");
		String data = "Congratulations, You won. Assigning your prize";
		Thread.sleep(1000L); // Intentional delay
		log.info("congratulations method completed with data: " + data);
		return CompletableFuture.completedFuture(data);
	}

	@Async("asyncExecutor")	//simulate call to assign bonus endpoint
	public CompletableFuture<String> testpost(String playerGuid,String campaignid, String playid)
			throws InterruptedException, CataboomFailedException, URISyntaxException {
		log.info("Called AssignBonusMethod");
		Campaign campaign=res.findByCampaignName(campaignid);
		final String baseUrl = config.getGatewayPublicUrl() + "/" + "service-casino-provider-cataboom/getPrizeInfo";
		URI uri = new URI(baseUrl);

		ResponseEntity<ReceivedParams> result = restTemplate.getForEntity(uri, ReceivedParams.class);
		ReceivedParams pc = result.getBody();
		pc.setAccountid(playerGuid);
		pc.setCampaignid(campaign.getCampaignName());
		pc.setToken(campaign.getToken());
		pc.setPlayid(playid);
		log.info(pc.toString());

		final String postUrl = config.getGatewayPublicUrl() + "/"
				+ "service-casino-provider-cataboom/cataboom/prizefulfill";
		URI urii = new URI(postUrl);

		ResponseEntity<ReceivedParams> resultPost = restTemplate.postForEntity(urii, pc, ReceivedParams.class);
		log.info("Successful assigned bonus to player");

		Thread.sleep(1000L); // Intentional delay

		return CompletableFuture.completedFuture("Exiting post method");
	}

}
