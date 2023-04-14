package lithium.service.cashier.processor.inpay.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.inpay.api.data.InpayAccount;
import lithium.service.cashier.processor.inpay.api.data.InpayDebtor;
import lithium.service.cashier.processor.inpay.api.data.InpayDebtorAccount;
import lithium.service.cashier.processor.inpay.api.data.InpayParticipant;
import lithium.service.cashier.processor.inpay.api.data.InpayReason;
import lithium.service.cashier.processor.inpay.api.data.InpayRequestData;
import lithium.service.cashier.processor.inpay.api.data.InpayState;
import lithium.service.cashier.processor.inpay.api.data.InpayTransactionData;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookData;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookDataV2;
import lithium.service.cashier.processor.inpay.data.InPayPaymentErrors;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.client.objects.Address;
import lithium.util.ExceptionMessageUtil;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

import java.util.List;


import static java.util.Objects.nonNull;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_NOTIFICATION_METHOD;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;


@Service
@Slf4j
@AllArgsConstructor
public class WithdrawService {

    @Autowired
    private CashierInternalClientService cashierService;

    private final ObjectMapper mapper;
    private final CashierDoCallbackService cashierDoCallbackService;
    private final RestTemplate restTemplate;
    private final InpayCryptoService cryptoService;
    private final MessageSource messageSource;

