package lithium.service.cashier.processor.opay.api.v2.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpayDepositStatusRequest {
    @JsonProperty("network_ref")
    private String networkRef;
    private String date;
    private String signature;
    @JsonProperty("group_ref")
    private String groupRef;
}
