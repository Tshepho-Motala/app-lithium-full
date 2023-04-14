package lithium.service.cashier.processor.mvend.context;

import lithium.service.cashier.processor.mvend.api.schema.balance.BalanceResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BalanceRequestContext extends RequestContext {
    String currencyCode;
    BalanceResponse response;
}
