package lithium.service.casino.provider.nucleus.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.nucleus.config.APIAuthentication;
import lithium.service.casino.provider.nucleus.data.request.AccountInfoRequest;
import lithium.service.casino.provider.nucleus.data.requestresponse.AccountInfoRequestResponse;
import lithium.service.casino.provider.nucleus.data.response.AccountInfoResponse;

@RestController
public class AccountInfoController extends BaseController {
	
	private static final Log log = LogFactory.getLog(AccountInfoController.class);
	
//	@Autowired
	//AccountInfoHandler handler;
	
	@RequestMapping(value = "/accountinfo", produces = "application/xml")
    AccountInfoRequestResponse info(@RequestParam String userId, @RequestParam String hash, APIAuthentication apiAuthentication) {

		AccountInfoRequest request = new AccountInfoRequest(userId);
		
		log.info("AccountInfoController " + request);
		
		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword());
		if (!disableHash) {
			if (!calculatedHash.equals(hash)) {
				log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
				return new AccountInfoRequestResponse(request, new AccountInfoResponse(
						AccountInfoResponse.RESPONSE_CODE_INVALID_HASH, "Invalid hash"));
			}
		} else {
			log.warn("Hash function disabled. | remoteHash: " + hash + " | localHash: " + calculatedHash);
		}
		
		try {
			
			lithium.service.casino.client.objects.response.AccountInfoResponse response = nucleusService.getCasinoService().handleAccountInfoRequest(userId, null);
			if(response == null) {
				log.error("Inavlid user id provided: "+userId);
				return new AccountInfoRequestResponse(request, new AccountInfoResponse(
						AccountInfoResponse.RESPONSE_CODE_UNKNOWN_USERID, "Invalid user id"));
			}
			
			AccountInfoResponse result = 
					mapper.map(response, AccountInfoResponse.class);
			log.info("AccountInfoController " + result);
			result.setEmail("");
			result.setResult(AccountInfoResponse.RESPONSE_SUCCESS);
	        return new AccountInfoRequestResponse(request, result);
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new AccountInfoRequestResponse(request, new AccountInfoResponse(AccountInfoResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
		}
		
    }
	
}
