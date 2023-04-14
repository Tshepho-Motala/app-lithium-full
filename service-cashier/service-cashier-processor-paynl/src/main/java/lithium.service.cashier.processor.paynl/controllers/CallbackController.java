package lithium.service.cashier.processor.paynl.controllers;

import lithium.service.cashier.processor.paynl.services.PaynlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class CallbackController {
    
    @Autowired
    PaynlService paynlService;

    @RequestMapping("/public/webhook/{transactionId}")
    public ResponseEntity webhook(@PathVariable String transactionId, @RequestParam Map<String, String> requestParams) {
        log.info("Pay.nl webhook is called. Transaction id: " + transactionId + ". RequestParams: " + requestParams);
        try {
            paynlService.processPaynlNotification(transactionId, requestParams);
            return ResponseEntity.ok("TRUE");
        } catch (Exception e) {
            log.error("Failed to process Pay.nl webhook notification for transaction id : " + transactionId + " Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
