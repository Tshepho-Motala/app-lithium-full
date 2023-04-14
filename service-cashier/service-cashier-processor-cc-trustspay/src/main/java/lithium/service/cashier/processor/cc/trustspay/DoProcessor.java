package lithium.service.cashier.processor.cc.trustspay;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.cc.FieldValidatorCC;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.DoProcessorWSAdapter;
import lithium.service.cashier.processor.cc.trustspay.data.AuthorizeRequest;
import lithium.service.cashier.processor.cc.trustspay.data.AuthorizeResponse;
import lithium.service.cashier.processor.cc.trustspay.data.QueryRequest;
import lithium.service.cashier.processor.cc.trustspay.data.QueryResponse;
import lithium.service.cashier.processor.cc.trustspay.ws.QueryClient;
import lithium.util.ObjectToHttpEntity;

@Service
public class DoProcessor extends DoProcessorWSAdapter {
	@Autowired LithiumConfigurationProperties config;
	@Autowired QueryClient queryClient;
	
	//Copied from DoProcessorCCAdapter.java needed to extend from DoProcessorWSAdapter for better logging on WS
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean valid = true;
		
		valid &= FieldValidatorCC.validateCVV(FieldValidatorCC.CVV_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateCardNumber(FieldValidatorCC.CC_NUMBER_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateExpiryMonth(FieldValidatorCC.EXP_MONTH_FIELD, STAGE_1, request, response);
		
		valid &= FieldValidatorCC.validateExpiryYear(FieldValidatorCC.EXP_YEAR_FIELD, STAGE_1, request, response);
		
		if (!valid) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		Map<String, String> properties = request.getProperties();
		AuthorizeRequest processorRequest = new AuthorizeRequest();
		
		processorRequest.setOrderNo(request.getTransactionId().toString());
		processorRequest.setOrderAmount(request.stageInputData(1, "amount"));
		processorRequest.setCardSecurityCode(request.stageInputData(1, "cvv"));
		NumberFormat formatter = new DecimalFormat("00");  
		processorRequest.setCardExpireMonth(formatter.format(Long.parseLong(request.stageInputData(1, "expmonth"))));
		processorRequest.setCardNo(request.stageInputData(1, "ccnumber"));
		processorRequest.setCardExpireYear(request.stageInputData(1, "expyear"));
		
		processorRequest.setAddress(request.getUser().getResidentialAddress().toOneLinerStreet());
		
		String lastKnownIP = request.getUser().getLastKnownIP();
		int indexOf = lastKnownIP.indexOf(",");
		if (indexOf != -1) lastKnownIP = lastKnownIP.substring(0, indexOf);
		
		processorRequest.setIp(lastKnownIP);
		processorRequest.setCity(request.getUser().getResidentialAddress().getCity());
		processorRequest.setCountry(request.getUser().getResidentialAddress().getCountry());
		processorRequest.setZip(request.getUser().getResidentialAddress().getPostalCode());

		processorRequest.setOrderCurrency(request.getUser().getCurrency());

		processorRequest.setEmail(request.getUser().getEmail());
		processorRequest.setFirstName(request.getUser().getFirstName());
		processorRequest.setLastName(request.getUser().getLastName());
		processorRequest.setPhone(request.getUser().getTelephoneNumber());
		processorRequest.setState(request.getUser().getResidentialAddress().getAdminLevel1());
		processorRequest.setIssuingBank("No idea");

		//TODO Find a way to get the csid
		// 		<input type="hidden" name="csid" value="UNDEFINED" id='csid'/>
		//		<script type='text/javascript' charset='utf-8' src='https://shoppingingstore.com/pub/sps.js'></script>
		processorRequest.setCsid(request.stageInputData(1, "csid"));
		
		processorRequest.setMerNo(properties.get("merchantNo"));
		processorRequest.setGatewayNo(properties.get("gatewayNo"));
		processorRequest.setReturnUrl(config.getGatewayPublicUrl() + "/service-cashier/frontend/loading");
		processorRequest.saveSignInfo(properties.get("key"));
		
		
		String processorResponseRaw = postForObject(request, response, context, rest,
			properties.get("paymentUrl"),
			ObjectToHttpEntity.forPostForm(processorRequest),
			String.class
		);
		
		AuthorizeResponse processorResponse = JAXB.unmarshal(new ByteArrayInputStream(processorResponseRaw.getBytes()), AuthorizeResponse.class);
		
		if (!processorResponse.calculateSignInfo(properties.get("key")).equals(processorResponse.getSignInfo())) {
			buildRawResponseLog(response, processorResponse);
			throw new Exception("Invalid signInfo on response. Was " + processorResponse.getSignInfo() 
				+ " expected " + processorResponse.calculateSignInfo(properties.get("key")));
		}
		
		response.setMessage(processorResponse.getOrderInfo());
		response.setProcessorReference(processorResponse.getTradeNo());
		response.setOutputData(1, "transaction_id", processorResponse.getTradeNo());
		response.setOutputData(1, "account_info", request.stageInputData(1, "ccnumber"));
		
		buildRawResponseLog(response, processorResponse);
		
		if (processorResponse.getOrderStatus().equals("0")) {
			return DoProcessorResponseStatus.DECLINED;
		}
		if (processorResponse.getOrderStatus().equals("1")) {
			return DoProcessorResponseStatus.SUCCESS;
		}
		
		return DoProcessorResponseStatus.NEXTSTAGE;

	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		QueryRequest queryRequest = QueryRequest.builder()
			.merNo(request.getProperty("merchantNo"))
			.gatewayNo(request.getProperty("gatewayNo"))
			.orderNo(request.stageOutputData(1, "transaction_id"))
			.build().saveSignInfo(request.getProperty("key"));
		
		QueryResponse queryResponse = queryClient.query(queryRequest, Boolean.valueOf(request.getProperty("testMode")));
		
		buildRawRequestLog(request, response, queryRequest);
		
		switch (queryResponse.getQueryResult()) {
			case -2:
			case -1:
				buildRawResponseLog(response, queryResponse);
				return DoProcessorResponseStatus.NOOP;
			case 0:
				buildRawResponseLog(response, queryResponse);
				return DoProcessorResponseStatus.DECLINED;
			case 1:
				buildRawResponseLog(response, queryResponse);
				return DoProcessorResponseStatus.SUCCESS;
			case 2: response.setMessage("Order does not exist"); break;
			case 3: response.setMessage("Incoming parameters incomplete"); break;
			case 4: response.setMessage("Order an excessive number"); break;
			case 5: response.setMessage("Merchant gateway access error"); break;
			case 6: response.setMessage("Signinfo information error"); break;
			case 7: response.setMessage("Access to the ip error"); break;
			case 999: response.setMessage("Query system error"); break;
		}
		
		buildRawResponseLog(response, queryResponse);
		return DoProcessorResponseStatus.FATALERROR;
	}
}