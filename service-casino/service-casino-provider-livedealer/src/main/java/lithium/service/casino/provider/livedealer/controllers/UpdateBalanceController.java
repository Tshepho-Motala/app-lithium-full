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
public class UpdateBalanceController extends BaseController {
	@Autowired
	private LivedealerService livedealerService;
	
	@RequestMapping("/postTransaction")
	public String updateBalance(
			@RequestParam("transactionID") String transactionId,
			@RequestParam("customerID") String customerId,
			@RequestParam("amount") Long amount,
			@RequestParam("transactionType") String transactionType,
			@RequestParam("user") String clientUser,
			@RequestParam("password") String clientPassword, APIAuthentication apiAuth) {
		
		if(!(clientUser.contentEquals(apiAuth.getBrandConfiguration().getClientUser()) && 
				clientPassword.contentEquals(apiAuth.getBrandConfiguration().getClientPassword()))) {
			log.error("Authentication is invalid for "+customerId+" : " + clientUser +"!="+ apiAuth.getBrandConfiguration().getClientUser() +
					" or " + clientPassword +"!="+ apiAuth.getBrandConfiguration().getClientPassword());
			return "";
		}

		if(transactionType.contentEquals("credit")) {
			amount = Math.abs(amount);
		} else {
			amount = Math.abs(amount) *-1;
		}
		return livedealerService.updateBalance(transactionId, amount, customerId, "unknown", apiAuth);
	}
}
