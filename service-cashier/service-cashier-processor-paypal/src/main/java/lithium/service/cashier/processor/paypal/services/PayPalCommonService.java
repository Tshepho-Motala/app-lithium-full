package lithium.service.cashier.processor.paypal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.processor.paypal.api.AuthApiError;
import lithium.service.cashier.processor.paypal.api.orders.OrderApiError;
import lithium.service.cashier.processor.paypal.api.orders.TokenResponse;
import lithium.service.cashier.processor.paypal.data.TokenContext;
import lithium.service.cashier.processor.paypal.exceptions.PayPalServiceHttpErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Service
@Slf4j
public abstract class PayPalCommonService {
    @Autowired
    protected ObjectMapper mapper;

    protected String getToken(TokenContext tokenContext, RestTemplate rest) throws Exception {
        String auth =  tokenContext.getUsername() + ":" + tokenContext.getPassword();
        String token = new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Object> exchange = rest.exchange(tokenContext.getApiUrl() + "/v1/oauth2/token", HttpMethod.POST, entity, Object.class);
        tokenContext.setResponseLog("PayPal token response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + objectToPrettyString(exchange.getBody()));

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get PayPal token failed (" + tokenContext.getId() + ") (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
            throw new PayPalServiceHttpErrorException(parseAuthApiError(exchange), exchange.getBody().toString(), exchange.getStatusCodeValue());
        }
        log.info("PayPal token response (DebugId: " + exchange.getHeaders().get("Paypal-Debug-Id") + "): " + exchange.getBody());
        TokenResponse tokenResponse = mapper.convertValue(exchange.getBody(), TokenResponse.class);
        return tokenResponse.getAccessToken();
    }

    private String parseAuthApiError(ResponseEntity<Object> exchange) {
        try {
            if (nonNull(exchange.getBody())) {
                AuthApiError authApiError = mapper.convertValue(exchange.getBody(), AuthApiError.class);
                if (nonNull(authApiError) && nonNull(authApiError.getErrorDescription())) {
                    return authApiError.getErrorDescription();
                }
            }
        } catch (Exception e) {
            log.warn("Can't parse error message: " + exchange);
        }
        return "/v1/oauth2/**, http error code: " + exchange.getStatusCodeValue() + ", message: " + exchange.getStatusCode().name();
    }


    protected String buildTokenContextFromRequest(DoProcessorRequest request, DoProcessorResponse response, boolean depositUsingBA, RestTemplate restTemplate) throws Exception {
        TokenContext context = TokenContext.builder()
                .username(request.getProperty(depositUsingBA ? "username_ba" : "username"))
                .password(request.getProperty(depositUsingBA ? "password_ba" : "password"))
                .apiUrl(request.getProperty("api_url"))
                .id(String.valueOf(request.getTransactionId()))
                .build();

        String token = getToken(context, restTemplate);
        response.addRawResponseLog(context.getResponseLog());
        return token;
    }

    protected void applyCmid(DoProcessorRequest request, DoProcessorResponse response, MultiValueMap<String, String> headers) {
        try {
            String paypalClientMetadataId = request.stageInputData(1, "correlationId");
            headers.add("PayPal-Client-Metadata-Id", paypalClientMetadataId);
        } catch (Exception e) {
            log.error("Fraudnet failed(" + request.getTransactionId() +"). Missing CMID, " + e.getMessage());
            response.addRawRequestLog("Fraudnet failed. Missing CMID, " + e.getMessage());
        }
    }

    protected String createErrorMessage(OrderApiError orderApiError, ResponseEntity<Object> exchange) {
        try {
            if (nonNull(orderApiError) && nonNull(orderApiError.getName())) {
                return "code: " + orderApiError.getDetails().stream().map(details -> details.getIssue() + ", message: " + details.getDescription())
                    .collect(Collectors.joining("; "));
            }
        } catch (Exception e) {
            log.warn("Can't parse error message: " + orderApiError);
        }
        return "/v2/checkout/orders/**, http error code: " + exchange.getStatusCodeValue() + ", message: " + exchange.getStatusCode().name();
    }

    protected String parseOrderApiError(ResponseEntity<Object> exchange) {
        return createErrorMessage(resolveOrderApiError(exchange), exchange);
    }

    protected OrderApiError resolveOrderApiError(ResponseEntity<Object> exchange) {
        try {
            if (nonNull(exchange.getBody())) {
                return mapper.convertValue(exchange.getBody(), OrderApiError.class);
            }
        } catch (Exception e) {
            log.warn("Can't convert response to error object: " + exchange);
        }
        return null;
    }
}
