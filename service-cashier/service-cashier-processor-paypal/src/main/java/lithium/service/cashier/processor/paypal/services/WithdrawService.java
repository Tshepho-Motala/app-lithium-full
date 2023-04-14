package lithium.service.cashier.processor.paypal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.paypal.api.payments.PaymentsApiError;
import lithium.service.cashier.processor.paypal.api.payouts.Item;
import lithium.service.cashier.processor.paypal.api.payouts.PayPalCurrencyObj;
import lithium.service.cashier.processor.paypal.api.payouts.PayoutItem;
import lithium.service.cashier.processor.paypal.api.payouts.PayoutsResponse;
import lithium.service.cashier.processor.paypal.api.payouts.SenderBatchHeader;
import lithium.service.cashier.processor.paypal.api.payouts.WithdrawalRequest;
import lithium.service.cashier.processor.paypal.api.webhook.WebhookRequest;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureRequest;
import lithium.service.cashier.processor.paypal.exceptions.PayPalServiceHttpErrorException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalTransactionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Service
@Slf4j
public class WithdrawService extends PayPalCommonService {

    public static final String RECIPIENT_TYPE_PAYPAL_ID = "PAYPAL_ID";
    public static final String PAYMENTS_PAYOUTS_PATH = "/v1/payments/payouts";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private CashierDoCallbackService cashier;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VerificationService verifyService;

    public DoProcessorResponseStatus initiateWithdrawal(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        PayoutsResponse payoutResponse = doPayout(request, response, rest);
        response.setProcessorReference(payoutResponse.getBatchHeader().getBatchId());
        response.setAdditionalReference(getAdditionalReference(request, payoutResponse));

        if ("PENDING".equals(payoutResponse.getBatchHeader().getStatus()) || "PROCESSING".equals(payoutResponse.getBatchHeader().getStatus())) {
            response.setStatus(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
        } else {
            resolveFinalStatus(request, response, payoutResponse);
        }

        log.info("Transaction" + request.getTransactionId() + " marked with " + response.getStatus());
        return response.getStatus();
    }

    private void resolveFinalStatus(DoProcessorRequest request, DoProcessorResponse response, PayoutsResponse payoutResponse) {
        if ("SUCCESS".equals(payoutResponse.getBatchHeader().getStatus())) {
            BigDecimal finalAmount = payoutResponse.getBatchHeader().getAmount().getValue();
            response.setAmountCentsReceived(CurrencyAmount.fromAmount(finalAmount).toCents().intValue());
            response.setStatus(DoProcessorResponseStatus.SUCCESS);
        } else if ("DENIED".equals(payoutResponse.getBatchHeader().getStatus())) {
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            response.setMessage("3:Transaction declined. Please contact your bank or try another payment method.");
        } else if ("CANCELED".equals(payoutResponse.getBatchHeader().getStatus())) {
            response.setStatus(DoProcessorResponseStatus.PLAYER_CANCEL);
        }
    }

    private PayoutsResponse doPayout(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        String emailSubj = request.getProperty("email_subject");
        String emailMessage = request.getProperty("email_message");
        String currency = request.getProperty("currency_code");
        String url = request.getProperty("api_url") + PAYMENTS_PAYOUTS_PATH;
        BigDecimal transactionValue = request.processorCommunicationAmount();
        String payPalId = request.getProcessorAccount().getReference();

        WithdrawalRequest withdrawalRequest = buildWithdrawalRequest(request, emailSubj, emailMessage, transactionValue, currency, payPalId);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + buildTokenContextFromRequest(request, response, false, rest));
        headers.add("content-type", "application/json");
        applyCmid(request, response, headers);

        log.debug("PayPal withdrawal init request (" + request.getTransactionId() + "): " + withdrawalRequest);
        response.addRawRequestLog("PayPal Withdrawal request: " + objectToPrettyString(withdrawalRequest));

        HttpEntity<WithdrawalRequest> entity = new HttpEntity<>(withdrawalRequest, headers);
        ResponseEntity<Object> exchange =
                rest.exchange(url, HttpMethod.POST, entity, Object.class, new HashMap<>());

        response.addRawResponseLog("PayPal withdrawal init response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Paypal withdrawal request call failed (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parsePaymentsApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.debug("PayPal withdrawal init response (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), PayoutsResponse.class);
    }

    public void handlePayoutsWebhook(WebhookRequest webhook, VerifyWebhookSignatureRequest signedWebhook) throws Exception {
        PayoutsResponse apiResponse = mapper.convertValue(webhook.getResource(), PayoutsResponse.class);
        String reference = apiResponse.getBatchHeader().getBatchId();
        log.info("Handling payouts webhook (" + reference + ")...");

        DoProcessorRequest request = getDoProcessorRequestByProcessorReference(reference);

        verifyService.verifyWebhookSignature(request, signedWebhook);

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(request.getTransactionId())
                .processorReference(request.getProcessorReference())
                .rawRequestLog("Received payouts webhook: " + objectToPrettyString(signedWebhook))
                .build();

        cashier.doSafeCallback(response);

        if (request.isTransactionFinalized()) {
            log.info("Transaction already finalized " + request.getTransactionId());
            return;
        }

        response.setStatus(checkWithdrawalStatus(request, response, restTemplate));
        log.info("After webhook receiving, transaction status to transactionID " + request.getTransactionId() + "changed to " + response.getStatus());

        cashier.doSafeCallback(response);
    }

