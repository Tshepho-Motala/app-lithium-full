package lithium.service.cashier.processor.interswitch.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.cashier.client.exceptions.Status439BankAccountLookupClientException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.processor.interswitch.api.schema.AccountReceivable;
import lithium.service.cashier.processor.interswitch.api.schema.Bank;
import lithium.service.cashier.processor.interswitch.api.schema.Beneficiary;
import lithium.service.cashier.processor.interswitch.api.schema.Initiation;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchBadResponse;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchBankListResponse;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchError;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchErrorDescriptor;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchTransactionVerifyResponse;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchWithdrawRequest;
import lithium.service.cashier.processor.interswitch.api.schema.InterswitchWithdrawResponse;
import lithium.service.cashier.processor.interswitch.api.schema.Sender;
import lithium.service.cashier.processor.interswitch.api.schema.Termination;
import lithium.service.cashier.processor.interswitch.data.AccountVerificationResponse;
import lithium.service.cashier.processor.interswitch.exceptions.Status511InterswitchServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public class WithdrawService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    private String getEmail(DoProcessorRequest request) throws Exception {
        DoProcessorRequestUser user = request.getUser();
        String email = user.getEmail();
        if (email == null || email.trim().isEmpty()) {
            String dummyEmail = request.getProperty("dummy_email");
            if (dummyEmail != null && !dummyEmail.trim().isEmpty()) {
                email = request.getProperty("dummy_email");
            }
        }
        return email;
    }

    public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

	    rest.setErrorHandler(new DefaultResponseErrorHandler() {
		    @Override
		    public boolean hasError(ClientHttpResponse response) throws IOException {
			    return false;
		    }
	    });

	    String initiatingAmount = request.inputAmountCents().toString();
	    String terminatingAmount = initiatingAmount;

	    String initiatingCurrencyCode = request.getProperty("currency_code");
	    String terminatingCurrencyCode = initiatingCurrencyCode;

	    String terminatingCountryCode = request.getProperty("country_code");

	    String initiatingPaymentMethodCode = request.getProperty("initiation_payment_method_code");
	    String terminatingPaymentMethodCode = request.getProperty("terminating_payment_method_code");

	    String macCipher = initiatingAmount + initiatingCurrencyCode + initiatingPaymentMethodCode + terminatingAmount
			    + terminatingCurrencyCode + terminatingPaymentMethodCode + terminatingCountryCode;

	    String bank_code;
	    String account_number;

	    ProcessorAccount processorAccount = request.getProcessorAccount();
	    if (processorAccount!=null) {
		    // is direct withdraw
		    bank_code = processorAccount.getData().get("bank_code");
		    account_number = processorAccount.getData().get("account_number");
	    } else {
		    bank_code = request.stageInputData(1, "bank_code");
		    account_number = request.stageInputData(1, "account_number");
	    }

	    InterswitchWithdrawRequest interswitchWithdrawRequest = InterswitchWithdrawRequest.builder()
			    .mac(encryptThisString(macCipher, request))
			    .transferCode(request.getProperty("request_reference_prefix") + request.getTransactionId())
			    .initiatingEntityCode(request.getProperty("initiating_entity_code"))

			    .beneficiary(Beneficiary.builder()
					    .lastname(request.getUser().getLastName())
					    .othernames(request.getUser().getFirstName())
					    .build()
			    )
			    .initiation(Initiation.builder()
					    .amount(initiatingAmount)
					    .channel(request.getProperty("initiation_channel"))
					    .currencyCode(initiatingCurrencyCode)
					    .paymentMethodCode(initiatingPaymentMethodCode)
					    .build()
			    )
                .sender(Sender.builder()
                        .email(getEmail(request))
                        .lastname(request.getUser().getLastName())
                        .othernames(request.getUser().getFirstName())
                        .phone(request.getUser().getCellphoneNumber())
                        .build()
                )
                .termination(Termination.builder()
                        .accountReceivable(AccountReceivable.builder()
                                .accountNumber(account_number)
                                .accountType(request.getProperty("terminating_account_type"))
                                .build()
                        )
                        .amount(terminatingAmount)
                        .countryCode(terminatingCountryCode)
                        .currencyCode(terminatingCurrencyCode)
                        .entityCode(bank_code)
                        .paymentMethodCode(terminatingPaymentMethodCode)
                        .build()
                )
                .build();

        log.info("Interswitch withdraw request body(" + request.getTransactionId() + ") : " + interswitchWithdrawRequest);

        String apiUrl = request.getProperty("withdraw_api_url");

        MultiValueMap<String, String> headers = getHeadersMap(apiUrl, getTimestampInSeconds(request), request.getProperty("client_id"),
                request.getProperty("secret_key"), request.getProperty("header_signature_method"), request.getProperty("terminal_id"), false);

        HttpEntity<InterswitchWithdrawRequest> entity = new HttpEntity<>(interswitchWithdrawRequest, headers);

        response.addRawRequestLog("Initiate Interswitch withdraw request: " + objectToPrettyString(interswitchWithdrawRequest));
        ResponseEntity<Object> responseEntity = rest.exchange(apiUrl, HttpMethod.POST, entity, Object.class);
        log.info("Initiate Interswitch withdraw response(" + request.getTransactionId() + "): " + responseEntity);
        response.addRawResponseLog("Initiate withdraw response: " + objectToPrettyString(responseEntity.getBody()));

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
	        if (responseEntity.getBody() != null) {
		        InterswitchBadResponse interswitchBadResponse = mapper.convertValue(responseEntity.getBody(), InterswitchBadResponse.class);
		        InterswitchErrorDescriptor interswitchErrorDescriptor = interswitchBadResponse.getError();
		        String status = interswitchErrorDescriptor.getResponseCodeGrouping();
		        String errorMessage = interswitchErrorDescriptor.getMessage();
		        String declineReason = "(" + status + ") " + errorMessage;
		        response.setDeclineReason(declineReason);
		        return DoProcessorResponseStatus.DECLINED;
	        }
	        throw new Status511InterswitchServiceException("Can't initiate withdraw(" + request.getTransactionId() + "), got wrong response: " + responseEntity.getBody());
        }

        InterswitchWithdrawResponse interswitchWithdrawResponse = mapper.convertValue(responseEntity.getBody(), InterswitchWithdrawResponse.class);

        String responseCodeGrouping = interswitchWithdrawResponse.getResponseCodeGrouping();
        boolean isSuccess = responseCodeGrouping.equalsIgnoreCase("SUCCESSFUL") ||
                responseCodeGrouping.equalsIgnoreCase("PENDING");

        if (isSuccess) {
            response.setProcessorReference(interswitchWithdrawResponse.getTransferCode());
            return DoProcessorResponseStatus.NEXTSTAGE;
        } else {
	        String declineReason = "(" + interswitchWithdrawResponse.getResponseCode() + ") " + interswitchWithdrawResponse.getResponseCodeGrouping();
	        response.setDeclineReason(declineReason);
	        log.info(declineReason + " .TransactionId=" + request.getTransactionId());
	        return DoProcessorResponseStatus.DECLINED;
        }
    }

    public DoProcessorResponseStatus verify(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        if (request.getProcessorReference() == null) {
            return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        }

        DoProcessorResponseStatus status = DoProcessorResponseStatus.PENDING_AUTO_RETRY;
        InterswitchTransactionVerifyResponse interswitchTransactionVerifyResponse = getInterswitchTransaction(request, response, rest);

        String interswitchStatus = interswitchTransactionVerifyResponse.getStatus();

        if (interswitchStatus.equalsIgnoreCase("Complete")) {
            BigDecimal finalAmount = new BigDecimal(interswitchTransactionVerifyResponse.getAmount());
            response.setAmountCentsReceived(finalAmount.intValue());
            status = DoProcessorResponseStatus.SUCCESS;
			if (request.getProcessorAccount()==null) {
				ProcessorAccount processorAccount = createProcessorAccount(
						request.stageInputData(1, "account_number"),
						request.stageInputData(1, "bank_code"),
						interswitchTransactionVerifyResponse.getCustomer(),
						request.getUser());
				if (processorAccount != null) {
					response.setProcessorAccount(processorAccount);
				}
			}
        } else if (interswitchStatus.toLowerCase().contains("fail")) {
	        String declineReason;
	        InterswitchError interswitchError = interswitchTransactionVerifyResponse.getError();
	        if (interswitchError != null) {
		        declineReason = "(" + interswitchError.getCode() + ") " + interswitchError.getMessage();
	        } else {
		        declineReason = "Interswitch declined transaction";
	        }
	        response.setDeclineReason(declineReason);
	        log.info(declineReason + " .TransactionId=" + request.getTransactionId());
	        status = DoProcessorResponseStatus.DECLINED;
        }

        response.setAdditionalReference(interswitchTransactionVerifyResponse.getTransactionRef());
        response.setStatus(status);
        response.addRawResponseLog("Received status response: " + status);

        return status;
    }

    public List<Bank> getBankList(Map<String, String> map) throws Exception {
        long timestamp = getTimestampInSecondsByProperties(map);
        String apiUrl = map.get("bank_list_url");
        MultiValueMap<String, String> headers = getHeadersMap(apiUrl, timestamp, map.get("client_id"), map.get("secret_key"),
                map.get("header_signature_method"), map.get("terminal_id"), true);

        ResponseEntity<Object> bankListResponseResponse =
                restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), Object.class, new HashMap<>());

        log.info("Get Banks list response " + bankListResponseResponse);
        InterswitchBankListResponse banks = mapper.convertValue(bankListResponseResponse.getBody(), InterswitchBankListResponse.class);
        return banks.getBanks();
    }

    private long getTimestampInSeconds(DoProcessorRequest request) throws Exception {
        TimeZone currentTimeZone = TimeZone.getTimeZone(request.getProperty("time_zone"));
        return getTimeZone(currentTimeZone);
    }

    private long getTimestampInSecondsByProperties(Map<String, String> propertiesMap) {
        TimeZone currentTimeZone = TimeZone.getTimeZone(propertiesMap.get("time_zone"));
        return getTimeZone(currentTimeZone);
    }

    private long getTimeZone(TimeZone lagosTimeZone) {
        Calendar calendar = Calendar.getInstance(lagosTimeZone);
        return calendar.getTimeInMillis() / 1000;
    }

    private MultiValueMap<String, String> getHeadersMap(String apiUrl, long timestamp, String clientId, String secretKey, String signatureMethod, String terminalId, boolean isGetMethod) throws Exception {
        HttpMethod httpMethod;
        if (isGetMethod) {
            httpMethod = HttpMethod.GET;
        } else {
            httpMethod = HttpMethod.POST;
        }
        String nonce = getNonce();
        String signature = getSignature(httpMethod, timestamp, nonce, signatureMethod, encodeValue(apiUrl), clientId, secretKey);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("Authorization", getAutorization(clientId));
        headers.add("Content-Type", "application/json");
        headers.add("Signature", signature);
        headers.add("Timestamp", String.valueOf(timestamp));
        headers.add("Nonce", nonce);
        headers.add("SignatureMethod", signatureMethod);
        if (isGetMethod) {
            headers.add("TerminalID", terminalId);
        }

        String message;
        if (isGetMethod) {
            message = "Interswitch get transaction request headers : ";
        } else {
            message = "Interswitch withdraw request headers : ";
        }

        log.info(message + headers);

        return headers;
    }

    private String getNonce() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    private String getAutorization(String clientId) {
        return "InterswitchAuth " + new String(Base64.encodeBase64(clientId.getBytes()));
    }

    private String getSignature(HttpMethod httpMethod, long timestamp, String nonce, String signatureMethod, String encodedResourceUrl, String clientId, String clientSecretKey) throws NoSuchAlgorithmException {
        String signatureCipher = httpMethod.name() + "&" + encodedResourceUrl + "&" + timestamp + "&" + nonce + "&" + clientId + "&" + clientSecretKey;
        MessageDigest messageDigest = MessageDigest.getInstance(signatureMethod);
        byte[] signatureBytes = messageDigest.digest(signatureCipher.getBytes());
        return new String(Base64.encodeBase64(signatureBytes));
    }

    private String encryptThisString(String input, DoProcessorRequest request) {
        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(request.getProperty("signature_method"));
            digest.reset();
            digest.update(input.getBytes(request.getProperty("hash_charset")));
            toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            log.error("Hash create error: " + e.getMessage());
            e.printStackTrace();
        }
        return toReturn;
    }

    private InterswitchTransactionVerifyResponse getInterswitchTransaction(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        long timestamp = getTimestampInSeconds(request);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("transactionRef", request.getProcessorReference());
        String apiUrl = request.getProperty("withdraw_transaction_api_url");

        URI expanded = rest.getUriTemplateHandler().expand(apiUrl, uriVariables);

        MultiValueMap<String, String> headers = getHeadersMap(expanded.toString(), timestamp, request.getProperty("client_id"),
                request.getProperty("secret_key"), request.getProperty("header_signature_method"), request.getProperty("terminal_id"), true);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        log.info("Verify withdraw request(" + request.getTransactionId() + "): " + entity + ", uri variables: " + uriVariables);
        response.addRawRequestLog("Verify withdraw request: " + httpEntityToPrettyString(entity));
        response.addRawRequestLog("uri variables: " + objectToPrettyString(uriVariables));

        ResponseEntity<Object> verifyResponseEntity = rest.exchange(apiUrl, HttpMethod.GET, entity, Object.class, uriVariables);

        log.info("Verify withdraw response(" + request.getTransactionId() + "): " + verifyResponseEntity);
        response.addRawResponseLog("Verify withdraw response: (" + verifyResponseEntity.getStatusCodeValue() + ") " + objectToPrettyString(verifyResponseEntity.getBody()));

        if (!verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new Status511InterswitchServiceException("Can't verify withdraw, got wrong response: " + verifyResponseEntity.getBody());
        }
        return mapper.convertValue(verifyResponseEntity.getBody(), InterswitchTransactionVerifyResponse.class);
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public Response<Boolean> validateAccount(String accountNumber, String bankCode, Map<String, String> properties, RestTemplate restTemplate) throws Status511InterswitchServiceException {
	    ResponseEntity<Object> validateAccountResponse = restTemplate.exchange(properties.get("name_enquiry_url"), HttpMethod.GET,
                buildValidationRequestHeaders(accountNumber, bankCode, properties),
                Object.class, new HashMap<>());
	    log.info("Validation response: " + validateAccountResponse);
	    if (!validateAccountResponse.getStatusCode().is2xxSuccessful()) {
		    return Response.<Boolean>builder().data(false).message("Invalid bank account number. Please check your bank name, NUBAN details and try again.").build();
	    }
	    boolean isValidAccount = validateAccountResponse.getBody().toString().contains("accountName");
	    return Response.<Boolean>builder().data(isValidAccount).message(validateAccountResponse.getBody().toString()).build();
    }

    private String printRequest(String apiUrl, String method, MultiValueMap<String, String> headers) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(System.lineSeparator() + method + " " + apiUrl + System.lineSeparator());
	    sb.append("Headers:" + System.lineSeparator());
	    for (String key : headers.keySet()) {
		    sb.append(key + " : " + headers.get(key) + System.lineSeparator());
	    }
	    return sb.toString();
    }

	private ProcessorAccount createProcessorAccount(String accountNumber, String bankCode, String accountName, DoProcessorRequestUser requestUser) {
		String bankDataDescriptor = new StringBuilder().append(bankCode).append("/").append(accountNumber).toString();
		return ProcessorAccount.builder()
				.reference(requestUser.getGuid()+"/"+bankDataDescriptor)
				.status(PaymentMethodStatusType.ACTIVE)
				.type(ProcessorAccountType.BANK)
				.descriptor(bankDataDescriptor)
				.name(accountName)
				.data(new HashMap<String, String>() {{
					put("account_number", accountNumber);
					put("bank_code", bankCode);
				}})
				.hideInDeposit(true)
		        .build();
	}

    public Response<BankAccountLookupResponse> bankAccountLookup(BankAccountLookupRequest bankAccountLookupRequest) throws Exception {
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
            String bankName = getBankName(bankAccountLookupRequest, domainMethodProcessorProperties, bankCode);
            AccountVerificationResponse accountVerificationResponse = getAccountVerificationResponse(domainMethodProcessorProperties, accountNumber, bankCode);
            String accountName = accountVerificationResponse.getAccountName();
            return Response.<BankAccountLookupResponse>builder().data(buildBankAccountLookupResponse(accountNumber, accountName, bankCode, bankName)).build();
        } catch (Status439BankAccountLookupClientException e) {
            log.error("Bank Account Lookup Failed: {}", e.getMessage());
            return Response.<BankAccountLookupResponse>builder().data(buildFailedBankAccountLookupResponse(e.getMessage())).build();
        }
    }

    private BankAccountLookupResponse buildFailedBankAccountLookupResponse(String failedStatusReasonMessage) {
        return BankAccountLookupResponse.builder().status("Failed").failedStatusReasonMessage(failedStatusReasonMessage).build();
    }

    private BankAccountLookupResponse buildBankAccountLookupResponse(String accountNumber, String accountName, String bankCode, String bankName) {
        return BankAccountLookupResponse.builder()
                .status("Success").accountNumber(accountNumber).accountName(accountName).bankCode(bankCode).bankName(bankName)
                .build();
    }

    private HttpEntity<MultiValueMap<String, String>> buildValidationRequestHeaders(String accountNumber, String bankCode, Map<String, String> properties)
            throws Status511InterswitchServiceException {
        MultiValueMap<String, String> headers;
        try {
            headers = getHeadersMap(properties.get("name_enquiry_url"), getTimestampInSecondsByProperties(properties), properties.get("client_id"),
                    properties.get("secret_key"), properties.get("header_signature_method"), properties.get("terminal_id"), true);
            headers.add("bankCode", bankCode);
            headers.add("accountId", accountNumber);
        } catch (Exception e) {
            throw new Status511InterswitchServiceException("Cant get properties for interswitch validate request build");
        }
        log.debug("Send validation request:" + printRequest(properties.get("name_enquiry_url"), HttpMethod.GET.name(), headers));
        return new HttpEntity<>(headers);
    }

    private AccountVerificationResponse getAccountVerificationResponse(Map<String, String> domainMethodProcessorProperties, String accountNumber, String bankCode) throws Status511InterswitchServiceException, Status439BankAccountLookupClientException, IOException {
        ResponseEntity<String> responseEntity = restTemplate.exchange(domainMethodProcessorProperties.get("name_enquiry_url"), HttpMethod.GET,
                buildValidationRequestHeaders(accountNumber, bankCode, domainMethodProcessorProperties),
                String.class, new HashMap<>());
        log.info("Validation response:" + responseEntity);
        if (!responseEntity.getStatusCode().is2xxSuccessful() || !responseEntity.getBody().contains("accountName")) {
            throw new Status439BankAccountLookupClientException("Invalid input data. Please check your bank name, NUBAN details and try again.");
        }
        return mapper.readValue(responseEntity.getBody(), AccountVerificationResponse.class);
    }

    private String getBankName(BankAccountLookupRequest bankAccountLookupRequest, Map<String, String> domainMethodProcessorProperties, String bankCode) throws Exception {
        return bankAccountLookupRequest.getBankName() != null
                ? bankAccountLookupRequest.getBankName()
                : getBankList(domainMethodProcessorProperties).stream()
                .filter(b -> b.getCbnCode().equals(bankCode))
                .findFirst()
                .map(Bank::getBankName)
                .orElseThrow(() -> new Status439BankAccountLookupClientException("'bank_code' is invalid"));
    }
}
