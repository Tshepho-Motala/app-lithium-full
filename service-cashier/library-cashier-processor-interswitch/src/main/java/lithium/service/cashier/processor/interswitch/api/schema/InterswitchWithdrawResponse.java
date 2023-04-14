package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterswitchWithdrawResponse {
    private String mac;
    private String transactionDate;
    private String transferCode;
    private String pin;
    private String responseCode;
    private String responseCodeGrouping;
}
