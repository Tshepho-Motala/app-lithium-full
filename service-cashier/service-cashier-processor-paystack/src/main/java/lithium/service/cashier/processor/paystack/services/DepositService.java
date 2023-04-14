package lithium.service.cashier.processor.paystack.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.enums.CashierPaymentType;
import lithium.service.cashier.processor.paystack.api.schema.CustomField;
import lithium.service.cashier.processor.paystack.api.schema.Metadata;
import lithium.service.cashier.processor.paystack.api.schema.PaystackUSSDChargeRequest;
import lithium.service.cashier.processor.paystack.api.schema.PaystackUSSDChargeResponse;
import lithium.service.cashier.processor.paystack.api.schema.Ussd;
import lithium.service.cashier.processor.paystack.api.schema.UssdDepositChargeRequestMetadata;
import lithium.service.cashier.processor.paystack.api.schema.UssdTransaction;
import lithium.service.cashier.processor.paystack.api.schema.WebhookDepositRequestData;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackAuthorizationRequest;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackAuthorizationResponse;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackInitializeTransactionRequest;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackInitializeTransactionResponse;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackResponse;
import lithium.service.cashier.processor.paystack.exeptions.PaystackServiceHttpErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.checkFinalizedAndStatus;
import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.getPaystackMessageFromBody;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;

@Slf4j
@Service
public class DepositService {

    @Autowired
    LithiumConfigurationProperties config;
    @Autowired
    CashierDoCallbackService cashier;
    @Autowired
    CashierInternalClientService cashierService;
    @Autowired
    DepositVerifyService verifyService;
    @Autowired
    ObjectMapper mapper;

    private String getEmail(DoProcessorRequest request) throws Exception {
        DoProcessorRequestUser user = request.getUser();
        String email = user.getEmail();
        try {
            if (email == null || email.trim().isEmpty()) {
                String dummyEmail = request.getProperty("dummy_email");
                if (dummyEmail != null && !dummyEmail.trim().isEmpty()) {
                    email = request.getProperty("dummy_email");
                    if (user.getCellphoneNumber() != null) {
                        email = user.getCellphoneNumber() + email;
                    } else if (user.getUsername() != null) {
                        email = user.getUsername();
                    }
                }
                log.debug("User " + user.getGuid() + " email address empty. Replaced with " + email + " .TransactionId=" + request.getTransactionId());
            }
        } catch (Exception e) {
	        log.error("Email parsing failed for user " + user.toString() + " .TransactionId=" + request.getTransactionId(), e);
        }
        return email;
    }

    public DoProcessorResponseStatus initiateUssdDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        CustomField customField = CustomField.builder()
                .displayName("Trigger Method")
                .value("ussd")
                .variableName("trigger_method")
                .build();

        List<CustomField> customFields = new ArrayList<>();
        customFields.add(customField);


	    PaystackUSSDChargeRequest chargeRequest = PaystackUSSDChargeRequest.builder()
			    .reference(request.getTransactionId().toString())
			    .amount(Math.toIntExact(request.inputAmountCents()))
			    .email(getEmail(request))
			    .ussd(Ussd.builder()
					    .type(request.stageInputData(1, "bank_code"))
					    .build())
			    .metadata(UssdDepositChargeRequestMetadata.builder()
					    .customFields(customFields)
					    .build())
			    .build();

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	    headers.add("Authorization", "Bearer " + request.getProperty("secret_key"));
	    headers.add("content-type", "application/json");
	    headers.add("User-Agent", "Paystack-Developers-Hub");
	    HttpEntity<PaystackUSSDChargeRequest> entity = new HttpEntity<>(chargeRequest, headers);

	    response.addRawRequestLog(objectToPrettyString(chargeRequest));

	    ResponseEntity<String> exchange = rest.exchange(request.getProperty("ussd_charges_api_url"), HttpMethod.POST, entity, String.class, new HashMap<>());

	    response.addRawResponseLog("Initiate Paystack Ussd deposit : " + httpEntityToPrettyString(exchange));

	    PaystackUSSDChargeResponse fwResponse = mapper.readValue(exchange.getBody(), PaystackUSSDChargeResponse.class);

