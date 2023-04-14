package lithium.service.casino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.response.AccountInfoResponse;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.service.CasinoService;
import lombok.extern.slf4j.Slf4j;

@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
@Slf4j
@RestController
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class AccountInfoController {
	@Autowired
	ServiceCasinoConfigurationProperties serviceGamesConfig;
//	@Autowired
//	private LithiumServiceClientFactory services;
//	@Autowired
//	private ModelMapper mapper;
	@Autowired
	private CasinoService casinoService;
	
	@RequestMapping("/casino/accountInfo")
	public AccountInfoResponse handleAccountInfoRequest(@RequestParam("guid") String guid, @RequestParam(name="userApiToken", required=false) String userApiToken) throws Exception {
		return casinoService.getUserInfo(guid, userApiToken);
	}
}
