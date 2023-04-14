package service.casino.provider.cataboom.mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.controllers.UpdateBalanceController;
import service.casino.provider.cataboom.controllers.UpdateBalanceController.CataboomFailedException;
@RestController
@Slf4j
public class MockPlayLinkController {
	@Autowired
	private HashMap<String, String> map;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private LithiumConfigurationProperties config;
	@ResponseBody
	@RequestMapping("/dplay/{campaignid}/{uuid}")
	public Response<String> data(@PathVariable String campaignid,
									@PathVariable String uuid) throws InterruptedException, ExecutionException, CataboomFailedException, URISyntaxException{
		String playerguid="";
				if(map.containsKey(uuid)) {
					log.info("MAP: "+map.toString());
					String getguidfrommap=map.get(uuid);
					log.info("getguidfrommap"+ getguidfrommap);
					String []split=getguidfrommap.split("_split now_");
					playerguid=split[0];
					log.info("playerGUID: " +playerguid);
				}
				else {
					return null;
				}
		
		log.info("campaignid: "+campaignid + "uuid :" +uuid + "playerguid :"+playerguid);
		
		final String baseUrl = config.getGatewayPublicUrl() + "/" + "service-casino-provider-cataboom/mock/fakewin?playerGuid="+playerguid
				+"&campaignid="+campaignid+"&playid="+uuid;
		log.info("BASE URL: "+baseUrl);
		URI uri = new URI(baseUrl);

		ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
		String response = result.getBody();
		return Response.<String>builder().data(response).build();
	}
}
