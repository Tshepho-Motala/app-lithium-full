package lithium.service.cashier.processor.flutterwave.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataRequest {
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("account_bank")
    private String accountBank;
}
