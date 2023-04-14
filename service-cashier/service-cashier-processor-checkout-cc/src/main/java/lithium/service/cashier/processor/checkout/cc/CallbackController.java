package lithium.service.cashier.processor.checkout.cc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutCardSourceWebhook;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutCardVerifyWebhookData;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutSourceUpdateWebhookData;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutWebhookData;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutWebhookRequest;
import lithium.service.cashier.processor.checkout.cc.data.exceptions.CheckoutSignatureVerifyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Slf4j
@RestController
@RequestMapping("/public")
public class CallbackController {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String HMAC_ENCODING="ASCII";

    @Autowired
    CashierDoCallbackService callbackService;

    @Autowired
    CashierInternalClientService cashierService;

    @Autowired
    CheckoutApiService checkoutApiService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping("/webhook")
    public ResponseEntity webhook(@RequestBody String data, @RequestHeader("CKO-Signature") String signature) throws Exception {
        try {
            log.info("Checkout webhook is called: " + data);
            ObjectMapper mapper = new ObjectMapper();
            CheckoutWebhookRequest request = mapper.readValue(data, CheckoutWebhookRequest.class);

            if (request.getType().equals("payment_paid") || request.getType().equals("payment_declined")
                || request.getType().equals("payment_captured") || request.getType().equals("payment_voided")
                || request.getType().equals("payment_capture_declined") || request.getType().equals("payment_void_declined")
                || request.getType().equals("payment_approved")) {
                CheckoutWebhookData webhookData = mapper.readValue(request.getData().toString(), CheckoutWebhookData.class);
                processWebhook(webhookData, request.getType(), signature, data);
            } else if (request.getType().equals("source_updated")) {
                CheckoutSourceUpdateWebhookData sourceUpdateData = mapper.readValue(request.getData().toString(), CheckoutSourceUpdateWebhookData.class);
                processSourceUpdateWebhook(sourceUpdateData, signature, data);
            } else if (request.getType().equals("card_verified")) {
                CheckoutCardVerifyWebhookData cardVerifyData = mapper.readValue(request.getData().toString(), CheckoutCardVerifyWebhookData.class);

                String ref = cardVerifyData.getReference();
                CheckoutCardSourceWebhook cardSource = cardVerifyData.getSource();
                UserCard userCard = UserCard.builder()
                        .reference(cardSource.getId())
                        .cardType(cardSource.getCard_type())
                        .lastFourDigits(cardSource.getLast_4())
                        .bin(cardSource.getBin())
                        .expiryDate(String.format("%02d/%02d", cardSource.getExpiry_month(), cardSource.getExpiry_year() % 100))
                        .name(cardSource.getName())
                        .scheme(cardSource.getScheme())
                        .fingerprint(cardSource.getFingerprint())
		                .issuingCountry(cardSource.getIssuerCountry())
                        .isDefault(true)
                        .build();

                cashierService.saveUserCard(cardVerifyData.getMetadata().get("userGuid").toString(), Long.parseLong(cardVerifyData.getReference()), userCard);
            }
        } catch(CheckoutSignatureVerifyException e) {
            log.error("Failed to proceed checkout webhook. Request: " + data + " Exception: "+ String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch(Exception e) {
            log.error("Failed to proceed checkout webhook. Request: " + data + " Exception: "+ String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }


    @RequestMapping("/{transactionId}/success")
    public ModelAndView depositSuccess(
            @PathVariable("transactionId") Long transactionId,
            @RequestParam(name = "cko-session-id", required = true) String sessionId) throws Exception  {
        try {
            log.debug("Received success redirect from checkout.com for transaction: " + transactionId + " session_id: " + sessionId);
            return new ModelAndView(processCheckoutDepositRedirect(transactionId, sessionId, true));
        } catch (Exception ex) {
            log.error("Failed to process success redirect from checkout. TransactionId: " + transactionId + " sessionId: " + sessionId, ex);
            throw ex;
        }
    }

    @RequestMapping("/{transactionId}/failed")
    public ModelAndView depositFailed(
            @PathVariable("transactionId") Long transactionId,
            @RequestParam(name = "cko-session-id", required = false) String sessionId) throws Exception
    {
        try {
            log.debug("Received failed redirect from checkout.com for transaction: " + transactionId + " session_id: " + sessionId);

            return new ModelAndView(processCheckoutDepositRedirect(transactionId, sessionId, false));

        } catch (Exception ex) {
            log.error("Failed to process failed redirect from checkout. TransactionId: " + transactionId, ex);
            throw ex;
        }
    }

    private void checkSignature(String request, String Signature, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),HMAC_SHA256_ALGORITHM);
        sha256_HMAC.init(secret_key);
        byte[] res = sha256_HMAC.doFinal(request.getBytes(StandardCharsets.UTF_8));
        String hash = toHexString(res);
        if (!hash.equals(Signature)) {
            throw new CheckoutSignatureVerifyException("Signature check is failed for webhook request: " + request);
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    private void processSourceUpdateWebhook(CheckoutSourceUpdateWebhookData updatedSource, String signature, String data) throws Exception {
        List<ProcessorAccount> processorAccounts = cashierService.getProcessorAccountsByReference(updatedSource.getId());

        if (processorAccounts.isEmpty()) {
            log.info("No processor account found for source reference: " + updatedSource.getId());
            return;
        }

        if (processorAccounts.size() > 1) {
            throw new Exception("More than one processor accounts found for source reference: " + updatedSource.getId());
        }

        ProcessorAccount processorAccount = processorAccounts.get(0);
        String domainName = processorAccount.getUserGuid().split("/",2)[0];

        Map<String, String> dmpProperties = cashierService.propertiesOfFirstEnabledProcessorByMethodCode(domainName, true, "checkout-cc");

        checkSignature(data, signature, dmpProperties.get("secret_key"));

        updateProcessorAccountDataWithSource(processorAccount.getData(), updatedSource);

        cashierService.updateExpiredUserCard(processorAccount);
    }

    private void updateProcessorAccountDataWithSource(Map<String, String> processorAccountData, CheckoutSourceUpdateWebhookData updatedSource) {
        processorAccountData.put("expiryDate", String.format("%02d/%02d", updatedSource.getExpiryMonth(), updatedSource.getExpiryYear() % 100));
        processorAccountData.put("fingerprint", updatedSource.getFingerprint());
        processorAccountData.put("last4Digits", updatedSource.getLast4Digits());
        processorAccountData.put("bin", updatedSource.getBin());
    }

    private void processWebhook(CheckoutWebhookData webhookData, String type, String signature, String data) throws Exception {
        Long transactionId = Long.parseLong(webhookData.getReference());
        String processorReference = webhookData.getId();

        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "checkout-cc");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transactionid: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }

        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        checkSignature(data, signature, doProcessorRequest.getProperty("secret_key"));

        if (!webhookData.getId().equalsIgnoreCase(doProcessorRequest.getProcessorReference()) && doProcessorRequest.stageOutputData(1).containsKey("soft_decline")) {
            log.info("Ignore " + type + " webhook received for the soft declined transaction id:"  + transactionId);
            return;
        }

        Map<Integer, Map<String, String>> outputData = new HashMap<>();
        Map<String, String> output = new HashMap<>();

        output.put("responseCode", webhookData.getResponse_code());
        output.put("responseSummary", webhookData.getResponse_summary());

        outputData.put(2, output);

        DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
                .transactionId(transactionId)
                .processorReference(processorReference)
                .rawRequestLog("Received " + type + " webhook call: " + jsonObjectToPrettyString(data))
                .outputData(outputData)
                .build();

        DoProcessorResponseStatus status = type.equals("payment_approved")
            ? checkoutApiService.handlePaymentApproved(doProcessorRequest, doProcessorResponse)
            : checkoutApiService.verifyPayment(doProcessorRequest, doProcessorResponse, processorReference);

        if (!doProcessorRequest.isTransactionFinalized()) {
            doProcessorResponse.setStatus(status);
        }

        Response<DoResponse> response = callbackService.doSafeCallback(doProcessorResponse);
        log.debug("Received response from service-cashier: " + response.toString());
    }

    private String processCheckoutDepositRedirect(Long transactionId, String sessionId, boolean isSuccess) throws Exception{

        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "checkout-cc");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transactionid: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        try {
            DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
                    .rawRequestLog("Received " + (isSuccess ? "successful" : "failed") + " redirect from Checkout. SessionId: " + sessionId)
                    .transactionId(transactionId)
                    .build();

            DoProcessorResponseStatus status = checkoutApiService.verifyPayment(doProcessorRequest,doProcessorResponse, sessionId);

            if (!doProcessorRequest.isTransactionFinalized()) {
                doProcessorResponse.setStatus(status);
            }

            log.debug("Sending request to service-cashier: " + doProcessorRequest.toString());
            Response<DoResponse> response = callbackService.doSafeCallback(doProcessorResponse);
            log.debug("Received response from service-cashier: " + response.toString());

            if  (status == DoProcessorResponseStatus.SUCCESS) {
                return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=success";
            } else if (status == DoProcessorResponseStatus.DECLINED) {
                String redirect =  "redirect:" + doProcessorRequest.stageInputData(1).get("return_url")
                                   + "?status=failed";
                if (doProcessorResponse.getMessage() != null) {
                    redirect += "&error=" +  doProcessorResponse.getMessage();
                }
                 return redirect;
            } else if (status == DoProcessorResponseStatus.NOOP &&
                    doProcessorResponse.stageOutputData(2, "paymentStatus").equalsIgnoreCase("authorized")) {
                return Boolean.parseBoolean(doProcessorRequest.getProperty("pending_on_redirect"))
                       ? "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=pending&tnx_id=" + doProcessorResponse.getTransactionId()
                       : "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=success";
            }
        } catch (Exception ex) {
            log.error("Failed to process redirect from checkout. TransactionId: " + transactionId, ex);
        }
        return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=failed&error=" + GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, doProcessorRequest.getUser().getDomain(), doProcessorRequest.getUser().getLanguage());
    }
}
