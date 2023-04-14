package lithium.service.cashier.processor.netaxept;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CallbackController {
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	private CashierDoCallbackService service;
//	@Autowired
//	private RestTemplate restTemplate;
	
//	@RequestMapping("/callback/RUNhcmRPbkNhbGxiYWNrQ29udHJvbGxlcg") 
//	public String callback(
//		@RequestParam("t") Long transactionId,
//		@RequestParam(value="id", required=false) String id,
//		@RequestParam(value="resourcePath", required=false) String resourcePath,
//		@RequestParam(value="target", required=false) String target,
//		@RequestParam(value="method", required=false) String method,
//		HttpServletResponse webResponse
//	) throws Exception {
//		log.info("Received callback from processor: "+transactionId+","+id+","+resourcePath+","+target+","+method+"");
//		
//		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(transactionId, "netaxept");
//		if (!processorRequestResponse.isSuccessful()) {
//			throw new Exception(processorRequestResponse.getMessage());
//		}
//		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
//		
//		Map<Integer, Map<String, String>> outputData = new HashMap<>();
//		Map<String, String> output = new HashMap<>();
//		output.put("processorReference", id);
//		outputData.put(2, output);
//		
//		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
//			.transactionId(doProcessorRequest.getTransactionId())
//			.processorReference(id)
//			.status(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS)
//			.outputData(outputData)
//			.build();
//		
//		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
//
//		Response<String> response = service.doCallback(doProcessorResponse);
//		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
//		log.info("Received response from service-cashier: " + response.toString());
//		
//		webResponse.sendRedirect(config.getGatewayPublicUrl() + "/service-cashier/frontend/loadingrefresh");
//		return "OK";
//	}
}
