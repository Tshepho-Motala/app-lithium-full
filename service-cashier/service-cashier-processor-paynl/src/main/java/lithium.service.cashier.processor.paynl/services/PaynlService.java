package lithium.service.cashier.processor.paynl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.paynl.data.Amount;
import lithium.service.cashier.processor.paynl.data.Customer;
import lithium.service.cashier.processor.paynl.data.Stats;
import lithium.service.cashier.processor.paynl.data.Transaction;
import lithium.service.cashier.processor.paynl.data.enums.ErrorCodes;
import lithium.service.cashier.processor.paynl.data.enums.TransactionStatus;
import lithium.service.cashier.processor.paynl.data.request.IBan;
import lithium.service.cashier.processor.paynl.data.request.Payment;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lithium.service.cashier.processor.paynl.data.response.PayoutResponse;
import lithium.service.cashier.processor.paynl.data.response.PayoutStatusResponse;
import lithium.service.cashier.processor.paynl.exceptions.Error;
import lithium.service.cashier.processor.paynl.exceptions.PaynlException;
import lithium.service.cashier.processor.paynl.exceptions.PaynlGeneralException;
import lithium.service.cashier.processor.paynl.exceptions.PaynlValidatePayoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Service
@Slf4j
public class PaynlService {

    @Autowired
    private LithiumConfigurationProperties lithiumProperties;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CashierDoCallbackService callbackService;

    @Value("${spring.application.name}")
    private String moduleName;
    
    private final static String TRANSACTION_TYPE = "CIT";

