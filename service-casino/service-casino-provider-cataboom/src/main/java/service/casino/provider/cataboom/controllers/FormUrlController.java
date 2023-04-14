package service.casino.provider.cataboom.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
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
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.ProviderClient;
import lithium.service.user.client.UserApiInternalClient;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.config.APIAuthentication;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.entities.InitialLink;
import service.casino.provider.cataboom.entities.User;

import service.casino.provider.cataboom.mock.MockController;
import service.casino.provider.cataboom.objects.ReceivedParams;
import service.casino.provider.cataboom.repositories.CampaignRepository;
import service.casino.provider.cataboom.repositories.InitialLinkRepository;
import service.casino.provider.cataboom.repositories.UserRepository;
import service.casino.provider.cataboom.services.CataboomService;
import service.casino.provider.cataboom.services.UserService;



@Slf4j
@RestController
public class FormUrlController extends BaseController{
	
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	CampaignRepository repository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	InitialLinkRepository linkRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserService userService;
	@Autowired
	CataboomService cataboomService;
	
	
	@ResponseBody
	@RequestMapping("/FormGameUrl")
	public Response<String> cataboomReqUrlActual(Long campaignid,
											Principal principal,
											APIAuthentication auth,
											@PathVariable String providerUrl,
											@PathVariable String domainName
											) throws Exception{

		String playerguid=domainName+"/"+principal.getName();
		UserApiInternalClient cl = getUserService();
		if(cl.getUser(playerguid)==null) {
			log.error("Unknown user attempted to get a cataboom link: " + playerguid);
			return null;
		}

		return Response.<String>builder()
				.data(cataboomService.generateCataboomUniqueLink(playerguid, campaignid))
				.build();
	}
	@ResponseBody
	@RequestMapping("/findplayerid")
	public Response<List<InitialLink>> findbyplayerID(String playerguid){
		User user=userRepo.findByPlayerGuid(playerguid);
		long id=user.getId();
		List<InitialLink> list= linkRepo.findByUserId(id);
		return Response.<List<InitialLink>>builder().data(list).build();
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
