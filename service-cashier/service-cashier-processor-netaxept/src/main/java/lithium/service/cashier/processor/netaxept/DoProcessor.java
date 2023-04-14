package lithium.service.cashier.processor.netaxept;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.netaxept.DoProcessorNetaxeptAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.netaxept.data.QueryRequest;
import lithium.service.cashier.processor.netaxept.data.RegisterRequest;
import lithium.service.cashier.processor.netaxept.data.RegisterResponse;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorNetaxeptAdapter {
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
		
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		amount = amount.movePointRight(2);
		
		
		RegisterRequest processorRequest = RegisterRequest.builder()
				.merchantId(request.getProperty(PROPERTY_MERCHANT_ID))
				.token(request.getProperty(PROPERTY_TOKEN))
				.currencyCode(request.getUser().getCurrency())
				.orderNumber(request.getTransactionId().toString())
				.transactionId(request.getTransactionId().toString())
				.amount(amount.toString())
				.redirectUrl(config.getGatewayPublicUrl() + request.getProperty(PROPERTY_CALLBACK_URL))
				//.redirectUrl("http://10.0.14.86:9000" + request.getProperty(PROPERTY_CALLBACK_URL))
				.build();
	
		
		String processorResponseString = null;
		processorResponseString = postForObject(request, response, context, rest,
			request.getProperty(PROPERTY_BASE_URL) + "/Netaxept/Register.aspx",
			ObjectToHttpEntity.forPostFormFormParam(processorRequest),
			String.class
		);
		
		XmlMapper om = new XmlMapper();
		JsonNode root = om.readTree(processorResponseString);
		
		JsonNode tranId = root.findValue("TransactionId");
		JsonNode errorMessage = null;
		
		RegisterResponse processorResponse = null;
		if (tranId != null) {
			processorResponse = new RegisterResponse();
			processorResponse.setTransactionId(tranId.textValue());
		} else {
			errorMessage = root.findPath("error").findValue("message");
		}

		if (processorResponse != null && !processorResponse.getTransactionId().isEmpty()) {
			buildRawResponseLog(response, processorResponse);
			//Call to mobile goes to different url for terminal redirect
			if (request.getMobile() != null && request.getMobile().booleanValue() == true) {
				response.setRedirectUrl(request.getProperty(PROPERTY_BASE_URL) + "/terminal/mobile/default.aspx"+"?merchantId=" + request.getProperty(PROPERTY_MERCHANT_ID) + "&transactionId=" + processorResponse.getTransactionId());
			} else {
				response.setRedirectUrl(request.getProperty(PROPERTY_BASE_URL) + "/Terminal/default.aspx"+"?merchantId=" + request.getProperty(PROPERTY_MERCHANT_ID) + "&transactionId=" + processorResponse.getTransactionId());
			}
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.REDIRECT_NEXTSTAGE;
		} else {
			if (errorMessage != null) {
				response.setMessage(errorMessage.textValue());
			}
			buildRawResponseLog(response, processorResponseString);
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
		
		String transactionId = request.getTransactionId()+"";
		QueryRequest processorRequest = QueryRequest.builder()
				.merchantId(request.getProperty(PROPERTY_MERCHANT_ID))
				.token(request.getProperty(PROPERTY_TOKEN))
				.transactionId(transactionId)
				.build();
	
		
		String processorResponseString = null;
		processorResponseString = postForObject(request, response, context, rest,
			request.getProperty(PROPERTY_BASE_URL) + "/Netaxept/Query.aspx",
			ObjectToHttpEntity.forPostFormFormParam(processorRequest),
			String.class
		);
		
		XmlMapper om = new XmlMapper();
		JsonNode root = om.readTree(processorResponseString);
		
		JsonNode errorNode = root.findPath("Error");
		if (!errorNode.isMissingNode()) {
			JsonNode operationNode = errorNode.findPath("Operation");
			JsonNode responseCodeNode = errorNode.findPath("ResponseCode");
			JsonNode responseSourceNode = errorNode.findPath("ResponseSource");
			JsonNode responseTextNode = errorNode.findPath("ResponseText");
			
			response.setMessage(responseTextNode.textValue() +" "+ responseSourceNode.textValue() +" "+ operationNode.textValue() + " " + responseCodeNode.textValue() + " ");
			buildRawResponseLog(response, processorResponseString);
		}
		
		JsonNode summaryNode = root.findPath("Summary");
		if (!summaryNode.isMissingNode()) {
			JsonNode authorizedNode = summaryNode.findPath("Authorized");
			JsonNode annulledNode = summaryNode.findPath("Annulled");
			JsonNode amountCapturedNode = summaryNode.findPath("AmountCaptured");
			JsonNode amountCreditedNode = summaryNode.findPath("AmountCredited");
			
			buildRawResponseLog(response, processorResponseString);
			
			if (!amountCreditedNode.isMissingNode() && !amountCreditedNode.textValue().contentEquals("0")) {
				log.error("Captured amount was credited after the original transaction. "  + processorResponseString);
				return DoProcessorResponseStatus.DECLINED;
			}
			
			if (!annulledNode.isMissingNode() && annulledNode.textValue().contentEquals("true")) {
				log.error("The transaction was annulled. " + processorResponseString);
				return DoProcessorResponseStatus.DECLINED;
			}
			
			if (!authorizedNode.isMissingNode() && authorizedNode.textValue().contentEquals("true")) {
				return DoProcessorResponseStatus.SUCCESS;
			}
		}
		
		log.error("Query request returned unexpected result. " + processorResponseString);
		buildRawResponseLog(response, processorResponseString);
		return DoProcessorResponseStatus.DECLINED;
	}

}
