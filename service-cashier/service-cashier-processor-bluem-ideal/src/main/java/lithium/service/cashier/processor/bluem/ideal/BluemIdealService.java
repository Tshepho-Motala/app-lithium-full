package lithium.service.cashier.processor.bluem.ideal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import lithium.config.LithiumConfigurationProperties;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.bluem.BluemSignatureHandler;
import lithium.service.cashier.processor.bluem.api.BluemEPaymentErrors;
import lithium.service.cashier.processor.bluem.api.data.CurrencySimpleType;
import lithium.service.cashier.processor.bluem.api.data.DebtorReturnURLComplexType;
import lithium.service.cashier.processor.bluem.api.data.DebtorWalletComplexType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentStatusRequestType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentStatusUpdateType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentTransactionRequestType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentTransactionResponseType;
import lithium.service.cashier.processor.bluem.api.data.ErrorComplexType;
import lithium.service.cashier.processor.bluem.api.data.IDealComplexType;
import lithium.service.cashier.processor.bluem.api.data.IDealDetailsComplexType;
import lithium.service.cashier.processor.bluem.api.data.PaymentMethodDetailsComplexType;
import lithium.service.cashier.processor.bluem.exceptions.BluemConnectionException;
import lithium.service.cashier.processor.bluem.exceptions.BluemValidatePaymentException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BluemIdealService {

    @Autowired
    LithiumConfigurationProperties lithiumProperties;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CashierInternalClientService cashierService;

    @Autowired
    CashierDoCallbackService callbackService;
    @Autowired
    RestTemplate restTemplate;
	@Autowired
	UserApiInternalClientService userService;
    @Value("${spring.application.name}")
    private String moduleName;

    private static final String TRANSACTION_ID = "{{trn_id}}";
    private static final String HTTP_SCHEMA = "http";

    private static final String ENTRANCE_CODE_PREFIX = "L";

    private ObjectMapper mapper = BluemIdealService.getXmlMapper();

    private static ObjectMapper getXmlMapper() {
        JacksonXmlModule jacksonXmlModule = new JacksonXmlModule();
        XmlMapper objectMapper = new XmlMapper(jacksonXmlModule);
        objectMapper.registerModule(new JaxbAnnotationModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setDefaultUseWrapper(false);
        return objectMapper;
    }

    public DoProcessorResponseStatus initiateDeposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            Date now = new Date();
            EPaymentTransactionRequestType paymentRequest = new EPaymentTransactionRequestType();

            paymentRequest.setDocumentType("PayRequest");
            String entranceCode = ENTRANCE_CODE_PREFIX + request.getTransactionId().toString();
            response.setOutputData(1, "entrance_code", entranceCode);
            paymentRequest.setEntranceCode(entranceCode);
            paymentRequest.setSendOption("none");
            paymentRequest.setLanguage(request.getUser().getLanguage());

            paymentRequest.setAmount(request.inputAmount());
            paymentRequest.setCurrency(CurrencySimpleType.valueOf(request.getUser().getCurrency().toUpperCase()));
            paymentRequest.setDueDateTime(BluemApiService.getTimeString(new Date(now.getTime() + Long.parseLong(request.getProperty("expire_after")))));
            paymentRequest.setPaymentReference(request.getTransactionId().toString());
            paymentRequest.setDebtorReference(request.getUser().getRealGuid());

            DebtorReturnURLComplexType returnURL = new DebtorReturnURLComplexType();

            returnURL.setValue(BooleanUtils.isTrue(Boolean.parseBoolean(request.getProperty("handle_redirect")))
                                ? gatewayPublicUrl() + "/public/redirect/" + request.getTransactionId().toString()
                                : getReturnUrl(request));

            returnURL.setAutomaticRedirect(Boolean.parseBoolean(request.getProperty("bluem_auto_redirect")));
            paymentRequest.setDebtorReturnURL(returnURL);
            paymentRequest.setDescription(request.getProperty("payment_description"));

            //According to the BLUEM support DebtorAdditionalData should not be sent in our case
            //DebtorAdditionalDataComplexType additionalData = new DebtorAdditionalDataComplexType();
            //additionalData.setEmailAddress(request.getUser().getEmail());
            //additionalData.setCustomerName(request.getUser().getFullName());
            //BLUEM returns 400 with no details on incorrect phone number
            //should we send so much data, player will still should login to bank app
            //additionalData.setMobilePhoneNumber(request.getUser().getCellphoneNumber());

            //if (request.getUser().getResidentialAddress() != null) {
            //    additionalData.setCustomerAddressLine1(request.getUser().getResidentialAddress().getAddressLine1());
            //    additionalData.setCustomerAddressLine2(request.getUser().getResidentialAddress().getAddressLine2());
            //}
            //paymentRequest.setDebtorAdditionalData(additionalData);

            setIDEALDebtorWallet(request, paymentRequest);

            BluemApiService apiService = BluemApiService.instance(request.getProperty("payments_api_url"), request.getProperty("sender_id"), request.getProperty("brand_id"), request.getProperty("token"), restTemplate, mapper);

            response.addRawRequestLog(mapper.writeValueAsString(paymentRequest));
            EPaymentTransactionResponseType paymentResponse = apiService.sendPaymentRequest(paymentRequest, now);

            response.addRawResponseLog(mapper.writeValueAsString(paymentResponse));
            response.setPaymentType("BANK");


            if (paymentResponse.getError() != null) {
                ErrorComplexType error = paymentResponse.getError();
                response.setDeclineReason(error.getErrorCode() + ": " + error.getErrorMessage());
                response.setErrorCode(BluemEPaymentErrors.fromErrorCode(error.getErrorCode()).getGeneralError().getCode());
                response.setMessage(BluemEPaymentErrors.fromErrorCode(error.getErrorCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.error("Failed to create iDEAL payment. Bluem responded with error: " + response.getDeclineReason());
                return DoProcessorResponseStatus.DECLINED;
            } else {
                response.setProcessorReference(paymentResponse.getTransactionID());
                response.setIframeUrl(paymentResponse.getTransactionURL());
                response.setIframeMethod("GET");
                return DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE;
            }
        } catch (BluemConnectionException ce) {
            response.setDeclineReason(ce.getHttpErrorCode() + ": " + ce.getMessage());
            response.setErrorCode(GeneralError.VERIFY_INPUT_DETAILS.getCode());
            response.setMessage(GeneralError.VERIFY_INPUT_DETAILS.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            return DoProcessorResponseStatus.DECLINED;
        } catch (Exception e) {
            log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog( "Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.FATALERROR;
        }
    }

    private String getReturnUrl(DoProcessorRequest request) throws Exception {
        return request.stageInputData(1, "return_url").replace(TRANSACTION_ID, request.getTransactionId().toString());
    }

    private void setIDEALDebtorWallet(DoProcessorRequest request, EPaymentTransactionRequestType paymentRequest) {
        String bic = Optional.ofNullable(request.getProcessorAccount()).filter(p -> p.getData() != null).map(p -> p.getData().get("bank_code")).orElse(request.stageInputData(1).get("bank_code"));
        if (!StringUtil.isEmpty(bic)) {
            DebtorWalletComplexType wallet = new DebtorWalletComplexType();
            IDealComplexType idealWallet = new IDealComplexType();
            idealWallet.setBIC(bic);
            wallet.setIDEAL(idealWallet);
            paymentRequest.setDebtorWallet(wallet);
        }
    }

    public DoProcessorResponseStatus verifyDeposit(DoProcessorRequest request, DoProcessorResponse response, PaymentMethodDetailsComplexType paymentMethodDetails) throws BluemValidatePaymentException {
        if (request.getProcessorReference() == null) {
            log.error("Failed to verify Bluem payment transaction id: " + request.getTransactionId() + ". No processor reference.");
            throw new BluemValidatePaymentException("Failed to verify payment transaction: " + request.getTransactionId() + ". No processor reference.");
        }

        try {
            EPaymentStatusRequestType statusRequest = new EPaymentStatusRequestType();
            statusRequest.setEntranceCode(request.stageOutputData(1 , "entrance_code"));
            statusRequest.setTransactionID(request.getProcessorReference());

            response.addRawRequestLog(mapper.writeValueAsString(statusRequest));

            BluemApiService apiService = BluemApiService.instance(request.getProperty("payments_api_url"), request.getProperty("sender_id"), request.getProperty("brand_id"), request.getProperty("token"), restTemplate, mapper);
            EPaymentStatusUpdateType statusResponse = apiService.getPaymentStatus(statusRequest);

            response.addRawResponseLog(mapper.writeValueAsString(statusResponse));

            if (statusResponse.getError() != null) {
                ErrorComplexType error = statusResponse.getError();
                response.setDeclineReason(error.getErrorCode() + ": " + error.getErrorMessage());
                response.setErrorCode(BluemEPaymentErrors.fromErrorCode(error.getErrorCode()).getGeneralError().getCode());
                response.setMessage(BluemEPaymentErrors.fromErrorCode(error.getErrorCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                throw new Exception("Failed to get Bluem transaction status (transaction id: " + request.getTransactionId()+"): " + error.getErrorCode() + ": " + error.getErrorMessage());
            }

            switch (statusResponse.getStatus()) {
                case SUCCESS:
                    //work around for the bluem issue: no account details for successful transaction
                    if (statusResponse.getPaymentMethodDetails() == null) {
                        log.info("Bluem ideal payment method details were not provided in transaction status response (transactionId: " + request.getTransactionId() + ")taken from webhook data.");
                        if (paymentMethodDetails == null) {
                            throw new Exception("Payment method details are not provided by Bluem (transactionId: " + request.getTransactionId() + ").");
                        }
                        statusResponse.setPaymentMethodDetails(paymentMethodDetails);
                    }
                    response.setProcessorAccount(getIdealProcessorAccount(statusResponse, request, statusResponse.getPaymentMethodDetails()));
                    if (!Boolean.parseBoolean(request.getProperty("skip_amount_check"))) {
                        response.setAmountCentsReceived(CurrencyAmount.fromAmount(statusResponse.getAmount()).toCents().intValue());
                    }
                    return DoProcessorResponseStatus.SUCCESS;
                case CANCELLED:
                    response.setErrorCode(GeneralError.CANCEL_TRANSACTION.getCode());
                    response.setMessage(GeneralError.CANCEL_TRANSACTION.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.PLAYER_CANCEL;
                case FAILURE:
                    response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
                    response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.DECLINED;
                case EXPIRED:
                    response.setErrorCode(GeneralError.EXPIRED_TRANSACTION.getCode());
                    response.setMessage(GeneralError.EXPIRED_TRANSACTION.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                    return DoProcessorResponseStatus.EXPIRED;
                default:
                    return DoProcessorResponseStatus.NOOP;
            }
        } catch (Exception e) {
            log.error("Failed to verify payment transaction: " + request.getTransactionId() + ". Exception: " + e.getMessage(), e);
            throw new BluemValidatePaymentException("Failed to verify payment transaction: " + request.getTransactionId());
        }
    }

    private ProcessorAccount getIdealProcessorAccount(EPaymentStatusUpdateType statusResponse, DoProcessorRequest request, PaymentMethodDetailsComplexType paymentMethodDetails) {
        IDealDetailsComplexType accountDetails = paymentMethodDetails.getIDealDetails();
        ProcessorAccount processorAccount = ProcessorAccount.builder()
            //we could set reference to IBAN here as is, since there is not actual reference to the Bluem account
            //in that way we would give an option to reuse this account by other processor DEPOSIT. Do we need that?
            //it was designed to have separate one in this case, since processor account is linked to the DomainMethodProcessor
            //We should have 2 processor accounts if deposit with same IBAN was done over different processors
            //.reference(accountDetails.getDebtorIBAN())
            .reference("bideal_" + UUID.nameUUIDFromBytes(accountDetails.getDebtorIBAN().getBytes()).toString().replace("-", ""))
            .status(PaymentMethodStatusType.ACTIVE)
            .hideInDeposit(false)
            .type(ProcessorAccountType.BANK)
            .providerData(statusResponse.getTransactionID())
            .name(accountDetails.getDebtorAccountName())
            .descriptor(accountDetails.getDebtorIBAN())
            .data( new HashMap<String, String>() {{
                put("name", accountDetails.getDebtorAccountName());
                put("iban", accountDetails.getDebtorIBAN());
                put("bank_code", !StringUtil.isEmpty(accountDetails.getDebtorBankID()) ? accountDetails.getDebtorBankID() : request.stageInputData(1).get("bank_code"));
                put("country", accountDetails.getDebtorIBAN().substring(0,2));
            }}).build();
        return processorAccount;
    }


    public void processBluemNotification(String data) throws Exception {
        JsonNode jsonNode = mapper.readTree(data);
        Long transactionId = Long.parseLong(jsonNode.findValue("PaymentReference").asText());
        JsonNode paymentMethodDetailsNode = Optional.ofNullable(jsonNode.findValue("PaymentMethodDetails")).orElse(null);
        PaymentMethodDetailsComplexType paymentMethodDetails = paymentMethodDetailsNode != null ? mapper.treeToValue(paymentMethodDetailsNode, PaymentMethodDetailsComplexType.class) : null;

        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "bluem-ideal");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transaction for Bluem notification: " + data);
            throw new Exception(processorRequestResponse.getMessage());
        }
        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        BluemSignatureHandler.checkSignature(data, doProcessorRequest.getProperty("certificate"));

        DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
            .rawRequestLog("Received Bluem notification: " + mapper.readTree(data))
            .transactionId(doProcessorRequest.getTransactionId())
            .build();
        try {
            DoProcessorResponseStatus status = verifyDeposit(doProcessorRequest, doProcessorResponse, paymentMethodDetails);
            if (!doProcessorRequest.isTransactionFinalized()) {
                doProcessorResponse.setStatus(status);
            }
        } finally {
            callbackService.doSafeCallback(doProcessorResponse);
        }

    }

    public String processBluemDepositRedirect(Long transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "bluem-ideal");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transactionid: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
            .rawRequestLog("Received redirect from Bluem.")
            .transactionId(transactionId)
            .build();

        try {
            //The is an issue with doMachine synchronization in case notification and
            //BLUEM redirection handling on Lithium side is not expected to be enabled for now
            DoProcessorResponseStatus status = verifyDeposit(doProcessorRequest, doProcessorResponse, null);
            if (!doProcessorRequest.isTransactionFinalized()) {
                doProcessorResponse.setStatus(status);
            }
        } catch (Exception ex) {
            log.error("Failed to process redirect from Bluem. TransactionId: " + transactionId, ex);
        } finally {
            callbackService.doSafeCallback(doProcessorResponse);
        }
        return getReturnUrl(doProcessorRequest);
    }

    private String getRedirectUrl(DoProcessorRequest request, String state, String errorMessage) throws Exception {
        String returnUrl = getReturnUrl(request);
        URI returnUri = new URI(returnUrl);
        if (returnUri.getScheme().startsWith(HTTP_SCHEMA)) {
            return getReturnUrl(request) + "?trn_id=" + request.getTransactionId() + getStatusForClient(request, DoMachineState.fromName(state), errorMessage);
        } else {
            return getReturnUrl(request);
        }
    }

    private String getStatusForClient(DoProcessorRequest doProcessorRequest, DoMachineState state, String errorString) {
        if (state.isActive()) {
            return "&status=pending";
        }

        switch (state) {
            case SUCCESS:
                return "&status=success" + (!StringUtil.isEmpty(errorString) ? "&error=" + errorString : "");
            case PLAYER_CANCEL:
                return "&status=canceled&error=" +  GeneralError.CANCEL_TRANSACTION.getResponseMessageLocal(messageSource, doProcessorRequest.getUser().getDomain(), doProcessorRequest.getUser().getLanguage());
            case FATALERROR:
                return "&status=failed&error=" +  GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, doProcessorRequest.getUser().getDomain(), doProcessorRequest.getUser().getLanguage());
            case DECLINED:
            default:
                return "&status=failed&error=" +  GeneralError.VERIFY_INPUT_DETAILS.getResponseMessageLocal(messageSource, doProcessorRequest.getUser().getDomain(), doProcessorRequest.getUser().getLanguage());
        }
    }

    private String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl() + "/" + moduleName;
    }
}
