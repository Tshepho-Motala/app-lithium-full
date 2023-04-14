package lithium.service.cashier.processor.paypal.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.paypal.api.webhook.WebhookRequest;
import lithium.service.cashier.processor.paypal.data.OrderConfirmResponse;
import lithium.service.cashier.processor.paypal.data.VerifyWebhookSignatureRequest;
import lithium.service.cashier.processor.paypal.services.BillingAgreementService;
import lithium.service.cashier.processor.paypal.services.DepositService;
import lithium.service.cashier.processor.paypal.services.VerificationService;
import lithium.service.cashier.processor.paypal.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WebhookController {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private DepositService depositService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private BillingAgreementService billingAgreementService;

    @PostMapping("/public/webhook/{domainName}")
    public ResponseEntity webhook(@PathVariable String domainName,
                                  @RequestHeader("PAYPAL-AUTH-ALGO") String authAlgo, @RequestHeader("PAYPAL-CERT-URL") String certUrl,
                                  @RequestHeader("PAYPAL-TRANSMISSION-ID") String transId, @RequestHeader("PAYPAL-TRANSMISSION-SIG") String tranSig,
                                  @RequestHeader("PAYPAL-TRANSMISSION-TIME") String tranTime, @RequestBody String data) {
        try {
            log.info("Webhook is called: " + data);
            WebhookRequest webhook = mapper.readValue(data, WebhookRequest.class);

            VerifyWebhookSignatureRequest signedWebhook = VerifyWebhookSignatureRequest.builder()
                    .authAlgo(authAlgo)
                    .certUrl(certUrl)
                    .tranId(transId)
                    .tranSig(tranSig)
                    .tranTime(tranTime)
                    .webhook(data)
                    .build();

            if ("CHECKOUT.ORDER.APPROVED".equals(webhook.getEventType())
                    && "checkout-order".equalsIgnoreCase(webhook.getResourceType())) {
                depositService.handleOrderApprovedWebhook(webhook, signedWebhook);
            } else if ("PAYMENT.CAPTURE.COMPLETED".equals(webhook.getEventType())
                    && "capture".equalsIgnoreCase(webhook.getResourceType())) {
                depositService.handleCaptureWebhook(webhook, signedWebhook);
            } else if ("PAYMENT.PAYOUTSBATCH.SUCCESS".equals(webhook.getEventType())
                    && "payouts".equalsIgnoreCase(webhook.getResourceType())) {
                withdrawService.handlePayoutsWebhook(webhook, signedWebhook);
            } else if ("BILLING_AGREEMENTS.AGREEMENT.CANCELLED".equalsIgnoreCase(webhook.getEventType())
                    && "Agreement".equalsIgnoreCase(webhook.getResourceType())) {
                billingAgreementService.handleCancelBillingAgreementWebhook(webhook, signedWebhook, domainName);
            }
        } catch (Exception e) {
            log.error("Got error during proceed webhook ("+data+"): " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/public/callback/order/{orderId}/{action}")
    public OrderConfirmResponse orderCallback(@PathVariable String action, @PathVariable String orderId,
                                              @RequestParam(value = "payerID", required = false) String payerID) {

        log.info("Got handle Paypal Order (" + action + ", " + orderId + "): payerID = " + payerID);

        try {
            DoProcessorResponse response = depositService.handleOrderCallback(orderId, action, payerID);
            if (DoProcessorResponseStatus.SUCCESS.equals(response.getStatus())) {
                return new OrderConfirmResponse("SUCCESS", null);
            } else if (DoProcessorResponseStatus.DECLINED.equals(response.getStatus())) {
                return new OrderConfirmResponse("FAILED", response.getMessage());
            } else if (DoProcessorResponseStatus.PLAYER_CANCEL.equals(response.getStatus())) {
                return new OrderConfirmResponse("CANCELED", response.getMessage());
            } else if (DoProcessorResponseStatus.NOOP.equals(response.getStatus())) {
                return new OrderConfirmResponse("PENDING", null);
            }
            return new OrderConfirmResponse("ERROR", response.getDeclineReason());
        } catch (Exception e) {
            log.error("Failed handle Paypal Order (" + orderId + ") due " + e.getMessage(), e);
            return new OrderConfirmResponse("ERROR", "0:Transaction declined. Please verify your details and try again or use another method.");
        }
    }

}
