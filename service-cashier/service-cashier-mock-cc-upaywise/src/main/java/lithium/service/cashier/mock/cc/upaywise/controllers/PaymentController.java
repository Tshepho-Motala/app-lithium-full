package lithium.service.cashier.mock.cc.upaywise.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.processor.cc.upaywise.data.UPayWiseRequest;
import lithium.service.cashier.processor.cc.upaywise.data.UPayWiseResponse;
import lithium.service.cashier.processor.cc.upaywise.data.enums.ResponseCode;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.exchange.client.ExchangeClient;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/paymentgateway/payments/performXmlTransaction")
@Slf4j
public class PaymentController {
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private LithiumConfigurationProperties config;
	
	private double getExchangeRate(String fromCurrencyCode, String toCurrencyCode) throws LithiumServiceClientFactoryException {
		ExchangeClient client = services.target(ExchangeClient.class, "service-exchange", true);
		return client.rate(fromCurrencyCode, toCurrencyCode).getData();
	}
	
	@PostMapping
	public UPayWiseResponse performXmlTransaction(@RequestBody UPayWiseRequest request) throws LithiumServiceClientFactoryException {
		log.info("Request:: " + request);
		BigDecimal amount = new BigDecimal(request.getAmount());
		
		String currencyCode = request.getCurrencyCode();
		BigDecimal exchangeRate = new BigDecimal(getExchangeRate("USD", currencyCode));
		
		BigDecimal amountUsd = amount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
		
		if (amountUsd.movePointRight(2).intValue() == 5000) {
			UPayWiseResponse response = UPayWiseResponse.builder()
				.result("Fail")
				.responseCode(ResponseCode.RC506.getCode())
				.authCode("Test123")
				.eci("Test456")
				.tranId("123456789")
				.trackId(request.getMerchantTrackId())
				.terminalId(request.getTerminalId())
				.rrn("Test789")
				.build();
			return response;
		} else if (amountUsd.movePointRight(2).intValue() < 3000) {
			UPayWiseResponse response = UPayWiseResponse.builder()
				.result("Successful")
				.responseCode(ResponseCode.RC000.getCode())
				.authCode("Test123")
				.eci("Test456")
				.tranId("123456789")
				.trackId(request.getMerchantTrackId())
				.terminalId(request.getTerminalId())
				.rrn("Test789")
				.build();
			return response;
		} else if (amountUsd.movePointRight(2).intValue() < 4000) {
			UPayWiseResponse response = UPayWiseResponse.builder()
				.result("Successful")
				.responseCode(ResponseCode.RC509.getCode())
				.authCode("Test1234")
				.eci("Test456")
				.tranId("123456789")
				.trackId(request.getMerchantTrackId())
				.terminalId(request.getTerminalId())
				.rrn("Test789")
				.build();
			return response;
		} else {
			UPayWiseResponse response = UPayWiseResponse.builder()
				.targetUrl(config.getGatewayPublicUrl()+"/service-cashier-mock-cc-upaywise/processCopyAndPayThreed?t="+request.getMerchantTrackId()+"&payId=")
				.payId("1243")
				.build();
			return response;
		}
	}
}