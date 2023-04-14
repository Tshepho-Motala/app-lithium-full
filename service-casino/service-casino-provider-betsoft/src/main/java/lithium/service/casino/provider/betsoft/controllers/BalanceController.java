package lithium.service.casino.provider.betsoft.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.betsoft.config.APIAuthentication;
import lithium.service.casino.provider.betsoft.data.request.BalanceRequest;
import lithium.service.casino.provider.betsoft.data.requestresponse.BalanceRequestResponse;
import lithium.service.casino.provider.betsoft.data.response.BalanceResponse;

@RestController
public class BalanceController extends BaseController {
    
	private static final Log log = LogFactory.getLog(BalanceController.class);
	
//	@Autowired
//	BalanceHandler handler;
	
	@RequestMapping(value = "/balance", produces = "application/xml")
	BalanceRequestResponse balance(@RequestParam String userId, APIAuthentication apiAuthentication) {
		BalanceRequest request = new BalanceRequest(userId);
		log.debug("BalanceController " + request);
		
		lithium.service.casino.client.objects.request.BalanceRequest br = mapper.map(request, lithium.service.casino.client.objects.request.BalanceRequest.class);
		br.setDomainName(getDomainNameFromPlayerGuid(userId));
		br.setProviderGuid(getDomainNameFromPlayerGuid(userId)+"/"+apiAuthentication.getProviderUrl());
		br.setUserGuid(userId);
		
		log.debug("BalanceRequest :: "+br);
		try {
			lithium.service.casino.client.objects.response.BalanceResponse remoteResult = betsoftService.getCasinoService().handleBalanceRequest(br);
			
			if (remoteResult.getBalanceCents() == null) {
				return new BalanceRequestResponse(request, new BalanceResponse(BalanceResponse.RESPONSE_CODE_UNKNOWN_USERID, "Unknown user id or accounting service is down"));
			}
			
			BalanceResponse result = mapper.map(remoteResult, BalanceResponse.class);
			result.setResult(BalanceResponse.RESPONSE_SUCCESS);
			
			log.debug("BalanceController " + result);
			return new BalanceRequestResponse(request, result);
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new BalanceRequestResponse(request, new BalanceResponse(BalanceResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
		}

    }
	
}
