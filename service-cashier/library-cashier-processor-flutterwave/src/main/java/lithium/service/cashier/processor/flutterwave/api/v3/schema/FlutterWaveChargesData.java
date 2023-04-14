package lithium.service.cashier.processor.flutterwave.api.v3.schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveChargesData {
    Long id;
    String tx_ref;
    String flw_ref;
    String device_fingerprint;
    BigDecimal amount;
    String currency;
    BigDecimal charged_amount;
    BigDecimal app_fee;
    BigDecimal merchant_fee;
    String processor_response;
    String auth_model;
    String ip;
    String narration;
    String status;
    String payment_type;
    String created_at;
    String account_id;
    String fraud_status;
    String charge_type;
    String payment_code;
    FlutterWaveChargesCustomer customer;
    FlutterWaveChargesCard card;
}
