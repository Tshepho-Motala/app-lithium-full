package lithium.service.casino.provider.rival.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.data.request.ValidateRequest;
import lithium.service.casino.provider.rival.data.response.ValidateResponse;
import lithium.service.casino.provider.rival.service.RivalService;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
public class ValidateController extends BaseController {
	@Autowired
	private RivalService rivalService;
	
	@RequestMapping(params={"jsoncall=validate"})
	public @ResponseBody ValidateResponse validate(APIAuthentication apiAuth, @RequestBody Map<String,String> allParams) {
		ValidateRequest validateRequest = new ValidateRequest(allParams);

		ValidateResponse response = new ValidateResponse();
		log.info("validateRequest: " + validateRequest);
		log.info("APIAuthentication: " + apiAuth);
		if(rivalService.isHashValid(allParams, apiAuth)) {
			if(rivalService.isSessionValid(validateRequest.getPlayerId(), validateRequest.getSessionId())) {
				try {
					
					response.setBalance(rivalService.getBalance(validateRequest.getPlayerId(), apiAuth));
					
				} catch (Exception ex) {
					log.error("Unable to get player balance: " + validateRequest, ex);
					response.setError("Unable to get player balance");
				}
			} else {
				response.setError("Session invalid");
			}
		}else {
			response.setError("Hash validation failed");
		}
		
		response.setCurrency(apiAuth.getBrandConfiguration().getCurrency());
		
		return response;
	}
}
