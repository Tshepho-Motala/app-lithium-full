package lithium.service.cashier.processor.mvend.api.schema.deposit;

import lithium.service.cashier.processor.mvend.api.schema.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DepositResponse extends Response {

    private String reference;

}
