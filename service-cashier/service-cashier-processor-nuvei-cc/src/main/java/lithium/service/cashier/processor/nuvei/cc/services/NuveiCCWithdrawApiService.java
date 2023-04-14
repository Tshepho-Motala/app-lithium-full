package lithium.service.cashier.processor.nuvei.cc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safecharge.biz.SafechargeRequestExecutor;
import com.safecharge.request.SafechargeBaseRequest;
import com.safecharge.response.PayoutResponse;
import com.safecharge.response.SafechargeResponse;
import com.safecharge.util.APIConstants;
import com.safecharge.util.Constants;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.nuvei.cc.builders.PayoutRequestBuilder;
import lithium.service.cashier.processor.nuvei.cc.builders.PayoutStatusRequestBuilder;
import lithium.service.cashier.processor.nuvei.data.NuveiGetPayoutStatusRequest;
import lithium.service.cashier.processor.nuvei.data.NuveiGetPayoutStatusResponse;
import lithium.service.cashier.processor.nuvei.exceptions.NuveiVerifyTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Slf4j
@Service
public class NuveiCCWithdrawApiService extends NuveiCCApiService{
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private PayoutRequestBuilder payoutRequestBuilder;
    @Autowired
    private PayoutStatusRequestBuilder payoutStatusRequestBuilder;
    @Autowired
    private RestTemplate restTemplate;

    private static String GET_PAYOUT_STATUS_URL = "api/v1/getPayoutStatus.do";

    public DoProcessorResponseStatus payout(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        response.setPaymentType("card");
        SafechargeResponse safechargeResponse;
        try {
            SafechargeBaseRequest payoutRequest = payoutRequestBuilder.getRequest(request);
            response.addRawRequestLog(objectToPrettyString(payoutRequest));
            safechargeResponse = SafechargeRequestExecutor.getInstance().execute(payoutRequest);
            response.addRawResponseLog(objectToPrettyString(safechargeResponse));
        } catch (Exception e) {
            response.setDeclineReason("Failed to initiate Nuvei payout.");
            response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
            log.error("Failed to initiate Nuvei payout for the transaction with id: " + request.getTransactionId() + " Exceptiion: " + e.getMessage(), e);
            response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }

        if (Constants.APIResponseStatus.ERROR.equals(safechargeResponse.getStatus())) {
            return handleNuveiError(request,response, safechargeResponse.getErrCode(), safechargeResponse.getReason());
        }

        PayoutResponse payoutResponse = (PayoutResponse) safechargeResponse;
        response.setProcessorReference(payoutResponse.getTransactionId());

        switch (payoutResponse.getTransactionStatus()) {
            case "APPROVED":
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            case "ERROR":
            case "DECLINED":
            default:
                return handleNuveiError(request, response, payoutResponse.getGwExtendedErrorCode(), ((PayoutResponse) safechargeResponse).getGwErrorReason());
        }
    }

    @Override
    public DoProcessorResponseStatus verifyTransaction(DoProcessorRequest request, DoProcessorResponse response) throws NuveiVerifyTransactionException {
        //TODO: not implemented in Nuvei Java SDK and returns 500
        try {
            NuveiGetPayoutStatusRequest payoutStatusReuest = payoutStatusRequestBuilder.getRequest(request);
            payoutStatusReuest.setChecksum(getCheckSum(new String[] { payoutStatusReuest.getMerchantId(),payoutStatusReuest.getMerchantSiteId(), payoutStatusReuest.getClientRequestId(), payoutStatusReuest.getTimeStamp(), request.getProperty("merchant_key")}));
            String requestBody = mapper.writeValueAsString(payoutStatusReuest);

            response.addRawRequestLog(jsonObjectToPrettyString(requestBody));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
            String hostUrl = Boolean.parseBoolean(request.getProperty("test")) ? APIConstants.Environment.INTEGRATION_HOST.getUrl() : APIConstants.Environment.PRODUCTION_HOST.getUrl();

            ResponseEntity<String> exchange = restTemplate.exchange(hostUrl + GET_PAYOUT_STATUS_URL, HttpMethod.POST, entity, String.class, new HashMap<String,String>());
            response.addRawResponseLog(jsonObjectToPrettyString(exchange.getBody()));

            NuveiGetPayoutStatusResponse payoutStatusResponse = mapper.readValue(exchange.getBody(), NuveiGetPayoutStatusResponse.class);

            if ("ERROR".equals(payoutStatusResponse.getStatus())) {
                return handleNuveiError(request, response, payoutStatusResponse.getErrCode(), payoutStatusResponse.getReason());
            }

            switch (payoutStatusResponse.getTransactionStatus()) {
                case "APPROVED":
                    response.setAmountCentsReceived(CurrencyAmount.fromAmountString(payoutStatusResponse.getAmount()).toCents().intValue());
                    return DoProcessorResponseStatus.SUCCESS;
                case "ERROR":
                case "DECLINED":
                default:
                    return handleNuveiError(request, response, payoutStatusResponse.getGwExtendedErrorCode(), payoutStatusResponse.getGwErrorReason());
            }
        } catch (Exception e) {
            log.error("Failed to validate Nuvei payout. Exception: " + e.getMessage(), e);
            throw new NuveiVerifyTransactionException("Failed to verify nuvei payout.");
        }
    }
}


