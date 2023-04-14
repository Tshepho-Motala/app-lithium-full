package lithium.service.cashier.processor.cc.qwipi;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CallbackController {
	
	@Autowired CashierDoCallbackService service;
	
	@RequestMapping("/callback/c912c842-427g-4a34-bc5b-02202caef028") 
	public String callback(PaymentResponseS2S request, HttpServletResponse webResponse) throws Exception {
		log.info("Received callback from processor: " + request.toString());
		
		Long transactionId = Long.parseLong(request.getBillNo());
		
		Response<DoProcessorRequest> processorRequest = service.doCallbackGetTransaction(transactionId, "qwipi");
		if (!processorRequest.isSuccessful()) {
			throw new Exception(processorRequest.getMessage());
		}
		
		if (!request.calculateMd5Info(processorRequest.getData().getProperty("md5Key")).equals(request.getMd5Info())) {
			throw new Exception("Invalid MD5 key");
		}
		
		DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
		String message = request.getRemark();
		
		switch (ResultCode.fromCode(Integer.parseInt(request.getResultCode()))) {
			case SUCCESS: status = DoProcessorResponseStatus.SUCCESS; break;
			case PROCESSING: status = DoProcessorResponseStatus.NOOP; break;
			case FAILED: status = DoProcessorResponseStatus.DECLINED; break;
			default: throw new Exception("Invalid result from QWIPI " + request.toString());
		}
		
		DoProcessorResponse processorResponse = DoProcessorResponse.builder()
				.transactionId(transactionId)
				.processorReference(request.getOrderId())
				.rawResponseLog(JsonStringify.objectToString(request))
				.status(status)
				.amountCentsReceived(new BigDecimal(request.getAmount()).multiply(new BigDecimal(100)).intValue())
				.message(message)
				.build();

		log.info("Sending request to service-cashier: " + processorResponse.toString());

		Response<String> response = service.doCallback(processorResponse);
		if (!response.isSuccessful()) webResponse.setStatus(response.getStatus().id());
		log.info("Received response from service-cashier: " + response.toString());
		
		return response.getMessage();
	}
}
