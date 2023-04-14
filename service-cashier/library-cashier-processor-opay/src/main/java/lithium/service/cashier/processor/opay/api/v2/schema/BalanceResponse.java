package lithium.service.cashier.processor.opay.api.v2.schema;


import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class BalanceResponse extends BaseResponse {
    private String status;
    private String message;
    private BigDecimal balance;
    private String firstName;
}
