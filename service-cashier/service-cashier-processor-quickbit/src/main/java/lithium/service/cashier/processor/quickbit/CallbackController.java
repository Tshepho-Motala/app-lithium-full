package lithium.service.cashier.processor.quickbit;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.quickbit.data.CallbackResponse;
import lithium.service.cashier.processor.quickbit.data.enums.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;
	
	@RequestMapping("/030791F2FA35B94AA3954288DEDEE7C2/")
	private String callback(
		@RequestBody CallbackResponse callbackResponse,
		HttpServletRequest webRequest,
		HttpServletResponse webResponse
	) throws Exception {
		log.info("Received callback from Quickbit " + callbackResponse.toString());
		
		Long internalTranId = Long.parseLong(callbackResponse.getRequestReference());
		
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "quickbit");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		String calculatedHash = callbackResponse.calculateHash(doProcessorRequest.getProperty("secretkey"));
		if (!calculatedHash.contentEquals(callbackResponse.getChecksum())) {
			throw new Exception("Invalid checksum");
		}
			
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();
		if (callbackResponse.getTransactionId() != null && !callbackResponse.getTransactionId().isEmpty())
			output.put("processorReference", callbackResponse.getTransactionId());
		outputData.put(1, output);
		
		DoProcessorResponseStatus status = null;
		
		StatusCode sc = StatusCode.find(Integer.parseInt(callbackResponse.getStatusCode()));
		
		switch (sc) {
			case I201:
			case I203:
			case I307:
			case I308:
			case I309:
				if (callbackResponse.getPayStatus().contentEquals("completed") &&
					callbackResponse.getOrderStatus().contentEquals("completed")) {
						status = DoProcessorResponseStatus.NOOP;
						break;
				} else {
					return "OK";
				}
			case I200:
				status = DoProcessorResponseStatus.SUCCESS;
				break;
			default:
				log.info("Declining transaction (" + internalTranId + ") due to (" + sc.getDescription() + ")");
				status = DoProcessorResponseStatus.DECLINED;
				break;
		}
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
			.transactionId(doProcessorRequest.getTransactionId())
			.processorReference(callbackResponse.getTransactionId())
			.status(status)
			.outputData(outputData)
			.message(callbackResponse.getStatusMessage())
			.rawResponseLog(callbackResponse.toString())
			.build();
			
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		webResponse.sendRedirect(config.getGatewayPublicUrl() + "/service-cashier/frontend/loadingrefresh");
		return "OK";
	}
}