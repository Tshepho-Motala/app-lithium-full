package lithium.service.cashier.mock.smartcash.controllers;

import lithium.service.cashier.mock.smartcash.validators.AcceptedValues;
import lithium.service.cashier.mock.smartcash.services.Simulator;
import lithium.service.cashier.processor.smartcash.data.SmartcashCustomerSearchResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashPayoutRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/standard/v1/disbursements")
public class DisbursementContoller {
        @Autowired
        Simulator simulator;

        @GetMapping("/{transactionReference}")
        public SmartcashPaymentResponse getPayout(@PathVariable String transactionReference,
                                                  @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                                  @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
            return simulator.getTransactionStatus(transactionReference);
        }

        @PostMapping
        public SmartcashPaymentResponse payout(@RequestBody SmartcashPayoutRequest payoutRequest,
                                               @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                               @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
            return simulator.simulatePayment(payoutRequest.getTransaction().getId(), payoutRequest.getPayee().getMsisdn(), payoutRequest.getTransaction().getAmount(), countryCode, currencyCode, true);
        }

        @GetMapping("/user/instruments")
        public SmartcashCustomerSearchResponse getCustomer(@RequestParam String msisdn,
                                                           @RequestHeader("x-country-code") @AcceptedValues(values = {"NG"}) String countryCode,
                                                           @RequestHeader("x-currency-code") @AcceptedValues(values = {"NGN"}) String currencyCode) {
            return simulator.getCustomer(msisdn);
        }
    }