    private final String PARTICIPANT_TYPE = "private";
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @TimeThisMethod
    public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {

        try {
            InpayRequestData requestData = buildInpayRequestData(request);

            String xRequestID = UUID.nameUUIDFromBytes(request.getTransactionId().toString().getBytes()).toString();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", request.getProperty("X_AUTHORIZATION"));
            headers.add("X-Auth-Uuid", request.getProperty("X_AUTH_UUID"));
            headers.add("X-Request-ID", xRequestID);

            String encryptedRequest = cryptoService.signAndEncryptRequest(request.getProperty("MERCHANT_PRIVATE_KEY"), request.getProperty("MERCHANT_CERTIFICATE"), request.getProperty("INPAY_CERTIFICATE"), mapper.writeValueAsString(requestData));

            log.info("Initial withdraw request(" + request.getTransactionId() + ", " + xRequestID + "): " + requestData);
            response.addRawRequestLog("Initial withdraw request(" + request.getTransactionId() + ", " + xRequestID + "): " + objectToPrettyString(requestData));
            log.debug("Initial withdraw request (" + xRequestID + "): " + requestData + "\nencryptedRequest\n" + encryptedRequest);

            HttpEntity<String> entity = new HttpEntity<String>(encryptedRequest, headers);

            ResponseEntity<String> inpayResponse =
                    rest.exchange(request.getProperty("withdraw_api_url"), HttpMethod.POST, entity, String.class);

            String decryptedResponse = cryptoService.decryptAndVerifyResponse(request.getProperty("MERCHANT_PRIVATE_KEY"), request.getProperty("INPAY_CA_CHAIN"), inpayResponse.getBody());

            response.addRawResponseLog("Initial withdraw response: " + jsonObjectToPrettyString(decryptedResponse));
            log.info("Initial withdraw response (" + request.getTransactionId() + "): " + decryptedResponse);

            InpayTransactionData inpayTransactionData = mapper.readValue(decryptedResponse, InpayTransactionData.class);

            log.debug("Initial withdraw response (" + request.getTransactionId() + "): " + inpayTransactionData);

            if (inpayTransactionData.getState().equalsIgnoreCase("received")) {
                response.setTransactionId(Long.valueOf(inpayTransactionData.getEndToEndId()));
                response.setProcessorReference(inpayTransactionData.getInpayUniqueReference());
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if (inpayTransactionData.getState().equalsIgnoreCase("rejected")) {
                logErrorMessage(request, response, inpayTransactionData);
                if (inpayTransactionData.getInpayUniqueReference() != null || transactionAlreadyInitiated(inpayTransactionData)) {
                    response.addRawResponseLog("Transaction moved to next stage to try verify transaction status");
                    return DoProcessorResponseStatus.NEXTSTAGE;
                }
                String declineReason = buildDeclineReason(inpayTransactionData);
                response.setDeclineReason(declineReason);
                response.addRawResponseLog("Transaction was declined: " + declineReason);
                return DoProcessorResponseStatus.DECLINED;
            } else {
                logErrorMessage(request, response, inpayTransactionData);
                response.addRawResponseLog("Transaction moved to next stage to try verify transaction status");
                return DoProcessorResponseStatus.NEXTSTAGE;
            }
        } catch (Exception ex) {
            String message = "Withdraw stage1 failed (" + request.getTransactionId() + ") due: " + ex.getMessage() + ". Transaction moved to next stage to try verify transaction status";
            log.error(message, ex);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
            response.setMessage(ex.getMessage());
            response.setDeclineReason(ex.getMessage());
            return DoProcessorResponseStatus.NEXTSTAGE;
        }
    }

    private void logErrorMessage(DoProcessorRequest request, DoProcessorResponse response, InpayTransactionData inpayTransactionData) {
        InPayPaymentErrors topErrorReason = getTopInpayPaymentError(inpayTransactionData);
        response.setErrorCode(topErrorReason.getGeneralError().getCode());
        response.setMessage(topErrorReason.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
        response.addRawResponseLog("Got error during initiate withdraw: " + topErrorReason);
        log.info(topErrorReason + " .TransactionId=" + request.getTransactionId());
    }

    public DoProcessorResponseStatus verify(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) {
        DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;
        try {

            if (request.getProcessorReference() == null || request.getProcessorReference().isEmpty()) {
                log.error("Verify failed, no processor reference specified for transactionId:" + request.getTransactionId());
                response.setErrorCode(GeneralError.GENERAL_ERROR.getCode());
                response.setMessage(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.addRawResponseLog("Can't verify transaction on Inpay side due missing processor reference. Please, check it manually and make related action for transaction.");
                return status;
            }
            Map<String, String> uriVariables = new HashMap<>();
            String apiUrl = request.getProperty("get_payment_api_url") + "/" + request.getProcessorReference();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", request.getProperty("X_AUTHORIZATION"));
            headers.add("X-Auth-Uuid", request.getProperty("X_AUTH_UUID"));

            HttpEntity<?> entity = new HttpEntity<>(headers);
            log.info("Verify withdraw request(" + request.getTransactionId() + "): " + entity + ", uri variables: " + uriVariables);
            response.addRawRequestLog("Inpay verify withdraw request: " + httpEntityToPrettyString(entity));
            response.addRawRequestLog("uri variables: " + objectToPrettyString(uriVariables));

            ResponseEntity<String> inpayResponseObj = rest.exchange(apiUrl, HttpMethod.GET, entity, String.class, uriVariables);

            String decryptedResponse = cryptoService.decryptAndVerifyResponse(request.getProperty("MERCHANT_PRIVATE_KEY"), request.getProperty("INPAY_CA_CHAIN"), inpayResponseObj.getBody());

            response.addRawResponseLog("Verify withdraw response: " + jsonObjectToPrettyString(decryptedResponse));
            log.info("Verify withdraw response (" + request.getTransactionId() + "): " + decryptedResponse);

            InpayTransactionData inpayTransactionData = mapper.readValue(decryptedResponse, InpayTransactionData.class);

            log.debug("Verify withdraw response (" + request.getTransactionId() + "): " + inpayTransactionData);

            InpayState state = InpayState.getState(inpayTransactionData.getState());
            if (state.isFailed()) {
                logErrorMessage(request, response, inpayTransactionData);
                String declineReason = buildDeclineReason(inpayTransactionData);
                response.setDeclineReason(declineReason);
                response.addRawResponseLog("Transaction was declined: " + declineReason);
                status = DoProcessorResponseStatus.DECLINED;
            } else {
                if (InpayState.COMPLETED.getStatus().equalsIgnoreCase(state.getStatus())) {
                    response.setAmountCentsReceived(CurrencyAmount.fromAmountString(inpayTransactionData.getAmount()).toCents().intValue());
                    status = DoProcessorResponseStatus.SUCCESS;
                }
            }

            if (state.isReturned()) {
                ProcessorNotificationData mailData = new ProcessorNotificationData();
                mailData.setRecipientTypes(new String[]{"external"});
                mailData.setTemplateName(request.getProperty("reversal_notification_template"));
                mailData.setPlaceholders(constructPlaceholders("Withdraw"));
                mailData.setTo(request.getProperty("inpay_email"));
                response.setNotificationData(mailData);
            }

            response.setStatus(status);
        } catch (Exception ex) {
            log.error("Error during verify transaction: [ " + request.getTransactionId() + " ]", ex);
        }
        return status;
    }


	public Set<Placeholder> constructPlaceholders(String notificationMethod) {
		Set<Placeholder> placeholders = new HashSet<>();
		placeholders.add(CASHIER_NOTIFICATION_METHOD.from(notificationMethod));
		return placeholders;
	}

    public void proceedWithdrawWebhook(InpayWebhookData inpayWebhookData) throws Exception {

        Long transactionId = Long.valueOf(inpayWebhookData.getOrder_id());
        DoProcessorRequest request = cashierDoCallbackService.getTransaction(transactionId, "inpay");
        request.setProcessorReference(inpayWebhookData.getInvoice_reference());

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(transactionId)
                .rawRequestLog("Received webhook call: " + objectToPrettyString(inpayWebhookData))
                .build();
        cashierDoCallbackService.doSafeCallback(response);
        response.addRawResponseLog("Inpay webhook data: " + objectToPrettyString(inpayWebhookData));

        try {
            DoProcessorResponseStatus status = verify(request, response, restTemplate);
            response.setStatus(status);
        } finally {
            checkFinalizedAndStatus(request, response);
            cashierDoCallbackService.doSafeCallback(response);
        }
    }

    public void proceedWithdrawWebhookV2(String domainName, String cryptedWebhookData) throws Exception {

        Map<String, String> dmpProperties = getProcessorProperties(domainName);
        String decryptedRequestData = cryptoService.decryptAndVerifyResponse(dmpProperties.get("MERCHANT_PRIVATE_KEY"), dmpProperties.get("INPAY_CA_CHAIN"), cryptedWebhookData);

        InpayWebhookDataV2 inpayWebhookData = mapper.readValue(decryptedRequestData, InpayWebhookDataV2.class);

        Long transactionId = Long.valueOf(inpayWebhookData.getEndToEndId());
        DoProcessorRequest request = cashierDoCallbackService.getTransaction(transactionId, "inpay");
        request.setProcessorReference(inpayWebhookData.getInpayUniqueReference());

        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(transactionId)
                .rawRequestLog("Received webhook call: " + objectToPrettyString(inpayWebhookData))
                .processorReference(inpayWebhookData.getInpayUniqueReference())
                .build();
        cashierDoCallbackService.doSafeCallback(response);

        try {
            DoProcessorResponseStatus status = verify(request, response, restTemplate);
            response.setStatus(status);
        } finally {
            checkFinalizedAndStatus(request, response);
            cashierDoCallbackService.doSafeCallback(response);
        }
    }


    private InpayRequestData buildInpayRequestData(DoProcessorRequest request) throws Exception {
        ProcessorAccount processorAccount = request.getProcessorAccount();
        String iban = processorAccount.getData().get("iban");

        if (StringUtil.isEmpty(iban)) {
            throw new Status500InternalServerErrorException("Invalid IBAN value");
        }

        DoProcessorRequestUser user = request.getUser();
        InpayParticipant inpayParticipant = buildInpayParticipant(user);

        InpayRequestData inpayRequestData = InpayRequestData.builder()
                .amount(request.inputAmount().toString())
                .currencyCode(user.getCurrency())
                .endToEndId(request.getTransactionId().toString())
                .remittanceDescription(request.getProperty("payment_purpose"))
                .localInstrument(request.getProperty("local_instrument"))
                .creditor(inpayParticipant)
                .creditorAccount(InpayAccount.builder()
                        .iban(iban)
                        .countryCode(user.getCountryCode())
                        .build())
                .ultimateDebtor(inpayParticipant)
                .debtor(InpayDebtor.builder()
                        .name(request.getProperty("merchant_name"))
                        .build())
                .debtorAccount(InpayDebtorAccount.builder()
                        .schemeName(request.getProperty("debtor_account_sheme_name"))
                        .id(Long.valueOf(request.getProperty("debtor_account_id")))
                        .build())
                .build();

        return inpayRequestData;
    }

    private InpayParticipant buildInpayParticipant(DoProcessorRequestUser user) {
        InpayParticipant inpayParticipant = InpayParticipant.builder()
                .type(PARTICIPANT_TYPE)
                .birthDate(formatDateValue(user.getDateOfBirth()))
                .name(user.getFullName())
                .countryCode(user.getCountryCode())
                .email(user.getEmail())
                .build();

        Address address = user.getResidentialAddress();
        if (address != null) {
            inpayParticipant.setAddressLines(address.toOneLinerStreet());
            inpayParticipant.setCity(address.getCity());
            inpayParticipant.setPostcode(address.getPostalCode());
        }
        return inpayParticipant;

    }

    private String formatDateValue(DateTime dateTime) {
        if (dateTime == null) return null;
        return DATE_FORMAT.format(dateTime.toDate());
    }

    private InPayPaymentErrors getTopInpayPaymentError(InpayTransactionData inpayWebhookData) {
        if (inpayWebhookData.getReasons() != null && !inpayWebhookData.getReasons().isEmpty()) {
            return InPayPaymentErrors.fromErrorCode(inpayWebhookData.getReasons().stream().findFirst().get().getCode());
        }
        return InPayPaymentErrors.TECHNICAL_ISSUE_UNKNOWN;
    }

    private String buildDeclineReason(InpayTransactionData inpayTransactionData) {
        if (inpayTransactionData.getReasons() == null || inpayTransactionData.getReasons().isEmpty()) {
            return "";
        }
        return inpayTransactionData.getReasons()
                .stream()
                .map(inpayReason -> inpayReason.getCode() + formatDetails(inpayReason))
                .collect(Collectors.joining(","));
    }

    private String formatDetails(InpayReason inpayReason) {
        if (inpayReason.getDetails() == null || inpayReason.getDetails().isEmpty()) {
            return "";
        }
        return inpayReason.getDetails().entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + getDetailStringValue(entry.getValue()))
                .collect(Collectors.joining(";", " (", ")"));
    }

    private String getDetailStringValue(Object valueObject) {
        try {
            if (valueObject instanceof List) {
                return ((List<Object>) valueObject)
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
            }
        } catch (Exception ex) {
            log.warn("Cant parse Inpay detail value {"+ valueObject +"}");
        }
        return valueObject.toString();
    }

    private boolean transactionAlreadyInitiated(InpayTransactionData inpayTransactionData) {
        return Optional.ofNullable(inpayTransactionData.getReasons()).orElse(new ArrayList<>())
                .stream().map(inpayReason -> inpayReason.getDetails())
                .map(details -> details.get("end_to_end_id"))
                .filter(o -> o instanceof List)
                .map(o -> (List<String>) o)
                .flatMap(Collection::stream)
                .filter(s -> s.contains("was already used"))
                .findAny().isPresent();
    }

    public static void checkFinalizedAndStatus(DoProcessorRequest request, DoProcessorResponse response) {
        if (request.isTransactionFinalized() && nonNull(response.getStatus())) {
            log.warn("Transaction (" + request.getTransactionId() + ") already finalized and can't be change status to " + response.getStatus().name());
            response.addRawResponseLog("Transaction already finalized and can't be change status to " + response.getStatus().name());
            response.setStatus(null);
        }
    }

    private Map<String, String> getProcessorProperties(String domainName) throws Status500InternalServerErrorException {
        try {
            Map<String, String> dmpProperties = cashierService.propertiesOfFirstEnabledProcessorByMethodCode(domainName, false, "inpay");

            if (dmpProperties.size() == 0) {
                log.warn("Invalid processor configuration");
                throw new Status500InternalServerErrorException("Invalid processor configuration");
            }
            return dmpProperties;
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
        }
    }
}
