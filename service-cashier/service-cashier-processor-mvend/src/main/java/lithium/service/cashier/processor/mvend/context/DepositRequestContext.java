package lithium.service.cashier.processor.mvend.context;

import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositRequest;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DepositRequestContext extends RequestContext {
    private DepositRequest request;
    private DepositResponse response;
    private Long amountInCents;
    private Long cashierReferenceNumber;
}
