package lithium.service.cashier.processor.opay.context;

import lithium.service.cashier.processor.opay.api.v2.schema.DepositResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BalanceRequestContext extends RequestContext {
    String currencyCode;
    DepositResponse response;
}
