package lithium.service.cashier.processor.hexopay.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.TransactionRemarkData;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.hexopay.api.gateway.ErrorResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.PaymentRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.PayoutRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AdditionData;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.data.BillingAddress;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Customer;
import lithium.service.cashier.processor.hexopay.api.gateway.TransactionResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.TransactionsResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.data.CreditCard;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Transaction;
import lithium.service.cashier.processor.hexopay.api.gateway.data.VerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.AvsVerificationCode;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.CvcVerificationCode;
import lithium.service.cashier.processor.hexopay.exceptions.HexopayInvalidSignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class HexopayGatawayApiService {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CashierDoCallbackService callbackService;
    @Autowired
    private CashierInternalClientService cashierService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LithiumConfigurationProperties lithiumProperties;
    @Value("${spring.application.name}")
    private String moduleName;

    public DoProcessorResponseStatus cardReuseDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) {
        try
        {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .request(PaymentRequest.Request.builder()
                        .test(Boolean.parseBoolean(request.getProperty("test")))
                        .currency(request.getUser().getCurrency())
                        .amount(request.inputAmountCents())
                        .description(request.getProperty("payment_description"))
                        .trackingId(request.getTransactionId().toString())
                        .creditCard(PaymentRequest.Request.CreditCard.builder()
                                .token(request.getProcessorAccount().getReference())
                                .cvv(request.stageInputData(1).get("cvv"))
                                .holder(request.stageInputData(1).get("nameoncard"))
                                .skipThreeD(!Boolean.parseBoolean(request.getProperty("use_3DSecure")))
                                .build())
                        .additionData(AdditionData.builder()
                                .contract(new String[]{"credit"}).build())
                        .returnUrl(gatewayPublicUrl() + "/public/redirect?trx_id=" + request.getTransactionId())
                        .notificationUrl(gatewayPublicUrl() + "/public/webhook")
                        .customer(Customer.builder()
                                .email(request.getUser().getEmail())
                                .deviceId(request.getUser().getRealGuid())
                                .ip(request.getUser().getLastKnownIP())
                                .birthDate(new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth().toDate()))
                                .build())
                        .build())
                .build();

            if (!request.getProperty("expired_after").isEmpty()) {
                Integer expiredAfter = Integer.parseInt(request.getProperty("expired_after"));
                paymentRequest.getRequest().setExpiredAt(DateTime.now().plusSeconds(expiredAfter/1000).toString());
            }

            if (request.getUser().getResidentialAddress() != null && Boolean.parseBoolean(request.getProperty("send_billing_address"))) {
                paymentRequest.getRequest().setBillingAddress(BillingAddress.builder()
                        .firstName(request.getUser().getFirstName())
                        .lastName(request.getUser().getLastName())
                        .address(request.getUser().getResidentialAddress().getAddressLine1())
                        .city(request.getUser().getResidentialAddress().getCity())
                        .zip(request.getUser().getResidentialAddress().getPostalCode())
                        .country(request.getUser().getResidentialAddress().getCountryCode()).build());
            }

            String avsResponseCodes = request.getProperty("avs_reject_codes");
            String cvvResponseCodes = request.getProperty("cvc_reject_codes");
            if (!avsResponseCodes.isEmpty() || !cvvResponseCodes.isEmpty()) {
                AvsCvcVerificationRequest verificationRequest = AvsCvcVerificationRequest.builder().build();
                verificationRequest.setAvsVerification(!avsResponseCodes.isEmpty() ? VerificationRequest.builder().rejectCodes(avsResponseCodes.trim().split("\\s*,\\s*")).build() : null);
                verificationRequest.setCvcVerification(!cvvResponseCodes.isEmpty() ? VerificationRequest.builder().rejectCodes(cvvResponseCodes.trim().split("\\s*,\\s*")).build() : null);
                paymentRequest.getRequest().getAdditionData().setAvsCvcVerification(verificationRequest);
            }

            response.addRawRequestLog(paymentRequest.toString());
            response.setPaymentType("card");

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Basic " + getAuthToken(request.getProperty("shop_id"), request.getProperty("secret_key")));
            headers.add("Content-type", "application/json");

            HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

            log.info("Payment request (transactionId: " + request.getTransactionId() + "): " + mapper.writeValueAsString(paymentRequest));

            ResponseEntity<Object> exchange = rest.exchange(request.getProperty("gateway_api_url") + "/transactions/payments", HttpMethod.POST, entity, Object.class, new HashMap<>());
            response.addRawResponseLog("Payment response: " + exchange.getBody());

            if (!exchange.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to initiate Hexopay card (" + request.getProcessorAccount().getReference() + ") reuse deposit ("+ request.getTransactionId() + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
                response.setDeclineReason(mapper.convertValue(exchange.getBody(), ErrorResponse.class).getResponse().getMessage());
                response.setMessage(GeneralError.VERIFY_CARD_DETAILS.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.VERIFY_CARD_DETAILS.getCode());
                return DoProcessorResponseStatus.DECLINED;
            }
            log.info("Payment response (transactionId: " + request.getTransactionId() + "): " + exchange.getBody());

            Transaction transaction = mapper.convertValue(exchange.getBody(), TransactionResponse.class).getTransaction();

            response.setProcessorReference(transaction.getUid());

            setAvsVerificatonStatus(transaction.getAvsCvcVerification(), request, response);

            if ("success".equals(transaction.getStatus())) {
                //waiting for the notification to move to success
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if ("incomplete".equals(transaction.getStatus())) {
                response.setIframeUrl(transaction.getRedirectUrl());
                response.setIframeMethod("GET");
                return DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE;
            } else if ("failed".equals(transaction.getStatus())) {
                response.setDeclineReason(transaction.getMessage());
                response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
                return DoProcessorResponseStatus.DECLINED;
            } else if ("expired".equals(transaction.getStatus())) {
                return DoProcessorResponseStatus.EXPIRED;
            }
        } catch (Exception e) {
            log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + " processorAccountId: " + request.getProcessorAccount().getId() + " Exceptiion: "  + e.getMessage(), e);
            response.addRawResponseLog(e.getMessage());
            return DoProcessorResponseStatus.FATALERROR;
        }
        return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
    }

    public DoProcessorResponseStatus payout(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) {
        try
        {
            PayoutRequest payoutRequest = PayoutRequest.builder()
                    .request(PayoutRequest.Request.builder()
                            .test(Boolean.parseBoolean(request.getProperty("test")))
                            .currency(request.getUser().getCurrency())
                            .amount(request.inputAmountCents())
                            .description(request.getProperty("payment_description"))
                            .trackingId(request.getTransactionId().toString())
                            .creditCard(PayoutRequest.Request.CreditCard.builder()
                                    .token(request.getProcessorAccount().getReference())
                                    .holder(request.getProcessorAccount().getName())
                                    .build())
                            .notificationUrl(gatewayPublicUrl() + "/public/webhook")
                            .recipient(Customer.builder()
                                    .email(request.getUser().getEmail())
                                    .deviceId(request.getUser().getRealGuid())
                                    .ip(request.getUser().getLastKnownIP())
                                    .birthDate(new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth().toDate()))
                                    .build())
                            .build())
                    .build();

            if (request.getUser().getResidentialAddress() != null && Boolean.parseBoolean(request.getProperty("send_billing_address"))) {
                payoutRequest.getRequest().setBillingAddress(BillingAddress.builder()
                        .firstName(request.getUser().getFirstName())
                        .lastName(request.getUser().getLastName())
                        .address(request.getUser().getResidentialAddress().getAddressLine1())
                        .city(request.getUser().getResidentialAddress().getCity())
                        .zip(request.getUser().getResidentialAddress().getPostalCode())
                        .country(request.getUser().getResidentialAddress().getCountryCode()).build());
            }

            if (!request.getProperty("expired_after").isEmpty()) {
                Integer expiredAfter = Integer.parseInt(request.getProperty("expired_after"));
                payoutRequest.getRequest().setExpiredAt(DateTime.now().plusSeconds(expiredAfter/1000).toString());
            }

            response.addRawRequestLog(payoutRequest.toString());
            response.setPaymentType("card");

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Basic " + getAuthToken(request.getProperty("shop_id"), request.getProperty("secret_key")));
            headers.add("Content-type", "application/json");

            HttpEntity<PayoutRequest> entity = new HttpEntity<>(payoutRequest, headers);

            log.info("Payout request (transactionId: " + request.getTransactionId() + "): " + mapper.writeValueAsString(payoutRequest));

            ResponseEntity<Object> exchange = rest.exchange(request.getProperty("gateway_api_url") + "/transactions/payouts", HttpMethod.POST, entity, Object.class, new HashMap<>());
            response.addRawResponseLog("Payout response: " + exchange.getBody());

            if (!exchange.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to initiate Hexopay (" + request.getProcessorAccount().getReference() + ") withdraw ("+ request.getTransactionId() + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
                response.setDeclineReason(mapper.convertValue(exchange.getBody(), ErrorResponse.class).getResponse().getMessage());
                response.setMessage(GeneralError.VERIFY_CARD_DETAILS.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.VERIFY_CARD_DETAILS.getCode());
                return DoProcessorResponseStatus.DECLINED;
            }
            log.info("Payout response (transactionId: " + request.getTransactionId() + "): " + exchange.getBody());

            Transaction transaction = mapper.convertValue(exchange.getBody(), TransactionResponse.class).getTransaction();

            response.setProcessorReference(transaction.getUid());
            if ("success".equals(transaction.getStatus())) {
                //waiting for the notification to move to success
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if ("incomplete".equals(transaction.getStatus())) {
                response.setIframeUrl(transaction.getRedirectUrl());
                response.setIframeMethod("GET");
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if ("failed".equals(transaction.getStatus())) {
                response.setDeclineReason(transaction.getMessage());
                response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
                return DoProcessorResponseStatus.DECLINED;
            } else if ("expired".equals(transaction.getStatus())) {
                return DoProcessorResponseStatus.EXPIRED;
            }
        } catch (Exception e) {
            log.error("Failed to initiate payout for the transaction with id: " + request.getTransactionId() + " processorAccountId: " + request.getProcessorAccount().getId() + " Exceptiion: "  + e.getMessage(), e);
            response.addRawResponseLog(e.getMessage());
            response.setDeclineReason("Failed to initiate payout. See exception in the response log.");
            return DoProcessorResponseStatus.DECLINED;
        }
        return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
    }

    public DoProcessorResponseStatus updateTransactionResponse(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) {
        DoProcessorResponseStatus status;
        try {
            Transaction transactionResonse = getHexopayTransaction(request, response, rest);
            response.setProcessorReference(transactionResonse.getUid());

            setAvsVerificatonStatus(transactionResonse.getAvsCvcVerification(), request, response);

            if ("successful".equalsIgnoreCase(transactionResonse.getStatus())) {
                response.setAmountCentsReceived(transactionResonse.getAmount().intValue());
                status = DoProcessorResponseStatus.SUCCESS;
                if (transactionResonse.getCreditCard() != null && transactionResonse.getCreditCard().getToken() != null && request.stageInputData(1).get("processorAccountId") == null && transactionResonse.getType().equals("payment")) {
                    response.setProcessorAccount(processorAccountFromCreditCard(transactionResonse.getCreditCard()));
                }
            } else if ("incomplete".equalsIgnoreCase(transactionResonse.getStatus())) {
                status = DoProcessorResponseStatus.NOOP;
            } else if ("expired".equalsIgnoreCase(transactionResonse.getStatus())) {
                status = DoProcessorResponseStatus.EXPIRED;
            } else if ("failed".equalsIgnoreCase(transactionResonse.getStatus())) {
                response.setDeclineReason(transactionResonse.getMessage());
                response.setMessage(GeneralError.VERIFY_CARD_DETAILS.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.VERIFY_CARD_DETAILS.getCode());
                status = DoProcessorResponseStatus.DECLINED;
            } else {
                log.error("Unknown status " + transactionResonse.getStatus() + " is returned from Hexopay for transactionId: " + request.getTransactionId());
                status = DoProcessorResponseStatus.NOOP;
            }
            response.setStatus(status);
        } catch (Exception e) {
            log.error("Failed to update transaction response (" + request.getTransactionId() + "). Exception: " + e.getMessage(), e);
            response.addRawResponseLog(e.getMessage());
            return DoProcessorResponseStatus.NOOP;
        }
        return status;
    }

    public Transaction getHexopayTransaction(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Basic " + getAuthToken(request.getProperty("shop_id"), request.getProperty("secret_key")));

            HttpEntity<?> entity = new HttpEntity<>(headers);

            response.addRawRequestLog("Get transaction from Hexopay");
            log.info("Get Hexopay transaction for transaction id: " + request.getTransactionId());
            ResponseEntity<String> transactionResponseEntity = rest.exchange(request.getProperty("gateway_api_url") + "/v2/transactions/tracking_id/" + request.getTransactionId(),
                    HttpMethod.GET, entity,
                    String.class, new HashMap<>());

            response.addRawResponseLog("Get transaction response: " + transactionResponseEntity.getBody());

            if (!transactionResponseEntity.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to get Hexopay transaction (" + request.getTransactionId() + ") (" + transactionResponseEntity.getStatusCodeValue() + "): " + transactionResponseEntity.getBody());
                throw new Exception("Failed to get Hexopay transaction (" + request.getTransactionId() + ") (" + transactionResponseEntity.getStatusCodeValue() + "): " + transactionResponseEntity.getBody());
            }
            log.info("Get Hexopay transaction response (" + request.getTransactionId() + "): " + transactionResponseEntity.getBody());

            TransactionsResponse transactionsResponse =  mapper.readValue(transactionResponseEntity.getBody(), TransactionsResponse.class);
            if (transactionsResponse.getTransactions() == null || transactionsResponse.getTransactions().length == 0) {
                throw new Exception("No hexopay transaction for transaction id: " + request.getTransactionId());
            } else if (transactionsResponse.getTransactions().length > 1) {
                throw new Exception("Get more then one transaction from Hexopay for transaction Id: " + request.getTransactionId());
            }
            return transactionsResponse.getTransactions()[0];

        } catch (Exception e) {
            log.error("Failed to get transaction from Hexopay for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            throw new Exception("Failed to get transaction (" + request.getTransactionId() + "). Exception: " + e.getMessage(), e);
        }
    }

    public void handlePaymentWebhook(String data, String signature) throws Exception {
        Transaction webhookRequest = mapper.readValue(data, TransactionResponse.class).getTransaction();

        Long transactionId = Long.parseLong(webhookRequest.getTrackingId());

        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

        DoProcessorRequest processorRequest = processorRequestResponse.getData();

        checkSignature(data, signature, processorRequest.getProperty("public_key"));

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(processorRequest.getTransactionId())
                .rawRequestLog("Received Hexopay payment notification: " + webhookRequest.toString())
                .build();

        if (!processorRequest.isTransactionFinalized()) {
             updateTransactionResponse(processorRequest, response, restTemplate);
        }
        //add transaction remark with card details in case account is not saved
        if (webhookRequest.getCreditCard() != null && !"successful".equalsIgnoreCase(webhookRequest.getStatus())) {
            response.setRemark(getCreditCardDataRemark(webhookRequest.getCreditCard()));
        }

        callbackService.doSafeCallback(response);
    }

    public String handleHexopayRedirect(String transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(Long.parseLong(transactionId));
        try {
            DoProcessorRequest processorRequest = processorRequestResponse.getData();

            DoProcessorResponse response = DoProcessorResponse.builder()
                    .transactionId(processorRequest.getTransactionId())
                    .build();

            DoProcessorResponseStatus status = updateTransactionResponse(processorRequest, response, restTemplate);
            //remove status to add workflow note
            if (processorRequest.isTransactionFinalized()) {
                response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .build();
            }
            response.addRawRequestLog("Received Hexopay " + (status == DoProcessorResponseStatus.SUCCESS ? "success" : "failed") + " redirect.");
            callbackService.doSafeCallback(response);

            if (!processorRequest.isTransactionFinalized()) {
                processorRequestResponse = getCallbackGetTransaction(processorRequest.getTransactionId());
                processorRequest = processorRequestResponse.getData();
            }

            if (!processorRequest.isTransactionFinalized()) {
                return "redirect:" + processorRequest.stageInputData(1).get("return_url") + "?status=pending&tnx_id=" + transactionId;
            } else {
                return "redirect:" + processorRequest.stageInputData(1).get("return_url") + "?status=" + (status == DoProcessorResponseStatus.SUCCESS ? "success" : "failed&error=" + GeneralError.VERIFY_CARD_DETAILS.getResponseMessageLocal(messageSource, processorRequest.getUser().getDomain(), processorRequest.getUser().getLanguage()));
            }
        } catch (Exception e) {
            log.error("Failed to proceed Hexopay redirect (" + transactionId + "). Exeption: " + e.getMessage(), e);
            return "redirect:" + processorRequestResponse.getData().stageInputData(1).get("return_url") + "?status=failed";
        }
    }

    private ProcessorAccount processorAccountFromCreditCard(CreditCard creditCard) {
        return ProcessorAccount.builder()
                .reference(creditCard.getToken())
                .status(PaymentMethodStatusType.ACTIVE)
                .hideInDeposit(false)
                .type(ProcessorAccountType.CARD)
                .name(creditCard.getHolder())
                .descriptor(creditCard.getLast4Digits())
                .data(getCardData(creditCard)).build();
    }

    private Map<String,String> getCardData(CreditCard creditCard) {
        return new HashMap<String, String>() {{
            put("bin", creditCard.getBin());
            put("scheme", creditCard.getBrand());
            put("fingerprint", creditCard.getStamp());
            put("name", creditCard.getHolder());
            put("expiryDate", String.format("%02d/%02d", creditCard.getExpMonth(), creditCard.getExpYear() % 100));
            put("last4Digits", creditCard.getLast4Digits());
            put("issuerCountry", creditCard.getIssuerCountry());
            put("product", creditCard.getProduct());
            put("bank", creditCard.getIssuerName());
        }};
    }

    private TransactionRemarkData getCreditCardDataRemark(CreditCard creditCard) {
        String remark = "Additional Transaction Information: " + getCardData(creditCard).entrySet().stream()
                .filter(entrySet -> !entrySet.getKey().equals("fingerprint"))
                .map(entrySet -> entrySet.getKey() + ": " + entrySet.getValue())
                .collect(Collectors.joining(", ", "", "."));
        return TransactionRemarkData.builder()
                .remark(remark)
                .type(TransactionRemarkType.ACCOUNT_DATA)
                .build();
    }

    private Response<DoProcessorRequest> getCallbackGetTransaction(Long transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "hexopay");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transaction with id: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        return processorRequestResponse;
    }

    private String getAuthToken(String shopId, String secretKey) {
        String auth =  shopId + ":" + secretKey;
        return  new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)));
    }

    private void checkSignature(String request, String signature, String publicKey) throws Exception {
        try {
            Signature sigEng = Signature.getInstance("SHA256withRSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(publicKey));
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
            sigEng.initVerify(rsaPubKey);
            sigEng.update(request.getBytes());
            if (!sigEng.verify(java.util.Base64.getDecoder().decode(signature))) {
                throw new Exception("Signature is invalid");
            }
        } catch (Exception e) {
            log.warn("Signature verification is failed for webhook request: " + request + "Exception: " + e.getMessage(), e);
            throw new HexopayInvalidSignatureException("Signature verification is failed");
        }
    }
    private void setAvsVerificatonStatus(AvsCvcVerificationResponse avsCvcResponse, DoProcessorRequest request, DoProcessorResponse response) {
        if (avsCvcResponse != null) {
            AvsVerificationCode avsVerificationCode =
                    avsCvcResponse.getAvsVerification() != null && avsCvcResponse.getAvsVerification().getResultCode() != null
                            ? AvsVerificationCode.fromCode(avsCvcResponse.getAvsVerification().getResultCode())
                            : null;
            CvcVerificationCode cvcVerificationCode = avsCvcResponse.getCvcVerification() != null && avsCvcResponse.getCvcVerification().getResultCode() != null
                    ? CvcVerificationCode.fromCode(avsCvcResponse.getCvcVerification().getResultCode())
                    : null;
            response.setOutputData(request.getStage(), "avs_verification_result", avsVerificationCode != null ? avsVerificationCode.code() + ": " + avsVerificationCode.getDescription() : null);
            response.setOutputData(request.getStage(), "cvc_verification_result", cvcVerificationCode != null ? cvcVerificationCode.code() + ": " + cvcVerificationCode.getDescription() : null);
        }
    }

    private String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl() + "/" + moduleName;
    }
}
