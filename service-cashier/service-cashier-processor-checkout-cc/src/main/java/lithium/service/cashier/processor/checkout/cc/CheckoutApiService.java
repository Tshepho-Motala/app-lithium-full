package lithium.service.cashier.processor.checkout.cc;

import com.checkout.CheckoutApi;
import com.checkout.CheckoutApiException;
import com.checkout.CheckoutApiImpl;
import com.checkout.CheckoutValidationException;
import com.checkout.common.Address;
import com.checkout.payments.AlternativePaymentSource;
import com.checkout.payments.CaptureResponse;
import com.checkout.payments.CardSourceResponse;
import com.checkout.payments.CustomerRequest;
import com.checkout.payments.GetPaymentResponse;
import com.checkout.payments.PaymentActionSummary;
import com.checkout.payments.PaymentRequest;
import com.checkout.payments.PaymentResponse;
import com.checkout.payments.RequestSource;
import com.checkout.payments.ResponseSource;
import com.checkout.payments.ThreeDSEnrollment;
import com.checkout.payments.TokenSource;
import com.checkout.payments.VoidResponse;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.objects.TransactionRemarkData;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.checkout.cc.data.AuthenticationResponses;
import lithium.service.cashier.processor.checkout.cc.data.CheckoutErrors;
import lithium.service.cashier.processor.checkout.cc.data.CvvMaskedIdSource;
import lithium.service.cashier.processor.checkout.cc.data.ThreeDSRequestV2;
import lithium.service.cashier.processor.checkout.cc.data.exceptions.CheckCardOwnerException;
import lithium.service.cashier.processor.checkout.cc.data.exceptions.Status500VerifyException;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.CASHIER_INVALID_ACCOUNT;
import static lithium.service.cashier.client.objects.enums.DeclineReasonErrorType.getError;

