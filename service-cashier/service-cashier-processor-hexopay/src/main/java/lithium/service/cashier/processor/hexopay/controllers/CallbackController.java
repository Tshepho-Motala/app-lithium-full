package lithium.service.cashier.processor.hexopay.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.WebhookType;
import lithium.service.cashier.processor.hexopay.services.HexopayGatawayApiService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
public class CallbackController {
    @Autowired
    private HexopayGatawayApiService service;

    @RequestMapping("/public/webhook")
    public ResponseEntity webhook(@RequestBody String data,
                                  @RequestHeader("content-signature") String signature) {
        log.info("Hexopay webhook is called: " + data);

        try {
            switch (getWebhoookType(data)) {
                case payment:
                    service.handlePaymentWebhook(data, signature);
                    break;
               case expired:
                    //service.handleTokenExpireWebhook(data);
                    break;
                default:
                    log.info("Unknown Hexopay webhook request: " + data);
            }
        } catch (Exception e) {
            log.error("Failed to process Hexopay webhook notification: " + data + " Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();

    }

    @RequestMapping("/public/redirect")
    public ModelAndView redirect(@RequestParam("trx_id") String transactionId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String token,
                                 @RequestParam(required = false) String uid) throws Exception {
        log.info("Resieved redirect from Hexopay. TransactionId: " + transactionId + ", uuid: "+ uid + ", status: "+ status + ", token: " + token);
        return new ModelAndView(service.handleHexopayRedirect(transactionId));
    }

    @RequestMapping("/public/result")
    public String result(@RequestParam(name="trx_id", required = false) String transactionId,
                                 @RequestParam String status)
    {
        return status.toUpperCase();
    }

    private WebhookType getWebhoookType(String data) {
        try {
            JSONObject json = new JSONObject(data);
            if (json.has("transaction")) {
                return WebhookType.payment;
            } else if (json.has("token")) {
                return WebhookType.expired;
            } else {
                return WebhookType.unknown;
            }
        } catch (JSONException e) {
            log.error("Unkown hexopay notification type: " + data);
            return WebhookType.unknown;
        }
    }

}
