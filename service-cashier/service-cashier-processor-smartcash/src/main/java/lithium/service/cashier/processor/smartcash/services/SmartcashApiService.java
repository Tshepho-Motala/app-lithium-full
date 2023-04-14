package lithium.service.cashier.processor.smartcash.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.smartcash.SmartcashEncryptor;
import lithium.service.cashier.processor.smartcash.data.Payer;
import lithium.service.cashier.processor.smartcash.data.SmartcashConnectionError;
import lithium.service.cashier.processor.smartcash.data.SmartcashResponseStatus;
import lithium.service.cashier.processor.smartcash.data.SmartcashAuthorizationRequest;
import lithium.service.cashier.processor.smartcash.data.SmartcashAuthorizationResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashCallbackData;
import lithium.service.cashier.processor.smartcash.data.SmartcashCustomerSearchResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentRequest;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPayoutRequest;
import lithium.service.cashier.processor.smartcash.data.TransactionRequestData;
import lithium.service.cashier.processor.smartcash.data.TransactionResponseData;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashApiUrls;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashResponseCodes;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashTransactionStatus;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashAuthorizationException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashCustomerSearchException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashInvalidMobileException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashInvalidSignatureException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashPaymentException;
import lithium.service.cashier.processor.smartcash.exceptions.SmartcashVerifyTransactionException;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;

import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Slf4j
@Service
public class SmartcashApiService {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    protected CashierDoCallbackService callbackService;
    @Autowired
    CashierInternalClientService cashierService;
    @Autowired
    protected MessageSource messageSource;
    @Autowired
    protected RestTemplate restTemplate;

    private static final String TRANSFER_TYPE = "WALLET";
    private static final String AUTHENTICATION_MEDIUM = "USSD_PUSH";
    private static final String GRANT_TYPE = "client_credentials";

    private static final int TRANSACTION_ID_MIN_LENGTH = 5;


