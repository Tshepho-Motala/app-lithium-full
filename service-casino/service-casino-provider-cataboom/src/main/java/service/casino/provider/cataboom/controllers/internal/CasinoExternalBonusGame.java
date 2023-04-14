package service.casino.provider.cataboom.controllers.internal;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.casino.provider.cataboom.services.CataboomService;

import java.util.UUID;


@RestController
@RequestMapping("/casino/externalBonusGame")
@Slf4j
public class CasinoExternalBonusGame {
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected CataboomService cataboomService;

	@RequestMapping(method = RequestMethod.GET,value = "/generateExternalBonusLink")
	public Response<String> generateLink(
			@RequestParam("playerGuid") String playerGuid, @RequestParam("campaignId") Long campaignId
	) throws Exception {
		return Response.<String>builder()
				.data(cataboomService.generateCataboomUniqueLink(playerGuid, campaignId))
				.build();

//		return Response.<String>builder()
//				.data("http://thisisauniquelink.com/"+ UUID.randomUUID())
//				.build();
	}
}
