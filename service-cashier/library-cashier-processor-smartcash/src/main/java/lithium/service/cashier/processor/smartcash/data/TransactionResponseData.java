package lithium.service.cashier.processor.smartcash.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponseData {
    private String id;
    private String message;
    @JsonProperty("smart_cash_money_id")
    private String smartcashMoneyId;
    @JsonProperty("reference_id")
    private String referenceId;
    private String status;
    private String timestamp;
}
