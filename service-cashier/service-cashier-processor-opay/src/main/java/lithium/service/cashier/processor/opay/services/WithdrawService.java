package lithium.service.cashier.processor.opay.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.cashier.client.exceptions.Status439BankAccountLookupClientException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.opay.api.v2.schema.transfer.StatusToWalletRequest;
import lithium.service.cashier.processor.opay.api.v2.schema.transfer.ToWalletRequest;
import lithium.service.cashier.processor.opay.api.v2.schema.transfer.ToWalletResponse;
import lithium.service.cashier.processor.opay.api.v2.schema.validate.OpayUserRequest;
import lithium.service.cashier.processor.opay.api.v2.schema.validate.OpayUserResponse;
import lithium.service.cashier.processor.opay.context.V3Context;
import lithium.service.cashier.processor.opay.exceptions.Status900InvalidSignatureException;
import lithium.service.cashier.processor.opay.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.opay.exceptions.Status908OpayServiceError;
import lithium.service.cashier.processor.opay.exceptions.Status909UnhandledTransactionCodeError;
import lithium.service.cashier.processor.opay.exceptions.Status999GeneralFailureException;
import lithium.util.ExceptionMessageUtil;
import lithium.util.Hash;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;
import static lithium.util.ObjectToFormattedText.httpEntityToPrettyString;

@Service
@Slf4j
@AllArgsConstructor
public class WithdrawService extends BaseOpayService {
    private final CashierInternalClientService cashierService;
    private final ObjectMapper mapper;

    @TimeThisMethod
    public Response<Boolean> validateUser(String phoneNumber, String domainName, RestTemplate restTemplate) throws Exception {
        OpayUserResponse opayUser = validateOpayUser(phoneNumber, buildV3Context(getMethodProcessor(domainName).getProperties()), restTemplate);
        if (!isOpayUserMessageSuccessful(opayUser.getMessage())) {
            log.warn("User invalid due " + opayUser.getMessage());
            return Response.<Boolean>builder().data(false).message("Incorrect Opay Mobile number. Check your OPay phone number and try again.").build();
        }
        OpayUserResponse.User opayData = opayUser.getData();
        Map<String, Map<String, String>> map = new HashMap<>();
        Map<String, String> stage2Map = new HashMap<>();
        stage2Map.put("full_name", opayData.getFirstName() + " " + opayData.getLastName());
        stage2Map.put("user_id", opayData.getUserId());
        map.put("2", stage2Map);
        return Response.<Boolean>builder().data(true).data2(map).message("Opay account validated").build();
    }

    private V3Context buildV3Context(Map<String, String> properties) {
        V3Context context = V3Context.builder()
                .url(properties.get("base_v3_url"))
                .publicKey(properties.get("public_key"))
                .merchantId(properties.get("merchant_id"))
                .build();
        return context;
    }

    public Response<BankAccountLookupResponse> bankAccountLookup(BankAccountLookupRequest bankAccountLookupRequest, RestTemplate restTemplate) throws Exception {
        Map<String, String> domainMethodProcessorProperties = bankAccountLookupRequest.getDomainMethodProcessorProperties();
        try {
            if (bankAccountLookupRequest.getAccountNumber() == null) {
                throw new Status439BankAccountLookupClientException("Missing 'account_number' in withdraw request");
            }
            String accountNumber = bankAccountLookupRequest.getAccountNumber();
            OpayUserResponse opayUser = validateOpayUser(accountNumber, buildV3Context(domainMethodProcessorProperties), restTemplate);
            if (!isOpayUserMessageSuccessful(opayUser.getMessage())) {
                throw new Status439BankAccountLookupClientException("Incorrect Opay Mobile number. Check your OPay phone number and try again.");
            }
            String accountName = opayUser.getData().getFirstName() + " " + opayUser.getData().getLastName();
            return Response.<BankAccountLookupResponse>builder().data(buildBankAccountLookupResponse(accountNumber, accountName, opayUser)).build();
        } catch (Status439BankAccountLookupClientException e) {
            log.error("Bank Account Lookup Failed: {}", e.getMessage());
            return Response.<BankAccountLookupResponse>builder().data(buildFailedBankAccountLookupResponse(e.getMessage())).build();
        }
    }

    private BankAccountLookupResponse buildFailedBankAccountLookupResponse(String failedStatusReasonMessage) {
        return BankAccountLookupResponse.builder().status("Failed").failedStatusReasonMessage(failedStatusReasonMessage).build();
    }

