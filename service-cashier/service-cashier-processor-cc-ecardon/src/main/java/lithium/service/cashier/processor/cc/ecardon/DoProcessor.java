package lithium.service.cashier.processor.cc.ecardon;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.enums.CardType;
import lithium.service.cashier.method.cc.DoProcessorCCAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.cc.ecardon.data.DebitRequest;
import lithium.service.cashier.processor.cc.ecardon.data.DebitResponse;
import lithium.service.cashier.processor.cc.ecardon.data.Parameters;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorCCAdapter {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		Map<String, String> properties = request.getProperties();
		DebitRequest processorRequest = new DebitRequest();
		
		processorRequest.setMerchantTransactionId(request.getTransactionId().toString());
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		DecimalFormat df = new DecimalFormat("#0.##");
		df.setMinimumFractionDigits(2);
		processorRequest.setAmount(df.format(amount));
		processorRequest.setCurrency(request.getUser().getCurrency());
		
		String ccnumber = request.stageInputData(1, "ccnumber");
		
		processorRequest.setPaymentBrand(CardType.detect(ccnumber).shortCode());
		processorRequest.setPaymentType("DB");
		
		processorRequest.setCardNumber(ccnumber);
		processorRequest.setCardHolder(request.getUser().getFullName());
		NumberFormat format = new DecimalFormat("00");
		processorRequest.setCardExpiryMonth(format.format(format.parse(request.stageInputData(1, "expmonth"))));
		processorRequest.setCardExpiryYear(request.stageInputData(1, "expyear"));
		processorRequest.setCardSecurityCode(request.stageInputData(1, "cvv"));
		
		processorRequest.setCustomerMerchantCustomerId(request.getUser().md5Guid());
		processorRequest.setCustomerGivenName(request.getUser().getFirstName());
		processorRequest.setCustomerSurname(request.getUser().getLastName());
		//The birth day of the customer in the format yyyy-MM-dd, e.g. 1970-02-17
		DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd");
		processorRequest.setCustomerBirthDate(f.print(request.getUser().getDateOfBirth()));
//		processorRequest.setCustomerStatus("EXISTING");
		processorRequest.setCustomerPhone(request.getUser().getTelephoneNumber());
		processorRequest.setCustomerMobile(request.getUser().getCellphoneNumber());
		processorRequest.setCustomerEmail(request.getUser().getEmail());
		processorRequest.setCustomerIp(request.getUser().getLastKnownIP());
		
		processorRequest.setBillingStreet1(request.getUser().getResidentialAddress().getAddressLine1());
		processorRequest.setBillingStreet2(request.getUser().getResidentialAddress().getAddressLine2());
		processorRequest.setBillingCity(request.getUser().getResidentialAddress().getCity());
		processorRequest.setBillingState(request.getUser().getResidentialAddress().getAdminLevel1());
		processorRequest.setBillingPostCode(request.getUser().getResidentialAddress().getPostalCode());
		processorRequest.setBillingCountry(request.getUser().getResidentialAddress().getCountryCode());
		
		processorRequest.setAuthenticationUserId(properties.get("authentication.userId"));
		processorRequest.setAuthenticationPassword(properties.get("authentication.password"));
		processorRequest.setAuthenticationEntityId(properties.get("authentication.entityId"));
		if ((properties.get("testMode")!=null) && (!properties.get("testMode").isEmpty()) && (!properties.get("testMode").equals("LIVE"))) {
			processorRequest.setTestMode(properties.get("testMode"));
		} else {
			processorRequest.setTestMode(null);
		}
		
		response.setOutputData(1, "account_info", request.stageInputData(1, "ccnumber"));
		
		// "http://196.22.242.139:9000"  <-- Riaan external ip
		// config.getGatewayPublicUrl()
		processorRequest.setShopperResultUrl(
			config.getGatewayPublicUrl()+
			properties.get("callback.url")+
			"?t="+request.getTransactionId()
		);
		
		DebitResponse processorResponse = null;
		processorResponse = postForObject(request, response, context, rest,
			properties.get("paymentUrl"),
			ObjectToHttpEntity.forPostFormFormParam(processorRequest),
			DebitResponse.class
		);
		
		response.setAmount(BigDecimal.valueOf(Double.parseDouble(processorRequest.getAmount())));
		response.setProcessorReference(processorResponse.getId());
		response.setMessage(processorResponse.getResult().getDescription());
		
		if (processorResponse.getResult().isSuccessful()) {
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.SUCCESS;
		} else if (processorResponse.getResult().isPending()) {
			response.setIframeUrl(processorResponse.getRedirect().getUrl());
			response.setIframePostData(processorResponse.getRedirect().getParameters().stream().collect(Collectors.toMap(Parameters::getName, Parameters::getValue)));
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.IFRAMEPOST;
		} else if (
			(processorResponse.getResult().isSuccessManualReview()) || 
			(processorResponse.getResult().isPending()) || 
			(processorResponse.getResult().isPendingWaiting())
		) {
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		} else {
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		Map<String, String> properties = request.getProperties();
		
		String authenticationUserId = (properties.get("authentication.userId"));
		String authenticationPassword = (properties.get("authentication.password"));
		String authenticationEntityId = (properties.get("authentication.entityId"));
//		String testMode = "";
//		if ((properties.get("testMode")!=null) && (!properties.get("testMode").isEmpty()) && (!properties.get("testMode").equals("LIVE"))) testMode = (properties.get("testMode"));
		
		String processorReference = request.getOutputData().get(2).get("processorReference");
//		String processorReference = response.getProcessorReference();
		
		String uri = properties.get("paymentUrl")+"/"+processorReference;
		uri+= "?authentication.userId="+authenticationUserId;
		uri+= "&authentication.password="+authenticationPassword;
		uri+= "&authentication.entityId="+authenticationEntityId;
//		uri+= "&testMode="+testMode;
		
		DebitResponse processorResponse = getForObject(request, response, context, rest,
			uri,
			DebitResponse.class
		);
		log.debug("DebitResponse : "+processorResponse);
		
		if (processorResponse.getResult().isSuccessful()) {
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.SUCCESS;
		} else if (
			(processorResponse.getResult().isSuccessManualReview()) || 
			(processorResponse.getResult().isPending()) || 
			(processorResponse.getResult().isPendingWaiting())
		) {
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.NOOP;
		} else {
			response.setMessage(processorResponse.getResult().getDescription());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
	}
}
