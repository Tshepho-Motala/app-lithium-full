package lithium.service.cashier.mock.quickbit.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.quickbit.Configuration;
import lithium.service.cashier.mock.quickbit.data.objects.BuyRequest;
import lithium.service.cashier.processor.quickbit.data.CallbackResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/process-direct-api-buy-request")
@Slf4j
public class BuyController {
	@Autowired LithiumConfigurationProperties properties;
	@Autowired Configuration config;
	
	@PostMapping("/")
	public ModelAndView buyRequest(
		@RequestParam("redirecturl") String redirectUrl,
		@RequestParam("requestreference") String requestReference,
		@RequestParam("fiatamount") String fiatAmount,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		log.info("Received buy request (redirectUrl: " + redirectUrl + ", requestReference: " + requestReference
			+ ", fiatAmount: " + fiatAmount);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		
		CallbackResponse callbackResponse = CallbackResponse.builder()
		.statusCode("309")
		.statusMessage("User has been redirected to the 'enter card information' page.")
		.transactionId(requestReference)
		.payStatus("pending")
		.orderStatus("pending")
		.requestReference(requestReference)
		.build();
		
		callbackResponse.setChecksum(callbackResponse.calculateHash(config.getSecretKey()));
		
		restTemplate.postForObject(properties.getGatewayPublicUrl() +
				"/service-cashier-processor-quickbit/callback/030791F2FA35B94AA3954288DEDEE7C2/", callbackResponse, String.class);
		
		return new ModelAndView("buy", "br", new BuyRequest(redirectUrl, requestReference, fiatAmount));
	}
	
	@PostMapping("/buyPostRequest")
	public void buyPostRequest(
		@RequestParam("redirectUrl") String redirectUrl,
		@RequestParam("requestReference") String requestReference,
		@RequestParam("amount") String amount,
		@RequestParam("cardNo") String cardNo,
		@RequestParam("month") String month,
		@RequestParam("year") String year,
		@RequestParam("cvc") String cvc,
		HttpServletResponse response
	) throws IOException {
		log.info("buyPostRequest");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		
		CallbackResponse cbResponse = CallbackResponse.builder()
		.statusCode("309")
		.statusMessage("User has been redirected to the 'enter card information' page.")
		.transactionId(requestReference)
		.payStatus("completed")
		.orderStatus("completed")
		.requestReference(requestReference)
		.build();
		cbResponse.setChecksum(cbResponse.calculateHash(config.getSecretKey()));
		
		restTemplate.postForObject(properties.getGatewayPublicUrl() + 
				"/service-cashier-processor-quickbit/callback/030791F2FA35B94AA3954288DEDEE7C2/", cbResponse, String.class);
		
		CallbackResponse callbackResponse = CallbackResponse.builder()
		.statusCode("200")
		.statusMessage("Transaction Completed Successfully.")
		.requestReference(requestReference)
		.build();
		callbackResponse.setChecksum(callbackResponse.calculateHash(config.getSecretKey()));
		
		restTemplate.postForObject(properties.getGatewayPublicUrl() + 
				"/service-cashier-processor-quickbit/callback/030791F2FA35B94AA3954288DEDEE7C2/", callbackResponse, String.class);
	}
}
