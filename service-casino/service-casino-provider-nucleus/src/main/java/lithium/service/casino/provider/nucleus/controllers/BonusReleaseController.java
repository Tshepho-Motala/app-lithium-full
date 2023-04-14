package lithium.service.casino.provider.nucleus.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.nucleus.config.APIAuthentication;
import lithium.service.casino.provider.nucleus.data.request.BonusReleaseRequest;
import lithium.service.casino.provider.nucleus.data.requestresponse.BonusReleaseRequestResponse;
import lithium.service.casino.provider.nucleus.data.response.BonusReleaseResponse;

@RestController
public class BonusReleaseController extends BaseController {
    
//	private static final Log log = LogFactory.getLog(BonusReleaseController.class);
//	
////	@Autowired
////	BonusReleaseHandler handler;
//	
//	@RequestMapping(value = "/bonusrelease", produces = "application/xml")
//    BonusReleaseRequestResponse bonusRelease(@RequestParam String userId, 
//    		@RequestParam Integer bonusId,
//    		@RequestParam Long amount,
//    		@RequestParam String hash,
//    		APIAuthentication apiAuthentication
//    		) {
//
//		BonusReleaseRequest request = new BonusReleaseRequest(
//				userId, bonusId, amount);
//		request.setHash(hash);
//		
//		log.info("BonusReleaseController " + request);
//
//		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword()); 
//		
//		if (!calculatedHash.equals(hash)) {
//			log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
//			return new BonusReleaseRequestResponse(request, new BonusReleaseResponse(BonusReleaseResponse.RESPONSE_CODE_INVALID_HASH, "Invalid hash"));
//		}
//
//		try {
//			BonusReleaseResponse result = mapper.map(nucleusService.getCasinoService()
//					.handleBonusReleaseRequest(mapper.map(request, lithium.service.casino.client.objects.request.BonusReleaseRequest.class)),
//					BonusReleaseResponse.class);
//			log.info("BonusReleaseController " + result);
//	        return new BonusReleaseRequestResponse(request, result);
//		} catch (Exception e) {
//			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
//			return new BonusReleaseRequestResponse(request, new BonusReleaseResponse(BonusReleaseResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
//		}
//    }
	
}
