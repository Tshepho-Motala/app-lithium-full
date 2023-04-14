package lithium.service.cashier.processor;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class DoProcessorMachine {
	@Autowired CashierInternalClientService cashier;
	@Autowired LithiumConfigurationProperties config;
	@Autowired LithiumServiceClientFactory services;
	@Autowired DoProcessorInterface processor;
	@Autowired(required = false) DoProcessorReversalInterface reversalProcessor;
	
	@Autowired
	@Qualifier("lithium.service.cashier.RestTemplate")
	private RestTemplate rest;
	
	private DoProcessorContext context;
	
	public DoProcessorResponse run(DoProcessorRequest request) {
		log.info("DoProcessorMachine run request " + request);
		DoProcessorResponse response = null;
		try {
			prep(request);
			
			response = context.getResponse();
			switch (request.getTransactionType()) {
				case DEPOSIT:
					response = createResponse(processor.deposit(context.getRequest(), context.getResponse(), context, rest));
					break;
				case WITHDRAWAL:
					response = createResponse(processor.withdraw(context.getRequest(), context.getResponse(), context, rest));
					break;
				case REVERSAL:
					if (reversalProcessor != null) {
						response = createResponse(reversalProcessor.reverse(context.getRequest(), context.getResponse(), context, rest));
					} else {
						log.warn("A reversal request was received but no reversal processor instance was found in the processor setup. Implement the DoProcessorReversalInterface interface");
					}
					break;
				default:
					throw new DoErrorException("Unknown TransactionType: "+request.getTransactionType());
			}
			log.info("DoProcessorMachine "+request.getTransactionType().description().toLowerCase()+" run response " + request.toString() + " " + response.toString());
			return response;
		} catch (Throwable t) {
			log.error("DoErrorException during processor execute: " + t.getMessage(), t);
			response = createResponse(DoProcessorResponseStatus.REMOTE_FAILURE_AUTO_RETRY, t.getMessage());
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n========================================================================================================");
			sb.append("\r\n========================================================================================================");
			sb.append("\r\n"+ExceptionUtils.getMessage(t)+"\r\n");
			sb.append(""+ExceptionUtils.getRootCauseMessage(t)+"\r\n\r\n");
			sb.append("Full Stacktrace: \r\n"+ExceptionUtils.getFullStackTrace(t));
			response.addRawResponseLog(sb.toString());
			return response;
		}
	}

	private DoProcessorResponse createResponse(DoProcessorResponseStatus status) {
		context.getResponse().setStatus(status);
		return context.getResponse();
	}

	private DoProcessorResponse createResponse(DoProcessorResponseStatus status, String message) {
		context.getResponse().setStatus(status);
		context.getResponse().setMessage(message);
		return context.getResponse();
	}
	
	private void prep(DoProcessorRequest request) {
		context = new DoProcessorContext();
		context.setRequest(request);
		context.setResponse(new DoProcessorResponse());
	}
	
}
