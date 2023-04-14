package lithium.service.cashier.mock.inpay.controllers;

import lithium.service.cashier.mock.inpay.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    PaymentService paymentService;

    @PostMapping(value = "/transactions/withdraw")
    public String createPayment(@RequestHeader("authorization") String authorization,
                                @RequestHeader("x-auth-uuid") String xAuthUuid,
                                @RequestHeader("x-request-id") String xRequestId,
                                @RequestBody String body) throws Exception {
        return paymentService.createPayment(authorization, xAuthUuid, xRequestId, body);
    }

    @GetMapping(value = "/transactions/verify/{inpayUniqueReference}")
    public String getPaymentStatus(@RequestHeader("authorization") String authorization,
                                   @RequestHeader("x-auth-uuid") String xAuthUuid,
                                   @PathVariable String inpayUniqueReference) throws Exception {
        return paymentService.getPaymentStatus(authorization, xAuthUuid, inpayUniqueReference);
    }

}