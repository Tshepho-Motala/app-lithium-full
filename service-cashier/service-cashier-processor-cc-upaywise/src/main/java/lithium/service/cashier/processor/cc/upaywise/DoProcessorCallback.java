package lithium.service.cashier.processor.cc.upaywise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lithium.service.cashier.processor.cc.upaywise.data.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		log.info("Callback Parameters :: "+request.getParameterMap());
//		String result = request.getParameter("results");
		String tranId = request.getParameter("transid");
		String eci = request.getParameter("ecis");
		String trackId = request.getParameter("trackids");
		String responseCode = request.getParameter("responsecodes");
		String auth = request.getParameter("auths");
		String rrn = request.getParameter("rrns");
		String udf5 = request.getParameter("udfs5");
//		String amount = request.getParameter("amounts");
//		String email = request.getParameter("email");
		
		Long internalTranId = null;
		try {
			internalTranId = Long.parseLong(trackId);
		} catch (NumberFormatException e) {
			log.error("Could not convert trackId to Long " + e.getMessage(), e);
		}
		
		Response<DoProcessorRequest> processorRequestResponse = getDoProcessorRequest(internalTranId, request.getProcessorCode());
		if (!processorRequestResponse.isSuccessful()) {
			throw new Exception(processorRequestResponse.getMessage());
		}
		DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();
		
		response.setTransactionId(doProcessorRequest.getTransactionId());
		response.setAmountCentsReceived(doProcessorRequest.inputAmount().movePointRight(2).intValue());
		response.setProcessorReference(tranId);
		response.setOutputData(2, "eci", eci);
		response.setOutputData(2, "auth", auth);
		response.setOutputData(2, "rrn", rrn);
		response.setMessage(udf5);
		
		response.setProcessorRequest(request.getParameterMap());
		
		switch (ResponseCode.find(responseCode)) {
			case RC000:
				response.setStatus(DoProcessorResponseStatus.SUCCESS);
				break;
			case RC001:
			case RC509:
				response.setStatus(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
				break;
			default:
				response.setStatus(DoProcessorResponseStatus.DECLINED);
				break;
		}
		
		response.setCallbackResponse("OK");
		response.setRedirect(config.getGatewayPublicUrl()+"/service-cashier/frontend/loadingrefresh");
		
		return response;
	}
	
}