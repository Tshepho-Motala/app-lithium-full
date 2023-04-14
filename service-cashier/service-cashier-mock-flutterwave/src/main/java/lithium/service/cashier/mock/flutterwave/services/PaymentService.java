package lithium.service.cashier.mock.flutterwave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.flutterwave.FlutterwaveConfiguration;
import lithium.service.cashier.mock.flutterwave.data.entities.FlutterwaveTransaction;
import lithium.service.cashier.mock.flutterwave.data.entities.Scenario;
import lithium.service.cashier.mock.flutterwave.data.repositories.FlutterwaveTransactionRepository;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesData;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesMeta;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesResponse;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesResponseAuthorization;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static lithium.service.cashier.mock.flutterwave.ServiceCashierMockFlutterwaveApplication.FORMATTER;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LithiumConfigurationProperties lithiumProperties;

    @Autowired
    private FlutterwaveConfiguration properties;

    @Autowired
    private FlutterwaveTransactionRepository transactionRepository;

    private static final long TTL_MOCK = 86400000l;

    public ResponseEntity<FlutterWaveChargesResponse> createPayment(FlutterWaveChargesRequest flutterWaveChargesRequest) {
        long amount = getAmountInCents(flutterWaveChargesRequest.getAmount());
        Scenario scenario = Scenario.getScenarioByAmount(amount);
        if ("cancelled".equalsIgnoreCase(scenario.getState()))
            return new ResponseEntity<>(buildErrorResponse(scenario), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(buildPendingResponse(flutterWaveChargesRequest), HttpStatus.OK);
    }

    public void simulateWebhook(FlutterwaveTransaction flutterwaveTransaction) {
        try {
            FlutterWaveWebhookRequest request = FlutterWaveWebhookRequest.builder().
                    data(mapper.valueToTree(FlutterWaveChargesData.builder()
                            .id(flutterwaveTransaction.getId())
                            .status("successful")
                            .tx_ref(flutterwaveTransaction.getTxRef())
                            .build()))
                    .build();
            final String notificationData = mapper.writeValueAsString(request);

            HttpEntity<?> entity = new HttpEntity<>(notificationData, new LinkedMultiValueMap<>());

            ResponseEntity<String> verifyResponseEntity = restTemplate.exchange(lithiumProperties.getGatewayPublicUrl() + "/" + properties.getWebhookUrl(),
                    HttpMethod.POST, entity,
                    String.class, new HashMap<>());

            if (verifyResponseEntity.getStatusCode().is2xxSuccessful()) {
                flutterwaveTransaction.setFinalized(true);
                transactionRepository.save(flutterwaveTransaction);
            } else {
                log.error("Transaction id=" + flutterwaveTransaction.getTxRef() + ", unsuccessful verify response: status= "+verifyResponseEntity.getStatusCode() + " ; Message=" +verifyResponseEntity.getBody());
            }
        } catch (Exception ex) {
            log.error("Cant process webhook for transaction id = " + flutterwaveTransaction.getTxRef());
        }
    }

    private FlutterWaveChargesResponse buildErrorResponse(Scenario scenario) {
        FlutterWaveChargesResponse response = new FlutterWaveChargesResponse();
        response.setStatus("reject");
        response.setMessage("Charge rejected");
        response.setData(FlutterWaveChargesData.builder()
                .status(scenario.getState())
                .processor_response(scenario.getDescription())
                .build()
        );
        return response;
    }

    private FlutterWaveChargesResponse buildPendingResponse(FlutterWaveChargesRequest flutterWaveChargesRequest) {
        FlutterWaveChargesResponse response = new FlutterWaveChargesResponse();
        response.setStatus("success");
        response.setMessage("Charge initiated");
        response.setData(buildData(flutterWaveChargesRequest, "pending"));
        response.setMeta(buildMeta());
        return response;
    }

    private FlutterWaveChargesMeta buildMeta() {
        FlutterWaveChargesResponseAuthorization auth = new FlutterWaveChargesResponseAuthorization();
        auth.setMode("ussd");
        auth.setNote("*889*767*9039#");
        FlutterWaveChargesMeta meta = new FlutterWaveChargesMeta();
        meta.setAuthorization(auth);
        return meta;
    }

    private FlutterWaveChargesData buildData(FlutterWaveChargesRequest flutterWaveChargesRequest, String status) {
        Date date = new Date(System.currentTimeMillis());
        FlutterwaveTransaction transaction = transactionRepository.save(
                FlutterwaveTransaction.builder()
                        .txRef(flutterWaveChargesRequest.getTx_ref())
                        .flwRef("FLW_MOCK_" + flutterWaveChargesRequest.getTx_ref())
                        .amount(getAmountInCents(flutterWaveChargesRequest.getAmount()))
                        .currency(flutterWaveChargesRequest.getCurrency())
                        .createdAt(date)
                        .status(status)
                        .finalized(false)
                        .build()
        );
        return FlutterWaveChargesData.builder()
                .tx_ref(transaction.getTxRef())
                .flw_ref(transaction.getFlwRef())
                .device_fingerprint("N/A")
                .amount(new BigDecimal(flutterWaveChargesRequest.getAmount()))
                .charged_amount(new BigDecimal(flutterWaveChargesRequest.getAmount()))
                .currency(flutterWaveChargesRequest.getCurrency())
                .app_fee(BigDecimal.ZERO)
                .merchant_fee(BigDecimal.ZERO)
                .processor_response("Transaction in progress")
                .auth_model("USSD")
                .ip("N/A")
                .payment_type("ussd")
                .fraud_status("ok")
                .charge_type("normal")
                .status(transaction.getStatus())
                .narration("MerchantName")
                .account_id("17321")
                .id(transaction.getId())
                .created_at(FORMATTER.format(date))
                .build();
    }

    private long getAmountInCents(String amount) {
        BigDecimal bdAmount = new BigDecimal(amount);
        return bdAmount.movePointRight(2).longValue();
    }


    public List<FlutterwaveTransaction> getFinalizedTransactions() {
        return transactionRepository.findAllByStatusNotAndFinalizedFalse("pending");
    }

    public List<FlutterwaveTransaction> getPendingTransactions() {
        return transactionRepository.findAllByStatusAndFinalizedFalse("pending");
    }

    public void updateTransactionStatus(FlutterwaveTransaction flutterwaveTransaction) {
        if (isExpiredTransaction(flutterwaveTransaction)) {
            flutterwaveTransaction.setStatus("expired");
            flutterwaveTransaction.setFinalized(true);
        } else {
            Scenario scenario = Scenario.getScenarioByAmount(flutterwaveTransaction.getAmount());
            flutterwaveTransaction.setStatus(scenario.getState());
        }
        transactionRepository.save(flutterwaveTransaction);
    }

    private boolean isExpiredTransaction(FlutterwaveTransaction flutterwaveTransaction) {
        long createdAtMillis = flutterwaveTransaction.getCreatedAt().getTime();
        long currentMillis = System.currentTimeMillis();
        return currentMillis - createdAtMillis > TTL_MOCK;
    }
}
