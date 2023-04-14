package lithium.service.cashier.processor;

import java.util.Map;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.http.LoggingRequestInterceptor;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoProcessorAdapter implements DoProcessorInterface, DoProcessorReversalInterface {

	public static final int STAGE_1 = 1;
	public static final int STAGE_2 = 2;
	public static final int STAGE_3 = 3;
	public static final int STAGE_4 = 4;
	public static final int STAGE_5 = 5;
	public static final int STAGE_6 = 6;
	public static final int STAGE_7 = 7;
	
	@Override
	public DoProcessorResponseStatus deposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception {
		DoProcessorResponseStatus responseStatus;
		
		switch(request.getStage()) {
			case STAGE_1: responseStatus = validateDepositStage1(request, response); break;
			case STAGE_2: responseStatus = validateDepositStage2(request, response); break;
			case STAGE_3: responseStatus = validateDepositStage3(request, response); break;
			case STAGE_4: responseStatus = validateDepositStage4(request, response); break;
			case STAGE_5: responseStatus = validateDepositStage5(request, response); break;
			case STAGE_6: responseStatus = validateDepositStage6(request, response); break;
			case STAGE_7: responseStatus = validateDepositStage7(request, response); break;
			default: responseStatus = DoProcessorResponseStatus.SUCCESS;
		}
		
		if (responseStatus !=  DoProcessorResponseStatus.SUCCESS) return responseStatus;
		
		switch(request.getStage()) {
			case STAGE_1: return depositStage1(request, response, context, rest);
			case STAGE_2: return depositStage2(request, response, context, rest);
			case STAGE_3: return depositStage3(request, response, context, rest);
			case STAGE_4: return depositStage4(request, response, context, rest);
			case STAGE_5: return depositStage5(request, response, context, rest);
			case STAGE_6: return depositStage6(request, response, context, rest);
			case STAGE_7: return depositStage7(request, response, context, rest);
			default: throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
		}
	}
	
	protected void buildRawRequestLog(DoProcessorRequest request, DoProcessorResponse response, Object processorRequest) {
		response.addRawRequestLog(LoggingRequestInterceptor.getRequestData());
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
		if (processorRequest!=null) sb.append("\r\nRequest: (svc-processor -> ext-processor)[external]");
		if (processorRequest!=null) sb.append("\r\n========================================================================================================\r\n");
		if (processorRequest!=null) sb.append(JsonStringify.objectToStringFiltered(processorRequest));
		if (processorRequest!=null) sb.append("\r\n========================================================================================================\r\n");
		response.addRawRequestLog(sb.toString());
		log.debug(sb.toString());
	}
	
	protected void buildRawResponseLog(DoProcessorResponse response, Object processorResponse) {
		StringBuilder sb = new StringBuilder();
		response.addRawResponseLog(LoggingRequestInterceptor.getResponseData());
		if (processorResponse!=null) sb.append("\r\n========================================================================================================");
		if (processorResponse!=null) sb.append("\r\nResponse: (ext-processor -> svc-processor)[external]");
		if (processorResponse!=null) sb.append("\r\n=======================================================================================================\r\n");
		if (processorResponse instanceof String)
			sb.append(processorResponse);
		else
			if (processorResponse!=null) sb.append(JsonStringify.objectToStringFiltered(processorResponse));
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nResponse: (svc-processor -> svc-cashier)[internal]");
		sb.append("\r\n========================================================================================================\r\n");
		sb.append(JsonStringify.objectToStringFiltered(response));
		sb.append("\r\n========================================================================================================\r\n");
		response.addRawResponseLog(sb.toString());
		log.debug(sb.toString());
	}
	
	protected <T> T postForObject(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String url, Object processorRequest, Class<T> responseType, Object... uriVariables) {
		return postForObject(request, response, context, rest, url, processorRequest, processorRequest, responseType, uriVariables);
	}
	
	protected <T> T postForObject(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String url, Object processorRequest, Object objectToPost, Class<T> responseType, Object... uriVariables) {
		buildRawRequestLog(request, response, processorRequest);
		
		T processorResponse = rest.postForObject(url, objectToPost, responseType);
		
		return processorResponse;
	}
	
	protected <T> T postForObject(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String url, Object processorRequest, Object objectToPost, Class<T> responseType, Map<String, ?> urlVariables) {
		buildRawRequestLog(request, response, processorRequest);
		
		T processorResponse = rest.postForObject(url, objectToPost, responseType, urlVariables);
		
		return processorResponse;
	}
	
	protected <T> T getForObject(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String url, Class<T> responseType) {
		buildRawRequestLog(request, response, "");
		
		T processorResponse = rest.getForObject(url, responseType);
		
		return processorResponse;
	}
	
	protected <T> T getForObject(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String url, Class<T> responseType, Map<String, ?> urlVariables) {
		buildRawRequestLog(request, response, "");
		
		T processorResponse = rest.getForObject(url, responseType, urlVariables);
		
		return processorResponse;
	}
	
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage4(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage5(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage6(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus depositStage7(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Deposit stage " + request.getStage() + " is not implemented in this processor");
	}
	
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateDepositStage2(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateDepositStage3(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateDepositStage4(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateDepositStage5(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateDepositStage6(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateDepositStage7(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Deposit stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	@Override
	public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception {
		DoProcessorResponseStatus responseStatus;
		
		switch(request.getStage()) {
			case STAGE_1: responseStatus = validateWithdrawalStage1(request, response); break;
			case STAGE_2: responseStatus = validateWithdrawalStage2(request, response); break;
			case STAGE_3: responseStatus = validateWithdrawalStage3(request, response); break;
			case STAGE_4: responseStatus = validateWithdrawalStage4(request, response); break;
			case STAGE_5: responseStatus = validateWithdrawalStage5(request, response); break;
			case STAGE_6: responseStatus = validateWithdrawalStage6(request, response); break;
			case STAGE_7: responseStatus = validateWithdrawalStage7(request, response); break;
			default: responseStatus = DoProcessorResponseStatus.SUCCESS;
		}
		
		if (responseStatus !=  DoProcessorResponseStatus.SUCCESS) return responseStatus;
		
		switch(request.getStage()) {
			case STAGE_1: return withdrawStage1(request, response, context, rest);
			case STAGE_2: return withdrawStage2(request, response, context, rest);
			case STAGE_3: return withdrawStage3(request, response, context, rest);
			case STAGE_4: return withdrawStage4(request, response, context, rest);
			case STAGE_5: return withdrawStage5(request, response, context, rest);
			case STAGE_6: return withdrawStage6(request, response, context, rest);
			case STAGE_7: return withdrawStage7(request, response, context, rest);
			default: throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
		}
	}

	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage4(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage5(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage6(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus withdrawStage7(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Withdrawal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus validateWithdrawalStage1(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateWithdrawalStage2(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateWithdrawalStage3(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateWithdrawalStage4(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateWithdrawalStage5(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	protected DoProcessorResponseStatus validateWithdrawalStage6(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateWithdrawalStage7(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Withdrawal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	@Override
	public DoProcessorResponseStatus reverse(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception {
		DoProcessorResponseStatus responseStatus;

		switch(request.getStage()) {
			case STAGE_1: responseStatus = validateReversealStage1(request, response); break;
			case STAGE_2: responseStatus = validateReversealStage2(request, response); break;
			case STAGE_3: responseStatus = validateReversealStage3(request, response); break;
			case STAGE_4: responseStatus = validateReversealStage4(request, response); break;
			case STAGE_5: responseStatus = validateReversealStage5(request, response); break;
			case STAGE_6: responseStatus = validateReversealStage6(request, response); break;
			case STAGE_7: responseStatus = validateReversealStage7(request, response); break;
			default: responseStatus = DoProcessorResponseStatus.SUCCESS;
		}

		if (responseStatus !=  DoProcessorResponseStatus.SUCCESS) return responseStatus;

		switch(request.getStage()) {
			case STAGE_1: return reverseStage1(request, response, context, rest);
			case STAGE_2: return reverseStage2(request, response, context, rest);
			case STAGE_3: return reverseStage3(request, response, context, rest);
			case STAGE_4: return reverseStage4(request, response, context, rest);
			case STAGE_5: return reverseStage5(request, response, context, rest);
			case STAGE_6: return reverseStage6(request, response, context, rest);
			case STAGE_7: return reverseStage7(request, response, context, rest);
			default: throw new NotImplementedException("Reverse stage " + request.getStage() + " is not implemented in this processor");
		}
	}

	protected DoProcessorResponseStatus reverseStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage4(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage5(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage6(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus reverseStage7(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new NotImplementedException("Reversal stage " + request.getStage() + " is not implemented in this processor");
	}

	protected DoProcessorResponseStatus validateReversealStage1(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage2(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage3(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage4(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage5(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage6(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}

	protected DoProcessorResponseStatus validateReversealStage7(DoProcessorRequest request, DoProcessorResponse response) {
		log.debug("Reversal stage " + request.getStage() + " validation is not implemented in this processor");
		return DoProcessorResponseStatus.SUCCESS;
	}
}
