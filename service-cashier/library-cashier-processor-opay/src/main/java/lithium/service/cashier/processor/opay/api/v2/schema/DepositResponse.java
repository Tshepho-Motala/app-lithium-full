package lithium.service.cashier.processor.opay.api.v2.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DepositResponse extends BaseResponse {
    @JsonProperty("payment_ref")
    private String paymentRef;
}
