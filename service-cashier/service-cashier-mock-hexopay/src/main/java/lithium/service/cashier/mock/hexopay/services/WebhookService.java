package lithium.service.cashier.mock.hexopay.services;

import lithium.service.cashier.mock.hexopay.data.exceptions.InvalidNotificationResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
public class WebhookService
{
    @Autowired
    RestTemplate restTemplate;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 3), include = {InvalidNotificationResponseException.class})
    public void callWebhook(String body, String trackingId, String url, String signature) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        if (signature != null && !signature.isEmpty()) {
            headers.add("content-signature", signature);
        }

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, new HashMap<>());

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get " + exchange.getStatusCodeValue() + " notification response trakingId=" + trackingId + " retry" + exchange.getBody());
            throw new InvalidNotificationResponseException();
        }
    }

}
