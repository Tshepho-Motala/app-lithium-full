package lithium.service.cashier.processor.netaxept;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.netaxept.DoProcessorNetaxeptAdapter;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		Long transactionId = Long.parseLong(request.getParameter("transactionId"));
		String responseCode = request.getParameter("responseCode");
		String processorId = request.getParameter("dm_id");
		String processorType = request.getParameter("dm_type");
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(transactionId, request.getProcessorCode());
		
		response.setTransactionId(doProcessorRequest.getData().getTransactionId());
		response.setProcessorReference(transactionId+"");
		response.setOutputData(2, "transactionId", request.getParameter("transactionId"));
		response.setMessage(responseCode);
		
//		XmlMapper om = new XmlMapper();
//		JsonNode root = request.getRequestBody() == null ? om.createObjectNode() : om.readTree(request.getRequestBody().toString());
//		JsonNode tranIdNode = root.findValue("TransactionId");
//		JsonNode responseCodeNode = root.findValue("ResponseCode");
//		
//		JsonNode result = root.findPath("Result");
//		JsonNode responseTextNode = null;
//		JsonNode responseSourceNode = null;
		
		
//		if (responseCode.contentEquals("OK")) {
			//API says to do a query call and confirm status of transaction just to be sure. This only seems to be the case with direct bank deposit results.
//			response.setStatus(DoProcessorResponseStatus.SUCCESS);
//		} else {
			//Handle error state on terminal response
//			if (!result.isMissingNode()) {
//				responseCodeNode = result.findValue("ResponseCode");
//				tranIdNode = result.findValue("TransactionId");
//				responseTextNode = root.findValue("ResponseText");
//				responseSourceNode = root.findValue("ResponseSource");
//				response.setMessage(responseSourceNode.textValue() + ": " + responseCodeNode.textValue() + " - " + responseTextNode.textValue() + " tranId: " + tranIdNode.textValue());
//			}
			//response.setStatus(DoProcessorResponseStatus.DECLINED);
//		}
		response.setStatus(DoProcessorResponseStatus.REDIRECT);
		response.setProcessorRequest(request.getParameterMap());
		response.setCallbackResponse("OK");
		Map<String,String> fieldMap = doProcessorRequest.getData().getInputData().get(1);
		String iframeDepositUrlFromFrontend = fieldMap.get("iframeDepositCallbackUrl");
		if (iframeDepositUrlFromFrontend == null || iframeDepositUrlFromFrontend.trim().isEmpty()) {
			iframeDepositUrlFromFrontend = doProcessorRequest.getData().getProperty(DoProcessorNetaxeptAdapter.PROPERTY_WEBSITE_CALLBACK_REDIRECT_URL);
		}
		response.setRedirect(iframeDepositUrlFromFrontend+"?dm_id="+processorId+"&dm_type="+processorType+"&limitMinAmount=0&limitMaxAmount=0");
		
		return response;
	}
	
	
}
