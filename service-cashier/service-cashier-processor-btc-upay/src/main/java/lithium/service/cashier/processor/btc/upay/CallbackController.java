package lithium.service.cashier.processor.btc.upay;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.upay.btc.data.IPNRequest;
import lithium.util.JsonStringify;

@RestController
@Slf4j
public class CallbackController {

	@Autowired CashierDoCallbackService service;

	@PostMapping("/callback/e328bf4a-280c-4414-bca3-01602db2e056")
	public String ipn(IPNRequest request, HttpServletResponse webResponse) throws Exception {

		log.info("Callback request " + request.toString());
		
		Long transactionId = Long.parseLong(request.getOrder_id());
		
		Response<DoProcessorRequest> processorRequest = service.doCallbackGetTransaction(transactionId, "upaybtc");
		if (!processorRequest.isSuccessful()) {
			throw new Exception(processorRequest.getMessage());
		}
		
		log.info("Retrieved transaction from cashier: " + processorRequest.toString());
		
		if (!request.calculateSign(processorRequest.getData().getProperty("apisecret")).equals(request.getSign())) {
			throw new Exception("Invalid signature");
		}

		DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
		if (request.getStatus().equals("success")) {
			status = DoProcessorResponseStatus.SUCCESS;
		} else {
			status = DoProcessorResponseStatus.NOOP;
		}

		DoProcessorResponse processorResponse = DoProcessorResponse.builder()
				.transactionId(transactionId)
				.processorReference(request.getTransaction_id())
				.rawRequestLog(JsonStringify.objectToString(request))
				.status(status)
				.build();

		if (request.getCurrency().equals("BTC")) {
			
			Double bitcoins = Double.parseDouble(processorRequest.getData().stageOutputData(1, "bitcoins"));
			Double settledAmount = Double.parseDouble(request.getSettled_amount());
			processorResponse.setOutputData(2, "bitcoinsreceived", settledAmount.toString());

			if (bitcoins.floatValue() != settledAmount.floatValue()) {
				processorResponse.setStatus(DoProcessorResponseStatus.DECLINED);
				processorResponse.setMessage("Requested amount of BTC and received amount differ: " + bitcoins + " vs " + settledAmount);
			}
			
		}
		
		//This 
		if (request.getCurrency().equals(processorRequest.getData().getUser().getCurrency())) {
			processorResponse.setAmountCentsReceived(new BigDecimal(request.getSettled_amount()).multiply(new BigDecimal(100)).intValue());
		}

		log.info("Sending request to service-cashier: " + processorResponse.toString());

		Response<String> response = service.doCallback(processorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		return response.getMessage();
	}
	
}
