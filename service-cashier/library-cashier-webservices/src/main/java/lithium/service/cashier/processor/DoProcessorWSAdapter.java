package lithium.service.cashier.processor;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.processor.ws.WebServiceLoggingUtils;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoProcessorWSAdapter extends DoProcessorAdapter {

	protected void buildRawRequestLog(DoProcessorRequest request, DoProcessorResponse response, Object processorRequest) {
		response.addRawRequestLog(WebServiceLoggingUtils.getRequestData());
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nRequest: (svc-cashier -> svc-processor)[internal]");
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nStage          : "+request.getStage());
		sb.append("\r\nGuid           : "+request.getUser().getGuid());
		sb.append("\r\nTransactionId  : "+request.getTransactionId());
		sb.append("\r\nProperties     : \r\n"+JsonStringify.objectToStringFiltered(request.getProperties()));
		sb.append("\r\nInputData      : \r\n"+JsonStringify.objectToStringFiltered(request.getInputData()));
		sb.append("\r\nOutputData     : \r\n"+JsonStringify.objectToStringFiltered(request.getOutputData()));
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nRequest: (svc-processor -> ext-processor)[external]");
		sb.append("\r\n========================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(processorRequest));
		sb.append("\r\n========================================================================================================\r\n");
		response.addRawRequestLog(sb.toString());
		log.debug(sb.toString());
	}
	
	protected void buildRawResponseLog(DoProcessorResponse response, Object processorResponse) {
		StringBuilder sb = new StringBuilder();
		response.addRawResponseLog(WebServiceLoggingUtils.getResponseData());
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nResponse: (ext-processor -> svc-processor)[external]");
		sb.append("\r\n=======================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(processorResponse));
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nResponse: (svc-processor -> svc-cashier)[internal]");
		sb.append("\r\n========================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(response));
		sb.append("\r\n========================================================================================================\r\n");
		response.addRawResponseLog(sb.toString());
		log.debug(sb.toString());
	}
}
