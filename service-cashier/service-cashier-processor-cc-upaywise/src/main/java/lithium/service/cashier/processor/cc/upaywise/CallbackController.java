package lithium.service.cashier.processor.cc.upaywise;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import lithium.service.cashier.processor.cc.upaywise.data.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;
	
	@RequestMapping("/callback/e52ba9fda1d019cff025b95dc7d75172/")
	public String callback(
		@RequestParam("results") String result,
		@RequestParam("transid") String tranId,
		@RequestParam("ecis") String eci,
		@RequestParam("trackids") String trackId,
		@RequestParam("responsecodes") String responseCode,
		@RequestParam("auths") String auth,
		@RequestParam("rrns") String rrn,
		@RequestParam("udfs5") String udf5,
		@RequestParam("amounts") String amount,
		@RequestParam("email") String email,
		HttpServletRequest webRequest,
		HttpServletResponse webResponse
	) throws Exception {
		log.info("Received callback from processor: " +responseCode+ ", "  +result+", "+tranId+", "+eci+", "+trackId+", "+amount+", "+email);
		
		Long internalTranId = null;
		try {
			internalTranId = Long.parseLong(trackId);
		} catch (NumberFormatException e) {
			log.error("Could not convert trackId to Long " + e.getMessage(), e);
		}
		
		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "upaywise");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();
		output.put("processorReference", tranId);
		output.put("eci", eci);
		output.put("auth", auth);
		output.put("rrn", rrn);
		outputData.put(2, output);
		
		DoProcessorResponseStatus status = null;
		
		ResponseCode rc = ResponseCode.find(responseCode);
		switch (rc) {
			case RC000:
				status = DoProcessorResponseStatus.SUCCESS;
				break;
			case RC001:
			case RC509:
				status = DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
				break;
			default:
				status = DoProcessorResponseStatus.DECLINED;
				break;
		}
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
			.transactionId(doProcessorRequest.getTransactionId())
			.processorReference(tranId)
			.status(status)
			.outputData(outputData)
			.message(udf5)
			.rawResponseLog(webRequest.getQueryString())
			.build();
		
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());
		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		webResponse.sendRedirect(config.getGatewayPublicUrl() + "/service-cashier/frontend/loadingrefresh");
		return "OK";
	}
}