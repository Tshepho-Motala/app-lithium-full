package lithium.service.casino.provider.rival.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.data.request.BalanceRequest;
import lithium.service.casino.provider.rival.data.response.BalanceResponse;
import lithium.service.casino.provider.rival.service.RivalService;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
public class GetBalanceController extends BaseController {
	@Autowired
	RivalService rivalService;
	
	@RequestMapping(params={"jsoncall=getbalance"})
	public BalanceResponse getBalance(APIAuthentication apiAuth, @RequestBody Map<String, String> allParams) {
		BalanceRequest balanceRequest = new BalanceRequest(allParams);
		BalanceResponse balanceResponse = new BalanceResponse();
		log.info("balanceRequest: " + balanceRequest);
		log.info("APIAuthentication: " + apiAuth);
		if(rivalService.isHashValid(allParams, apiAuth)) {
			balanceResponse.setCurrency(apiAuth.getBrandConfiguration().getCurrency());
			try {
				balanceResponse.setBalance(rivalService.getBalance(balanceRequest.getPlayerId(), apiAuth));
			} catch (Exception e) {
				log.error("Unable to get balance: " + balanceRequest, e);
				balanceResponse.setError("Unable to get balance");
			}
		} else {
			balanceResponse.setError("Invalid hash");
		}
		log.info("balanceResponse: " + balanceResponse);
		return balanceResponse;
	}
}
