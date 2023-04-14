package lithium.service.cashier.processor.nuvei.cc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safecharge.biz.RequestBuilder;
import com.safecharge.biz.SafechargeRequestExecutor;
import com.safecharge.model.MerchantInfo;
import com.safecharge.request.SafechargeBaseRequest;
import com.safecharge.response.SafechargeResponse;
import com.safecharge.util.APIConstants;
import com.safecharge.util.Constants;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.nuvei.exceptions.NuveiInvalidSignatureException;
import lithium.service.cashier.processor.nuvei.exceptions.NuveiVerifyTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public abstract class NuveiCCApiService {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    protected CashierDoCallbackService callbackService;
    @Autowired
    CashierInternalClientService cashierService;
    @Autowired
    protected MessageSource messageSource;

    protected String getSessionToken(MerchantInfo merchantInfo) throws Exception {
        RequestBuilder requestBuilder = new RequestBuilder();
        SafechargeBaseRequest request = requestBuilder.getSessionTokenRequest(merchantInfo);
        SafechargeResponse response = SafechargeRequestExecutor.getInstance().execute(request);
        if (Constants.APIResponseStatus.ERROR.equals(response.getStatus())) {
            throw new Exception(response.getReason());
        }
        return response.getSessionToken();
    }

    public String getSessionToken(String merchantId, String merchantSiteId, String merchantKey, boolean test) throws Exception {
        return getSessionToken(createMerchantInfo(merchantId, merchantSiteId, merchantKey, test));
    }

    protected MerchantInfo createMerchantInfo(String merchantId, String siteId, String merchantKey, boolean test) {
        return new MerchantInfo(merchantKey, merchantId, siteId, test ? APIConstants.Environment.INTEGRATION_HOST.getUrl() : APIConstants.Environment.PRODUCTION_HOST.getUrl(), Constants.HashAlgorithm.SHA256);
    }

    protected Response<DoProcessorRequest> getCallbackGetTransaction(Long transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "nuvei-cc");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transaction with id: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        return processorRequestResponse;
    }

    protected void checkSignature(Map<String, String> nuveiData, String merchantKey, String transactionId) throws NuveiInvalidSignatureException {
        try {
            String checkSum = getCheckSum(merchantKey, nuveiData.get("totalAmount"), nuveiData.get("currency"), nuveiData.get("responseTimeStamp"), nuveiData.get("PPP_TransactionID"), nuveiData.get("Status"), nuveiData.get("productId"));

            if (!checkSum.equals(nuveiData.get("advanceResponseChecksum"))) {
                throw new Exception("Invalid Nuvei checksum. Nuvei data: " + nuveiData.toString());
            }
        } catch (Exception e) {
            log.error("Nuvei signature check filed. For transactionid: " + transactionId + " Nuvei data: " +  nuveiData.toString(), e);
            throw new NuveiInvalidSignatureException("Nuvei signature check is failed. For transactionId: " + transactionId + ". Nuvei data: " +  nuveiData.toString());
        }
    }

    protected String getCheckSum(String... values) throws Exception {
        StringBuilder checkSum = new StringBuilder();
        Arrays.stream(values).filter(Objects::nonNull).forEach(v -> checkSum.append(v));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(checkSum.toString().getBytes(StandardCharsets.UTF_8));
        return toHexString(hash);
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    protected String getCardFingerprint(String bin, String last4Digits, String expMonth, String expYear) throws Exception {
        return getCheckSum(bin, last4Digits, expMonth, expYear);
    }

    public DoProcessorResponseStatus handleNuveiError(DoProcessorRequest request, DoProcessorResponse response, int errorCode, String errorReason) {
        return handleNuveiError(request, response, String.valueOf(errorCode), errorReason);
    }

    public DoProcessorResponseStatus handleNuveiError(DoProcessorRequest request, DoProcessorResponse response, String errorCode, String errorReason) {
        response.setDeclineReason(errorCode + ":" + errorReason);
        //TODO: map coresponding error here two enums for errors here probably? and may be default error from input
        response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
        response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
        return DoProcessorResponseStatus.DECLINED;
    }

    public void handlePaymentWebhook(String userTokenId, Map<String,String> nuveiData) throws Exception {
        Long transactionId = Long.parseLong(userTokenId);

        Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

        DoProcessorRequest processorRequest = processorRequestResponse.getData();

        checkSignature(nuveiData, processorRequest.getProperty("merchant_key"), processorRequest.getTransactionId().toString());

        DoProcessorResponse processorResponse = DoProcessorResponse.builder()
            .transactionId(processorRequest.getTransactionId())
            .rawRequestLog("Received Nuvei notification: " + objectToPrettyString(nuveiData))
            .build();

        try {
            DoProcessorResponseStatus status = verifyTransaction(processorRequest, processorResponse);
            if (!processorRequest.isTransactionFinalized()) {
                processorResponse.setStatus(status);
            }
        } finally {
            callbackService.doSafeCallback(processorResponse);
        }
    }

    protected abstract DoProcessorResponseStatus verifyTransaction(DoProcessorRequest request, DoProcessorResponse response) throws NuveiVerifyTransactionException;

}


