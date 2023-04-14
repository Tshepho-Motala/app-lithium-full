package lithium.service.cashier.processor.opay.api.v2.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class DepositRequest {
    @JsonProperty("date_time")
    private String dateTime;
    private String amount;
    private String narrative;
    @JsonProperty("network_ref")
    private String networkRef;
    @JsonProperty("external_ref")
    private String externalRef;
    private String msisdn;
    private String signature;
    @JsonProperty("group_ref")
    private String groupRef;
    @JsonProperty("payment_channel")
    private String paymentChannel;
}
