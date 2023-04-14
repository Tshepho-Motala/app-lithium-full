package lithium.service.cashier.processor.paysafegateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.paysafegateway.data.TransactionToken;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
	@Autowired LithiumConfigurationProperties config;
	@Autowired CashierDoCallbackService service;

	@PostMapping("/0f6348fc25c2ad469f99f402d661a461/")
	public String handleCallback(
		WebRequest webRequest,
		HttpServletResponse webResponse,
		@RequestBody TransactionToken tranToken
	) {
		try {
			log.debug("Received callback from service-cashier-frontend-paysafegateway [tranToken="+tranToken+"]");
			Long internalTranId = Long.parseLong(tranToken.getTransactionId());
			Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(internalTranId, "paysafe-gateway");
			if (!processorRequestResponse.isSuccessful()) {
				throw new Exception(processorRequestResponse.getMessage());
			}
			DoProcessorResponseStatus status = DoProcessorResponseStatus.DECLINED;
			Map<Integer, Map<String, String>> outputData = new HashMap<>();
			Map<String, String> output = new HashMap<>();
			if (tranToken.getToken() != null) {
				output.put("paymentToken", String.valueOf(tranToken.getToken()));
				output.put("userAgent",tranToken.getUserAgent());
				output.put("userIp",tranToken.getUserIp());
				if (tranToken.getIframeError() != null) {
					ObjectMapper objectMapper = new ObjectMapper();
					output.put("iframeError",objectMapper.writeValueAsString(tranToken.getIframeError()));
				}
				status = DoProcessorResponseStatus.NEXTSTAGE;
			}
			outputData.put(1, output);
			DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
			DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
			.transactionId(internalTranId)
			.status(status)
			.outputData(outputData)
			.rawResponseLog(JsonStringify.objectToString(tranToken))
			.build();
			log.debug("Sending request to service-cashier: " + doProcessorRequest.toString());
			Response<String> response = service.doCallback(doProcessorResponse);
			if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
			log.debug("Received response from service-cashier: " + response.toString());
			return "OK";
		} catch (Exception ex) {
			log.error("Failed  to process payment request to : {}", ex);
			return "OK";
		}

	}
}
