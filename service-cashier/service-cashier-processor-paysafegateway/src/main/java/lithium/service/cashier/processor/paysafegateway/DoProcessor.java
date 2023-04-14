package lithium.service.cashier.processor.paysafegateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.paysafegateway.DoProcessorPaysafeGatewayAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.paysafegateway.data.Advice;
import lithium.service.cashier.processor.paysafegateway.data.BillingDetails;
import lithium.service.cashier.processor.paysafegateway.data.Card;
import lithium.service.cashier.processor.paysafegateway.data.CardPaymentRequest;
import lithium.service.cashier.processor.paysafegateway.data.CardPaymentResponse;
import lithium.service.cashier.processor.paysafegateway.data.IframeError;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDEnrollmentRequest;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDEnrollmentResult;
import lithium.service.cashier.processor.paysafegateway.data.VerificationRequest;
import lithium.service.cashier.processor.paysafegateway.data.VerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class DoProcessor extends DoProcessorPaysafeGatewayAdapter {

	@Autowired
	private LiabilityShiftMatrixService liabilityShiftMatrixService;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest){
		try {
			String redirectTo = request.getProperty("paymentFormUrl");
			Map<String, String> queryParams = new LinkedHashMap<>();
			queryParams.put("env", request.getProperty("environment"));
			queryParams.put("sak", base64Hash(request.getProperty("suTokenApiUn"), request.getProperty("suTokenApiPswd")));
			queryParams.put("aid", request.getProperty("accountId"));
			queryParams.put("3ds", request.getProperty("useThreeDSecure"));
			queryParams.put("3dsv2", request.getProperty("useThreeDSecureVersion2"));
			queryParams.put("amt", request.processorCommunicationAmount().movePointRight(2).toPlainString());
			queryParams.put("cur", request.getUser().getCurrency());
			queryParams.put("tranid", String.valueOf(request.getTransactionId()));
			response.setIframeUrl(redirectTo);
			response.setIframeMethod("GET");
			response.setIframePostData(queryParams);
			return DoProcessorResponseStatus.IFRAMEPOST;
		} catch (Exception e) {
			log.error("Unable to process payment for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		log.info("useThreeDSecure {}", request.getProperty("useThreeDSecure"));
		String errorJson=request.stageOutputData(1, "iframeError");
		String paymentToken=request.stageOutputData(1, "paymentToken");
		if (StringUtils.isEmpty(paymentToken) && StringUtils.isNotEmpty(errorJson)) {
			log.info("Error from iframe {}", errorJson);
			ObjectMapper objectMapper = new ObjectMapper();
			IframeError iframeError=objectMapper.readValue(errorJson, IframeError.class);
			buildRawResponseLog(response, iframeError);
			return DoProcessorResponseStatus.DECLINED;
		}
		if ("false".equalsIgnoreCase(request.getProperty("useThreeDSecure"))) {
			return DoProcessorResponseStatus.NEXTSTAGE;
		}

			String url=request.getProperty("cardPaymentApiBaseUrl")
					+ "/customervault/v1/singleusetokens/search";
			ThreeDEnrollmentRequest threeDEnrollmentRequest = ThreeDEnrollmentRequest.builder().paymentToken(request.stageOutputData(1, "paymentToken")).build();
			log.debug("ThreeDEnrollmentRequest " + threeDEnrollmentRequest);
			final HttpEntity<ThreeDEnrollmentRequest> entity = new HttpEntity<>(threeDEnrollmentRequest,
					buildHeaders(request.getProperty("apiKeyUn"), request.getProperty("apiKeyPswd")));
			ThreeDEnrollmentResult threeDEnrollmentResult = rest.exchange(url, HttpMethod.POST, entity, ThreeDEnrollmentResult.class).getBody();
			log.info("ThreeDEnrollmentResult: {}", threeDEnrollmentResult);
			buildRawResponseLog(response, threeDEnrollmentResult);
			if (threeDEnrollmentResult != null) {
				Advice advice = liabilityShiftMatrixService.consult(threeDEnrollmentResult.getCard());
				if (advice == Advice.PROCEED) {
					buildRawResponseLog(response, "LIABILITY SHIFT ADVICE : " + advice);
					response.setOutputData(2,"paymentToken",request.stageOutputData(1, "paymentToken"));
					response.setOutputData(2,"liabilityShiftAdvice",advice.toString());
					return DoProcessorResponseStatus.NEXTSTAGE;
				} else {
					response.setOutputData(2,"paymentToken", request.stageOutputData(1, "paymentToken"));
					response.setOutputData(2,"liabilityShiftAdvice", advice.toString());
					buildRawResponseLog(response, "LIABILITY SHIFT ADVICE: " + advice);
					response.setProcessorReference(threeDEnrollmentResult.getId());
					return DoProcessorResponseStatus.DECLINED;
				}
			}
			return DoProcessorResponseStatus.DECLINED;
		} catch (Exception e) {
			log.error("Unable to process payment for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus validateDepositStage3(DoProcessorRequest request, DoProcessorResponse response) {
		try {
			RestTemplate rest = new RestTemplate();
			rest.setErrorHandler(new DefaultResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
					return false;
				}
			});
			VerificationRequest verificationRequest = VerificationRequest.builder()
					.card(Card.builder().paymentToken(request.stageOutputData(1, "paymentToken")).build())
					.merchantRefNum(String.valueOf(request.getTransactionId()))
					.billingDetails(
							BillingDetails.builder()
									.street(request.getUser().getResidentialAddress().toOneLinerStreet())
									.city(request.getUser().getResidentialAddress().getCity())
									.country(request.getUser().getResidentialAddress().getCountryCode())
									.zip(request.getUser().getResidentialAddress().getPostalCode())
									.build()
					)
					.build();
			log.info("VerificationRequest {}", verificationRequest);
			final HttpEntity<VerificationRequest> entity = new HttpEntity<>(verificationRequest,
					buildHeaders(request.getProperty("apiKeyUn"), request.getProperty("apiKeyPswd")));
			VerificationResponse verificationResponse = rest.exchange(request.getProperty("cardPaymentApiBaseUrl")
							+ "/cardpayments/v1/accounts/" + request.getProperty("accountId") + "/verifications",
					HttpMethod.POST, entity, VerificationResponse.class).getBody();
			log.debug("VerificationResponse " + verificationResponse);
			buildRawResponseLog(response, verificationResponse);
			if (verificationResponse != null 
			        && verificationResponse.getStatus() != null 
					&& "COMPLETED".equalsIgnoreCase(verificationResponse.getStatus()) 
					&& request.getTransactionId() != null 
					&& verificationResponse.getMerchantRefNum() != null 
					&& request.getTransactionId().toString().equalsIgnoreCase(verificationResponse.getMerchantRefNum())) {
				return DoProcessorResponseStatus.SUCCESS;
			} else {
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			log.error("Unable to validate the paymentToken for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			CardPaymentRequest cardPaymentRequest = CardPaymentRequest.builder()
					.merchantRefNum(String.valueOf(request.getTransactionId()))
					.amount(request.processorCommunicationAmount().movePointRight(2).longValue())
					.settleWithAuth(true)
					.card(Card.builder().paymentToken(request.stageOutputData(1, "paymentToken")).build())
					.billingDetails(
							BillingDetails.builder()
									.street(request.getUser().getResidentialAddress().toOneLinerStreet())
									.city(request.getUser().getResidentialAddress().getCity())
									.country(request.getUser().getResidentialAddress().getCountryCode())
									.zip(request.getUser().getResidentialAddress().getPostalCode())
									.build()
					)
					.build();
			log.debug("CardPaymentRequest " + cardPaymentRequest);
			final HttpEntity<CardPaymentRequest> entity = new HttpEntity<>(cardPaymentRequest,
					buildHeaders(request.getProperty("apiKeyUn"), request.getProperty("apiKeyPswd")));
			CardPaymentResponse cardPaymentResponse = rest.exchange(request.getProperty("cardPaymentApiBaseUrl")
							+ "/cardpayments/v1/accounts/" + request.getProperty("accountId") + "/auths",
					HttpMethod.POST, entity, CardPaymentResponse.class).getBody();
			log.debug("CardPaymentResponse " + cardPaymentResponse);
			buildRawResponseLog(response, cardPaymentResponse);
			if ( cardPaymentResponse != null && cardPaymentResponse.getStatus() != null && "COMPLETED".equalsIgnoreCase(cardPaymentResponse.getStatus())) {
				response.setProcessorReference(cardPaymentResponse.getId());
				return DoProcessorResponseStatus.SUCCESS;
			} else {
				response.setProcessorReference(cardPaymentResponse.getId());
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			log.error("Unable to process card payment for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}

	private String base64Hash(String username, String password) {
		return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	private HttpHeaders buildHeaders(String un, String pswd) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + base64Hash(un, pswd));
		return headers;
	}
}
