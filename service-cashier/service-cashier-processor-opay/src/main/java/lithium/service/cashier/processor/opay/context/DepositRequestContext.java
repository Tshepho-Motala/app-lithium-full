package lithium.service.cashier.processor.opay.context;

import lithium.service.cashier.processor.opay.api.v2.schema.DepositRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DepositRequestContext extends RequestContext {
    private DepositRequest request;
    private Long amountInCents;
    private Long cashierReferenceNumber;
    private String paymentType;
}