	    if (!exchange.getStatusCode().is2xxSuccessful()) {
		    log.error("Paystack ussd charges call failed. Transaction id=" + request.getTransactionId() + " (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());

		    String errorMessage = getPaystackMessageFromBody(mapper, exchange.getBody());
		    UssdTransaction fwResponseData = fwResponse.getData();
		    if (fwResponseData != null) {
			    errorMessage = fwResponse.getData().getMessage();
		    }
		    throw new PaystackServiceHttpErrorException(errorMessage, exchange.getStatusCodeValue());
	    }
	    log.info("Got ussd charges response: " + exchange.getBody() + " .TransactionId=" + request.getTransactionId());

	    response.setPaymentType(CashierPaymentType.USSD.toString().toLowerCase());

	    if (fwResponse.isStatus()) {
		    response.setOutputData(1, "ussd", fwResponse.getData().getUssdCode());
		    response.setProcessorReference(request.getTransactionId().toString());
		    response.setOutputData(1, "skipVerify", "true");
		    return DoProcessorResponseStatus.NEXTSTAGE;
	    } else {
		    String declineReason = fwResponse.getMessage();
		    UssdTransaction fwResponseData = fwResponse.getData();
		    if (fwResponseData != null) {
			    declineReason = fwResponseData.getMessage();
		    }
		    log.error("Paystack transaction id=" + request.getTransactionId() + " failed. Message=" + declineReason);
		    response.setDeclineReason(declineReason);
		    return DoProcessorResponseStatus.DECLINED;
	    }
    }
    public DoProcessorResponseStatus recurringWebDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        UserCard userCard = cashierService.getUserCard(request.stageInputData(1, "cardReference"), request.getUser().getRealGuid());

	    PaystackAuthorizationRequest authorizationRequest = PaystackAuthorizationRequest.builder()
			    .amount(request.processorCommunicationAmount().movePointRight(2).toBigInteger().toString())
			    .currency(request.getUser().getCurrency())
			    .reference(request.getTransactionId().toString())
			    .email(getEmail(request))
			    .authorizationCode(userCard.getProviderData())
			    .metadata(Metadata.builder()
					    .customFields(new ArrayList<>()).build())
			    .build();

	    log.info("Paystack init recurring web deposit request" + authorizationRequest + " .TransactionId=" + request.getTransactionId());

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	    headers.add("Authorization", "Bearer " + request.getProperty("secret_key"));
	    headers.add("content-type", "application/json");
	    headers.add("User-Agent", "Paystack-Developers-Hub");

	    HttpEntity<PaystackAuthorizationRequest> entity = new HttpEntity<>(authorizationRequest, headers);
	    response.setPaymentType("card");
	    response.addRawRequestLog(objectToPrettyString(authorizationRequest));
	    ResponseEntity<String> exchange = null;
	    try {
		    exchange = rest.exchange(request.getProperty("authorization_url"), HttpMethod.POST, entity, String.class, new HashMap<>());
		    response.addRawResponseLog("Paystack Recurring Web Deposit : " + objectToPrettyString(exchange.getBody()));
	    } catch (ResourceAccessException e) {
		    if (e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof IOException && e.getCause().getMessage().contains("code: 504")) {
			    response.addRawResponseLog("GATEWAY_TIMEOUT");
			    response.setProcessorReference(request.getTransactionId().toString());
			    response.setOutputData(1, "paystack_timeout", "true");
			    log.error("TIMEOUT on paystack charge_authorization API. TransactionId: " + request.getTransactionId() + " Exception: " + e.getMessage(), e);
			    return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
		    }
	        throw e;
        }

	    PaystackResponse<PaystackAuthorizationResponse> authorizationResponse = mapper.readValue(exchange.getBody(), new TypeReference<PaystackResponse<PaystackAuthorizationResponse>>() {
	    });
	    PaystackAuthorizationResponse authorizationResponseData = authorizationResponse.getData();

