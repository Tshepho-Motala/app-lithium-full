package lithium.service.cashier.mock.flutterwave.services;

import lithium.math.CurrencyAmount;
import lithium.service.cashier.mock.flutterwave.data.entities.FlutterwaveTransaction;
import lithium.service.cashier.mock.flutterwave.data.entities.Scenario;
import lithium.service.cashier.mock.flutterwave.data.repositories.FlutterwaveTransactionRepository;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveVerifyResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveVerifyResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static lithium.service.cashier.mock.flutterwave.ServiceCashierMockFlutterwaveApplication.FORMATTER;

@Slf4j
@Service
public class ValidationService {
    @Autowired
    private FlutterwaveTransactionRepository transactionRepository;

    public ResponseEntity<FlutterWaveVerifyResponse> validatePayment(String transactionId) {
        FlutterwaveTransaction transaction = transactionRepository.findOne(Long.valueOf(transactionId));
        if (transaction == null) {
            log.error("Cant validate flutterwave ussd mock transaction id=" + transactionId);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Scenario scenario = Scenario.getScenarioByAmount(transaction.getAmount());
        FlutterWaveVerifyResponse response = FlutterWaveVerifyResponse.builder()
                .status("success")
                .message("Transaction in progress")
                .data(FlutterWaveVerifyResponseData.builder()
                        .id(Math.toIntExact(transaction.getId()))
                        .status(transaction.getStatus())
                        .txRef(transaction.getTxRef())
                        .flwRef(transaction.getFlwRef())
                        .amount(CurrencyAmount.fromCents(transaction.getAmount()).toAmount())
                        .currency(transaction.getCurrency())
                        .createdAt(FORMATTER.format(transaction.getCreatedAt()))
                        .processorResponse(scenario.getDescription())
                        .paymentType("ussd")
                        .build())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
