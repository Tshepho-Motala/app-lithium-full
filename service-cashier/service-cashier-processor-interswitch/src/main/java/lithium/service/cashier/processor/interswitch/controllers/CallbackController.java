package lithium.service.cashier.processor.interswitch.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.api.schema.DepositVerifyResponse;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchWebDepositCallbackRequest;
import lithium.service.cashier.processor.interswitch.data.TransactionConfirmationResponseType;
import lithium.service.cashier.processor.interswitch.services.DepositService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@RestController
@Slf4j
public class CallbackController {

    @Autowired
    private DepositService depositService;
    @Autowired
    private CashierDoCallbackService cashier;
    @Autowired
    private CashierInternalClientService cashierService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    MessageSource messageSource;

    @PostMapping(path = "/public/verifyDeposit")
    public DepositVerifyResponse verifyDeposit(@RequestBody Object callback, LithiumTokenUtil tokenUtil) throws Exception {
        log.info("Callback received: " + callback);
        String requestReferencePrefix = getRequestReferencePrefix(tokenUtil.domainName());
        InterswitchWebDepositCallbackRequest callbackRequest = mapper.convertValue(callback, InterswitchWebDepositCallbackRequest.class);
        removeReferencePrefixFromTxnRef(requestReferencePrefix, callbackRequest);

        long transactionId = Long.parseLong(callbackRequest.getTxnref());
        DoProcessorResponse.DoProcessorResponseBuilder responseBuilder = DoProcessorResponse.builder()
                .transactionId(transactionId).rawResponseLog("Received redirect return: " + objectToPrettyString(callbackRequest));
        DoProcessorRequest request = cashier.getTransaction(transactionId, "interswitch");

        String statusDesc = TransactionConfirmationResponseType.getDescByCode(callbackRequest.getResp());

        responseBuilder.amountCentsReceived(CurrencyAmount.fromCentsString(callbackRequest.getAmount()).toAmount().intValue());

        String processorReference = callbackRequest.getPayRef();
        DoProcessorResponse response = responseBuilder.processorReference(processorReference).build();
        response.setOutputData(2, "processor_redirect_status", statusDesc);
        response.addRawRequestLog("Got callback: " + objectToPrettyString(callback));
        cashier.doCallback(response);
        request.setProcessorReference(processorReference);

        try {
            DoProcessorResponseStatus status = depositService.verifyWebDeposit(request, response).getBody();
            return DepositVerifyResponse.builder().status(status.toString())
                    .transactionReference(callbackRequest.getTxnref())
                    .paymentReference(callbackRequest.getPayRef())
                    .responseCode(callbackRequest.getResp())
                    .responseDescription(TransactionConfirmationResponseType.getDescByCode(callbackRequest.getResp()))
                    .build();
        } finally {
            if (request.isTransactionFinalized() && nonNull(response.getStatus())) {
                log.warn("Transaction (" + request.getTransactionId() + ") already finalized and can't be change status to " + response.getStatus().name());
                response.addRawResponseLog("Transaction already finalized and can't be change status to " + response.getStatus().name());
                response.setStatus(null);
            }
            cashier.doCallback(response);
        }
    }

    private void removeReferencePrefixFromTxnRef(String requestReferencePrefix, InterswitchWebDepositCallbackRequest callbackRequest) {
        callbackRequest.setTxnref(callbackRequest.getTxnref().replace(requestReferencePrefix, ""));
    }

    private String getRequestReferencePrefix(String domainName) throws Exception {
        DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(domainName, true, "interswitch", "interswitch");
        if (dmp != null && dmp.getProperties() != null && dmp.getProperties().get("request_reference_prefix") != null) {
            return dmp.getProperties().get("request_reference_prefix");
        }
        return "";
    }

    @PostMapping(path = "/public/{domainName}/{transactionId}/redirect",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ModelAndView redirect(@PathVariable String domainName, @PathVariable Long transactionId, InterswitchWebDepositCallbackRequest callbackRequest) throws Exception {
        log.info("Recieved Interswitch Post redirect: ",  callbackRequest.toString());
        try {
            String requestReferencePrefix = getRequestReferencePrefix(domainName);
            removeReferencePrefixFromTxnRef(requestReferencePrefix, callbackRequest);

            DoProcessorRequest request;
            if (callbackRequest.getTxnref() == null || callbackRequest.getTxnref().isEmpty()) {
                request = cashier.getTransaction(transactionId, "interswitch");
                return new ModelAndView("redirect:" + request.stageInputData(1).get("return_url") + "?status=failed&error=" + GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            } else {
                request = cashier.getTransaction(Long.parseLong(callbackRequest.getTxnref()), "interswitch");
            }

            return new ModelAndView(processInterswitchRediret(callbackRequest, request));
        } catch (Exception e) {
            log.error("Failed to process redirect from Interswitch (transactionId : " + transactionId + ", domain: " + domainName + ", body: " + callbackRequest.toString() + ")", e);
            throw e;
        }
    }

    private String processInterswitchRediret(InterswitchWebDepositCallbackRequest callbackRequest, DoProcessorRequest request) throws Exception {

        DoProcessorResponse.DoProcessorResponseBuilder responseBuilder = DoProcessorResponse.builder()
                .transactionId(request.getTransactionId());

        try {
            String statusDesc = TransactionConfirmationResponseType.getDescByCode(callbackRequest.getResp());
            String processorReference = callbackRequest.getPayRef();
            DoProcessorResponse response = responseBuilder.processorReference(processorReference).build();
            response.addRawResponseLog("Received redirect return: " + objectToPrettyString(callbackRequest));
            response.setOutputData(2, "processor_redirect_status", statusDesc);
            response.addRawRequestLog("Got Interswitch redirect: " + objectToPrettyString(callbackRequest));
            cashier.doCallback(response);
            request.setProcessorReference(processorReference);

            DoProcessorResponseStatus status = depositService.verifyWebDeposit(request, response).getBody();
            cashier.doCallback(response);

            if (status == DoProcessorResponseStatus.SUCCESS) {
                return "redirect:" + request.stageInputData(1).get("return_url") + "?status=success";
            } else if (status == DoProcessorResponseStatus.DECLINED) {
                return "redirect:" + request.stageInputData(1).get("return_url") + "?status=failed&error=" + GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage());
            } else if (status == DoProcessorResponseStatus.NOOP || status == DoProcessorResponseStatus.PENDING_AUTO_RETRY) {
                return "redirect:" + request.stageInputData(1).get("return_url") + "?status=pending&trn_id=" + response.getTransactionId();
            }

        } catch (Exception ex) {
            log.error("Failed to process redirect from Interswitch. TransactionId: " + request.getTransactionId(), ex);
        }
        return "redirect:" + request.stageInputData(1).get("return_url") + "?status=failed&error=" + GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage());
    }
}