    public SmartcashAuthorizationResponse getAuthorizationToken(DoProcessorRequest request, DoProcessorResponse response) throws SmartcashException {
       try {
            SmartcashAuthorizationRequest authorizationRequest = SmartcashAuthorizationRequest.builder()
                .clientId(request.getProperty("client_id"))
                .clientSecret(request.getProperty("secret_key"))
                .grantType(GRANT_TYPE)
                .build();

            String requestBody = mapper.writeValueAsString(authorizationRequest);
            log.info("Smartcash authorization is requested for transactionid: " + request.getTransactionId());
            response.addRawRequestLog("Smartcash authorization is requested");
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> exchange = restTemplate.exchange(request.getProperty("api_url") + SmartcashApiUrls.AUTH_URL.getUrl(), HttpMethod.POST, entity, String.class, new HashMap<String,String>());
            log.info("Smartcash authorization response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody() + " userGuid: " + request.getUser().getRealGuid() + "transactionid: " + request.getTransactionId());
            response.addRawResponseLog("Smartcash authorization response " + httpEntityToPrettyString(exchange));

            return mapper.readValue(exchange.getBody(), SmartcashAuthorizationResponse.class);
        } catch (RestClientResponseException restException) {
           throw getSmartcashExceptions(request, response, restException, "authorization");
        } catch (Exception e) {
            log.error("Smartcash authorization is failed. Exception: " + e.getMessage(), e);
           response.addRawResponseLog("Smartcash authorization is failed. Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new SmartcashAuthorizationException("Smartcash authorization is failed.");
        }
    }

    public SmartcashCustomerSearchResponse getCustomerInfo(DoProcessorRequest request, DoProcessorResponse response, String token, String msisdn) throws SmartcashException {
        try {
            HttpEntity<?> entity = new HttpEntity<>(getHeaders(token, request.getUser().getCountryCode(), request.getUser().getCurrency()));
            log.info("Smartcash customer search is requested for msisdn = " + msisdn + " userGuid: " + request.getUser().getRealGuid() + "transactionid: " + request.getTransactionId());
            response.addRawRequestLog("Smartcash customer search is requested for msisdn = " + msisdn + " Country code: " + request.getUser().getCountryCode() + " Currency: " + request.getUser().getCurrency());

            ResponseEntity<String> exchange = restTemplate.exchange(request.getProperty("api_url") + (request.getTransactionType() == TransactionType.DEPOSIT ? SmartcashApiUrls.DEPOSIT_CUSTOMER_SEARCH_URL.getUrl() : SmartcashApiUrls.WITHDRAW_CUSTOMER_SEARCH_URL.getUrl()), HttpMethod.GET, entity, String.class, msisdn);
            log.info("Smartcash customer search response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody() + " userGuid: " + request.getUser().getRealGuid() + "transactionid: " + request.getTransactionId());
            response.addRawResponseLog("Smartcash customer search response: " + httpEntityToPrettyString(exchange));

            return mapper.readValue(exchange.getBody(), SmartcashCustomerSearchResponse.class);
        } catch (RestClientResponseException restException) {
            throw getSmartcashExceptions(request, response, restException, "customer search");
        } catch (Exception e) {
            log.error("Smartcash customer search is failed. Exception: " + e.getMessage(), e);
            response.addRawResponseLog("Smartcash customer search is failed. Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new SmartcashCustomerSearchException("Customer search is failed.");
        }
    }

    private MultiValueMap<String, String> getHeaders(String token, String countryCode, String currency) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", "Bearer " + token);
        headers.add("x-country-code", countryCode);
        headers.add("x-currency-code", currency);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public SmartcashPaymentResponse payment(DoProcessorRequest request, DoProcessorResponse response, String token, String msisdn, String walletId) throws SmartcashException {
        try {
            SmartcashPaymentRequest paymentRequest = SmartcashPaymentRequest.builder()
                .payer(Payer.builder()
                    .walletId(walletId)
                    .msisdn(msisdn)
                    .transferType(TRANSFER_TYPE)
                    .build())
                .reference(request.getProperty("reference"))
                .authenticationMedium(AUTHENTICATION_MEDIUM)
                .transaction(TransactionRequestData.builder()
                    .id(padTransactionId(request.getTransactionId()))
                    .amount(request.inputAmount().toBigInteger().toString())
                    .build())
                .build();

            String requestBody = mapper.writeValueAsString(paymentRequest);
            log.info("Smartcash payment request: " + requestBody);
            response.addRawRequestLog("Smartcash payment request: " + jsonObjectToPrettyString(requestBody));
            HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders(token, request.getUser().getCountryCode(), request.getUser().getCurrency()));

            ResponseEntity<String> exchange = restTemplate.exchange(request.getProperty("api_url") + SmartcashApiUrls.DEPOSIT_URL.getUrl(), HttpMethod.POST, entity, String.class, new HashMap<String,String>());

            log.error("Smartcash payment response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody() + " transactionid: " + request.getTransactionId());
            response.addRawResponseLog("Smartcash payment response: " + httpEntityToPrettyString(exchange));

            return mapper.readValue(exchange.getBody(), SmartcashPaymentResponse.class);
        } catch (RestClientResponseException restException) {
            throw getSmartcashExceptions(request, response, restException, "payment");
        } catch (Exception e) {
            log.error("Smartcash payment is failed. Exception: " + e.getMessage(), e);
            response.addRawResponseLog("Smartcash payment is failed. Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new SmartcashPaymentException("Smartcash payment is failed.");
        }
    }

    public SmartcashPaymentResponse payout(DoProcessorRequest request, DoProcessorResponse response, String token, String msisdn, String walletId) throws SmartcashException {
        try {
            SmartcashPayoutRequest payoutRequest = SmartcashPayoutRequest.builder()
                .payee(Payer.builder()
                    .walletId(walletId)
                    .msisdn(msisdn)
                    .transferType(TRANSFER_TYPE)
                    .build())
                .reference(request.getProperty("reference"))
                .pin(SmartcashEncryptor.encryptPin(request.getProperty("pin"), request.getProperty("public_key")))
                .transaction(TransactionRequestData.builder()
                    .id(padTransactionId(request.getTransactionId()))
                    .amount(request.inputAmount().toBigInteger().toString())
                    .build())
                .build();

            String requestBody = mapper.writeValueAsString(payoutRequest);
            log.info("Smartcash payout request: " + requestBody);
            response.addRawRequestLog("Smartcash payout request: " + requestBody);
            HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders(token, request.getUser().getCountryCode(), request.getUser().getCurrency()));

            ResponseEntity<String> exchange = restTemplate.exchange(request.getProperty("api_url") + SmartcashApiUrls.WITHDRAW_URL.getUrl(), HttpMethod.POST, entity, String.class, new HashMap<String,String>());

            log.error("Smartcash payout response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody() + " transactionid: " + request.getTransactionId());
            response.addRawResponseLog("Smartcash payout response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody());

            return mapper.readValue(exchange.getBody(), SmartcashPaymentResponse.class);
        } catch (RestClientResponseException restException) {
            throw getSmartcashExceptions(request, response, restException, "payout");
        } catch (Exception e) {
            log.error("Smartcash payout is failed. Exception: " + e.getMessage(), e);
            response.addRawResponseLog("Smartcash payout is failed. Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new SmartcashPaymentException("Smartcash payout is failed.");
        }
    }

    public SmartcashPaymentResponse getTransaction(DoProcessorRequest request, DoProcessorResponse response, String token) throws SmartcashException {
        try {
            HttpEntity<?> entity = new HttpEntity<>(getHeaders(token, request.getUser().getCountryCode(), request.getUser().getCurrency()));
            log.info("Smartcash get transaction is requested for userGuid: " + request.getUser().getRealGuid() + "transactionid: " + request.getTransactionId());

            ResponseEntity<String> exchange = restTemplate.exchange(request.getProperty("api_url") + (request.getTransactionType() == TransactionType.DEPOSIT ? SmartcashApiUrls.DEPOSIT_GET_TRANSACTION_URL.getUrl() : SmartcashApiUrls.WITHDRAW_GET_TRANSACTION_URL.getUrl()), HttpMethod.GET, entity, String.class, padTransactionId(request.getTransactionId()));
            log.error("Smartcash get transaction response httpStatusCode: " + exchange.getStatusCode() + " body: " + exchange.getBody() + " transactionid: " + request.getTransactionId());
            response.addRawResponseLog("Smartcash get transaction response httpStatusCode: " + exchange.getStatusCode() + " body: " + jsonObjectToPrettyString(exchange.getBody()));

            return mapper.readValue(exchange.getBody(), SmartcashPaymentResponse.class);
        } catch (RestClientResponseException restException) {
            throw getSmartcashExceptions(request, response, restException, "get transaction");
        } catch (Exception e) {
            log.error("Smartcash get transaction is failed. Exception: " + e.getMessage(), e);
            response.addRawResponseLog("Smartcash get transaction is failed. Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new SmartcashPaymentException("Smartcash get transaction is failed.");
        }
    }

    private SmartcashException getSmartcashExceptions(DoProcessorRequest request, DoProcessorResponse response, RestClientResponseException restException, String method) {
        log.error("Smartcash " + method + " response httpStatusCode: " + restException.getRawStatusCode() + " body: " + restException.getResponseBodyAsString() + " transactionid: " + request.getTransactionId(), restException);
        response.addRawResponseLog("Smartcash " + method + " response httpStatusCode: " + restException.getRawStatusCode() + " body: " + restException.getResponseBodyAsString());
        SmartcashException smartcashException = new SmartcashException(restException.getRawStatusCode() + ": " + restException.getStatusText());
        if (!StringUtil.isEmpty(restException.getResponseBodyAsString())) {
            try {
                SmartcashConnectionError smartcashConnectionError = mapper.readValue(restException.getResponseBodyAsString(), SmartcashConnectionError.class);
                smartcashException = new SmartcashException(smartcashConnectionError.getDescription());
            } catch (Exception ex) {
                log.warn("Failed to get Smartcash error details from response httpStatusCode: " + restException.getRawStatusCode() + " body: " + restException.getResponseBodyAsString() + " transactionid: " + request.getTransactionId());
            }
        }
        return smartcashException;
    }
    public void handlePaymentWebhook(String webhookData) throws Exception {
        SmartcashCallbackData callbackData = mapper.readValue(webhookData, SmartcashCallbackData.class);
        Long transactionId = Long.parseLong(callbackData.getTransaction().getTransactionId());

        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

        DoProcessorRequest processorRequest = processorRequestResponse.getData();

        checkSignature(webhookData, callbackData.getHash(), processorRequest.getProperty("hash_key"));

        DoProcessorResponse processorResponse = DoProcessorResponse.builder()
            .transactionId(processorRequest.getTransactionId())
            .build();
        processorResponse.addRawResponseLog("Received Smartcash webhook: " + jsonObjectToPrettyString(webhookData));

        try {
            DoProcessorResponseStatus status = verifyTransaction(processorRequest, processorResponse);
            if (!processorRequest.isTransactionFinalized()) {
                processorResponse.setStatus(status);
            }
        } finally {
            callbackService.doSafeCallback(processorResponse);
        }
    }

    protected Response<DoProcessorRequest> getCallbackGetTransaction(Long transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "smartcash");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transaction with id: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        return processorRequestResponse;
    }

    public DoProcessorResponseStatus verifyTransaction(DoProcessorRequest request, DoProcessorResponse response) throws SmartcashVerifyTransactionException {
        try {
            SmartcashAuthorizationResponse authResponse = getAuthorizationToken(request, response);
            SmartcashPaymentResponse paymentResponse = getTransaction(request,response, authResponse.getAccessToken());

            SmartcashResponseStatus paymentStatus = paymentResponse.getStatus();
            SmartcashResponseCodes responseCode = SmartcashResponseCodes.fromResponseCode(paymentStatus.getResponseCode());

            if (!paymentStatus.isSuccess()) {
                if (responseCode == SmartcashResponseCodes.COLLECTION_BAD_REQUEST || responseCode == SmartcashResponseCodes.DISBURSEMENT_BAD_REQUEST) {
                    response.setErrorCode(responseCode.getGeneralError().getCode());
                    response.setMessage(responseCode.getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.EXPIRED;
                } else {
                    response.setDeclineReason(paymentStatus.getCode() + ": " + paymentStatus.getMessage());
                    response.setErrorCode(responseCode.getGeneralError().getCode());
                    response.setMessage(responseCode.getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.DECLINED;
                }
            }

            TransactionResponseData smartcashTransaction = paymentResponse.getData().getTransaction();
            SmartcashTransactionStatus transactionStatus = SmartcashTransactionStatus.fromCode(smartcashTransaction.getStatus());
            if (transactionStatus == SmartcashTransactionStatus.TS) {
                return DoProcessorResponseStatus.SUCCESS;
            } else if (transactionStatus == SmartcashTransactionStatus.TIP) {
                return DoProcessorResponseStatus.NOOP;
            } else {
                response.setDeclineReason(smartcashTransaction.getMessage());
                response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
                response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                return DoProcessorResponseStatus.DECLINED;
            }
        } catch (Exception e) {
            log.error("Failed to validate Smartcash payment. Exception: " + e.getMessage(), e);
            throw new SmartcashVerifyTransactionException("Failed to verify smartcash payment.");
        }
    }

    public String getCustomerWallet(DoProcessorRequest request, DoProcessorResponse response, String token, String msisdn) throws SmartcashException {
        SmartcashCustomerSearchResponse customerSearchResponse = getCustomerInfo(request, response, token, msisdn);
        SmartcashResponseStatus customerSearchStatus = customerSearchResponse.getStatus();

        try {
            String walletId = customerSearchResponse.getData().getInstruments()[0].getWalletId();
            response.setProcessorAccount(ProcessorAccount.builder()
                .reference(walletId)
                .status(PaymentMethodStatusType.ACTIVE)
                .type(ProcessorAccountType.USSD)
                .descriptor(msisdn)
                .name(customerSearchResponse.getData().getFirstName() + " " +  customerSearchResponse.getData().getLastName())
                .hideInDeposit(true)
                .data(new HashMap<String, String>() {{
                    put("name", customerSearchResponse.getData().getFirstName() + " " +  customerSearchResponse.getData().getLastName());
                    put("walletId", walletId);
                    put("msisdn", msisdn);
                }})
                .build());
            return walletId;
        } catch (Exception e) {
            log.error("Failed to find Smartcash customer by mobile number: " + request.getUser().getTelephoneNumber() + " TransactionId: " + request.getTransactionId());
            throw new SmartcashCustomerSearchException("Customer not found.");
        }
    }

    public String padTransactionId(Long transactionId) {
        return Strings.padStart(transactionId.toString(), TRANSACTION_ID_MIN_LENGTH, '0');
    }

    public String msisdnFromMobile(String mobileNumber, int msisdnLength) throws SmartcashInvalidMobileException {
        if (StringUtil.isEmpty(mobileNumber) || mobileNumber.length() < msisdnLength) {
            throw new SmartcashInvalidMobileException("Invalid mobile number.");
        }
        return mobileNumber.substring(mobileNumber.length() - msisdnLength);
    }

    private void checkSignature(String callbackData, String hash, String hashKey) throws Exception {
        JsonNode root = mapper.readTree(callbackData);
        ((ObjectNode)root).remove("hash");
        if (!hash.equals(SmartcashEncryptor.encryptCallback(root.toString(), hashKey))) {
            throw new SmartcashInvalidSignatureException("Signature check is failed for webhook request: " + callbackData);
        }
    }
}


