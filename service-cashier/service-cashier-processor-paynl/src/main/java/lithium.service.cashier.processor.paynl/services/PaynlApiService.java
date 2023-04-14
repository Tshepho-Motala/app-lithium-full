package lithium.service.cashier.processor.paynl.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lithium.service.cashier.processor.paynl.data.response.PayoutResponse;
import lithium.service.cashier.processor.paynl.data.response.PayoutStatusResponse;
import lithium.service.cashier.processor.paynl.exceptions.Error;
import lithium.service.cashier.processor.paynl.exceptions.PaynlException;
import lithium.service.cashier.processor.paynl.exceptions.PaynlGeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class PaynlApiService {

    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    private String paymentUrl;
    private String verifyUrl;
    private String apiToken;

    public static PaynlApiService instance(String paymentUrl, String verifyUrl, String username, String password, RestTemplate restTemplate, ObjectMapper mapper){
        return new PaynlApiService(paymentUrl, verifyUrl, username, password, restTemplate, mapper);
    }    
    
    public PaynlApiService(String paymentUrl, String verifyUrl, String username, String password, RestTemplate restTemplate, ObjectMapper mapper){
        this.paymentUrl = paymentUrl;
        this.verifyUrl = verifyUrl;
        this.apiToken = getAuthToken(username, password);
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public PayoutResponse sendPayoutRequest(PayoutRequest request) throws Exception {
        String requestBody = mapper.writeValueAsString(request);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders());
        log.info("Pay.nl payout request: " + requestBody);
        ResponseEntity<String> exchange = restTemplate.exchange(paymentUrl, HttpMethod.POST, entity, String.class);
        log.info("Pay.nl payout response: " + exchange.getBody());
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Pay.nl responded with http error code: " + exchange.getStatusCodeValue() + ". Reason: " + exchange.getBody());
            throw getPaynlException(exchange.getBody(), exchange.getStatusCode());
        }
        PayoutResponse response = mapper.readValue(exchange.getBody(), PayoutResponse.class);
        return response;
    }

    public PayoutStatusResponse getPayoutStatus(String transactionId) throws Exception {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());
        log.info("Pay.nl payout status request for transactionId : " + transactionId + ": " + entity);
        ResponseEntity<String> exchange = restTemplate.exchange(verifyUrl + "/" + transactionId, HttpMethod.GET, entity, String.class, new HashMap<>());
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Pay.nl responded with http error code: " + exchange.getStatusCodeValue() + ". Reason:" + exchange.getBody());
            throw getPaynlException(exchange.getBody(), exchange.getStatusCode());
        }
        log.info("Pay.nl payout status response: " + exchange.getBody());
        PayoutStatusResponse payoutStatusResponse = mapper.readValue(exchange.getBody(), PayoutStatusResponse.class);
        return payoutStatusResponse;
    }
    
    private MultiValueMap<String, String> getHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + apiToken);
        headers.add("Content-type", "application/json");
        return headers;
    }

    private String getAuthToken(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    private PaynlGeneralException getPaynlException (String body, HttpStatus httpStatus) throws IOException {
        JsonNode jsonNode = mapper.readTree(body);

        List<JsonNode> codes = jsonNode.findValues("code");
        List<JsonNode> messages = jsonNode.findValues("message");
        List<Error> errors = new ArrayList<>();
        if (!codes.isEmpty() && codes.size() == messages.size()) {
            for (int i = 0; i < codes.size(); i++) {
                errors.add(Error.builder()
                        .message(messages.get(i).textValue())
                        .code(codes.get(i).textValue())
                        .build());
            }
        }
        return errors.isEmpty() ? new PaynlGeneralException(httpStatus) : new PaynlException(errors, body);
    }
    
}