    private BankAccountLookupResponse buildBankAccountLookupResponse(String accountNumber, String accountName, OpayUserResponse opayUser) {
        return BankAccountLookupResponse.builder()
                .status("Success").accountNumber(accountNumber).accountName(accountName)
                .message(opayUser.getMessage()).code(opayUser.getCode())
                .userId(opayUser.getData().getUserId()).address(opayUser.getData().getAddress())
                .email(opayUser.getData().getEmail()).phoneNumber(opayUser.getData().getPhoneNumber())
                .firstName(opayUser.getData().getFirstName()).lastName(opayUser.getData().getLastName()).build();
    }

    private boolean isOpayUserMessageSuccessful(String opayUserMessage) {
        return "SUCCESSFUL".equals(opayUserMessage);
    }

    @TimeThisMethod
    public ResponseEntity<DoProcessorResponseStatus> withdrawV3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            String phoneNumber = inputData("account_number", request);
            ToWalletRequest toWalletRequest = ToWalletRequest.builder()
                    .amount(String.valueOf(inputCents(request)))
//                    .country(request.getProperty("country_code"))
                    .currency(request.getUser().getCurrency())
                    .reason("Withdrawal " + request.getUser().getGuid())
                    .receiver(ToWalletRequest.Reciever.builder()
//                            .name(request.getUser().getFullName())
                            .type("USER")
                            .phoneNumber(preparePhoneNumber(phoneNumber))
                            .build())
                    .reference(request.getTransactionId().toString())
                    .build();

            response.addRawRequestLog("Initiate withdraw V3 request: " + objectToPrettyString(toWalletRequest));

            ToWalletResponse toWalletResponse = transferToWallet(toWalletRequest, request, rest, response);

            response.addRawResponseLog("Initiate withdraw V3 response: " + objectToPrettyString(toWalletResponse));
            if (toWalletResponse.getData() != null) {
                response.setProcessorReference(toWalletResponse.getData().getOrderNo());
            }
            DoProcessorResponseStatus status = resolveStatusOnStage1(response, toWalletResponse);
            response.setStatus(status);

            if (DoProcessorResponseStatus.DECLINED.equals(status)) {
                response.setDeclineReason("Declined by Opay: " + toWalletResponse.getMessage());
            }

