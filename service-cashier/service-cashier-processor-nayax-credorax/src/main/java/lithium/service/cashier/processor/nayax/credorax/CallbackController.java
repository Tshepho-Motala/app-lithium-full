package lithium.service.cashier.processor.nayax.credorax;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@PostMapping("/callback/LKJOIJWQELKNSOIHSDASDLKJWE") 
	public String callback(
		@RequestParam("t") Long transactionId,
		@RequestParam("d") String data
	) throws Exception {
		log.info("Received callback from processor: " + transactionId + " " + data);
		

		ObjectMapper json = new ObjectMapper();
		// json.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		
		NayaxTransaction transaction = (NayaxTransaction) json.readValue(data, NayaxTransaction.class);	

		Response<DoProcessorRequest> processorRequestResponse = service.doCallbackGetTransaction(transactionId, "nayax-credorax");
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		Map<Integer, Map<String, String>> outputData = new HashMap<>();
		Map<String, String> output = new HashMap<>();

		if (transaction.getInfo() == null) {
			throw new Exception("Transaction info is null " + transactionId + " " + data);
		}
		if (transaction.getInfo().getTransactionId() == null) {
			throw new Exception("Transaction id is null " + transactionId + " " + data);
		}
		
		output.put("processorReference", transaction.getInfo().getTransactionId().toString());
		output.put("ccLast4Digits", transaction.getInfo().getCcLast4Digits());
		outputData.put(2, output);
		
		DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
			.transactionId(doProcessorRequest.getTransactionId())
			.processorReference(transaction.getInfo().getTransactionId().toString())
			.status((transaction.success) ? DoProcessorResponseStatus.SUCCESS: DoProcessorResponseStatus.DECLINED)
			.outputData(outputData)
			.build();
		
		log.info("Sending request to service-cashier: " + doProcessorRequest.toString());

		Response<String> response = service.doCallback(doProcessorResponse);
		if (!response.isSuccessful()) {
			log.error("Response from cashier was unhealthy: " + response.getMessage());
			throw new Exception("NOT OK");
		}
		log.info("Received response from service-cashier: " + response.toString());
		
		return "OK";
	}
}
