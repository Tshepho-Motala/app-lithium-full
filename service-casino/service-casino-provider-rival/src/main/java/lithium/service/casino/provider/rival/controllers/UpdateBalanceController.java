package lithium.service.casino.provider.rival.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.data.request.UpdateBalanceRequest;
import lithium.service.casino.provider.rival.data.response.UpdateBalanceResponse;
import lithium.service.casino.provider.rival.service.RivalService;



@RestController
public class UpdateBalanceController extends BaseController {
	@Autowired
	private RivalService rivalService;
	
	@RequestMapping(params={"jsoncall=updatebalance"})
	public UpdateBalanceResponse updateBalance(APIAuthentication apiAuth, @RequestBody Map<String,String> allParams) {
		UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest(allParams);
		
		if(!rivalService.isHashValid(allParams, apiAuth)) {
			UpdateBalanceResponse updateBalanceResponse = new UpdateBalanceResponse();
			updateBalanceResponse.setError("Invalid hash");
			return updateBalanceResponse;
		}
		
		return rivalService.updateBalance(updateBalanceRequest, apiAuth);
	}
}
