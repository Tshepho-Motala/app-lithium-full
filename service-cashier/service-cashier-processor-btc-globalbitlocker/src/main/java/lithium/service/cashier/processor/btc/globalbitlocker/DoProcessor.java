package lithium.service.cashier.processor.btc.globalbitlocker;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.btc.DoProcessorBTCAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.btc.globalbitlocker.data.AmountConverterRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.data.AmountConverterResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.data.GetStatusRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.data.GetStatusResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.data.OutgoingPaymentRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.data.OutgoingPaymentResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressResponse;
import lithium.service.cashier.processor.btc.globalbitlocker.enums.Status;
import lithium.util.ObjectToStringMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorBTCAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		log.info("GW : "+config.getGatewayPublicUrl());
		//"http://196.22.242.139:9000"
		String callbackUrl = config.getGatewayPublicUrl()+
			request.getProperty("callback.url")+
			"?t="+request.getTransactionId();
		ReceiveAddressRequest processorRequest = ReceiveAddressRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.user(request.getUser().getGuid())
			.callbackUrl(URLEncoder.encode(callbackUrl, "UTF-8"))
			.build();
		processorRequest.setSign(processorRequest.calculateSign());
		processorRequest.setCallbackUrl(processorRequest.getCallbackUrl()+"&h="+processorRequest.getSign());
		
		ReceiveAddressResponse processorResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			ReceiveAddressRequest.API+
			"&key="+processorRequest.getApiKey()+
			"&callback_url="+processorRequest.getCallbackUrl()+
			"&user="+processorRequest.getUser()+
			"&amount="+request.stageInputData(1, "amount"),
			ReceiveAddressResponse.class,
			ObjectToStringMap.toStringMapFormMap(processorRequest)
		);
		buildRawResponseLog(response, processorResponse);
		
		AmountConverterRequest amountConverterRequest = AmountConverterRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.amount(request.stageInputData(1, "amount"))
			.build();
		
		AmountConverterResponse amountConverterResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			AmountConverterRequest.API+
			"&key="+amountConverterRequest.getApiKey()+
			"&amount="+amountConverterRequest.getAmount(),
			AmountConverterResponse.class,
			ObjectToStringMap.toStringMapFormMap(amountConverterRequest)
		);
		
		response.setProcessorReference(processorResponse.getId());
		
		if ((Status.fromStatus(processorResponse.getSystemStatus())) == Status.FAILED) {
			response.setMessage(processorResponse.getSystemMessage());
			buildRawResponseLog(response, amountConverterResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		buildRawResponseLog(response, amountConverterResponse);
		
		GetStatusRequest getStatusRequest = GetStatusRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.id(processorResponse.getId())
			.build();
		
		GetStatusResponse getStatusResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			GetStatusRequest.API+
			"&key="+getStatusRequest.getApiKey()+
			"&id="+getStatusRequest.getId(),
			GetStatusResponse.class,
			ObjectToStringMap.toStringMapFormMap(getStatusRequest)
		);
		
		response.stageOutputData(1).put("summary", processorResponse.getSummary());
		response.stageOutputData(1).put("externalId", processorResponse.getId());
		response.stageOutputData(1).put("account_info", processorResponse.getAddress());
		response.stageOutputData(1).put("address", processorResponse.getAddress());
		response.stageOutputData(1).put("bitcoins", amountConverterResponse.getBtcAmount());
		
		switch (Status.fromStatus(getStatusResponse.getStatus())) {
			case COMPLETED:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.SUCCESS;
			case EXPIRED:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.DECLINED;
			case WAITING:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.NEXTSTAGE;
			default:
				response.setMessage(processorResponse.getSystemMessage());
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.FATALERROR;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetStatusRequest processorRequest = GetStatusRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.id(request.stageOutputData(1, "externalId"))
			.build();
		
		GetStatusResponse processorResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			GetStatusRequest.API+
			"&key="+processorRequest.getApiKey()+
			"&id="+processorRequest.getId(),
			GetStatusResponse.class,
			ObjectToStringMap.toStringMapFormMap(processorRequest)
		);
		response.setProcessorReference(processorRequest.getId());
		
		switch (Status.fromStatus(processorResponse.getStatus())) {
			case COMPLETED:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.SUCCESS;
			case EXPIRED:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.DECLINED;
			case WAITING:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.NOOP;
			default:
				response.setMessage(processorResponse.getSystemMessage());
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.FATALERROR;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String callbackUrl = config.getGatewayPublicUrl()+
			request.getProperty("callback.url")+
			"?t="+request.getTransactionId();
		OutgoingPaymentRequest processorRequest = OutgoingPaymentRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.user(request.getUser().getGuid())
			.callbackUrl(URLEncoder.encode(callbackUrl, "UTF-8"))
			.address(request.stageInputData(1, "address"))
			.amount(request.stageInputData(1, "amount"))
			.build();
		processorRequest.setCallbackUrl(processorRequest.getCallbackUrl()+"&h="+processorRequest.calculateSign());
		log.info(processorRequest.toString());
		
		OutgoingPaymentResponse processorResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			OutgoingPaymentRequest.API+
			"&key="+processorRequest.getApiKey()+
			"&callback_url="+processorRequest.getCallbackUrl()+
			"&user="+processorRequest.getUser()+
			"&address="+processorRequest.getAddress()+
			"&amount="+request.stageInputData(1, "amount"),
			OutgoingPaymentResponse.class,
			ObjectToStringMap.toStringMapFormMap(processorRequest)
		);
		response.setProcessorReference(processorResponse.getId());
		
		if ((Status.fromStatus(processorResponse.getSystemStatus())) == Status.FAILED) {
			response.setMessage(processorResponse.getSystemMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		buildRawResponseLog(response, processorResponse);
		
		GetStatusRequest getStatusRequest = GetStatusRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.id(processorResponse.getId())
			.build();
		
		GetStatusResponse getStatusResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			GetStatusRequest.API+
			"&key="+getStatusRequest.getApiKey()+
			"&id="+getStatusRequest.getId(),
			GetStatusResponse.class,
			ObjectToStringMap.toStringMapFormMap(getStatusRequest)
		);
		response.stageOutputData(1).put("externalId", processorResponse.getId());
		
		switch (Status.fromStatus(getStatusResponse.getStatus())) {
			case COMPLETED:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.SUCCESS;
			case EXPIRED:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.DECLINED;
			case WAITING:
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.NEXTSTAGE;
			default:
				response.setMessage(processorResponse.getSystemMessage());
				buildRawResponseLog(response, getStatusResponse);
				return DoProcessorResponseStatus.FATALERROR;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetStatusRequest processorRequest = GetStatusRequest.builder()
			.apiKey(request.getProperty("apikey"))
			.id(request.stageOutputData(1, "externalId"))
			.build();
		
		GetStatusResponse processorResponse = getForObject(request, response, context, rest,
			request.getProperty("apiurl")+
			GetStatusRequest.API+
			"&key="+processorRequest.getApiKey()+
			"&id="+processorRequest.getId(),
			GetStatusResponse.class,
			ObjectToStringMap.toStringMapFormMap(processorRequest)
		);
		response.setProcessorReference(processorRequest.getId());
		
		
		switch (Status.fromStatus(processorResponse.getStatus())) {
			case COMPLETED:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.SUCCESS;
			case EXPIRED:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.DECLINED;
			case WAITING:
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.NOOP;
			default:
				response.setMessage(processorResponse.getSystemMessage());
				buildRawResponseLog(response, processorResponse);
				return DoProcessorResponseStatus.FATALERROR;
		}
	}
}