            return ResponseEntity.status(status.getCode()).body(status);
        } catch (Status901InvalidOrMissingParameters | Status900InvalidSignatureException e) {
            log.error("Withdraw V3 initiate error:" + e.getMessage() + ". Transaction(" + request.getTransactionId() + ") declined");
            response.addRawResponseLog("Withdraw V3 initiate error:" + e.getMessage() + ". Transaction declined");
            response.setDeclineReason("Wrong configuration");
            response.setStatus(DoProcessorResponseStatus.DECLINED);
            return ResponseEntity.status(DoProcessorResponseStatus.DECLINED.getCode()).body(DoProcessorResponseStatus.DECLINED);
        } catch (Exception e) {
            log.error("Withdraw V3 initiate error:" + e.getMessage() + ". For resolve transaction(" + request.getTransactionId() + ") status moved to NEXTSTAGE_NOPROCESS state");
            response.addRawResponseLog("Withdraw V3 initiate error:" + e.getMessage() + ". For resolve status moved to NEXTSTAGE_NOPROCESS state. \n"
                    + "To complete transaction it should to check state on Opay side and do final decision manually"
                    + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setStatus(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
            return ResponseEntity.status(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS.getCode()).body(DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS);
        }

    }

    private String preparePhoneNumber(String phoneNumber) throws Status901InvalidOrMissingParameters {
        if (phoneNumber.length() < 10) {
            log.error("Wrong phone number: " + phoneNumber);
            throw new Status901InvalidOrMissingParameters("Wrong phone number: " + phoneNumber);
        }
        return "+234" + phoneNumber.substring(phoneNumber.length() - 10);
    }

    private DoProcessorResponseStatus resolveStatusOnStage1(DoProcessorResponse response, ToWalletResponse toWalletResponse) throws Status909UnhandledTransactionCodeError {
        if (toWalletResponse.getData() == null) {
            String errorCode = toWalletResponse.getCode();
            String error = errorCode + " " + toWalletResponse.getMessage();
            if ("50001".equals(errorCode)) { // Service not available, please try again. [A.T.I.50001]
                log.warn("Got statusCode: " + error + ", transaction(" + response.getTransactionId() + "). For resolve status moved to NEXTSTAGE_NOPROCESS state");
                response.addRawResponseLog(error + ". For resolve status moved to NEXTSTAGE_NOPROCESS state\n."
                        + "To complete transaction it should to check state on Opay side and do final decision manually");
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else if ("04057".equals(errorCode)) { // T.TT.I.04999 order existed
                log.warn("Got statusCode: " + error + ", transaction(" + response.getTransactionId() + ") For resolve status moved to NEXTSTAGE_NOPROCESS_WITH_RETRY state");
                response.addRawResponseLog(error + ". For resolve status moved to NEXTSTAGE_NOPROCESS_WITH_RETRY state");
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS_WITH_RETRY;
            }
            log.warn("Unhandled statusCode: " + error + ", transaction(" + response.getTransactionId() + ")");
            throw new Status909UnhandledTransactionCodeError(error);
        }

        String status = toWalletResponse.getData().getStatus();
        if ("FAILED".equals(status) || "CLOSED".equals(status)) {
            return DoProcessorResponseStatus.DECLINED;
        }
        return DoProcessorResponseStatus.NEXTSTAGE;
    }

    private ProcessorAccount createProcessorAccount(String phoneNumber, String accountName, String userId) {
        return ProcessorAccount.builder()
                .reference(userId + "/" + phoneNumber)
                .status(PaymentMethodStatusType.ACTIVE)
                .type(ProcessorAccountType.BANK)
                .descriptor(phoneNumber)
                .name(accountName)
                .hideInDeposit(true)
                .build();
    }

    private DoProcessorResponseStatus resolveStatusOnStage2(DoProcessorResponse response, ToWalletResponse toWalletResponse) {
        if (toWalletResponse.getData() == null) {
            String errorCode = toWalletResponse.getCode();
            String error = errorCode + " " + toWalletResponse.getMessage();
            if ("50001".equals(errorCode)) { // Service not available, please try again. [A.T.I.50001]
                log.warn("Got statusCode: " + error + ", transaction(" + response.getTransactionId() + "). For rechecking status later moved to PENDING_AUTO_RETRY state");
                return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
            }
            log.warn("Unhandled statusCode: " + error + ", transaction(" + response.getTransactionId() + ") moved to FATALERROR state");
            response.addRawResponseLog("Unhandled statusCode: " + error);
            return DoProcessorResponseStatus.FATALERROR;
        }

        String status = toWalletResponse.getData().getStatus();
        if ("SUCCESSFUL".equals(status)) {
            return DoProcessorResponseStatus.SUCCESS;
        }
        if ("FAILED".equals(status) || "CLOSED".equals(status)) {
            return DoProcessorResponseStatus.DECLINED;
        }
        return DoProcessorResponseStatus.PENDING_AUTO_RETRY;
    }

    @TimeThisMethod
    public ResponseEntity<DoProcessorResponseStatus> verifyWithdrawV3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            StatusToWalletRequest statusToWalletRequest = StatusToWalletRequest.builder()
                    .orderNo(request.getProcessorReference())
                    .reference(request.getTransactionId().toString())
                    .build();

            response.addRawRequestLog("Verify status withdraw V3 request: " + objectToPrettyString(statusToWalletRequest));

            ToWalletResponse toWalletResponse = transferStatusToWallet(statusToWalletRequest, request, rest, response);

            ToWalletResponse.TransactionData walletResponseData = toWalletResponse.getData();

            DoProcessorResponseStatus status = resolveStatusOnStage2(response, toWalletResponse);
            response.setStatus(status);

            if (walletResponseData != null) {
                response.setProcessorReference(toWalletResponse.getData().getOrderNo());
                if (status == DoProcessorResponseStatus.SUCCESS) {
                    response.setAmountCentsReceived(CurrencyAmount.fromCentsString(toWalletResponse.getData().getAmount()).toAmount().intValue());
                }
            }

            if (DoProcessorResponseStatus.DECLINED.equals(status)) {
                response.setDeclineReason("Declined by Opay: " + toWalletResponse.getMessage());
            }

            if (DoProcessorResponseStatus.SUCCESS.equals(status)) {
                String fullName = request.getInputData().get(1).get("full_name");
                String userId = request.getInputData().get(1).get("user_id");
                String phoneNumber = request.getInputData().get(1).get("account_number");
                if (nonNull(userId) && nonNull(phoneNumber) && nonNull(fullName)) {
                    response.setProcessorAccount(createProcessorAccount(phoneNumber, fullName, userId));
                }
            }

            return ResponseEntity.status(status.getCode()).body(status);

        } catch (Exception e) {
            log.error("Withdraw V3 verify status error:" + e.getMessage());
            response.addRawResponseLog("Withdraw V3 verify status error:" + e.getMessage() + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(e));
            response.setStatus(DoProcessorResponseStatus.PENDING_AUTO_RETRY);
            return ResponseEntity.status(DoProcessorResponseStatus.PENDING_AUTO_RETRY.getCode()).body(DoProcessorResponseStatus.PENDING_AUTO_RETRY);
        }
    }

    public ToWalletResponse transferToWallet(ToWalletRequest request, DoProcessorRequest processorRequest, RestTemplate rest, DoProcessorResponse response) throws Status900InvalidSignatureException, JsonProcessingException, Status908OpayServiceError, Status901InvalidOrMissingParameters {
        log.info("Initiate V3 withdraw request: " + request);
        String body = mapper.writeValueAsString(request);
        LinkedMultiValueMap<String, String> headers = prepareHeaders(processorRequest, body);

        ResponseEntity<Object> exchange = rest.exchange(baseUrl(processorRequest) + "transfer/toWallet", HttpMethod.POST, new HttpEntity<>(request, headers), Object.class);
        response.addRawResponseLog("Initiate withdraw V3 response: " + httpEntityToPrettyString(exchange));

        log.info("Initiate V3 withdraw (" + request.getReference() + ") response(" + exchange.getStatusCodeValue() + "): " + exchange.getBody());
        ToWalletResponse toWalletResponse = mapper.convertValue(exchange.getBody(), ToWalletResponse.class);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.warn("Invalid response status for initiate V3 withdraw (" + request.getReference() + "): " + exchange.getStatusCode());
            throw new Status908OpayServiceError("OPay service error: (" + exchange.getStatusCodeValue() + ") " + toWalletResponse.getMessage());
        }

        return toWalletResponse;
    }

    private LinkedMultiValueMap<String, String> prepareHeaders(DoProcessorRequest request, String body) throws Status900InvalidSignatureException {
        try {
            return new LinkedMultiValueMap<String, String>() {{
                add("Content-Type", "application/json");
                add("Authorization", "Bearer SHA256withRSA." + Hash.builder(rsaPrivate(request), body).sha256WithRSA());
                add("MerchantID", merchantId(request));
            }};
        } catch (Exception e) {
            throw new Status900InvalidSignatureException(e.getMessage());
        }
    }

    public ToWalletResponse transferStatusToWallet(StatusToWalletRequest request, DoProcessorRequest processorRequest, RestTemplate rest, DoProcessorResponse response) throws JsonProcessingException, Status900InvalidSignatureException, Status901InvalidOrMissingParameters, Status908OpayServiceError {
        log.info("Verify status V3 withdraw request: " + request);

        String body = mapper.writeValueAsString(request);
        LinkedMultiValueMap<String, String> headers = prepareHeaders(processorRequest, body);

        ResponseEntity<Object> exchange = rest.exchange(baseUrl(processorRequest) + "transfer/status/toWallet", HttpMethod.POST, new HttpEntity<>(request, headers), Object.class);
        response.addRawResponseLog("Verify status withdraw V3 response: " + httpEntityToPrettyString(exchange));

        log.info("Verify status V3 withdraw (" + request.getReference() + ") response(" + exchange.getStatusCodeValue() + "): " + exchange.getBody());
        ToWalletResponse toWalletResponse = mapper.convertValue(exchange.getBody(), ToWalletResponse.class);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.warn("Invalid response status for Verify status V3 withdraw (" + request.getReference() + "): " + exchange.getStatusCode());
            throw new Status908OpayServiceError("OPay service error: (" + exchange.getStatusCodeValue() + ") " + toWalletResponse.getMessage());
        }

        if (isNull(toWalletResponse.getData())) {
            log.warn("Invalid response data for Verify status V3 withdraw (" + request.getReference() + "): " + toWalletResponse);
            throw new Status908OpayServiceError("OPay service error: (" + toWalletResponse.getCode() + ") " + toWalletResponse.getMessage());
        }

        return toWalletResponse;
    }

    private OpayUserResponse validateOpayUser(String phoneNumber, V3Context context, RestTemplate restTemplate) throws Status908OpayServiceError {
        log.info("Validate request: " + phoneNumber + ", " + context);

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Content-Type", "application/json");
            add("Authorization", "Bearer " + context.getPublicKey());
            add("MerchantID", context.getMerchantId());
        }};

        ResponseEntity<Object> exchange = restTemplate.exchange(context.getUrl() + "info/user", HttpMethod.POST, new HttpEntity<>(new OpayUserRequest(phoneNumber), headers), Object.class);
        log.info("Validate response: " + exchange.getBody());
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.warn("Invalid response status for validate Opay user (" + phoneNumber + "): " + exchange.getStatusCode());
            throw new Status908OpayServiceError("Invalid response status for validate Opay user: " + exchange.getStatusCode());
        }
        return mapper.convertValue(exchange.getBody(), OpayUserResponse.class);
    }

    public DomainMethodProcessor getMethodProcessor(String domainName) throws Status999GeneralFailureException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(domainName, false, "opay", "opay");

            log.debug("Received properties processor config: " + dmp);
            if (dmp.getProperties().size() == 0) {
                log.warn("Invalid processor configuration");
                throw new Status999GeneralFailureException("Invalid processor configuration");
            }
            return dmp;
        } catch (Exception e) {
            log.error("Error trying to call cashier client: " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status999GeneralFailureException(ExceptionMessageUtil.allMessages(e));
        }
    }


}
