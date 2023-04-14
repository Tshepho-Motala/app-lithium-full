package lithium.service.cashier.mock.inpay.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.inpay.InpayConfiguration;
import lithium.service.cashier.mock.inpay.data.exceptions.Status400InvalidNotificationException;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookData;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookDataV2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
@Service
public class NotificationService {

    private final InpayConfiguration properties;
    private final RestTemplate restTemplate;
    private final LithiumConfigurationProperties lithiumProperties;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 3))
    public void callWebhook(InpayWebhookData webhookData) throws Status400InvalidNotificationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<InpayWebhookData> entity = new HttpEntity<>(webhookData, headers);
        ResponseEntity<InpayWebhookData> exchange = restTemplate.exchange(lithiumProperties.getGatewayPublicUrl() + "/" + properties.getWebhookUrl(), HttpMethod.POST, entity, InpayWebhookData.class);
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Get " + exchange.getStatusCodeValue() + " notification response " + exchange.getBody());
            throw new Status400InvalidNotificationException("Invalid notification.");
        }
    }

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 3))
	public void callWebhookV2(String webhookData) throws Status400InvalidNotificationException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> entity = new HttpEntity<>(webhookData, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(lithiumProperties.getGatewayPublicUrl() + "/" + properties.getWebhookUrlV2(), HttpMethod.POST, entity, String.class);
		if (!exchange.getStatusCode().is2xxSuccessful()) {
			log.error("Get " + exchange.getStatusCodeValue() + " notification response " + exchange.getBody());
			throw new Status400InvalidNotificationException("Invalid notification.");
		}
	}
}
