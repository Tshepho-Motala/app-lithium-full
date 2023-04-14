package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomField {
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("variable_name")
    private String variableName;
    private String value;
}
