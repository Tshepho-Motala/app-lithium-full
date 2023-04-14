package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class UssdDepositChargeRequestMetadata {
    @JsonProperty("custom_fields")
    private List<CustomField> customFields;
}
