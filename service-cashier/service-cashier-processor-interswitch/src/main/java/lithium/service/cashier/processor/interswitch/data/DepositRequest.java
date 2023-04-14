package lithium.service.cashier.processor.interswitch.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class DepositRequest {
    private String userId;
    private String dateTime;
    private String amount;
    private String networkRef;
    private String externalRef;
    private String groupRef;
}