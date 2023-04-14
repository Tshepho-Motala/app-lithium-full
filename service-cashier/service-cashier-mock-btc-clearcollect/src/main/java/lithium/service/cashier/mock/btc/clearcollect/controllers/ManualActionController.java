package lithium.service.cashier.mock.btc.clearcollect.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.processor.btc.clearcollect.data.CallbackRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestWithoutSignature;

@RestController
public class ManualActionController {
	
	@RequestMapping("/manual/callback") 
	private String callback(ManualCallbackRequest request, HttpServletResponse response) throws Exception {
		
		CallbackRequest callbackRequest = CallbackRequest.builder()
				.address("asdjalksjdalksjdalkjslaksjd")
				.clientTracking(request.getReferenceNr())
				.confirmations(request.getConfirmations())
				.creditAmountUsdInt(request.getAmountUsdCents())
				.requestAmountUsdInt(request.getAmountUsdCents())
				.status(request.getStatus())
				.build();
		
		RequestWithoutSignature<CallbackRequest> callbackRequestWrapper = new RequestWithoutSignature<>();
		callbackRequestWrapper.setData(callbackRequest);
		
		RestTemplate rest = new RestTemplate();
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		String result = rest.postForObject(request.getUrl(), callbackRequestWrapper.createHttpEntity(), String.class);
		return result;
	}


}
