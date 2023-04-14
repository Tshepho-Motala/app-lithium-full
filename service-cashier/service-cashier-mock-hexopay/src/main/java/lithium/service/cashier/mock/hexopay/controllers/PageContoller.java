package lithium.service.cashier.mock.hexopay.controllers;

import lithium.service.cashier.mock.hexopay.services.Simulator;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenRequest;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ctp/api/checkouts")
public class PageContoller {
    @Autowired
    Simulator simulator;

    @PostMapping
    public PaymentTokenResponse paymentToken(@RequestBody PaymentTokenRequest tokenRequest) {
        return simulator.simulateCreateTransactionToken(tokenRequest);
    }

}
