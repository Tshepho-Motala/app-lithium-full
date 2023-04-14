package lithium.service.cashier.mock.giap.controllers;

import org.springframework.stereotype.Controller;

//@Slf4j
@Controller
public class AuthorizeController {
	
//	@Autowired Configuration config;
//	
//	@RequestMapping("/TestTPInterface")
//	public @ResponseBody String authorize(AuthorizeRequest request) {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		AuthorizeResponse response = authorizeInternal(request);
//		JAXB.marshal(response, baos);
//		return baos.toString();
//	}
//	
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
