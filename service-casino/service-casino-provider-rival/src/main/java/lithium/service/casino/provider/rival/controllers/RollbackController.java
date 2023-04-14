package lithium.service.casino.provider.rival.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.data.request.RollbackRequest;
import lithium.service.casino.provider.rival.data.request.UpdateBalanceRequest;
import lithium.service.casino.provider.rival.data.response.RollbackResponse;
import lithium.service.casino.provider.rival.data.response.UpdateBalanceResponse;
import lithium.service.casino.provider.rival.service.RivalService;



@RestController
public class RollbackController extends BaseController {
	@Autowired
	private RivalService rivalService;
	
	@RequestMapping(params={"jsoncall=rollback"})
	public RollbackResponse rollback(APIAuthentication apiAuth, @RequestBody Map<String,String> allParams) {
		RollbackRequest rollbackRequest = new RollbackRequest(allParams);
		
		if(!rivalService.isHashValid(allParams, apiAuth)) {
			RollbackResponse rollbackResponse = new RollbackResponse();
			rollbackResponse.setError("Invalid hash");
			return rollbackResponse;
		}
		return rivalService.rollbackTransaction(rollbackRequest, apiAuth);
	}
}
