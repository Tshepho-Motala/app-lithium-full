package lithium.service.kyc.provider.onfido.controller;

import com.onfido.api.ApiJson;
import com.onfido.exceptions.OnfidoException;
import com.onfido.webhooks.WebhookPayload;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.onfido.exceptions.Status412NotFoundApplicantException;
import lithium.service.kyc.provider.onfido.exceptions.Status413RetrieveDocumentException;
import lithium.service.kyc.provider.onfido.exceptions.Status414NotFoundOnfidoCheckException;
import lithium.service.kyc.provider.onfido.service.OnfidoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {
    private final ApiJson<WebhookPayload> parser = new ApiJson<>(WebhookPayload.class);
    private final OnfidoService onfidoService;

    @PostMapping("/{domainName}")
    public ResponseEntity webhook(@PathVariable String domainName, @RequestBody String data) {
        try {
            log.info("Got webhook (" + domainName + "): " + data);
            WebhookPayload webhookPayload = parser.parse(data);
            if ("check.completed".equals(webhookPayload.getPayload().getAction())) {
                onfidoService.handleCheckComplete(domainName, webhookPayload.getPayload().getObject());
            }
        } catch (Status413RetrieveDocumentException | Status512ProviderNotConfiguredException |
                 Status414NotFoundOnfidoCheckException | Status412NotFoundApplicantException e) {
            log.error("Got error during proceed webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Got unexpected error during proceed webhook: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();

    }


}