    public DoProcessorResponseStatus checkWithdrawalStatus(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        PayoutsResponse payout = getPayout(request, response, rest);
        if (nonNull(payout.getItems()) && !payout.getItems().isEmpty()) {
            response.setAdditionalReference(payout.getItems().get(0).getTransactionId());
        }


//        The possible values are:
//
//        DENIED. Your payout requests were denied, so they were not processed. Check the error messages to see any steps necessary to fix these issues.
//        PENDING. Your payout requests were received and will be processed soon.
//        PROCESSING. Your payout requests were received and are now being processed.
//        SUCCESS. Your payout batch was processed and completed. Check the status of each item for any holds or unclaimed transactions.
//        CANCELED. The payouts file that was uploaded through the PayPal portal was cancelled by the sender.

        if ("PENDING".equals(payout.getBatchHeader().getStatus()) || "PROCESSING".equals(payout.getBatchHeader().getStatus())) {
            response.setStatus(DoProcessorResponseStatus.NOOP);
        } else resolveFinalStatus(request, response, payout);

        log.info("Transaction" + request.getTransactionId() + " marked with " + response.getStatus());
        return response.getStatus();
    }

    private PayoutsResponse getPayout(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + buildTokenContextFromRequest(request, response, false, rest));
        headers.add("content-type", "application/json");

        HttpEntity entity = new HttpEntity(headers);
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("batchId", request.getProcessorReference());

        ResponseEntity<Object> exchange =
                rest.exchange(request.getProperty("api_url") + "/v1/payments/payouts/{batchId}", HttpMethod.GET, entity, Object.class, uriVariables);
        response.addRawResponseLog("PayPal payout status check response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Paypal transaction status check call failed (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + "): " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parsePaymentsApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.debug("PayPal payout status check response (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());

        return mapper.convertValue(exchange.getBody(), PayoutsResponse.class);
    }

    private WithdrawalRequest buildWithdrawalRequest(DoProcessorRequest request, String emailSubj, String emailMessage, BigDecimal transactionValue, String currency, String payPalId) {
        WithdrawalRequest.WithdrawalRequestBuilder withdrawalRequestBuilder = WithdrawalRequest.builder();

        SenderBatchHeader batchHeader = SenderBatchHeader.builder()
                .senderBatchId(String.valueOf(request.getTransactionId()))
                .emailSubject(emailSubj)
                .emailMessage(emailMessage)
                .build();
        withdrawalRequestBuilder.senderBatchHeader(batchHeader);

        List<PayoutItem> payoutItems = new ArrayList<>();
        PayoutItem payoutItem = PayoutItem.builder()
                .recipientType(RECIPIENT_TYPE_PAYPAL_ID)
                .amount(new PayPalCurrencyObj(transactionValue, currency))
                .senderItemId(String.valueOf(request.getTransactionId()))
                .receiver(payPalId)
                .build();
        payoutItems.add(payoutItem);
        withdrawalRequestBuilder.items(payoutItems);

        return withdrawalRequestBuilder.build();
    }

    private DoProcessorRequest getDoProcessorRequestByProcessorReference(String reference) throws Exception {
        Response<DoProcessorRequest> transactionResponse = cashier.doCallbackGetTransactionFromProcessorReference(reference, "paypal");
        if (!transactionResponse.isSuccessful()) {
            throw new PayPalTransactionNotFoundException("Failed to get transaction for processor reference: " + reference);
        }
        return transactionResponse.getData();
    }

    private String parsePaymentsApiError(ResponseEntity<Object> exchange) {
        try {
            if (nonNull(exchange.getBody())) {
                PaymentsApiError paymentsApiError = mapper.convertValue(exchange.getBody(), PaymentsApiError.class);
                if (nonNull(paymentsApiError) && nonNull(paymentsApiError.getName())) {
	                String message = paymentsApiError.getMessage();
	                if (paymentsApiError.getDetails() != null && !paymentsApiError.getDetails().isEmpty()) {
		                message = paymentsApiError.getDetails().stream()
				                .map(PaymentsApiError.Details::getIssue).collect(Collectors.joining("; "));
	                }
	                return "code: " + paymentsApiError.getName() + ", message: " + message;
                }
            }
        } catch (Exception e) {
	        log.warn("Can't parse error message: " + exchange, e);
        }
        return "/v1/payments/**, http error code: " + exchange.getStatusCodeValue() + ", message: " + exchange.getStatusCode().name();
    }

	private String getAdditionalReference(DoProcessorRequest request, PayoutsResponse payoutResponse) {
		List<Item> items = payoutResponse.getItems();
		if (items != null) {
			Optional<Item> optionalItem = items.stream().filter(item -> item.getTransactionId() != null).findFirst();
			if (optionalItem.isPresent()) {
				return optionalItem.get().getTransactionId();
			}
		}
		log.warn("Cant find additional reference for transaction = " + request.getTransactionId() + " PayoutsResponse: " + payoutResponse.toString());
		return null;
	}
}
