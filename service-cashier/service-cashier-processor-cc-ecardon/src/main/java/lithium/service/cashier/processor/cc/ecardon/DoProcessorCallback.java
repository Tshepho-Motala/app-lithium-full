package lithium.service.cashier.processor.cc.ecardon;

import java.util.Arrays;

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

@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		Long transactionId = Long.parseLong(request.getParameter("t"));
		Response<DoProcessorRequest> doProcessorRequest = getDoProcessorRequest(transactionId, request.getProcessorCode());
		
		String id = request.getParameter("id");
		String resourcePath = request.getParameter("resourcePath");
		String target = request.getParameter("target");
		String method = request.getParameter("method");
		
		response.setTransactionId(doProcessorRequest.getData().getTransactionId());
		response.setProcessorReference(id);
		response.setOutputData(2, "processorReference", id);
		response.setStatus(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
		response.setProcessorRequest(Arrays.asList("id:"+id, "transactionId:"+transactionId, "resourcePath:"+resourcePath, "target:"+target, "method:"+method));
		response.setCallbackResponse("OK");
		// "http://196.22.242.139:9000"  <-- Riaan external ip
		// config.getGatewayPublicUrl()
		response.setRedirect(
			config.getGatewayPublicUrl()+
			"/service-cashier/frontend/loadingrefresh"
		);
		
		return response;
	}
}