    public DoProcessorResponseStatus initiateWithdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
        PayoutRequest payoutRequest = buildRequest(request);
        response.addRawRequestLog(objectToPrettyString(payoutRequest));
        PaynlApiService apiService = PaynlApiService.instance(request.getProperty("payments_api_url"), request.getProperty("get_transaction_url"), request.getProperty("username"), request.getProperty("password"), restTemplate, mapper);
        try {
            PayoutResponse payoutResponse = apiService.sendPayoutRequest(payoutRequest);
            response.addRawResponseLog(objectToPrettyString(payoutResponse));
            response.setProcessorReference(payoutResponse.getTransaction().getId());
            response.setAdditionalReference(payoutResponse.getTransaction().getOrderId());
            return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
        } catch (PaynlException e) {
            response.setDeclineReason(e.getErrorsList().stream().map(er -> er.getCode() + ": " + er.getMessage()).collect(Collectors.joining("; ")));
            Error error = e.getErrorsList().stream().findFirst().get();
            response.addRawResponseLog("Pay.nl responded with error(s): " + objectToPrettyString(e.getErrorsList()));
            response.setErrorCode(ErrorCodes.fromErrorCode(error.getCode()).getGeneralError().getCode());
            response.setMessage(ErrorCodes.fromErrorCode(error.getCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            return DoProcessorResponseStatus.DECLINED;
        } catch (PaynlGeneralException e) {
            response.addRawResponseLog("Pay.nl responded with http status " + e.getHttpStatus());
            response.setDeclineReason("Pay.nl responded with http status: " + e.getHttpStatus());
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            return DoProcessorResponseStatus.DECLINED;
        } catch (Exception e) {
            log.error("Failed to initiate payout for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog("Failed to initiate payout for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage());
            response.setDeclineReason("Failed to initiate payout for the transaction");
            response.addRawResponseLog("Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    public DoProcessorResponseStatus verifyPayout(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws PaynlValidatePayoutException {
        if (request.getProcessorReference() == null) {
            log.error("Failed to verify Pay.nl payout transaction id: " + request.getTransactionId() + ". No processor reference.");
            throw new PaynlValidatePayoutException("Failed to verify payout transaction: " + request.getTransactionId() + ". No processor reference.");
        }
        try {
            response.addRawRequestLog("Verify Pay.nl payout request for transaction " + request.getProcessorReference());
            PaynlApiService apiService = PaynlApiService.instance(request.getProperty("payments_api_url"), request.getProperty("get_transaction_url"), request.getProperty("username"), request.getProperty("password"), restTemplate, mapper);
            PayoutStatusResponse payoutStatus = apiService.getPayoutStatus(request.getProcessorReference());
            log.info("Payout status response: " + payoutStatus);
            response.addRawResponseLog(objectToPrettyString(payoutStatus));

            TransactionStatus transactionStatus = TransactionStatus.getTransactionStatusByCode(payoutStatus.getStatus().getCode());
            switch (transactionStatus) {
                case PAID:
                    if (!Boolean.parseBoolean(request.getProperty("skip_amount_check"))) {
                        response.setAmountCentsReceived(CurrencyAmount.fromAmountString(payoutStatus.getAmount().getValue()).toAmount().intValue());
                    }
                    return DoProcessorResponseStatus.SUCCESS;
                case DENIED:
                case FAILURE:
                    response.setDeclineReason("Payout was declined by Pay.nl");
                    response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
                    response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.DECLINED;
                case CANCEL:
                case DENIED_V2:
                    response.setDeclineReason("Payout was cancelled by an employee or external system");
                    response.setErrorCode(GeneralError.CANCEL_TRANSACTION.getCode());
                    response.setMessage(GeneralError.CANCEL_TRANSACTION.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.PLAYER_CANCEL;
                case EXPIRED:
                    response.setDeclineReason("Expired transaction");
                    response.setErrorCode(GeneralError.EXPIRED_TRANSACTION.getCode());
                    response.setMessage(GeneralError.EXPIRED_TRANSACTION.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.EXPIRED;
                default:
                    return DoProcessorResponseStatus.NOOP;
            }
        } catch (PaynlException e) {
            response.setDeclineReason(e.getErrorsList().stream().map(er -> er.getCode() + ": " + er.getMessage()).collect(Collectors.joining("; ")));
            Error error = e.getErrorsList().stream().findFirst().get();
            response.addRawResponseLog("Pay.nl responded with error(s): " + objectToPrettyString(e.getErrorsList()));
            response.setErrorCode(ErrorCodes.fromErrorCode(error.getCode()).getGeneralError().getCode());
            response.setMessage(ErrorCodes.fromErrorCode(error.getCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            return DoProcessorResponseStatus.DECLINED;
        } catch (PaynlGeneralException e) {
            response.addRawResponseLog("Pay.nl responded with http status " + e.getHttpStatus());
            response.setDeclineReason("Pay.nl responded with http status: " + e.getHttpStatus());
            response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
            return DoProcessorResponseStatus.DECLINED;
        } catch (Exception e) {
            response.addRawResponseLog("Failed to initiate payout for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage());
            log.error("Failed to verify payout transaction: " + request.getTransactionId() + ". Exception: " + e.getMessage(), e);
            throw new PaynlValidatePayoutException("Failed to verify payout transaction: " + request.getTransactionId());
        }
    }

    public void processPaynlNotification(String transactionId, Map<String, String> requestParams) throws Exception {
        log.info("Webhook data: tranaction id: " + transactionId + " request parameters: " + requestParams);

        long id = Long.parseLong(transactionId);

        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(id, "paynl");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transaction id for Pay.nl notification: " + id);
            throw new Exception(processorRequestResponse.getMessage());
        }
        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
                .transactionId(doProcessorRequest.getTransactionId())
                .rawRequestLog("Received Pay.nl notification for transaction id #" + id + " with request parameters: " + objectToPrettyString(requestParams))
                .build();
        try {
            DoProcessorResponseStatus status = verifyPayout(doProcessorRequest, doProcessorResponse, restTemplate);
            if (!doProcessorRequest.isTransactionFinalized()) {
                doProcessorResponse.setStatus(status);
            }
        } finally {
            callbackService.doSafeCallback(doProcessorResponse);
        }
    }

    public PayoutRequest buildRequest(DoProcessorRequest request) throws Exception {
        return PayoutRequest.builder()
                .transaction(buildTransaction(request))
                .payment(buildPayment(request))
                .customer(buildCustomer(request.getUser()))
                .stats(buildStats(request))
                .build();
    }

    private Transaction buildTransaction(DoProcessorRequest request) throws Exception {
        return Transaction.builder()
                .type(TRANSACTION_TYPE)
                .serviceId(request.getProperty("serviceId"))
                .description(request.getProperty("description") + " " + request.getTransactionId())
                .reference(request.getTransactionId().toString()) 
                .amount(buildAmount(request))
                .exchangeUrl(gatewayPublicUrl() + "/public/webhook/" + request.getTransactionId())
                .build();
    }

    private Amount buildAmount(DoProcessorRequest request) throws Exception {
        return Amount.builder()
                .value(request.inputAmountCents().toString())
                .currency(request.getUser().getCurrency())
                .build();
    }

    private Payment buildPayment(DoProcessorRequest request) {
        ProcessorAccount processorAccount = request.getProcessorAccount();
        IBan iBan = IBan.builder()
                .number(processorAccount.getData().get("iban"))
                .bic(Optional.ofNullable(processorAccount).filter(p -> p.getData() != null).map(p -> p.getData().get("bank_code")).orElse(null))
                .holder(processorAccount.getName())
                .build();
        return Payment.builder()
                .iBan(iBan)
                .method("iban")
                .build();
    }

    private Customer buildCustomer(DoProcessorRequestUser user) {
        return Customer.builder()
                .firstName(user.getFirstName())
                .lastName(!ObjectUtils.isEmpty(user.getLastNamePrefix()) ? user.getLastNamePrefix() + " " + user.getLastName() : user.getLastName())
                .ipAddress(user.getLastKnownIP())
                .birthDate(new SimpleDateFormat("yyyy-MM-dd").format(user.getDateOfBirth().toDate()))
                .phone(user.getCellphoneNumber())
                .email(user.getEmail())
                .language(user.getLanguage())
                .gender(mapGender(user.getGender()))
                .reference(user.getId().toString())
                .build();
    }
    
    private Stats buildStats(DoProcessorRequest request) {
        Boolean contraAccount = request.getProcessorAccount().getContraAccount();
        return Stats.builder()
                .extra1(request.getUser().getId().toString())
                .extra2(contraAccount != null ? "contra_acc_" + contraAccount : null)
                .build();
    }
    
    private String mapGender(String gender) {
        if ("male".equalsIgnoreCase(gender)) {
            return "M";
        }
        if ("female".equalsIgnoreCase(gender)) {
            return "F";
        }
        return null;
    }
    private String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl() + "/" + moduleName;
    }
    
}
