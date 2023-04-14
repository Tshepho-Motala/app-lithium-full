package lithium.service.casino.provider.nucleus.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.provider.nucleus.config.APIAuthentication;
import lithium.service.casino.provider.nucleus.data.request.AuthenticationRequest;
import lithium.service.casino.provider.nucleus.data.requestresponse.AuthenticationRequestResponse;
import lithium.service.casino.provider.nucleus.data.response.AuthenticationResponse;
import lithium.tokens.LithiumTokenUtil;



@RestController
public class AuthenticateController extends BaseController {
	
	private static final Log log = LogFactory.getLog(AuthenticateController.class);
	
	@Autowired
	BalanceController balanceController;
	
	@RequestMapping(value = "/auth", produces = "application/xml")
    AuthenticationRequestResponse auth(@RequestParam String token, @RequestParam String hash, APIAuthentication apiAuthentication) {

		AuthenticationRequest request = new AuthenticationRequest(token);
		
		log.debug("AuthenticateController " + request);
		
		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword());
		if (!disableHash) {
			if (!calculatedHash.equals(hash)) {
				log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
				return new AuthenticationRequestResponse(request, new AuthenticationResponse(
						AuthenticationResponse.RESPONSE_CODE_INVALID_HASH, "Invalid hash"));
			}
		} else {
			log.warn("Hash function disabled. | remoteHash: " + hash + " | localHash: " + calculatedHash);
		}
		
		try {
			AuthenticationResponse result = null;
			Response<Boolean> authResponse = nucleusService.getCasinoService().authenticate(getUserGuidFromUserApiToken(token), getApiTokenFromUserApiToken(token));
			if(authResponse.getStatus() == Status.OK && authResponse.getData().booleanValue() == true) {
				result = new AuthenticationResponse();
				result.setCode(AuthenticationResponse.RESPONSE_SUCCESS);
				result.setUserId(getUserGuidFromUserApiToken(token));
				result.setBalanceCents(balanceController.balance(getUserGuidFromUserApiToken(token), apiAuthentication).getResponse().getBalanceCents());
				result.setCurrency(apiAuthentication.getBrandConfiguration().getCurrency());
			} else {
				log.error("Invalid token auth request: " + token);
				result = new AuthenticationResponse(AuthenticationResponse.RESPONSE_CODE_INVALID_TOKEN, "");
			}
			
			log.debug("AuthenticateController " + result);
	        return new AuthenticationRequestResponse(request, result);
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new AuthenticationRequestResponse(request, new AuthenticationResponse(AuthenticationResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
		}
		
    }
	
	private String getUserGuidFromUserApiToken(String userApiToken) {
		return userApiToken.substring(userApiToken.lastIndexOf("|")+1);
	}
	
	private String getApiTokenFromUserApiToken(String userApiToken) {
		return userApiToken.substring(0, userApiToken.lastIndexOf("|"));
	}
	
}
