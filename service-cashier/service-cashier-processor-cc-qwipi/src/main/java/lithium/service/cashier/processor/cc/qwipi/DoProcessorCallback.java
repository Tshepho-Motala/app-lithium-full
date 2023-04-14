package lithium.service.cashier.processor.cc.qwipi;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		log.info(request+"");
		PaymentResponseS2S paymentResponseS2S = convertToProcessorClass(request, PaymentResponseS2S.class);
		log.info(paymentResponseS2S+"");
		Long transactionId = Long.parseLong(paymentResponseS2S.getBillNo());
		
		Response<DoProcessorRequest> processorRequest = getDoProcessorRequest(transactionId, "qwipi");
		
		if (!paymentResponseS2S.calculateMd5Info(processorRequest.getData().getProperty("md5Key")).equals(paymentResponseS2S.getMd5Info())) {
			throw new Exception("Invalid MD5 key");
		}
		
		DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
		String message = paymentResponseS2S.getRemark();
		
		switch (ResultCode.fromCode(Integer.parseInt(paymentResponseS2S.getResultCode()))) {
			case SUCCESS: status = DoProcessorResponseStatus.SUCCESS; break;
			case PROCESSING: status = DoProcessorResponseStatus.NOOP; break;
			case FAILED: status = DoProcessorResponseStatus.DECLINED; break;
			default: throw new Exception("Invalid result from QWIPI " + request.toString());
		}
		
		response.setTransactionId(transactionId);
		response.setProcessorReference(paymentResponseS2S.getOrderId());
		response.setStatus(status);
		response.setAmountCentsReceived(new BigDecimal(paymentResponseS2S.getAmount()).multiply(new BigDecimal(100)).intValue());
		response.setMessage(message);
		response.setCallbackResponse(message);
		response.setProcessorRequest(paymentResponseS2S);
		
		return response;
	}
}