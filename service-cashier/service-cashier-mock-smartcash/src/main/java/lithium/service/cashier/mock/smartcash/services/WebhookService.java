package lithium.service.cashier.mock.smartcash.services;

import lithium.service.cashier.mock.smartcash.data.exceptions.InvalidNotificationResponseException;
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
    //Smartcash does not do retry
   // @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 3), include = {InvalidNotificationResponseException.class})
    public void callWebhook(String url, String body) {
        HttpEntity<?> entity = new HttpEntity<>(body);

        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, new HashMap<>());

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Got " + exchange.getStatusCodeValue() + " for smartcash notification.");
            throw new InvalidNotificationResponseException();
        }
    }

}
