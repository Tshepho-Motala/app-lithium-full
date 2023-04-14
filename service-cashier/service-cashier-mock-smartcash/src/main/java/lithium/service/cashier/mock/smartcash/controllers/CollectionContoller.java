package lithium.service.cashier.mock.smartcash.controllers;

import lithium.service.cashier.mock.smartcash.validators.AcceptedValues;
import lithium.service.cashier.mock.smartcash.services.Simulator;
import lithium.service.cashier.processor.smartcash.data.SmartcashCustomerSearchResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentRequest;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/merchant/v1/payments")
public class CollectionContoller {
    @Autowired
    Simulator simulator;

    @GetMapping("/{transactionReference}")
    public SmartcashPaymentResponse getPayment(@PathVariable String transactionReference,
                                                @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                                @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
        return simulator.getTransactionStatus(transactionReference);
    }

    @PostMapping
    public SmartcashPaymentResponse payment(@Valid @RequestBody SmartcashPaymentRequest paymentRequest,
                                            @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                            @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
        return simulator.simulatePayment(paymentRequest.getTransaction().getId(), paymentRequest.getPayer().getMsisdn(), paymentRequest.getTransaction().getAmount(), countryCode, currencyCode, false);
    }

    @GetMapping("/user/instruments")
    public SmartcashCustomerSearchResponse getCustomer(@RequestParam String msisdn,
                                                       @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                                       @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
        return simulator.getCustomer(msisdn);
    }
}
