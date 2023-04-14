package lithium.service.cashier.processor.ids.idebit;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lithium.service.cashier.processor.ids.idebit.data.PaymentNotificationResponse;
import lithium.service.cashier.processor.ids.idebit.data.ReturnNotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {

		// Identification method for message type (R=return message) (T=transfer notification)
		String transactionType = request.getParameterMap().get("txn_type");


		if (transactionType.equalsIgnoreCase("T")) {
			PaymentNotificationResponse paymentNotificationResponse = new PaymentNotificationResponse();
			paymentNotificationResponse.mapParametersToObject(request.getParameterMap());
			response.addRawRequestLog(paymentNotificationResponse.toString());
			if (paymentNotificationResponse.getStatus().equalsIgnoreCase("S")) {
				response.setTransactionId(Long.parseLong(paymentNotificationResponse.getTransactionNumber()));
				response.setProcessorReference(paymentNotificationResponse.getIdsTransactionNumber());
				response.setStatus(DoProcessorResponseStatus.NEXTSTAGE);
				response.setOutputData(2, "transactionId", paymentNotificationResponse.getTransactionNumber());
				response.setOutputData(2, "processorUserId", paymentNotificationResponse.getIdsUserId());
				response.setOutputData(2, "processorTransactionId", paymentNotificationResponse.getIdsTransactionNumber());
				response.setOutputData(2, "account_info", paymentNotificationResponse.getIdsUserId());

				response.setProcessorRequest(request.getParameterMap());
				response.setProcessorUserId(paymentNotificationResponse.getIdsUserId());
			} else {
				response.setMessage(paymentNotificationResponse.getErrorString() + "("+paymentNotificationResponse.getErrorCode()+")");
				response.setStatus(DoProcessorResponseStatus.NEXTSTAGE);
			}

		} else if (transactionType.equalsIgnoreCase("R")) {
			ReturnNotificationResponse returnNotificationResponse = new ReturnNotificationResponse();
			returnNotificationResponse.mapParametersToObject(request.getParameterMap());
			response.addRawRequestLog(returnNotificationResponse.toString());
			response.setTransactionId(Long.parseLong(returnNotificationResponse.getOriginalTransactionNumber()));
			response.setProcessorReference(returnNotificationResponse.getIdsTransactionNumber());
			response.setProcessorRequest(request.getParameterMap());
			response.setProcessorUserId(returnNotificationResponse.getIdsUserId());
			response.setStatus(DoProcessorResponseStatus.REVERSAL_NEXTSTAGE);
		}

		return response;
	}
}
