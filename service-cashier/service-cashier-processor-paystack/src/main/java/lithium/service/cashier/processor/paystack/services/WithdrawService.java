package lithium.service.cashier.processor.paystack.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.cashier.client.exceptions.Status439BankAccountLookupClientException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.processor.paystack.api.schema.PaystackTransferRecipientRequest;
import lithium.service.cashier.processor.paystack.api.schema.PaystackTransferRecipientResponse;
import lithium.service.cashier.processor.paystack.api.schema.PaystackTransferRequest;
import lithium.service.cashier.processor.paystack.api.schema.PaystackTransferResponse;
import lithium.service.cashier.processor.paystack.api.schema.WebhookWithdrawRequestData;
import lithium.service.cashier.processor.paystack.api.schema.banklist.PaystackBank;
import lithium.service.cashier.processor.paystack.api.schema.banklist.PaystackBankListResponse;
import lithium.service.cashier.processor.paystack.data.AccountVerificationResponse;
import lithium.service.cashier.processor.paystack.exeptions.PaystackAlreadyInitiatedTransactionException;
import lithium.service.cashier.processor.paystack.exeptions.PaystackServiceHttpErrorException;
import lithium.service.cashier.processor.paystack.exeptions.PaystackWrongConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.checkFinalizedAndStatus;
import static lithium.service.cashier.processor.paystack.util.PaystackCommonUtils.getPaystackMessageFromBody;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.jsonObjectToPrettyString;

@Service
@Slf4j
public class WithdrawService extends BasePaystackService {

    @Autowired
    private CashierDoCallbackService cashier;
    @Autowired
    private WithdrawVerifyService verifyService;
    @Autowired
    private ObjectMapper mapper;

    public final static String UNDEFINED_REASON = "Transfer from LiveScore";
    public final static String DEFAULT_REASON = "Undefined decline reason";
    public final static String TECHNICAL_ERROR_REASON = "Technical Error";

    public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        MultiValueMap<String, String> headers = prepareHeaders(request);

        PaystackTransferRecipientResponse transferRecipientResponseData = null;
        try {
            transferRecipientResponseData = sendTransferRecipient(request, response, rest, headers);
        } catch (PaystackServiceHttpErrorException ex) {
            return buildDeclineResponse(ex.getMessage(), request.getTransactionId(), response) ;
        } catch (Exception ex) {
            String message = "Withdraw initialization failed (" + request.getTransactionId() + ") due to " + ex.getMessage() + ". Declined during getting transfer recipient.";
            log.error(message);
            response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
            response.setMessage(ex.getMessage());
            response.setDeclineReason(TECHNICAL_ERROR_REASON);
            return DoProcessorResponseStatus.DECLINED;
        }

