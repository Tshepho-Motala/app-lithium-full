package lithium.service.cashier.mock.cc.trustspay.controllers;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lithium.service.cashier.mock.cc.trustspay.Configuration;
import lithium.service.cashier.processor.cc.trustspay.data.AuthorizeRequest;
import lithium.service.cashier.processor.cc.trustspay.data.AuthorizeResponse;
import lithium.service.cashier.processor.cc.trustspay.data.ValidationException;
import lithium.service.cashier.processor.cc.trustspay.data.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuthorizeController {
	
	@Autowired Configuration config;
	
	@RequestMapping("/TestTPInterface")
	public @ResponseBody String authorize(AuthorizeRequest request) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		AuthorizeResponse response = authorizeInternal(request);
		JAXB.marshal(response, baos);
		return baos.toString();
	}
	
	private AuthorizeResponse authorizeInternal(AuthorizeRequest request) {
		log.info("Payment request: " + request.toString() + " expected md5 " + request.calculateSignInfo(config.getKey()));
		
		AuthorizeResponse response = AuthorizeResponse.builder()
				.merNo(request.getMerNo())
				.gatewayNo(request.getGatewayNo())
				.orderNo(request.getOrderNo())
				.tradeNo("TNO" + request.getOrderNo())
				.orderAmount(request.getOrderAmount())
				.orderStatus(OrderStatus.SUCCESS.getCode().toString())
				.orderCurrency(request.getOrderCurrency())
				.build();
		
		try {
			request.validate(config.getKey());
		} catch (ValidationException ve) {
			response.setOrderStatus(OrderStatus.FAILED.getCode().toString());
			response.setResponseCode(ve.getErrorCode().name());
			response.setOrderInfo(ve.getErrorCode().description());
			return response.saveSignInfo(config.getKey());
		}
		
		try {
			BigDecimal timeout = new BigDecimal(request.getOrderAmount());
			timeout = timeout.multiply(new BigDecimal(1000));
			if ((timeout.intValue() >= 30000) && (timeout.intValue() < 100000)) {
				log.warn("Sleeping for : "+timeout.intValue()+"ms");
				Thread.sleep(timeout.intValue());
			}
		} catch (Exception e) {
			log.error("setting timeout", e);
		}
		
		return response.saveSignInfo(config.getKey());
	}
	
}
