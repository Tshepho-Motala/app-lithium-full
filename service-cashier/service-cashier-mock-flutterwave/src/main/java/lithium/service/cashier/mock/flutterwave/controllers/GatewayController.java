package lithium.service.cashier.mock.flutterwave.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.mock.flutterwave.services.PaymentService;
import lithium.service.cashier.mock.flutterwave.services.ValidationService;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveVerifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GatewayController {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ValidationService validationService;

    @PostMapping(value = "/transactions/deposit")
    public ResponseEntity<FlutterWaveChargesResponse> createPayment(
            @RequestBody String body) throws Exception {
        FlutterWaveChargesRequest flutterWaveChargesRequest = mapper.readValue(body, FlutterWaveChargesRequest.class);
        return paymentService.createPayment(flutterWaveChargesRequest);
    }

    @GetMapping(value = "/transactions/{id}/verify")
    public ResponseEntity<FlutterWaveVerifyResponse> verifyPayment(
            @PathVariable("id") String transactionId) throws Exception {
        return validationService.validatePayment(transactionId);
    }
}
