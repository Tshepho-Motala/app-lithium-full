package lithium.service.cashier.mock.cc.qwipi.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.OperationType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lithium.util.ObjectToHttpEntity;

@RestController
public class ManualActionController {
	
	@RequestMapping("/manual/callback") 
	private String callback(ManualCallbackRequest request, HttpServletResponse response) throws Exception {
		
		PaymentResponseS2S qwipiResponse = PaymentResponseS2S.builder()
				.amount(new BigDecimal(request.getAmountUsdCents()).divide(new BigDecimal(100)).toString())
				.billNo(request.getReferenceNr())
				.orderId(new Long(new Date().getTime()).toString())
				.currency("USD")
				.dateTime(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()))
				.merNo(request.getMerno())
				.errorCode(ErrorCode.E0000000.name().substring(1))
				.operation(OperationType.PAYMENT.getCodeString())
				.remark(ErrorCode.E0000000.description())
				.billingDescriptor("NEWCO")
				.resultCode(ResultCode.SUCCESS.getCode().toString())
				.build();
		
		qwipiResponse.saveMd5Info(request.getKey());
		
		RestTemplate rest = new RestTemplate();
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		
		String result = rest.postForObject(request.getUrl(), ObjectToHttpEntity.forPostForm(qwipiResponse), String.class);
		return result;
	}


}
