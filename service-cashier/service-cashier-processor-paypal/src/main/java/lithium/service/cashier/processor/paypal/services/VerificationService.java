package lithium.service.cashier.processor.paypal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureRequest;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureResponse;
import lithium.service.cashier.processor.paypal.exceptions.PayPalInvalidSignatureException;
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
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class VerificationService {
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CashierInternalClientService  cashierService;

    public void verifyWebhookSignature(DoProcessorRequest request, VerifyWebhookSignatureRequest verifyWebhookRequest) throws Exception {
        boolean isBA = nonNull(request.stageInputData(1).get("processorAccountId")) && request.getTransactionType().equals(TransactionType.DEPOSIT);
        verifyWebhookSignature(verifyWebhookRequest, request.getProperties(), isBA);
    }

    public void verifyWebhookSignature(VerifyWebhookSignatureRequest verifyWebhookRequest, String domainName, boolean isDeposit, boolean isBA) throws Exception {
        DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                domainName, isDeposit, "paypal", "paypal");

        verifyWebhookSignature(verifyWebhookRequest, dmp.getProperties(), isBA);
    }

    private void callVerifyWebhookSignature(VerifyWebhookSignatureRequest verifyWebhookRequest, String userName, String password, String apiUrl) throws Exception {
        String auth =  userName + ":" + password;
        String token = new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + token);
        headers.add("Content-type", "application/json");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<VerifyWebhookSignatureRequest> entity = new HttpEntity<>(verifyWebhookRequest, headers);

        ResponseEntity<Object> exchange = restTemplate.exchange(apiUrl + "/v1/notifications/verify-webhook-signature", HttpMethod.POST, entity, Object.class);
        log.debug("PayPal verify webhook signature response: " + exchange.getBody());

        if (!exchange.getStatusCode().is2xxSuccessful() ) {
            throw new PayPalInvalidSignatureException("Failed to verify PayPal webhook signature (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
        }
        VerifyWebhookSignatureResponse tokenResponse = mapper.convertValue(exchange.getBody(), VerifyWebhookSignatureResponse.class);
        if (!tokenResponse.getVerificationStatus().equalsIgnoreCase("success")) {
            throw new PayPalInvalidSignatureException("Invalid webhook signature. Paypal webhook verification status is: " + tokenResponse.getVerificationStatus());
        }
    }

    private void verifyWebhookSignature(VerifyWebhookSignatureRequest verifyWebhookRequest, Map<String, String> properties,  boolean isBA) throws Exception {
        String userName = isBA ? properties.get("username_ba") : properties.get("username");
        String password = isBA ? properties.get("password_ba") : properties.get("password");
        String webhookId = isBA ? properties.get("webhook_ba_id") : properties.get("webhook_id");
        if (!Boolean.parseBoolean(properties.get("enable_webhook_verification"))) {
            log.warn("Webhook signature verification is disabled. Webhook: " + verifyWebhookRequest.getWebhook());
            return;
        }

        verifyWebhookRequest.setWebhookId(webhookId);
        callVerifyWebhookSignature(verifyWebhookRequest, userName, password, properties.get("api_url"));
    }
}
