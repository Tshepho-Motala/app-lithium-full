package service.casino.provider.cataboom.mock;

import static lithium.service.Response.Status.OK;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.config.APIAuthentication;
import service.casino.provider.cataboom.controllers.UpdateBalanceController;
import service.casino.provider.cataboom.controllers.UpdateBalanceController.CataboomFailedException;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.repositories.CampaignRepository;

@Slf4j
@Controller
@RequestMapping("/mock")
public class MockController {
	@Autowired
	CampaignRepository res;
	@Autowired
	protected LithiumServiceClientFactory services;
	@Autowired
	MockService service;
	@Autowired
	private HashMap<String, String> map;
	@Autowired
	private HashMap<String, Campaign> campaignMap;
	@Autowired
	CampaignRepository repository;
	@Autowired
	private RestTemplate restTemplate;
	

	
	@Bean
	public HashMap<String, Campaign> campaignMap() {
		List<Campaign> list=(List<Campaign>) res.findAll();
		HashMap<String, Campaign> mapCampaign = new HashMap<>(); 
		for(Campaign c : list){
			mapCampaign.put(c.getCampaignName().toString(),c);
		}
		return mapCampaign;
	}
	
	@Autowired
	private LithiumConfigurationProperties config;
	
	@RequestMapping("/form")
	public String form(InputFields inputFields) {
		return "form";
	}

	@RequestMapping("/FormUrlStep1")
	public String mockFormUrlStep1( 
									@Valid InputFields inputFields,
									@RequestParam(value="campaignid")String campaignid,
									@RequestParam(value="playerGuid")String playerGuid,
									BindingResult result,
									Model model){
		if (result.hasErrors()) {
			return "form";
		}
		Campaign campaign= res.findByCampaignName(campaignid);
		String cUsername=campaign.getCampaignUsername();
		String cPassword=campaign.getCampaignPassword();
		log.info(campaign.toString());
		String url=catFakeUrl(playerGuid,campaign.getCampaignName(),cUsername,cPassword);
		String[]getplayid=url.split("/");
		int len=getplayid.length-1;
		String playid= getplayid[len];
		model.addAttribute("url", url);
		model.addAttribute("playerGuid",playerGuid);
		model.addAttribute("campaignid", campaign.getCampaignName().toString());
		model.addAttribute("playid", playid);
		return "formUrl";
	}
	
	
	@ResponseBody
	@RequestMapping("/{providerUrl}/{domainName}/FormGameUrlMock")
	public Response<String> cataboomReqUrl(	Long campaignid,
											Principal principal,
											APIAuthentication auth,
											@PathVariable String providerUrl,
											@PathVariable String domainName
											) throws Exception{
		Campaign campaign=repository.findOne(campaignid);
		log.info(campaign.toString());
		String playerguid=domainName+"/"+principal.getName();
		UserApiInternalClient cl=getUserService();
		if(cl.getUser(playerguid)==null) {return null;}
		String url=config.getGatewayPublicUrl()+"/"+providerUrl+"/dplay/"+""+campaign.getCampaignName()+"/";
		
		final String baseUrl = config.getGatewayPublicUrl() + "/" + "service-casino-provider-cataboom/mock/catFakeUrl?playerguid="+playerguid
				+"&campaignName="+campaign.getCampaignName()+"&campaignUsername="+campaign.getCampaignUsername()+"&campaignPassword="+campaign.getCampaignPassword();
		log.info("BASE URL: "+baseUrl);
		URI uri = new URI(baseUrl);

		ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
		String getMockUrl = result.getBody();
		String[]getplayid=getMockUrl.split("/");
		int len=getplayid.length-1;
		String playid= getplayid[len];
		url+=playid;
		log.info(url);
		return Response.<String>builder().data(url).build();
	}

	
	@ResponseBody
	@RequestMapping("/catFakeUrl")
	public String catFakeUrl(String playerguid, String campaignName, String campaignUsername, String campaignPassword){
		String uuid=constructTransactionId();
		String url;
		Campaign campaign= campaignMap.get(campaignName);
		if(campaign!=null) {
			if(campaign.getCampaignUsername().equals(campaignUsername)&& campaign.getCampaignPassword().equals(campaignPassword)) {
		if(map.containsValue(playerguid+"_split now_"+campaign.getCampaignName())) {
		url= "Already played";
		return url;
		}
		map.put(uuid, playerguid+"_split now_"+campaign.getCampaignName());
		url=config.getGatewayPublicUrl()+"/mock/unique/";
		url+= uuid;
		return url;
			}
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/fakewin")	// fake a win and give user a prize
	public String testAsynch(String playerGuid, String campaignid,String playid) throws InterruptedException, ExecutionException, CataboomFailedException, URISyntaxException {
		
		CompletableFuture<String> message = service.congratulations();
		CompletableFuture<String> message2 = service.testpost(playerGuid,campaignid,playid);

		// Wait until they are all done
		CompletableFuture.allOf(message, message2).join();
		log.info("OuputMessage--> " + message.get());
		return message.get();
	}

	private String constructTransactionId() {
		return UUID.randomUUID().toString();
	}
	
	private UserApiInternalClient getUserService() {
		UserApiInternalClient cl = null;
		try {
			cl = services.target(UserApiInternalClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}

}
