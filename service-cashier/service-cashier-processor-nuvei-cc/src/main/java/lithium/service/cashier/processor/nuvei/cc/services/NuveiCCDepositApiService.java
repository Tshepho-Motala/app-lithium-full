package lithium.service.cashier.processor.nuvei.cc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safecharge.biz.SafechargeRequestExecutor;
import com.safecharge.model.CardResponse;
import com.safecharge.model.PaymentOptionResponse;
import com.safecharge.request.SafechargeBaseRequest;
import com.safecharge.response.GetPaymentStatusResponse;
import com.safecharge.response.InitPaymentResponse;
import com.safecharge.response.PaymentResponse;
import com.safecharge.response.SafechargeResponse;
import com.safecharge.response.ThreeDResponse;
import com.safecharge.util.Constants;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.objects.TransactionRemarkData;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.nuvei.cc.builders.InitPaymentRequestBuilder;
import lithium.service.cashier.processor.nuvei.cc.builders.PaymentRequestBuilder;
import lithium.service.cashier.processor.nuvei.cc.builders.PaymentStatusRequestBuilder;
import lithium.service.cashier.processor.nuvei.data.Nuvei3DNotificationData;
import lithium.service.cashier.processor.nuvei.data.NuveiFingerprintNotification;

import lithium.service.cashier.processor.nuvei.exceptions.NuveiVerifyTransactionException;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_INVALID_ACCOUNT;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getError;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public class NuveiCCDepositApiService extends NuveiCCApiService{

    @Autowired
    ObjectMapper mapper;
    @Autowired
    CashierInternalClientService cashierService;
    @Autowired
    InitPaymentRequestBuilder initPaymentRequestBuilder;
    @Autowired
    PaymentRequestBuilder paymentRequestBuilder;
    @Autowired
    PaymentStatusRequestBuilder paymentStatusRequestBuilder;

    private static final String TRANSACTION_ID = "{{trn_id}}";

    public DoProcessorResponseStatus initPayment(DoProcessorRequest request, DoProcessorResponse response) {
        response.setPaymentType("card");
        try {
            String sessionToken = Optional.ofNullable(request.stageInputData(1).get("session_token")).orElse(getSessionToken(initPaymentRequestBuilder.getMerchantInfo(request)));
            response.setOutputData(1, "session_token", sessionToken);

            SafechargeBaseRequest initPaymentRequest = initPaymentRequestBuilder.getRequest(request, sessionToken);

            response.addRawRequestLog(objectToPrettyString(initPaymentRequest));
            SafechargeResponse safechargeResponse = SafechargeRequestExecutor.getInstance().execute(initPaymentRequest);
            response.addRawResponseLog(objectToPrettyString(safechargeResponse));

            if (Constants.APIResponseStatus.ERROR.equals(safechargeResponse.getStatus())) {
                return handleNuveiError(request, response, safechargeResponse.getErrCode(), safechargeResponse.getReason());
            }

            InitPaymentResponse initPaymentResponse = (InitPaymentResponse)safechargeResponse;
            response.setAdditionalReference(initPaymentResponse.getTransactionId());
            response.setOutputData(1, "paymentStatus", initPaymentResponse.getTransactionStatus());
            response.setOutputData(1, "ipAddress", request.getUser().getLastKnownIP());

            switch (initPaymentResponse.getTransactionStatus()) {
                case "APPROVED":
                    return handleApprovedInitPayment(initPaymentResponse, request,response);
                case "ERROR":
                case "DECLINED":
                default:
                    addCardRemark(request, response, initPaymentResponse.getPaymentOption());
                    return handleNuveiError(request, response, initPaymentResponse.getGwExtendedErrorCode(), initPaymentResponse.getGwErrorReason());
            }
        } catch (Throwable e) {
            response.setDeclineReason("Failed to initiate Nuvei payment");
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            log.error("Failed to initiate Nuvei payment for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
            response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    private DoProcessorResponseStatus handleApprovedInitPayment(InitPaymentResponse initPaymentResponse, DoProcessorRequest request, DoProcessorResponse response) {
        return handleInitTheeDSecure(initPaymentResponse.getPaymentOption().getCard().getThreeD(), request, response);
    }

    private DoProcessorResponseStatus handleInitTheeDSecure(ThreeDResponse threeDResponse, DoProcessorRequest request, DoProcessorResponse response) {
        if (threeDResponse != null) {
            response.setOutputData(1, "3DSecure_version", threeDResponse.getVersion());
            response.setOutputData(1, "3DS2_supported", String.valueOf(Boolean.parseBoolean(threeDResponse.getV2supported())));
            //fingerprint flow
            if (!StringUtil.isEmpty(threeDResponse.getMethodUrl()) && !StringUtil.isEmpty(request.stageInputData(1).get("method_notification_url")) && Boolean.parseBoolean(request.getProperties().get("fingerprint_enabled"))) {
                response.setIframeUrl(threeDResponse.getMethodUrl());
                HashMap<String, String> map = new HashMap<>();
                map.put("threeDSMethodData", threeDResponse.getMethodPayload());
                response.setIframePostData(map);
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else {
                response.setOutputData(1, "method_completion_ind", "U");
            }
        }
        return DoProcessorResponseStatus.NEXTSTAGE;
    }

    public DoProcessorResponseStatus payment(DoProcessorRequest request, DoProcessorResponse response) {
        try {
            SafechargeBaseRequest paymentRequest = paymentRequestBuilder.getRequest(request);
            response.addRawRequestLog(objectToPrettyString(paymentRequest));
            SafechargeResponse safechargeResponse = SafechargeRequestExecutor.getInstance().execute(paymentRequest);
            response.addRawResponseLog(objectToPrettyString(safechargeResponse));

            if (Constants.APIResponseStatus.ERROR.equals(safechargeResponse.getStatus())) {
                return handleNuveiError(request, response, safechargeResponse.getErrCode(), safechargeResponse.getReason());
            }

            PaymentResponse paymentResponse = (PaymentResponse) safechargeResponse;
            response.setProcessorReference(paymentResponse.getTransactionId());

            switch (paymentResponse.getTransactionStatus()) {
                case "APPROVED":
                    response.setProcessorAccount(getProcessorAccount(request, response, paymentResponse.getPaymentOption()));
                    return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
                case "REDIRECT":
                    response.setProcessorAccount(getProcessorAccount(request, response, paymentResponse.getPaymentOption()));
                    return handleTheeDSecureChallengeFlow(request, response, paymentResponse.getPaymentOption().getCard().getThreeD());
                case "ERROR":
                case "DECLINED":
                default:
                    return handleNuveiError(request,response,paymentResponse.getGwExtendedErrorCode(), paymentResponse.getGwErrorReason());
            }
        } catch (Throwable e) {
            response.setDeclineReason("Failed to make Nuvei payment");
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            log.error("Failed to make Nuvei payment for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
            response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    public DoProcessorResponseStatus liabilityShiftPayment(DoProcessorRequest request, DoProcessorResponse response) {
        try {
            SafechargeBaseRequest paymentRequest = paymentRequestBuilder.getRequest(request);
            response.addRawRequestLog(objectToPrettyString(paymentRequest));
            SafechargeResponse safechargeResponse = SafechargeRequestExecutor.getInstance().execute(paymentRequest);
            response.addRawResponseLog(objectToPrettyString(safechargeResponse));

            if (Constants.APIResponseStatus.ERROR.equals(safechargeResponse.getStatus())) {
                return handleNuveiError(request, response, safechargeResponse.getErrCode(), safechargeResponse.getReason());
            }

            PaymentResponse paymentResponse = (PaymentResponse) safechargeResponse;

            switch (paymentResponse.getTransactionStatus()) {
                case "APPROVED":
                    //waiting for webhoook
                    return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
                case "ERROR":
                case "DECLINED":
                default:
                    return handleNuveiError(request, response, ((PaymentResponse) safechargeResponse).getGwExtendedErrorCode(), ((PaymentResponse) safechargeResponse).getGwErrorReason());
            }
        } catch (Throwable e) {
            log.error("Failed to make Nuvei liability shift payment for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
            response.setDeclineReason("Failed to make Nuvei liability payment");
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    private DoProcessorResponseStatus handleTheeDSecureChallengeFlow(DoProcessorRequest request, DoProcessorResponse response, ThreeDResponse threeDResponse) {
        HashMap<String, String> map = new HashMap<>();
        response.setOutputData(request.getStage(), "ChallengeFlow", "true");
        response.setIframeUrl(threeDResponse.getAcsUrl());
        if (!StringUtil.isEmpty(threeDResponse.getcReq())) {
            //3DS2 challenge flow
            map.put("creq", threeDResponse.getcReq());
        } else {
            //3DS1 challenge flow
            map.put("PaReq", threeDResponse.getPaRequest());
            map.put("TermUrl", paymentRequestBuilder.gatewayPublicUrl() + "/public/" + request.getTransactionId() + "/threeD/v1");
            map.put("MD", request.getTransactionId().toString());
        }
        response.setIframePostData(map);
        return DoProcessorResponseStatus.NOOP;
    }

    @Override
    public DoProcessorResponseStatus verifyTransaction(DoProcessorRequest request, DoProcessorResponse response) throws NuveiVerifyTransactionException {
        try {
            SafechargeBaseRequest paymentStatusRequest = paymentStatusRequestBuilder.getRequest(request);
            response.addRawRequestLog(objectToPrettyString(paymentStatusRequest));
            SafechargeResponse paymentStatusResponse = SafechargeRequestExecutor.getInstance().execute(paymentStatusRequest);
            response.addRawResponseLog(objectToPrettyString(paymentStatusResponse));
            //in case 3DS2 challenge is failed the "Sale" transaction is not created on the Nuvei side
            // and "1140:A payment was not performed during this session" is returned and no webhook
            //in case 3DS1 challenge the "Sale" is created the error "Failed 3DSecure Authentication" is returned
            // and with webhook DECLINE webhook is called
            // to check with Nuvei

            if (Constants.APIResponseStatus.ERROR.equals(paymentStatusResponse.getStatus())) {
                return handleNuveiError(request, response, paymentStatusResponse.getErrCode(), paymentStatusResponse.getReason());
            }
            GetPaymentStatusResponse getPaymentStatusResponse = (GetPaymentStatusResponse) paymentStatusResponse;

            switch (getPaymentStatusResponse.getTransactionStatus()) {
                case "APPROVED":
                    response.setAmountCentsReceived(CurrencyAmount.fromAmountString(getPaymentStatusResponse.getAmount()).toCents().intValue());
                    return DoProcessorResponseStatus.SUCCESS;
                case "ERROR":
                case "DECLINED":
                    return handleNuveiError(request, response, getPaymentStatusResponse.getGwExtendedErrorCode(), getPaymentStatusResponse.getGwErrorReason());
                case "REDIRECT":
                default:
                    throw new Exception("Not final status: " + getPaymentStatusResponse.getTransactionStatus() + " received from Nuvei for transaction: " + request.getTransactionId());
            }
        } catch (Exception e) {
            log.error("Failed to validate Nuvei payment. Exception: " + e.getMessage(), e);
            throw new NuveiVerifyTransactionException("Failed to verify nuvei payment.");
        }
    }

    public String handleThreeDSecureNotification(String notification, Long transactionId, boolean is3DS2) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

        DoProcessorRequest processorRequest = processorRequestResponse.getData();

        DoProcessorResponse processorResponse = DoProcessorResponse.builder()
            .transactionId(processorRequest.getTransactionId())
            .rawRequestLog("Received Nuvei 3DSecure" + (is3DS2 ? "V2": "V1") + " notification: " + objectToPrettyString(notification))
            .build();

        boolean is3DChallengeSuccess;

        if (is3DS2) {
            Nuvei3DNotificationData data = mapper.readValue(notification, Nuvei3DNotificationData.class);
            is3DChallengeSuccess = "Y".equalsIgnoreCase(data.getTransStatus());
        } else {
            processorRequest.stageOutputData(2).put("PaRes", notification);
            is3DChallengeSuccess = !StringUtil.isEmpty(notification);
        }
        processorResponse.setOutputData(2, "is3DChallengeSuccess", String.valueOf(is3DChallengeSuccess));

        try {
            DoProcessorResponseStatus status = is3DChallengeSuccess ? liabilityShiftPayment(processorRequest, processorResponse)
                : verifyTransaction(processorRequest, processorResponse);
                processorResponse.setStatus(status);
        } catch (NuveiVerifyTransactionException ex) {
            processorResponse.setStatus(DoProcessorResponseStatus.DECLINED);
            processorResponse.setDeclineReason(ex.getMessage());
            processorResponse.setErrorCode(GeneralError.CONTACT_YOUR_BANK.getCode());
            processorResponse.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, processorRequest.getUser().getDomain(), processorRequest.getUser().getLanguage()));
        } finally {
            callbackService.doSafeCallback(processorResponse);
        }
        return getReturnUrl(processorRequest);
    }

    public String handleFingerprint(Long transactionId, NuveiFingerprintNotification notification) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

        DoProcessorRequest processorRequest = processorRequestResponse.getData();

        String methodNotificationUrl = processorRequest.stageInputData(1, "method_notification_url").replace(TRANSACTION_ID, processorRequest.getTransactionId().toString());
        DoProcessorResponse processorResponse = DoProcessorResponse.builder()
            .transactionId(processorRequest.getTransactionId())
            .rawRequestLog("Received Nuvei fingerprint notification: " + notification)
            .rawResponseLog("Redirect to " + methodNotificationUrl)
            .build();

        callbackService.doSafeCallback(processorResponse);
        return methodNotificationUrl;
    }

    private String getReturnUrl(DoProcessorRequest request) throws Exception {
        return request.stageInputData(1, "return_url").replace(TRANSACTION_ID, request.getTransactionId().toString()) + "?status=pending&trn_id=" + request.getTransactionId();
    }

    public ProcessorAccount getProcessorAccount(DoProcessorRequest request, DoProcessorResponse response, PaymentOptionResponse paymentOption) {
        ProcessorAccount processorAccount = null;

        if (request.getProcessorAccount() != null) {
            return processorAccount;
        }

        if (paymentOption == null || paymentOption.getCard() == null) {
            log.info("Nuvei processor account will not be saved for transactionId:" + request.getTransactionId() + ". No card data in the payment response.");
            return processorAccount;
        }

        try {
            CardResponse card = paymentOption.getCard();
            processorAccount = ProcessorAccount.builder()
                .reference(paymentOption.getUserPaymentOptionId())
                .providerData(response.getProcessorReference())
                .methodCode("nuvei-cc")
                .status(PaymentMethodStatusType.ACTIVE)
                .hideInDeposit(false)
                .type(ProcessorAccountType.CARD)
                .name(request.getInputData().get(1).get("nameoncard"))
                .descriptor(card.getBin() + "****" + card.getLast4Digits())
                .verified(true)
                .data(new HashMap<String, String>() {{
                    put("cardType", card.getCardType());
                    put("bin", card.getBin());
                    put("last4Digits", card.getLast4Digits());
                    put("expiryDate", card.getCcExpMonth() + "/" + card.getCcExpYear());
                    put("scheme", card.getCardBrand());
                    put("country", card.getIssuerCountry());
                    put("acquirerId", card.getAcquirerId());
                    put("name", request.getInputData().get(1).get("nameoncard"));
                    put("3DS", Optional.ofNullable(card.getThreeD()).map(ThreeDResponse::getVersion).map(Boolean::parseBoolean).map(v -> v ? "V2" : "V1").orElse("No"));
                }})
                .build();
        } catch(Exception e) {
            log.error("Failed to save processor account for transactionId: " + request.getTransactionId() + "Nuvei paymentOptions: " + paymentOption + " Exception: " + e.getMessage(), e);
        }
        return processorAccount;
    }

    private void addCardRemark(DoProcessorRequest request, DoProcessorResponse response, PaymentOptionResponse paymentOption) {
        if (request.getProcessorAccount() != null) {
            return;
        }

        if (paymentOption == null || paymentOption.getCard() == null) {
            log.info("Nuvei processor account will not be saved for transactionId:" + request.getTransactionId() + ". No card data in the payment response.");
            return;
        }
        try {
            CardResponse card = paymentOption.getCard();

            Map<String,String> cardData = new HashMap<String, String>() {{
                put("cardType", card.getCardType());
                put("bin", card.getBin());
                put("last4Digits", card.getLast4Digits());
                put("expiryDate", card.getCcExpMonth() + "/" + card.getCcExpYear());
                put("scheme", card.getCardBrand());
                put("country", card.getIssuerCountry());
                put("acquirerId", card.getAcquirerId());
            }};

            TransactionRemarkData remark = TransactionRemarkData.builder()
                .remark(cardData.entrySet().stream()
                                .map(e -> e.getKey()+": "+e.getValue())
                                .collect(joining(",", "", ".")))
                .type(TransactionRemarkType.ACCOUNT_DATA)
                .build();
            response.setRemark(remark);
        } catch(Exception e) {
            log.error("Failed to save  account remark for transactionId: " + request.getTransactionId() + "Nuvei paymentOptions: " + paymentOption + " Exception: " + e.getMessage(), e);
        }
    }
    //will be not used for now Nuvei risk engine will be used instead. Keep it in case specific verification will be needed.
    private boolean verifyAccount(DoProcessorRequest request, DoProcessorResponse response, PaymentOptionResponse paymentOption) {
        try {
            ProcessorAccount processorAccount = getProcessorAccount(request,response,paymentOption);
            if (processorAccount == null) {
                return true;
            }
            processorAccount.setVerified(null);
            VerifyProcessorAccountRequest verifyRequest = VerifyProcessorAccountRequest.builder()
                .processorAccount(processorAccount)
                .verifications(getAccountVerifications(request))
                .userGuid(request.getUser().getRealGuid())
                .build();
            VerifyProcessorAccountResponse verifyResponse = cashierService.verifyAccount(verifyRequest);

            if (BooleanUtils.isFalse(verifyResponse.getResult())) {
                ProcessorAccountVerificationType failedVerification = verifyResponse.getProcessorAccount().getFailedVerification();
                response.setErrorCode(failedVerification.getGeneralError().getCode());
                response.setMessage(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setDeclineReason(getError(CASHIER_INVALID_ACCOUNT) + ": " + failedVerification.getDescription());
                addCardRemark(request, response, paymentOption);
                log.error("Account is invalid. Verification: " + failedVerification + " ProcessorAccount: " + processorAccount.toString());
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to verify account for transaction id:" + request.getTransactionId() + ". Error: " + e.getMessage(), e);
            addCardRemark(request, response, paymentOption);
            response.setDeclineReason("Failed to verify account.");
            response.setErrorCode(GeneralError.INVALID_PROCESSOR_ACCOUNT.getCode());
            response.setMessage(GeneralError.INVALID_PROCESSOR_ACCOUNT.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            return false;
        }
        return true;
    }

    private List<ProcessorAccountVerificationType> getAccountVerifications(DoProcessorRequest request){
        String accountVerifications = request.getProperties().get("account_verifications");
        if (StringUtil.isEmpty(accountVerifications)) {
            return Collections.emptyList();
        }
        return Arrays.asList(accountVerifications.split("\\s*,\\s*")).stream().map(ProcessorAccountVerificationType::fromName).collect(Collectors.toList());
    }
}


