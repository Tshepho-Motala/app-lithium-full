package lithium.service.cashier.processor.btc.clearcollect;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.btc.clearcollect.data.CallbackRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestWithoutSignature;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CallbackController {
	@Autowired CashierDoCallbackService service;
	
	@RequestMapping("/callback/c928c842-437a-4434-bb5b-01602ceee056") 
	public String callback(WebRequest webRequest, HttpServletResponse webResponse) throws Exception {
		RequestWithoutSignature<CallbackRequest> request = new RequestWithoutSignature<>();
		request.payloadFromHeaders(webRequest, CallbackRequest.class);
		
		Long transactionId = Long.parseLong(request.getData().getClientTracking());
		Response<DoProcessorRequest> doProcessorRequest = service.doCallbackGetTransaction(transactionId, "clearcollect");
		int confirmations = Integer.parseInt(doProcessorRequest.getData().getProperty("btcconfirmations"));
		
		log.info("Received callback from processor: " + request.toString());
		
		DoProcessorResponse processorResponse = DoProcessorResponse.builder()
			.transactionId(Long.parseLong(request.getData().getClientTracking()))
			.processorReference(request.getData().getId())
			.rawRequestLog(JsonStringify.objectToString(request))
			.amountCentsReceived(Integer.parseInt(request.getData().getCreditAmountUsdInt()))
			.build();
		
		switch (request.getData().getStatus()) {
			case "C":
				if (Integer.parseInt(request.getData().getConfirmations()) >= confirmations) {
					processorResponse.setMessage("Success");
					processorResponse.setStatus(DoProcessorResponseStatus.SUCCESS);
				} else {
					processorResponse.setMessage("Not enough confirmations");
					processorResponse.setStatus(DoProcessorResponseStatus.NOOP);
				}
				break;
			case "O":
				processorResponse.setMessage("Out of bounds");
				processorResponse.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
			case "P":
				processorResponse.setMessage("Pending");
				processorResponse.setStatus(DoProcessorResponseStatus.NOOP);
				break;
			case "E":
				processorResponse.setMessage("Double spent");
				processorResponse.setStatus(DoProcessorResponseStatus.FATALERROR);
				break;
			default:
				processorResponse.setStatus(DoProcessorResponseStatus.NOOP);
				processorResponse.setMessage("Success");
				break;
		}
		
		log.info("Sending request to service-cashier: " + processorResponse.toString());
		
		Response<String> response = service.doCallback(processorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		return response.getMessage();
	}
}
