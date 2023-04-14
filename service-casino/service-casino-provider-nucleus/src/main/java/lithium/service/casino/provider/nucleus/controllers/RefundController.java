package lithium.service.casino.provider.nucleus.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.provider.nucleus.config.APIAuthentication;
import lithium.service.casino.provider.nucleus.data.request.RefundRequest;
import lithium.service.casino.provider.nucleus.data.requestresponse.RefundRequestResponse;
import lithium.service.casino.provider.nucleus.data.response.RefundResponse;

@RestController
public class RefundController extends BaseController {
    
	private static final Log log = LogFactory.getLog(RefundController.class);
	
//	@Autowired
//	RefundHandler handler;
	
	@RequestMapping(value = "/refund", produces = "application/xml")
    RefundRequestResponse refund(@RequestParam String userId, 
    		@RequestParam Long casinoTransactionId,
    		@RequestParam String hash,
    		APIAuthentication apiAuthentication
    		) {

		RefundRequest request = new RefundRequest(userId, casinoTransactionId);
		request.setCasinoTransactionId(casinoTransactionId);
		request.setHash(hash);
		
		log.info("RefundController " + request);

		String calculatedHash = request.calculateHash(apiAuthentication.getBrandConfiguration().getHashPassword());
		if (!disableHash) {
			if (!calculatedHash.equals(hash)) {
				log.warn("Request hash mismatch: calculatedHash: " + calculatedHash + " request " + request);
				return new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_INVALID_HASH, "Invalid hash"));
			}
		} else {
			log.warn("Hash function disabled. | remoteHash: " + hash + " | localHash: " + calculatedHash);
		}

		try {
			RollbackTranRequest rollbackRequest = new RollbackTranRequest();
			rollbackRequest.setDomainName(getDomainNameFromPlayerGuid(userId));
			rollbackRequest.setProviderGuid(getDomainNameFromPlayerGuid(userId)+"/"+apiAuthentication.getProviderUrl());
			rollbackRequest.setUserGuid(userId);
			rollbackRequest.setHash(hash);
			rollbackRequest.setTransactionId(casinoTransactionId+"");
			RollbackTranResponse response = nucleusService.getCasinoService().rollbackTran(rollbackRequest);
			
			if(response.getBalanceCents() == null) {
				return new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_UNKNOWN_USERID, "Unknown user id or accounting service down"));
			}
			
			if(Long.parseLong(response.getTranId()) <= 0) {
				return new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_UNKNOWN_TRANSACTIONID, "Unknown transaction id or accounting service down"));
			}
			log.info("RefundController " + response);
			
			RefundResponse result = new RefundResponse(response.getTranId());
			result.setResult(RefundResponse.RESPONSE_SUCCESS);
	        return new RefundRequestResponse(request, result);
		} catch (Exception e) {
			log.error("Unhandled exception during handler request: " + e.getMessage(), e);
			return new RefundRequestResponse(request, new RefundResponse(RefundResponse.RESPONSE_CODE_INTERNAL_ERROR, e.getMessage()));
		}
    }
	
}
