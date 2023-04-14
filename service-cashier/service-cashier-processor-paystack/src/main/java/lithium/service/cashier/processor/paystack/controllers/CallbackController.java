package lithium.service.cashier.processor.paystack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.paystack.api.schema.Metadata;
import lithium.service.cashier.processor.paystack.api.schema.PaystackWebhookRequest;
import lithium.service.cashier.processor.paystack.api.schema.WebhookDepositRequestData;
import lithium.service.cashier.processor.paystack.api.schema.WebhookWithdrawRequestData;
import lithium.service.cashier.processor.paystack.services.DepositService;
import lithium.service.cashier.processor.paystack.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;

@RestController
@Slf4j
public class CallbackController {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private DepositService depositService;
    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/public/webhook")
    public ResponseEntity webhook(@RequestBody String data) {
        try {
            log.info("Webhook is called: " + data);

            PaystackWebhookRequest request = mapper.readValue(data, PaystackWebhookRequest.class);

            log.info("Converted to Object" + request.toString());

            String eventType = request.getEvent();

            if (eventType != null && eventType.startsWith("transfer")) {
                withdrawService.proceedWithdrawWebhook(data, mapper.readValue(request.getData().toString(), WebhookWithdrawRequestData.class));
            } else {
                WebhookDepositRequestData depositData = mapper.readValue(request.getData().toString(), WebhookDepositRequestData.class);
                boolean isTriggeredViaUssdRequest = nonNull(depositData.getMetadata().getCustomFields())
                        && depositData.getMetadata().getCustomFields().stream().anyMatch(customField ->
                        "trigger_method".equals(customField.getVariableName()) && "ussd".equals(customField.getValue()));
                if (isTriggeredViaUssdRequest) {
                    depositService.processUSSDWebhook(depositData);
                } else {
                    depositService.processWidgetWebhook(depositData);
                }
            }
        } catch (Exception e) {
            log.error("Got error during proceed webhook: "  + e.getMessage() + " Stack: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();

    }
}
