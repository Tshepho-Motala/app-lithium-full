package lithium.service.sms.provider.clickatell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.sms.client.internal.DoCallbackClient;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.provider.clickatell.data.ClickatellCallback;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CallbackController {
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	@PostMapping("/callback/5862eaa03a91fa1aadb1e5e8ec1ffa71/")
	public @ResponseBody String callback(@RequestBody ClickatellCallback parameters) throws Exception {
		log.info("Received callback from provider: " + parameters.toString());
		
//		MessageStatusCode msCode = MessageStatusCode.find(parameters.getStatusCode());
//		
//		log.info("msCode: " + msCode);
//		
//		switch (msCode) {
//			case I001:
//			case I005:
//			case I006:
//			case I007:
//			case I009:
//			case I010:
//			case I012:
//			case I013:
//			case I014:
//				status = DoProviderResponseStatus.FAILED;
//				break;
//			case I002:
//			case I003:
//			case I011:
//				status = DoProviderResponseStatus.PENDING;
//				break;
//			case I004:
//				status = DoProviderResponseStatus.SUCCESS;
//				break;
//			default: status = DoProviderResponseStatus.FAILED;
//		}
		
		/**
		 * Clickatell is not sending the correct status codes as described on
		 * https://archive.clickatell.com/developers/api-docs/message-status-codes/.
		 * I am instead going to do a check on the status description. I have encountered
		 * DELIVERED_TO_GATEWAY and RECEIVED_BY_RECIPIENT, so followed this pattern for MESSAGE_QUEUED.
		 * The rest of the statuses will be a fail.
		 */
		
		DoProviderResponseStatus status = null;
		
		switch (parameters.getStatusDescription()) {
			case "MESSAGE_QUEUED":
			case "DELIVERED_TO_GATEWAY":
				status = DoProviderResponseStatus.PENDING;
				break;
			case "RECEIVED_BY_RECIPIENT":
				status = DoProviderResponseStatus.SUCCESS;
				break;
			default: status = DoProviderResponseStatus.FAILED;
		}
		
		try {
			DoProviderResponse doProviderResponse = DoProviderResponse.builder()
				.smsId(Long.parseLong(parameters.getClientMessageId()))
				.status(status)
				.message(parameters.getStatusDescription())
				.build();
			
			DoCallbackClient client = serviceFactory.target(DoCallbackClient.class, "service-sms", true);
			client.doProviderCallback(doProviderResponse);
		} catch (Exception ex) {
			log.error("There was a problem processing the callback from the provider " + parameters, ex);
		}
		return "OK";
	}
}