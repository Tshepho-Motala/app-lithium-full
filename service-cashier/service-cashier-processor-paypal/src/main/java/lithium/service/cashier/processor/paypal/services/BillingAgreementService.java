package lithium.service.cashier.processor.paypal.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.frontend.ProcessorAccountResponseStatus;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.paypal.api.Payer;
import lithium.service.cashier.processor.paypal.api.orders.OrderDetailsResponse;
import lithium.service.cashier.processor.paypal.api.payments.AgreementApiError;
import lithium.service.cashier.processor.paypal.api.payments.AgreementRequest;
import lithium.service.cashier.processor.paypal.api.payments.AgreementResponse;
import lithium.service.cashier.processor.paypal.api.payments.AgreementTokensRequest;
import lithium.service.cashier.processor.paypal.api.payments.AgreementTokensResponse;
import lithium.service.cashier.processor.paypal.api.payments.Plan;
import lithium.service.cashier.processor.paypal.api.webhook.WebhookRequest;
import lithium.service.cashier.processor.paypal.data.TokenContext;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureRequest;
import lithium.service.cashier.processor.paypal.exceptions.InvalidConfigurationException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalServiceHttpErrorException;
import lithium.service.cashier.processor.paypal.exceptions.Status999GeneralFailureException;
import lithium.util.ExceptionMessageUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class BillingAgreementService extends PayPalCommonService {

    @Autowired
    private CashierInternalClientService cashierService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LithiumConfigurationProperties lithiumProperties;
    @Autowired
    private VerificationService verificationService;

    public AgreementTokensResponse createAgreementTokens(String guid, String returnUrl, String cancelUrl,
                                                         String billingAgreement, String userName, String password,
                                                         String apiUrl, RestTemplate rest) throws Exception {

        AgreementTokensRequest tokensRequest = AgreementTokensRequest.builder()
                .description(billingAgreement)
                .payer(AgreementTokensRequest.Payer.builder().paymentMethod("PAYPAL").build())
                .plan(
                        Plan.builder()
                                .type("MERCHANT_INITIATED_BILLING")
                                .merchantPreferences(
                                        Plan.MerchantPreferences.builder()
                                                .returnUrl(returnUrl)
                                                .cancelUrl(cancelUrl)
                                                .acceptedPymtType("INSTANT")
                                                .skipShippingAddress(true)
                                                .build())
                                .build())
                .build();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        TokenContext context = TokenContext.builder()
                .username(userName)
                .password(password)
                .apiUrl(apiUrl)
                .id(guid)
                .build();
        headers.add("Authorization", "Bearer " + getToken(context, rest));
        headers.add("Content-type", "application/json");

        HttpEntity<AgreementTokensRequest> entity = new HttpEntity<>(tokensRequest, headers);

        log.info("PayPal agreement token request ("+ guid +"): " + entity);

        ResponseEntity<Object> exchange = rest.exchange(apiUrl + "/v1/billing-agreements/agreement-tokens", HttpMethod.POST, entity, Object.class, new HashMap<>());

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("PayPal agreement token failed ("+ guid +") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseAgreementApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.info("PayPal agreement token response ("+ guid +") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), AgreementTokensResponse.class);
    }

    public OrderDetailsResponse getOrder(String guid,
                                         String orderId, String userName, String password,
                                         String apiUrl, RestTemplate rest) throws Exception {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        TokenContext context = TokenContext.builder()
            .username(userName)
            .password(password)
            .apiUrl(apiUrl)
            .id(guid)
            .build();
        headers.add("Authorization", "Bearer " + getToken(context, rest));
        headers.add("Content-type", "application/json");

        HttpEntity entity = new HttpEntity(headers);
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("orderId", orderId);

        ResponseEntity<Object> exchange = rest.exchange(apiUrl + "/v2/checkout/orders/{orderId}", HttpMethod.GET, entity, Object.class, uriVariables);
        log.info("Details order response ("+ guid +") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get PayPal order  details failed (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseOrderApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        return mapper.convertValue(exchange.getBody(), OrderDetailsResponse.class);
    }

    public AgreementResponse createAgreement(String guid, String token, String userName, String password, String apiUrl,
                                             RestTemplate rest) throws Exception {

        AgreementRequest request = AgreementRequest.builder().tokenId(token).build();

        log.info("PayPal agreement request ("+ guid +"): " + request);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        TokenContext context = TokenContext.builder()
                .username(userName)
                .password(password)
                .apiUrl(apiUrl)
                .id(guid)
                .build();
        headers.add("Authorization", "Bearer " + getToken(context, rest));
        headers.add("Content-type", "application/json");

        HttpEntity<AgreementRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Object> exchange = rest.exchange(apiUrl + "/v1/billing-agreements/agreements", HttpMethod.POST, entity, Object.class, new HashMap<>());

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("PayPal agreement failed ("+ guid +") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseAgreementApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.info("PayPal agreement response ("+ guid +") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), AgreementResponse.class);
    }

    private String parseAgreementApiError(ResponseEntity<Object> exchange) {
        try {
            if (nonNull(exchange.getBody())) {
                AgreementApiError agreementApiError = mapper.convertValue(exchange.getBody(), AgreementApiError.class);
                if (nonNull(agreementApiError) && nonNull(agreementApiError.getName())) {
                    return "code: " + agreementApiError.getName()+ ", message: " + agreementApiError.getDetails().stream()
                            .map(AgreementApiError.Details::getMessage).collect(Collectors.joining("; "));
                }
            }
        } catch (Exception e) {
            log.warn("Can't parse error message: " + exchange);
        }
        return "/v1/billing-agreements/**, http error code: " + exchange.getStatusCodeValue() + ", message: " + exchange.getStatusCode().name();
    }

    public String getAgreementTokenId(String guid, String domainName) throws Exception {

        Map<String, String> properties = getPropertiesDMPFromServiceCashier(guid, domainName, true);
        String returnUrl = buildUrl("return", guid);
        String cancelUrl = buildUrl("cancel", guid);

        AgreementTokensResponse agreementTokens = createAgreementTokens(guid, returnUrl, cancelUrl,
            property(properties, "billing_agreement_description"), property(properties, "username_ba"),
            property(properties, "password_ba"), property(properties, "api_url"), restTemplate);

        return agreementTokens.getTokenId();
    }

    public ProcessorAccountResponse createBillingAgreement(String guid, AccountProcessorRequest request, String baToken, String payerId, String orderId) {
        ProcessorAccountResponse response = ProcessorAccountResponse.builder().build();
        try {
            Map<String, String> properties = request.getProperties();
            String usernameBa = property(properties, "username_ba");
            String passwordBa = property(properties, "password_ba");
            String apiUrl = property(properties, "api_url");

            OrderDetailsResponse orderDetailsResponse = getOrder(guid, orderId, usernameBa, passwordBa, apiUrl, restTemplate);
            if (orderDetailsResponse.getPayer() == null) {
                throw new Exception("Failed to get payer for order (" + orderDetailsResponse.getId() + ")");
            }

            VerifyProcessorAccountResponse verifyResponse = verifyProcessorAccount(request, response, orderDetailsResponse.getPayer());
            if (BooleanUtils.isFalse(verifyResponse.getResult())) {
                response.setProcessorAccount(verifyResponse.getProcessorAccount());
                response.setStatus(ProcessorAccountResponseStatus.FAILED);
                return response;
            }
            ProcessorAccount processorAccount = verifyResponse.getProcessorAccount();

            AgreementResponse agreement = createAgreement(guid, baToken, usernameBa,
                passwordBa, apiUrl, restTemplate);

            if ("ACTIVE".equals(agreement.getState())) {
                if (!agreement.getId().equals(processorAccount.getProviderData())) {
                    log.warn("Provider data (" + processorAccount.getId() + ", " + processorAccount.getProviderData() + ") overridden with new billing agreement (" + agreement.getId() + ")");
                }
                processorAccount.setProviderData(agreement.getId());
                response.setProcessorAccount(processorAccount);
                response.setStatus(ProcessorAccountResponseStatus.SUCCESS);
                response.setProcessorReference(agreement.getId());
            } else {
                log.error("Failed create PayPal billing agreement (" + guid + ", " + baToken + ") due unexpected state: " + agreement.getState());
                response.setStatus(ProcessorAccountResponseStatus.FAILED);
                response.setErrorMessage("Failed create PayPal billing agreement due unexpected state: " + agreement.getState());
                response.setErrorCode("400");
                response.setGeneralError(GeneralError.FAILED_TO_ADD_BILLING_AGREEMENT.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            }
        } catch (Exception e) {
            log.error("Failed create PayPal billing agreement (" + guid + ", " + baToken + ") due " + e.getMessage(), e);
            response.setStatus(ProcessorAccountResponseStatus.FAILED);
            response.setErrorMessage(e.getMessage());
            response.setGeneralError(GeneralError.FAILED_TO_ADD_BILLING_AGREEMENT.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
            if (e instanceof PayPalServiceHttpErrorException) {
                int httpCode = ((PayPalServiceHttpErrorException) e).getHttpCode();
                response.setErrorCode(String.valueOf(httpCode));
            } else {
                response.setErrorCode("500");
            }

        }
        return response;
    }

    public void handleCancelBillingAgreementWebhook(WebhookRequest webhook, VerifyWebhookSignatureRequest signedWebhook, String domainName) throws Exception {
        AgreementResponse resource = mapper.convertValue(webhook.getResource(), AgreementResponse.class);
        String payerId = resource.getPayer().getPayerInfo().getPayerId();
        log.info("Handling canceled billing agreement webhook (" + payerId + ")...");

        verificationService.verifyWebhookSignature(signedWebhook, domainName, true, true);

        List<ProcessorAccount> processorAccounts = cashierService.getProcessorAccounts(domainName, payerId, ProcessorAccountType.PAYPAL.getName());
        if (processorAccounts.isEmpty()) {
            log.warn("Can't found any processor account with reference: " + payerId);
        }
        for (ProcessorAccount processorAccount : processorAccounts) {
            if (!resource.getId().equals(processorAccount.getProviderData())) {
                log.warn("Provider data (" + processorAccount.getProviderData() + ") is not equal to canceled billing agreement (" + resource.getId() + "). ProcessorAccountId: " + processorAccount.getId() + ", PayerId: " + payerId);
            }
            processorAccount.setProviderData("");
            processorAccount.setHideInDeposit(true);
            cashierService.updateProcessorAccount(processorAccount);
            log.info("Processor account updated: " + processorAccount);
        }
    }

    private VerifyProcessorAccountResponse verifyProcessorAccount(AccountProcessorRequest request, ProcessorAccountResponse response, Payer payer) throws Exception {
        VerifyProcessorAccountRequest verifyRequest = VerifyProcessorAccountRequest.builder()
            .processorAccount(ProcessorAccount.builder()
                .reference(payer.getPayerId())
                .status(PaymentMethodStatusType.ACTIVE)
                .type(ProcessorAccountType.PAYPAL)
                .descriptor(payer.getPayerId())
                .name(payer.getName().getGivenName() + " " + payer.getName().getSurname())
                .hideInDeposit(false)
                .data(new HashMap<String, String>() {{
                    put("payer-email", payer.getEmailAddress());
                    put("payer-firstName", payer.getName().getGivenName());
                    put("payer-lastName", payer.getName().getSurname());
                    put("payer-payerId", payer.getPayerId());
                }})
                .build())
            .verifications(getAccountVerifications(request))
            .update(true)
            .userGuid(request.getUser().getRealGuid())
            .build();

        return cashierService.verifyAccount(verifyRequest);
    }

    private Map<String, String> getPropertiesDMPFromServiceCashier(String guid, String domainName, boolean deposit) throws Status999GeneralFailureException {
        try {
            DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                domainName, deposit, "paypal", "paypal");

            log.debug("Received properties processor config (" + guid + "): " + dmp);
            return dmp.getProperties();
        } catch (Exception e) {
            log.error("Error trying to call cashier client (" + guid + "): " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status999GeneralFailureException(ExceptionMessageUtil.allMessages(e), e);
        }
    }

    private String property(Map<String, String> properties, String key) throws InvalidConfigurationException {
        return ofNullable(properties.get(key))
            .orElseThrow(() -> new InvalidConfigurationException("Property '" + key + "' not found"));
    }

    private String buildUrl(String action, String guid) {
        return lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-paypal/public/callback/ba/"
            + guid + "/" + action;
    }

    private List<ProcessorAccountVerificationType> getAccountVerifications(AccountProcessorRequest request) throws Exception {
        String accountVerifications = request.getProperties().get("account_verifications");
        if (StringUtil.isEmpty(accountVerifications)) {
            return Collections.emptyList();
        }
        return Arrays.asList(accountVerifications.split("\\s*,\\s*")).stream().map(ProcessorAccountVerificationType::fromName).collect(Collectors.toList());
    }
}
