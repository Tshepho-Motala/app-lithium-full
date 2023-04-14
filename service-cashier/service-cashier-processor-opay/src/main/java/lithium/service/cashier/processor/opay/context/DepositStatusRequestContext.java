package lithium.service.cashier.processor.opay.context;

import lithium.service.cashier.client.objects.DepositStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DepositStatusRequestContext extends RequestContext {
    String networkRef;
    DepositStatus depositStatus;
}
