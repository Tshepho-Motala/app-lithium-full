package lithium.service.cashier.mock.neteller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lithium.service.cashier.mock.neteller.service.MockService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value="/oauth2/v1")
public class AuthorizeController {
	@Autowired MockService mockService;
	
//	@RequestMapping("/token")
//	public @ResponseBody TokenResponse authorize(
//		@RequestParam(name = "grant_type", required = true, defaultValue = "client_credentials") String grantType,
//		WebRequest request
//	) {
//		log.info("Request: "+request+" ,grantType: "+grantType);
//		request.getHeaderNames().forEachRemaining(h -> {
//			log.info("Header: "+h+" :: "+request.getHeader(h));
//		});
//
//		TokenResponse tr = TokenResponse.builder()
//		.accessToken(mockService.createJWT())
//		.tokenType("Bearer")
//		.expiresIn(300)
//		.build();
//		return tr;
//	}
	
//	private AuthorizeResponse authorizeInternal(AuthorizeRequest request) {
	//	log.info("Payment request: " + request.toString() + " expected md5 " + request.calculateSignInfo(config.getKey()));
		
//		AuthorizeResponse response = AuthorizeResponse.builder()
//				//.merNo(request.getMerNo())
//				//.gatewayNo(request.getGatewayNo())
//				//.orderNo(request.getOrderNo())
//				//.tradeNo("TNO" + request.getOrderNo())
//				//.orderAmount(request.getOrderAmount())
//				.orderStatus(OrderStatus.SUCCESS.getCode().toString())
//				//.orderCurrency(request.getOrderCurrency())
//				.build();
//		
//		try {
//			request.validate(config.getKey());
//		} catch (ValidationException ve) {
//			response.setOrderStatus(OrderStatus.FAILED.getCode().toString());
//			response.setResponseCode(ve.getErrorCode().name());
//			response.setOrderInfo(ve.getErrorCode().description());
//			return response.saveSignInfo(config.getKey());
//		}
//		
//		try {
//			//BigDecimal timeout = new BigDecimal(request.getOrderAmount());
//			BigDecimal timeout = new BigDecimal(0);
//			timeout = timeout.multiply(new BigDecimal(1000));
//			if ((timeout.intValue() >= 30000) && (timeout.intValue() < 100000)) {
//				log.warn("Sleeping for : "+timeout.intValue()+"ms");
//				Thread.sleep(timeout.intValue());
//			}
//		} catch (Exception e) {
//			log.error("setting timeout", e);
//		}
//		
//		return response.saveSignInfo(config.getKey());
//		return null;
//	}
//	
}
