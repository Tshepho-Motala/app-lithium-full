package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveVerifyResponseData {
    private Integer id;
    @JsonProperty("tx_ref")
    private String txRef;
    @JsonProperty("flw_ref")
    private String flwRef;
    @JsonProperty("device_fingerprint")
    private String deviceFingerprint;
    private BigDecimal amount;
    private String currency;
    @JsonProperty("charged_amount")
    private String chargedAmount;
    @JsonProperty("app_fee")
    private String appFee;
    @JsonProperty("merchant_fee")
    private String merchantFee;
    @JsonProperty("processor_response")
    private String processorResponse;
    @JsonProperty("auth_model")
    private String authModel;
    private String ip;
    private String narration;
    private String status;
    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("account_id")
    private String accountId;
    private FlutterWaveCard card;
    private FlutterWaveMeta meta;
    @JsonProperty("amount_settled")
    private String amountSettled;
    private FlutterWaveChargesCustomer customer;
}