import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public class CheckoutApiService {

    @Autowired
    CashierInternalClientService cashierService;
	@Autowired
	MessageSource messageSource;

    @Autowired
    LithiumConfigurationProperties lithiumProperties;

    @Autowired
    CheckoutApiService checkoutApiService;

    public DoProcessorResponseStatus deposit(DoProcessorRequest request, DoProcessorResponse response, String paymentToken, boolean use3DSecure) throws Exception {
        boolean verifyAccount = !StringUtil.isEmpty(paymentToken);
        try {
            CheckoutApi checkoutApi = CheckoutApiImpl.create(request.getProperty("secret_key"), Boolean.parseBoolean(request.getProperty("use_sendbox")), request.getProperty("public_key"));

            PaymentRequest<?> paymentRequest = createPaymentRequest(request, response, paymentToken, use3DSecure);

            String idempotencyKey = UUID.nameUUIDFromBytes(request.getTransactionId().toString().getBytes()).toString();
            if (response.stageOutputData(1).containsKey("soft_decline")) {
                idempotencyKey += "_sd";
            }
            log.info("Checkout payment request (" + request.getTransactionId() +"): " + paymentRequest.toString());
            PaymentResponse apiResponse = checkoutApi.paymentsClient().requestAsync(paymentRequest, idempotencyKey).get();
            log.info("Checkout payment response (" + request.getTransactionId() +"): " + apiResponse.toString());
            response.setRawResponseLog(objectToPrettyString(apiResponse));
            response.setPaymentType("card");

            if (apiResponse.isPending()) {
                log.debug("Payment is in the pending state, 3D secure check will be initiated.");
                response.setOutputData(1, "paymentStatus", apiResponse.getPending().getStatus());
                response.setProcessorReference(apiResponse.getPending().getId());
                //just verify, without saving. We can not store account here
                //card reference is not returned from checkout yet (there is all other info needed for verification including fingerprint)
                if (verifyAccount && !verifyAccount(request, response, null, apiResponse.getPending().getId(), checkoutApi)) {
                    return DoProcessorResponseStatus.DECLINED;
                }
                response.setIframeUrl(apiResponse.getPending().getRedirectLink().getHref());
                response.setProcessorUserId(apiResponse.getPending().getCustomer().getId());
                response.setIframeMethod("GET");
                return DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE;
            } else if (apiResponse.getPayment().isApproved() && apiResponse.getPayment().getResponseSummary() != null
                    && apiResponse.getPayment().getResponseSummary().contains("40111")) {
                // in case of AVS check is failed we will receive approved=true and 40111 in the response summary
                response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
                response.setProcessorReference(apiResponse.getPayment().getId());
                String errorCode = "40111";
                String errorMessage = apiResponse.getPayment().getResponseSummary().substring(apiResponse.getPayment().getResponseSummary().indexOf(" - ") + 3);
                response.setOutputData(1, "responseCode", "40111");
                response.setOutputData(1, "responseSummary", errorMessage);
                response.setErrorCode(GeneralError.AVS_CHECK_FAILED.getCode());
                response.setMessage(GeneralError.AVS_CHECK_FAILED.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                //will wait for void notification
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if (apiResponse.getPayment().isApproved()) {
                response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
                response.setProcessorReference(apiResponse.getPayment().getId());
                response.setProcessorUserId(apiResponse.getPayment().getCustomer().getId());
                if (verifyAccount) {
                   verifyAccount(request, response, null, apiResponse.getPayment().getId(), checkoutApi);
                }
                //next state waiting for webhook
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            }  else if (!use3DSecure && "20154".equalsIgnoreCase(apiResponse.getPayment().getResponseCode())) {
                response.setOutputData(1, "soft_decline", "true");
                log.info("Soft decline is received for transaction id: " + request.getTransactionId());
                return DoProcessorResponseStatus.DECLINED;
            } else {
                response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
                response.setProcessorReference(apiResponse.getPayment().getId());
                response.setOutputData(1, "responseCode", apiResponse.getPayment().getResponseCode());
                response.setOutputData(1, "responseSummary", apiResponse.getPayment().getResponseSummary());
                response.setErrorCode(CheckoutErrors.fromErrorCode(apiResponse.getPayment().getResponseCode()).getGeneralError().getCode());
                response.setMessage(CheckoutErrors.fromErrorCode(apiResponse.getPayment().getResponseCode()).getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setDeclineReason(apiResponse.getPayment().getResponseCode() + ": " + apiResponse.getPayment().getResponseSummary());
                return DoProcessorResponseStatus.DECLINED;
            }
        } catch (Exception ex) {
            if (ex instanceof ExecutionException && ex.getCause() instanceof CheckoutApiException) {
               return handleCheckoutExeption(request, response, (CheckoutApiException)ex.getCause());
            }
            log.error("Unable to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + ex.getMessage(), ex);
            response.addRawResponseLog( "Exception: " + ex.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(ex)));
            return DoProcessorResponseStatus.FATALERROR;
        }

    }

    public DoProcessorResponseStatus verifyPayment(DoProcessorRequest request, DoProcessorResponse response, String sessionId) throws Status500VerifyException {
        try {
            String secretKey = request.getProperty("secret_key");
            String publicKey = request.getProperty("public_key");
            boolean useSandbox = Boolean.parseBoolean(request.getProperty("use_sendbox"));

            CheckoutApi api = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);
            String paymentId = sessionId != null && !sessionId.isEmpty() ? sessionId : request.getProcessorReference();
            GetPaymentResponse payment = api.paymentsClient().getAsync(paymentId).get();
            log.info("Checkout payment details response (" + request.getTransactionId() +"): " + payment.toString());
            response.setOutputData(request.getStage(), "paymentStatus", payment.getStatus());
            response.setRawResponseLog(objectToPrettyString(payment));

            if (payment.getStatus().equalsIgnoreCase("captured")
                || payment.getStatus().equalsIgnoreCase("paid")) {
                response.setAmountCentsReceived(payment.getAmount().intValue());
                return DoProcessorResponseStatus.SUCCESS;
            } else if (payment.getStatus().equalsIgnoreCase("declined")) {
                setDeclineReasonFromActions(request, response, payment, false);
                if (payment.getSource() != null && !StringUtil.isEmpty(request.stageInputData(1).get("paymentToken"))) {
                    response.setRemark(TransactionRemarkData.builder().remark(buildProcessorAccountRemark(processorAccountFromCardSource((CardSourceResponse)payment.getSource()))).type(TransactionRemarkType.ACCOUNT_DATA).build());
                }
                return DoProcessorResponseStatus.DECLINED;
            } else if (payment.getStatus().equalsIgnoreCase("voided")) {
                response.setOutputData(request.getStage(), "paymentStatus", "voided");
                if (!StringUtil.isEmpty(request.stageOutputData(1).get("invalid_account"))) {
                    response.setDeclineReason(getError(CASHIER_INVALID_ACCOUNT) + ": " + request.stageOutputData(1).get("invalid_account"));
                } else {
                    setDeclineReasonFromActions(request, response, payment, true);
                }
                return DoProcessorResponseStatus.DECLINED;
            } else {
                response.setOutputData(request.getStage(), "paymentStatus", payment.getStatus());
                log.info("Payment status is not final: " + payment.getStatus() + " Transactionid:" + request.getTransactionId() + " ProcessorReference: " + request.getProcessorReference());
                if (payment.getStatus().equalsIgnoreCase("authorized") && request.isTransactionExpired() && Boolean.parseBoolean(request.getProperty("void_on_expire"))) {
                    voidPayment(request, response, api);
                    response.setDeclineReason("Void on expiry");
                    return DoProcessorResponseStatus.DECLINED;
                }
                return DoProcessorResponseStatus.NOOP;
            }
        } catch (Exception e) {
            log.error("Failed to get payment details for transaction with id " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog(e.getMessage());
            throw new Status500VerifyException("Failed to get payment details for transaction with id " + request.getTransactionId() + ". ", e);
        }
    }

    public DoProcessorResponseStatus payout(DoProcessorRequest request, DoProcessorResponse response) {
        try {
            String secretKey = request.getProperty("secret_key");
            String publicKey = request.getProperty("public_key");
            boolean useSandbox = Boolean.parseBoolean(request.getProperty("use_sendbox"));

            AlternativePaymentSource source = null;
            String cardReference = request.stageInputData(1).get("cardReference");

            //Leave priority of cardReference until cc flow will be refactored
            if (isNull(cardReference) && nonNull(request.getProcessorAccount())) {
                cardReference = request.getProcessorAccount().getReference();
            }

            source = new AlternativePaymentSource("id");
            source.put("id", cardReference);

            source.put("first_name", request.getUser().getFirstName());
            source.put("last_name", request.getUser().getLastName());

            PaymentRequest<AlternativePaymentSource> paymentRequest =
                PaymentRequest.fromDestination(source, request.getUser().getCurrency(), request.inputAmountCents()); //cents
            //paymentRequest.setFundTransferType("AA");
            paymentRequest.setReference(request.getTransactionId().toString());

            CheckoutApi checkoutApi = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);
            response.setRawRequestLog(objectToPrettyString(paymentRequest));

            PaymentResponse apiResponse = checkoutApi.paymentsClient().requestAsync(paymentRequest, UUID.nameUUIDFromBytes(request.getTransactionId().toString().getBytes()).toString()).get();
            response.setRawResponseLog(objectToPrettyString(apiResponse));
            response.setPaymentType("card");

            if (apiResponse.isPending()) {
                log.debug("Pay-out is in the pending state, waiting for webhook.");
                response.setOutputData(1, "paymentStatus", apiResponse.getPending().getStatus());
                response.setProcessorReference(apiResponse.getPending().getId());
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if (apiResponse.getPayment().isApproved()) {
                response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
                response.setProcessorReference(apiResponse.getPayment().getId());
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else {
                response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
                response.setProcessorReference(apiResponse.getPayment().getId());
                response.setOutputData(1, "responseCode", apiResponse.getPayment().getResponseCode());
                response.setOutputData(1, "responseSummary", apiResponse.getPayment().getResponseSummary());
                response.setErrorCode(CheckoutErrors.fromErrorCode(apiResponse.getPayment().getResponseCode()).getGeneralError().getCode());
                response.setMessage(CheckoutErrors.fromErrorCode(apiResponse.getPayment().getResponseCode()).getGeneralError().getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.error("Payment was declined by checkout. ErrorCode: " + apiResponse.getPayment().getResponseCode() + "ErrorMessage: " + apiResponse.getPayment().getResponseSummary() + " TransactionId: " + request.getTransactionId());
                response.setDeclineReason(apiResponse.getPayment().getResponseCode() + ": " + apiResponse.getPayment().getResponseSummary());
                return DoProcessorResponseStatus.DECLINED;
            }
        } catch (Exception ex) {
            if (ex instanceof ExecutionException && ex.getCause() instanceof CheckoutApiException) {
                return handleCheckoutExeption(request, response, (CheckoutApiException)ex.getCause());
            }
            log.error("Unable to initiate pay-out for transaction id: " + request.getTransactionId() + ". " + ex.getMessage(), ex);
            response.addRawResponseLog( "Exception: " + ex.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(ex)));
            response.setDeclineReason("Unable to initiate pay-out. Exception: " + ex.getMessage());
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    public DoProcessorResponseStatus handlePaymentApproved(DoProcessorRequest request, DoProcessorResponse response) throws Exception {
        if (!StringUtil.isEmpty(request.stageInputData(1).get("paymentToken"))) {
            String secretKey = request.getProperty("secret_key");
            String publicKey = request.getProperty("public_key");
            boolean useSandbox = Boolean.parseBoolean(request.getProperty("use_sendbox"));

            CheckoutApi api = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);
            //here we should get full card info and can store account
            //in case if there was no 3d secure redirect and
            if (!verifyAccount(request, response, null, ofNullable(request.getProcessorReference()).orElse(response.getProcessorReference()), api)) {
                voidPayment(request, response, api);
                return DoProcessorResponseStatus.DECLINED;
            } else {
                capturePayment(request, response, api);
            }
        } else {
            log.info("Auto capturing is enabled. Skip payment_approved webhook processing for transaction id:" + request.getTransactionId());
        }
        return DoProcessorResponseStatus.NOOP;
    }

    public void voidPayment(DoProcessorRequest request, DoProcessorResponse response, CheckoutApi api) throws Exception {
        try {
            log.info("Payment void is requested for transaction id: " + request.getTransactionId());
            VoidResponse voidResponse = api.paymentsClient().voidAsync(response.getProcessorReference() != null ? response.getProcessorReference() : request.getProcessorReference()).get();
            log.info("Payment void was accepted for transaction id: " + request.getTransactionId());
            response.addRawResponseLog("Payment void response: " + objectToPrettyString(voidResponse));

        } catch (Exception e) {
            log.error("Failed to void payment for transaction with id " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog("Failed to void payment: "+ e.getMessage());
            throw new Exception("Failed to void payment for transaction with id " + request.getTransactionId() + ". ", e);
        }
    }

    public void capturePayment(DoProcessorRequest request, DoProcessorResponse response, CheckoutApi api) throws Exception {
        try {
            log.info("Payment capture is requested for transaction id: " + request.getTransactionId());
            CaptureResponse captureResponse = api.paymentsClient().captureAsync(response.getProcessorReference() != null ? response.getProcessorReference() : request.getProcessorReference()).get();
            log.info("Payment capture was accepted for transaction id: " + request.getTransactionId());
            response.addRawResponseLog("Payment capture response: " + objectToPrettyString(captureResponse));
        } catch (Exception e) {
            log.error("Failed to capture payment for transaction with id " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog("Failed to capture payment: " + e.getMessage());
            throw new Exception("Failed to capture payment for transaction with id " + request.getTransactionId() + ". ", e);
        }
    }

    private PaymentRequest<?> createPaymentRequest(DoProcessorRequest request, DoProcessorResponse response, String paymentToken, boolean use3DSecure) throws Exception {
        RequestSource source = getPaymentRequestSource(request, paymentToken);
        PaymentRequest<?> paymentRequest =
            PaymentRequest.fromSource(source, request.getUser().getCurrency(), request.inputAmountCents()); //cents

        CustomerRequest customer = new CustomerRequest();
        customer.setEmail(request.getUser().getEmail());
        String nameOnCard = request.stageInputData(1).get("nameoncard");
        customer.setName(nameOnCard != null && !nameOnCard.isEmpty() ? nameOnCard : request.getUser().getFullName());
        paymentRequest.setCustomer(customer);
        //init two steps deposit (to verify account before capture) in case new card is used
        paymentRequest.setCapture(StringUtil.isEmpty(paymentToken));


        ThreeDSRequestV2 threeDSRequest = new ThreeDSRequestV2();
        threeDSRequest.setEnabled(use3DSecure);
        threeDSRequest.setAttemptN3D(Boolean.parseBoolean(request.getProperty("attempt_n3d")));
        threeDSRequest.setExemption(ofNullable(request.getProperty("exemption")).filter(Predicate.not(String::isEmpty)).orElse(null));

        paymentRequest.setReference(request.getTransactionId().toString());
        paymentRequest.setThreeDS(threeDSRequest);

        String redirectUrl = lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-checkout-cc/public";
        paymentRequest.setSuccessUrl(redirectUrl + "/" + request.getTransactionId() + "/success");
        paymentRequest.setFailureUrl(redirectUrl + "/" + request.getTransactionId() + "/failed");
        
        response.setRawRequestLog(objectToPrettyString(paymentRequest));
        return paymentRequest;

    }

    private RequestSource getPaymentRequestSource(DoProcessorRequest request, String paymentToken) throws Exception {
        RequestSource source = null;
        if (!StringUtil.isEmpty(paymentToken)) {
            if (request.getUser().getResidentialAddress() != null && Boolean.parseBoolean(request.getProperty("send_billing_address"))) {
                Address address = new Address(
                    request.getUser().getResidentialAddress().getAddressLine1(),
                    request.getUser().getResidentialAddress().getAddressLine2(),
                    request.getUser().getResidentialAddress().getCity(),
                    request.getUser().getResidentialAddress().getAdminLevel1(),
                    request.getUser().getResidentialAddress().getPostalCode(),
                    request.getUser().getResidentialAddress().getCountryCode());
                source = new TokenSource(paymentToken, address);
            } else {
                source = new TokenSource(paymentToken);
            }
        } else if (request.getProcessorAccount() != null) {
            source = new CvvMaskedIdSource(request.getProcessorAccount().getReference());
            ((CvvMaskedIdSource) source).setCvv(request.stageInputData(1, "cvv"));
        } else {
            throw new Exception("Failed to set Checkout request source.");
        }
        return source;
    }

    private DoProcessorResponseStatus handleCheckoutExeption(DoProcessorRequest request, DoProcessorResponse response, CheckoutApiException checkOutEx) {
        String respMessage = "";
        if (checkOutEx instanceof CheckoutValidationException) {
            CheckoutValidationException validationException = (CheckoutValidationException) checkOutEx;
            for (String error : validationException.getError().getErrorCodes()) {
                //error codes started from 422xxx are not mapped to general on client side keep checkout errors
                respMessage += CheckoutErrors.fromErrorName(error).getCheckoutError() + ";";
            }
            response.setDeclineReason(respMessage);
        } else {
            respMessage = CheckoutErrors.unknown_error.getGeneralErrorLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage());
            response.setDeclineReason(checkOutEx.getMessage());
        }
        response.setErrorCode(GeneralError.VERIFY_INPUT_DETAILS.getCode());
        response.setMessage(respMessage);
        log.error("Unable to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + "CheckoutException:" + checkOutEx.getMessage(), checkOutEx);
        response.addRawResponseLog(checkOutEx.getMessage());
        return DoProcessorResponseStatus.DECLINED;
    }

    private ProcessorAccount processorAccountFromCardSource(CardSourceResponse cardSource) {
        return ProcessorAccount.builder()
            .reference(cardSource.getId())
            .type(ProcessorAccountType.CARD)
            .descriptor(cardSource.getLast4())
            .status(PaymentMethodStatusType.ACTIVE)
            .name(cardSource.getName())
            .hideInDeposit(false)
            .data(new HashMap<String, String>() {
                {
                    put("name", cardSource.getName());
                    put("cardType", cardSource.getCardType());
                    put("scheme", cardSource.getScheme());
                    put("bin", cardSource.getBin());
                    put("expiryDate", String.format("%02d/%02d", cardSource.getExpiryMonth(), cardSource.getExpiryYear() % 100));
                    put("fingerprint", cardSource.getFingerprint());
                    put("issuerCountry", cardSource.getIssuerCountry());
                    put("issuer", cardSource.getIssuer());
                    put("last4Digits", cardSource.getLast4());
                }})
            .build();
    }

    private boolean verifyAccount(DoProcessorRequest request, DoProcessorResponse response, ResponseSource responseSource, String processorReference, CheckoutApi checkoutApi) throws CheckCardOwnerException {
        try {
            CardSourceResponse cardSource = responseSource == null ? getCardSource(request, processorReference, checkoutApi) : (CardSourceResponse)responseSource;
            boolean saveCard = Boolean.parseBoolean(request.stageInputData(1).get("save_card")) && cardSource != null && !StringUtil.isEmpty(cardSource.getId()) && !StringUtil.isEmpty(cardSource.getFingerprint());
            response.setOutputData(request.getStage(), "cardSourceId", cardSource.getId());

            VerifyProcessorAccountRequest verifyRequest = VerifyProcessorAccountRequest.builder()
                .processorAccount(processorAccountFromCardSource(cardSource))
                .verifications(getAccountVerifications(request))
                .userGuid(request.getUser().getRealGuid())
                .build();
            VerifyProcessorAccountResponse verifyResponse = cashierService.verifyAccount(verifyRequest);

            if (saveCard) {
                response.setProcessorAccount(verifyResponse.getProcessorAccount());
            }

            if (BooleanUtils.isFalse(verifyResponse.getResult())) {
                ProcessorAccountVerificationType failedVerification = verifyResponse.getProcessorAccount().getFailedVerification();
                if (!saveCard) {
                    response.setErrorCode(failedVerification.getGeneralError().getCode());
                    response.setMessage(failedVerification.getGeneralError().getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    response.setDeclineReason(getError(CASHIER_INVALID_ACCOUNT) + ": " + failedVerification.getDescription());
                    response.setRemark(TransactionRemarkData.builder().remark(buildProcessorAccountRemark(verifyResponse.getProcessorAccount())).type(TransactionRemarkType.ACCOUNT_DATA).build());
                }
                response.setOutputData(1, "invalid_account", failedVerification.getName());
                log.error("Account is invalid. Verification: " + failedVerification + " CardSource: " + cardSource.toString());
                return false;
            }
        } catch (Exception e) {
            response.setOutputData(1, "invalid_account", "Verification failed");
            log.error("Failed to verify account for transaction id:" + request.getTransactionId() + ". Error: " + e.getMessage(), e);
            response.setDeclineReason("Failed to verify account.");
            response.setErrorCode(GeneralError.INVALID_PROCESSOR_ACCOUNT.getCode());
            response.setMessage(GeneralError.INVALID_PROCESSOR_ACCOUNT.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            return false;
        }
        return true;
    }

    private String buildProcessorAccountRemark(ProcessorAccount processorAccount) {
        return "Additional Transaction Information: " + processorAccount.getData().entrySet().stream()
            .filter(entrySet -> !entrySet.getKey().equals("fingerprint"))
            .map(entrySet -> entrySet.getKey() + ": " + entrySet.getValue())
            .collect(Collectors.joining(", ", "", "."));
    }

    private List<ProcessorAccountVerificationType> getAccountVerifications(DoProcessorRequest request) throws Exception {
        String accountVerifications = request.getProperties().get("account_verifications");
        if (StringUtil.isEmpty(accountVerifications)) {
            return Collections.emptyList();
        }
        return Arrays.asList(accountVerifications.split("\\s*,\\s*")).stream().map(ProcessorAccountVerificationType::fromName).collect(Collectors.toList());
    }

    private CardSourceResponse getCardSource(DoProcessorRequest request, String processorReference, CheckoutApi checkoutApi) throws Exception {
        CardSourceResponse cardSource = null;
        try {
            GetPaymentResponse payment = checkoutApi.paymentsClient().getAsync(processorReference).get();

            if (payment.getSource() != null) {
                cardSource = (CardSourceResponse) payment.getSource();
            } else {
                throw new Exception("Card source is null. Checkout get payment responce: " + payment.toString());
            }
        } catch (Exception e) {
            log.error("Failed to get payment source for transaction: " + request.getTransactionId() + e.getMessage(), e);
            throw new Exception("Failed to get payment source");
        }
        return cardSource;
    }
	private void setDeclineReasonFromActions(DoProcessorRequest request, DoProcessorResponse response, GetPaymentResponse payment, boolean isVoided) throws Exception
    {
		ArrayList<PaymentActionSummary> actions = (ArrayList) payment.getActions();
		if (!actions.isEmpty()) {
            response.setOutputData(request.getStage(), "responseCode", actions.get(actions.size() - 1).getResponseCode());
            response.setOutputData(request.getStage(), "responseSummary", actions.get(actions.size() - 1).getResponseSummary());
		}
        String errorCode = response.stageOutputData(request.getStage()).get("responseCode");
        String errorMessage = response.stageOutputData(request.getStage()).get("responseSummary");
		String declineReason;
		if (errorCode != null && errorMessage != null) {
			if (errorMessage.contains("40111")) {
				errorCode = "40111";
				errorMessage =  errorMessage.substring(errorMessage.indexOf(" - ") + 3);
				response.setErrorCode(GeneralError.AVS_CHECK_FAILED.getCode());
				response.setMessage(GeneralError.AVS_CHECK_FAILED.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			} else {
				CheckoutErrors checkoutError = CheckoutErrors.fromErrorCode(errorCode);
				response.setErrorCode(checkoutError.getGeneralError().getCode());
				response.setMessage(checkoutError.getGeneralError().getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
			}
			declineReason = errorCode + ": " + errorMessage;
		} else {
		    declineReason = getTreeDSecureDeclineReason(payment).orElse((isVoided ? "Voided" : "Declined") + " by checkout. ErrorCode is undefined");
			response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
			response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
		}
		log.info(declineReason+ " TransactionId: " + request.getTransactionId());
		response.setDeclineReason(declineReason);
	}

	private Optional<String> getTreeDSecureDeclineReason(GetPaymentResponse payment) {
        return Optional.ofNullable(payment.getThreeDS())
            .map(ThreeDSEnrollment::getAuthenticationResponse)
            .map(a -> AuthenticationResponses.fromCode(a))
            .filter(a -> a != AuthenticationResponses.authenticated)
            .map(AuthenticationResponses::getDescription);
    }
    
}
