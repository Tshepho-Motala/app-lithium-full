package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Recharge {
    private String biller;
    private String customerId1;
    private String customerId2;
    private String paymentTypeName;
    private String paymentTypeCode;
    private String billerId;
}