	    if (exchange.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
		    //paystack still waiting for the response from provider
		    response.addRawResponseLog("GATEWAY_TIMEOUT");
		    response.setProcessorReference(request.getTransactionId().toString());
		    response.setOutputData(1, "paystack_timeout", "true");
		    return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
	    } else if (!exchange.getStatusCode().is2xxSuccessful()) {
		    log.error("Paystack init transaction id=" + request.getTransactionId() + " recurring web deposit call failed (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
		    String errorMessage = getPaystackMessageFromBody(mapper, exchange.getBody());
		    if (authorizationResponseData != null) {
			    errorMessage = authorizationResponseData.getGateway_response();
		    }
		    throw new PaystackServiceHttpErrorException(errorMessage, exchange.getStatusCodeValue());
	    }
	    log.info("Paystack init transaction id=" + request.getTransactionId() + " recurring web deposit response " + exchange.getBody());

	    response.setProcessorReference(authorizationResponse.getData().getReference());

	    DoProcessorResponseStatus status = DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;

	    if (authorizationResponseData.getStatus().toLowerCase().equals("success")) {
		    status = DoProcessorResponseStatus.NEXTSTAGE;
	    } else if (authorizationResponseData.getStatus().toLowerCase().equals("failed")) {
		    String declineReason = authorizationResponseData.getGateway_response() == null ? authorizationResponse.getData().getMessage() : authorizationResponseData.getGateway_response();
		    log.error("Paystack transaction id=" + request.getTransactionId() + " failed. Message=" + declineReason);
		    response.setDeclineReason(declineReason);
		    status = DoProcessorResponseStatus.DECLINED;
	    }
	    return status;
    }

    public DoProcessorResponseStatus initiateWebDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        PaystackInitializeTransactionRequest.PaystackInitializeTransactionRequestBuilder requestBuilder = PaystackInitializeTransactionRequest.builder();

        List<CustomField> customFields = new ArrayList<>(Arrays.asList(
                CustomField.builder()
                        .displayName(request.getUser().getFirstName())
                        .variableName("msisdn")
                        .value(request.getUser().getCellphoneNumber())
                        .build(),
                CustomField.builder()
                        .displayName("Trigger Method")
                        .variableName("trigger_method")
                        .value("widget")
                        .build()
        ));

        Metadata.MetadataBuilder metadataBuilder = Metadata.builder();
        metadataBuilder.customFields(customFields);

        if (request.stageInputData(1).get("cancel_url") != null && !request.stageInputData(1).get("cancel_url").isEmpty()) {
            String cancelRedirectUrl = config.getGatewayPublicUrl() + "/service-cashier-processor-paystack/public/playercancel/" + request.getTransactionId();
            metadataBuilder.cancelUrl(cancelRedirectUrl);
        }

	    requestBuilder
			    .amount(request.processorCommunicationAmount().movePointRight(2).toBigInteger().toString())
			    .reference(request.getTransactionId().toString())
			    .email(getEmail(request))
			    .callbackUrl(config.getGatewayPublicUrl() + "/service-cashier-processor-paystack/public/redirectreturn")
			    .metadata(metadataBuilder.build());

	    PaystackInitializeTransactionRequest initializeTransactionRequest = requestBuilder.build();
	    log.info("Paystack init web deposit request" + initializeTransactionRequest + " .TransactionId=" + request.getTransactionId());

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	    headers.add("Authorization", "Bearer " + request.getProperty("secret_key"));
	    headers.add("content-type", "application/json");
	    headers.add("User-Agent", "Paystack-Developers-Hub");

	    HttpEntity<PaystackInitializeTransactionRequest> entity = new HttpEntity<>(initializeTransactionRequest, headers);

	    response.addRawRequestLog(objectToPrettyString(initializeTransactionRequest));

	    ResponseEntity<String> exchange = rest.exchange(request.getProperty("init_deposit_url"), HttpMethod.POST, entity, String.class, new HashMap<>());

	    response.addRawResponseLog("Paystack init web deposit : " + httpEntityToPrettyString(exchange));

	    PaystackInitializeTransactionResponse initializeTransactionResponse = mapper.readValue(exchange.getBody(), PaystackInitializeTransactionResponse.class);

	    if (!exchange.getStatusCode().is2xxSuccessful()) {
		    log.error("Paystack init transaction id=" + request.getTransactionId() + " web deposit call failed (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
		    throw new PaystackServiceHttpErrorException(getPaystackMessageFromBody(mapper, exchange.getBody()), exchange.getStatusCodeValue());
	    }

	    log.info("Paystack init transaction id=" + request.getTransactionId() + " web deposit response " + exchange.getBody());

	    response.setIframeUrl(initializeTransactionResponse.getData().getAuthorizationUrl());
	    response.setProcessorReference(initializeTransactionResponse.getData().getReference());

	    return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
    }