        if (!"true".equalsIgnoreCase(transferRecipientResponseData.getStatus())) {
            return buildDeclineResponse(transferRecipientResponseData.getMessage(), request.getTransactionId(), response) ;
        }
        PaystackTransferResponse paystackTransferResponse = sendTransfer(request, response, rest,  transferRecipientResponseData.getData().getRecipientCode(), headers);
        if (!checkPaystackTransferResponse(paystackTransferResponse)) {
            return buildDeclineResponse(transferRecipientResponseData.getMessage(), request.getTransactionId(), response) ;
        }
        return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
    }

    private DoProcessorResponseStatus buildDeclineResponse(String declineReason, long transactionId, DoProcessorResponse response) {
        log.error("Paystack transaction id=" + transactionId + " failed. Message=" + declineReason);
        response.setDeclineReason(resolveDeclineReason(declineReason));
        return DoProcessorResponseStatus.DECLINED;
    }

    private PaystackTransferRecipientResponse sendTransferRecipient(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest, MultiValueMap<String, String> headers) throws PaystackWrongConfigurationException, IOException, PaystackServiceHttpErrorException {

        ProcessorAccount processorAccount = request.getProcessorAccount(); //If it's not null - direct withdraw
        String bank_code = ofNullable(processorAccount).map(account -> account.getData().get("bank_code"))
                .orElse(inputData("bank_code", request));
        String account_number = ofNullable(processorAccount).map(account -> account.getData().get("account_number"))
                .orElse(inputData("account_number", request));

        PaystackTransferRecipientRequest transferRecipientRequest = PaystackTransferRecipientRequest.builder()
                .type(property("paystack_recipient_type", request))
                .name(request.getUser().getUsername())
                .accountNumber(account_number)
                .bankCode(bank_code)
                .currency(request.getUser().getCurrency())
                .build();

        response.addRawRequestLog("Transfer recipient request: " + objectToPrettyString(transferRecipientRequest));

        log.debug("Paystack transfer recipient request: " + transferRecipientRequest + " .TransactionId:" + request.getTransactionId());

        String transferRecipientApiUrl = property("paystack_transfer_recipient_api_url", request);

        HttpEntity<PaystackTransferRecipientRequest> entity = new HttpEntity<>(transferRecipientRequest, headers);

        ResponseEntity<String> exchange =
                rest.exchange(transferRecipientApiUrl, HttpMethod.POST, entity, String.class, new HashMap<>());
        response.addRawResponseLog("Transfer recipient response: " + httpEntityToPrettyString(exchange));
        if (!is2xxHttpStatus(exchange.getStatusCodeValue())) {
            log.error("Paystack transfer recipient call failed (" + exchange.getStatusCodeValue() + ") " + exchange.getBody() + "(" + request.getTransactionId() + ")");
            throw new PaystackServiceHttpErrorException(getPaystackMessageFromBody(mapper, exchange.getBody()), exchange.getStatusCodeValue());
        }
        log.debug("Transfer recipient response: " + exchange.getBody() + ". (" + request.getTransactionId() + ")");
        return mapper.readValue(exchange.getBody(), PaystackTransferRecipientResponse.class);
    }

    private PaystackTransferResponse sendTransfer(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest, String recipientCode, MultiValueMap<String, String> headers) throws PaystackWrongConfigurationException, PaystackAlreadyInitiatedTransactionException, IOException, PaystackServiceHttpErrorException {

        PaystackTransferRequest paystackTransferRequest = PaystackTransferRequest.builder()
                .amount(inputCents(request))
                .recipient(recipientCode)
                .source(property("paystack_transfer_source", request))
                .reference(request.getTransactionId().toString())
                .build();

        log.debug("Paystack transfer request: " + paystackTransferRequest + ". TransactionId: " + request.getTransactionId());
        response.addRawRequestLog("Transfer request: " + objectToPrettyString(paystackTransferRequest));

        HttpEntity<PaystackTransferRequest> entity = new HttpEntity<>(paystackTransferRequest, headers);

        String transferApiUrl = property("paystack_transfer_api_url", request);
        ResponseEntity<String> exchange =
                rest.exchange(transferApiUrl, HttpMethod.POST, entity, String.class, new HashMap<>());

        response.addRawResponseLog("Transfer response: " + httpEntityToPrettyString(exchange));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Paystack transfer request call failed (" + exchange.getStatusCodeValue() + ") " + exchange.getBody() + "(" + request.getTransactionId() + ")");
            String message = getPaystackMessageFromBody(mapper, exchange.getBody());
            if (message.contains("Please provide a unique reference. Reference already exists on a transfer")) {
                throw new PaystackAlreadyInitiatedTransactionException();
            }
            throw new PaystackServiceHttpErrorException(message, exchange.getStatusCodeValue());
        }
        log.debug("Paystack transfer response: " + exchange.getBody() + ". TransactionId: " + request.getTransactionId());

        return mapper.readValue(exchange.getBody(), PaystackTransferResponse.class);
    }

    private boolean checkPaystackTransferResponse(PaystackTransferResponse paystackTransferResponse) {
        return Boolean.parseBoolean(paystackTransferResponse.getStatus()) &&
                (paystackTransferResponse.getData().getStatus().equalsIgnoreCase("success") ||
                        paystackTransferResponse.getData().getStatus().equalsIgnoreCase("pending"));
    }

    public void proceedWithdrawWebhook(@RequestBody String data, WebhookWithdrawRequestData webhookRequestData) throws Exception {

        DoProcessorResponseStatus status = DoProcessorResponseStatus.NOOP;

        String reference = webhookRequestData.getReference();
        DoProcessorResponse response = DoProcessorResponse.builder()
                .transactionId(Long.parseLong(reference))
                .processorReference(webhookRequestData.getId().toString())
                .status(status)
                .additionalReference(webhookRequestData.getTransferCode())
                .rawRequestLog("Received webhook call: " + jsonObjectToPrettyString(data))
                .build();

	    DoProcessorRequest cashierTransaction = cashier.getTransaction(Long.parseLong(reference), "paystack");
	    checkFinalizedAndStatus(cashierTransaction, response);
	    cashier.doSafeCallback(response);

        try {
	        verifyService.verify(cashierTransaction, response);
        } catch (Exception ex) {
	        String message = "Withdraw transaction id=" + Long.parseLong(reference) + " verification failed (" + reference + ") due " + ex.getMessage();
	        log.error(message);
	        response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
	        response.setMessage(ex.getMessage());
	        throw ex;
        } finally {
		    checkFinalizedAndStatus(cashierTransaction, response);
		    cashier.doSafeCallback(response);
	    }
    }

    @Cacheable(value = "lithium.service.cashier.processor.paystack.banks", cacheManager = "banksCacheManager", unless = "#result == null")
    public List<PaystackBank> getBankList(String url, String secretKey, RestTemplate restTemplate) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", "Bearer " + secretKey);
        headers.add("content-type", "application/json");
        headers.add("User-Agent", "Paystack-Developers-Hub");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        Map<String, String> map = new HashMap<>();

        ResponseEntity<String> banksResponseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class, map);
        if (!banksResponseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Paystack bank list call failed (" + banksResponseEntity.getStatusCodeValue() + ") " + banksResponseEntity.getBody());
            throw new Exception("Paystack bank list call failed (" + banksResponseEntity.getStatusCodeValue() + ") " + banksResponseEntity.getBody());
        }

        log.debug("Get banks list response: " + banksResponseEntity);
        PaystackBankListResponse fwResponse =
                mapper.readValue(banksResponseEntity.getBody(), PaystackBankListResponse.class);

        if (!fwResponse.isStatus()) {
            throw new Exception("Get banks list did not return success: " + fwResponse.isStatus() + " " + fwResponse.getMessage());
        }
        return fwResponse.getData();
    }

    public Response<BankAccountLookupResponse> bankAccountLookup(BankAccountLookupRequest bankAccountLookupRequest, RestTemplate restTemplate) throws Exception {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        Map<String, String> domainMethodProcessorProperties = bankAccountLookupRequest.getDomainMethodProcessorProperties();
        try {
            if (bankAccountLookupRequest.getAccountNumber() == null) {
                throw new Status439BankAccountLookupClientException("Missing 'account_number' in withdraw request");
            }
            String accountNumber = bankAccountLookupRequest.getAccountNumber();
            if (bankAccountLookupRequest.getBankCode() == null) {
                throw new Status439BankAccountLookupClientException("Missing 'bank_code' in withdraw request");
            }
            String bankCode = bankAccountLookupRequest.getBankCode();
            String banksUrl = domainMethodProcessorProperties.get("withdraw_banks_url");
            String secretKey = domainMethodProcessorProperties.get("secret_key");
            String bankName = getBankName(bankAccountLookupRequest, restTemplate, bankCode, banksUrl, secretKey);
            AccountVerificationResponse accountValidationResponse = getAccountVerificationResponse(restTemplate, secretKey, accountNumber, bankCode, domainMethodProcessorProperties);
            String accountName = accountValidationResponse.getData().getAccountName();
            return Response.<BankAccountLookupResponse>builder().data(buildBankAccountLookupResponse(accountNumber, accountName, bankCode, bankName, accountValidationResponse)).build();
        } catch (Status439BankAccountLookupClientException e) {
            log.error("Bank Account Lookup Failed: {}", e.getMessage());
            return Response.<BankAccountLookupResponse>builder().data(buildFailedBankAccountLookupResponse(e.getMessage())).build();
        }
    }

    private BankAccountLookupResponse buildFailedBankAccountLookupResponse(String failedStatusReasonMessage) {
        return BankAccountLookupResponse.builder().status("Failed").failedStatusReasonMessage(failedStatusReasonMessage).build();
    }

    private BankAccountLookupResponse buildBankAccountLookupResponse(String accountNumber, String accountName, String bankCode,
                                                                     String bankName, AccountVerificationResponse accountValidationResponse) {
        return BankAccountLookupResponse.builder()
                .status("Success").accountNumber(accountNumber).accountName(accountName).bankCode(bankCode).bankName(bankName)
                .message(accountValidationResponse.getMessage()).bankId(accountValidationResponse.getData().getBankId()).build();
    }

    private AccountVerificationResponse getAccountVerificationResponse(RestTemplate restTemplate, String secretKey, String accountNumber, String bankCode,
                                                                       Map<String, String> domainMethodProcessorProperties) throws Status439BankAccountLookupClientException, IOException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + secretKey);
        headers.add("User-Agent", "Paystack-Developers-Hub");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        Map<String, String> map = new HashMap<>();
        String url = UriComponentsBuilder.fromHttpUrl(domainMethodProcessorProperties.get("account_verification_url"))
                .queryParam("account_number", accountNumber)
                .queryParam("bank_code", bankCode)
                .toUriString();
        ResponseEntity<String> accountValidationResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, map);
        log.debug("Validation response: " + accountValidationResponse);
        if (!accountValidationResponse.getStatusCode().is2xxSuccessful()) {
            throw new Status439BankAccountLookupClientException("Invalid input data. Please check your bank name, NUBAN details and try again.");
        }
        return mapper.readValue(accountValidationResponse.getBody(), AccountVerificationResponse.class);
    }

    private String getBankName(BankAccountLookupRequest bankAccountLookupRequest, RestTemplate restTemplate, String bankCode, String banksUrl, String secretKey) throws Exception {
        return bankAccountLookupRequest.getBankName() != null
                ? bankAccountLookupRequest.getBankName()
                : getBankList(banksUrl, secretKey, restTemplate).stream()
                .filter(b -> b.getCode().equals(bankCode))
                .findFirst()
                .map(PaystackBank::getName)
                .orElseThrow(() -> new Status439BankAccountLookupClientException("'bank_code' is invalid"));
    }

    public static String resolveDeclineReason(String responseReason) {
    	if (UNDEFINED_REASON.equalsIgnoreCase(responseReason)) {
    		return DEFAULT_REASON;
	    } else {
    		return responseReason;
	    }
    }

    /* Using this method instead of exchange.getStatusCode().is2xxSuccessful() is due to avoid IllegalArgumentException
     * which is thrown when http status code (e.g. 521) could not be found in HttpStatus Enum */
    private static boolean is2xxHttpStatus(int statusCodeValue) {
        return HttpStatus.Series.resolve(statusCodeValue) == HttpStatus.Series.SUCCESSFUL;
    }
}
