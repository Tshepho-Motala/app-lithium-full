package lithium.service.cashier.processor.paypal.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.processor.paypal.api.Amount;
import lithium.service.cashier.processor.paypal.api.orders.CapturePaymentResponse;
import lithium.service.cashier.processor.paypal.api.orders.CapturePaymentWithBillingAgreementRequest;
import lithium.service.cashier.processor.paypal.api.orders.CreateOrderRequest;
import lithium.service.cashier.processor.paypal.api.orders.OrderApiError;
import lithium.service.cashier.processor.paypal.api.orders.OrderDetailsResponse;
import lithium.service.cashier.processor.paypal.api.orders.OrderResponse;
import lithium.service.cashier.processor.paypal.exceptions.PayPalCaptureOrderException;
import lithium.service.cashier.processor.paypal.exceptions.PayPalServiceHttpErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Service
@Slf4j
public class OrderService extends PayPalCommonService {
    @Autowired
    private LithiumConfigurationProperties lithiumProperties;

    private final static List<String> ISSUES_FOR_DECLINE = Arrays.asList("TRANSACTION_REFUSED", "INSTRUMENT_DECLINED",
            "COMPLIANCE_VIOLATION", "TRANSACTION_LIMIT_EXCEEDED", "PAYER_CANNOT_PAY", "INVALID_RESOURCE_ID", "AGREEMENT_ALREADY_CANCELLED",
            "REDIRECT_PAYER_FOR_ALTERNATE_FUNDING");

    public OrderResponse createOrder(DoProcessorRequest request, DoProcessorResponse response, boolean depositUsingBA, RestTemplate rest) throws Exception {

        Long transactionId = request.getTransactionId();
        CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .intent("CAPTURE")
                .purchaseUnits(Collections.singletonList(
                        CreateOrderRequest.PurchaseUnit.builder()
                                .referenceId(transactionId.toString())
                                .description(request.getProperty("purchase_description"))
                                .amount(Amount.builder()
                                        .currencyCode(request.getProperty("currency_code"))
                                        .value(request.inputAmount())
                                        .build())
                                .invoiceId(request.getTransactionId())
                                .build()))
                .applicationContext(
                        CreateOrderRequest.ApplicationContext.builder()
                                .brandName(request.getProperty("brand_name"))
                                .landingPage("NO_PREFERENCE")
                                .shippingPreference("NO_SHIPPING")
                                .userAction("PAY_NOW")
                                .returnUrl(buildUrl("return", request.getTransactionId()))
                                .cancelUrl(buildUrl("cancel", request.getTransactionId()))
                                .build())
                .build();

        log.debug("Initiate PayPal request (" + transactionId + "): " + orderRequest);
        response.addRawRequestLog("Create order request: " + objectToPrettyString(orderRequest));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + buildTokenContextFromRequest(request, response, depositUsingBA, rest));
        headers.add("Content-type", "application/json");

        applyCmidIfRequired(request, response, headers);

        HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<Object> exchange = rest.exchange(request.getProperty("api_url") + "/v2/checkout/orders", HttpMethod.POST, entity, Object.class, new HashMap<>());
        response.addRawResponseLog("Create order response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Initiate PayPal order failed (" + transactionId + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseOrderApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.debug("Initiate PayPal response (" + transactionId + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), OrderResponse.class);
    }

    private void applyCmidIfRequired(DoProcessorRequest request, DoProcessorResponse response, MultiValueMap<String, String> headers) {
        if (nonNull(request.getProcessorAccount())) {
            applyCmid(request, response, headers);
        }
    }

    private String buildUrl(String action, Long transactionId) {
        return lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-paypal/public/callback/order/"
                + transactionId + "/" + action;
    }

    public OrderDetailsResponse getOrder(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) throws Exception {

        Long transactionId = request.getTransactionId();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + buildTokenContextFromRequest(request, response, nonNull(request.getProcessorAccount()), rest));
        headers.add("Content-type", "application/json");

        HttpEntity entity = new HttpEntity(headers);
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("orderId", request.getProcessorReference());

        ResponseEntity<Object> exchange = rest.exchange(request.getProperty("api_url") + "/v2/checkout/orders/{orderId}", HttpMethod.GET, entity, Object.class, uriVariables);
        response.addRawResponseLog("Details order response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get PayPal order  details failed (" + transactionId + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseOrderApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.debug("PayPal order details response (" + transactionId + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), OrderDetailsResponse.class);
    }

    public CapturePaymentResponse capturePayment(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest, String billingAgreementId) throws Exception {
        boolean usingBA = nonNull(billingAgreementId);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + buildTokenContextFromRequest(request, response, usingBA, rest));
        headers.add("Content-type", "application/json");
        headers.add("PayPal-Request-Id", request.getTransactionId().toString());
        applyCmidIfRequired(request, response, headers);

        CapturePaymentWithBillingAgreementRequest paymentRequest = null;
        if (usingBA) {
            paymentRequest = CapturePaymentWithBillingAgreementRequest.builder()
                    .paymentSource(CapturePaymentWithBillingAgreementRequest.PaymentSource.builder()
                            .token(CapturePaymentWithBillingAgreementRequest.PaymentSource.Token.builder()
                                    .id(billingAgreementId)
                                    .type("BILLING_AGREEMENT")
                                    .build())
                            .build())
                    .build();
        }

        HttpEntity<CapturePaymentWithBillingAgreementRequest> entity = new HttpEntity<>(paymentRequest, headers);

        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("orderId", request.getProcessorReference());
        log.debug("Capture payment PayPal request(" + request.getTransactionId() + "): " + paymentRequest);
        ResponseEntity<Object> exchange = rest.exchange(request.getProperty("api_url") + "/v2/checkout/orders/{orderId}/capture", HttpMethod.POST, entity, Object.class, uriVariables);
        response.addRawResponseLog("Capture payment PayPal response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Capture payment PayPal order failed (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());

            OrderApiError orderApiError = resolveOrderApiError(exchange);
            if (nonNull(orderApiError) && nonNull(orderApiError.getDetails())) {
                String issue = orderApiError.getDetails().stream()
                        .filter(details -> ISSUES_FOR_DECLINE.contains(details.getIssue()))
                        .findFirst()
                        .map(OrderApiError.Details::getIssue)
                        .orElse(null);
                if (nonNull(issue)) {
                    throw new PayPalCaptureOrderException(issue);
                }
            }
            throw new PayPalServiceHttpErrorException(createErrorMessage(orderApiError, exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.debug("Capture payment PayPal response (" + request.getTransactionId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        return mapper.convertValue(exchange.getBody(), CapturePaymentResponse.class);
    }




}
