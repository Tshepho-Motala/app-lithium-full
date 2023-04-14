package lithium.service.cashier.mock.upay.controllers;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.processor.upay.btc.data.IPNRequest;
import lithium.util.ObjectToHttpEntity;

@RestController
public class ManualActionController {
	
	@RequestMapping("/manual/callback") 
	private String callback(ManualCallbackRequest request, HttpServletResponse response) throws Exception {
		
		IPNRequest ipnRequest = IPNRequest.builder()
				.settled_amount(new BigDecimal(request.getAmountUsdCents()).divide(new BigDecimal(100)).toString())
				.order_id(request.getReferenceNr())
				.transaction_id(new Long(new Date().getTime()).toString())
				.currency("USD")
				.build();
		ipnRequest.sign(request.getKey());
		
		RestTemplate rest = new RestTemplate();
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		
		String result = rest.postForObject(request.getUrl(), ObjectToHttpEntity.forPostForm(ipnRequest), String.class);
		return result;
	}


}
