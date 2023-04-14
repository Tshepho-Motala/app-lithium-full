package lithium.service.cashier.mock.paynl.services;

import lithium.service.cashier.processor.paynl.data.WebhookData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    private RestTemplate restTemplate;
    
    public void callWebhook(String url, WebhookData webhookData) throws Exception {
        log.info("Sending webhook to url: " + url + " with data: " + webhookData);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/json");
        HttpEntity<WebhookData> entity = new HttpEntity<>(headers);
        
        String uri = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("action", webhookData.getAction())
            .queryParam("order_id", webhookData.getOrderId())
            .queryParam("amount", webhookData.getAmount())
            .queryParam("refund_id", webhookData.getRefundId())
            .toUriString();
                
        ResponseEntity<String> exchange = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class, new HashMap<>());
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get " + exchange.getStatusCodeValue() + " notification response " + exchange.getBody());
            throw new Exception("Invalid notification.");
        }
    }
}