	public String processRedirect(String reference) throws Exception {
		Long transactionId = tryParseLong(reference);

		Response<DoProcessorRequest> transactionResponse = (transactionId != null)
				? cashier.doCallbackGetTransaction(transactionId, "paystack")
				: cashier.doCallbackGetTransactionFromProcessorReference(reference, "paystack");

		DoProcessorRequest request = transactionResponse.getData();

		if (!transactionResponse.isSuccessful()) {
			throw new Exception("Failed to get transaction for reference: " + reference);
		}

		DoProcessorResponse response = DoProcessorResponse.builder()
				.transactionId(request.getTransactionId())
				.processorReference(request.getProcessorReference())
				.rawResponseLog("Received redirect return: " + reference)
				.build();
		try {
			DoProcessorResponseStatus status = verifyService.verify(request, response, true);
			return request.stageInputData(1).get("return_url") + "?status=" + status.toString()
					+ "&reference=" + reference;
		} finally {
			checkFinalizedAndStatus(request, response);
			cashier.doSafeCallback(response);
		}
	}

	public void processWidgetWebhook(WebhookDepositRequestData data) throws Exception {
		Long transactionId = tryParseLong(data.getReference());

		Response<DoProcessorRequest> transactionResponse = (transactionId != null)
				? cashier.doCallbackGetTransaction(transactionId, "paystack")
				: cashier.doCallbackGetTransactionFromProcessorReference(data.getReference(), "paystack");

		DoProcessorRequest cashierTransaction = transactionResponse.getData();

		if (!transactionResponse.isSuccessful()) {
			throw new Exception("Failed to get transaction for reference: " + data.getReference());
		}

		DoProcessorResponse response = getDoProcessorResponse(data, cashierTransaction);

		//in case of card reuse and no timeout verification is done synchronously on the state 2
		if (cashierTransaction.stageInputData(1).get("cardReference") != null && !cashierTransaction.stageOutputData(1).containsKey("paystack_timeout")) {
			return;
		}

		try {
			verifyService.verify(cashierTransaction, response, false);
		} finally {
			checkFinalizedAndStatus(cashierTransaction, response);
			cashier.doSafeCallback(response);
		}

	}

	public void processUSSDWebhook(WebhookDepositRequestData data) throws Exception {
		DoProcessorRequest cashierTransaction = cashier.doCallbackGetTransaction(Long.parseLong(data.getReference()), "paystack").getData();
		DoProcessorResponse response = getDoProcessorResponse(data, cashierTransaction);
		try {
			verifyService.verifyUssdRequest(cashierTransaction, response);
		} finally {
			checkFinalizedAndStatus(cashierTransaction, response);
			cashier.doSafeCallback(response);
		}
	}

	private DoProcessorResponse getDoProcessorResponse(WebhookDepositRequestData data, DoProcessorRequest cashierTransaction) {
		DoProcessorResponse processorResponse = DoProcessorResponse.builder()
				.transactionId(cashierTransaction.getTransactionId())
				.processorReference(cashierTransaction.getProcessorReference())
				.rawRequestLog("Received webhook call: " + objectToPrettyString(data))
				.build();
		CashierPaymentType paymenttype = CashierPaymentType.fromDescription(data.getChannel());
		if (paymenttype != null) {
			processorResponse.setPaymentType(paymenttype.toString().toLowerCase());
        }
        return processorResponse;
    }

    public String processPlayerCancelTransaction(Long cashierTransactionId) throws Exception {
	    //transaction status will not be changed PLAT-2081
        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(cashierTransactionId)
                .rawResponseLog("Received redirect return. Status: Cancel TransactionId: " + cashierTransactionId)
                .build();
        response.setOutputData(2, "processor_redirect_status", "cancel");
        response.addRawRequestLog("Transaction " + cashierTransactionId + " canceled by player");
        cashier.doSafeCallback(response);
        DoProcessorRequest request = cashier.getTransaction(cashierTransactionId, "paystack");
        return request.stageInputData(1).get("cancel_url");
    }

    private Long tryParseLong(String value) {
        Long retVal;
        try {
            retVal = Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            retVal = null;
        }
        return retVal;
    }
}
