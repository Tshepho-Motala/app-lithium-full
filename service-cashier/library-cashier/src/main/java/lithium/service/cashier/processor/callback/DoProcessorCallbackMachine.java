package lithium.service.cashier.processor.callback;

import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class DoProcessorCallbackMachine {
	@Autowired CashierInternalClientService cashier;
	@Autowired LithiumConfigurationProperties config;
	@Autowired LithiumServiceClientFactory services;
	@Autowired DoProcessorCallbackInterface processor;
	@Autowired RestTemplate rest;
	
	private void buildRawRequestLog(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) {
		StringBuffer sb = new StringBuffer();
		sb.append("========================================================================================================");
		sb.append("\r\nRaw Callback Request Data: (ext-processor -> svc-cashier)[external]");
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nContextPath     : "+request.getContextPath());
		sb.append("\r\nHash            : "+request.getHash());
		sb.append("\r\nProcessorCode   : "+request.getProcessorCode());
		sb.append("\r\nLocale          : "+request.getLocale());
		sb.append(
			"\r\nHeaders         : \r\n\t"+
			request.getHeaderMap()
			.entrySet().stream()
			.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
			.collect(Collectors.joining("\r\n\t"))
		);
		sb.append(
			"\r\nParameters      : "+
			request.getParameterMap()
			.entrySet().stream()
			.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
			.collect(Collectors.joining("\r\n\t"))
		);
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nRequest: (received processor data)");
		sb.append("\r\n========================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(response.getProcessorRequest()));
		log.debug(sb.toString());
		response.addRawRequestLog(sb.toString());
	}
	
	private void buildRawResponseLog(DoProcessorCallbackResponse response) {
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nResponse: (svc-processor -> svc-cashier)[internal]");
		sb.append("\r\n========================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(response));
		log.debug(sb.toString());
		response.addRawResponseLog(sb.toString());
	}
	
	public DoProcessorCallbackResponse run(DoProcessorCallbackRequest request) {
		log.info("DoProcessorMachine run request " + request);
		DoProcessorCallbackResponse response;
		try {
			response = DoProcessorCallbackResponse.builder2().build();
			response = processor.callback(request, response);
			buildRawRequestLog(request, response);
			buildRawResponseLog(response);
			return response;
		} catch (Throwable t) {
			log.error("DoErrorException during processor execute: " + t.getMessage(), t);
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n========================================================================================================");
			sb.append("\r\n========================================================================================================");
			sb.append("\r\n"+ExceptionUtils.getMessage(t)+"\r\n");
			sb.append(""+ExceptionUtils.getRootCauseMessage(t)+"\r\n\r\n");
			sb.append("Full Stacktrace: \r\n"+ExceptionUtils.getFullStackTrace(t));
			log.error(sb.toString(), t);
//			response.addRawRequestLog(sb.toString());
			return null;
		}
	}
}
