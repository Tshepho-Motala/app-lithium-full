package lithium.service.cashier.processor.interswitch.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchTransactionResponse;
import lithium.service.cashier.processor.interswitch.exceptions.Status511InterswitchServiceException;
import lithium.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static lithium.service.cashier.processor.interswitch.data.TransactionConfirmationResponseType.SUCCESS;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public class DepositService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    LithiumConfigurationProperties lithiumProperties;

    public DoProcessorResponseStatus initiateWebDeposit(DoProcessorRequest request, DoProcessorResponse response) throws Exception {
        String transactionReference = request.getProperty("request_reference_prefix") + request.getTransactionId();
        Map<String, String> postData = new HashMap<>();
        postData.put("amount", request.inputAmount().toString());
        postData.put("currency", request.getProperty("currency_code"));
        postData.put("cust_id", request.getUser().getGuid());
        postData.put("pay_item_id", request.getProperty("pay_item_id"));
        postData.put("product_id", request.getProperty("product_id"));
        String handleRedirect = request.stageInputData(1).get("handle_redirect");
        String redirectUrl = handleRedirect != null && Boolean.parseBoolean(handleRedirect)
            ? lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-interswitch/public/" + request.getUser().getDomain() + "/" + request.getTransactionId() +"/redirect"
            : request.stageInputData(1).get("return_url");

        postData.put("site_redirect_url", redirectUrl);
        postData.put("txn_ref", transactionReference);
        postData.put("hash", Hash.builderSha512(createPayloadStage1(request, transactionReference, redirectUrl)).sha512());
        response.stageOutputData(1).putAll(postData);
        postData.replace("amount", request.inputAmountCents().toString());
        log.info("Put post data for initiate deposit transaction (" + transactionReference + "): " + postData);
        response.setIframePostData(postData);
        response.setIframeUrl(request.getProperty("deposit_widget_url"));
        return DoProcessorResponseStatus.NEXTSTAGE;
    }

    @TimeThisMethod
    public ResponseEntity<DoProcessorResponseStatus> verifyWebDeposit(DoProcessorRequest request, DoProcessorResponse response) throws Exception {
        String transactionReference = request.getProperty("request_reference_prefix") + request.getTransactionId();
        InterswitchTransactionResponse interswitchTransaction = getInterswitchTransaction(request, transactionReference, response);
        DoProcessorResponseStatus status = getProcessorResponseStatus(interswitchTransaction);
        BigDecimal finalAmount = new BigDecimal(interswitchTransaction.getAmount());
        response.setAmountCentsReceived(finalAmount.intValue());
        response.setPaymentType("card");
        response.setAdditionalReference(transactionReference);
        response.setProcessorReference(interswitchTransaction.getPaymentReference());
        response.setStatus(status);
        response.addRawResponseLog("Received status response: " + status.toString());
        return ResponseEntity.status(status.getCode()).body(status);
    }

    private DoProcessorResponseStatus getProcessorResponseStatus(InterswitchTransactionResponse interswitchTransaction) {
        if (SUCCESS.code().equals(interswitchTransaction.getResponseCode())) {
            return DoProcessorResponseStatus.SUCCESS;
        }
        return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
    }

    private InterswitchTransactionResponse getInterswitchTransaction(DoProcessorRequest request, String transactionReference, DoProcessorResponse response) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("transactionReference", transactionReference);
        map.put("amount", request.inputAmountCents().toString());
        map.put("product_id", request.getProperty("product_id"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Hash", Hash.builderSha512(createPayloadStage2(request, transactionReference)).sha512());
        HttpEntity<?> entity = new HttpEntity<>(new HashMap<>(), headers);
        log.info("Verify request(" + request.getTransactionId() + "): " + entity);
        ResponseEntity<Object> verifyResponseEntity =
                restTemplate.exchange(request.getProperty("deposit_transaction_api_url"), HttpMethod.GET, entity, Object.class, map);
        log.info("Verify response(" + request.getTransactionId()+ "): (" + verifyResponseEntity.getStatusCodeValue() + ") " + verifyResponseEntity.getBody());
        response.addRawResponseLog("Verify response: ("+verifyResponseEntity.getStatusCodeValue()+") " + objectToPrettyString(verifyResponseEntity.getBody()));
        if (!verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new Status511InterswitchServiceException("Can't verify deposit, got wrong response: " + verifyResponseEntity.getBody());
        }
        return mapper.convertValue(verifyResponseEntity.getBody(), InterswitchTransactionResponse.class);
    }

    private String createPayloadStage1(DoProcessorRequest request, String transactionReference, String redirectUrl) throws Exception {
        String productId = request.getProperty("product_id");
        String payItemId = request.getProperty("pay_item_id");
        String amount = request.inputAmountCents().toString();
        String macKey = request.getProperty("mac_key");
        return transactionReference + productId + payItemId + amount + redirectUrl + macKey;
    }

    private String createPayloadStage2(DoProcessorRequest request, String transactionReference) throws Exception {
        String productId = request.getProperty("product_id");
        String macKey = request.getProperty("mac_key");
        return productId + transactionReference + macKey;
    }
}
