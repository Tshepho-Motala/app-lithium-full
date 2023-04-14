package lithium.service.cashier.processor.mvend.api.schema.balance;

import lithium.service.cashier.processor.mvend.api.schema.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse extends Response {

    private String groupRef;
    private String msisdn;
    private Double balance;
    private String currency;
    private String firstName;

}
