package lithium.service.casino.provider.livedealer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.livedealer.config.APIAuthentication;
import lithium.service.casino.provider.livedealer.service.LivedealerService;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
public class GetBalanceController extends BaseController {
	@Autowired
	LivedealerService livedealerService;
	
	@RequestMapping("/getCustomerInfo")
	public Long getCustomerInfo(@RequestParam("customerID") String customerId,
			@RequestParam("user") String clientUser,
			@RequestParam("password") String clientPassword,
			APIAuthentication apiAuth) {

		log.info("balanceRequest: " + customerId);
		log.info("APIAuthentication: " + apiAuth);
		
		if(!(clientUser.contentEquals(apiAuth.getBrandConfiguration().getClientUser()) && 
				clientPassword.contentEquals(apiAuth.getBrandConfiguration().getClientPassword()))) {
			log.error("Authentication is invalid for "+customerId+" : " + clientUser +"!="+ apiAuth.getBrandConfiguration().getClientUser() +
					" or " + clientPassword +"!="+ apiAuth.getBrandConfiguration().getClientPassword());
			return 0L;
		}
		
		try {
			return livedealerService.getBalance(customerId, apiAuth);
		} catch (Exception e) {
			log.error("Unable to get balance: " + customerId, e);
			return 0L;
		}
	}
}
