package lithium.service.cashier.processor.neosurf;

import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lithium.service.cashier.processor.neosurf.data.RegisterResponse;
import lombok.extern.slf4j.Slf4j;

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {

	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request,
			DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		// String key = "78cf6e73069df2206a1fc0fd150991ed";
		String lithiumTransactionId = request.getParameter("merchantTransactionId");

		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(Long.valueOf(lithiumTransactionId),
				request.getProcessorCode());
		String key = doProcessorRequest.getData().getProperty("secretkey");

		Long neosurfTransactionId = Long.parseLong(request.getParameter("transactionId"));

		String responseCode = request.getParameter("status");
		response.setTransactionId(Long.valueOf(lithiumTransactionId));
		response.setProcessorReference(neosurfTransactionId.toString());
		response.setOutputData(2, "transactionId", lithiumTransactionId.toString());
		response.setMessage(responseCode);

		RegisterResponse requestResponse = RegisterResponse.builder()
				.amount(Integer.parseInt(request.getParameter("amount"))).checksum(key)
				.created(request.getParameter("created")).currency(request.getParameter("currency"))
				.errorCode(request.getParameter("errorCode")).errorMessage(request.getParameter("errorMessage"))
				.merchantId(request.getParameter("merchantId")).subMerchantId(request.getParameter("subMerchantId"))
				.hash(request.getParameter("hash").split("")[1].trim())
				.methodChargedAmount(request.getParameter("methodChargedAmount"))
				.transactionId(neosurfTransactionId.toString()).merchantTransactionId(lithiumTransactionId.toString())
				.methodCurrency(request.getParameter("methodCurrency"))
				.methodExpiry(request.getParameter("methodExpiry")).methodId(request.getParameter("methodId"))
				.methodLabel(request.getParameter("methodLabel")).methodName(request.getParameter("methodName"))
				.status(request.getParameter("status")).subMerchantId(request.getParameter("subMerchantId"))
				.transaction3d(request.getParameter("transaction3d")).build();

		response.setProcessorRequest(request.getParameterMap());
		response.setCallbackResponse(responseCode);
		String computedHash = requestResponse.validate(key);

		if (request.getParameter("hash").equalsIgnoreCase(computedHash)) {
			if (request.getParameter("errorCode") == null || request.getParameter("errorCode").isEmpty()) {
				response.setStatus(DoProcessorResponseStatus.NEXTSTAGE);
			} else {
			    response.setMessage(responseCode + "-" + request.getParameter("errorMessage"));
				response.setStatus(DoProcessorResponseStatus.FATALERROR);
			}
		} else {

			response.setStatus(DoProcessorResponseStatus.FATALERROR);
		}
		return response;
	}
}
