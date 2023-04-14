package lithium.service.cashier.mock.paynl.controllers;

import lithium.service.cashier.mock.paynl.services.PayoutService;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GatewayController {
    
    @Autowired
    private PayoutService payoutService;

    @PostMapping(value = "/transactions/payout", produces = "application/json")
    public ResponseEntity<String> createPayout(@RequestHeader("Authorization") String authorization,
                                                       @RequestBody PayoutRequest payoutRequest) {
        return payoutService.createPayout(authorization, payoutRequest);
    }

    @GetMapping(value = "/transactions/verify/{transactionId}", produces = "application/json")
    public ResponseEntity<String> verifyPayout(@RequestHeader("Authorization") String authorization,
                                                             @PathVariable String transactionId) {
        return payoutService.verifyPayout(authorization, transactionId);
    }
    
}
