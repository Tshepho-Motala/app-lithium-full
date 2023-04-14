package lithium.service.cashier.processor.paypal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.paypal.api.Link;
import lithium.service.cashier.processor.paypal.api.Payer;
import lithium.service.cashier.processor.paypal.api.orders.CapturePaymentResponse;
import lithium.service.cashier.processor.paypal.api.orders.OrderDetailsResponse;
import lithium.service.cashier.processor.paypal.api.orders.OrderResponse;
import lithium.service.cashier.processor.paypal.api.webhook.CaptureResource;
import lithium.service.cashier.processor.paypal.api.webhook.WebhookRequest;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureRequest;
import lithium.service.cashier.processor.paypal.exceptions.PayPalCaptureOrderException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalMissingDataException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalServiceException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalTransactionNotFoundException;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_INVALID_ACCOUNT;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getError;

import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Service
@Slf4j
public class DepositService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CashierInternalClientService cashierService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CashierDoCallbackService cashier;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private VerificationService verificationService;


    public DoProcessorResponseStatus initiateDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        boolean usingBA = nonNull(request.getProcessorAccount()) && !StringUtil.isEmpty(request.getProcessorAccount().getProviderData());
        response.setRemark(TransactionRemarkData.builder().remark("isBillingAgreement: " + usingBA).type(TransactionRemarkType.ADDITIONAL_INFO).build());
        OrderResponse order = orderService.createOrder(request, response, usingBA, rest);
        if ("CREATED".equals(order.getStatus())) {
            response.setProcessorReference(order.getId());

            String url = order.getLinks().stream()
                    .filter(link -> "approve".equals(link.getRel()))
                    .findAny()
                    .map(Link::getHref)
                    .orElseThrow(() -> new PayPalMissingDataException("Can't find approve link for order(" + request.getTransactionId() + ")"));

            if (usingBA) {
                response.setStatus(DoProcessorResponseStatus.NEXTSTAGE);
            } else {
                response.setIframeUrl(url);
                response.setStatus(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
                HashMap<String, String> map = new HashMap<>();
                map.put("orderId", order.getId());
                response.setIframePostData(map);
            }
            return response.getStatus();
        }
        throw new PayPalServiceException("Unexpected initiated PayPal order's status (" + request.getTransactionId() + "): " + order.getStatus());
    }

    public DoProcessorResponseStatus captureOrder(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        if (request.isTransactionFinalized()) {
            log.info("Transaction already finalized (" + request.getTransactionId() + ")");
            response.addRawResponseLog("Capture order avoid due transaction already finalized");
            return DoProcessorResponseStatus.NOOP;
        }

        OrderDetailsResponse orderDetailsResponse = orderService.getOrder(request, response, rest);
        if (request.getProcessorAccount() == null) {
            if (!orderDetailsResponse.getStatus().equalsIgnoreCase("APPROVED")) {
                log.info("Paypal payment in not approved yet. State:" + orderDetailsResponse.getStatus() + " Do nothing.");
                response.addRawResponseLog("Paypal payment in not approved yet. State:" + orderDetailsResponse.getStatus() + " Do nothing.");
                return DoProcessorResponseStatus.NOOP;
            }
            if (orderDetailsResponse.getPayer() == null) {
                throw new Exception("There is no related payer for order (" + orderDetailsResponse.getId() + ")");
            }
        }

        String billingAgreementId = ofNullable(request.getProcessorAccount()).map(ProcessorAccount::getProviderData).orElse(null);
        try {
            if (request.getProcessorAccount() == null && !verifyProcessorAccount(request, response, orderDetailsResponse.getPayer())) {
                return response.getStatus();
            }

            BigDecimal finalAmount = orderDetailsResponse.getPurchaseUnits().get(0).getAmount().getValue();
            response.setAmountCentsReceived(CurrencyAmount.fromAmount(finalAmount).toCents().intValue());

            CapturePaymentResponse capturePaymentResponse = orderService.capturePayment(request, response, rest, billingAgreementId);
            String captureId = findCaptureId(capturePaymentResponse);
            response.setAdditionalReference(captureId);
	        response.setRemark(TransactionRemarkData.builder().remark(buildBillingAddress(capturePaymentResponse.getPayer().getAddress())).type(TransactionRemarkType.ADDITIONAL_INFO).build());

            String status = capturePaymentResponse.getStatus();
            if ("COMPLETED".equals(status)) {
                response.setStatus(DoProcessorResponseStatus.SUCCESS);
            } else if ("CREATED".equals(status) || "SAVED".equals(status) || "APPROVED".equals(status) || "PAYER_ACTION_REQUIRED".equals(status)) {
                response.setStatus(DoProcessorResponseStatus.NOOP);
            } else if ("VOIDED".equals(status)) {
                response.setStatus(DoProcessorResponseStatus.DECLINED);
                response.setDeclineReason("Voided by PayPal service");
                response.setErrorCode(GeneralError.CONTACT_YOUR_BANK.getCode());
                response.setMessage(GeneralError.CONTACT_YOUR_BANK.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.info("PayPal Order voided on capture (" + request.getTransactionId() + ")");
            }
            return response.getStatus();

        } catch (PayPalCaptureOrderException e) {
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            response.setDeclineReason(e.getMessage());
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            log.warn("Decline transaction (" + request.getTransactionId() + ") due got error when capture order: " + e.getMessage());
            return response.getStatus();
        } catch (Exception e) {
            log.error("Failed to capture order (" + request.getTransactionId() + ") due internal issue. Try again later " + e.getMessage(), e);
            response.setDeclineReason("Failed to capture order due internal issue. Try again later");
            response.setMessage("Failed to capture order due internal issue. Try again later");
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setStatus(DoProcessorResponseStatus.FATALERROR);
            return response.getStatus();
        }
    }

    private String buildBillingAddress(Payer.Address address) {

			StringBuilder sb = new StringBuilder();
			addAddressVariable(sb, address.getAddressLine1());
			addAddressVariable(sb, address.getAddressLine2());
			addAddressVariable(sb, address.getAdminArea1());
			addAddressVariable(sb, address.getAdminArea2());
			addAddressVariable(sb, address.getPostalCode());
			addAddressVariable(sb, address.getCountryCode());

			if (sb.length() != 0) {
				sb.insert(0, "Billing address: ");
				return sb.toString();
			} else {
				return "Billing address: Address not defined";
			}
		}

	private void addAddressVariable(StringBuilder sb, String addressVariable)  {
		if (addressVariable != null) {
			if (sb.length() != 0 ) sb.append(", ");
			sb.append(addressVariable);
		}
	}

	private String findCaptureId(CapturePaymentResponse capturePaymentResponse) {
        if (!capturePaymentResponse.getPurchaseUnits().isEmpty() &&
                !capturePaymentResponse.getPurchaseUnits().get(0).getPayments().getCaptures().isEmpty()) {
            return capturePaymentResponse.getPurchaseUnits().get(0).getPayments().getCaptures().get(0).getId();
        }
        log.warn("Can't find captureId (" + capturePaymentResponse.getId() + ")");
        return null;
    }

    public DoProcessorResponse handleOrderCallback(String orderId, String action, String payerID) throws Exception {
        DoProcessorRequest request = getDoProcessorRequestByReferenceId(orderId);
        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(request.getTransactionId())
                .processorReference(request.getProcessorReference())
                .rawRequestLog("Got redirect from Paypal Order (" + action + "): payerID = " + payerID)
                .build();

        cashier.doSafeCallback(response);

        if ("return".equals(action)) {
            captureOrder(request, response, restTemplate);
        } else if ("cancel".equals(action)) {
            response.setStatus(DoProcessorResponseStatus.PLAYER_CANCEL);
            response.setDeclineReason("Transaction canceled by player");
            response.setMessage("Transaction canceled by player");
        } else {
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setDeclineReason("Transaction declined due unexpected action: " + action);
        }
        if (request.isTransactionFinalized()) {
            response.setStatus(null);
        }
        cashier.doSafeCallback(response);
        return response;
    }

    public void handleOrderApprovedWebhook(WebhookRequest webhook, VerifyWebhookSignatureRequest signedWebhook) throws Exception {
        OrderDetailsResponse resource = mapper.convertValue(webhook.getResource(), OrderDetailsResponse.class);
        log.info("Handling order approved webhook (" + resource.getId() + ")...");

        DoProcessorRequest request = getDoProcessorRequestByReferenceId(resource.getId());

        verificationService.verifyWebhookSignature(request, signedWebhook);

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(request.getTransactionId())
                .processorReference(request.getProcessorReference())
                .rawRequestLog("Received order approved webhook: " + jsonObjectToPrettyString(signedWebhook.getWebhook()))
                .build();

        cashier.doSafeCallback(response);

        if ("APPROVED".equalsIgnoreCase(resource.getStatus())) {
            captureOrder(request, response, restTemplate);
        } else {
            response.addRawResponseLog("Avoid capture proceed due unexpected state of order: " + resource.getStatus());
        }

        if (request.isTransactionFinalized()) {
            response.setStatus(null);
        }
        cashier.doSafeCallback(response);

    }

    public void handleCaptureWebhook(WebhookRequest webhook, VerifyWebhookSignatureRequest signedWebhook) throws Exception {
        CaptureResource resource = mapper.convertValue(webhook.getResource(), CaptureResource.class);
        log.info("Handling capture webhook (" + resource.getId() + ")...");

        DoProcessorRequest request = getDoProcessorRequestByAdditionalReference(resource.getId());

        verificationService.verifyWebhookSignature(request, signedWebhook);

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(request.getTransactionId())
                .processorReference(request.getProcessorReference())
                .additionalReference(resource.getId())
                .rawRequestLog("Received capture webhook: " + jsonObjectToPrettyString(signedWebhook.getWebhook()))
                .build();

        cashier.doSafeCallback(response);

        if (request.isTransactionFinalized()) {
            log.info("Transaction already finalized (" + request.getTransactionId() + ")");
            return;
        }

        OrderDetailsResponse orderDetails = orderService.getOrder(request, response, restTemplate);
        if ("COMPLETED".equals(orderDetails.getStatus())) {
            BigDecimal amount = orderDetails.getPurchaseUnits().get(0).getAmount().getValue();
            response.setAmountCentsReceived(CurrencyAmount.fromAmount(amount).toCents().intValue());
            response.setStatus(DoProcessorResponseStatus.SUCCESS);
            log.info("PayPal Order captured (" + request.getTransactionId() + ")");
        } else if ("VOIDED".equals(orderDetails.getStatus())) {
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            response.setDeclineReason("Voided by PayPal service");
            response.setErrorCode(GeneralError.CONTACT_YOUR_BANK.getCode());
            response.setMessage(GeneralError.CONTACT_YOUR_BANK.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            log.info("PayPal Order voided (" + request.getTransactionId() + ")");
        } else {
            log.warn("PayPal Order in unexpected state (" + request.getTransactionId() + "): " + orderDetails.getStatus());
        }

        cashier.doSafeCallback(response);
    }

    private DoProcessorRequest getDoProcessorRequestByAdditionalReference(String additionalReference) throws Exception {
        Response<DoProcessorRequest> transactionResponse = cashier.doCallbackGetTransactionFromAdditionalReference(additionalReference, "paypal");
        if (!transactionResponse.isSuccessful()) {
            throw new PayPalTransactionNotFoundException("Failed to get transaction for additional reference: " + additionalReference);
        }
        return transactionResponse.getData();
    }

    private DoProcessorRequest getDoProcessorRequestByReferenceId(String processorReference) throws Exception {
        Response<DoProcessorRequest> transactionResponse = cashier.doCallbackGetTransactionFromProcessorReference(processorReference, "paypal");
        if (!transactionResponse.isSuccessful()) {
            throw new PayPalTransactionNotFoundException("Failed to get transaction by processor reference: " + processorReference);
        }
        return transactionResponse.getData();
    }

    private boolean verifyProcessorAccount(DoProcessorRequest request, DoProcessorResponse response, Payer payer) throws Exception {
        VerifyProcessorAccountRequest verifyRequest = VerifyProcessorAccountRequest.builder()
            .processorAccount(ProcessorAccount.builder()
                .reference(payer.getPayerId())
                .status(PaymentMethodStatusType.ACTIVE)
                .type(ProcessorAccountType.PAYPAL)
                .descriptor(payer.getPayerId())
                .hideInDeposit(true)
                .name(payer.getName().getGivenName() + " " + payer.getName().getSurname())
                .data(new HashMap<String, String>() {{
                    put("payer-email", payer.getEmailAddress());
                    put("payer-firstName", payer.getName().getGivenName());
                    put("payer-lastName", payer.getName().getSurname());
                    put("payer-payerId", payer.getPayerId());
                 }})
                .build())
            .verifications(getAccountVerifications(request))
            .userGuid(request.getUser().getRealGuid())
            .build();
        VerifyProcessorAccountResponse verifyResponse = cashierService.verifyAccount(verifyRequest);
        response.setProcessorAccount(verifyResponse.getProcessorAccount());

        if (BooleanUtils.isFalse(verifyResponse.getResult())) {
            ProcessorAccountVerificationType failedVerification = verifyResponse.getProcessorAccount().getFailedVerification();
            log.error("Account is invalid. Verification: " + failedVerification + " Processor account: " + verifyResponse.getProcessorAccount());
            response.setErrorCode(failedVerification.getGeneralError().getCode());
            response.setMessage(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            response.setDeclineReason(getError(CASHIER_INVALID_ACCOUNT) + ": " + failedVerification.getDescription());
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            return false;
        }
        return true;
    }

    private List<ProcessorAccountVerificationType> getAccountVerifications(DoProcessorRequest request) throws Exception {
        String accountVerifications = request.getProperties().get("account_verifications");
        if (StringUtil.isEmpty(accountVerifications)) {
            return Collections.emptyList();
        }
        return Arrays.asList(accountVerifications.split("\\s*,\\s*")).stream().map(ProcessorAccountVerificationType::fromName).collect(Collectors.toList());
    }
}

